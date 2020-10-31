package net.industryhive.tracker.connect;

import net.industryhive.common.connect.Sweeper;
import net.industryhive.common.logger.LogInfo;
import net.industryhive.common.logger.Logger;
import net.industryhive.common.protocol.BaseProtocol;
import net.industryhive.tracker.initial.Initializer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

/**
 * @Author 未央
 * @Create 2020-10-16 14:15
 */
public class Connector {
    private static ServerSocket SERVER_SOCKET = null;
    private static boolean flag = true;

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
            Logger.error(LogInfo.TRACKER_START_FAILURE(Initializer.TRACKER_ID));
            return;
        }
        Logger.info(LogInfo.TRACKER_START_SUCCESS(Initializer.TRACKER_ID));
        while (flag) {
            try {
                Socket connection = SERVER_SOCKET.accept();
                InputStream reqStream = connection.getInputStream();
                byte[] header = new byte[8];
                int length = reqStream.read(header);
                byte[] recognize = Arrays.copyOf(header, 7);
                if (length == -1 || !Arrays.equals(recognize, BaseProtocol.RECOGNIZE)) {
                    Logger.warning(LogInfo.INVALID_PROTOCOL);
                    Sweeper.close(connection, BaseProtocol.RESPONSE_FAILURE, LogInfo.INVALID_PROTOCOL);
                    continue;
                }
                if (header[7] == BaseProtocol.UPLOAD_COMMAND) {
                    Random random = new Random();
                    int groupIndex = random.nextInt(Initializer.STORAGE_MAP.size());
                    String[] storages = Initializer.STORAGE_MAP.get("group" + groupIndex);
                    int storageIndex = random.nextInt(storages.length);
                    Sweeper.close(connection, BaseProtocol.RESPONSE_SUCCESS, storages[storageIndex]);
                } else if (header[7] == BaseProtocol.DOWNLOAD_COMMAND || header[7] == BaseProtocol.QUERY_COMMAND) {
                    DataInputStream dis = new DataInputStream(reqStream);
                    String filePath = dis.readUTF();
                    String groupName = filePath.substring(0, filePath.indexOf('/'));
                    String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
                    String deSrc = fileName.substring(0, fileName.lastIndexOf('.'));
                    byte[] decode = Base64.getDecoder().decode(deSrc);
                    String realName = new String(decode, StandardCharsets.UTF_8);
                    String storageId = realName.substring(1, 3);
                    int storageIndex = Integer.parseInt(storageId, 16);
                    String[] storages = Initializer.STORAGE_MAP.get(groupName);
                    Sweeper.close(connection, BaseProtocol.RESPONSE_SUCCESS, storages[storageIndex]);
                } else {
                    Logger.warning(LogInfo.INVALID_PROTOCOL);
                    Sweeper.close(connection, BaseProtocol.RESPONSE_FAILURE, LogInfo.INVALID_PROTOCOL);
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
