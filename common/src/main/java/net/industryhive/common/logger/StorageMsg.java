package net.industryhive.common.logger;

/**
 * @Author 未央
 * @Create 2021-02-04 17:40
 */
public class StorageMsg {
    public static final String INVALID_PROTOCOL = "Invalid Storage Protocol";
    public static final String STORED_NUMBER_MAX = "Files Stored Number Reached Maximum";
    public static final String STORED_NUMBER_NORMAL = "Number Of Files Stored Normal";

    public static String PATH_IS_NOT_DIRECTORY(String path) {
        return "Path Is Not A Directory: /" + path;
    }

    public static String DIRECTORY_NOT_FOUND(String path) {
        return "Directory Not Found: /" + path;
    }

    public static String STORAGE_START_FAILURE(String storageId) {
        return "Storage[" + storageId + "] Start Failure";
    }

    public static String STORAGE_START_SUCCESS(String storageId) {
        return "Storage[" + storageId + "] Start Success";
    }
}
