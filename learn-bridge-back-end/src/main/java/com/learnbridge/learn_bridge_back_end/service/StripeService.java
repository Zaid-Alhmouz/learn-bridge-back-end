package com.learnbridge.learn_bridge_back_end.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import com.stripe.param.PaymentMethodCreateParams.CardDetails;
import com.stripe.service.CustomerService;
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

    public PaymentIntent authorizePayment(long amountCents,
                                          String currency,
                                          String customerId,
                                          String paymentMethodId)
            throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountCents)
                .setCurrency(currency)
                .setCustomer(customerId)
                .setPaymentMethod(paymentMethodId)
                .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
                .setConfirm(true)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .setAllowRedirects(
                                        PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER
                                )
                                .build()
                )
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

    /**
     * Update the customer to use the given PM as their default for invoices/subscriptions.
     */
    public Customer updateCustomerDefaultPaymentMethod(String customerId, String pmId) throws StripeException {
        // retrieve instance, then call instance.update(...)
        Customer customer = Customer.retrieve(customerId);
        CustomerUpdateParams params = CustomerUpdateParams.builder()
                .setInvoiceSettings(
                        CustomerUpdateParams.InvoiceSettings.builder()
                                .setDefaultPaymentMethod(pmId)
                                .build()
                )
                .build();
        return customer.update(params);
    }


    /**
     * List all card-type PaymentMethods for a given customer.
     */
    public List<PaymentMethod> listCustomerPaymentMethods(String customerId,
                                                          PaymentMethodListParams.Type type)
            throws StripeException {
        return PaymentMethod.list(
                PaymentMethodListParams.builder()
                        .setCustomer(customerId)
                        .setType(type)
                        .build()
        ).getData();
    }

    /**
     * Detach a PaymentMethod from any customer, cleaning up orphaned PMs.
     */
    public void detachPaymentMethod(String paymentMethodId) throws StripeException {
        PaymentMethod pm = PaymentMethod.retrieve(paymentMethodId);
        pm.detach(PaymentMethodDetachParams.builder().build());
    }

    public PaymentMethod attachPaymentMethodToCustomer(String customerId, String pmId) throws StripeException {
        return PaymentMethod.retrieve(pmId)
                .attach(PaymentMethodAttachParams.builder()
                        .setCustomer(customerId)
                        .build());
    }

    /**
     * Retrieve a PM without attaching.
     */
    public PaymentMethod retrievePaymentMethod(String pmId) throws StripeException {
        return PaymentMethod.retrieve(pmId);
    }

}
