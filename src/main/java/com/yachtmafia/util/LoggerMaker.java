package com.yachtmafia.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xfant on 2018-01-26.
 */
public class LoggerMaker {

    private LoggerMaker() {
        /**
         * empty
         */
    }

    public static void logError(Object caller, String text){
        logError(caller, text, null);
    }

    public static void logError(Object caller, String text, Throwable ex){
//        Logging logging = LoggingOptions.getDefaultInstance().getService();
//        if (ex != null){
//            text += " " + ex.toString();
//        }

//        LogEntry entry = LogEntry.newBuilder(Payload.StringPayload.of(text))
//                .setSeverity(Severity.ERROR)
//                .setLogName(caller.getClass().getSimpleName())
//                .setResource(MonitoredResource.newBuilder("global").build())
//                .build();
//
//        // Writes the log entry asynchronously
//        logging.write(Collections.singleton(entry));

        Logger logger = LoggerFactory.getLogger(caller.getClass().getSimpleName());
        logger.error(text, ex);
    }

    public static void logInfo(Object caller, String text){
        logInfo(caller, text, null);
    }

    public static void logInfo(Object caller, String text, Throwable ex){
//        Logging logging = LoggingOptions.getDefaultInstance().getService();
//        if (ex != null){
//            text += " " + ex.toString();
//        }
//
//        LogEntry entry = LogEntry.newBuilder(Payload.StringPayload.of(text))
//                .setSeverity(Severity.INFO)
//                .setLogName(caller.getClass().getSimpleName())
//                .setResource(MonitoredResource.newBuilder("global").build())
//                .build();
//
//        // Writes the log entry asynchronously
//        logging.write(Collections.singleton(entry));

        Logger logger = LoggerFactory.getLogger(caller.getClass().getSimpleName());
        logger.info(text, ex);
    }

    public static void logWarning(Object caller, String text){
        logWarning(caller, text, null);
    }

    public static void logWarning(Object caller, String text, Throwable ex){
//        Logging logging = LoggingOptions.getDefaultInstance().getService();
//        if (ex != null){
//            text += " " + ex.toString();
//        }
//
//        LogEntry entry = LogEntry.newBuilder(Payload.StringPayload.of(text))
//                .setSeverity(Severity.WARNING)
//                .setLogName(caller.getClass().getSimpleName())
//                .setResource(MonitoredResource.newBuilder("global").build())
//                .build();
//
//        // Writes the log entry asynchronously
//        logging.write(Collections.singleton(entry));


        Logger logger = LoggerFactory.getLogger(caller.getClass().getSimpleName());
        logger.warn(text, ex);
    }

    public static void logEmergency(Object caller, String text){
        logEmergency(caller, text, null);
    }

    public static void logEmergency(Object caller, String text, Throwable ex){
//        Logging logging = LoggingOptions.getDefaultInstance().getService();
//        if (ex != null){
//            text += " " + ex.toString();
//        }
//
//        LogEntry entry = LogEntry.newBuilder(Payload.StringPayload.of(text))
//                .setSeverity(Severity.EMERGENCY)
//                .setLogName(caller.getClass().getSimpleName())
//                .setResource(MonitoredResource.newBuilder("global").build())
//                .build();
//
//        // Writes the log entry asynchronously
//        logging.write(Collections.singleton(entry));

        Logger logger = LoggerFactory.getLogger(caller.getClass().getSimpleName());
        logger.error(text, ex);
    }
}
