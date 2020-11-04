package net.industryhive.common.system;

/**
 * @Author 未央
 * @Create 2020-10-07 11:26
 */
public class Constant {
    public static final String OS_NAME = System.getProperty("os.name");
    private static String CURRENT_SYS;
    public static String CURRENT_CONF;
    public static String CURRENT_LOG;

    public static String getCurrentSys() {
        return CURRENT_SYS;
    }

    public static void setCurrentSys(String currentSys) {
        CURRENT_SYS = currentSys;
        if (currentSys.equals("TRACKER")) {
            CURRENT_CONF = "../conf/tracker.properties";
            CURRENT_LOG = "../logs/tracker.log";
        } else {
            CURRENT_CONF = "../conf/storage.properties";
            CURRENT_LOG = "../logs/storage.log";
        }
    }
}
