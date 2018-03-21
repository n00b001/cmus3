package com.yachtmafia.bank;

import com.paypal.api.payments.*;
import com.paypal.api.payments.Error;
import com.paypal.base.rest.APIContext;
import com.yachtmafia.config.Config;
import com.yachtmafia.exchange.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.yachtmafia.util.Util.getCoinDoubleValue;

public class BankImpl implements Bank {
//    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());

    private static final Logger logger = LogManager.getLogger(Bank.class);
    private final Config config;

    public BankImpl(Config config) {
        this.config = config;
    }

    @Override
    public boolean transferFromBankToExchange(String currency, String amount, Exchange exchange) {
        throw new NotImplementedException();
    }

    @Override
    public boolean payUser(String currency, String amount, String user) {
        BigDecimal coinDoubleValue = getCoinDoubleValue(amount, currency, 2);
        BigDecimal bigDecimal = coinDoubleValue.setScale(2, BigDecimal.ROUND_DOWN);
        String value = bigDecimal.toPlainString();
        Currency paypalAmount = new Currency(currency, value);

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

            PayoutBatch payoutBatch = payout.create(context, null);
            PayoutBatchHeader batchHeaderResp = payoutBatch.getBatchHeader();
            String batchStatus = batchHeaderResp.getBatchStatus();
            if (!"PENDING".equals(batchStatus)) {
                throw new Exception("Unexpected status: " + batchStatus);
            }

            String payoutBatchId = batchHeaderResp.getPayoutBatchId();
            PayoutBatch response = Payout.get(context, payoutBatchId);
            while("PENDING".equals(response.getBatchHeader().getBatchStatus())){
                Thread.sleep(2000);
                logger.info("Waiting for pending payment...");
                response = Payout.get(context, payoutBatchId);
            }

            while("PROCESSING".equals(response.getBatchHeader().getBatchStatus())){
                Thread.sleep(20000);
                logger.info("Waiting for payment to process...");
                response = Payout.get(context, payoutBatchId);
            }


            printErrors(response);

            if (!"SUCCESS".equals(response.getBatchHeader().getBatchStatus())){
                logger.error("Did not successfully submit payment! currency: "
                        + currency + " amount : " + amount + " user: " + user);
                return false;
            }
            return true;
        } catch (Exception e) {
            // Handle errors
            logger.error("Error: " + payout.toJSON(), e);
            return false;
        }
    }

    private void printErrors(PayoutBatch response) {
        List<PayoutItemDetails> payoutBatchItems = response.getItems();
        if (payoutBatchItems != null) {
            for (PayoutItemDetails item : payoutBatchItems) {
                Error errors = item.getErrors();
                if (errors != null) {
                    logger.error("Caught: ");
                    List<ErrorDetails> details = errors.getDetails();
                    for (ErrorDetails det : details) {
                        String issue = det.getIssue();
                        logger.error(issue);
                    }
                }
            }
        }
    }
}
