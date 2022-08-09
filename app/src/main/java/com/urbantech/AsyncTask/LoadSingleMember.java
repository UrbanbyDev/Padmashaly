package com.urbantech.AsyncTask;

import android.os.AsyncTask;

import com.urbantech.interfaces.MembershipListener;
import com.urbantech.interfaces.SuccessListener;
import com.urbantech.item.ItemMember;
import com.urbantech.item.ItemUser;
import com.urbantech.utils.Constant;
import com.urbantech.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadSingleMember extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private SuccessListener successListener;
    private String success = "0";

    public LoadSingleMember(SuccessListener successListener, RequestBody requestBody) {
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
                JSONObject obj = jsonArray.getJSONObject(i);

                String user_id = obj.getString("user_id");
                String member_id=obj.getString("member_id");
                String member_profile = obj.getString("member_profile");
                String member_religion = obj.getString("member_religion");
                String member_caste = obj.getString("member_caste");
                String member_name = obj.getString("member_name");
                String member_surname = obj.getString("member_surname");
                String member_dob = obj.getString("member_dob");
                String member_gender = obj.getString("member_gender");
                String member_fathername = obj.getString("member_fathername");
                String member_marital_status = obj.getString("member_marital_status");
                String spouse_name = obj.getString("spouse_name");
                String spouse_dob = obj.getString("spouse_dob");
                String spouse_gender = obj.getString("spouse_gender");
                String member_qualification = obj.getString("member_qualification");
                String member_profession = obj.getString("member_profession");
                String member_hno = obj.getString("member_hno");
                String member_wardno = obj.getString("member_wardno");
                String member_city = obj.getString("member_city");
                String member_mandal = obj.getString("member_mandal");
                String member_assembly = obj.getString("member_assembly");
                String member_parliament = obj.getString("member_parliament");
                String member_district = obj.getString("member_district");
                String member_state = obj.getString("member_state");
                String member_country = obj.getString("member_country");
                String membership_type = obj.getString("membership_type");
                String member_amount = obj.getString("member_amount");
                String member_mobile = obj.getString("member_mobile");

                Constant.itemMember = new ItemMember(user_id, member_id,member_profile,member_religion,member_caste,member_name,member_surname,member_dob,member_gender,member_fathername,member_marital_status,member_qualification,member_profession,member_hno,member_wardno,member_city,member_mandal,member_assembly,member_parliament,member_district,member_state,member_country,member_mobile,membership_type,member_amount,spouse_name,spouse_dob,spouse_gender);
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



