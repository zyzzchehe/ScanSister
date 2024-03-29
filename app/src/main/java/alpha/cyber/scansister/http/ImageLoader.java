package alpha.cyber.scansister.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import alpha.cyber.scansister.bean.Sister;

public class ImageLoader {
    //将从网络获取的图片数据写入到缓存中

    /**
     *
     * @param outputStream 缓存文件对应的要写入的流
     * @param fetchUrl
     * @return
     */
    public static Bitmap getBitmapFromNet(String fetchUrl) {
        try {
            URL url = new URL(fetchUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream in = conn.getInputStream();
                return BitmapFactory.decodeStream(in);
            } else {
                Log.e("zyz","请求失败：" + code);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getJsonData(String path) {
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            URL url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");//请求方式
            conn.setConnectTimeout(5000);//超时时间
            if(conn.getResponseCode() != 200){
                throw new IOException("请求失败");
            }
            inputStream = conn.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine() )!= null){
                stringBuffer.append(line);
            }
            return stringBuffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if (conn != null)   conn.disconnect();
                if(inputStream != null)   inputStream.close();
                if(inputStreamReader != null) inputStreamReader.close();
                if(bufferedReader != null) bufferedReader.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }


    public static List<Sister> parseJsonData(String json){
        try {
            List<Sister> imgList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                String url = obj.getString("url");
//                Log.e("zyz","image path is = "+url);
                Sister sister = new Sister();
                sister.setUrl(url);
                imgList.add(sister);
            }
            return imgList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
