package com.learnbridge.learn_bridge_back_end.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import com.stripe.param.PaymentIntentCreateParams.CaptureMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StripeService {

    public StripeService(@Value("${stripe.api.key}") String apiKey) {
        Stripe.apiKey = apiKey;
    }

    // 1. Retrieve or create a Stripe Customer by email
    public Customer getOrCreateCustomer(String email) throws StripeException {
        // List existing customers filtered by email
        Map<String, Object> listParams = new HashMap<>();
        listParams.put("email", email);
        List<Customer> customers = Customer.list(listParams).getData();
        if (!customers.isEmpty()) {
            return customers.get(0);
        }
        // Otherwise create new customer
        Map<String, Object> createParams = new HashMap<>();
        createParams.put("email", email);
        return Customer.create(createParams);
    }

    // 2. Create a SetupIntent to vault a card for off-session use
    public SetupIntent createSetupIntent(String customerId) throws StripeException {
        SetupIntentCreateParams params = SetupIntentCreateParams.builder()
                .setCustomer(customerId)
                .addPaymentMethodType("card")
                .setConfirm(true)
                .build();
        return SetupIntent.create(params);
    }

    // 3. Attach a PaymentMethod to a Customer and set it as default
    public PaymentMethod attachPaymentMethod(String customerId, String paymentMethodId) throws StripeException {
        // Retrieve the PaymentMethod
        PaymentMethod pm = PaymentMethod.retrieve(paymentMethodId);

        // Attach the PaymentMethod to the customer
        pm.attach(Map.of("customer", customerId));

        // Retrieve the Customer object
        Customer customer = Customer.retrieve(customerId);

        // Update the customer's default payment method
        customer.update(Map.of(
                "invoice_settings", Map.of("default_payment_method", pm.getId())
        ));

        return pm;
    }


    // 4. Authorize (hold) funds using a PaymentIntent with manual capture
    public PaymentIntent authorizePayment(long amountCents, String currency,
                                          String customerId, String paymentMethodId) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountCents)
                .setCurrency(currency)
                .setCustomer(customerId)
                .setPaymentMethod(paymentMethodId)
                .setCaptureMethod(CaptureMethod.MANUAL)
                .setConfirm(true)
                .build();
        return PaymentIntent.create(params);
    }

    // 5. Capture a previously authorized PaymentIntent
    public PaymentIntent capturePayment(String paymentIntentId) throws StripeException {
        PaymentIntent pi = PaymentIntent.retrieve(paymentIntentId);
        return pi.capture();
    }

    // 6. Cancel (release) a manual-capture authorization
    public PaymentIntent cancelAuthorization(String paymentIntentId) throws StripeException {
        PaymentIntent pi = PaymentIntent.retrieve(paymentIntentId);
        return pi.cancel();
    }

    // 7. Transfer captured funds to a connected instructor account
    public Transfer transferToInstructor(long amountCents, String currency,
                                         String connectedAccountId) throws StripeException {
        TransferCreateParams params = TransferCreateParams.builder()
                .setAmount(amountCents)
                .setCurrency(currency)
                .setDestination(connectedAccountId)
                .build();
        return Transfer.create(params);
    }

    // 8. Issue a full or partial refund against a charge
    public Refund refundPayment(String chargeId, Long amountToRefundCents) throws StripeException {
        RefundCreateParams.Builder builder = RefundCreateParams.builder()
                .setCharge(chargeId);
        if (amountToRefundCents != null) {
            builder.setAmount(amountToRefundCents);
        }
        return Refund.create(builder.build());
    }
}
