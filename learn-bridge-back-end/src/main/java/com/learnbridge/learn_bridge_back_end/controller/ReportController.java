package com.learnbridge.learn_bridge_back_end.controller;


import com.learnbridge.learn_bridge_back_end.dto.ReportDTO;
import com.learnbridge.learn_bridge_back_end.dto.UserDTO;
import com.learnbridge.learn_bridge_back_end.entity.Report;
import com.learnbridge.learn_bridge_back_end.entity.ReportStatus;
import com.learnbridge.learn_bridge_back_end.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/reports")
@CrossOrigin(origins = "http://localhost:4200")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/pending-reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReportDTO>> getPendingReports() {

        List<ReportDTO> pendingReports = reportService.getAllPendingReports();
        if (pendingReports.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else
            return new ResponseEntity<>(pendingReports, HttpStatus.OK);

    }

    @PutMapping("/resolve/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportDTO> updateReportStatus(@PathVariable Long reportId) {

            ReportDTO resolvedReport = reportService.resolveReport(reportId);
            if (resolvedReport == null) {
                return ResponseEntity.notFound().build();
            }
            else {
                return new ResponseEntity<>(resolvedReport, HttpStatus.OK);
            }
    }

    @DeleteMapping("delete/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportDTO> deleteReport(@PathVariable Long reportId) {

        ReportDTO deletedReport = reportService.deleteReport(reportId);

        if (deletedReport == null) {
            return ResponseEntity.notFound().build();
        }
        else {
            return new ResponseEntity<>(deletedReport, HttpStatus.OK);
        }
    }

    @PutMapping("block/{reportedUser}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> blockReport(@PathVariable Long reportedUser) {

        UserDTO blockedUser = reportService.blockUser(reportedUser);
        if (blockedUser == null) {
            return ResponseEntity.notFound().build();
        }
        else {
            return new ResponseEntity<>(blockedUser, HttpStatus.OK);
        }
    }

}
