package net.industryhive.common.logger;

/**
 * @Author 未央
 * @Create 2020-10-23 09:27
 */
public class LogInfo {
    public static final String INVALID_PROTOCOL = "Invalid Protocol";
    public static final String STORAGE_NOT_FOUNT = "Storage Not Found";

    public static String TRACKER_INITIALIZE_FAILURE(String trackerId) {
        return "Tracker[" + trackerId + "] Initialize Failure";
    }

    public static String TRACKER_INITIALIZE_SUCCESS(String trackerId) {
        return "Tracker[" + trackerId + "] Initialize Success";
    }

    public static String TRACKER_START_FAILURE(String trackerId) {
        return "Tracker[" + trackerId + "] Start Failure";
    }

    public static String TRACKER_START_SUCCESS(String trackerId) {
        return "Tracker[" + trackerId + "] Start Success";
    }

    public static String STORAGE_START_FAILURE(String storageId) {
        return "Storage[" + storageId + "] Start Failure";
    }

    public static String STORAGE_START_SUCCESS(String storageId) {
        return "Storage[" + storageId + "] Start Success";
    }
}
