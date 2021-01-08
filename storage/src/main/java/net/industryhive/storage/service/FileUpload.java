package net.industryhive.storage.service;

import net.industryhive.storage.task.UploadTask;

import java.net.Socket;

/**
 * 文件上传服务组件
 *
 * @Author 未央
 * @Create 2020-10-05 12:14
 */
public class FileUpload {
    public static void upload(Socket connection) {
        new Thread(new UploadTask(connection)).start();
    }
}