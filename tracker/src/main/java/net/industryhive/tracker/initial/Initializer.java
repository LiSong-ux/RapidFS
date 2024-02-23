package net.industryhive.tracker.initial;

import net.industryhive.common.config.PropertiesLoader;
import net.industryhive.common.logger.TrackerMsg;
import net.industryhive.common.system.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @Author 未央
 * @Create 2020-10-18 11:50
 */
public class Initializer {
    private static final Logger logger = LoggerFactory.getLogger("tracker");

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
        if (STORAGE_MAP.isEmpty()) {
            logger.error(TrackerMsg.STORAGE_NOT_FOUNT);
            logger.error(TrackerMsg.TRACKER_INITIALIZE_FAILURE(TRACKER_ID));
            return 1;
        }
        logger.info(TrackerMsg.TRACKER_INITIALIZE_SUCCESS(TRACKER_ID));
        return 0;
    }
}
