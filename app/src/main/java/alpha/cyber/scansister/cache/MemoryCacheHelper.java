package alpha.cyber.scansister.cache;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.collection.LruCache;

public class MemoryCacheHelper {

    private static final String TAG = "MemoryCacheHelper";
    //内存缓存
    LruCache<String, Bitmap> mLruCache;

    public MemoryCacheHelper(){
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<>(cacheSize);
    }

    //想内存中保存图片数据
    public void addBitmapToMemoryCache(String imgUrl,Bitmap bitmap){
        if(getBitmapFromMemoryCache(imgUrl) == null){
            mLruCache.put(MD5Encoder.imageUrlToMD5(imgUrl),bitmap);
        }else {
            Log.d(TAG,"该图片已存在缓存中");
        }
    }
    //从内存中获取图片
    public Bitmap getBitmapFromMemoryCache(String imgUrl) {
        return mLruCache.get(MD5Encoder.imageUrlToMD5(imgUrl));
    }

    //清除内存缓存
    public void clearMemoryCache(String imgUrl){
        mLruCache.remove(MD5Encoder.imageUrlToMD5(imgUrl));
    }
}
