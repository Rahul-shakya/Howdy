package com.example.asus.howdy;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView=inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList=(RecyclerView)mMainView.findViewById(R.id.friends_list);
        mAuth=FirebaseAuth.getInstance();

        mCurrent_user_id=mAuth.getCurrentUser().getUid();

        mFriendsDatabase= FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        //inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        startListening();

    }

    public void startListening(){
        Query query=FirebaseDatabase.getInstance()
                .getReference()
                .child("Friends").child(mCurrent_user_id)
                .limitToLast(50);

        FirebaseRecyclerOptions<Friends> options=new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(query,Friends.class)
                .build();

        FirebaseRecyclerAdapter adapter=new FirebaseRecyclerAdapter<Friends,FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final FriendsViewHolder holder, int position, final Friends model) {

                holder.setDate(model.date);

                final String list_user_id=getRef(position).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final String userName=dataSnapshot.child("name").getValue().toString();
                        String image=dataSnapshot.child("image").getValue().toString();
                       // String userOnline=dataSnapshot.child("online").getKey().toString();

                        holder.setName(userName);
                        holder.setImage(image);

                        if(dataSnapshot.hasChild("online")){
                            String userOnline=dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(userOnline);
                        }

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                CharSequence options[] =new CharSequence[]{"Open Profile","Send message"};
                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());

                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        //Click Event for each item
                                        if(i==0){
                                            Intent profile_intent=new Intent(getContext(),ProfileActivity.class);
                                            profile_intent.putExtra("user_id",list_user_id);
                                            startActivity(profile_intent);
                                        }
                                        else if(i==1){

                                            Intent chatIntent=new Intent(getContext(),ChatActivity.class);
                                            chatIntent.putExtra("user_id",list_user_id);
                                            chatIntent.putExtra("user_name",userName);
                                            startActivity(chatIntent);

                                        }

                                    }
                                });
                                builder.show();
                            }
                        });



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }


            @Override
            public FriendsViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new FriendsViewHolder(view);
            }
        };


        mFriendsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
        }
        public void setDate(String date){
            TextView userDateView=(TextView)mView.findViewById(R.id.user_single_status);
            userDateView.setText(date);
        }

        public void setName(String name){

            TextView userNameView=(TextView)mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setImage(String image) {
            CircleImageView userImageView=(CircleImageView)mView.findViewById(R.id.user_single_image);
            Picasso.get().load(image).placeholder(R.drawable.pic).into(userImageView);
        }

        public void setUserOnline(String online_status){
            ImageView userOnlineView=(ImageView)mView.findViewById(R.id.user_single_online);

            if(online_status.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            }else{
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }
    }


}
