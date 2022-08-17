package com.urbantech.padmashali;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.urbantech.AsyncTask.LoadFav;
import com.urbantech.AsyncTask.LoadOtp;
import com.urbantech.interfaces.SuccessListener;
import com.urbantech.utils.Constant;
import com.urbantech.utils.Methods;

public class OtpActivity extends AppCompatActivity {

    EditText otp1,otp2,otp3,otp4;
    AppCompatButton verify_btn;
    String mobile,otptext,type;
    Methods methods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        verify_btn = findViewById(R.id.verify_btn);
        otp1=findViewById(R.id.otp1);
        otp2=findViewById(R.id.otp2);
        otp3=findViewById(R.id.otp3);
        otp4=findViewById(R.id.otp4);


        Intent intent = getIntent();
        mobile = intent.getExtras().getString("mobile");
        type=intent.getExtras().getString("verification_type");


        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otp1.getText().toString().length() == 1) {
                    otp2.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (otp2.getText().length() == 0) {
                    otp1.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otp2.getText().toString().length() == 1) {
                    otp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (otp2.getText().length() == 0) {
                    otp1.requestFocus();
                }
            }
        });
        otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (otp3.getText().length() == 0) {
                    otp2.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otp3.getText().toString().length() == 1) {
                    otp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (otp3.getText().length() == 0) {
                    otp2.requestFocus();
                }
            }
        });
        otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (otp4.getText().length() == 0) {
                    otp3.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otp4.getText().toString().length() == 1) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//doubt
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null)
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (otp4.getText().length() == 0) {
                    otp3.requestFocus();
                }
            }
        });

        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otptext=otp1.getText().toString().trim()+otp2.getText().toString().trim()
                        +otp3.getText().toString().trim()+otp4.getText().toString().trim();

                if(otptext.length()==4){

                    loadOtp();
                }else {
                    Toast.makeText(getApplicationContext(),"Check OTP",Toast.LENGTH_LONG).show();
                }

            }
        });
//        methods.showToast("OTP send to you Mobile Number");

    }

    private void loadOtp() {

        if (methods.isNetworkAvailable()) {
            LoadOtp loadOtp = new LoadOtp(new SuccessListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String otpSuccess, String message) {
                    if (success.equals("1")) {
                        if (otpSuccess.equals("1")) {
                            if(type.equals("register")){
                                Intent intent=new Intent(OtpActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Intent intent=new Intent(OtpActivity.this,ProfileActivity.class);
                                startActivity(intent);
                                finish();
                            }

                           Toast.makeText(OtpActivity.this, message, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(OtpActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(OtpActivity.this, getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_OTP, 0, "", "", "", type, otptext, "", "", "", "", mobile, "", "", "","","","","","","","","","","","","","","","","","","","","",null, null));
            loadOtp.execute();
        } else {
            methods.showToast(OtpActivity.this.getString(R.string.err_internet_not_conn));
        }
    }
}