package net.industryhive.common.connect;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @Author 未央
 * @Create 2020-10-18 11:31
 */
public class Sweeper {
    public static void close(Socket connection, short status, String result) {
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(connection.getOutputStream());
            dos.write(status);
            dos.writeUTF(result);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
