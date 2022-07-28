package com.urbantech.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.NativeAd;
import com.urbantech.AsyncTask.LoadNews;
import com.urbantech.adapter.AdapterNewsByCat;
import com.urbantech.padmashali.ActivitySearch;
import com.urbantech.padmashali.R;
import com.urbantech.interfaces.NewsListener;
import com.urbantech.item.ItemNews;
import com.urbantech.utils.Constant;
import com.urbantech.utils.EndlessRecyclerViewScrollListener;
import com.urbantech.utils.Methods;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentNewsByCat extends Fragment {

    private Methods methods;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private AdapterNewsByCat myAdapter;
    private ArrayList<ItemNews> arrayList;
    private int position;
    private Boolean isLoaded = false, isVisible = false;
    private CircularProgressBar progressBar;

    private TextView textView_empty;
    private AppCompatButton button_try;
    private LinearLayout ll_empty;
    private String errr_msg;
    private int page = 1, totalArraySize = 0;
    private Boolean isOver = false, isScroll = false;

    public FragmentNewsByCat newInstance(int pos) {
        FragmentNewsByCat fragment = new FragmentNewsByCat();
        Bundle args = new Bundle();
        args.putInt("someInt", pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news_by_cat, container, false);

        methods = new Methods(getActivity());


        position = getArguments().getInt("someInt", 0);
        arrayList = new ArrayList<>();

        progressBar = v.findViewById(R.id.progressBar_news);

        ll_empty = v.findViewById(R.id.ll_empty);
        textView_empty = v.findViewById(R.id.textView_empty_msg);
        button_try = v.findViewById(R.id.button_empty_try);

        recyclerView = v.findViewById(R.id.recyclerView_newsbycat);
        gridLayoutManager = new GridLayoutManager(getActivity(), 1);
//        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                return myAdapter.isHeader(position) ? gridLayoutManager.getSpanCount() : 1;
//            }
//        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
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

        if (isVisible && !isLoaded) {
            loadNewsData();
        }

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewsData();
            }
        });

        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_tv, menu);

        if (!Constant.channelStatus) {
            menu.findItem(R.id.menu_tv).setVisible(false);
        }

        MenuItem item = menu.findItem(R.id.menu_search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_tv:
                methods.openTV();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            if (!s.trim().equals("") && getActivity() != null) {
                Intent intent = new Intent(getActivity(), ActivitySearch.class);
                startActivity(intent);
            }
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {

            return false;
        }
    };

    private void loadNewsData() {
        if (methods.isNetworkAvailable()) {
            LoadNews loadNews = new LoadNews(new NewsListener() {
                @Override
                public void onStart() {
                    if (arrayList.size() == 0) {
                        ll_empty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemNews> arrayListNews, int totalNews) {
                    if (getActivity() != null) {
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
                }
            }, methods.getAPIRequest(Constant.METHOD_NEWS_BY_CAT, page, "", "", "", "", Constant.arrayList_cat.get(position).getId(), "", "", "", "", "", Constant.itemUser.getId(), "",  "","","","","","","","","",null, null));
            loadNews.execute();
        } else {
            isOver = true;
            errr_msg = getString(R.string.err_internet_not_conn);
            setEmpty();
        }

        isLoaded = true;
    }

    private void loadNativeAds() {
        if (Constant.isNativeAd && arrayList.size() >= 10) {
            AdLoader.Builder builder = new AdLoader.Builder(getActivity(), Constant.nativeAdID);
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

    private void setAdapter() {
        if (!isScroll) {
            myAdapter = new AdapterNewsByCat(getActivity(), arrayList,false);
            recyclerView.setAdapter(myAdapter);
            setEmpty();

            loadNativeAds();
        } else {
            myAdapter.notifyDataSetChanged();
        }
    }

    public void setEmpty() {
        progressBar.setVisibility(View.GONE);
        if (arrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            recyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        isVisible = isVisibleToUser;
        if (isVisibleToUser && isAdded() && !isLoaded) {
            loadNewsData();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroy() {
        if (myAdapter != null) {
            myAdapter.destroyNativeAds();
        }
        super.onDestroy();
    }
}