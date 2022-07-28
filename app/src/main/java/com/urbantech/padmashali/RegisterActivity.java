package com.urbantech.padmashali;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.urbantech.AsyncTask.LoadRegister;
import com.urbantech.interfaces.SocialLoginListener;
import com.urbantech.utils.Constant;
import com.urbantech.utils.Methods;
import com.urbantech.utils.SharedPref;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import cn.refactor.library.SmoothCheckBox;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class RegisterActivity extends AppCompatActivity {

    Methods methods;
    private RelativeLayout rl_terms;
    EditText editText_name, editText_email, editText_pass, editText_cpass, editText_phone;
    TextView textView_signin, tv_terms;
    Button button_register;
    SmoothCheckBox cb_terms;
    ProgressDialog progressDialog;
    SharedPref sharedPref;
    RoundedImageView iv_dp;
    Uri imageUri;
    int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPref = new SharedPref(this);
        methods = new Methods(this);
        methods.setStatusColor(getWindow());
        methods.forceRTLIfSupported(getWindow());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.registering));
        progressDialog.setCancelable(false);

        rl_terms = findViewById(R.id.rl_regis_terms);
        textView_signin = findViewById(R.id.tv_regis_signin);
        cb_terms = findViewById(R.id.cb_regis_terms);
        tv_terms = findViewById(R.id.tv_regis_terms);
        button_register = findViewById(R.id.button_register);
        editText_name = findViewById(R.id.et_regis_name);
        editText_email = findViewById(R.id.et_regis_email);
        editText_pass = findViewById(R.id.et_regis_password);
        editText_cpass = findViewById(R.id.et_regis_cpassword);
        editText_phone = findViewById(R.id.et_regis_phone);
        iv_dp = findViewById(R.id.iv_register);

        button_register.setBackground(methods.getRoundDrawable(getResources().getColor(R.color.primary)));

        TextView tv_welcome = findViewById(R.id.tv);

        tv_welcome.setTypeface(tv_welcome.getTypeface(), Typeface.BOLD);
        textView_signin.setTypeface(textView_signin.getTypeface(), Typeface.BOLD);
        button_register.setTypeface(button_register.getTypeface(), Typeface.BOLD);

        rl_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_terms.setChecked(!cb_terms.isChecked(), true);
            }
        });

        tv_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, TermsOfUseActivity.class);
                intent.putExtra("isprivacy", false);
                startActivity(intent);
            }
        });

        iv_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (methods.checkPer()) {
                    pickImage();
                } else {
                    Toast.makeText(RegisterActivity.this, getString(R.string.cannot_use_save_permission), Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    loadRegister();
                }
            }
        });

        textView_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private Boolean validate() {
        if (editText_name.getText().toString().trim().isEmpty()) {
            editText_name.setError(getResources().getString(R.string.enter_name));
            editText_name.requestFocus();
            return false;
        } else if (editText_email.getText().toString().trim().isEmpty()) {
            editText_email.setError(getResources().getString(R.string.enter_email));
            editText_email.requestFocus();
            return false;
        } else if (!isEmailValid(editText_email.getText().toString())) {
            editText_email.setError(getString(R.string.error_invalid_email));
            editText_email.requestFocus();
            return false;
        } else if (editText_pass.getText().toString().isEmpty()) {
            editText_pass.setError(getResources().getString(R.string.enter_password));
            editText_pass.requestFocus();
            return false;
        } else if (editText_pass.getText().toString().endsWith(" ")) {
            editText_pass.setError(getResources().getString(R.string.pass_end_space));
            editText_pass.requestFocus();
            return false;
        } else if (editText_cpass.getText().toString().isEmpty()) {
            editText_cpass.setError(getResources().getString(R.string.enter_cpassword));
            editText_cpass.requestFocus();
            return false;
        } else if (!editText_pass.getText().toString().equals(editText_cpass.getText().toString())) {
            editText_cpass.setError(getResources().getString(R.string.pass_nomatch));
            editText_cpass.requestFocus();
            return false;
        } else if (editText_phone.getText().toString().trim().isEmpty()) {
            editText_phone.setError(getResources().getString(R.string.enter_phone));
            editText_phone.requestFocus();
            return false;
        }else if (editText_phone.getText().length()<10){
            editText_phone.setError("Enter Correct Phone Number");
            editText_phone.requestFocus();
            return false;
        } else if (!cb_terms.isChecked()) {
            Toast.makeText(RegisterActivity.this, getString(R.string.accept_terms), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && !email.contains(" ");
    }

    private void loadRegister() {
        if (methods.isNetworkAvailable()) {
            File file = null;
            if (imageUri != null) {
                file = new File(methods.getTempUploadPath(imageUri));
            }

            LoadRegister loadRegister = new LoadRegister(new SocialLoginListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message, String user_id, String user_name, String email, String user_profile, String user_cat, String auth_id, Boolean isReporter) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (registerSuccess.equals("1")) {
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("from", "");
                            startActivity(intent);
                            finish();
                        } else {
                            editText_email.setError(message);
                            editText_email.requestFocus();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, getString(R.string.err_server_no_conn), Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_REGISTER, 0, "", "", "", "", "", "", editText_email.getText().toString(), editText_pass.getText().toString(), editText_name.getText().toString(), editText_phone.getText().toString(), "", "", "","","","","","","","","", file, null));
            loadRegister.execute();
        } else {
            Toast.makeText(this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_image)), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            iv_dp.setImageURI(imageUri);
        }
    }
}