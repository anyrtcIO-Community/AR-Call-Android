package org.anyrtc.arp2p.utils;

import java.util.Random;

/**
 * Created by Skyline on 2017/11/1.
 */

public class P2PUtils {

    public static String getRandomString(int length) {
        String str = "0123456789";
        Random random = new Random();
        StringBuffer sf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(10);//0~61
            sf.append(str.charAt(number));
        }
        return sf.toString();
    }
}
