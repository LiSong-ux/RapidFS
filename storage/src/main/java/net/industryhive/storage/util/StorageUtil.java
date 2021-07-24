package net.industryhive.storage.util;

import net.industryhive.storage.initial.Initializer;

/**
 * @author 未央
 * @create 2021-07-24 22-47
 */
public class StorageUtil {
    public static String getRealpath(String filepath) {
        return filepath.replaceFirst("group\\d+/M[0-9]{2}", Initializer.STORE_PATH);
    }
}
