package com.urbantech.padmashali;

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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.urbantech.AsyncTask.LoadProfileEdit;
import com.urbantech.eventbus.GlobalBus;
import com.urbantech.interfaces.SuccessListener;
import com.urbantech.item.ItemEventBus;
import com.urbantech.utils.Constant;
import com.urbantech.utils.Methods;
import com.urbantech.utils.SharedPref;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ProfileEditActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Methods methods;
    private LinearLayout ll_pass, ll_cpass;
    private EditText editText_name, editText_email, editText_phone, editText_pass, editText_cpass;
    private SharedPref sharedPref;
    private ProgressDialog progressDialog;
    private RoundedImageView iv_profile;
    private Uri imageUri;
    private int PICK_IMAGE_REQUEST = 1;
    private final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    RadioGroup rg_caste;
    private String caste_type="";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        sharedPref = new SharedPref(this);
        methods = new Methods(this);

        toolbar = findViewById(R.id.toolbar_proedit);
        toolbar.setTitle(getResources().getString(R.string.profile_edit));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));

        ll_pass = findViewById(R.id.ll_pass);
        ll_cpass = findViewById(R.id.ll_cpass);
        AppCompatButton button_update = findViewById(R.id.button_prof_update);
        iv_profile = findViewById(R.id.iv_pro_edit);
        editText_name = findViewById(R.id.et_profedit_name);
        editText_email = findViewById(R.id.et_profedit_email);
        editText_phone = findViewById(R.id.et_profedit_phone);
        editText_pass = findViewById(R.id.et_profedit_password);
        editText_cpass = findViewById(R.id.et_profedit_cpassword);

        rg_caste=findViewById(R.id.caste_grp);

        rg_caste.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                if (i == R.id.caste_yes) {

                    caste_type = "Yes";

                }

                if (i == R.id.caste_no) {

                    caste_type = "No";

                }
            }
        });



        if (Constant.itemUser.getLoginType().equals(Constant.LOGIN_TYPE_FB) || Constant.itemUser.getLoginType().equals(Constant.LOGIN_TYPE_GOOGLE)) {
            ll_pass.setVisibility(View.GONE);
            ll_cpass.setVisibility(View.GONE);
            editText_email.setEnabled(false);
        }

        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (methods.checkPer()) {
                    pickImage();
                } else {
                    Toast.makeText(ProfileEditActivity.this, getString(R.string.cannot_use_save_permission), Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_update.setBackground(methods.getRoundDrawable(getResources().getColor(R.color.colorPrimary)));
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    loadUpdateProfile();
                }
            }
        });

        setProfileVar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_image)), PICK_IMAGE_REQUEST);
    }

    private Boolean validate() {
        editText_name.setError(null);
        editText_email.setError(null);
        editText_cpass.setError(null);
        if (editText_name.getText().toString().trim().isEmpty()) {
            editText_name.setError(getString(R.string.cannot_empty));
            editText_name.requestFocus();
            return false;
        } else if (editText_email.getText().toString().trim().isEmpty()) {
            editText_email.setError(getString(R.string.email_empty));
            editText_email.requestFocus();
            return false;
        } else if (editText_pass.getText().toString().endsWith(" ")) {
            editText_pass.setError(getString(R.string.pass_end_space));
            editText_pass.requestFocus();
            return false;
        } else if (!editText_pass.getText().toString().trim().equals(editText_cpass.getText().toString().trim())) {
            editText_cpass.setError(getString(R.string.pass_nomatch));
            editText_cpass.requestFocus();
            return false;
        }
        else {
            return true;
        }
    }

    private void updateData() {
        Constant.itemUser.setName(editText_name.getText().toString());
        Constant.itemUser.setEmail(editText_email.getText().toString());
        Constant.itemUser.setMobile(editText_phone.getText().toString());

        if (!editText_pass.getText().toString().equals("")) {
            sharedPref.setRemeber(false);
        }
    }

    private void loadUpdateProfile() {
        if (methods.isNetworkAvailable()) {
            File file = null;
            if (imageUri != null) {
                file = new File(methods.getTempUploadPath(imageUri));
            }
            LoadProfileEdit loadProfileEdit = new LoadProfileEdit(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (registerSuccess.equals("1")) {
                            updateData();
                            Constant.isUpdate = true;
                            GlobalBus.getBus().postSticky(new ItemEventBus(getString(R.string.profile), null, 0));

                            Intent intent=new Intent(ProfileEditActivity.this,MainActivity.class);
                            startActivity(intent);

                        } else {
                            editText_email.setError(message);
                            editText_email.requestFocus();
                        }
                        Toast.makeText(ProfileEditActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileEditActivity.this, getString(R.string.err_server_no_conn), Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_PROFILE_EDIT, 0, "", "", "", "", caste_type, "", editText_email.getText().toString(), editText_pass.getText().toString(), editText_name.getText().toString(), editText_phone.getText().toString(), Constant.itemUser.getId(), "", "","","","","","","","","","","","","","","","","","","","","", file, null));
            loadProfileEdit.execute();
        } else {
            Toast.makeText(ProfileEditActivity.this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    private void setProfileVar() {
        editText_name.setText(Constant.itemUser.getName());
        editText_phone.setText(Constant.itemUser.getMobile());
        editText_email.setText(Constant.itemUser.getEmail());

        if (!Constant.itemUser.getImage().equals("")) {
            Picasso.get().load(Constant.itemUser.getImage()).placeholder(R.drawable.placeholder_prof).into(iv_profile);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
            iv_profile.setImageURI(imageUri);
        }
    }
}