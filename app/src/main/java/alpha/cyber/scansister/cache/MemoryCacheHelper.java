package alpha.cyber.scansister.cache;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.collection.LruCache;

public class MemoryCacheHelper {

    //内存缓存
    static LruCache<String, Bitmap> lruCache;

    public static LruCache<String,Bitmap> initMemoryCache(){
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        lruCache = new LruCache<>(cacheSize);
        return lruCache;
    }

    //想内存中保存图片数据
    public static void addBitmapToMemoryCache(String imgUrl,Bitmap bitmap){
        if(getBitmapFromMemoryCache(imgUrl) == null){
            lruCache.put(CacheTools.imageUrlToMD5(imgUrl),bitmap);
        }else {
            Log.d("zyz","该图片已存在缓存中");
        }
    }
    //从内存中获取图片
    public static Bitmap getBitmapFromMemoryCache(String imgUrl) {
        return lruCache.get(CacheTools.imageUrlToMD5(imgUrl));
    }

    //清除内存缓存
    public static void clearMemoryCache(String imgUrl){
        lruCache.remove(CacheTools.imageUrlToMD5(imgUrl));
    }
}
