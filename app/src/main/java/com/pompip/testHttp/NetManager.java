package com.pompip.testHttp;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;

public class NetManager {
    private static final String TAG = "NetManager";
    private static String U = "bonreeio";
    private static String P = "67640df2f2a3a18f4346c19d23dfc2d5";
    private static String host = "http://47.95.255.151:10600";//http://dapi.hubpd.com //http://47.95.255.151:10600
    private static final String WEIXING_URL = "/bonree/weixin/bizhome";
    private static final String UPLOAD_URL = "/bonree/weixin/bizpacket";

    //    private static  String WEIXING_URL = "http://47.95.255.151:10600/bonree/weixin/bizhome";
//    private static  String UPLOAD_URL = "http://47.95.255.151:10600/bonree/weixin/bizpacket";
    public static String getWeixinURL(String param) {
        try {
            Log.d(TAG, "getWeixinURL: param:  " + param);
            if (param != null) {

                String h = param.substring(param.indexOf("http:"));
                Log.d(TAG, "getWeixinURL: host:  " + h);
                if (!TextUtils.isEmpty(h)) {
                    host = h.trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            JSONObject object = new JSONObject();
            object.put("u", U);
            object.put("p", P);


            HttpResult httpResponse = HttpsClientUtil.uploadData(object.toString().getBytes(),host + WEIXING_URL);
            Log.i(TAG, "getWeixinURL: response = " + httpResponse);
            if (httpResponse != null && httpResponse.responseEntity != null) {
                String result = new String(httpResponse.responseEntity);
                Log.d(TAG, "getWeixinURL: result  " + result);
                JSONObject resultJson = new JSONObject(result);
                int sys_status = resultJson.getInt("sys_status");
                if (sys_status == 0) {
                    return resultJson.getString("data");
                } else {
                    Log.e(TAG, "getWeixinURL: error" + resultJson.getString("errmsg"));
                    return "";
                }
            } else {
                return "";
            }
            //{"data":"https://mp.weixin.qq.com/mp/profile_ext?action=home&__biz=MjM5NTQ4MTQyMg==","errmsg":"","sys_status":0}

        } catch (Exception e) {
            Log.e(TAG, "getWeixinURL: ",e );
            return "";
        }
    }

//    public static void dataUpload(PocketDataBuilder pocketDataBuilder) {
//        try {
////            if (pocketDataBuilder.reqUrl.contains("/mp/profile_ext?action=home&__biz=")) {
//            JSONObject object = new JSONObject();
//            object.put("u", U);
//            object.put("p", P);
//            object.put("packet", pocketDataBuilder.buildJson());
//            String obj = object.toString();
//            Log.d(TAG, "dataUpload param size: " + obj.getBytes().length);
////            writeFile(obj);
//            HttpResult httpResponse = HttpsClientUtil.uploadData(obj.getBytes(),host + UPLOAD_URL);
//            Log.i(TAG, "dataUpload: httpResponse = " + httpResponse);
//            String result = null;
//            if (httpResponse != null && httpResponse.responseEntity != null) {
//                result = new String(httpResponse.responseEntity);
//            }
//            Log.d(TAG, "dataUpload: result:" + result);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    private static void writeFile(String obj) throws Exception {

        File fileDir = new File(Environment.getExternalStorageDirectory(), "weixin");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File file = new File(fileDir, "myData_" + System.currentTimeMillis() + ".json");
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter writer = new FileWriter(file);
        writer.write(obj);
        writer.flush();
        writer.close();
    }
}
