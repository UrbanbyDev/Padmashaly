package com.urbantech.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.urbantech.AsyncTask.LoadStatus;
import com.urbantech.padmashali.R;
import com.urbantech.interfaces.SuccessListener;
import com.urbantech.item.ItemGallery;
import com.urbantech.utils.Constant;
import com.urbantech.utils.Methods;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


public class AdapterGalleryEdit extends RecyclerView.Adapter<AdapterGalleryEdit.MyViewHolder> {

    private Context context;
    private Methods methods;
    private String newsID;
    private ArrayList<ItemGallery> arrayList;
    private ArrayList<Uri> arrayListUri;

    class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView iv_gallery, iv_close;

        MyViewHolder(View view) {
            super(view);
            iv_gallery = view.findViewById(R.id.iv_gallery);
            iv_close = view.findViewById(R.id.iv_gallery_close);
        }
    }

    public AdapterGalleryEdit(Context context, String newsID, ArrayList<ItemGallery> arrayList, ArrayList<Uri> arrayListUri) {
        this.context = context;
        this.newsID = newsID;
        this.arrayList = arrayList;
        this.arrayListUri = arrayListUri;
        methods = new Methods(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gallery, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        if (arrayList.get(position).getUri() != null) {
            Picasso.get()
                    .load(arrayList.get(position).getUri())
                    .placeholder(R.drawable.placeholder_news)
                    .into(holder.iv_gallery);
        } else {
            Picasso.get()
                    .load(arrayList.get(position).getImage())
                    .placeholder(R.drawable.placeholder_news)
                    .into(holder.iv_gallery);
        }

        holder.iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position <= arrayList.size()) {
                    if (arrayList.get(position).getUri() != null) {

                        for (int i = 0; i < arrayList.size(); i++) {
                            for (int j = 0; j < arrayListUri.size(); j++) {

                                if (arrayList.get(i).getUri() != null && (arrayList.get(i).getUri() == arrayListUri.get(j))) {
                                    arrayListUri.remove(j);
                                    break;
                                }
                            }
                        }

                        arrayList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, arrayList.size());
                    } else {
                        openRemoveDialog(holder.getAbsoluteAdapterPosition());
                    }
                }
            }
        });
    }

    private void openRemoveDialog(int pos) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        alertDialog.setTitle(context.getString(R.string.delete));
        alertDialog.setMessage(context.getString(R.string.sure_delete_uploaded_image));
        alertDialog.setPositiveButton(context.getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loadRemoveImage(pos);
            }
        });
        alertDialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();
    }

    private void loadRemoveImage(int pos) {
        if (methods.isNetworkAvailable()) {

            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(context.getString(R.string.loading));

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
                            arrayList.remove(pos);
                            notifyItemRemoved(pos);
                            notifyItemRangeChanged(pos, arrayList.size());
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, context.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_REMOVE_GALLERY_IMAGE, 0, "", newsID, "", "", arrayList.get(pos).getId(), "", "", "", "", "", "", "", "","","","","","","","","",null, null));
            loadStatus.execute();
        } else {
            Toast.makeText(context, context.getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}