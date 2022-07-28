package com.urbantech.padmashali;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.urbantech.AsyncTask.LoadFeedback;
import com.urbantech.interfaces.FeedbackListener;
import com.urbantech.utils.Constant;
import com.urbantech.utils.Methods;

public class FeedbackActivity extends AppCompatActivity {

    AppCompatEditText feedback_text;
    AppCompatButton submit_feedback;
    Methods methods;

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
        setContentView(R.layout.activity_feedback);

        Toolbar toolbar = findViewById(R.id.toolbar_feedback);
        toolbar.setTitle(getString(R.string.feedback));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        feedback_text=findViewById(R.id.feedback_text);
        submit_feedback=findViewById(R.id.button_submit_feedback);

        submit_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFeedback();
            }
        });

    }

    private void uploadFeedback() {
        LoadFeedback loadFeedback = new LoadFeedback(new FeedbackListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(String success, String registerSuccess, String message, String user_id, String feedback) {
                if (success.equals("1")) {
                    Toast.makeText(FeedbackActivity.this, message, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from", "");
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(FeedbackActivity.this, getString(R.string.err_server_no_conn), Toast.LENGTH_SHORT).show();
                }

            }

        }, methods.getAPIRequest(Constant.METHOD_FEEDBACK, 0, feedback_text.getText().toString(), "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "","","","","","","","","","",null, null));
        loadFeedback.execute();
    }
}