package net.industryhive.storage.initial;

import net.industryhive.common.config.PropertiesLoader;
import net.industryhive.common.system.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;

/**
 * @Author 未央
 * @Create 2020-10-06 21:38
 */
public class Initializer {
    static {
        Constant.setCurrentSys("STORAGE");
    }

    public static int stageIndex = 0;
    public static String currentStage = "00/00";
    public static String nextStage = "00/01";
    public static final String[] STAGE_NAMES = listStage();
    public static final String GROUP_ID =
            PropertiesLoader.getValue("group.id");
    public static final String STORAGE_ID =
            PropertiesLoader.getValue("storage.id");
    public static final String STORAGE_PORT =
            PropertiesLoader.getValue("storage.port");
    public static final String STORE_PATH =
            PropertiesLoader.getValue("store.path");
    private static final Logger logger = LoggerFactory.getLogger("storage");

    public static int initialize() {
        if (GROUP_ID == null) {
            logger.error("group.id Is Null");
            return 3;
        }
        if (STORAGE_ID == null) {
            logger.error("storage.id Is Null");
            return 1;
        }
        if (STORE_PATH == null) {
            logger.error("store.path Is Null");
            return 2;
        }
        File storePath = new File(STORE_PATH);
        if (!storePath.exists()) {
            logger.info("Storage[" + STORAGE_ID + "] Initialize Start");
            boolean mdSP = storePath.mkdirs();
            if (!mdSP) {
                logger.error("Make store_path Error: " + STORE_PATH);
                return 3;
            }
        } else {
            if (!storePath.isDirectory()) {
                logger.error("store_path Is Not A Directory: " + STORE_PATH);
                return 4;
            }
        }
        File stage = new File(STORE_PATH + "/AF/09");
        if (!stage.exists()) {
            for (String stageName : STAGE_NAMES) {
                String stagePath = STORE_PATH + "/" + stageName;
                File stageDir = new File(stagePath);
                if (stageDir.exists()) continue;
                boolean mdSD = stageDir.mkdirs();
                if (!mdSD) {
                    logger.error("Storage[" + STORAGE_ID + "] Initialize Error");
                    return 5;
                }
            }
            logger.info("Storage[" + STORAGE_ID + "] Initialize Success");
        }
        int reStoreResult = storePointer(STORE_PATH);
        if (reStoreResult == 0) {
            logger.info("Number Of Files Stored Normal");
        }
        return 0;
    }

    public static int storePointer(String store_path) {
        String[] stageNames = listStage();
        int index = 0;
        for (String stageName : STAGE_NAMES) {
            File stage = new File(store_path + "/" + stageName);
            if (!stage.exists()) {
                logger.error("Directory Not Found: /" + stageName);
                return 2;
            }
            if (!stage.isDirectory()) {
                logger.error("Path Is Not A Directory: /" + stageName);
                return 3;
            }
            String[] files = stage.list();
            if (files != null && files.length < 100) {
                stageIndex = index;
                currentStage = stageName;
                nextStage = stageNames[index + 1];
                return 0;
            }
            index++;
        }
        logger.warn("Files Stored Number Reached Maximum");
        return 1;
    }

    public static synchronized void reStorePointer(String stagePath) {
        String currentPath = Initializer.STORE_PATH + "/" + currentStage;
        if (!currentPath.equals(stagePath)) {
            return;
        }
        File stageDir = new File(stagePath);
        String[] filesNum = stageDir.list();
        if (filesNum != null && filesNum.length > 99) {
            stageIndex++;
            currentStage = STAGE_NAMES[stageIndex];
            nextStage = STAGE_NAMES[stageIndex + 1];
        }
    }

    private static String[] listStage() {
        ArrayList<String> stageNameList = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            String first = Integer.toHexString(i).toUpperCase();
            if (first.length() == 1) first = "0" + first;
            for (int j = 0; j < 256; j++) {
                String second = Integer.toHexString(j).toUpperCase();
                if (second.length() == 1) second = "0" + second;
                String stageName = first + "/" + second;
                stageNameList.add(stageName);
            }
        }
        return stageNameList.toArray(new String[65536]);
    }
}
