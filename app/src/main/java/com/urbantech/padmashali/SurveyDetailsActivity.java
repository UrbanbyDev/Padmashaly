package com.urbantech.padmashali;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.urbantech.AsyncTask.LoadSurveyDetails;
import com.urbantech.adapter.SurveyDetailsAdapter;
import com.urbantech.interfaces.SurveyDetailsListener;
import com.urbantech.item.ItemSurvey;
import com.urbantech.utils.Constant;
import com.urbantech.utils.Methods;
import com.urbantech.utils.SharedPref;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class SurveyDetailsActivity extends AppCompatActivity {

    Methods methods;
    AppCompatButton add_person;
    RecyclerView recycler_survey_details;
    private SharedPref sharedPref;
    SurveyDetailsAdapter adapter;
    ArrayList<ItemSurvey> arrayList ;


    private TextView textView_empty;
    private AppCompatButton button_try;
    private LinearLayout ll_empty;
    private String errr_msg;
    private CircularProgressBar progressBar;

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
        setContentView(R.layout.activity_survey_details);

        Toolbar toolbar = findViewById(R.id.toolbar_survey_details);
        toolbar.setTitle(getString(R.string.survey));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        arrayList = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar_cat);

        ll_empty = findViewById(R.id.ll_empty);
        textView_empty = findViewById(R.id.textView_empty_msg);
        button_try = findViewById(R.id.button_empty_try);


        recycler_survey_details=findViewById(R.id.recycler_survey_details);

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(SurveyDetailsActivity.this);
        recycler_survey_details.setLayoutManager(linearLayoutManager);

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surveyDetails();
            }
        });

        surveyDetails();

        add_person=findViewById(R.id.add_person);
        add_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SurveyDetailsActivity.this,SurveyActivity.class);
                startActivity(intent);
            }
        });

    }

    private void surveyDetails() {

        if (methods.isNetworkAvailable()) {
            LoadSurveyDetails loadSurveyDetails = new LoadSurveyDetails(new SurveyDetailsListener() {
                @Override
                public void onStart() {
                    arrayList.clear();
                    ll_empty.setVisibility(View.GONE);
                    recycler_survey_details.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemSurvey> arrayListSurvey) {
                    if (SurveyDetailsActivity.this!= null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                arrayList.addAll(arrayListSurvey);
                                errr_msg = getString(R.string.no_survey_details);
                            } else {
                                errr_msg = getString(R.string.err_server_no_conn);
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            }
                        }
                        setAdapter();
                        progressBar.setVisibility(View.GONE);
                    }
                }


            }, methods.getAPIRequest(Constant.METHOD_SURVEY_DETAILS,0,"","","","","","","","","","",Constant.itemUser.getId(),"","","","","","","","","","","","","","","","","","","","","","",null, null));
            loadSurveyDetails.execute();
        } else {
            errr_msg = getString(R.string.err_internet_not_conn);
            setEmpty();
        }
    }

    private void setEmpty() {

        progressBar.setVisibility(View.GONE);
        if (arrayList.size() > 0) {
            recycler_survey_details.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            recycler_survey_details.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }

    }

    private void setAdapter() {
        adapter = new SurveyDetailsAdapter(SurveyDetailsActivity.this, arrayList);
        recycler_survey_details.setAdapter(adapter);
        setEmpty();
    }
}