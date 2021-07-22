package net.industryhive.storage.task;

import net.industryhive.storage.initial.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * @author LiSong-ux
 * @create 2021-07-22 19:54
 */
public class DeleteTask implements Runnable {
    private final Socket connection;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;
    private final Logger logger = LoggerFactory.getLogger("storage");

    public DeleteTask(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            dis = new DataInputStream(connection.getInputStream());
            dos = new DataOutputStream(connection.getOutputStream());
            String filepath = dis.readUTF();
            if (filepath.equals("")) {
                logger.warn("File Path Is Null");
                dos.writeUTF("Error: File Path Is Null");
                return;
            }

            String realpath = new StringBuilder(filepath).replace(0, 11, Initializer.STORE_PATH).toString();
            File file = new File(realpath);
            if (!file.exists() || !file.isFile()) {
                String message = "File Not Found: " + filepath;
                logger.warn(message);
                dos.writeUTF(message);
                return;
            }
            String message;
            if (!file.delete()) {
                message = "Delete File Failed: " + filepath;
            } else {
                message = "Delete File Success: " + filepath;
            }
            logger.info(message);
            dos.writeUTF(message);
            connection.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dos != null) {
                    dos.close();
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
