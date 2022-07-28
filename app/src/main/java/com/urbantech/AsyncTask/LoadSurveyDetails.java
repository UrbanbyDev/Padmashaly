package com.urbantech.AsyncTask;

import android.os.AsyncTask;

import com.urbantech.interfaces.CatListener;
import com.urbantech.interfaces.SurveyDetailsListener;
import com.urbantech.item.ItemCat;
import com.urbantech.item.ItemSurvey;
import com.urbantech.utils.Constant;
import com.urbantech.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadSurveyDetails extends AsyncTask<String, String, String> {

    private SurveyDetailsListener surveyDetailsListener;
    private RequestBody requestBody;
    private ArrayList<ItemSurvey> arrayList = new ArrayList<>();
    private String verifyStatus = "1", message = "";

    public LoadSurveyDetails(SurveyDetailsListener surveyDetailsListener, RequestBody requestBody) {
        this.surveyDetailsListener = surveyDetailsListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        surveyDetailsListener.onStart();
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
                    String user_id = obj.getString("user_id");
                    String survey_name = obj.getString("survey_name");
                    String survey_surname = obj.getString("survey_surname");
                    String survey_fathername = obj.getString("survey_fathername");
                    String survey_dateofbirth = obj.getString("survey_dateofbirth");
                    String survey_gender = obj.getString("survey_gender");
                    String survey_housenumber = obj.getString("survey_housenumber");
                    String survey_wardnumber = obj.getString("survey_wardnumber");
                    String survey_city = obj.getString("survey_city");
                    String survey_mandal = obj.getString("survey_mandal");
                    String survey_district = obj.getString("survey_district");
                    String survey_qualification = obj.getString("survey_qualification");
                    String survey_survey_occupation = obj.getString("survey_occupation");

                    ItemSurvey itemSurvey = new ItemSurvey(user_id,survey_name,survey_surname,survey_fathername,survey_dateofbirth,survey_gender,survey_housenumber,survey_wardnumber,survey_city,survey_mandal,survey_district,survey_qualification,survey_survey_occupation);
                    arrayList.add(itemSurvey);
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
        surveyDetailsListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}
