package com.learnbridge.learn_bridge_back_end.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.Transfer;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.TransferCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    public PaymentIntent createAuthorization(long amount, String currency, String paymentMethodId) throws StripeException
    {

        // authorization hold for up to 7 days
        return PaymentIntent.create(
                PaymentIntentCreateParams.builder()
                        .setAmount(amount)
                        .setCurrency(currency)
                        .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
                        .setPaymentMethod(paymentMethodId)
                        .setConfirm(true)
                        .build()
        );
    }

    public PaymentIntent capturePayment(String paymentIntentId) throws StripeException
    {

        // finalize the hold
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        return paymentIntent.capture();
    }

    public Transfer transferToInstructor(long amount, String currency, String instructorAccountId) throws StripeException
    {

        // move funds to connected account account
        return Transfer.create(
                TransferCreateParams.builder()
                        .setAmount(amount)
                        .setCurrency(currency)
                        .setDestination(instructorAccountId)
                        .build()
        );
    }

    public Refund refundChange(String changeId, Long amountToRefund) throws StripeException
    {
        // full or partial refund
        RefundCreateParams.Builder builder = RefundCreateParams.builder().setCharge(changeId);

        if (amountToRefund != null)
            builder.setAmount(amountToRefund);

        return Refund.create(builder.build());
    }
}
