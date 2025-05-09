package com.learnbridge.learn_bridge_back_end.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import com.stripe.param.PaymentMethodCreateParams.CardDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StripeService {

    public StripeService(@Value("${stripe.api.key}") String apiKey) {
        Stripe.apiKey = apiKey;
    }


    public SetupIntent createSetupIntent(String customerId) throws StripeException {
        return SetupIntent.create(
                SetupIntentCreateParams.builder()
                        .setCustomer(customerId)
                        .build()
        );
    }

    public Customer getOrCreateCustomer(String email) throws StripeException {
        List<Customer> customers = Customer.list(
                CustomerListParams.builder()
                        .setEmail(email)
                        .build()
        ).getData();

        if (!customers.isEmpty()) {
            return customers.get(0);
        }

        return Customer.create(
                CustomerCreateParams.builder()
                        .setEmail(email)
                        .build()
        );
    }

    public PaymentMethod createAndAttachCard(
            String customerId,
            String cardNumber,
            String expMonth,
            String expYear,
            String cvc
    ) throws StripeException {
        // Build the raw-card PaymentMethodCreateParams using CardDetails
        PaymentMethodCreateParams createParams = PaymentMethodCreateParams.builder()
                .setType(PaymentMethodCreateParams.Type.CARD)
                .setCard(
                        CardDetails.builder()
                                .setNumber(cardNumber)
                                .setExpMonth(Long.parseLong(expMonth))
                                .setExpYear(Long.parseLong(expYear))
                                .setCvc(cvc)
                                .build()
                )
                .build();

        // Create the PaymentMethod
        PaymentMethod paymentMethod = PaymentMethod.create(createParams);

        // Attach to the customer
        paymentMethod.attach(
                PaymentMethodAttachParams.builder()
                        .setCustomer(customerId)
                        .build()
        );

        return paymentMethod;
    }

    public PaymentMethod retrieveAndAttachPaymentMethod(
            String customerId,
            String paymentMethodId
    ) throws StripeException {
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        paymentMethod.attach(
                PaymentMethodAttachParams.builder()
                        .setCustomer(customerId)
                        .build()
        );
        return paymentMethod;
    }

    public PaymentIntent authorizePayment(long amountCents, String currency, String customerId, String paymentMethodId)
            throws StripeException {
        return PaymentIntent.create(
                PaymentIntentCreateParams.builder()
                        .setAmount(amountCents)
                        .setCurrency(currency)
                        .setCustomer(customerId)
                        .setPaymentMethod(paymentMethodId)
                        .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
                        .setConfirm(true)
                        .build()
        );
    }

    public PaymentIntent capturePayment(String paymentIntentId) throws StripeException {
        PaymentIntent pi = PaymentIntent.retrieve(paymentIntentId);
        return pi.capture();
    }

    public PaymentIntent cancelAuthorization(String paymentIntentId) throws StripeException {
        PaymentIntent pi = PaymentIntent.retrieve(paymentIntentId);
        return pi.cancel();
    }

    public Transfer transferToInstructor(long amountCents, String currency, String connectedAccountId)
            throws StripeException {
        TransferCreateParams params = TransferCreateParams.builder()
                .setAmount(amountCents)
                .setCurrency(currency)
                .setDestination(connectedAccountId)
                .build();
        return Transfer.create(params);
    }

    public Refund refundPayment(String chargeId, Long amountToRefundCents) throws StripeException {
        RefundCreateParams.Builder builder = RefundCreateParams.builder()
                .setCharge(chargeId);
        if (amountToRefundCents != null) {
            builder.setAmount(amountToRefundCents);
        }
        return Refund.create(builder.build());
    }
}
