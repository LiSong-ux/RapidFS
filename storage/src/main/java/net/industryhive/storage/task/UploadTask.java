package net.industryhive.storage.task;

import net.industryhive.common.connect.Sweeper;
import net.industryhive.common.protocol.BaseProtocol;
import net.industryhive.storage.initial.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

/**
 * 文件上传任务执行类
 *
 * @Author 未央
 * @Create 2021-01-08 15:27
 */
public class UploadTask implements Runnable {
    private final Socket connection;
    private DataInputStream dis = null;
    private FileOutputStream fos = null;
    private final Logger logger = LoggerFactory.getLogger("storage");

    public UploadTask(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            dis = new DataInputStream(connection.getInputStream());
            String stagePath = Initializer.STORE_PATH + "/" + Initializer.currentStage;
            File stageDir = new File(stagePath);
            if (!stageDir.exists()) {
                logger.error("Path Not Found: " + stagePath);
                Sweeper.close(connection, BaseProtocol.RESPONSE_FAILURE, "Path Not Found: " + stagePath);
                return;
            }
            if (!stageDir.isDirectory()) {
                logger.error("Path Is Not A Directory: " + stagePath);
                Sweeper.close(connection, BaseProtocol.RESPONSE_FAILURE, "Path Is Not A Directory: " + stagePath);
                return;
            }
            String storageId = Integer.toHexString(Integer.parseInt(Initializer.STORAGE_ID));
            if (storageId.length() == 1) storageId = "0" + storageId;
            String encoding = "N" + storageId + new Date().getTime() + (int) (Math.random() * 90 + 10);
            byte[] enSrc = encoding.getBytes(StandardCharsets.UTF_8);
            String encoded = Base64.getEncoder().encodeToString(enSrc);
            String realName = dis.readUTF();
            String fileName;
            if (realName.contains(".")) {
                String extension = realName.substring(realName.lastIndexOf("."));
                fileName = encoded + extension;
            } else {
                fileName = encoded;
            }
            File file = new File(stagePath + "/" + fileName);
            fos = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = dis.read(bytes)) != -1) {
                fos.write(bytes, 0, length);
            }
            String uploadPath = "group" + Initializer.GROUP_ID + "/M00/" + Initializer.currentStage + "/" + fileName;
            logger.info("Upload File Success: " + uploadPath);
            Sweeper.close(connection, BaseProtocol.RESPONSE_SUCCESS, uploadPath);
            Initializer.reStorePointer(stagePath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (dis != null) {
                    dis.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
