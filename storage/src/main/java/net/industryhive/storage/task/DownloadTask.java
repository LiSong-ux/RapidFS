package net.industryhive.storage.task;

import net.industryhive.storage.initial.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

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
                logger.warn("File Not Found: " + filePath);
                dos.writeUTF("File Not Found: " + filePath);
                return;
            }
            logger.info("Download Start");
            dos.writeUTF("Download Start");
            fis = new FileInputStream(file);
            byte[] fileBytes = new byte[1024];
            while (fis.read(fileBytes) != -1) {
                dos.write(fileBytes);
            }
            connection.shutdownOutput();
            logger.info("Download File Success: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dos != null) {
                    dos.close();
                }
                if (fis != null) {
                    fis.close();
                }
                if (dis != null) {
                    dis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
