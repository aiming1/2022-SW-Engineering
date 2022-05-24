package com.libienz.se_2022_closet.startApp_1.UserAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.libienz.se_2022_closet.R;
import com.libienz.se_2022_closet.databinding.ActivityFindPwBinding;

public class FindPwActivity extends AppCompatActivity {

    private ActivityFindPwBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference userRef = database.getReference("user");


    public void passwordReset() {

        String inputEmail = binding.enterEmailToFindPw.getText().toString();
        if (inputEmail.equals("")) {
            binding.notifyEmailRes.setText("이메일 아이디를 입력해주세요!");
            binding.notifyEmailRes.setTextColor(Color.parseColor("#B30000"));
            return;
        }
        Log.d("toFind",inputEmail);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot userSnaposhot : snapshot.getChildren()) {

                    String dbItrEmail = userSnaposhot.child("email").getValue().toString();

                    Log.d("toFind",dbItrEmail);

                    if(inputEmail.equals(dbItrEmail)) { //db에서 긁어온 메일과 입력 메일이 같을 때때
                        //String password = userSnaposhot.child("password").getValue().toString();

                        String email = binding.enterEmailToFindPw.getText().toString();
                        auth.setLanguageCode("ko");
                        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            binding.notifyEmailRes.setText("입력하신 이메일로 인증링크를 전송했습니다. 링크를 클릭하여 새로운 패스워드를 설정해주세요!");
                                            binding.notifyEmailRes.setTextColor(Color.parseColor("#008000"));
                                            Log.d("sent", "Email sent.");
                                        }
                                        else {
                                            binding.notifyEmailRes.setText("입력하신 이메일로 인증링크를 전송하는데에 실패했습니다.");
                                            binding.notifyEmailRes.setTextColor(Color.parseColor("#B30000"));
                                        }
                                    }
                                });
                        return;
                    }
                }
                binding.notifyEmailRes.setText("회원가입되어 있지 않은 아이디입니다!");
                binding.notifyEmailRes.setTextColor(Color.parseColor("#B30000"));
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던중 에러 발생 시
                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_pw);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        binding.findPwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordReset();
            }
        });
        binding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginActiyity.class);
                startActivity(intent);
            }
        });


    }
}