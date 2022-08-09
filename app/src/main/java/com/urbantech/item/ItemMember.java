package com.urbantech.item;

import java.io.Serializable;

public class ItemMember implements Serializable {

    String user_id,member_id,member_profile,member_religion,member_caste,member_name,meber_surname,member_dob,member_gender,member_fathername,member_marital_status,member_qualification,member_profession,member_hno,member_wardno,member_city,member_mandal,member_assembly,member_parliament,member_district,member_state,member_county,member_mobile,member_type,member_amount,spouse_name,spouse_dob,spouse_gender;

    public ItemMember(String user_id,String member_id, String member_profile, String member_religion, String member_caste, String member_name, String meber_surname, String member_dob, String member_gender, String member_fathername, String member_marital_status, String member_qualification, String member_profession, String member_hno, String member_wardno, String member_city, String member_mandal, String member_assembly, String member_parliament, String member_district, String member_state, String member_county, String member_mobile, String member_type, String member_amount, String spouse_name, String spouse_dob, String spouse_gender) {
        this.user_id=user_id;
        this.member_id = member_id;
        this.member_profile = member_profile;
        this.member_religion = member_religion;
        this.member_caste = member_caste;
        this.member_name = member_name;
        this.meber_surname = meber_surname;
        this.member_dob = member_dob;
        this.member_gender = member_gender;
        this.member_fathername = member_fathername;
        this.member_marital_status = member_marital_status;
        this.member_qualification = member_qualification;
        this.member_profession = member_profession;
        this.member_hno = member_hno;
        this.member_wardno = member_wardno;
        this.member_city = member_city;
        this.member_mandal = member_mandal;
        this.member_assembly = member_assembly;
        this.member_parliament = member_parliament;
        this.member_district = member_district;
        this.member_state = member_state;
        this.member_county = member_county;
        this.member_mobile = member_mobile;
        this.member_type = member_type;
        this.member_amount = member_amount;
        this.spouse_name = spouse_name;
        this.spouse_dob = spouse_dob;
        this.spouse_gender = spouse_gender;
    }

    public void setSpouse_name(String spouse_name) {
        this.spouse_name = spouse_name;
    }

    public void setSpouse_dob(String spouse_dob) {
        this.spouse_dob = spouse_dob;
    }

    public void setSpouse_gender(String spouse_gender) {
        this.spouse_gender = spouse_gender;
    }

    public String getSpouse_name() {
        return spouse_name;
    }

    public String getSpouse_dob() {
        return spouse_dob;
    }

    public String getSpouse_gender() {
        return spouse_gender;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public void setMember_profile(String member_profile) {
        this.member_profile = member_profile;
    }

    public void setMember_religion(String member_religion) {
        this.member_religion = member_religion;
    }

    public void setMember_caste(String member_caste) {
        this.member_caste = member_caste;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public void setMeber_surname(String meber_surname) {
        this.meber_surname = meber_surname;
    }

    public void setMember_dob(String member_dob) {
        this.member_dob = member_dob;
    }

    public void setMember_gender(String member_gender) {
        this.member_gender = member_gender;
    }

    public void setMember_fathername(String member_fathername) {
        this.member_fathername = member_fathername;
    }

    public void setMember_marital_status(String member_marital_status) {
        this.member_marital_status = member_marital_status;
    }

    public void setMember_qualification(String member_qualification) {
        this.member_qualification = member_qualification;
    }

    public void setMember_profession(String member_profession) {
        this.member_profession = member_profession;
    }

    public void setMember_hno(String member_hno) {
        this.member_hno = member_hno;
    }

    public void setMember_wardno(String member_wardno) {
        this.member_wardno = member_wardno;
    }

    public void setMember_city(String member_city) {
        this.member_city = member_city;
    }

    public void setMember_mandal(String member_mandal) {
        this.member_mandal = member_mandal;
    }

    public void setMember_assembly(String member_assembly) {
        this.member_assembly = member_assembly;
    }

    public void setMember_parliament(String member_parliament) {
        this.member_parliament = member_parliament;
    }

    public void setMember_district(String member_district) {
        this.member_district = member_district;
    }

    public void setMember_state(String member_state) {
        this.member_state = member_state;
    }

    public void setMember_county(String member_county) {
        this.member_county = member_county;
    }

    public void setMember_mobile(String member_mobile) {
        this.member_mobile = member_mobile;
    }

    public void setMember_type(String member_type) {
        this.member_type = member_type;
    }

    public void setMember_amount(String member_amount) {
        this.member_amount = member_amount;
    }

    public void setUser_id(String user_id){this.user_id=user_id;}

    public String getMember_id() {
        return member_id;
    }

    public String getMember_profile() {
        return member_profile;
    }

    public String getMember_religion() {
        return member_religion;
    }

    public String getMember_caste() {
        return member_caste;
    }

    public String getMember_name() {
        return member_name;
    }

    public String getMeber_surname() {
        return meber_surname;
    }

    public String getMember_dob() {
        return member_dob;
    }

    public String getMember_gender() {
        return member_gender;
    }

    public String getMember_fathername() {
        return member_fathername;
    }

    public String getMember_marital_status() {
        return member_marital_status;
    }

    public String getMember_qualification() {
        return member_qualification;
    }

    public String getMember_profession() {
        return member_profession;
    }

    public String getMember_hno() {
        return member_hno;
    }

    public String getMember_wardno() {
        return member_wardno;
    }

    public String getMember_city() {
        return member_city;
    }

    public String getMember_mandal() {
        return member_mandal;
    }

    public String getMember_assembly() {
        return member_assembly;
    }

    public String getMember_parliament() {
        return member_parliament;
    }

    public String getMember_district() {
        return member_district;
    }

    public String getMember_state() {
        return member_state;
    }

    public String getMember_county() {
        return member_county;
    }

    public String getMember_mobile() {
        return member_mobile;
    }

    public String getMember_type() {
        return member_type;
    }

    public String getMember_amount() {
        return member_amount;
    }

    public String getUser_id(){
        return user_id;
    }



}
