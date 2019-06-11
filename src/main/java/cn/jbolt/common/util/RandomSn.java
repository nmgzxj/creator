package cn.jbolt.common.util;

import java.util.Random;

public class RandomSn {
    private static final Random random = new Random();

    public static String getSn(int length) { // length表示生成字符串的长度
        String base = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    public static String getLowLetterSn(int length) { // length表示生成字符串的长度生成小写字母sn
    	String base = "abcdefghijklmnqstuwxyz";
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < length; i++) {
    		int number = random.nextInt(base.length());
    		sb.append(base.charAt(number));
    	}
    	return sb.toString();
    }
    public static String getLowSn(int length) {
    	String base = "abcdefghijklmnqstuwxyz0123456789";
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < length; i++) {
    		int number = random.nextInt(base.length());
    		sb.append(base.charAt(number));
    	}
    	return sb.toString();
    }
    /**
     * 生成制定长度数字随机串
     * @param length
     * @return
     */
    public static Integer getNumberSnToInt(int length) { // length表示生成字符串的长度生成小写字母sn
    	return Integer.parseInt(getNumberSn(length));
    }
    /**
     * 生成制定长度数字随机串
     * @param length
     * @return
     */
    public static String getNumberSn(int length) { // length表示生成字符串的长度生成小写字母sn
    	String base = "123456789";
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < length; i++) {
    		int number = random.nextInt(base.length());
    		sb.append(base.charAt(number));
    	}
    	return sb.toString();
    }

}
