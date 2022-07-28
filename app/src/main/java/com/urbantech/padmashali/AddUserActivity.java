package com.urbantech.padmashali;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.urbantech.AsyncTask.LoadAddUser;
import com.urbantech.interfaces.UserListener;
import com.urbantech.utils.Constant;
import com.urbantech.utils.Methods;
import com.urbantech.utils.SharedPref;

import java.io.File;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AddUserActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Methods methods;
    private LinearLayout ll_pass_adduser, ll_cpass_adduser;
    private EditText editText_name_adduser, editText_email_adduser, editText_phone_adduser, editText_pass_adduser, editText_cpass_adduser;
    private SharedPref sharedPref;
    private ProgressDialog progressDialog;
    private RoundedImageView iv_profile_adduser;
    private Uri imageUri;
    private int PICK_IMAGE_REQUEST = 1;
    private final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        sharedPref = new SharedPref(this);
        methods = new Methods(this);

        toolbar = findViewById(R.id.toolbar_adduser);
        toolbar.setTitle(getResources().getString(R.string.profile_edit));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));

        ll_pass_adduser = findViewById(R.id.ll_pass_adduser);
        ll_cpass_adduser = findViewById(R.id.ll_cpass_adduser);
        AppCompatButton button_update = findViewById(R.id.button_add_user);
        iv_profile_adduser = findViewById(R.id.iv_add_user);
        editText_name_adduser = findViewById(R.id.et_adduser_name);
        editText_email_adduser = findViewById(R.id.et_adduser_email);
        editText_phone_adduser = findViewById(R.id.et_adduser_phone);
        editText_pass_adduser = findViewById(R.id.et_adduser_password);
        editText_cpass_adduser= findViewById(R.id.et_adduser_cpassword);


        if (Constant.itemUser.getLoginType().equals(Constant.LOGIN_TYPE_FB) || Constant.itemUser.getLoginType().equals(Constant.LOGIN_TYPE_GOOGLE)) {
            ll_pass_adduser.setVisibility(View.GONE);
            ll_cpass_adduser.setVisibility(View.GONE);
            editText_email_adduser.setEnabled(false);
        }

//        iv_profile_adduser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (methods.checkPer()) {
//                    pickImage();
//                } else {
//                    Toast.makeText(AddUserActivity.this, getString(R.string.cannot_use_save_permission), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });



        button_update.setBackground(methods.getRoundDrawable(getResources().getColor(R.color.colorPrimary)));
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    addUser();
                }
            }
        });

    }

    private void addUser() {
        if (methods.isNetworkAvailable()) {
            File file = null;
            if (imageUri != null) {
                file = new File(methods.getTempUploadPath(imageUri));
            }else{
                imageUri = imageUri.parse(String.valueOf(R.drawable.ic_baseline_person_24));
                iv_profile_adduser.setImageURI(imageUri);
                file = new File(methods.getTempUploadPath(imageUri));
            }
            LoadAddUser loadAddUser = new LoadAddUser(new UserListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message, String user_id, String user_name, String email, String user_profile, String user_cat, String auth_id, Boolean isReporter) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        Toast.makeText(AddUserActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (registerSuccess.equals("1")) {
                            Intent intent = new Intent(AddUserActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("from", "");
                            startActivity(intent);
                            finish();
                        } else {
                            editText_email_adduser.setError(message);
                            editText_email_adduser.requestFocus();
                        }
                    } else {
                        Toast.makeText(AddUserActivity.this, getString(R.string.err_server_no_conn), Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_ADDUSER, 0, "", "", "", "", "", "", editText_email_adduser.getText().toString(), editText_pass_adduser.getText().toString(), editText_name_adduser.getText().toString(), editText_phone_adduser.getText().toString(), "", "",  "","","","","","","","","",file, null));
            loadAddUser.execute();
        } else {
            Toast.makeText(this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate() {
        editText_name_adduser.setError(null);
        editText_email_adduser.setError(null);
        editText_cpass_adduser.setError(null);
        if (editText_name_adduser.getText().toString().trim().isEmpty()) {
            editText_name_adduser.setError(getString(R.string.cannot_empty));
            editText_name_adduser.requestFocus();
            return false;
        } else if (editText_email_adduser.getText().toString().trim().isEmpty()) {
            editText_email_adduser.setError(getString(R.string.email_empty));
            editText_email_adduser.requestFocus();
            return false;
        } else if (editText_pass_adduser.getText().toString().endsWith(" ")) {
            editText_pass_adduser.setError(getString(R.string.pass_end_space));
            editText_pass_adduser.requestFocus();
            return false;
        } else if (!editText_pass_adduser.getText().toString().trim().equals(editText_cpass_adduser.getText().toString().trim())) {
            editText_cpass_adduser.setError(getString(R.string.pass_nomatch));
            editText_cpass_adduser.requestFocus();
            return false;
        } else {
            return true;
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
            iv_profile_adduser.setImageURI(imageUri);
        }
    }
}