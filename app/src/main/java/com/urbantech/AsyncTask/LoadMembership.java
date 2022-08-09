package com.urbantech.AsyncTask;

import android.os.AsyncTask;

import com.urbantech.interfaces.MembershipListener;
import com.urbantech.interfaces.SuccessListener;
import com.urbantech.interfaces.SurveyListener;
import com.urbantech.utils.Constant;
import com.urbantech.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadMembership extends AsyncTask<String,String,String> {
    private RequestBody requestBody;
    private SuccessListener successListener;
    private String success = "0", message = "", user_id="", member_religion="",  member_caste="",  member_name="",  member_surname="",  member_dob="",  member_gender="",  member_fathername="",  member_marital_status="",  spouse_name="",  spouse_dob="",  spouse_gender="",  member_qualification="", member_profession="", member_hno="", member_wardno="", member_city="",member_mandal="",member_assembly="",member_parliament="",member_district="",member_state="",member_country="",membership_type="",member_amount="",member_mobile="",member_profile="";

    public LoadMembership(SuccessListener successListener, RequestBody requestBody) {
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
        successListener.onEnd(s, success, message);
//        membershipListener.onEnd(s, success, message, user_id, member_religion,  member_caste,  member_name,  member_surname,  member_dob,  member_gender,  member_fathername,  member_marital_status,  spouse_name,  spouse_dob,  spouse_gender,  member_qualification, member_profession, member_hno, member_wardno, member_city,member_mandal,member_assembly,member_parliament,member_district,member_state,member_country,membership_type,member_amount,member_mobile,member_profile);
        super.onPostExecute(s);
    }
}


