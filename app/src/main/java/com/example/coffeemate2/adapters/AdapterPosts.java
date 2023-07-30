package com.example.coffeemate2.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeemate2.ChatActivity;
import com.example.coffeemate2.R;
import com.example.coffeemate2.models.ModelPosts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    Context context;
    List<ModelPosts>postList;

    public AdapterPosts(Context context, List<ModelPosts>postList){
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts,viewGroup,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //get data
        String uid =postList.get(i).getUid();
        String uName =postList.get(i).getUname();
        String uDp =postList.get(i).getUdp();
        String pId =postList.get(i).getPid();
        String pPost =postList.get(i).getPpost();
        String pTimeStamp =postList.get(i).getPtime();

        //convert time stamp to dd//mm//yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format(" hh:mm aa",calendar).toString();

        //set data
        myHolder.unameTv.setText(uName);
        myHolder.ptimeTv.setText(pTime);
        myHolder.pdescription.setText(pPost);
        //set user dp
        try{
            Picasso.get().load(uDp).
                    placeholder(R.drawable.ic_p).
                    into(myHolder.upictureTv);
        }
        catch (Exception e){

        }

        //handle button
        myHolder.mMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "More", Toast.LENGTH_SHORT).show();
            }
        });

        myHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
            }
        });

        myHolder.mChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                if (!uid.equals(fUser.getUid())){
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("hisUid", uid);
                    context.startActivity(intent);
                }
                else{
                    Toast.makeText(context, "It's you", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //view from row_post.xml
        ImageView upictureTv;
        TextView unameTv, ptimeTv, pdescription, plikesTv;
        ImageButton mMoreBtn, mLikeBtn, mChatBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            upictureTv = itemView.findViewById(R.id.uPictureTv);
            ptimeTv = itemView.findViewById(R.id.pTimeTv);
            unameTv = itemView.findViewById(R.id.uNameTv);
            pdescription = itemView.findViewById(R.id.pDescriptionTv);
            plikesTv = itemView.findViewById(R.id.pLikesTv);
            mMoreBtn = itemView.findViewById(R.id.moreBtn);
            mLikeBtn = itemView.findViewById(R.id.likeBtn);
            mChatBtn = itemView.findViewById(R.id.chatBtn);

        }
    }
}
