package com.urbantech.padmashali;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.button.MaterialButton;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.urbantech.AsyncTask.LoadApplyReporter;
import com.urbantech.AsyncTask.LoadNews;
import com.urbantech.AsyncTask.LoadProfile;
import com.urbantech.adapter.AdapterUserNews;
import com.urbantech.eventbus.EventDelete;
import com.urbantech.eventbus.GlobalBus;
import com.urbantech.interfaces.NewsListener;
import com.urbantech.interfaces.SuccessListener;
import com.urbantech.item.ItemNews;
import com.urbantech.utils.Constant;
import com.urbantech.utils.EndlessRecyclerViewScrollListener;
import com.urbantech.utils.Methods;
import com.urbantech.utils.MySingleton;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    private Methods methods;
    private RecyclerView rv_profile;
    private AdapterUserNews myAdapter;
    private ArrayList<ItemNews> arrayList;
    private RoundedImageView iv_prof;
    private TextView textView_email, textView_mobile, textView_notlog, tv_hello;
    private ProgressDialog progressDialog;
    //    private CollapsingToolbarLayout collapsing;
    private MaterialButton btn_apply_reporter, btn_add_news,btn_id_card;
    private CircularProgressBar progressBar;
    private int page = 1, totalArraySize = 0;
    private Boolean isOver = false, isScroll = false;
    private Dialog dialog_request;

    private TextView textView_empty,edit_profile;
    private AppCompatButton button_try;
    private LinearLayout ll_empty;
    private String errr_msg;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.toolbar_pro);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        methods = new Methods(this);

        arrayList = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        ll_empty = findViewById(R.id.ll_empty);
        textView_empty = findViewById(R.id.textView_empty_msg);
        button_try = findViewById(R.id.button_empty_try);
        errr_msg = getString(R.string.no_news_found);

//        collapsing = findViewById(R.id.collapsing);
//        collapsing.setTitle(Constant.itemUser.getName());

        btn_add_news = findViewById(R.id.btn_details_add_news);
        btn_apply_reporter = findViewById(R.id.btn_profile_apply_reporter);
        rv_profile = findViewById(R.id.rv_profile);
        iv_prof = findViewById(R.id.iv_prof);
        progressBar = findViewById(R.id.progressBar_prof);
        textView_email = findViewById(R.id.tv_prof_email);
        textView_mobile = findViewById(R.id.tv_prof_mobile);
        textView_notlog = findViewById(R.id.textView_notlog);
        tv_hello = findViewById(R.id.tv_details_hello);

        btn_id_card=findViewById(R.id.btn_id_card);

        tv_hello.setText(getString(R.string.hello) + " " + Constant.itemUser.getName() + ",");

