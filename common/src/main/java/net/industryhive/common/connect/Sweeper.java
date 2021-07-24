package net.industryhive.common.connect;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @Author 未央
 * @Create 2020-10-18 11:31
 */
public class Sweeper {
    /**
     * 关闭连接并返回状态及结果信息
     *
     * @param connection Socket连接
     * @param status     执行状态
     * @param result     执行详细信息
     */
    public static void sweep(Socket connection, short status, String result) {
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
