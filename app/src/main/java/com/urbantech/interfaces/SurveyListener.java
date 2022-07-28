package com.urbantech.interfaces;

public interface SurveyListener {
    void onStart();
    void onEnd(String success, String registerSuccess, String message,String user_id, String survey_name, String survey_surname, String survey_fathername, String survey_dateofbirth, String survey_gender, String survey_housenumber, String survey_wardnumber, String survey_city, String survey_mandal, String survey_district, String survey_qualification, String survey_occupation);
}