//        LinearLayout ll_adView = findViewById(R.id.ll_adView);
//        methods.showBannerAd(ll_adView);

        edit_profile=findViewById(R.id.edit_profile);
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProfileActivity.this,ProfileEditActivity.class);
                startActivity(intent);
            }
        });

        btn_id_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProfileActivity.this,IDActivity.class);
                startActivity(intent);
            }
        });



        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv_profile.setLayoutManager(llm);
        rv_profile.setItemAnimator(new DefaultItemAnimator());

        rv_profile.addOnScrollListener(new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isScroll = true;
                            loadNewsData();
                        }
                    }, 0);
                } else {
                    myAdapter.hideHeader();
                }
            }
        });

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewsData();
            }
        });

        btn_add_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, UploadsNewsActivity.class);
                startActivity(intent);
            }
        });

        if (Constant.itemUser != null && !Constant.itemUser.getId().equals("")) {
            if (Constant.itemUser.getIsReporter()) {
                btn_add_news.setVisibility(View.VISIBLE);
                if (methods.isNetworkAvailable()) {
                    loadUserProfile();
                } else {
                    setEmpty(true, getString(R.string.err_internet_not_conn));
                }
            } else {
                btn_add_news.setVisibility(View.GONE);
                loadUserProfile();

                btn_apply_reporter.setVisibility(View.VISIBLE);
                btn_apply_reporter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Constant.itemUser.getLoginType().equals(Constant.LOGIN_TYPE_NORMAL)) {
                            loadApplyReporter("", "");
                        } else {
                            openAdminDetailDialog();
                        }
                    }
                });
            }
        } else {
            setEmpty(true, getString(R.string.not_log));
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_profile, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
//            case R.id.item_profile_edit:
//                Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
//                startActivity(intent);
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserProfile() {
        LoadProfile loadProfile = new LoadProfile(new SuccessListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, String registerSuccess, String message) {
                progressDialog.dismiss();
                if (success.equals("1")) {
                    switch (registerSuccess) {
                        case "1":
                            setVariables();
                            break;
                        case "-2":
                            methods.getInvalidUserDialog(message);
                            break;
                        case "-1":
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            break;
                        default:
                            setEmpty(false, message);
                            break;
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }

//                if (Constant.itemUser.getIsReporter()) {
                    loadNewsData();
//                }
            }
        }, methods.getAPIRequest(Constant.METHOD_PROFILE, 0, "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "",  "","","","","","","","","",null, null));
        loadProfile.execute();
    }

    private void openAdminDetailDialog() {
        dialog_request = new Dialog(ProfileActivity.this);
        dialog_request.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_request.setContentView(R.layout.layout_dialog_admin);
        if (getString(R.string.isRTL).equals("true")) {
            dialog_request.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog_request.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
        dialog_request.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        AppCompatButton et_button = dialog_request.findViewById(R.id.btn_admin_submit);
        AppCompatEditText et_email = dialog_request.findViewById(R.id.et_admin_email);
        AppCompatEditText et_password = dialog_request.findViewById(R.id.et_admin_password);

        et_email.setText(Constant.itemUser.getEmail());
        if (!et_email.getText().toString().trim().equals("")) {
            et_email.setEnabled(false);
        }

        et_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_email.getText().toString().trim().equals("")) {
                    Toast.makeText(ProfileActivity.this, getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
                } else if (et_password.getText().toString().trim().equals("")) {
                    Toast.makeText(ProfileActivity.this, getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
                } else {
                    loadApplyReporter(et_email.getText().toString(), et_password.getText().toString());
                }
            }
        });

        dialog_request.show();

    }

    private void loadNewsData() {
        if (methods.isNetworkAvailable()) {
            LoadNews loadNews = new LoadNews(new NewsListener() {
                @Override
                public void onStart() {
                    if (arrayList.size() == 0) {
                        ll_empty.setVisibility(View.GONE);
                        rv_profile.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemNews> arrayListNews, int totalNews) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            if (arrayListNews.size() == 0) {
                                isOver = true;
                                try {
                                    myAdapter.hideHeader();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                totalArraySize = totalArraySize + arrayListNews.size();
                                for (int i = 0; i < arrayListNews.size(); i++) {
                                    arrayList.add(arrayListNews.get(i));

                                    if (Constant.isNativeAd) {
                                        int abc = arrayList.lastIndexOf(null);
                                        if (((arrayList.size() - (abc + 1)) % Constant.nativeAdShow == 0) && (arrayListNews.size() - 1 != i || totalArraySize != totalNews)) {
                                            arrayList.add(null);
                                        }
                                    }
                                }

                                page = page + 1;
                            }
                            errr_msg = getString(R.string.no_news_found);
                        } else {
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                        }
                    } else {
                        errr_msg = getString(R.string.err_server_no_conn);
                    }
                    setAdapter();
                    progressBar.setVisibility(View.GONE);
                }
            }, methods.getAPIRequest(Constant.METHOD_USER_UPLOAD_LIST, page, "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "",  "","","","","","","","","",null, null));

            loadNews.execute();
        } else {
            isOver = true;
            errr_msg = getString(R.string.err_internet_not_conn);
            setEmpty();
        }
    }

    private void loadApplyReporter(String email, String password) {
        if (methods.isNetworkAvailable()) {

            LoadApplyReporter loadApplyReporter = new LoadApplyReporter(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (registerSuccess.equals("1")) {
                            btn_apply_reporter.setText(getString(R.string.applied_reporter));
                        } else {
                            btn_apply_reporter.setText(message);
                        }
                        btn_apply_reporter.setEnabled(false);
                        if (dialog_request != null && dialog_request.isShowing()) {
                            dialog_request.dismiss();
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, getString(R.string.err_server_no_conn), Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_APPLY_REPORTER, 0, "", "", "", "", "", "", email, password, "", "", Constant.itemUser.getId(), "",  "","","","","","","","","",null, null));
            loadApplyReporter.execute();
        } else {
            Toast.makeText(this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNativeAds() {
        if (Constant.isNativeAd && arrayList.size() >= 10) {
            AdLoader.Builder builder = new AdLoader.Builder(ProfileActivity.this, Constant.nativeAdID);
            AdLoader adLoader = builder.forNativeAd(
                    new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            // A native ad loaded successfully, check if the ad loader has finished loading
                            // and if so, insert the ads into the list.
                            try {
                                myAdapter.addAds(nativeAd);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).build();

            // Load the Native Express ad.
            adLoader.loadAds(new AdRequest.Builder().build(), 5);
        }
    }

    public void setAdapter() {
        try {
            if (!isScroll) {
                myAdapter = new AdapterUserNews(ProfileActivity.this, arrayList, true);
                rv_profile.setAdapter(myAdapter);
                setEmpty();
//                loadNativeAds();
            } else {
                myAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setEmpty() {
        progressBar.setVisibility(View.GONE);
        if (arrayList.size() > 0) {
            rv_profile.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            rv_profile.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    public void setVariables() {
        textView_mobile.setText(Constant.itemUser.getMobile());
        textView_email.setText(Constant.itemUser.getName());

//        collapsing.setTitle(Constant.itemUser.getName());
        tv_hello.setText(getString(R.string.hello) + " " + Constant.itemUser.getName() + ",");

        if (!Constant.itemUser.getImage().equals("")) {
            Picasso.get().load(Constant.itemUser.getImage()).placeholder(R.drawable.placeholder_prof).into(iv_prof);
        }
    }

    public void setEmpty(Boolean flag, String message) {
        if (flag) {
            textView_notlog.setText(message);
            textView_notlog.setVisibility(View.VISIBLE);
        } else {
            textView_notlog.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        if (Constant.isUpdate) {
            Constant.isUpdate = false;
            setVariables();
        }
        super.onResume();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onDelete(EventDelete eventDelete) {
        try {
            arrayList.clear();
            arrayList.addAll(MySingleton.getInstance().getNews());
            myAdapter.notifyDataSetChanged();
            GlobalBus.getBus().removeStickyEvent(eventDelete);
        } catch (Exception e) {
            GlobalBus.getBus().removeStickyEvent(eventDelete);
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onStop() {
        GlobalBus.getBus().unregister(this);
        super.onStop();
    }
}