package net.industryhive.storage.service;

import net.industryhive.storage.task.DeleteTask;

import java.net.Socket;

/**
 * @author LiSong-ux
 * @create 2021-07-22 19:53
 */
public class DeleteService {
    public static void delete(Socket connection) {
        new Thread(new DeleteTask(connection)).start();
    }
}
