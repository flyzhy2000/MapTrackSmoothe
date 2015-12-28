package com.location.demo.map_location_smoothe_demo.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Huoyunren on 2015/12/28.
 */
public class FileUtils {

    public static void saveFile(String str, String fileName) {
        String filePath = null;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + fileName +".txt";
        } else
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + fileName +".txt";

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file, true);
            outStream.write(str.getBytes());
            String enter = "\r\n";
            outStream.write(enter.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
