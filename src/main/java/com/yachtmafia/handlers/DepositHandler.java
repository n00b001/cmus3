package com.yachtmafia.handlers;

import com.yachtmafia.cryptoKeyPairs.CryptoKeyPair;
import com.yachtmafia.cryptoKeyPairs.CryptoKeyPairGenerator;
import com.yachtmafia.messages.SwapMessage;
import com.yachtmafia.util.AddressBalance;
import com.yachtmafia.util.StatusLookup;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.Wallet;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.yachtmafia.util.Const.DEPOSIT_TOPIC_NAME;

public class DepositHandler implements MessageHandler {
    private static final String TOPIC_NAME = DEPOSIT_TOPIC_NAME;
//    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
private static final Logger logger = LogManager.getLogger(DepositHandler.class);

    private final HandlerDAO handlerDAO;
    private ExecutorService pool;
    private ConsumerRecord<String, String> message;

    public DepositHandler(HandlerDAO handlerDAO, ExecutorService pool) {
        this.handlerDAO = handlerDAO;
        this.pool = pool;
    }

    private DepositHandler(HandlerDAO handlerDAO, ConsumerRecord<String, String> message) {
        this.handlerDAO = handlerDAO;
        this.message = message;
    }

    @Override
    public Future<Boolean> run(ConsumerRecord<String, String> message) {
        return pool.submit(new DepositHandler(handlerDAO, message));
    }

    @Override
    public Boolean call() throws Exception {
        if (TOPIC_NAME.equals(message.topic())) {
            SwapMessage swapMessage = new SwapMessage(message.value());
            handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.REQUEST_RECEIVED_BY_SERVER);
            logger.info("swapmessage: " + swapMessage.toString());
            String publicAddress = handlerDAO.getDbWrapper().getPublicAddress(swapMessage.getUsername(),
                    swapMessage.getToCoinName());
            if (publicAddress == null){
                try {
                    CryptoKeyPair keyPair = CryptoKeyPairGenerator.parse(swapMessage.getToCoinName(),
                            handlerDAO.getNetwork());
                    boolean success = handlerDAO.getDbWrapper().addNewWallet(swapMessage.getUsername(),
                            swapMessage.getToCoinName(),
                            keyPair.getPublicAddress(), keyPair.getPrivateKey());
                    if (!success) {
                        logger.error("Did not add wallet successfully! " + message);
                        handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.COULD_NOT_ADD_WALLET);
                        return false;
                    }
                    publicAddress = keyPair.getPublicAddress();
                }catch (Exception e){
                    logger.error("Did not add wallet successfully! " + message, e);
                    handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.COULD_NOT_ADD_WALLET);
                    return false;
                }
            }
            handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.WALLET_CREATED);
//            boolean success = handlerDAO.getBank().transferFromBankToExchange(swapMessage.getFromCoinName(),
//                    swapMessage.getAmountOfCoin(), handlerDAO.getExchange());
//            if (!success){
//                logError(this, "Did not transfer balance from bank to exchange! "
//                         + message);
//                return false;
//            }
//            String purchasedAmount = handlerDAO.getExchange().exchangeCurrency(swapMessage.getFromCoinName(),
//                    swapMessage.getToCoinName(), swapMessage.getAmountOfCoin());
//            if(purchasedAmount == null){
//                logError(this, "Failed to make purchase: " + message.toString());
//            }

            boolean success = sendEmail(publicAddress, swapMessage);
            if (!success){
                logger.error("Did not email! " + message);
                handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.COULD_NOT_SEND_EMAIL);
                return false;
            }

            handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.SUBMITTING_TO_EXCHANGE);
            String purchasedAmount = waitForFunds(publicAddress);
            handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.VERIFYING_EXCHANGE);

//            success = handlerDAO.getExchange().withdrawCrypto(
//                    swapMessage.getToCoinName(), publicAddress, purchasedAmount);
//            if (!success){
//                logError(this, "Did not withdraw coins! " + message);
//                return false;
//            }
            handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.ADDING_TO_WALLET);
            success = handlerDAO.getDbWrapper().addPortfolioBalance(swapMessage, purchasedAmount);
            handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.FINALISING);
            if (!success){
                logger.error("Did not add portfolio balance " + message);
                handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.COULD_NOT_ADD_PORTFOLIO_BALANCE);
                return false;
            }
            handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.SUCCESS);
            return true;
        }
        return false;
    }

    private boolean sendEmail(String publicAddress, SwapMessage message) {

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                            handlerDAO.getConfig().EMAIL_SENDER,
                            handlerDAO.getConfig().EMAIL_PASSWORD);
                    }
                });

        try {

            for (String recipient : handlerDAO.getConfig().EMAIL_RECIPTS) {
                Message mailMessage = new MimeMessage(session);
                mailMessage.setFrom(new InternetAddress(handlerDAO.getConfig().EMAIL_SENDER));
                mailMessage.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(recipient));
                mailMessage.setSubject("User Has made purchase: " + message.getID());
                mailMessage.setText("Address: " + publicAddress
                        + "\n\nCoin from: " + message.getFromCoinName()
                        + "\nAmount: " + message.getAmountOfCoin()
                        + "\n\nCoin to: " + message.getToCoinName() + "\n"
                        + "\n\n\nFull message: \n" + message.toString());

                Transport.send(mailMessage);

                logger.info("Sent email to: " + recipient + "!");
            }
            return true;

        } catch (MessagingException e) {
            logger.error("Email error: ", e);
        }
        return false;
    }

    private String waitForFunds(String publicAddress) throws InterruptedException {
        NetworkParameters network = handlerDAO.getNetwork();
        Address address = Address.fromBase58(network, publicAddress);
        AddressBalance addressBalance = new AddressBalance(address);

        try {
            Wallet wallet = handlerDAO.getWalletWrapper().getBitcoinWalletAppKit().wallet();
            Coin balance = wallet.getBalance(addressBalance);

//        Wallet wallet = Wallet.fromWatchingKeyB58(MainNetParams.get(), publicAddress, 0);
//        Coin balance = wallet.getBalance();
            logger.info("Waiting for exchange...");
            while (balance.isZero()) {
                Thread.sleep(1000);
                balance = wallet.getBalance();
            }
            logger.info("Exchange done for value: " + balance.getValue());
            return String.valueOf(balance.getValue());
        } catch (IllegalStateException ex) {
            logger.error("Caught: ", ex);
            WalletAppKit walletAppKit = handlerDAO.getWalletWrapper().getBitcoinWalletAppKit();
            if (!walletAppKit.isRunning()) {
                throw new RuntimeException("Wallet is not running!");
            }
            throw new RuntimeException(ex);
        }

//        ECKey ecKey = ECKey.fromPublicOnly()
//        List<ECKey> ecKeyList = new ArrayList<>();
//        ecKeyList.add(ecKey);
//        Wallet wallet = Wallet.fromKeys(network, ecKeyList);
//
//        org.bitcoinj.core.Address address = org.bitcoinj.core.Address.fromBase58(network, depositAddress);
//
//
//        WalletWrapper walletWrapper = handlerDAO.getWalletWrapper();
//        walletWrapper.
    }
}
