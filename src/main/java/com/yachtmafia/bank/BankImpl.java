package com.yachtmafia.bank;

import com.paypal.api.payments.*;
import com.paypal.api.payments.Error;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.apache.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankImpl implements Bank {
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    // Replace these values with your clientId and secret. You can use these to get started right now.
    String clientId = "AbTewYvrX2Ts8bDNai80TeybnI8G9qKoPsUQoZN8Qs0fMvZZJsgCRBeRyIduGsuYLZ-sbgj47ZNeNFeV";
    String clientSecret = "EBT3S1kHYZVH6Um_VlIfRRTEKTc86WzUniKvac770EL6J1T2ig77X2VrLWpfx4tBBBNwkQoCGXFhpjcK";

    @Override
    public boolean transferFromBankToExchange(String currency, long amount) {
        throw new NotImplementedException();
    }

    @Override
    public boolean payUser(String currency, long amount, String user) {
        Currency paypalAmount = new Currency(currency, String.valueOf(amount / getUnitsPerCoin(currency)));

        PayoutItem payoutItem = new PayoutItem();
        payoutItem.setAmount(paypalAmount);
        payoutItem.setNote("With love, from Cryptobox");
        payoutItem.setRecipientType("EMAIL");
        payoutItem.setReceiver(user);

        List<PayoutItem> items = new ArrayList<>();
        items.add(payoutItem);

        PayoutSenderBatchHeader batchHeader = new PayoutSenderBatchHeader();
        batchHeader.setRecipientType("EMAIL");
        batchHeader.setEmailSubject("You've got funds!");

        Payout payout = new Payout();
        payout.setItems(items);
        payout.setSenderBatchHeader(batchHeader);

        try {
            APIContext context = new APIContext(clientId, clientSecret, "sandbox");
            Map<String, String> params = new HashMap<>();
//            params.put("email_subject", "You have a payout!");

            PayoutBatch payoutBatch = payout.create(context, params);
            PayoutBatchHeader batchHeaderResp = payoutBatch.getBatchHeader();
            String batchStatus = batchHeaderResp.getBatchStatus();

            /**
             * todo: forever PENDING
             */
            while ("PENDING".equals(batchStatus)) {
                LOG.info("Waiting for transaction to complete... ");
                Thread.sleep(1000);
            }

            List<PayoutItemDetails> payoutBatchItems = payoutBatch.getItems();
            if (payoutBatchItems != null) {
                for (PayoutItemDetails item : payoutBatchItems) {
                    String transactionStatus = item.getTransactionStatus();
                    LOG.info(transactionStatus);
                    Error errors = item.getErrors();
                    List<ErrorDetails> details = errors.getDetails();
                    for (ErrorDetails det : details) {
                        String issue = det.getIssue();
                        LOG.error(issue);
                    }
                }
            }

            LOG.info("");
        } catch (PayPalRESTException e) {
            // Handle errors
            LOG.error("Error: ", e);
        } catch (InterruptedException e) {
            LOG.error("Caught: ", e);
            Thread.currentThread().interrupt();
        }
        throw new NotImplementedException();
    }

    private long getUnitsPerCoin(String currency) {
        switch (currency) {
            case "GBP":
                return 100;
            case "USD":
                return 100;
            case "EUR":
                return 100;
            case "JPY":
                return 1000;
            case "CHF":
                return 100;
            case "CAD":
                return 100;
            default:
                LOG.fatal("UNKNOWN CURRENCY: " + currency);
                return 0;
        }
    }

    public void test() {
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal("1.00");

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        java.util.List<Transaction> transactions = new java.util.ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("https://example.com/cancel");
        redirectUrls.setReturnUrl("https://example.com/return");
        payment.setRedirectUrls(redirectUrls);

        try {
            APIContext context = new APIContext(clientId, clientSecret, "sandbox");
            Payment createdPayment = payment.create(context);
            LOG.info(createdPayment.toString());
        } catch (PayPalRESTException e) {
            // Handle errors
            LOG.error("Error: ", e);
        }
    }
}
