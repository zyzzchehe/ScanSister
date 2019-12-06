package alpha.cyber.scansister.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

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

    DiskLruCache mDiskLruCache;
    MemoryCacheHelper mMemoryCacheHelper;

    public DiskCacheHelper(Context context){
        try {
            /**
             * 打开缓存，获取缓存对象
             * 4个参数：
             * 缓存路径 appversioncode 每个key对应几个缓存文件 一般都是1个，每个缓存文件可以存储的最对字节
             */
            File mFile = getDiskCacheDir(context);
            if(!mFile.exists()){
                mFile.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(mFile,getAppVersion(context),1,10*1024*1024);
            mMemoryCacheHelper = new MemoryCacheHelper();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    /**
     * 先判断是否有外置sd卡，如果有就会保存在//sdcard/Android/data/包名/cache下边
     * 否则如果没有，那就保存在//data/data/包名下边
     * @param context 上下文
     * @return
     */
    public File getDiskCacheDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();//sdcard/Android/data/包名/cache
            Log.d("zyz","out sdcard cachePath = "+cachePath);
        } else {
            cachePath = Environment.getExternalStorageDirectory().getAbsoluteFile()+"/scan_sister";//data/data/包名
            Log.d("zyz","in sdcard cachePath = "+cachePath);
        }
        return new File(cachePath);
    }

    public int getAppVersion(Context context) {
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
    public void addBitmapToDiskCache(String url,Bitmap bitmap){
        FileInputStream fileInputStream = null;
        FileDescriptor fileDescriptor;
        try {
            String key = MD5Encoder.imageUrlToMD5(url);
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);//这里要传入缓存文件文件名
            OutputStream outputStream = editor.newOutputStream(0);
            boolean compressSucc = bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            //判断是否存储成功
            if(compressSucc){
                editor.commit();
            }else {
                editor.abort();
            }
            mDiskLruCache.flush();
            //添加到内存缓存
            mMemoryCacheHelper.addBitmapToMemoryCache(url,bitmap);

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
    public Bitmap getBitmapFromDiskCache(String imageUrl){
        try {
            String key = MD5Encoder.imageUrlToMD5(imageUrl);
            DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);
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
    public void clearDiskCache(String imageUrl){
        String key = MD5Encoder.imageUrlToMD5(imageUrl);
        try {
            mDiskLruCache.remove(key);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
