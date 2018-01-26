package com.yachtmafia.util;

import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.*;
import java.util.Collections;

/**
 * Created by xfant on 2018-01-26.
 */
public class LoggerMaker {
    private LoggerMaker() {
        /**
         * empty
         */
    }

    public static void logError(Class classname, String text){
        logError(classname, text, null);
    }

    public static void logError(Class classname, String text, Throwable ex){
        Logging logging = LoggingOptions.getDefaultInstance().getService();
        if (ex != null){
            text += " " + ex.toString();
        }

        LogEntry entry = LogEntry.newBuilder(Payload.StringPayload.of(text))
                .setSeverity(Severity.ERROR)
                .setLogName(classname.getSimpleName())
                .setResource(MonitoredResource.newBuilder("global").build())
                .build();

        // Writes the log entry asynchronously
        logging.write(Collections.singleton(entry));
    }

    public static void logInfo(Class classname, String text){
        logInfo(classname, text, null);
    }

    public static void logInfo(Class classname, String text, Throwable ex){
        Logging logging = LoggingOptions.getDefaultInstance().getService();
        if (ex != null){
            text += " " + ex.toString();
        }

        LogEntry entry = LogEntry.newBuilder(Payload.StringPayload.of(text))
                .setSeverity(Severity.INFO)
                .setLogName(classname.getSimpleName())
                .setResource(MonitoredResource.newBuilder("global").build())
                .build();

        // Writes the log entry asynchronously
        logging.write(Collections.singleton(entry));
    }

    public static void logWarning(Class classname, String text){
        logWarning(classname, text, null);
    }

    public static void logWarning(Class classname, String text, Throwable ex){
        Logging logging = LoggingOptions.getDefaultInstance().getService();
        if (ex != null){
            text += " " + ex.toString();
        }

        LogEntry entry = LogEntry.newBuilder(Payload.StringPayload.of(text))
                .setSeverity(Severity.WARNING)
                .setLogName(classname.getSimpleName())
                .setResource(MonitoredResource.newBuilder("global").build())
                .build();

        // Writes the log entry asynchronously
        logging.write(Collections.singleton(entry));
    }

    public static void logEmergency(Class classname, String text){
        logEmergency(classname, text, null);
    }

    public static void logEmergency(Class classname, String text, Throwable ex){
        Logging logging = LoggingOptions.getDefaultInstance().getService();
        if (ex != null){
            text += " " + ex.toString();
        }

        LogEntry entry = LogEntry.newBuilder(Payload.StringPayload.of(text))
                .setSeverity(Severity.EMERGENCY)
                .setLogName(classname.getSimpleName())
                .setResource(MonitoredResource.newBuilder("global").build())
                .build();

        // Writes the log entry asynchronously
        logging.write(Collections.singleton(entry));
    }
}
