package com.urbantech.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.urbantech.item.ItemSurvey;
import com.urbantech.padmashali.R;
import com.urbantech.padmashali.SingleSurveyActivity;
import com.urbantech.utils.Methods;

import java.util.ArrayList;

public class SurveyDetailsAdapter extends RecyclerView.Adapter<SurveyDetailsAdapter.MyViewHolder>{

    private ArrayList<ItemSurvey> arrayList;
    private Context context;
    private Methods methods;

    class MyViewHolder extends RecyclerView.ViewHolder {

        CardView survey_card;
        TextView name, surname, fathername,dob,gender,housenumber,wardnumber,city,district,mandal,occupation,qualification;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name_survey_details);
            surname = view.findViewById(R.id.survey_details_surname);
            dob = view.findViewById(R.id.survey_details_dob);
            gender = view.findViewById(R.id.survey_details_gender);
            housenumber = view.findViewById(R.id.survey_details_housenumber);
            wardnumber=view.findViewById(R.id.survey_details_wardnumber);
            city=view.findViewById(R.id.survey_details_city);
            mandal=view.findViewById(R.id.survey_details_mandal);
            district=view.findViewById(R.id.survey_details_district);
            qualification=view.findViewById(R.id.survey_details_qualification);
            occupation=view.findViewById(R.id.survey_details_Occuapation);

            survey_card=view.findViewById(R.id.survey_card);
        }
    }

    public SurveyDetailsAdapter(Context context, ArrayList<ItemSurvey> arrayList) {
        this.arrayList = arrayList;
        this.context = context;

        methods = new Methods(context);
    }

    @NonNull
    @Override
    public SurveyDetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_survey_details, parent, false);

        return new SurveyDetailsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SurveyDetailsAdapter.MyViewHolder holder, int position) {


        holder.name.setText(arrayList.get(position).getSurvey_name());
        holder.surname.setText(arrayList.get(position).getSurvey_surname());
        holder.dob.setText(arrayList.get(position).getSurvey_dob());
        holder.gender.setText(arrayList.get(position).getSurvey_gender());
        holder.housenumber.setText(arrayList.get(position).getSurvey_housenumber());
        holder.wardnumber.setText(arrayList.get(position).getSurvey_wardnumber());
        holder.city.setText(arrayList.get(position).getSurvey_wardnumber());
        holder.district.setText(arrayList.get(position).getSurvey_district());
        holder.mandal.setText(arrayList.get(position).getSurvey_mandal());
        holder.occupation.setText(arrayList.get(position).getSurvey_occupation());
        holder.qualification.setText(arrayList.get(position).getSurvey_qualification());

        holder.survey_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleSurveyActivity.class);
                intent.putExtra("name",arrayList.get(position).getSurvey_name());
                intent.putExtra("surname",arrayList.get(position).getSurvey_surname());
                intent.putExtra("fathername",arrayList.get(position).getSurvey_fathername());
                intent.putExtra("dob",arrayList.get(position).getSurvey_dob());
                intent.putExtra("gender",arrayList.get(position).getSurvey_gender());
                intent.putExtra("hno",arrayList.get(position).getSurvey_housenumber());
                intent.putExtra("wardnumber",arrayList.get(position).getSurvey_wardnumber());
                intent.putExtra("city",arrayList.get(position).getSurvey_city());
                intent.putExtra("mandal",arrayList.get(position).getSurvey_mandal());
                intent.putExtra("district",arrayList.get(position).getSurvey_district());
                intent.putExtra("qualification",arrayList.get(position).getSurvey_qualification());
                intent.putExtra("occupation",arrayList.get(position).getSurvey_occupation());
                context.startActivity(intent);
            }
        });

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
