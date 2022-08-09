package com.urbantech.padmashali;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.urbantech.utils.Methods;

public class SingleSurveyActivity extends AppCompatActivity {

    TextView name,surame,fathername,dob,gender,hno,wardnumber,city,mandal,district,qualification,occupation;
    private Methods methods;

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
        setContentView(R.layout.activity_single_survey);

        Toolbar toolbar = findViewById(R.id.toolbar_single_survey);
        toolbar.setTitle(getString(R.string.survey));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        name=findViewById(R.id.single_survey_name);
        surame=findViewById(R.id.single_survey_surname);
        fathername=findViewById(R.id.single_survey_fathername);
        dob=findViewById(R.id.single_survey_dob);
        gender=findViewById(R.id.single_survey_gender);
        hno=findViewById(R.id.single_survey_hno);
        wardnumber=findViewById(R.id.single_survey_wardnumber);
        city=findViewById(R.id.single_survey_city);
        mandal=findViewById(R.id.single_survey_mandal);
        district=findViewById(R.id.single_survey_district);
        qualification=findViewById(R.id.single_survey_qualification);
        occupation=findViewById(R.id.single_survey_occupation);

        if (getIntent().getExtras() != null) {
            name.setText(getIntent().getStringExtra("name"));
            surame.setText(getIntent().getStringExtra("surname"));
            fathername.setText(getIntent().getStringExtra("fathername"));
            dob.setText(getIntent().getStringExtra("dob"));
            gender.setText(getIntent().getStringExtra("gender"));
            hno.setText(getIntent().getStringExtra("hno"));
            wardnumber.setText(getIntent().getStringExtra("wardnumber"));
            city.setText(getIntent().getStringExtra("city"));
            mandal.setText(getIntent().getStringExtra("mandal"));
            district.setText(getIntent().getStringExtra("district"));
            qualification.setText(getIntent().getStringExtra("qualification"));
            occupation.setText(getIntent().getStringExtra("occupation"));
        }



    }


}