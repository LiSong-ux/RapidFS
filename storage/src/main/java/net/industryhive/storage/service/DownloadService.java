package net.industryhive.storage.service;

import net.industryhive.storage.task.DownloadTask;

import java.net.Socket;

/**
 * 文件下载服务组件
 *
 * @Author 未央
 * @Create 2020-10-05 16:33
 */
public class DownloadService {
    public static void download(Socket connection) {
        new Thread(new DownloadTask(connection)).start();
    }
}
