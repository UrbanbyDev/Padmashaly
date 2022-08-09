package com.urbantech.padmashali;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.urbantech.AsyncTask.LoadCommentDelete;
import com.urbantech.AsyncTask.LoadCommentPost;
import com.urbantech.AsyncTask.LoadFav;
import com.urbantech.AsyncTask.LoadLike;
import com.urbantech.AsyncTask.LoadReport;
import com.urbantech.AsyncTask.LoadSingleNews;
import com.urbantech.AsyncTask.LoadStatus;
import com.urbantech.adapter.AdapterComments;
import com.urbantech.adapter.AdapterNewsHome;
import com.urbantech.eventbus.EventDelete;
import com.urbantech.eventbus.GlobalBus;
import com.urbantech.fragments.FragmentComment;
import com.urbantech.interfaces.ClickListener;
import com.urbantech.interfaces.CommentDeleteListener;
import com.urbantech.interfaces.PostCommentListener;
import com.urbantech.interfaces.SingleNewsListener;
import com.urbantech.interfaces.SuccessListener;
import com.urbantech.item.ItemComment;
import com.urbantech.item.ItemEventBus;
import com.urbantech.item.ItemNews;
import com.urbantech.utils.Constant;
import com.urbantech.utils.DBHelper;
import com.urbantech.utils.Methods;
import com.urbantech.utils.MySingleton;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class NewsDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    DBHelper dbHelper;
    private ArrayList<ItemComment> arrayListComments;
    private Boolean isFromPush = false;
    SliderLayout sliderLayout;
    Methods methods;
    String nid;
    RecyclerView rv_comment;
    AdapterComments adapterComm;
    EditText editText_comment;
    ImageView iv_post_comment;
    RoundedImageView iv_user;
    LinearLayout ll_related_news;
    RecyclerView rv_relatedNews;
    ArrayList<ItemNews> arrayList_relatednews;
    private AdapterNewsHome adapterRelatedNews;
    TextView textView_views, textView_title, textView_cat, textView_comment, textView_empty_comment, tv_related_all;
    WebView webView;
    CircularProgressBar progressBar;
    MenuItem menuItem;
    AppBarLayout mAppBarLayout;
    Menu menu;
    BottomSheetDialog dialog_setas;
    ProgressDialog progressDialog;
    Boolean isUser = false;

    private View youtubeFragment;
    private YouTubePlayer ytPlayer;
    private boolean liked=false;

    FloatingActionButton fab_like;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        dbHelper = new DBHelper(this);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        isUser = getIntent().getBooleanExtra("isuser", false);

        progressDialog = new ProgressDialog(NewsDetailsActivity.this);
        progressDialog.setMessage(getString(R.string.loading));

        arrayList_relatednews = new ArrayList<>();
        arrayListComments = new ArrayList<>();

        collapsingToolbar = findViewById(R.id.collapsing);
        toolbar = findViewById(R.id.toolbar_news);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        LinearLayout ll_adView = findViewById(R.id.adView);
