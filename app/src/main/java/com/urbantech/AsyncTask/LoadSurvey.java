package com.urbantech.AsyncTask;


import android.os.AsyncTask;

import com.urbantech.interfaces.SurveyListener;
import com.urbantech.utils.Constant;
import com.urbantech.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadSurvey extends AsyncTask<String,String,String> {
    private RequestBody requestBody;
    private SurveyListener surveyListener;
    private String success = "0", message = "", user_id="",survey_name = "", survey_surname = "", survey_fathername = "", survey_dateofbirth = "", survey_gender = "", survey_housenumber = "",survey_wardnumber="",survey_city="",survey_mandal="",survey_district="",survey_qualification="",survey_occupation="";

    public LoadSurvey(SurveyListener surveyListener, RequestBody requestBody) {
        this.surveyListener = surveyListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        surveyListener.onStart();
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
        surveyListener.onEnd(s, success, message, user_id,survey_name, survey_surname, survey_fathername, survey_dateofbirth, survey_gender, survey_housenumber, survey_wardnumber,survey_city,survey_mandal,survey_district,survey_qualification,survey_occupation);
        super.onPostExecute(s);
    }
}
