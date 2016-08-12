package com.xzj.babyfun.logging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import android.os.Environment;
import android.os.Process;
import android.text.TextUtils;

import com.xzj.babyfun.constant.Constant;

/**
 * 
 * 内部日志输出类，用于内部调试输出，开发者不可达 。<br>
 * 
 * 辅助 log 类，输出log到 logcat 以及输出到 log 文件。接口形式采用android.util.Log接口样式，方便移植。<br>
 * 
 * 可以通过 {@link #setLogEnabled(boolean)}函数设置log是否输出。默认为true<br>
 * 可以通过 {@link #setLog2File(boolean)}设置log输出到sdcard文件，默认为false。文件名以进程名为前缀。
 * 
 * 如果需要输出log到文件，需要设置写 sdcard权限 ： &lt;uses-permission
 * android:name="android.permission.WRITE_EXTERNAL_STORAGE" /> <br>
 * 文件循环个数为 {@link #FILE_NUMBER}, 单个文件最大单位 {@link #FILE_LIMETE} <br>
 * 输出到文件是封装的java.util.logging.Logger。
 */
public final class SLog {

    private static final String TAG = "AIZI-SLog";

    /** log开关，关闭后，log功能失效，不能输出任何东西。！！relase发布时和线上代码，保持关闭。 */
    private static boolean sLogEnabled = true; // false & Constants.DEBUG;

    /** 用于控制是输出log到文件，还是logcat。 */
    private static boolean sLog2File = false;

    /** java.util.logging.Logger object. */
    private static Logger sFilelogger;

    /** 单个log文件的大小单位： byte。 */
    public static final int FILE_LIMETE = 1024 * 1024 * 10;
    /** 最多的log文件的个数，以 0123编号作为后缀。 */
    public static final int FILE_NUMBER = 2;

    /** private constructor. */
    private SLog() {
    }

    /**
     * Send a VERBOSE log message.
     * 
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     */
    public static void v(String tag, String msg) {
        if (sLogEnabled) {
            tag = Constant.LOG_PREFIX + tag;
            if (sLog2File && sFilelogger != null) {
                sFilelogger.log(Level.INFO, tag + ": " + msg);
            } else {
                android.util.Log.v(tag, msg);
            }
        }
    }

    /**
     * Send a VERBOSE log message and log the exception.
     * 
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static void v(String tag, String msg, Throwable tr) {
        v(tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * log info.
     * 
     * @param tag
     *            tag
     * @param msg
     *            msg
     */
    public static void i(String tag, String msg) {
        if (sLogEnabled) {
            tag = Constant.LOG_PREFIX + tag;
            if (sLog2File && sFilelogger != null) {
                sFilelogger.log(Level.INFO, tag + ": " + msg);
            } else {
                android.util.Log.i(tag, msg);
            }
        }
    }

    /**
     * Send a INFO log message and log the exception.
     * 
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static void i(String tag, String msg, Throwable tr) {
        i(tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * log debug info.
     * 
     * @param tag
     *            tag
     * @param msg
     *            msg
     */
    public static void d(String tag, String msg) {
        if (sLogEnabled) {
            tag = Constant.LOG_PREFIX + tag;
            if (sLog2File && sFilelogger != null) {
                sFilelogger.log(Level.INFO, tag + ": " + msg);
            } else {
                android.util.Log.d(tag, msg);
            }
        }
    }

    /**
     * Send a debug log message and log the exception.
     * 
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static void d(String tag, String msg, Throwable tr) {
        d(tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * log warn.
     * 
     * @param tag
     *            tag
     * @param msg
     *            msg
     */
    public static void w(String tag, String msg) {
        if (sLogEnabled) {
            tag = Constant.LOG_PREFIX + tag;
            if (sLog2File && sFilelogger != null) {
                sFilelogger.log(Level.WARNING, tag + ": " + msg);
            } else {
                android.util.Log.w(tag, msg);
            }
        }
    }

    /**
     * Send a WARN log message and log the exception.
     * 
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static void w(String tag, String msg, Throwable tr) {
        w(tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * log error.
     * 
     * @param tag
     *            tag
     * @param msg
     *            msg
     */
    public static void e(String tag, String msg) {
        if (sLogEnabled) {
            tag = Constant.LOG_PREFIX + tag;
            if (sLog2File && sFilelogger != null) {
                sFilelogger.log(Level.SEVERE, tag + ": " + msg);
            } else {
                if (!TextUtils.isEmpty(msg)) {
                    android.util.Log.e(tag, msg);
                }
            }
        }
    }

