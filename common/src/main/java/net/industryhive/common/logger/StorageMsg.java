package net.industryhive.common.logger;

/**
 * @Author 未央
 * @Create 2021-02-04 17:40
 */
public class StorageMsg {
    public static final String INVALID_PROTOCOL = "Invalid Storage Protocol";

    public static String STORAGE_START_FAILURE(String storageId) {
        return "Storage[" + storageId + "] Start Failure";
    }

    public static String STORAGE_START_SUCCESS(String storageId) {
        return "Storage[" + storageId + "] Start Success";
    }
}
