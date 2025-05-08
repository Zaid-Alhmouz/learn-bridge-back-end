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

    public Customer getOrCreateCustomer(String email) throws StripeException {
        Map<String, Object> listParams = new HashMap<>();
        listParams.put("email", email);
        List<Customer> customers = Customer.list(listParams).getData();
        if (!customers.isEmpty()) {
            return customers.get(0);
        }
        Map<String, Object> createParams = new HashMap<>();
        createParams.put("email", email);
        return Customer.create(createParams);
    }

    public PaymentMethod createAndAttachCard(String customerId, String cardNumber, String expMonth, String expYear, String cvc) throws StripeException {
        Map<String, Object> cardParams = new HashMap<>();
        cardParams.put("number", cardNumber);
        cardParams.put("exp_month", expMonth);
        cardParams.put("exp_year", expYear);
        cardParams.put("cvc", cvc);

        Map<String, Object> paymentMethodParams = new HashMap<>();
        paymentMethodParams.put("type", "card");
        paymentMethodParams.put("card", cardParams);

        PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams);
        paymentMethod.attach(Map.of("customer", customerId));

        Customer customer = Customer.retrieve(customerId);
        customer.update(Map.of(
                "invoice_settings", Map.of("default_payment_method", paymentMethod.getId())
        ));

        return paymentMethod;
    }

    public PaymentIntent authorizePayment(long amountCents, String currency, String customerId, String paymentMethodId) throws StripeException {
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

    public PaymentIntent capturePayment(String paymentIntentId) throws StripeException {
        PaymentIntent pi = PaymentIntent.retrieve(paymentIntentId);
        return pi.capture();
    }

    public PaymentIntent cancelAuthorization(String paymentIntentId) throws StripeException {
        PaymentIntent pi = PaymentIntent.retrieve(paymentIntentId);
        return pi.cancel();
    }

    public Transfer transferToInstructor(long amountCents, String currency, String connectedAccountId) throws StripeException {
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



    public PaymentMethod retrieveAndAttachPaymentMethod(String customerId, String paymentMethodId) throws StripeException {
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);

        // Attach payment method to customer
        Map<String, Object> attachParams = new HashMap<>();
        attachParams.put("customer", customerId);
        paymentMethod.attach(attachParams);

        // Optionally set as default payment method for the customer
        Customer customer = Customer.retrieve(customerId);
        Map<String, Object> updateParams = new HashMap<>();
        Map<String, Object> invoiceSettings = new HashMap<>();
        invoiceSettings.put("default_payment_method", paymentMethod.getId());
        updateParams.put("invoice_settings", invoiceSettings);
        customer.update(updateParams);

        return paymentMethod;
    }

}
