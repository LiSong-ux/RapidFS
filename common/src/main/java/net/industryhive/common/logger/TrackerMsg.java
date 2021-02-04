package net.industryhive.common.logger;

/**
 * @Author 未央
 * @Create 2021-02-04 17:40
 */
public class TrackerMsg {
    public static final String INVALID_PROTOCOL = "Invalid Tracker Protocol";
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
}
