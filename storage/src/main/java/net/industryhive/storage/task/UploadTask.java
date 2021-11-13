package net.industryhive.storage.task;

import net.industryhive.common.connect.Sweeper;
import net.industryhive.storage.initial.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static net.industryhive.common.protocol.BaseProtocol.RESPONSE_FAILURE;
import static net.industryhive.common.protocol.BaseProtocol.RESPONSE_SUCCESS;
import static net.industryhive.common.util.IOUtil.*;
import static net.industryhive.storage.initial.Initializer.STORAGE_ID;

/**
 * 文件上传任务执行类
 *
 * @Author 未央
 * @Create 2021-01-08 15:27
 */
public class UploadTask implements Runnable {
    private final Socket connection;
    private DataInputStream dis = null;
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
                Sweeper.sweep(connection, RESPONSE_FAILURE, "Path Not Found: " + stagePath);
                return;
            }
            if (!stageDir.isDirectory()) {
                logger.error("Path Is Not A Directory: " + stagePath);
                Sweeper.sweep(connection, RESPONSE_FAILURE, "Path Is Not A Directory: " + stagePath);
                return;
            }

            //获取客户端上传的原始文件名，截取文件扩展名
            String realName = dis.readUTF();
            String extension = "";
            if (realName.contains(".")) {
                extension = realName.substring(realName.lastIndexOf("."));
            }

            // 拼接临时文件名并向磁盘写入文件
            String tempFilepath = stagePath + "/" + Math.random();
            File file = new File(tempFilepath);

            // 文件写入操作
            if (!writeIn(dis, file)) {
                Sweeper.sweep(connection, RESPONSE_FAILURE, "Write File Failed: 520");
            }

            // 拼接最终文件名
            // 将storageId转换为16进制
            String storageId = Integer.toHexString(Integer.parseInt(STORAGE_ID));
            if (storageId.length() == 1) {
                storageId = "0" + storageId;
            }
            String encoding = storageId + file.length() + System.currentTimeMillis() + (int) (Math.random() * 90 + 10);
            byte[] enSrc = encoding.getBytes(StandardCharsets.UTF_8);
            String encoded = Base64.getEncoder().encodeToString(enSrc);
            if (encoded.endsWith("=")) {
                encoded = encoded.substring(0, encoded.indexOf("="));
            }
            String fileName = encoded + extension;

            // 修改临时文件的文件名为最终文件名
            if (!file.renameTo(new File(stagePath + "/" + fileName))) {
                logger.error("Rename Temporary File Failed: " + tempFilepath);
                if (!file.delete()) {
                    logger.error("Delete Temporary File Failed: " + tempFilepath);
                }
                Sweeper.sweep(connection, RESPONSE_FAILURE, "Upload File Failed: 510");
                return;
            }

            String uploadPath = "group" + Initializer.GROUP_ID + "/M00/" + Initializer.currentStage + "/" + fileName;
            logger.info("Upload File Success: " + uploadPath);
            Sweeper.sweep(connection, RESPONSE_SUCCESS, uploadPath);
            Initializer.reStorePointer(stagePath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(dis);
            close(connection);
        }
    }
}
