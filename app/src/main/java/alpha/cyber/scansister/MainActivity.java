package alpha.cyber.scansister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.List;
import java.util.Random;

import alpha.cyber.scansister.bean.Sister;
import alpha.cyber.scansister.cache.DiskCacheHelper;
import alpha.cyber.scansister.cache.MemoryCacheHelper;
import alpha.cyber.scansister.cache.NetCacheHelper;
import alpha.cyber.scansister.http.ImageLoader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ImageView mImageView;
    private List<Sister> list;
    private int count = 1;
    private int num = 1;//默认是1
    private String imgPath;

    private MemoryCacheHelper memoryCacheHelper;
    private DiskCacheHelper diskCacheHelper;
    private NetCacheHelper netCacheHelper;
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
                String url = list.get(count).getUrl();
                netCacheHelper.displayImage(url,mImageView);
                count++;
                if(count == list.size()-1){
                    count = 0;
                }
            }
        });

        memoryCacheHelper = new MemoryCacheHelper();
        diskCacheHelper = new DiskCacheHelper(this);
        netCacheHelper = new NetCacheHelper(memoryCacheHelper,diskCacheHelper);
    }



    private void initImages(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                list =  ImageLoader.parseJsonData(ImageLoader.getJsonData(imgPath));
                if(list == null || list.size() == 0) {
                    Log.e(TAG,"init first image fail");
                }else {
                    Log.e(TAG,"init success， list size = "+list.size()+" ,display the first image");
                    String imgUrl = list.get(0).getUrl();
                    Message message = Message.obtain();
                    message.obj = imgUrl;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String imgUrl = (String) msg.obj;
            netCacheHelper.displayImage(imgUrl,mImageView);
        }
    };
}
