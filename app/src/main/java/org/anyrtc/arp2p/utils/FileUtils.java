package org.anyrtc.arp2p.utils;

import android.os.Environment;

import org.anyrtc.arp2p.P2PApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by liuxiaozhong on 2017/11/8.
 */

public class FileUtils {

    public static String USERID_PATH = Environment.getExternalStorageDirectory() + File.separator + P2PApplication.the().getPackageName() + "/id.txt";

    public static boolean existsUserId() {
        String path = Environment.getExternalStorageDirectory() + File.separator + P2PApplication.the().getPackageName() + "/id.txt";
        File userId = new File(path);
        return userId.exists();
    }

    public static void putUserId(String userid) throws IOException {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory() + File.separator + P2PApplication.the().getPackageName();//获取SDCard目录
            File user = new File(path);
            user.mkdir();
            user = new File(user, "id.txt");
            FileOutputStream out = new FileOutputStream(user);
            Writer writer = new OutputStreamWriter(out);
            try {
                writer.write(userid);
                SharePrefUtil.putString("userid",userid);
            } finally {
                writer.close();
            }
        }
    }


    public static String getUserId() throws IOException {
        String userid="";
            BufferedReader reader = null;
            StringBuilder data = new StringBuilder();
            try {
                File userId = new File(USERID_PATH);
                FileInputStream in = new FileInputStream(userId);
                reader = new BufferedReader(new InputStreamReader(in));
                String line = new String();
                while ((line = reader.readLine()) != null) {
                    data.append(line);
                }
                userid=data.toString();
            } catch (FileNotFoundException e) {
                userid= "";
            } finally {
                reader.close();
            }
        return userid;
    }
}
