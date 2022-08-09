package com.urbantech.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.urbantech.AsyncTask.LoadFav;
import com.urbantech.AsyncTask.LoadLike;
import com.urbantech.padmashali.LoginActivity;
import com.urbantech.padmashali.NewsDetailsActivity;
import com.urbantech.padmashali.R;
import com.urbantech.interfaces.InterAdListener;
import com.urbantech.interfaces.SuccessListener;
import com.urbantech.item.ItemNews;
import com.urbantech.padmashali.VideoviewActivity;
import com.urbantech.utils.Constant;
import com.urbantech.utils.Methods;
import com.urbantech.utils.MyBounceInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class AdapterNewsByVideo extends RecyclerView.Adapter {

    private ArrayList<ItemNews> arrayList;
    private Context context;
    private ArrayList<ItemNews> filteredArrayList;
    private NameFilter filter;
    private Methods methods;

    private final int VIEW_PROG = -1;

    private Boolean isAdLoaded = false;
    private List<NativeAd> mNativeAdsAdmob = new ArrayList<>();
    private boolean liked=false;


    public AdapterNewsByVideo(Context context, ArrayList<ItemNews> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        filteredArrayList = arrayList;
        methods = new Methods(context, interAdListener);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_heading, textView_views, textView_cat;
        ImageView imageView_fav, imageView_play;
        RelativeLayout rl_main;
        RoundedImageView imageView;
        FloatingActionButton fab_share,fab_like;

        MyViewHolder(View view) {
            super(view);
            rl_main = view.findViewById(R.id.rl_news);
            textView_heading = view.findViewById(R.id.tv_home_news_title);
            textView_views = view.findViewById(R.id.tv_home_news_views);
            textView_cat = view.findViewById(R.id.tv_home_news_cat);
            imageView = view.findViewById(R.id.imageView_home_latest);
            imageView_fav = view.findViewById(R.id.iv_home_news_fav);
            imageView_play = view.findViewById(R.id.iv_home_news_play);
            fab_share = view.findViewById(R.id.fab_home_news_share);
            fab_like=view.findViewById(R.id.fab_home_news_like);
        }
    }

    private static class ADViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rl_native_ad;

        private ADViewHolder(View view) {
            super(view);
            rl_native_ad = view.findViewById(R.id.rl_native_ad);
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private static CircularProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_PROG) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progressbar, parent, false);
            return new ProgressViewHolder(v);
        } else if (viewType >= 1000) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ads, parent, false);
            return new ADViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_newsbyvideo, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).textView_heading.setTypeface(methods.getFontMedium());

            setPlay(((MyViewHolder) holder).imageView_play, position);
            methods.setFavImage(arrayList.get(position).getIsFav(), ((MyViewHolder) holder).imageView_fav);
            methods.setLikeImage(arrayList.get(position).getIsLike(), ((MyViewHolder) holder).fab_like);

            ((MyViewHolder) holder).textView_cat.setText(arrayList.get(position).getCatName());
            ((MyViewHolder) holder).textView_heading.setText(arrayList.get(position).getHeading());
            ((MyViewHolder) holder).textView_views.setText(arrayList.get(position).getTotalViews()+" Views");

            Picasso.get()
                    .load(methods.getImageThumbSize(arrayList.get(holder.getAdapterPosition()).getImageThumb(), "header"))
                    .placeholder(R.drawable.placeholder_news)
                    .into(((MyViewHolder) holder).imageView);


            ((MyViewHolder) holder).imageView_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (Constant.isLogged) {
                        didTapFav(((MyViewHolder) holder).imageView_fav);
                        loadFav(arrayList.get(position).getId(), position, ((MyViewHolder) holder).imageView_fav);
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
//                        intent.putExtra("from", "app");
                        context.startActivity(intent);
                    }
                }
            });

            ((MyViewHolder) holder).fab_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Constant.isLogged) {
                        didTapFav(((MyViewHolder) holder).fab_like);
                        loadLike(arrayList.get(position).getId(), position, ((MyViewHolder) holder).fab_like);
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.putExtra("from", "app");
                        context.startActivity(intent);
                    }
                }
            });

            ((MyViewHolder) holder).rl_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    methods.showInterAd(holder.getAdapterPosition(), "");
                    Constant.selected_news_pos = position;
                    Constant.itemNewsCurrent = arrayList.get(position);

                    Intent intent = new Intent(context, NewsDetailsActivity.class);
                    intent.putExtra("isuser", false);
                    context.startActivity(intent);
                }
            });

            ((MyViewHolder) holder).imageView_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!arrayList.get(holder.getAdapterPosition()).getVideoId().equals("") && arrayList.get(holder.getAdapterPosition()).getVideoId()!= null){
                        methods.startVideoPlay(arrayList.get(holder.getAdapterPosition()).getVideoId());
                    }else {
                        Intent intent = new Intent(context, VideoviewActivity.class);
                        intent.putExtra("url", arrayList.get(holder.getAdapterPosition()).getVideoUrl());
                        context.startActivity(intent);
                    }

                }
            });

            ((MyViewHolder) holder).fab_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    methods.shareNews(arrayList.get(holder.getAdapterPosition()));
                }
            });
        } else if (holder instanceof ADViewHolder) {
            if (isAdLoaded) {
                try {
                    if (((ADViewHolder) holder).rl_native_ad.getChildCount() == 0) {
                        if (mNativeAdsAdmob.size() >= 5) {

                            int i = new Random().nextInt(mNativeAdsAdmob.size() - 1);

                            NativeAdView adView = (NativeAdView) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_admob, null);
                            populateUnifiedNativeAdView(mNativeAdsAdmob.get(i), adView);
                            ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                            ((ADViewHolder) holder).rl_native_ad.addView(adView);

                            ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (getItemCount() == 1) {
                ProgressViewHolder.progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void loadLike(String id, int position, FloatingActionButton fab_like) {
        if (methods.isNetworkAvailable()) {
            LoadLike loadLike = new LoadLike(new SuccessListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String likeSuccess, String message) {
                    if (success.equals("1")) {
                        if (likeSuccess.equals("1")) {
                            arrayList.get(position).setIsLike(true);
                        } else {
                            arrayList.get(position).setIsLike(false);
                        }
                        methods.setLikeImage(arrayList.get(position).getIsLike(), fab_like);

                        notifyItemChanged(position);

                        methods.showToast(message);
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_LIKE, 0, "", id, "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "","","","","","","","","","","","","","","","","","","","","","", null, null));
            loadLike.execute();
        } else {
            methods.showToast(context.getString(R.string.err_internet_not_conn));
        }
    }

    private void loadFav(String qid, final int posi, ImageView iv_fav) {
        if (methods.isNetworkAvailable()) {
            LoadFav loadFav = new LoadFav(new SuccessListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String favSuccess, String message) {
                    if (success.equals("1")) {
                        if (favSuccess.equals("1")) {
                            arrayList.get(posi).setIsFav(true);
                        } else {
                            arrayList.get(posi).setIsFav(false);
                        }
                        methods.setFavImage(arrayList.get(posi).getIsFav(), iv_fav);

                        notifyItemChanged(posi);

                        methods.showToast(message);
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_FAV, 0, "", qid, "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "","","","","","","","","","","","","","","","","","","","","","", null, null));
            loadFav.execute();
        } else {
            methods.showToast(context.getString(R.string.err_internet_not_conn));
        }
    }

    private void didTapFav(ImageView imageView) {
        final Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.bubble);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.12, 40);
        myAnim.setInterpolator(interpolator);
        imageView.startAnimation(myAnim);
    }

    private void setPlay(ImageView imageView, int pos) {
        if (arrayList.get(pos).getType().equals("video")) {
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);
    }

    public String getId(int pos) {
        return arrayList.get(pos).getId();
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size() + 1;
    }

    public boolean isHeader(int position) {
        return position == arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return VIEW_PROG;
        } else if (arrayList.get(position) == null) {
            return 1000 + position;
        } else {
            return position;
        }
    }

    public void addAds(NativeAd nativeAd) {
        mNativeAdsAdmob.add(nativeAd);
        isAdLoaded = true;
    }

    private boolean isProgressPos(int position) {
        return position == arrayList.size();
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new NameFilter();
        }
        return filter;
    }

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<ItemNews> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getHeading();
                    if (nameList.toLowerCase().contains(constraint))
                        filteredItems.add(filteredArrayList.get(i));
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = filteredArrayList;
                    result.count = filteredArrayList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            arrayList = (ArrayList<ItemNews>) results.values;
            notifyDataSetChanged();
        }
    }

    private InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            Constant.selected_news_pos = position;
            Constant.itemNewsCurrent = arrayList.get(position);

            Intent intent = new Intent(context, NewsDetailsActivity.class);
            intent.putExtra("isuser", false);
            context.startActivity(intent);
        }
    };

    public void destroyNativeAds() {
        try {
            for (int i = 0; i < mNativeAdsAdmob.size(); i++) {
                mNativeAdsAdmob.get(i).destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}