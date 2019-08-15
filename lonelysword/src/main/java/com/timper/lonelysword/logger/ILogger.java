package com.timper.lonelysword.logger;


/**
 * User: tangpeng.yang
 * Date: 2019/3/16
 * Description: logger日志类
 * FIXME
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
