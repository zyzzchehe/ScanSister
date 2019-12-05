package alpha.cyber.scansister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
                        byte[] data = ImageLoader.getSisterImage(list.get(count).getUrl());
                        Message message = Message.obtain();
                        message.what = 100;
                        Bundle bundle = new Bundle();
                        bundle.putByteArray("byteArr",data);
                        message.setData(bundle);
                        handler.sendMessage(message);
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
            if(msg.what == 100){
                byte[] arr =  msg.getData().getByteArray("byteArr");
                Log.e("zyz","find sister image success,展示妹子图片");
                mImageView.setImageBitmap(BitmapFactory.decodeByteArray(arr,0,arr.length));
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
                    byte[] data = ImageLoader.getSisterImage(list.get(0).getUrl());
                    Message message = Message.obtain();
                    message.what = 100;
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("byteArr",data);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        }).start();
    }
}
