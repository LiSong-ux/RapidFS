package net.industryhive.common.logger;

import net.industryhive.common.system.Constant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author 未央
 * @Create 2020-10-05 9:48
 */
public class Logger {
    private static final File log = new File(Constant.CURRENT_LOG);
    private static FileWriter logWriter = null;
    private static String lineBreak = "\n";

    static {
        try {
            if (Constant.OS_NAME.contains("windows")) {
                lineBreak = "\r\n";
            }
            if (!log.exists()) {
                if (!log.getParentFile().exists()) {
                    System.out.println(currentDate() + " [ERROR  ]Log Initialize Failure: Path logs Not Found");
                    System.exit(0);
                }
            }
            logWriter = new FileWriter(log, true);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void info(String info) {
        String output = currentDate() + " [INFO   ]" + info + lineBreak;
        output(output);
    }

    public static void warning(String info) {
        String output = currentDate() + " [WARNING]" + info + lineBreak;
        output(output);
    }

    public static void error(String info) {
        String output = currentDate() + " [ERROR  ]" + info + lineBreak;
        output(output);
    }

    public static String currentDate() {
        Date date = new Date();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    private static void output(String output) {
        System.out.print(output);
        try {
            logWriter.write(output);
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
