package com.urbantech.AsyncTask;

import android.os.AsyncTask;

import com.urbantech.interfaces.FeedbackListener;
import com.urbantech.interfaces.SurveyListener;
import com.urbantech.utils.Constant;
import com.urbantech.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadFeedback extends AsyncTask<String,String,String> {
    private RequestBody requestBody;
    private FeedbackListener feedbackListener;
    private String success = "0", message = "", user_id="",feedback = "";

    public LoadFeedback(FeedbackListener feedbackListener, RequestBody requestBody) {
        this.feedbackListener = feedbackListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        feedbackListener.onStart();
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

            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        feedbackListener.onEnd(s, success, message, user_id,feedback);
        super.onPostExecute(s);
    }
}
