package net.industryhive.storage.service;

import net.industryhive.common.connect.Sweeper;
import net.industryhive.common.protocol.BaseProtocol;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * @Author 未央
 * @Create 2020-10-06 11:18
 */
public class QueryService {
    public static void query(Socket connection) {
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(connection.getInputStream());
            String fileName = dis.readUTF();
            File file = new File(fileName);
            if (file.exists()) {
                Sweeper.close(connection, BaseProtocol.RESPONSE_SUCCESS, file.getName());
            } else {
                Sweeper.close(connection, BaseProtocol.RESPONSE_FAILURE, "File Not Exist: " + file.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dis != null) {
                    dis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
