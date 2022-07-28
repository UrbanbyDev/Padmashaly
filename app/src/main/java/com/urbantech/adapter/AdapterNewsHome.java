package com.urbantech.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.urbantech.utils.Constant;
import com.urbantech.utils.Methods;
import com.urbantech.utils.MyBounceInterpolator;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class AdapterNewsHome extends RecyclerView.Adapter<AdapterNewsHome.MyViewHolder> {

    private ArrayList<ItemNews> arrayList;
    private Context context;
    private ArrayList<ItemNews> filteredArrayList;
    private NameFilter filter;
    private Methods methods;


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

    public AdapterNewsHome(Context context, ArrayList<ItemNews> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        filteredArrayList = arrayList;
        methods = new Methods(context, interAdListener);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_news_home, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.textView_heading.setTypeface(methods.getFontMedium());

        setPlay(holder.imageView_play, position);
        methods.setFavImage(arrayList.get(position).getIsFav(), holder.imageView_fav);
        methods.setLikeImage(arrayList.get(position).getIsLike(),holder.fab_like);

        holder.textView_cat.setText(arrayList.get(position).getCatName());
        holder.textView_heading.setText(arrayList.get(position).getHeading());
        holder.textView_views.setText(arrayList.get(position).getTotalViews()+" Views");

        Picasso.get()
                .load(methods.getImageThumbSize(arrayList.get(holder.getAbsoluteAdapterPosition()).getImageThumb(), "home"))
                .placeholder(R.drawable.placeholder_news)
                .into(holder.imageView);


        holder.imageView_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.isLogged) {
                    didTapFav(holder.imageView_fav);
                    loadFav(arrayList.get(position).getId(), position, holder.imageView_fav);
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.putExtra("from", "app");
                    context.startActivity(intent);
                }
            }
        });

        holder.fab_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.isLogged) {
                    didTapFav(holder.fab_like);
                    loadLike(arrayList.get(position).getId(), position, holder.fab_like);
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.putExtra("from", "app");
                    context.startActivity(intent);
                }
            }
        });


        holder.rl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                methods.showInterAd(holder.getAdapterPosition(), "");
                Constant.selected_news_pos = position;
                Constant.itemNewsCurrent = arrayList.get(position);

                Intent intent = new Intent(context, NewsDetailsActivity.class);
                intent.putExtra("isuser", false);
                context.startActivity(intent);
            }
        });

        holder.imageView_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.startVideoPlay(arrayList.get(holder.getAdapterPosition()).getVideoId());
            }
        });

        holder.fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.shareNews(arrayList.get(holder.getAdapterPosition()));
            }
        });
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
            }, methods.getAPIRequest(Constant.METHOD_LIKE, 0, "", id, "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "", "","","","","","","","","",null, null));
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
            }, methods.getAPIRequest(Constant.METHOD_FAV, 0, "", qid, "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "", "","","","","","","","","",null, null));
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

    public String getId(int pos) {
        return arrayList.get(pos).getId();
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public boolean isHeader(int position) {
        return (position + 3) % 3 == 0;
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
}