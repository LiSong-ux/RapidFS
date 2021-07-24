package net.industryhive.storage.task;

import net.industryhive.common.connect.Sweeper;
import net.industryhive.common.util.IOUtil;
import net.industryhive.storage.initial.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

import static net.industryhive.common.protocol.BaseProtocol.DOWNLOAD_START;
import static net.industryhive.common.protocol.BaseProtocol.RESPONSE_FAILURE;
import static net.industryhive.common.util.IOUtil.close;

/**
 * 文件下载任务执行类
 *
 * @Author 未央
 * @Create 2021-01-08 15:28
 */
public class DownloadTask implements Runnable {
    private final Socket connection;
    private DataInputStream dis = null;
    private FileInputStream fis = null;
    private DataOutputStream dos = null;
    private final Logger logger = LoggerFactory.getLogger("storage");

    public DownloadTask(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            dis = new DataInputStream(connection.getInputStream());
            dos = new DataOutputStream(connection.getOutputStream());
            String filePath = dis.readUTF();
            if (filePath.equals("")) {
                logger.warn("File Path Is Null");
                dos.writeUTF("Error: File Path Is Null");
                return;
            }

            String realPath = new StringBuilder(filePath).replace(0, 11, Initializer.STORE_PATH).toString();
            File file = new File(realPath);
            if (!file.exists() || !file.isFile()) {
                String message = "File Not Found: " + filePath;
                logger.warn(message);
                Sweeper.sweep(connection, RESPONSE_FAILURE, message);
                return;
            }
            logger.info("Download Start");

            dos.write(DOWNLOAD_START);
            if (!IOUtil.writeOut(file, dos)) {
                Sweeper.sweep(connection, RESPONSE_FAILURE, "Read File Failed: 530");
            }
            connection.shutdownOutput();

            logger.info("Download Over");

            logger.info("Download File Success: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(dos);
            close(fis);
            close(dis);
        }
    }
}
