package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.dao.PaymentInfoDAO;
import com.learnbridge.learn_bridge_back_end.entity.PaymentInfo;
import com.learnbridge.learn_bridge_back_end.entity.Session;
import com.learnbridge.learn_bridge_back_end.service.NotificationService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/webhooks/stripe")
public class WebhookController {

    @Value("${stripe.webhook.secret:default_secret}")
    private String endpointSecret;

    @Autowired
    private PaymentInfoDAO paymentInfoDAO;

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<String> handleStripeEvent(
            @RequestHeader("Stripe-Signature") String sigHeader,
            @RequestBody String payload) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook signature verification failed");
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded((PaymentIntent) event.getDataObjectDeserializer()
                        .getObject().orElse(null));
                break;
            case "charge.refunded":
                handleChargeRefunded((Charge) event.getDataObjectDeserializer()
                        .getObject().orElse(null));
                break;
            case "transfer.paid":
                handleTransferPaid((Transfer) event.getDataObjectDeserializer()
                        .getObject().orElse(null));
                break;
            default:
                // Unexpected event type
                break;
        }
        return ResponseEntity.ok("Received");
    }

    private void handlePaymentIntentSucceeded(PaymentIntent pi) {
        if (pi == null) return;

        Optional<PaymentInfo> opt = paymentInfoDAO.findByStripePaymentIntentId(pi.getId());
        opt.ifPresent(info -> {
            info.setCaptured(true);

            String latestChargeId = pi.getLatestCharge();
            if (latestChargeId != null) {
                try {
                    Charge charge = Charge.retrieve(latestChargeId);
                    info.setStripeChargeId(charge.getId());
                    paymentInfoDAO.updatePaymentInfo(info);

                    // Notify user funds captured
                    notificationService.sendTransferNotification(
                            info.getCard().getUser(), // Assuming the instructor is associated with the card's user
                            info.getAmount(),
                            charge.getId()
                    );
                } catch (StripeException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    private void handleChargeRefunded(Charge charge) {
        if (charge == null) return;
        Optional<PaymentInfo> opt = paymentInfoDAO.findByStripeChargeId(charge.getId());
        opt.ifPresent(info -> {
            info.setRefunded(true);
            String refundId = charge.getRefunds().getData().get(0).getId();
            info.setStripeRefundId(refundId);
            paymentInfoDAO.updatePaymentInfo(info);
            // Notify learner of refund
            notificationService.sendRefundNotification(
                    info.getUser(),
                    info.getAmount(),
                    refundId
            );
        });
    }

    private void handleTransferPaid(Transfer transfer) {
        if (transfer == null) return;
        Optional<PaymentInfo> opt = paymentInfoDAO.findByStripeTransferId(transfer.getId());
        opt.ifPresent(info -> {
            info.setStripeTransferId(transfer.getId());
            paymentInfoDAO.updatePaymentInfo(info);
            // Notify instructor of transfer
            notificationService.sendTransferNotification(
                    info.getCard().getUser(),
                    info.getAmount(),
                    info.getStripeChargeId()
            );
        });
    }
}
