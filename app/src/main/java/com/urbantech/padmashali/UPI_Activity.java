package com.urbantech.padmashali;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.onesignal.OneSignal;
import com.urbantech.AsyncTask.LoadLogin;
import com.urbantech.AsyncTask.LoadRegister;
import com.urbantech.interfaces.LoginListener;
import com.urbantech.interfaces.SocialLoginListener;
import com.urbantech.item.ItemUser;
import com.urbantech.utils.Constant;
import com.urbantech.utils.Methods;
import com.urbantech.utils.SharedPref;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class UPI_Activity extends AppCompatActivity {

    private String from = "";
    EditText amountEt, noteEt, nameEt, upiIdEt;
    AppCompatButton send;
    String reg_name,reg_constiuency,reg_address,reg_phone,reg_dob,reg_email,reg_pass;

    final int UPI_PAYMENT = 0;
    private String reference_number;

    Methods methods;
    ProgressDialog progressDialog;
    SharedPref sharedPref;

    String payment_status;
    File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upi);

        sharedPref = new SharedPref(this);
        methods = new Methods(this);
        methods.setStatusColor(getWindow());
        methods.forceRTLIfSupported(getWindow());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.registering));
        progressDialog.setCancelable(false);

        send = findViewById(R.id.send);
        amountEt = findViewById(R.id.amount_et);
        noteEt = findViewById(R.id.note);
        nameEt = findViewById(R.id.name);
        upiIdEt = findViewById(R.id.upi_id);

        reference_number="AIFB"+getRandomNumberString();

        if (getIntent().getExtras() != null) {
            reg_name = getIntent().getStringExtra("name");
            reg_constiuency = getIntent().getStringExtra("constituency");
            reg_address = getIntent().getStringExtra("address");
            reg_phone=getIntent().getStringExtra("phone");
            reg_dob = getIntent().getStringExtra("dob");
            reg_email=getIntent().getStringExtra("email");
            reg_pass=getIntent().getStringExtra("password");
            file = (File) getIntent().getExtras().get("file");
            //The key argument must always match that used send and retrive value from one activity to another.
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Getting the values from the EditTexts

                String amount = amountEt.getText().toString();
                String note = noteEt.getText().toString();

                String name = nameEt.getText().toString();
                String upiId = upiIdEt.getText().toString();
                //upiIdEt.setFocusable(false);
                if(amountEt.getText().toString().trim().isEmpty()) {
                    amountEt.setError("Enter Amount");
                    amountEt.requestFocus();
                }else {
                    payUsingUpi(amount, upiId, name, note, reference_number);
//                    loadRegister();
                }
            }
        });
    }

    private String getRandomNumberString() {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
        if (isConnectionAvailable(UPI_Activity.this)) {
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
//                loadRegister();

                //Code to handle successful transaction here.
                Toast.makeText(UPI_Activity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                // Log.d("UPI", "responseStr: "+approvalRefNo);
                Toast.makeText(this, "YOUR REGISTRATION HAS BEEN \n SUCCESSFUL ", Toast.LENGTH_LONG).show();
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(UPI_Activity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(UPI_Activity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(UPI_Activity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
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

//    private void loadRegister() {
//        if (methods.isNetworkAvailable()) {
////            File file = null;
//////            if (imageUri != null) {
//////                file = new File(methods.getTempUploadPath(imageUri));
//////            }
//
//            LoadRegister loadRegister = new LoadRegister(new SocialLoginListener() {
//                @Override
//                public void onStart() {
//                    progressDialog.show();
//                }
//
//                @Override
//                public void onEnd(String success, String registerSuccess, String message, String user_id, String user_name, String email, String user_profile, String user_cat, String auth_id, String constituency, String address, String dateofbirth,String payment_status, Boolean isReporter) {
//                    progressDialog.dismiss();
//                    if (success.equals("1")) {
//                        Toast.makeText(UpiActivity.this, message, Toast.LENGTH_SHORT).show();
//                        if (registerSuccess.equals("1")) {
////                            Intent intent = new Intent(UpiActivity.this, LoginActivity.class);
////                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                            intent.putExtra("from", "");
////                            startActivity(intent);
////                            finish();
//                            loadLogin();
//                        }
//
//                    } else {
//                        Toast.makeText(UpiActivity.this, getString(R.string.err_server_no_conn), Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//
//            }, methods.getAPIRequest(Constant.METHOD_REGISTER, 0, "", payment_status, "", "", "", reg_dob, reg_address,reg_constiuency,reg_email,reg_pass, reg_name,reg_phone, "", "", file, null));
//            loadRegister.execute();
//        } else {
//            Toast.makeText(this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void loadLogin() {
//        if (methods.isNetworkAvailable()) {
//            LoadLogin loadLogin = new LoadLogin(new LoginListener() {
//                @Override
//                public void onStart() {
//                    progressDialog.show();
//                }
//
//                @Override
//                public void onEnd(String success, String loginSuccess, String message, String user_id, String user_name, String dp, String user_cat, Boolean isReporter) {
//                    progressDialog.dismiss();
//                    if (success.equals("1")) {
//                        if (loginSuccess.equals("1")) {
//                            Constant.itemUser = new ItemUser(user_id, user_name, reg_name, "", dp, "", Constant.LOGIN_TYPE_NORMAL,"","","", isReporter);
////                            if (cb_rememberme.isChecked()) {
////                                sharedPref.setLoginDetails(Constant.itemUser, true, editText_password.getText().toString(), Constant.LOGIN_TYPE_NORMAL);
////                            } else {
////                                sharedPref.setRemeber(false);
////                            }
//                            sharedPref.setIsAutoLogin(true);
//                            sharedPref.setCat(user_cat);
//                            Constant.isLogged = true;
//
//                            OneSignal.sendTag("user_id", user_id);
//
//                            if (from.equals("app")) {
//                                finish();
//                            } else {
////                                openMainActivity();
//                                Intent intent=new Intent(UpiActivity.this,IDcardActivity.class);
//                                startActivity(intent);
//                            }
//                        }
//                        Toast.makeText(UpiActivity.this, message, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(UpiActivity.this, getString(R.string.err_server_no_conn), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }, methods.getAPIRequest(Constant.METHOD_LOGIN, 0,"","","",Constant.LOGIN_TYPE_NORMAL,"","","","",reg_email, reg_pass,"","","","", null, null));
//            loadLogin.execute();
//        } else {
//            Toast.makeText(UpiActivity.this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void openMainActivity() {
//        Intent intent;
//        if(!sharedPref.getIsSelectCatShown()) {
//            intent = new Intent(UpiActivity.this, SelectCategoriesActivity.class);
//            intent.putExtra("from","");
//        } else {
//            intent = new Intent(UpiActivity.this, MainActivity.class);
//        }
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        startActivity(intent);
//        finish();
//    }

//    }
}