package com.urbantech.padmashali;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.urbantech.AsyncTask.LoadMembership;
import com.urbantech.AsyncTask.LoadSurvey;
import com.urbantech.AsyncTask.LoadUpload;
import com.urbantech.interfaces.SuccessListener;
import com.urbantech.interfaces.SurveyListener;
import com.urbantech.utils.Constant;
import com.urbantech.utils.Methods;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import cn.refactor.library.SmoothCheckBox;

public class CreateMemberShipActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    Toolbar toolbar;
    Methods methods;
    RadioGroup religion_grp,caste_grp,marital_status;
    AppCompatEditText member_name,member_surname,member_fathername,spouse_name,edt_member_qualification,edt_member_profession,member_hno,member_wardno,member_city,member_madal,member_assembly,member_parliament,member_district,member_state,member_country,member_amount,member_mobilenumber;
    AppCompatTextView member_dob,spouse_dob;
    Spinner member_gender,spouse_gender,member_qualification,member_profession,membership_type;
    SmoothCheckBox cb_accept_allterms;
    AppCompatButton submit;
    LinearLayout ll_spouse_layout;
    CardView cardView_qualification,cardView_profession,cardview_amount;
    private String dob;
    private Uri imageUri;
    private ProgressDialog progress;
    String membership_id,str_religion="",str_caste="",str_marital_status="",str_qualification="",str_profession="";
    RoundedImageView member_profile;
    String payment_status="0",reference_number;
    final int UPI_PAYMENT = 0;
    TextView txt_payment_status;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_member_ship);

        methods = new Methods(this);

        toolbar = findViewById(R.id.toolbar_membership_create);
        toolbar.setTitle(getResources().getString(R.string.member_ship));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.loading));
        progress.setCancelable(false);

        religion_grp=findViewById(R.id.hindu_radio_grp);
        caste_grp=findViewById(R.id.caste_radio_grp);
        member_profile=findViewById(R.id.iv_membership);
        member_name=findViewById(R.id.membership_name);
        member_surname=findViewById(R.id.membership_surname);
        member_dob=findViewById(R.id.membership_dob);
        member_gender=findViewById(R.id.membership_gender);
        member_fathername=findViewById(R.id.membership_fathername);
        marital_status=findViewById(R.id.marriage_radio_grp);
        spouse_name=findViewById(R.id.membership_spouse_name);
        spouse_dob=findViewById(R.id.membership_spouse_dob);
        spouse_gender=findViewById(R.id.membership_spouse_gender);
        member_qualification=findViewById(R.id.membership_qualification_spinner);
        edt_member_qualification=findViewById(R.id.membership_qualification_others);
        member_profession=findViewById(R.id.membership_profession_spinner);
        edt_member_profession=findViewById(R.id.membership_occupation_others);
        member_hno=findViewById(R.id.membership_housenumber);
        member_wardno=findViewById(R.id.membership_wardnumber);
        member_city=findViewById(R.id.membership_city);
        member_madal=findViewById(R.id.membership_mandal);
        member_assembly=findViewById(R.id.membership_assembly);
        member_parliament=findViewById(R.id.membership_parliament);
        member_district=findViewById(R.id.membership_district);
        member_state=findViewById(R.id.membership_state);
        member_country=findViewById(R.id.membership_country);

        cb_accept_allterms=findViewById(R.id.cb_membership_terms);
        membership_type=findViewById(R.id.membership_applicable_spinner);
        member_amount=findViewById(R.id.membership_amount);
        member_mobilenumber=findViewById(R.id.membership_reference_number);

        submit=findViewById(R.id.btn_membership_submit);

        ll_spouse_layout=findViewById(R.id.ll_spouse_details);
        cardView_qualification=findViewById(R.id.card_qualification);
        cardView_profession=findViewById(R.id.card_profession);
        cardview_amount=findViewById(R.id.cardview_amount);

        membership_id=getRandomNumber();
        txt_payment_status=findViewById(R.id.payment_status);


        if(payment_status.equals("0")){
            submit.setText("pay for membership");
        }else{
            submit.setText("Submit");
        }

        religion_grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                if(i==R.id.hindu_yes){
                    str_religion="Hindu";
                }else {
                    str_religion="Other";
                }
            }
        });

        caste_grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                if (i==R.id.caste_yes){
                    str_caste="Padmashali";
                }else {
                    str_caste="Others";
                }
            }
        });

        marital_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                if(i==R.id.married){
                    ll_spouse_layout.setVisibility(View.VISIBLE);
                    str_marital_status="Married";
                }else if(i==R.id.unmarried){
                    ll_spouse_layout.setVisibility(View.GONE);
                    str_marital_status="Single";
                }
            }
        });

        member_qualification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(member_qualification.getSelectedItem().toString().equals("Others")){
                    cardView_qualification.setVisibility(View.VISIBLE);
                }else {
                    cardView_qualification.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        member_profession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(member_profession.getSelectedItem().toString().equals("Employee")){
                    cardView_profession.setVisibility(View.VISIBLE);
                }else {
                    cardView_profession.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        member_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dob="member";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CreateMemberShipActivity.this,
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );
// If you're calling this from a support Fragment
                dpd.show(getSupportFragmentManager(), "Datepickerdialog");
            }

        });

        spouse_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dob="spouse";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CreateMemberShipActivity.this,
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );
// If you're calling this from a support Fragment
                dpd.show(getSupportFragmentManager(), "Datepickerdialog");
            }
        });

        member_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (methods.checkPer()) {
                    pickImage();
                } else {
                    Toast.makeText(CreateMemberShipActivity.this, getString(R.string.cannot_use_save_permission), Toast.LENGTH_SHORT).show();
                }
            }
        });

        reference_number="Padmashali"+getRandomNumber();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()) {
                    if (payment_status.equals("0")) {

                        String amount = member_amount.getText().toString();
                        String note =getResources().getString(R.string.upi_note);
                        String name =getResources().getString(R.string.upi_name);
                        String upiId =getResources().getString(R.string.upi_id);

                        if(member_amount.getText().toString().trim().isEmpty()) {
                            member_amount.setError("Enter Amount");
                            member_amount.requestFocus();
                        }else {
                            payUsingUpi(amount, upiId, name, note, reference_number);
                        }

                    } else {
                        if (member_qualification.getSelectedItem().toString().equals("Others")) {
                            str_qualification = edt_member_qualification.getText().toString();
                        } else {
                            str_qualification = member_qualification.getSelectedItem().toString();
                        }

                        if (member_profession.getSelectedItem().toString().equals("Employee")) {
                            str_profession = edt_member_profession.getText().toString();
                        } else {
                            str_profession = member_profession.getSelectedItem().toString();
                        }
                        uploadMembership();
                    }
                }
            }
        });

    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_image)), PICK_IMAGE_REQUEST);
    }

    private void uploadMembership() {
        File file = null;
        try {
            if (imageUri != null) {
                file = new File(methods.getTempUploadPath(imageUri));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        LoadMembership loadMembership = new LoadMembership(new SuccessListener() {
            @Override
            public void onStart() {
                progress.show();
            }

            @Override
            public void onEnd(String success, String registerSuccess, String message) {
                if (success.equals("1")) {
                    if (registerSuccess.equals("1")) {

                        Intent intent=new Intent(CreateMemberShipActivity.this,OtpActivity.class);
                        intent.putExtra("mobile",member_mobilenumber.getText().toString());
                        intent.putExtra("verification_type","membership");
                        startActivity(intent);
                        finish();

//                        Toast.makeText(CreateMemberShipActivity.this, message, Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(CreateMemberShipActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(CreateMemberShipActivity.this, getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }
        }, methods.getAPIRequest(Constant.METHOD_MEMBERSHIP, 0,"", membership_id, "", membership_type.getSelectedItem().toString(), "", member_dob.getText().toString(), "", "", member_name.getText().toString(), member_mobilenumber.getText().toString(), Constant.itemUser.getId(), payment_status, member_surname.getText().toString(),member_fathername.getText().toString(),member_gender.getSelectedItem().toString(),member_hno.getText().toString(),member_wardno.getText().toString(),member_city.getText().toString(),member_madal.getText().toString(),member_district.getText().toString(),str_qualification,str_profession,str_religion,str_caste,str_marital_status,spouse_name.getText().toString(),spouse_dob.getText().toString(),spouse_gender.getSelectedItem().toString(),member_assembly.getText().toString(),member_parliament.getText().toString(),member_state.getText().toString(),member_country.getText().toString(),member_amount.getText().toString(), file, null));
        loadMembership.execute();

    }

    private boolean validateData() {
        if(str_religion.equals("")){
            Toast.makeText(CreateMemberShipActivity.this,"Select Religion",Toast.LENGTH_SHORT).show();
            return false;
        }else if(str_caste.equals("")){
            Toast.makeText(CreateMemberShipActivity.this,"Select Caste",Toast.LENGTH_SHORT).show();
            return false;
        } else if (member_name.getText().toString().trim().equals("")) {
            member_name.setError("required");
            member_name.requestFocus();
            return false;
        } else if (member_surname.getText().toString().trim().equals("")) {
            member_surname.setError("required");
            member_surname.requestFocus();
            return false;
        } else if (member_dob.getText().toString().trim().equals("")) {
            member_dob.setError("required");
            member_dob.requestFocus();
            return false;
        } else if (member_fathername.getText().toString().trim().equals("")) {
            member_fathername.setError("required");
            member_fathername.requestFocus();
            return false;
        }else if(str_marital_status.equals("")){
            Toast.makeText(CreateMemberShipActivity.this,"Select Marital status",Toast.LENGTH_SHORT).show();
            return false;
        }else if (member_hno.getText().toString().trim().equals("")) {
            member_hno.setError("required");
            member_hno.requestFocus();
            return false;
        } else if (member_wardno.getText().toString().trim().equals("")) {
            member_wardno.setError("required");
            member_wardno.requestFocus();
            return false;
        }else if (member_city.getText().toString().trim().equals("")) {
            member_city.setError("required");
            member_city.requestFocus();
            return false;
        }else if (member_madal.getText().toString().trim().equals("")) {
            member_madal.setError("required");
            member_madal.requestFocus();
            return false;
        }else if (member_assembly.getText().toString().trim().equals("")) {
            member_assembly.setError("required");
            member_assembly.requestFocus();
            return false;
        }else if (member_parliament.getText().toString().trim().equals("")) {
            member_parliament.setError("required");
            member_parliament.requestFocus();
            return false;
        }else if (member_district.getText().toString().trim().equals("")) {
            member_district.setError("required");
            member_district.requestFocus();
            return false;
        }else if (member_state.getText().toString().trim().equals("")) {
            member_state.setError("required");
            member_state.requestFocus();
            return false;
        }else if (member_country.getText().toString().trim().equals("")) {
            member_country.setError("required");
            member_country.requestFocus();
            return false;
        } else if(member_mobilenumber.getText().toString().trim().equals("")){
            member_mobilenumber.setError("required");
            member_mobilenumber.requestFocus();
            return false;
        }else if(!cb_accept_allterms.isChecked()){
            Toast.makeText(CreateMemberShipActivity.this, "Please Accept All Terms and Conditions", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if(dob.equals("member")) {
            member_dob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
        }
        if (dob.equals("spouse")){
            spouse_dob.setText(dayOfMonth+"-"+(monthOfYear+1)+"-"+year);
        }
    }

    private String getRandomNumber() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    void payUsingUpi(String amount, String upiId, String name, String note,String reference_number) {

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
//                .appendQueryParameter("mc","MAHSPO4HO")
                .appendQueryParameter("tr",reference_number)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
            member_profile.setImageURI(imageUri);
        }

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        //Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        //Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(CreateMemberShipActivity.this)) {
            String str = data.get(0);
            //Log.d("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {

//                Intent i = new Intent(UpiActivity.this, RegisterActivity.class);
//                i.putExtra("payment_status", String.valueOf(1));
//                setResult(1,i);
//                finish();
                payment_status="1";
                submit.setText("Submit");
                txt_payment_status.setVisibility(View.VISIBLE);
                cardview_amount.setVisibility(View.GONE);


                //Code to handle successful transaction here.
                Toast.makeText(CreateMemberShipActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                // Log.d("UPI", "responseStr: "+approvalRefNo);
//                Toast.makeText(this, "YOUR REGISTRATION HAS BEEN \n SUCCESSFUL ", Toast.LENGTH_LONG).show();
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(CreateMemberShipActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(CreateMemberShipActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(CreateMemberShipActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}