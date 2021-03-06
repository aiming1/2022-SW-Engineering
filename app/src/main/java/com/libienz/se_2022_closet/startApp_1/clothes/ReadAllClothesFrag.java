package com.libienz.se_2022_closet.startApp_1.clothes;

import static com.libienz.se_2022_closet.startApp_1.util.FirebaseReference.userRef;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.libienz.se_2022_closet.R;
import com.libienz.se_2022_closet.startApp_1.data.Clothes;
import com.libienz.se_2022_closet.startApp_1.userauth.MainActivity;

import java.util.ArrayList;

public class ReadAllClothesFrag extends Fragment {


    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference().child("Clothes").child(user.getUid());

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Clothes> mClothesList;
    Context context;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_read_all_clothes, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.clothes_recyclerview);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager= new GridLayoutManager(getActivity(),3);
        //mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(0);

        mClothesList=new ArrayList<>();

        context=container.getContext();
        mRecyclerAdapter = new RecyclerViewAdapter(mClothesList, context);





        userRef.child(user.getUid()).child("Clothes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mClothesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //???????????? ????????? ?????????
                    Clothes clothes = snapshot.getValue(Clothes.class);
                    mClothesList.add(clothes);

                    Log.d("ReadAllClothesFrag", "Single ValueEventListener : " + snapshot.getValue());
                }
                mRecyclerAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRecyclerAdapter.setOnItemClickListener (new RecyclerViewAdapter.OnItemClickListener() {
            //????????? ?????????

            @Override
            public void onItemClick(View view, int position) {
                MainActivity mainActivity = (MainActivity) getActivity();
                LinearLayout weatherLayout= mainActivity.findViewById(R.id.weatherLayout);
                weatherLayout.setVisibility(View.GONE);


                //?????? ?????? ???????????? ?????????
                //Toast.makeText(view.getContext(),"???????????????: "+mClothesList.get(position).getClothesKey(),Toast.LENGTH_LONG).show();
                Bundle bundle = new Bundle(); // ????????? ?????? ??? ??????
                bundle.putString("ClothesKey",mClothesList.get(position).getClothesKey());//????????? ?????? ??? ??????
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                readClothesFrag fragment2 = new readClothesFrag();//???????????????2 ??????
                fragment2.setArguments(bundle);//????????? ???????????????2??? ?????? ??????
                transaction.replace(R.id.frag_fl, fragment2);
                transaction.commit();

            }

        });


        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


}