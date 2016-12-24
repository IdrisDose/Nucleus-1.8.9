/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * The {@link LoggerWrapper} is simply that, a wrapper around a {@link LoggerWrapper}. By itself, it does nothing new, but
 * makes it easier to extend the logger to provide additional functionality.
 *
 * <p>
 *     (Basically, couldn't find an appropriate wrapper class, made my own.)
 * </p>
 */
public class LoggerWrapper implements Logger {

    protected final Logger wrappedLogger;

    public LoggerWrapper(Logger wrappedLogger) {
        this.wrappedLogger = wrappedLogger;
    }

    @Override
    public String getName() {
        return wrappedLogger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return wrappedLogger.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        wrappedLogger.trace(msg);
    }

    @Override
    public void trace(String format, Object arg) {
        wrappedLogger.trace(format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        wrappedLogger.trace(format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        wrappedLogger.trace(format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        wrappedLogger.trace(msg, t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return wrappedLogger.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String msg) {
        wrappedLogger.trace(marker, msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        wrappedLogger.trace(marker, format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.trace(marker, format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        wrappedLogger.trace(marker, format, argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        wrappedLogger.trace(marker, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return wrappedLogger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        wrappedLogger.debug(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        wrappedLogger.debug(format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        wrappedLogger.debug(format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        wrappedLogger.debug(format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        wrappedLogger.debug(msg, t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return wrappedLogger.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String msg) {
        wrappedLogger.debug(marker, msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        wrappedLogger.debug(marker, format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.debug(marker, format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        wrappedLogger.debug(marker, format, arguments);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        wrappedLogger.debug(marker, msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return wrappedLogger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        wrappedLogger.info(msg);
    }

    @Override
    public void info(String format, Object arg) {
        wrappedLogger.info(format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        wrappedLogger.info(format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        wrappedLogger.info(format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        wrappedLogger.info(msg, t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return wrappedLogger.isInfoEnabled();
    }

    @Override
    public void info(Marker marker, String msg) {
        wrappedLogger.info(marker, msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        wrappedLogger.info(marker, format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.info(marker, format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        wrappedLogger.info(marker, format, arguments);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        wrappedLogger.info(marker, msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return wrappedLogger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        wrappedLogger.warn(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        wrappedLogger.warn(format, arg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        wrappedLogger.warn(format, arguments);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        wrappedLogger.warn(format, arg1, arg2);
    }

    @Override
    public void warn(String msg, Throwable t) {
        wrappedLogger.warn(msg, t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return wrappedLogger.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String msg) {
        wrappedLogger.warn(marker, msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        wrappedLogger.warn(marker, format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.warn(marker, format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        wrappedLogger.warn(marker, format, arguments);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        wrappedLogger.warn(marker, msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return wrappedLogger.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        wrappedLogger.error(msg);
    }

    @Override
    public void error(String format, Object arg) {
        wrappedLogger.error(format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        wrappedLogger.error(format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        wrappedLogger.error(format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        wrappedLogger.error(msg, t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return wrappedLogger.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String msg) {
        wrappedLogger.error(marker, msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        wrappedLogger.error(marker, format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        wrappedLogger.error(marker, format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        wrappedLogger.error(marker, format, arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        wrappedLogger.error(marker, msg, t);
    }
}