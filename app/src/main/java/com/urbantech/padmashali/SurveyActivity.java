package com.urbantech.padmashali;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.urbantech.AsyncTask.LoadSurvey;
import com.urbantech.interfaces.SurveyListener;
import com.urbantech.utils.Constant;
import com.urbantech.utils.Methods;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

public class SurveyActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Methods methods;
    AppCompatButton button_survey;
    AppCompatEditText survey_name,survey_surname,survey_fathername,survey_hno,survey_wardno,survey_city,survey_mandal,survey_district,survey_qualification;
    AppCompatTextView survey_dob;
    Spinner spinner_occupation,survey_gender;
    ProgressDialog progressDialog;

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
        setContentView(R.layout.activity_survey);

        Toolbar toolbar = findViewById(R.id.toolbar_survey);
        toolbar.setTitle(getString(R.string.survey));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());


        final ArrayList<String> list = new ArrayList<>();
        list.add("Business");
        list.add("Employee");

        final ArrayList<String> gender_list = new ArrayList<>();
        gender_list.add("Male");
        gender_list.add("Female");
        gender_list.add("Other");

        survey_name=findViewById(R.id.survey_name);
        survey_surname=findViewById(R.id.survey_surname);
        survey_fathername=findViewById(R.id.survey_fathername);
        survey_dob=findViewById(R.id.survey_dob);
        survey_gender=findViewById(R.id.survey_gender);
        survey_hno=findViewById(R.id.survey_housenumber);
        survey_wardno=findViewById(R.id.survey_wardnumber);
        survey_city=findViewById(R.id.city);
        survey_mandal=findViewById(R.id.mandal);
        survey_district=findViewById(R.id.district);
        survey_qualification=findViewById(R.id.qualification);

        spinner_occupation=findViewById(R.id.spinner_occupation);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>
                (SurveyActivity
                        .this, android.R.layout.simple_spinner_item,
                        list);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner_occupation.setAdapter(spinnerArrayAdapter);

        ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<>
                (SurveyActivity
                        .this, android.R.layout.simple_spinner_item,
                        gender_list);
        spinnerArrayAdapter1.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        survey_gender.setAdapter(spinnerArrayAdapter1);

        survey_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        SurveyActivity.this,
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );
// If you're calling this from a support Fragment
                dpd.show(getSupportFragmentManager(), "Datepickerdialog");
            }
        });

        button_survey=findViewById(R.id.button_upload_survey_submit);
        button_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent =new Intent(SurveyActivity.this,SurveyDetailsActivity.class);
//                startActivity(intent);

                uploadSurvey();


            }
        });

    }

    private void uploadSurvey() {
        LoadSurvey loadSurvey = new LoadSurvey(new SurveyListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(String success, String registerSuccess, String message, String user_id, String survey_name, String survey_surname, String survey_fathername, String survey_dateofbirth, String survey_gender, String survey_housenumber, String survey_wardnumber, String survey_city, String survey_mandal, String survey_district, String survey_qualification, String survey_occupation) {
                if (success.equals("1")) {
                        Toast.makeText(SurveyActivity.this, message, Toast.LENGTH_SHORT).show();
//
                        Intent intent = new Intent(SurveyActivity.this, SurveyDetailsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("from", "");
                        startActivity(intent);
                        finish();
//
                } else {
                    Toast.makeText(SurveyActivity.this, getString(R.string.err_server_no_conn), Toast.LENGTH_SHORT).show();
                }
            }


        }, methods.getAPIRequest(Constant.METHOD_SURVEY, 0, "", "", "", spinner_occupation.getSelectedItem().toString(), "", survey_dob.getText().toString(), "", "", survey_name.getText().toString(), "", Constant.itemUser.getId(), "", survey_surname.getText().toString(),survey_fathername.getText().toString(),survey_gender.getSelectedItem().toString(),survey_hno.getText().toString(),survey_wardno.getText().toString(),survey_city.getText().toString(),survey_mandal.getText().toString(),survey_district.getText().toString(),survey_qualification.getText().toString(),null, null));
        loadSurvey.execute();
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        survey_dob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
    }
}