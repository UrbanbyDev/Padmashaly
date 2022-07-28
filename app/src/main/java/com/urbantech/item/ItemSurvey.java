package com.urbantech.item;

import java.io.Serializable;

public class ItemSurvey implements Serializable {

    private String user_id,survey_name,survey_surname,survey_fathername,survey_dob,survey_gender,survey_housenumber,survey_wardnumber,survey_city,survey_mandal,survey_district,survey_qualification,survey_occupation;

    public void setSurvey_name(String survey_name) {
        this.survey_name = survey_name;
    }

    public void setSurvey_surname(String survey_surname) {
        this.survey_surname = survey_surname;
    }

    public void setSurvey_fathername(String survey_fathername) {
        this.survey_fathername = survey_fathername;
    }

    public void setSurvey_dob(String survey_dob) {
        this.survey_dob = survey_dob;
    }

    public void setSurvey_gender(String survey_gender) {
        this.survey_gender = survey_gender;
    }

    public void setSurvey_housenumber(String survey_housenumber) {
        this.survey_housenumber = survey_housenumber;
    }

    public void setSurvey_wardnumber(String survey_wardnumber) {
        this.survey_wardnumber = survey_wardnumber;
    }

    public void setSurvey_city(String survey_city) {
        this.survey_city = survey_city;
    }

    public void setSurvey_mandal(String survey_mandal) {
        this.survey_mandal = survey_mandal;
    }

    public void setSurvey_district(String survey_district) {
        this.survey_district = survey_district;
    }

    public void setSurvey_qualification(String survey_qualification) {
        this.survey_qualification = survey_qualification;
    }

    public void setSurvey_occupation(String survey_occupation) {
        this.survey_occupation = survey_occupation;
    }

    public String getSurvey_name() {
        return survey_name;
    }

    public String getSurvey_surname() {
        return survey_surname;
    }

    public String getSurvey_fathername() {
        return survey_fathername;
    }

    public String getSurvey_dob() {
        return survey_dob;
    }

    public String getSurvey_gender() {
        return survey_gender;
    }

    public String getSurvey_housenumber() {
        return survey_housenumber;
    }

    public String getSurvey_wardnumber() {
        return survey_wardnumber;
    }

    public String getSurvey_city() {
        return survey_city;
    }

    public String getSurvey_mandal() {
        return survey_mandal;
    }

    public String getSurvey_district() {
        return survey_district;
    }

    public String getSurvey_qualification() {
        return survey_qualification;
    }

    public String getSurvey_occupation() {
        return survey_occupation;
    }

    public ItemSurvey(String user_id, String survey_name, String survey_surname, String survey_fathername, String survey_dob, String survey_gender, String survey_housenumber, String survey_wardnumber, String survey_city, String survey_mandal, String survey_district, String survey_qualification, String survey_occupation) {
        this.user_id = user_id;
        this.survey_name = survey_name;
        this.survey_surname = survey_surname;
        this.survey_fathername = survey_fathername;
        this.survey_dob = survey_dob;
        this.survey_gender = survey_gender;
        this.survey_housenumber = survey_housenumber;
        this.survey_wardnumber = survey_wardnumber;
        this.survey_city = survey_city;
        this.survey_mandal = survey_mandal;
        this.survey_district = survey_district;
        this.survey_qualification = survey_qualification;
        this.survey_occupation = survey_occupation;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
