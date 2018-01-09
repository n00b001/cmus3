package com.yachtmafia.bank;

import com.paypal.api.payments.*;
import com.paypal.api.payments.Error;
import com.paypal.base.rest.APIContext;
import com.yachtmafia.config.Config;
import com.yachtmafia.exchange.Exchange;
import org.apache.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankImpl implements Bank {
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private final Config config;

    public BankImpl(Config config) {
        this.config = config;
    }

    @Override
    public boolean transferFromBankToExchange(String currency, long amount, Exchange exchange) {
        throw new NotImplementedException();
    }

    @Override
    public boolean payUser(String currency, long amount, String user) {
        long unitsPerCoin = getUnitsPerCoin(currency);
        if (unitsPerCoin == 0) {
            LOG.error("Unknown currency: " + currency);
            return false;
        }
        Currency paypalAmount = new Currency(currency, String.valueOf(amount / unitsPerCoin));

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
            APIContext context = new APIContext(config.CLIENT_ID_PAYPAL, config.CLIENT_SECRET_PAYPAL,
                    "sandbox");
            Map<String, String> params = new HashMap<>();
//            params.put("email_subject", "You have a payout!");

            PayoutBatch payoutBatch = payout.create(context, params);
            PayoutBatchHeader batchHeaderResp = payoutBatch.getBatchHeader();
            String batchStatus = batchHeaderResp.getBatchStatus();
            if (!"PENDING".equals(batchStatus)) {
                throw new Exception("Unexpected status: " + batchStatus);
            }

//            /**
//             * todo: forever PENDING
//             */
//            while ("PENDING".equals(batchStatus)) {
//                LOG.info("Waiting for transaction to complete... ");
//                Thread.sleep(10000);
//            }

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
                return false;
            }
        } catch (Exception e) {
            // Handle errors
            LOG.error("Error: ", e);
            return false;
        }
        return true;
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
}
