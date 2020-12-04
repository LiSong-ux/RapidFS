package net.industryhive.storage.service;

import net.industryhive.common.logger.Logger;
import net.industryhive.storage.initial.Initializer;

import java.io.*;
import java.net.Socket;

/**
 * 文件下载服务组件
 *
 * @Author 未央
 * @Create 2020-10-05 16:33
 */
public class FileDownload {
    public static void download(Socket connection) {
        DataInputStream dis = null;
        FileInputStream fis = null;
        DataOutputStream dos = null;
        try {
            dis = new DataInputStream(connection.getInputStream());
            dos = new DataOutputStream(connection.getOutputStream());
            String filePath = dis.readUTF();
            if (filePath.equals("")) {
                Logger.warning("File Path Is Null");
                dos.writeUTF("Error: File Path Is Null");
                return;
            }

            String realPath = new StringBuilder(filePath).replace(0, 11, Initializer.STORE_PATH).toString();
            File file = new File(realPath);
            if (!file.exists() || !file.isFile()) {
                Logger.warning("File Not Found: " + filePath);
                dos.writeUTF("File Not Found: " + filePath);
                return;
            }
            Logger.info("Download Start");
            dos.writeUTF("Download Start");
            fis = new FileInputStream(file);
            byte[] fileBytes = new byte[1024];
            while (fis.read(fileBytes) != -1) {
                dos.write(fileBytes);
            }
            connection.shutdownOutput();
            Logger.info("Download File Success: " + filePath);
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
