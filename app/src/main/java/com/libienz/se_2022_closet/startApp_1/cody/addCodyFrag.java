package com.libienz.se_2022_closet.startApp_1.cody;

import static com.libienz.se_2022_closet.startApp_1.util.FirebaseReference.userRef;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.libienz.se_2022_closet.R;
import com.libienz.se_2022_closet.startApp_1.clothes.ClothesAdapter;
import com.libienz.se_2022_closet.startApp_1.clothes.RecyclerViewAdapter;
import com.libienz.se_2022_closet.startApp_1.clothes.addClothesFrag;
import com.libienz.se_2022_closet.startApp_1.data.Clothes;
import com.libienz.se_2022_closet.startApp_1.data.Cody;
import com.libienz.se_2022_closet.startApp_1.userauth.MainActivity;

import java.util.ArrayList;


public class addCodyFrag extends Fragment {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ArrayList<String> codycomp = new ArrayList<>();
    private ArrayList<String> hashtag = new ArrayList<>(10);
    private ArrayList<String> clothesKey = new ArrayList<>();
    private RecyclerView codyaddC_rv;
    private RecyclerViewAdapter adapter;
    private ArrayList<Clothes> clothes;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_cody, container, false);

        //?????? : ????????? ???????????? ????????? ???????????? ????????? ???????????????. ???????????? ????????? ?????? ArrayList<String> codycomp??? ???????????? ?????? ???????????? ?????????
        //????????????????????? ????????? ??????
        codyaddC_rv = (RecyclerView) view.findViewById(R.id.codyaddC_rv);
        codyaddC_rv.setHasFixedSize(true);

        codyaddC_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        codyaddC_rv.scrollToPosition(0);
        context = container.getContext();
        clothes = new ArrayList<>();
        adapter = new RecyclerViewAdapter(clothes, context);

        userRef.child(user.getUid()).child("Clothes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clothes.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //???????????? ????????? ?????????
                    Clothes cl = snapshot.getValue(Clothes.class);
                    clothes.add(cl);

                    Log.d("ReadAllClothesFrag", "Single ValueEventListener : " + snapshot.getValue());
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        codyaddC_rv.setAdapter(adapter);
        codyaddC_rv.setItemAnimator(new DefaultItemAnimator());

        //RecyclerViewAdapter?????? ????????? ????????? ???????????? ????????????
        //?????? ?????? ????????? ??? ?????? ?????? ????????? ????????? ????????? ???????????????.
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                clothesKey.add(clothes.get(position).getClothesKey());
            }
        });

        //????????? ?????? ??? ???????????? ???????????? clotheskey??? codycomp??? add
        Button addCodyComp_btn = (Button) view.findViewById(R.id.addCodyComp_btn);
        addCodyComp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clothesKey.isEmpty()){
                    Toast.makeText(container.getContext(), "????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                }
                else{
                    for(String k : clothesKey){
                        codycomp.add(k);
                    }
                    Toast.makeText(container.getContext(), "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                }
                clothesKey.clear();
            }
        });


        //???????????? ??????(?????????)??? ????????????
        EditText addCodyKey_et = (EditText) view.findViewById(R.id.addCodyKey_et);

        //?????? ????????? ????????????
        EditText addCodyTag_et = (EditText) view.findViewById(R.id.addCodyTag_et);
        TextView showAddedCodyTag_tv = view.findViewById(R.id.showAddedCodyTag_tv);
        addCodyTag_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hashtag.add(addCodyTag_et.getText().toString());
                    showAddedCodyTag_tv.append("#" + hashtag.get(hashtag.size() - 1) + " ");
                    addCodyTag_et.setText(null);
                    return true;
                }
                return false;
            }
        });

        //???????????? ????????? ?????? ????????? ?????????
        Button addCody_btn = (Button) view.findViewById(R.id.doneAddCody_btn);
        addCody_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //?????? ?????? ??? ?????????, ?????? ????????? ???????????? ?????? ??????
                if (user != null && addCodyKey_et.getText() != null && !hashtag.isEmpty()){
                    addCody(user.getUid(), codycomp, addCodyKey_et.getText().toString(), hashtag);
                    Toast.makeText(container.getContext(), "?????? ????????? ?????????????????????", Toast.LENGTH_SHORT).show();

                    hashtag.clear();
                    addCodyTag_et.setText(null);
                    addCodyKey_et.setText(null);
                    //??????????????? ??????, ?????? ???????????? ?????????
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
                else if (addCodyKey_et.getText() == null) {
                    Toast.makeText(container.getContext(), "?????? ???????????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                }
                else if (hashtag.isEmpty()) {
                    Toast.makeText(container.getContext(), "????????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(container.getContext(), "????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }

    //?????? ?????? ?????????
    public void addCody(String idToken, ArrayList<String> Comp, String Key, ArrayList<String> Tag) {
        Cody cody = new Cody(Key, Comp, Tag, false);

        //?????????????????? ???????????? ????????????????????? ?????? ?????? ??????
        userRef.child(idToken).child("Cody").child(Key).setValue(cody);
    }
}