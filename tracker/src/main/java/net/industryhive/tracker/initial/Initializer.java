package net.industryhive.tracker.initial;

import net.industryhive.common.config.PropertiesLoader;
import net.industryhive.common.logger.LogInfo;
import net.industryhive.common.logger.Logger;
import net.industryhive.common.system.Constant;

import java.util.HashMap;

/**
 * @Author 未央
 * @Create 2020-10-18 11:50
 */
public class Initializer {
    static {
        Constant.setCurrentSys("TRACKER");
    }

    public static final String TRACKER_ID =
            PropertiesLoader.getValue("tracker.id");
    public static final String TRACKER_PORT =
            PropertiesLoader.getValue("tracker.port");
    public static final HashMap<String, String[]> STORAGE_MAP = new HashMap<>();

    public static int initialize() {
        String groupName;
        String[] storages;
        for (int i = 0; ; i++) {
            groupName = "group" + i;
            storages = PropertiesLoader.getValueArray(groupName);
            if (storages == null || storages.length == 0) {
                break;
            }
            STORAGE_MAP.put(groupName, storages);
        }
        if (STORAGE_MAP.size() < 1) {
            Logger.error(LogInfo.STORAGE_NOT_FOUNT);
            Logger.error(LogInfo.TRACKER_INITIALIZE_FAILURE(TRACKER_ID));
            return 1;
        }
        Logger.info(LogInfo.TRACKER_INITIALIZE_SUCCESS(TRACKER_ID));
        return 0;
    }
}
