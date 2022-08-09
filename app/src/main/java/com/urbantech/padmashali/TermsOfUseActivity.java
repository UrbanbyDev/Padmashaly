package com.urbantech.padmashali;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.urbantech.AsyncTask.LoadAbout;
import com.urbantech.interfaces.AboutListener;
import com.urbantech.utils.Constant;
import com.urbantech.utils.Methods;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class TermsOfUseActivity extends AppCompatActivity {

    Toolbar toolbar;
    Methods methods;
    WebView webView;
    CircularProgressBar progressBar;
    Boolean isPrivacy = true;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsofuse);

        isPrivacy = getIntent().getBooleanExtra("isprivacy", true);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        toolbar = this.findViewById(R.id.toolbar_terms);
        if(isPrivacy) {
            toolbar.setTitle(getString(R.string.privacypolicy));
        } else {
            toolbar.setTitle(getString(R.string.terms_of_use));
        }
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = findViewById(R.id.wv_terms);
        progressBar = findViewById(R.id.progressBar_terms);
        webView.getSettings().setJavaScriptEnabled(true);

        if((isPrivacy && Constant.itemAbout.getPrivacy().equalsIgnoreCase("")) || (!isPrivacy && Constant.termsOfUse.equalsIgnoreCase(""))) {
            loadAboutData();
        } else {
            setWebView();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    public void loadAboutData() {
        LoadAbout loadAbout = new LoadAbout(TermsOfUseActivity.this, new AboutListener() {
            @Override
            public void onStart() {
                progressBar.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message) {
                progressBar.setVisibility(View.GONE);
                if (success.equals("1")) {
                    webView.setVisibility(View.VISIBLE);
                    setWebView();
                }
            }
        }, methods.getAPIRequest(Constant.METHOD_APP_DETAILS, 0, "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "",  "","","","","","","","","","","","","","","","","","","","","",null, null));
        loadAbout.execute();
    }

    private void setWebView() {
        String mimeType = "text/html;charset=UTF-8";
        String encoding = "utf-8";

        String dataString;
        if(isPrivacy) {
            dataString = Constant.itemAbout.getPrivacy();
        } else {
            dataString = Constant.termsOfUse;
        }

        String myCustomStyleString;
        if (methods.isDarkMode()) {
            myCustomStyleString = "<style channelType=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/pop_med.ttf\")}body,* {background-color:#000; color:#fff; font-family: MyFont; font-size: 13px;text-align: justify;}img{max-width:100%;height:auto; border-radius: 3px;}</style>";
        } else {
            myCustomStyleString = "<style channelType=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/pop_med.ttf\")}body,* {background-color:#fff; color:#000; font-family: MyFont; font-size: 13px;text-align: justify;}img{max-width:100%;height:auto; border-radius: 3px;}</style>";
        }

        String htmlString = "";
        if(!getString(R.string.isRTL).equals("true")) {
            htmlString = myCustomStyleString + "<div>" + dataString + "</div>";
        } else {
            htmlString = "<html dir=\"rtl\" lang=\"\"><body>" + myCustomStyleString + "<div>" + dataString + "</div>" + "</body></html>";
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            webView.loadData(htmlString, mimeType, encoding);
        } else {
            webView.loadDataWithBaseURL("blarg://ignored", htmlString, mimeType, encoding, "");
        }
    }
}