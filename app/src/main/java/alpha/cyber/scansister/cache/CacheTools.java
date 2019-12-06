package alpha.cyber.scansister.cache;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class CacheTools {

    /**
     * 将图片url转换为MD5，当做缓存文件的文件名，这样才是唯一的，每一张图片对应一个缓存文件
     */
    public static String imageUrlToMD5(String url){
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }
    //byte arr to hex str
    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}