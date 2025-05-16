package com.learnbridge.learn_bridge_back_end.controller;


import com.learnbridge.learn_bridge_back_end.dao.InstructorDAO;
import com.learnbridge.learn_bridge_back_end.dto.ReportDTO;
import com.learnbridge.learn_bridge_back_end.dto.UserDTO;
import com.learnbridge.learn_bridge_back_end.entity.*;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.NotificationService;
import com.learnbridge.learn_bridge_back_end.service.ReportService;
import com.learnbridge.learn_bridge_back_end.service.StripeService;
import com.learnbridge.learn_bridge_back_end.service.UserService;
import com.stripe.exception.StripeException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("api/reports")
@CrossOrigin(origins = "http://localhost:4200")
public class ReportController {

    @Autowired
    private ReportService reportService;


    @Autowired
    private StripeService stripeService;

    @Autowired
    private UserService userService;

    @Autowired
    private InstructorDAO instructorDAO;

    @Autowired
    private NotificationService notificationService;


    @GetMapping("/pending-reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReportDTO>> getPendingReports() {
        List<ReportDTO> pendingReports = reportService.getAllPendingReports();
        // always send 200 OK with the array (empty or not)
        return new ResponseEntity<>(pendingReports, HttpStatus.OK);
    }


    @PutMapping("/resolve/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportDTO> updateReportStatus(@PathVariable Long reportId) {

        ReportDTO resolvedReport = reportService.resolveReport(reportId);
        if (resolvedReport == null) {
            return ResponseEntity.notFound().build();
        } else {
            return new ResponseEntity<>(resolvedReport, HttpStatus.OK);
        }
    }

    /**
     * Delete the report (no money movement), returning the deleted DTO.
     */
    @DeleteMapping("/delete/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportDTO> deleteReport(@PathVariable Long reportId) {
        ReportDTO deleted = reportService.deleteReport(reportId);
        return ResponseEntity.ok(deleted);
    }


    /**
     * Block the reported user and mark this report RESOLVED.
     */
    @PutMapping("/block/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> blockUser(@PathVariable Long reportId) {
        Report r = reportService.findOrThrow(reportId);
        r.setReportStatus(ReportStatus.RESOLVED);
        reportService.save(r);
        UserDTO blocked = userService.blockUser(r.getReportedUser().getId());
        return ResponseEntity.ok(blocked);
    }


    @PostMapping("/create-report/{chatId}")
    public ResponseEntity<ReportDTO> createReport(
            @PathVariable Long chatId,
            @RequestBody ReportDTO reportDTO,
            @AuthenticationPrincipal SecurityUser loggedUser
    ) {
        Long reporterId = loggedUser.getUser().getId();
        String description = reportDTO.getDescription();
        ReportDTO createdReport = reportService.createReportByChat(
                reporterId, chatId, description, reportDTO.getReportType()
        );
        return new ResponseEntity<>(createdReport, HttpStatus.CREATED);
    }


    /**
     * Refund the held charge back to the *learner* (using the PaymentInfo on the Session),
     * then mark the report RESOLVED.
     */
    @PostMapping("/refund/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportDTO> refundLearner(@PathVariable Long reportId) {
        try {
            // 1) do the mock-or-live refund and get back the entity
            Report report = reportService.refundOrMock(reportId);

            // 2) extract exactly what we need from the entity:
            User learner    = report.getReporter();                   // who filed the report
            BigDecimal amt  = report.getSession().getTransaction().getAmount();
            String  refId   = report.getSession().getTransaction().getStripeRefundId();

            // 3) send the notification
            notificationService.sendRefundNotification(learner, amt, refId);

            // 4) return a DTO for the front-end
            return ResponseEntity.ok(reportService.toDTO(report));
        }
        catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
        catch (StripeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }


    /**
     * Transfer funds to the instructor’s Stripe account, mark report RESOLVED.
     */
    @PostMapping("/transfer/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportDTO> transferInstructor(@PathVariable Long reportId) {
        try {
            ReportDTO dto = reportService.transferOrMock(reportId);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (StripeException ex) {

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }

    }
}
