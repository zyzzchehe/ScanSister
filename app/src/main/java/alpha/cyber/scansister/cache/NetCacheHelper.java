package alpha.cyber.scansister.cache;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import alpha.cyber.scansister.http.ImageLoader;


public class NetCacheHelper {

    private static final String TAG="NetCacheHelper";
    
    
    private MemoryCacheHelper memoryCacheHelper;
    private DiskCacheHelper diskCacheHelper;
    private ImageView imageView;
    public NetCacheHelper(MemoryCacheHelper memoryCacheHelper,DiskCacheHelper diskCacheHelper){
        this.diskCacheHelper = diskCacheHelper;
        this.memoryCacheHelper = memoryCacheHelper;
    }

    public void displayImage(String url,ImageView imageView){
        this.imageView = imageView;
        Bitmap bitmap = memoryCacheHelper.getBitmapFromMemoryCache(url);
        if(bitmap != null){
            Log.d(TAG,"内存缓存中存在，url is "+url);
            imageView.setImageBitmap(bitmap);
        }else {
            bitmap = diskCacheHelper.getBitmapFromDiskCache(url);
            if(bitmap != null){
                Log.d(TAG,"磁盘缓存中存在，url is "+url);
                imageView.setImageBitmap(bitmap);
            }else {
                Object[] objArr = new Object[]{url};
                Log.d(TAG,"内存缓存、磁盘缓存都没有，去网络下载，url is "+url);
                new MyTask().execute(objArr);
            }
        }
    }

    class MyTask extends AsyncTask<Object,Void, Bitmap>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Bitmap doInBackground(Object... objects) {
            String url = (String) objects[0];
            Bitmap bitmap = ImageLoader.getBitmapFromNet(url);
            if(bitmap != null){
                memoryCacheHelper.addBitmapToMemoryCache(url,bitmap);
                diskCacheHelper.addBitmapToDiskCache(url,bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Log.e(TAG,"请求成功，显示妹子靓照");
            imageView.setImageBitmap(bitmap);
        }
    }
}
