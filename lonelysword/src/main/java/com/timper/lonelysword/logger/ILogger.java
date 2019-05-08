package com.timper.lonelysword.logger;


/**
 * Logger
 *
 * @author
 * @version 1.0
 * @since 16/5/16 下午5:39
 */
public interface ILogger {

    void showLog(boolean isShowLog);

    void showStackTrace(boolean isShowStackTrace);

    void debug(String tag, String message);

    void info(String tag, String message);

    void warning(String tag, String message);

    void error(String tag, String message);

    void monitor(String message);

    boolean isMonitorMode();

    String getDefaultTag();
}
