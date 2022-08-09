package com.urbantech.padmashali;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.urbantech.AsyncTask.LoadNews;
import com.urbantech.adapter.AdapterUserNews;
import com.urbantech.eventbus.EventDelete;
import com.urbantech.eventbus.GlobalBus;
import com.urbantech.interfaces.NewsListener;
import com.urbantech.item.ItemNews;
import com.urbantech.utils.Constant;
import com.urbantech.utils.EndlessRecyclerViewScrollListener;
import com.urbantech.utils.Methods;
import com.urbantech.utils.MySingleton;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class UserNewsActivity extends AppCompatActivity {

    Toolbar toolbar;
    private Methods methods;
    RecyclerView rv_profile;
    private AdapterUserNews myAdapter;
    private ArrayList<ItemNews> arrayList;
    private TextView textView_empty;
    private AppCompatButton button_try;
    private LinearLayout ll_empty;
    private String errr_msg;
    private ProgressDialog progressDialog;
    private CircularProgressBar progressBar;
    private int page = 1, totalArraySize = 0;
    private Boolean isOver = false, isScroll = false;

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
        setContentView(R.layout.activity_user_news);

        toolbar = findViewById(R.id.toolbar_user_news);
        toolbar.setTitle(getResources().getString(R.string.user_news));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        methods = new Methods(this);

        arrayList = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        rv_profile=findViewById(R.id.recycler_user_news);
        progressBar = findViewById(R.id.progressBar_user_news);

        ll_empty = findViewById(R.id.ll_empty);
        textView_empty = findViewById(R.id.textView_empty_msg);
        button_try = findViewById(R.id.button_empty_try);
        errr_msg = getString(R.string.no_news_found);

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

        loadNewsData();

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
            }, methods.getAPIRequest(Constant.METHOD_USER_UPLOAD_LIST, page, "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "",  "","","","","","","","","","","","","","","","","","","","","",null, null));

            loadNews.execute();
        } else {
            isOver = true;
            errr_msg = getString(R.string.err_internet_not_conn);
            setEmpty();
        }
    }

    public void setAdapter() {
        try {
            if (!isScroll) {
                myAdapter = new AdapterUserNews(UserNewsActivity.this, arrayList, true);
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


    @Override
    public void onResume() {
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