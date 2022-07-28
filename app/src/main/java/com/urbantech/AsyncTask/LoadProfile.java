package com.urbantech.AsyncTask;

import android.os.AsyncTask;

import com.urbantech.interfaces.SuccessListener;
import com.urbantech.item.ItemUser;
import com.urbantech.utils.Constant;
import com.urbantech.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadProfile extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private SuccessListener successListener;
    private String success = "0";

    public LoadProfile(SuccessListener successListener, RequestBody requestBody) {
        this.successListener = successListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        successListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);

                success = c.getString("success");
                String user_id = c.getString("user_id");
                String user_name = c.getString("name");
                String email = c.getString("email");
                String phone = c.getString("phone");
                String image = c.getString("user_profile");

                String loginType= Constant.itemUser.getLoginType();
                String authID = Constant.itemUser.getLoginType();
                Boolean isReporter = Constant.itemUser.getIsReporter();
                Constant.itemUser = new ItemUser(user_id, user_name, email, phone, image, authID, loginType, isReporter);
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        successListener.onEnd(s, success, "");
        super.onPostExecute(s);
    }
}