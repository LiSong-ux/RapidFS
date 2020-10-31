package net.industryhive.storage.connect;

import net.industryhive.common.connect.Sweeper;
import net.industryhive.common.logger.LogInfo;
import net.industryhive.common.logger.Logger;
import net.industryhive.common.protocol.BaseProtocol;
import net.industryhive.storage.initial.Initializer;
import net.industryhive.storage.service.FileDownload;
import net.industryhive.storage.service.FileQuery;
import net.industryhive.storage.service.FileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * 全局监听组件
 * 通过ServerSocket监听端口并获取连接
 *
 * @Author 未央
 * @Create 2020-10-05 13:50
 */
public class Connector {
    private static ServerSocket SERVER_SOCKET = null;
    private static boolean flag = true;

    static {
        try {
            SERVER_SOCKET = new ServerSocket(Integer.parseInt(Initializer.STORAGE_PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Connector() {
    }

    public static void connect() {
        if (Initializer.initialize() != 0) {
            Logger.error(LogInfo.STORAGE_START_FAILURE(Initializer.STORAGE_ID));
            return;
        }
        Logger.info(LogInfo.STORAGE_START_SUCCESS(Initializer.STORAGE_ID));

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
                    FileUpload.upload(connection);
                } else if (header[7] == BaseProtocol.DOWNLOAD_COMMAND) {
                    FileDownload.download(connection);
                } else if (header[7] == BaseProtocol.QUERY_COMMAND) {
                    FileQuery.query(connection);
                } else {
                    Logger.warning(LogInfo.INVALID_PROTOCOL);
                    Sweeper.close(connection, BaseProtocol.RESPONSE_FAILURE, LogInfo.INVALID_PROTOCOL);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void stopStorage() {
        flag = false;
    }
}
