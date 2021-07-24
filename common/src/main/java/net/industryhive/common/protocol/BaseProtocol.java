package net.industryhive.common.protocol;

/**
 * @Author 未央
 * @Create 2020-10-05 16:00
 */
public class BaseProtocol {
    public static final byte[] RECOGNIZE_TRACKER = {82, 97, 112, 105, 100, 70, 83, 0};
    public static final byte[] RECOGNIZE_STORAGE = {82, 97, 112, 105, 100, 70, 83, 1};
    public static final byte UPLOAD_COMMAND = 0;
    public static final byte DOWNLOAD_COMMAND = 1;
    public static final byte QUERY_COMMAND = 2;
    public static final byte DELETE_COMMAND = 3;
    public static final short RESPONSE_SUCCESS = 200;
    public static final short RESPONSE_FAILURE = 400;
    public static final short DOWNLOAD_START = 210;
}
