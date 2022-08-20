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

        reference_number=getRandomNumberString();

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

}