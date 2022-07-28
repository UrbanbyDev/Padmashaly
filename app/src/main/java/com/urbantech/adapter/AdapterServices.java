package com.urbantech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.urbantech.item.ItemServices;
import com.urbantech.padmashali.R;
import com.urbantech.utils.Methods;

import java.util.ArrayList;

public class AdapterServices extends RecyclerView.Adapter<AdapterServices.MyViewHolder>{
    private ArrayList<ItemServices> arrayList;
    private Context context;
    private ArrayList<ItemServices> filteredArrayList;
    private NameFilter filter;
    private Methods methods;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        ImageView iv_image;

        MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.upcoming_text);
            iv_image = view.findViewById(R.id.upcoming_img);
        }
    }

    public AdapterServices(Context context, ArrayList<ItemServices> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        filteredArrayList = arrayList;
        methods = new Methods(context);
    }

    @NonNull
    @Override
    public AdapterServices.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_upcomming, parent, false);

        return new AdapterServices.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterServices.MyViewHolder holder, int position) {

        holder.tv_name.setTypeface(methods.getFontMedium());
        holder.tv_name.setText(arrayList.get(position).getService_name());
        Picasso.get()
                .load(methods.getImageThumbSize(arrayList.get(position).getImageThumb(), "Service"))
                .placeholder(R.drawable.placeholder_news)
                .into(holder.iv_image);
    }

    public String getId(int pos) {
        return arrayList.get(pos).getSid();
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new AdapterServices.NameFilter();
        }
        return filter;
    }

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<ItemServices> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getService_name();
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

            arrayList = (ArrayList<ItemServices>) results.values;
            notifyDataSetChanged();
        }
    }

}
