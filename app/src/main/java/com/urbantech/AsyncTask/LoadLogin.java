package com.urbantech.AsyncTask;

import android.os.AsyncTask;

import com.urbantech.interfaces.LoginListener;
import com.urbantech.utils.Constant;
import com.urbantech.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadLogin extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private LoginListener loginListener;
    private Boolean isReporter;
    private String user_id="", user_name="", success="0", message = "", dp = "", user_cat = "";

    public LoadLogin(LoginListener loginListener, RequestBody requestBody) {
        this.loginListener = loginListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        loginListener.onStart();
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

                success = c.getString(Constant.TAG_SUCCESS);
                message = c.getString(Constant.TAG_MSG);
                if(success.equals("1")) {
                    user_id = c.getString("user_id");
                    user_name = c.getString("name");
                    dp = c.getString("user_profile");
                    user_cat = c.getString("user_category");
                    isReporter = c.getBoolean("is_reporter");
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
        loginListener.onEnd(s, success, message, user_id, user_name, dp, user_cat, isReporter);
        super.onPostExecute(s);
    }
}