package com.urbantech.padmashali;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.urbantech.utils.Constant;
import com.urbantech.utils.SharedPref;

public class IDActivity extends AppCompatActivity {

    TextView id_num,id_name,id_member;
    CardView id_layout;
    RoundedImageView id_image;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idactivity);

        sharedPref = new SharedPref(this);

        Toolbar toolbar = findViewById(R.id.toolbar_id_card);
        toolbar.setTitle(getString(R.string.id_card));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id_layout=findViewById(R.id.id_layout);
        id_num=findViewById(R.id.id_number);
        id_name=findViewById(R.id.id_name);
        id_member=findViewById(R.id.id_member);
        id_image=findViewById(R.id.iv_id);

        if(Constant.isLogged){

            if (!Constant.itemUser.getImage().equals("")) {
                Picasso.get().load(Constant.itemUser.getImage()).placeholder(R.drawable.placeholder_prof).into(id_image);
            }

            id_num.setText("ID Num:-"+sharedPref.getMemberID());
            id_name.setText(Constant.itemUser.getName());
            id_member.setText("MEMBER");
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}