package com.urbantech.AsyncTask;

import android.os.AsyncTask;

import com.urbantech.interfaces.ServicesListener;
import com.urbantech.item.ItemServices;
import com.urbantech.utils.Constant;
import com.urbantech.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadServices extends AsyncTask<String, String, String> {


    private ServicesListener servicesListener;
    private RequestBody requestBody;
    private ArrayList<ItemServices> arrayList = new ArrayList<>();
    private String verifyStatus = "1", message = "";

    public LoadServices(ServicesListener servicesListener, RequestBody requestBody) {
        this.servicesListener = servicesListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        servicesListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            JSONObject mainJson = new JSONObject(JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody));
            JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                if (!obj.has(Constant.TAG_SUCCESS)) {
                    String sid=obj.getString("sid");
                    String service_name = obj.getString("service_name");
                    String service_image = obj.getString("service_image");
                    String imageThumb=obj.getString("service_image_thumb");


                    ItemServices itemServices = new ItemServices(sid,service_name,service_image, imageThumb);
                    arrayList.add(itemServices);
                } else {
                    verifyStatus = obj.getString(Constant.TAG_SUCCESS);
                    message = obj.getString(Constant.TAG_MSG);
                }
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        servicesListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}
