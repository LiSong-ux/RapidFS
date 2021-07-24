package net.industryhive.storage.task;

import net.industryhive.common.connect.Sweeper;
import net.industryhive.common.protocol.BaseProtocol;
import net.industryhive.storage.initial.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import static net.industryhive.common.protocol.BaseProtocol.RESPONSE_FAILURE;
import static net.industryhive.common.util.IOUtil.close;

/**
 * @author LiSong-ux
 * @create 2021-07-22 19:54
 */
public class DeleteTask implements Runnable {
    private final Socket connection;
    private DataInputStream dis = null;
    private final Logger logger = LoggerFactory.getLogger("storage");

    public DeleteTask(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            String message;
            dis = new DataInputStream(connection.getInputStream());
            String stagePath = Initializer.STORE_PATH + "/" + Initializer.currentStage;
            String filepath = dis.readUTF();
            if (filepath.equals("")) {
                message = "Error: File Path Is Null";
                logger.warn(message);
                Sweeper.sweep(connection, RESPONSE_FAILURE, message);
                return;
            }

            String realpath = new StringBuilder(filepath).replace(0, 11, Initializer.STORE_PATH).toString();
            File file = new File(realpath);
            if (!file.exists() || !file.isFile()) {
                message = "File Not Found: " + filepath;
                logger.warn(message);
                Sweeper.sweep(connection, RESPONSE_FAILURE, message);
                return;
            }
            if (!file.delete()) {
                message = "Delete File Failed: " + filepath;
            } else {
                message = "Delete File Success: " + filepath;
            }
            Sweeper.sweep(connection, BaseProtocol.RESPONSE_SUCCESS, filepath);
            Initializer.reStorePointer(stagePath);
            logger.info(message);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(dis);
        }
    }
}
