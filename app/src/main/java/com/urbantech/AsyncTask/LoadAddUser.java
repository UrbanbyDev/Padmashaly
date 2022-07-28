package com.urbantech.AsyncTask;


import android.os.AsyncTask;

import com.urbantech.interfaces.SocialLoginListener;
import com.urbantech.interfaces.UserListener;
import com.urbantech.utils.Constant;
import com.urbantech.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadAddUser extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private UserListener userListener;
    private Boolean isReporter = false;
    private String success = "0", message = "", user_id = "", user_name = "", email = "", auth_id = "", user_cat = "", user_profile = "";

    public LoadAddUser(UserListener userListener, RequestBody requestBody) {
        this.userListener = userListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        userListener.onStart();
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
                if(c.has("user_id")) {
                    user_id = c.getString("user_id");
                    user_name = c.getString("name");
                    if(c.has("user_profile")) {
                        user_profile = c.getString("user_profile");
                    }
                    user_cat = c.getString("user_category");
                    auth_id = c.getString("auth_id");
                    email = c.getString("email");
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
        userListener.onEnd(s, success, message, user_id, user_name, email, user_profile, user_cat, auth_id, isReporter);
        super.onPostExecute(s);
    }
}
