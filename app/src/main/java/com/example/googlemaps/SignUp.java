package com.example.googlemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.googlemaps.Model.DriverInfo;
import com.google.android.gms.common.internal.BaseGmsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import static com.example.googlemaps.Common.DRIVER__INFO;

public class SignUp extends AppCompatActivity {
    FirebaseAuth auth;
    private  FirebaseAuth.AuthStateListener listener;
 private  String service;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        database=FirebaseDatabase.getInstance();
         RadioGroup radioGroup=(RadioGroup)findViewById(R.id.radio_group);
        auth=FirebaseAuth.getInstance();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){

                    case R.id.driver:
                        Common.DRIVER__INFO="DriverInfo";
                        databaseReference=database .getReference( Common.DRIVER__INFO);
                        service="Driver";
                        break;
                    case R.id.mechanic:

                        databaseReference=database .getReference( Common.DRIVER__INFO);
                        service="Mechanic";
                        break;
                    case R.id.rescue_winch:

                        databaseReference=database .getReference(Common.DRIVER__INFO);
                        service="RescueWinch";
                        break;
                }


            }
        });
        showregistyerlayout();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(SignUp.this,SplashActivity.class);
        startActivity(i);
    }

    private void showregistyerlayout() {
       final EditText firstName =findViewById(R.id.firstName);
        final EditText  lastName = findViewById(R.id.lastName);
        final EditText useremail = findViewById(R.id.email);
        final EditText userphone = findViewById(R.id.phone);
         final EditText  userpassword = findViewById(R.id.password);
         final EditText confirmPassword =findViewById(R.id.confirmPassword);

        Button signUp = findViewById(R.id.signUp);



        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected(SignUp.this)){
                    Toast.makeText(SignUp.this, " Please check Internet Connection!", Toast.LENGTH_SHORT).show();

                }
                else {

                    final String email = useremail.getText().toString().trim();
                    final String password = userpassword.getText().toString().trim();
                    final String cpassword = confirmPassword.getText().toString().trim();
                    final String fname = firstName.getText().toString().trim();
                    final String lname = lastName.getText().toString().trim();
                    final String phone = userphone.getText().toString().trim();


                    if (fname.isEmpty()) {
                        firstName.setError("corent password");
                        firstName.requestFocus();
                        return;
                    }
                    if (lname.isEmpty()) {
                        lastName.setError("corent password");
                        lastName.requestFocus();
                        return;
                    }

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        useremail.setError("enter vaild email");
                        useremail.requestFocus();
                        return;
                    }
                    if (email.isEmpty()) {
                        useremail.setError("Requer");
                        useremail.requestFocus();
                        return;
                    }
                    if (password.isEmpty()) {
                        userpassword.setError("Requred");
                        userpassword.requestFocus();
                        return;
                    }
                    if (!cpassword.equals(password)) {
                        userpassword.setError("Not Match");
                        userpassword.requestFocus();
                        return;
                    }
                    if (password.length() < 6) {
                        userpassword.setError("length must 6 Number");
                        userpassword.requestFocus();
                        return;
                    }
                    if (phone.isEmpty()) {
                        userphone.setError("Requred");
                        userphone.requestFocus();
                        return;
                    }
                /*
                if(phone.length()<11){
                   userphone .setError("invalid phone");
                   userphone.requestFocus();
                    return;
                }

                 */
                    if (!Patterns.PHONE.matcher(phone).matches()) {
                        userphone.setError("invalid phone");
                        userphone.requestFocus();
                        return;
                    }

                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String id = auth.getCurrentUser().getUid();
                                        String token = FirebaseInstanceId.getInstance().getToken();
                                        DriverInfo driverInfo = new DriverInfo(  /*name,email,phone,id,password,"0.0"   */);
                                        HashMap<String,Object> hashMap=new HashMap<>();
                                        hashMap.put("name",fname +" "+lname);
                                        hashMap.put("id",id);
                                        hashMap.put("email",email);
                                        hashMap.put("password",password);
                                        hashMap.put("phone",phone);
                                        hashMap.put("rating","0.0");
                                        hashMap.put("image","default");
                                        hashMap.put("token",token);
                                        hashMap.put("service",service);





                                        databaseReference.child(auth.getCurrentUser().getUid()).setValue(hashMap);
                                        Intent intent=new Intent(SignUp.this,SplashActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(SignUp.this, "Sign Up Failed Failed", Toast.LENGTH_SHORT).show();
                                    }

                                    // ...
                                }
                            });
                }



                /*



                    databaseReference.child(auth.getCurrentUser().getUid()).setValue(driverInfo).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(SplashActivity.this, "faild!", Toast.LENGTH_SHORT).show();

                        }

                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SplashActivity.this, "Register Succesfully! ", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            GotoHomeActivity(driverInfo);
                        }
                    });

                }
                */
            }
        });




    }
    private boolean isConnected(SignUp sUp) {
        ConnectivityManager connectivityManager= (ConnectivityManager) sUp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifiConn!=null&&wifiConn.isConnected())||(mobileConn!=null&&mobileConn.isConnected())){
            return true;
        }else {
            return false;
        }

    }
}