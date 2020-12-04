package net.industryhive.storage.service;

import net.industryhive.common.connect.Sweeper;
import net.industryhive.common.logger.Logger;
import net.industryhive.common.protocol.BaseProtocol;
import net.industryhive.storage.initial.Initializer;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

/**
 * 文件上传服务组件
 *
 * @Author 未央
 * @Create 2020-10-05 12:14
 */
public class FileUpload {
    public static void upload(Socket connection) {
        DataInputStream dis = null;
        FileOutputStream fos = null;
        try {
            dis = new DataInputStream(connection.getInputStream());
            String stage_path = Initializer.STORE_PATH + "/" + Initializer.currentStage;
            File stagePath = new File(stage_path);
            if (!stagePath.exists()) {
                Logger.error("Path Not Found: " + stage_path);
                Sweeper.close(connection, BaseProtocol.RESPONSE_FAILURE, "Path Not Found: " + stage_path);
                return;
            }
            if (!stagePath.isDirectory()) {
                Logger.error("Path Is Not A Directory: " + stage_path);
                Sweeper.close(connection, BaseProtocol.RESPONSE_FAILURE, "Path Is Not A Directory: " + stage_path);
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
            File file = new File(stage_path + "/" + fileName);
            fos = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = dis.read(bytes)) != -1) {
                fos.write(bytes, 0, length);
            }
            String uploadPath = "group" + Initializer.GROUP_ID + "/M00/" + Initializer.currentStage + "/" + fileName;
            Logger.info("Upload File Success: " + uploadPath);
            Sweeper.close(connection, BaseProtocol.RESPONSE_SUCCESS, uploadPath);
            String[] filesNum = stagePath.list();
            if (filesNum != null && filesNum.length > 99) {
                Initializer.stageIndex++;
                Initializer.currentStage = Initializer.STAGE_NAMES[Initializer.stageIndex];
                Initializer.nextStage = Initializer.STAGE_NAMES[Initializer.stageIndex + 1];
            }
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