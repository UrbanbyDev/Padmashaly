package com.urbantech.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.urbantech.item.ItemMember;
import com.urbantech.item.ItemSurvey;
import com.urbantech.padmashali.R;
import com.urbantech.padmashali.SingleMemberActivity;
import com.urbantech.utils.Constant;
import com.urbantech.utils.Methods;

import java.util.ArrayList;

public class AdapterMemberShip extends RecyclerView.Adapter<AdapterMemberShip.MyViewHolder> {

    ArrayList<ItemMember> arrayList;
    private Context context;
    private Methods methods;

    public AdapterMemberShip(Context context, ArrayList<ItemMember> arrayList) {
        this.arrayList = arrayList;
        this.context = context;

        methods = new Methods(context);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView member_name,member_id,member_phone,member_type;
        RoundedImageView member_image;
        CardView cardView_member;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            member_id=itemView.findViewById(R.id.member_id);
            member_name=itemView.findViewById(R.id.member_name);
            member_phone=itemView.findViewById(R.id.member_phone);
            member_type=itemView.findViewById(R.id.membership_type);
            member_image=itemView.findViewById(R.id.iv_member);

            cardView_member=itemView.findViewById(R.id.cardview_member);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_membership,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.member_id.setText("ID No:-"+arrayList.get(position).getMember_id());
        holder.member_name.setText(arrayList.get(position).getMember_name());
        holder.member_phone.setText(arrayList.get(position).getMember_mobile());
        holder.member_type.setText(arrayList.get(position).getMember_type());

        if(!arrayList.get(position).getMember_profile().equals("")){
            Picasso.get().load(arrayList.get(position).getMember_profile()).placeholder(R.drawable.placeholder_prof).into(holder.member_image);
        }

        holder.cardView_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, SingleMemberActivity.class);
                intent.putExtra("member_id",arrayList.get(position).getMember_id());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


}
