package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText username;
    private EditText fullname;
    private EditText reemail;
    private EditText repassword;
    private Button Regg;
    private TextView goLogin;
    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username=(EditText) findViewById(R.id.UserName);
        fullname=(EditText) findViewById(R.id.FullName);
        reemail=(EditText) findViewById(R.id.Remail);
        repassword=(EditText) findViewById(R.id.Rpassword);
        Regg=(Button) findViewById(R.id.Reg);
        goLogin=(TextView) findViewById(R.id.goToLogin);

        auth=FirebaseAuth.getInstance();
        /// go to login if u already have account
        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        // for new users registeration

        Regg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pd=new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait...");
                pd.show();
                String str_username=username.getText().toString();
                String str_fullname=fullname.getText().toString();
                String str_email=reemail.getText().toString();
                String str_password=repassword.getText().toString();

                if(TextUtils.isEmpty(str_username)||TextUtils.isEmpty(str_fullname)||
                        TextUtils.isEmpty(str_email)||TextUtils.isEmpty(str_password))
                {
                    Toast.makeText(RegisterActivity.this,"Fill missing fields",Toast.LENGTH_SHORT).show();

                }
                else if(str_password.length()<6){
                    Toast.makeText(RegisterActivity.this,"Password should contain above 6 characters",Toast.LENGTH_SHORT).show();
                }
                else{
                      register(str_username,str_fullname,str_email,str_password);

                }
            }
        });
    }
     private  void register(final String username, final String fullname, String email, String password)
     {
         auth.createUserWithEmailAndPassword(email,password)
                 .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if(task.isSuccessful()){
                             FirebaseUser firebaseUser=auth.getCurrentUser();
                             String userid=firebaseUser.getUid();

                             reference= FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                             HashMap<String,Object> hashMap=new HashMap<>();
                             hashMap.put("id",userid);
                             hashMap.put("username",username.toLowerCase());
                             hashMap.put("fullname",fullname);
                             hashMap.put("Bio","");
                             hashMap.put("imageUrl","https://firebasestorage.googleapis.com/v0/b/insta-eddde.appspot.com/o/placeholder.png?alt=media&token=f7d9412d-4161-4f39-99a8-5d416b45696a");


                             reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if(task.isSuccessful()){
                                         pd.dismiss();
                                         Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                                         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                         startActivity(intent);

                                     }
                                 }
                             });
                         }
                         else{
                             pd.dismiss();
                             Toast.makeText(RegisterActivity.this,"You cant Register with email or password",Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
     }

}