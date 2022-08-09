package com.urbantech.padmashali;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.urbantech.AsyncTask.LoadProfile;
import com.urbantech.AsyncTask.LoadSingleMember;
import com.urbantech.interfaces.SuccessListener;
import com.urbantech.item.ItemMember;
import com.urbantech.utils.Constant;
import com.urbantech.utils.Methods;

import java.lang.reflect.Method;

public class SingleMemberActivity extends AppCompatActivity {

    Methods methods;
    String member_id;
    private ProgressDialog progressDialog;
    RoundedImageView single_member_profile,id_card_image;
    TextView id,member_name,member_surname,member_dob,member_gender,member_fathername,member_marital_status,member_religion,member_caste,member_qualification,member_profession,assembly,parliament,member_address,member_mobile,member_type,card_id,card_name,card_membership_type,card_mobilenumber,card_address;
    ItemMember itemMember;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_member);

        Toolbar toolbar = findViewById(R.id.toolbar_single_member);
        toolbar.setTitle(getString(R.string.member_ship));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        methods = new Methods(this);

        single_member_profile=findViewById(R.id.iv_single_member);
        id_card_image=findViewById(R.id.iv_id);

        id=findViewById(R.id.single_member_id);
        member_name=findViewById(R.id.single_member_name);
        member_surname=findViewById(R.id.single_member_surname);
        member_dob=findViewById(R.id.single_member_dob);
        member_gender=findViewById(R.id.single_member_gender);
        member_fathername=findViewById(R.id.single_member_father_name);
        member_marital_status=findViewById(R.id.single_member_marital);
        member_religion=findViewById(R.id.single_member_hindu);
        member_caste=findViewById(R.id.single_member_padmashly);
        member_qualification=findViewById(R.id.single_member_qualification);
        member_profession=findViewById(R.id.single_member_profession);
        assembly=findViewById(R.id.single_member_assembly);
        parliament=findViewById(R.id.single_member_parliament);
        member_address=findViewById(R.id.single_member_address);
        member_mobile=findViewById(R.id.single_member_mobile);
        member_type=findViewById(R.id.single_member_type);

        card_id=findViewById(R.id.id_id);
        card_name=findViewById(R.id.id_name);
        card_membership_type=findViewById(R.id.id_membership_type);
        card_mobilenumber=findViewById(R.id.id_mobile);
        card_address=findViewById(R.id.id_address);



        if (getIntent().getExtras() != null) {
            member_id=getIntent().getStringExtra("member_id");
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);


        loadUserProfile();
    }
    private void loadUserProfile() {

        LoadSingleMember loadSingleMember = new LoadSingleMember(new SuccessListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, String registerSuccess, String message) {
                progressDialog.dismiss();
                if (success.equals("1")) {

                    if (!Constant.itemMember.getMember_profile().equals("")) {
                        Picasso.get().load(Constant.itemMember.getMember_profile()).placeholder(R.drawable.placeholder_prof).into(single_member_profile);
                        Picasso.get().load(Constant.itemMember.getMember_profile()).placeholder(R.drawable.placeholder_prof).into(id_card_image);
                    }

                    id.setText("ID Num:-"+Constant.itemMember.getMember_id());
                    member_name.setText(Constant.itemMember.getMember_name());
                    member_surname.setText(Constant.itemMember.getMeber_surname());
                    member_dob.setText(Constant.itemMember.getMember_dob());
                    member_gender.setText(Constant.itemMember.getMember_gender());
                    member_fathername.setText(Constant.itemMember.getMember_fathername());
                    member_marital_status.setText(Constant.itemMember.getMember_marital_status());
                    member_religion.setText(Constant.itemMember.getMember_religion());
                    member_caste.setText(Constant.itemMember.getMember_caste());
                    member_qualification.setText(Constant.itemMember.getMember_qualification());
                    member_profession.setText(Constant.itemMember.getMember_profession());
                    assembly.setText(Constant.itemMember.getMember_assembly());
                    parliament.setText(Constant.itemMember.getMember_parliament());
                    member_address.setText(Constant.itemMember.getMember_hno()+","+Constant.itemMember.getMember_wardno()+","+Constant.itemMember.getMember_city()+","+Constant.itemMember.getMember_district());
                    member_mobile.setText(Constant.itemMember.getMember_mobile());
                    member_type.setText(Constant.itemMember.getMember_type());

                    card_id.setText("ID Num:-"+Constant.itemMember.getMember_id());
                    card_name.setText(Constant.itemMember.getMember_name());
                    card_mobilenumber.setText(Constant.itemMember.getMember_mobile());
                    card_membership_type.setText(Constant.itemMember.getMember_type());
                    card_address.setText(Constant.itemMember.getMember_hno()+","+Constant.itemMember.getMember_wardno()+",\n"+Constant.itemMember.getMember_city()+",\n"+Constant.itemMember.getMember_district());

                } else {
                    Toast.makeText(SingleMemberActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }

            }
        }, methods.getAPIRequest(Constant.METHOD_SINGLE_MEMBERSHIP, 0, "", member_id, "", "", "", "", "", "", "", "", "", "",  "","","","","","","","","","","","","","","","","","","","","",null, null));
        loadSingleMember.execute();
    }
}