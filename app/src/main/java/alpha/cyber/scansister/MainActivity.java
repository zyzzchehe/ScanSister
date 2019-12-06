package alpha.cyber.scansister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LruCache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import alpha.cyber.scansister.bean.Sister;
import alpha.cyber.scansister.cache.CacheTools;
import alpha.cyber.scansister.cache.DiskCacheHelper;
import alpha.cyber.scansister.cache.DiskLruCache;
import alpha.cyber.scansister.cache.MemoryCacheHelper;
import alpha.cyber.scansister.http.ImageLoader;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private List<Sister> list;
    private int count = 1;
    private int num = 1;//默认是1
    private String imgPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgPath = "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/"+num;
        initImages();

        mImageView = findViewById(R.id.image);
        findViewById(R.id.bt_noscan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                num = random.nextInt(9);
                imgPath = "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/"+num;
                initImages();
            }
        });

        findViewById(R.id.bt_next_sister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url = list.get(count).getUrl();
                        handImageUrl(url);
                        count++;
                        if(count == list.size()-1){
                            count = 0;
                        }
                    }
                }).start();
            }
        });
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String url =  msg.getData().getString("imgUrl");
            if(msg.what == 100){
                Log.e("zyz","find sister image success,展示妹子图片");
                mImageView.setImageBitmap(MemoryCacheHelper.getBitmapFromMemoryCache(url));
            }else if (msg.what == 101){
                mImageView.setImageBitmap(DiskCacheHelper.getBitmapFromDiskCache(url));
            }else {
                Log.d("zyz","do nothing");
            }
        }
    };


    private void initImages(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                list =  ImageLoader.parseJsonData(ImageLoader.getJsonData(imgPath));
                if(list == null || list.size() == 0) {
                    Log.e("zyz","请求失败");
                }else {
                    Log.e("zyz","请求成功， list size = "+list.size()+" ,展示妹子图片！！！");
                    String imgUrl = list.get(0).getUrl();
                    handImageUrl(imgUrl);
                }
            }
        }).start();
    }

    private void handImageUrl(String imgUrl) {
        //先判断内存缓存中是否存在该图片
        if(MemoryCacheHelper.initMemoryCache() != null && MemoryCacheHelper.getBitmapFromMemoryCache(imgUrl) != null){
            //去显示图片
            Log.d("zyz","memory exist");
            Message message = Message.obtain();
            message.what = 100;
            Bundle bundle = new Bundle();
            bundle.putString("imgUrl",imgUrl);
            message.setData(bundle);
            handler.sendMessage(message);
            return;
        }else {
            //再判断磁盘缓存中是否存在该图片
            if(DiskCacheHelper.initDiskCache(MainActivity.this) != null && DiskCacheHelper.getBitmapFromDiskCache(imgUrl) != null){
                //去显示图片
                Log.d("zyz","disk exist");
                Message message = Message.obtain();
                message.what = 101;
                Bundle bundle = new Bundle();
                bundle.putString("imgUrl",imgUrl);
                message.setData(bundle);
                handler.sendMessage(message);
                return;
            }else {
                //将图片加到内存中和磁盘中
                DiskCacheHelper.addBitmapToDiskCache(MainActivity.this,imgUrl);
                handImageUrl(imgUrl);
            }
        }
    }
}
