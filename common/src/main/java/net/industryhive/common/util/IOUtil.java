package net.industryhive.common.util;

import java.io.*;

/**
 * @author LiSong-ux
 * @create 2021-07-24 15:48
 */
public class IOUtil {
    public static <T extends Closeable> void close(T t) {
        if (t != null) {
            try {
                t.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean writeIn(InputStream source, File target) {
        try (FileOutputStream fos = new FileOutputStream(target)) {
            byte[] bytes = new byte[1024];
            int length;
            while ((length = source.read(bytes)) != -1) {
                fos.write(bytes, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean writeOut(File source, OutputStream target) {
        try (FileInputStream fis = new FileInputStream(source)) {
            byte[] fileBytes = new byte[1024];
            while (fis.read(fileBytes) != -1) {
                target.write(fileBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
