package com.example.coffeemate2.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeemate2.ChatActivity;
import com.example.coffeemate2.R;
import com.example.coffeemate2.models.ModelUsers;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{

    Context context;
    List<ModelUsers> usersList;

    //constructor
    public AdapterUsers(Context context, List<ModelUsers> usersList){
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout(raw_user.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.row_users,viewGroup,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //get data
        String hisUID = usersList.get(i).getUid();
        String userImage = usersList.get(i).getProfileimage();
        String userName = usersList.get(i).getFullname();

        //set data
        myHolder.mNameTv.setText(userName);
        try {
            Picasso.get().load(userImage).
                    placeholder(R.drawable.ic_p).
                    into(myHolder.mAvatarTv);
        }catch (Exception e){

        }

        //handle item click
        myHolder.mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", hisUID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarTv;
        TextView mNameTv;
        ImageButton mAddFriend, mChat;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init view

            mAvatarTv = itemView.findViewById(R.id.avaterTv);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mChat = itemView.findViewById(R.id.chatTv);
            mAddFriend = itemView.findViewById(R.id.addfirendTv);
        }


    }
}
