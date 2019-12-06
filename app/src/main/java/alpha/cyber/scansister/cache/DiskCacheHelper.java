package alpha.cyber.scansister.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import alpha.cyber.scansister.http.ImageLoader;

public class DiskCacheHelper {
    /**
     * 磁盘缓存：DiskLruCache
     * 打开缓存
     * 写入缓存
     * 读取缓存
     * 清除缓存
     */

    static DiskLruCache diskLruCache;

    /**
     * 打开缓存，获取缓存对象
     * 4个参数：
     * 缓存路径 appversioncode 每个key对应几个缓存文件 一般都是1个，每个缓存文件可以存储的最对字节
     */
    public static DiskLruCache initDiskCache(Context context) {
        try {
            diskLruCache = DiskLruCache.open(getDiskCacheDir(context,"scan_sister"),getAppVersion(context),1,10*1024*1024);
            return diskLruCache;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();//sdcard/Android/data/包名/cache
        } else {
            cachePath = context.getCacheDir().getPath();//data/data/包名
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }


    /**
     * 写入缓存
     */
    public static void addBitmapToDiskCache(Context context,String imageUrl){
        FileInputStream fileInputStream = null;
        FileDescriptor fileDescriptor = null;
        try {
            String key = CacheTools.imageUrlToMD5(imageUrl);
            DiskLruCache.Editor editor = initDiskCache(context).edit(key);//这里要传入缓存文件文件名
            OutputStream outputStream = editor.newOutputStream(0);
            //判断是否存储成功
            if(ImageLoader.getSisterImageDataAndSaveToDiskCache(outputStream,imageUrl)){
                editor.commit();
            }else {
                editor.abort();
            }
            diskLruCache.flush();
            fileInputStream = (FileInputStream) diskLruCache.get(key).getInputStream(0);
            fileDescriptor = fileInputStream.getFD();
            //添加到内存缓存
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            MemoryCacheHelper.addBitmapToMemoryCache(imageUrl,bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(fileInputStream != null)  fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取缓存
     */
    public static Bitmap getBitmapFromDiskCache(String imageUrl){
        try {
            String key = CacheTools.imageUrlToMD5(imageUrl);
            DiskLruCache.Snapshot snapShot = diskLruCache.get(key);
            if (snapShot != null) {
                InputStream is = snapShot.getInputStream(0);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 清除缓存
     */
    public static void clearDiskCache(String imageUrl){
        String key = CacheTools.imageUrlToMD5(imageUrl);
        try {
            diskLruCache.remove(key);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
