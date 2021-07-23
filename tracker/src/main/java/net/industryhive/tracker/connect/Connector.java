package net.industryhive.tracker.connect;

import net.industryhive.common.connect.Sweeper;
import net.industryhive.common.logger.TrackerMsg;
import net.industryhive.common.protocol.BaseProtocol;
import net.industryhive.tracker.initial.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Pattern;

import static net.industryhive.common.protocol.BaseProtocol.*;

/**
 * @Author 未央
 * @Create 2020-10-16 14:15
 */
public class Connector {
    private static ServerSocket SERVER_SOCKET = null;
    private static boolean flag = true;
    private static final Logger logger = LoggerFactory.getLogger("tracker");

    static {
        try {
            SERVER_SOCKET = new ServerSocket(Integer.parseInt(Initializer.TRACKER_PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Connector() {
    }

    public static void connect() {
        if (Initializer.initialize() != 0) {
            logger.error(TrackerMsg.TRACKER_START_FAILURE(Initializer.TRACKER_ID));
            return;
        }
        logger.info(TrackerMsg.TRACKER_START_SUCCESS(Initializer.TRACKER_ID));

        // group0/M00/00/00/TjAwMTYyNzAwNjA1MDMzNDEz.jpg
        Pattern regCompile = Pattern.compile("^group\\d+/M\\d{2}/[0-9A-F]{2}/[0-9A-F]{2}/\\w+(\\.\\w+)?$");

        while (flag) {
            try {
                Socket connection = SERVER_SOCKET.accept();
                InputStream reqStream = connection.getInputStream();
                byte[] header = new byte[9];
                int length = reqStream.read(header);
                byte[] recognize = Arrays.copyOf(header, 8);
                if (length == -1 || !Arrays.equals(recognize, BaseProtocol.RECOGNIZE_TRACKER)) {
                    logger.warn(TrackerMsg.INVALID_PROTOCOL);
                    Sweeper.close(connection, BaseProtocol.RESPONSE_FAILURE, TrackerMsg.INVALID_PROTOCOL);
                    continue;
                }
                int storageIndex;
                String[] storages;
                switch (header[8]) {
                    case UPLOAD_COMMAND:
                        Random random = new Random();
                        int groupIndex = random.nextInt(Initializer.STORAGE_MAP.size());
                        storages = Initializer.STORAGE_MAP.get("group" + groupIndex);
                        storageIndex = random.nextInt(storages.length);
                        Sweeper.close(connection, BaseProtocol.RESPONSE_SUCCESS, storages[storageIndex]);
                        break;
                    case QUERY_COMMAND:
                    case DELETE_COMMAND:
                    case DOWNLOAD_COMMAND:
                        DataInputStream dis = new DataInputStream(reqStream);
                        String filepath = dis.readUTF();
                        if (!regCompile.matcher(filepath).matches()) {
                            Sweeper.close(connection, BaseProtocol.RESPONSE_FAILURE, TrackerMsg.INVALID_FILEPATH);
                            break;
                        }
                        String groupName = filepath.substring(0, filepath.indexOf('/'));
                        String filename = filepath.substring(filepath.lastIndexOf('/') + 1);
                        String deSrc = filename.substring(0, filename.lastIndexOf('.'));
                        byte[] decode = Base64.getDecoder().decode(deSrc);
                        String realName = new String(decode, StandardCharsets.UTF_8);
                        String storageId = realName.substring(1, 3);
                        storageIndex = Integer.parseInt(storageId, 16);
                        storages = Initializer.STORAGE_MAP.get(groupName);
                        Sweeper.close(connection, BaseProtocol.RESPONSE_SUCCESS, storages[storageIndex]);
                        break;
                    default:
                        logger.warn(TrackerMsg.INVALID_PROTOCOL);
                        Sweeper.close(connection, BaseProtocol.RESPONSE_FAILURE, TrackerMsg.INVALID_PROTOCOL);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void stopTracker() {
        flag = false;
    }
}