    /**
     * log error.
     * 
     * @param tag
     *            tag
     * @param e
     *            Throwable
     */
    public static void e(String tag, Throwable e) {
        String msg = getStackTraceString(e);

        e(tag, msg);
    }

    /**
     * Send a ERROR log message and log the exception.
     * 
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     */
    public static void e(String tag, String msg, Throwable tr) {
        msg = msg + '\n' + getStackTraceString(tr);

        e(tag, msg);
    }

    /**
     * Handy function to get a loggable stack trace from a Throwable
     * 
     * @param tr
     *            An exception to log
     * 
     * @return tr StackTraceString.
     */
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * 获取当前进程名。
     * 
     * @return 当前进程名。
     */
    private static String getLogFileName() {
        int pid = Process.myPid();
        String name = getProcessNameForPid(pid);
        if (TextUtils.isEmpty(name)) {
            name = "BaiduFileLog";
        }
        name = name.replace(':', '_');

        return name;
    }

    /**
     * 根据 进程 pid 获取进程名。
     * 
     * @param pid
     *            process id
     * @return 进程名
     */
    private static String getProcessNameForPid(int pid) {
        BufferedReader bf = null;
        FileReader fr = null;
        String cmdlinePath = "/proc/" + pid + "/cmdline"; // cmdline file path
        String statusPath = "/proc/" + pid + "/status"; // proc status file path

        String name = ""; // 进程名

        try {
            // 首先根据 cmdline 获取进程名
            File file = new File(cmdlinePath);
            bf = new BufferedReader(new FileReader(file));
            String line = null;
            line = bf.readLine();

            if (!TextUtils.isEmpty(line)) {
                // 从 cmdline中获取进程名
                int index = line.indexOf(0); // cmdline 为 c语言格式字符串，后边为 0
                name = line.substring(0, index);
            } else {
                // 从proc status获取进程名
                file = new File(statusPath);
                fr = new FileReader(file);
                bf = new BufferedReader(fr);
                line = bf.readLine();
                while (line != null) {
                    if (line.startsWith("Name:")) {
                        int index = line.indexOf("\t"); // 比如
                                                        // "Name:\tcom.baidu.appsearch"
                        if (index >= 0) {
                            name = line.substring(index + 1);
                        }
                        break;
                    }
                    line = bf.readLine();
                }
            }
        } catch (Exception e) {
            SLog.e(TAG, "error " + e.getMessage());
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException e) {
                    SLog.e(TAG, e);
                }
            }
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    SLog.e(TAG, e);
                }
            }
        }

        return name;
    }

    /**
     * 设置 log 是否输出。
     * 
     * @param enableOrNot
     *            true：输出log，false：log不会输出
     */
    public static void setLogEnabled(boolean enableOrNot) {
        sLogEnabled = enableOrNot;
    }

    /**
     * 设置 log 文件是否输出到 sdcard 根目录下的文件中，文件名为 进程名为前缀。
     * 
     * @param log2file
     *            true：输出到文件，false：打印到控制台
     */
    public static void setLog2File(boolean log2file) {
        sLog2File = log2file;

        if (sLog2File && sFilelogger == null) {

            /** java.util.logging.Logger 用到 */
            final String loggerName = getLogFileName();

            /** log文件名。 不同项目需要修改此文件名。 */
            final String logFileName =
                    new File(Environment.getExternalStorageDirectory(), loggerName).getAbsolutePath();

            FileHandler fhandler;

            try {
                // fhandler = new FileHandler(logFileName + ".log", true);
                fhandler = new FileHandler(logFileName + "_%g.log", FILE_LIMETE, FILE_NUMBER, true);
                fhandler.setFormatter(new SimpleFormatter());

                sFilelogger = Logger.getLogger(loggerName);
                sFilelogger.setLevel(Level.ALL);
                sFilelogger.addHandler(fhandler);

            } catch (SecurityException e) {
                SLog.e(TAG, "error " + e.getMessage());
            } catch (IOException e) {
                SLog.e(TAG, "error " + e.getMessage());
            }
        }
    }
}
