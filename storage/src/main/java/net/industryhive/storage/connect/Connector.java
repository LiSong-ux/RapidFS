package net.industryhive.storage.connect;

import net.industryhive.common.connect.Sweeper;
import net.industryhive.common.logger.StorageMsg;
import net.industryhive.storage.initial.Initializer;
import net.industryhive.storage.service.DownloadService;
import net.industryhive.storage.service.QueryService;
import net.industryhive.storage.service.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import static net.industryhive.common.protocol.BaseProtocol.*;

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
    private static final Logger logger = LoggerFactory.getLogger("storage");

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
            logger.error(StorageMsg.STORAGE_START_FAILURE(Initializer.STORAGE_ID));
            return;
        }
        logger.info(StorageMsg.STORAGE_START_SUCCESS(Initializer.STORAGE_ID));

        while (flag) {
            try {
                Socket connection = SERVER_SOCKET.accept();
                InputStream reqStream = connection.getInputStream();
                byte[] header = new byte[9];
                int length = reqStream.read(header);
                byte[] recognize = Arrays.copyOf(header, 8);
                if (length == -1 || !Arrays.equals(recognize, RECOGNIZE_STORAGE)) {
                    logger.warn(StorageMsg.INVALID_PROTOCOL);
                    Sweeper.close(connection, RESPONSE_FAILURE, StorageMsg.INVALID_PROTOCOL);
                    continue;
                }
                switch (header[8]) {
                    case UPLOAD_COMMAND:
                        UploadService.upload(connection);
                        break;
                    case DOWNLOAD_COMMAND:
                        DownloadService.download(connection);
                        break;
                    case QUERY_COMMAND:
                        QueryService.query(connection);
                        break;
                    default:
                        logger.warn(StorageMsg.INVALID_PROTOCOL);
                        Sweeper.close(connection, RESPONSE_FAILURE, StorageMsg.INVALID_PROTOCOL);
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