//        methods.showBannerAd(ll_adView);

        ll_related_news = findViewById(R.id.ll_related_news);
        rv_relatedNews = findViewById(R.id.rv_realated_news);
        tv_related_all = findViewById(R.id.tv_related_all);
        sliderLayout = findViewById(R.id.slider);
        youtubeFragment = findViewById(R.id.youtubeFragment);
        mAppBarLayout = findViewById(R.id.appbar);
        FloatingActionButton fab_comment = findViewById(R.id.fab_comment);
        FloatingActionButton fab_share = findViewById(R.id.fab_share);
        fab_like=findViewById(R.id.fab_like_dtls);
        rv_comment = findViewById(R.id.rv_comment_details);
        editText_comment = findViewById(R.id.et_details_comment);
        textView_empty_comment = findViewById(R.id.tv_empty_comment);
        textView_comment = findViewById(R.id.tv_viewall_comment);
        iv_post_comment = findViewById(R.id.iv_details_comment);
        iv_user = findViewById(R.id.iv_details_user);

        textView_views = findViewById(R.id.tv_views_details);
        textView_title = findViewById(R.id.tv_title_detail);
        textView_cat = findViewById(R.id.tv_cat_detail);
        webView = findViewById(R.id.webView_news_details);
        webView.getSettings().setJavaScriptEnabled(true);
        progressBar = findViewById(R.id.pb_details);


        textView_title.setTypeface(methods.getFontMedium());

        if (!Constant.itemUser.getImage().equals("")) {
            Picasso.get().load(Constant.itemUser.getImage()).placeholder(R.drawable.comment2).into(iv_user);
        } else {
            Picasso.get().load("null").placeholder(R.drawable.comment2).into(iv_user);
        }

        rv_comment.setLayoutManager(new LinearLayoutManager(NewsDetailsActivity.this));
        rv_relatedNews.setLayoutManager(new LinearLayoutManager(NewsDetailsActivity.this, RecyclerView.HORIZONTAL, false));

        iv_post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Constant.isLogged) {
                    methods.openLogin();
                } else if (editText_comment.getText().toString().trim().isEmpty()) {
                    Toast.makeText(NewsDetailsActivity.this, getResources().getString(R.string.enter_comment), Toast.LENGTH_SHORT).show();
                } else {
                    if (methods.isNetworkAvailable()) {
//                        Toast.makeText(NewsDetailsActivity.this, "This function is disabled in demo app", Toast.LENGTH_SHORT).show();
                        loadPostComment();
                    } else {
                        Toast.makeText(NewsDetailsActivity.this, getResources().getString(R.string.conn_net_post_comment), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        tv_related_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsDetailsActivity.this, NewsByRelatedActivity.class);
                startActivity(intent);
            }
        });

        textView_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCommentList();
            }
        });

        fab_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentComment fragmentComment = new FragmentComment();
                fragmentComment.show(getSupportFragmentManager(), fragmentComment.getTag());
            }
        });

        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.shareNews(Constant.itemNewsCurrent);
            }
        });



        fab_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.isLogged) {
                    loadLike(Constant.itemNewsCurrent.getId());
                } else {
                    Intent intent = new Intent(NewsDetailsActivity.this, LoginActivity.class);
                    intent.putExtra("from", "app");
                    startActivity(intent);
                }
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    showOption();
                } else if (isShow) {
                    isShow = false;
                    hideOption();
                }
            }
        });

        if (!Constant.pushPostID.equals("0")) {
            isFromPush = true;
            nid = Constant.pushPostID;
            Constant.selected_news_pos = 0;
        } else {
            dbHelper.addRecentNews(Constant.itemNewsCurrent);
            setVariables();
            nid = Constant.itemNewsCurrent.getId();
        }

        loadSingleNews();

        try {
            setFavImage(Constant.itemNewsCurrent.getIsFav(), menuItem);
            setLikeImage(Constant.itemNewsCurrent.getIsLike(),fab_like);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLikeImage(Boolean isLike, FloatingActionButton fab_like) {
        if (isLike) {
            fab_like.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_hover));
        } else {
            fab_like.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_details, menu);
        menuItem = menu.findItem(R.id.item_fav);

        if (isUser) {
            menu.findItem(R.id.item_delete).setVisible(true);
            menu.findItem(R.id.item_edit).setVisible(true);
        }

        MenuItem menuItem_comm = menu.findItem(R.id.item_comment);

        Drawable drawable = menuItem_comm.getIcon();
        drawable.mutate();
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        try {
            setFavImage(Constant.itemNewsCurrent.getIsFav(), menuItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == android.R.id.home) {
            onBackPressed();
        } else if (itemId == R.id.item_share) {
            methods.shareNews(Constant.itemNewsCurrent);
        } else if (itemId == R.id.item_comment) {
            openCommentList();
        } else if (itemId == R.id.item_report) {
            showReportDialog();
        } else if (itemId == R.id.item_fav) {
            if (Constant.isLogged) {
                loadFav(Constant.itemNewsCurrent.getId());
            } else {
                Intent intent = new Intent(NewsDetailsActivity.this, LoginActivity.class);
                intent.putExtra("from", "app");
                startActivity(intent);
            }
        } else if (itemId == R.id.item_delete) {
            showDeleteDialog();
        } else if (itemId == R.id.item_edit) {
            Intent intent = new Intent(NewsDetailsActivity.this, UploadedNewsEditActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadLike(String id) {
        if (methods.isNetworkAvailable()) {
            LoadLike loadLike = new LoadLike(new SuccessListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String likeSuccess, String message) {
                    if (success.equals("1")) {
                        if (likeSuccess.equals("1")) {
                            Constant.itemNewsCurrent.setIsLike(true);
                        } else {
                            Constant.itemNewsCurrent.setIsLike(false);
                        }
                        setLikeImage(Constant.itemNewsCurrent.getIsLike(), fab_like);

                        methods.showToast(message);
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_LIKE, 0, "", id, "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "",  "","","","","","","","","","","","","","","","","","","","","",null, null));
            loadLike.execute();
        } else {
            methods.showToast(getString(R.string.err_internet_not_conn));
        }
    }

    private void loadFav(String qid) {
        if (methods.isNetworkAvailable()) {
            LoadFav loadFav = new LoadFav(new SuccessListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String favSuccess, String message) {
                    if (success.equals("1")) {
                        if (favSuccess.equals("1")) {
                            Constant.itemNewsCurrent.setIsFav(true);
                        } else {
                            Constant.itemNewsCurrent.setIsFav(false);
                        }
                        setFavImage(Constant.itemNewsCurrent.getIsFav(), menuItem);

                        methods.showToast(message);
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_FAV, 0, "", qid, "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "",  "","","","","","","","","","","","","","","","","","","","","",null, null));
            loadFav.execute();
        } else {
            methods.showToast(getString(R.string.err_internet_not_conn));
        }
    }

    private void initSlider() {
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(3000);

        sliderLayout.removeAllSliders();

        DefaultSliderView textSlider = new DefaultSliderView(this);
        // initialize a SliderLayout
        textSlider
                .image(Constant.itemNewsCurrent.getImage())
                .setScaleType(BaseSliderView.ScaleType.CenterCrop);

        //add your extra information
        textSlider.bundle(new Bundle());
        textSlider.getBundle().putString("extra", Constant.itemNewsCurrent.getImage());

        sliderLayout.addSlider(textSlider);
    }

    private void setSlider() {
        if (Constant.itemNewsCurrent.getGalleryList() != null) {
            if (Constant.itemNewsCurrent.getGalleryList().size() == 0) {
                sliderLayout.stopAutoCycle();
                sliderLayout.setEnabled(false);
            }
            for (int i = 0; i < Constant.itemNewsCurrent.getGalleryList().size(); i++) {
                DefaultSliderView textSliderView = new DefaultSliderView(this);
                // initialize a SliderLayout
                textSliderView
                        .image(Constant.itemNewsCurrent.getGalleryList().get(i).getImage())
                        .setScaleType(BaseSliderView.ScaleType.CenterCrop);

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", Constant.itemNewsCurrent.getGalleryList().get(i).getImage());

                sliderLayout.addSlider(textSliderView);
            }
        }
    }

    private void setVariables() {
        initSlider();
        if (Constant.itemNewsCurrent.getType().equals("video")) {
            youtubeFragment.setVisibility(View.VISIBLE);
            initYoutube();
        } else {
            youtubeFragment.setVisibility(View.GONE);
        }

        setLikeImage(Constant.itemNewsCurrent.getIsLike(),fab_like);

        textView_cat.setText(Constant.itemNewsCurrent.getCatName());
        textView_title.setText(Constant.itemNewsCurrent.getHeading());
        textView_views.setText(Constant.itemNewsCurrent.getTotalViews()+" Views");

        String myCustomStyleString;
        if (methods.isDarkMode()) {
            myCustomStyleString = "<style channelType=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/pop_med.ttf\")}body,* {background-color:#000; color:#fff; font-family: MyFont; font-size: 13px;text-align: justify;}img{max-width:100%;height:auto; border-radius: 3px;}</style>";
        } else {
            myCustomStyleString = "<style channelType=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/pop_med.ttf\")}body,* {background-color:#fff; color:#000; font-family: MyFont; font-size: 13px;text-align: justify;}img{max-width:100%;height:auto; border-radius: 3px;}</style>";
        }

        String htmlString = "";
        if(!getString(R.string.isRTL).equals("true")) {
            htmlString = myCustomStyleString + "<div>" + Constant.itemNewsCurrent.getDesc() + "</div>";
        } else {
            htmlString = "<html dir=\"rtl\" lang=\"\"><body>" + myCustomStyleString + "<div>" + Constant.itemNewsCurrent.getDesc() + "</div>" + "</body></html>";
        }


        webView.loadDataWithBaseURL("", htmlString, "text/html", "utf-8", null);
    }

    private void initYoutube() {
        YouTubePlayerFragment youtubeFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtubeFragment);
        youtubeFragment.initialize(BuildConfig.API_KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        ytPlayer = youTubePlayer;
                        youTubePlayer.loadVideo(Constant.itemNewsCurrent.getVideoId());
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {
                        ytPlayer = null;
                    }
                });
    }

    private void showDeleteDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewsDetailsActivity.this, R.style.AlertDialogTheme);
        alertDialog.setTitle(getString(R.string.delete));
        alertDialog.setMessage(getString(R.string.sure_delete));
        alertDialog.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loadDelete();
            }
        });
        alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();
    }

    public void loadDelete() {
        if (methods.isNetworkAvailable()) {
            LoadStatus loadStatus = new LoadStatus(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (registerSuccess.equals("1")) {


                            GlobalBus.getBus().postSticky(new EventDelete(Constant.selected_news_pos));
                            MySingleton.getInstance().getNews().remove(Constant.selected_news_pos);
                            NewsDetailsActivity.this.finish();
//                            customPagerAdapter.notifyDataSetChanged();
                        }
                        Toast.makeText(NewsDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewsDetailsActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_DELETE_NEWS, 0, "", Constant.itemNewsCurrent.getId(), "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "",  "","","","","","","","","","","","","","","","","","","","","",null, null));
            loadStatus.execute();
        } else {
            Toast.makeText(this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    private void setCommentAdapter() {
        adapterComm = new AdapterComments(NewsDetailsActivity.this, arrayListComments, new ClickListener() {
            @Override
            public void onClick(int pos) {
                loadDeleteComment(pos);
            }
        });
        rv_comment.setAdapter(adapterComm);
        progressBar.setVisibility(View.GONE);

        setEmpty();
    }

    private void setRelatedAdapter() {
        if (arrayList_relatednews.size() > 0) {
            adapterRelatedNews = new AdapterNewsHome(NewsDetailsActivity.this, arrayList_relatednews);
            rv_relatedNews.setAdapter(adapterRelatedNews);

            ll_related_news.setVisibility(View.VISIBLE);
        } else {
            ll_related_news.setVisibility(View.GONE);
        }
    }

    private void setEmpty() {
        if (arrayListComments.size() == 0) {
            rv_comment.setVisibility(View.GONE);
            textView_empty_comment.setVisibility(View.VISIBLE);
        } else {
            rv_comment.setVisibility(View.VISIBLE);
            textView_empty_comment.setVisibility(View.GONE);
        }
    }

    private void loadDeleteComment(final int pos) {
        LoadCommentDelete loadCommentDelete = new LoadCommentDelete(new CommentDeleteListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, String isDeleted, String message, ItemComment itemComment) {
                progressDialog.dismiss();
                if (success.equals("1")) {
                    if (isDeleted.equals("1")) {
                        arrayListComments.remove(pos);
                        adapterComm.notifyItemRemoved(pos);
                        if (itemComment != null && !itemComment.getId().equals("null")) {
                            boolean isNew = true;

                            for (int i = 0; i < arrayListComments.size(); i++) {
                                if (arrayListComments.get(i).getId().equals(itemComment.getId())) {
                                    isNew = false;
                                    break;
                                }
                            }
                            if (isNew) {
                                arrayListComments.add(itemComment);
                                adapterComm.notifyItemInserted(arrayListComments.size() - 1);
                            }
                        }
                        setEmpty();
                    }
                    Toast.makeText(NewsDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewsDetailsActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }
        }, methods.getAPIRequest(Constant.METHOD_DELETE_COMMENTS, 0, "", Constant.itemNewsCurrent.getId(), "", "", arrayListComments.get(pos).getId(), "", "", "", "", "", "", "",  "","","","","","","","","","","","","","","","","","","","","",null, null));
        loadCommentDelete.execute();
    }

    private void loadSingleNews() {
        if (methods.isNetworkAvailable()) {
            LoadSingleNews loadSingleNews = new LoadSingleNews(new SingleNewsListener() {
                @Override
                public void onStart() {
                    arrayListComments.clear();
                }

                @Override
                public void onEnd(String success, ItemNews itemNews, ArrayList<ItemComment> arrayListComm, ArrayList<ItemNews> arrayListRelated) {
                    if (success.equals("1")) {
                        arrayListComments.addAll(arrayListComm);
                        arrayList_relatednews.addAll(arrayListRelated);
                        Constant.itemNewsCurrent = itemNews;

                        if (isFromPush) {
                            dbHelper.addRecentNews(Constant.itemNewsCurrent);
                            setVariables();
                        }
                        setSlider();

                        setRelatedAdapter();
                        setCommentAdapter();
                    } else {
                        Toast.makeText(NewsDetailsActivity.this, getResources().getString(R.string.err_server_no_conn), Toast.LENGTH_SHORT).show();
                    }
                }

            }, methods.getAPIRequest(Constant.METHOD_SINGLE_NEWS, 0, "", nid, "", "", "", "", "", "", "", "", "", "",  "","","","","","","","","","","","","","","","","","","","","",null, null));
            loadSingleNews.execute();
        } else {
            Toast.makeText(NewsDetailsActivity.this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    private void hideOption() {
        MenuItem item = menu.findItem(R.id.item_share);
        MenuItem item_comm = menu.findItem(R.id.item_comment);
        item.setVisible(false);
        item_comm.setVisible(false);
        collapsingToolbar.setTitle("");
    }

    private void showOption() {
        MenuItem item = menu.findItem(R.id.item_share);
        MenuItem item_comm = menu.findItem(R.id.item_comment);
        item.setVisible(true);
        item_comm.setVisible(true);
        collapsingToolbar.setTitle(Constant.itemNewsCurrent.getCatName());
    }

    private void openCommentList() {
        FragmentComment fragmentComment = new FragmentComment();
        fragmentComment.show(getSupportFragmentManager(), fragmentComment.getTag());
    }

    private void loadPostComment() {
        LoadCommentPost loadCommentPost = new LoadCommentPost(new PostCommentListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(String success, String isCommentPosted, String message, ItemComment itemComment) {
                if (success.equals("1")) {

                    if (isCommentPosted.equals("1")) {
                        arrayListComments.add(0, itemComment);
                        adapterComm.notifyDataSetChanged();
                        rv_comment.setVisibility(View.VISIBLE);
                        textView_empty_comment.setVisibility(View.GONE);
                        editText_comment.setText("");
                        rv_comment.smoothScrollToPosition(0);
                    }
                    Toast.makeText(NewsDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewsDetailsActivity.this, getString(R.string.err_server_no_conn), Toast.LENGTH_SHORT).show();
                }
            }

        }, methods.getAPIRequest(Constant.METHOD_POST_COMMENTS, 0, "", Constant.itemNewsCurrent.getId(), editText_comment.getText().toString(), "", "", "", "", "", "", "", Constant.itemUser.getId(), "",  "","","","","","","","","","","","","","","","","","","","","",null, null));
        loadCommentPost.execute();
    }

    public void loadReportSubmit(String report) {
        if (methods.isNetworkAvailable()) {
            LoadReport loadReport = new LoadReport(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (registerSuccess.equals("1")) {
                            try {
                                dialog_setas.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(NewsDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(NewsDetailsActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_REPORT, 0, "", Constant.itemNewsCurrent.getId(), "", "", "", "", "", "", "", "", Constant.itemUser.getId(), report,  "","","","","","","","","","","","","","","","","","","","","",null, null));
            loadReport.execute();
        } else {
            Toast.makeText(this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    private void showReportDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.layout_report, null);

        dialog_setas = new BottomSheetDialog(NewsDetailsActivity.this);
        dialog_setas.setContentView(view);
        dialog_setas.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog_setas.show();

        final EditText editText_report;
        Button button_submit;

        button_submit = dialog_setas.findViewById(R.id.button_report_submit);
        editText_report = dialog_setas.findViewById(R.id.et_report);

        button_submit.setBackground(methods.getRoundDrawable(getResources().getColor(R.color.colorPrimary)));

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_report.getText().toString().trim().isEmpty()) {
                    Toast.makeText(NewsDetailsActivity.this, getString(R.string.enter_report), Toast.LENGTH_SHORT).show();
                } else {
                    if (Constant.isLogged) {
//                        Toast.makeText(NewsDetailsActivity.this, "This function is disabled in demo app", Toast.LENGTH_SHORT).show();
                        loadReportSubmit(editText_report.getText().toString());
                    } else {
                        methods.clickLogin();
                    }
                }
            }
        });


    }

    public void setFavImage(Boolean isFav, MenuItem menuItem) {
        if (isFav) {
            menuItem.setIcon(getResources().getDrawable(R.drawable.fav_hover));
        } else {
            menuItem.setIcon(getResources().getDrawable(R.drawable.fav));
        }
    }



    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onProfileChange(ItemEventBus itemEventBus) {
        if (itemEventBus.getMessage().equals(getString(R.string.comments)) || itemEventBus.getMessage().equals(getString(R.string.comment_old))) {
            boolean isNew = true;

            for (int i = 0; i < arrayListComments.size(); i++) {
                if (arrayListComments.get(i).getId().equals(itemEventBus.getItemComment().getId())) {
                    isNew = false;
                    break;
                }
            }

            if (isNew) {
                if (itemEventBus.getMessage().equals(getString(R.string.comment_old))) {
                    arrayListComments.add(itemEventBus.getItemComment());
                } else {
                    arrayListComments.add(0, itemEventBus.getItemComment());
                }
                adapterComm.notifyDataSetChanged();
                rv_comment.setVisibility(View.VISIBLE);
                textView_empty_comment.setVisibility(View.GONE);
                editText_comment.setText("");

                rv_comment.smoothScrollToPosition(0);
            }

        } else if (itemEventBus.getMessage().equals(getString(R.string.delete))) {
            if (arrayListComments.get(itemEventBus.getPos()).getId().equals(itemEventBus.getItemComment().getId())) {
                arrayListComments.remove(itemEventBus.getPos());
                adapterComm.notifyDataSetChanged();
                setEmpty();
            }
        }
        GlobalBus.getBus().removeStickyEvent(itemEventBus);
    }

    @Override
    protected void onResume() {
        if (Constant.isNewsUpdated) {
            Constant.isNewsUpdated = false;
            loadSingleNews();
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (Constant.pushPostID.equals("0")) {
            super.onBackPressed();
        } else {
            Constant.pushPostID = "0";
            Intent intent = new Intent(NewsDetailsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}