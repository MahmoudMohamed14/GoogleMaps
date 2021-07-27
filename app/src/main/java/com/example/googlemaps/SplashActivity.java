package com.example.googlemaps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

import static com.example.googlemaps.Common.DRIVER__INFO;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth auth;
    private  FirebaseAuth.AuthStateListener listener;

FirebaseDatabase database;
DatabaseReference databaseReference;

    @Override
    protected void onStart() {

            super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent=new Intent(SplashActivity.this,DriverHomeActivity.class);

            startActivity(intent);
            finish();
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SplashActivity.this, e.getMessage()+"her", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    Log.d("token",instanceIdResult.getToken());
                    UserUtils.updateToken(SplashActivity.this,instanceIdResult.getToken());

                }
            });
            /*
        FirebaseDatabase.getInstance().getReference("MechanicInfo").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MechanicInfo mechan = snapshot.getValue(MechanicInfo.class);
                if (mechan != null) {
                    if (mechan.getId().equals(FirebaseAuth.getInstance()
                            .getCurrentUser().getUid())) {

                        Intent intent = new Intent(SplashActivity.this, DriverHomeActivity.class);
                        intent.putExtra("DRIVER_LOCATION_REFERANCE", "MechanicLocation");
                        startActivity(intent);
                        finish();
                        FirebaseInstanceId.getInstance().getInstanceId()
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SplashActivity.this, e.getMessage() + "her", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                Log.d("token", instanceIdResult.getToken());
                                UserUtils.updateToken(SplashActivity.this, instanceIdResult.getToken());

                            }
                        });
                    }


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });


        FirebaseDatabase.getInstance().getReference("DriverInfo").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DriverInfo driverInfo=snapshot.getValue(DriverInfo.class);
                if(driverInfo!=null) {
                    if (driverInfo.getId() .equals(FirebaseAuth.getInstance()
                            .getCurrentUser().getUid())) {


                            Intent intent=new Intent(SplashActivity.this,DriverHomeActivity.class);
                            intent.putExtra("DRIVER_LOCATION_REFERANCE","DriverLocation");
                            startActivity(intent);
                            finish();



                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference("RescueWinchInfo").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DriverInfo driverInfo=snapshot.getValue(DriverInfo.class);
                if(driverInfo!=null){
                    if(driverInfo.getId().equals(FirebaseAuth.getInstance()
                            .getCurrentUser().getUid())){




                            FirebaseInstanceId.getInstance().getInstanceId()
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SplashActivity.this, e.getMessage()+"her", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    Log.d("token",instanceIdResult.getToken());
                                    UserUtils.updateToken(SplashActivity.this,instanceIdResult.getToken());

                                }
                            });


                    }
                }}


            //  }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        }


             */

        }
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_signin);

        database=FirebaseDatabase.getInstance();

        auth=FirebaseAuth.getInstance();
        showloginlayout();



    }
/*
    private void inti() {

        providers= Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build()
                ,new AuthUI.IdpConfig.GoogleBuilder().build());


        auth=FirebaseAuth.getInstance();
        listener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth myFirebaseAuth) {
                FirebaseUser user = myFirebaseAuth.getCurrentUser();
                if (user != null) {
               CheckFirebase();
                }

                else
                 showloginlayout();


            }
        };
    }



    private void CheckFirebase() {
        databaseReference.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                   DriverInfo driverInfo=snapshot.getValue(DriverInfo.class);
                   if(driverInfo!=null) {

                   }


                   
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
*/
    private void ResetPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.forget_password, null);
        final EditText resetemail = view.findViewById(R.id.email_reset);
        Button resetpassword = view.findViewById(R.id.reset_password);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String email = resetemail.getText().toString().trim();
                if (email.isEmpty()) {
                    resetemail.setError("email is requried");
                    resetemail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    resetemail.setError("please inter valid email");
                    resetemail.requestFocus();
                    return;
                }
                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(v.getContext(), " check yor email", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), " some thing wrong happend!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }


        });

    }

//   private void showregistyerlayout() {
//       AlertDialog.Builder builder= new AlertDialog.Builder(this);
//        View view= LayoutInflater.from(this).inflate(R.layout.regester,null);
//        final EditText firstName = view.findViewById(R.id.firstName);
//       final EditText  lastName = view.findViewById(R.id.lastName);
//        final EditText useremail = view.findViewById(R.id.email);
//        final EditText userphone = view.findViewById(R.id.phone);
//        final EditText  userpassword = view.findViewById(R.id.password);
//        final EditText confirmPassword =view.findViewById(R.id.confirmPassword);
//
//        Button  signUp = view.findViewById(R.id.signUp);
//
//
//        builder.setView(view);
//        final AlertDialog dialog=builder.create();
//        dialog.show();
//        signUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String email = useremail.getText().toString().trim();
//                final String password = userpassword.getText().toString().trim();
//                final String cpassword = confirmPassword.getText().toString().trim();
//                final String fname = firstName.getText().toString().trim();
//                final String lname = lastName.getText().toString().trim();
//                final String phone = userphone.getText().toString().trim();
//
//
//                if (fname.isEmpty()) {
//                    firstName.setError("corent password");
//                    firstName.requestFocus();
//                    return;
//                }
//                if (lname.isEmpty()) {
//                    lastName.setError("corent password");
//                    lastName.requestFocus();
//                    return;
//                }
//
//                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                    useremail.setError("enter vaild email");
//                    useremail.requestFocus();
//                    return;
//                }
//                if (email.isEmpty()) {
//                    useremail.setError("Requer");
//                    useremail.requestFocus();
//                    return;
//                }
//                if (password.isEmpty()) {
//                    userpassword.setError("Requred");
//                    userpassword.requestFocus();
//                    return;
//                }
//                if (!cpassword.equals(password)) {
//                    userpassword.setError("Not Match");
//                    userpassword.requestFocus();
//                    return;
//                }
//                if (password.length() < 6) {
//                    userpassword.setError("length must 6 Number");
//                    userpassword.requestFocus();
//                    return;
//                }
//                if (phone.isEmpty()) {
//                    userphone.setError("Requred");
//                    userphone.requestFocus();
//                    return;
//                }
//                /*
//                if(phone.length()<11){
//                   userphone .setError("invalid phone");
//                   userphone.requestFocus();
//                    return;
//                }
//
//                 */
//                if (!Patterns.PHONE.matcher(phone).matches()) {
//                    userphone.setError("invalid phone");
//                    userphone.requestFocus();
//                    return;
//                }
//                auth.createUserWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(SplashActivity.this, new OnCompleteListener<AuthResult>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<AuthResult> task) {
//                                        if (task.isSuccessful()) {
//                                            String id = auth.getCurrentUser().getUid();
//                                            String token = FirebaseInstanceId.getInstance().getToken();
   //                                        DriverInfo driverInfo = new DriverInfo(  /*name,email,phone,id,password,"0.0"   */);
//                                            HashMap<String,Object> hashMap=new HashMap<>();
//                                            hashMap.put("name",fname +" "+lname);
//                                            hashMap.put("id",id);
//                                            hashMap.put("email",email);
//                                            hashMap.put("password",password);
//                                            hashMap.put("phone",phone);
//                                            hashMap.put("rating","0.0");
//                                            hashMap.put("image","default");
//                                            hashMap.put("token",token);
//
//
//                                            databaseReference.child(auth.getCurrentUser().getUid()).setValue(hashMap);
//                                            dialog.dismiss();
//
//                                        } else {
//                                            // If sign in fails, display a message to the user.
//                                            Toast.makeText(SplashActivity.this, "Sign Up Failed Failed", Toast.LENGTH_SHORT).show();
//                                        }
//
//                                        // ...
//                                    }
//                                });
//
//
//                /*
//
//
//
//                    databaseReference.child(auth.getCurrentUser().getUid()).setValue(driverInfo).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            dialog.dismiss();
//                            Toast.makeText(SplashActivity.this, "faild!", Toast.LENGTH_SHORT).show();
//
//                        }
//
//                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(SplashActivity.this, "Register Succesfully! ", Toast.LENGTH_SHORT).show();
//                           dialog.dismiss();
//                        GotoHomeActivity(driverInfo);
//                        }
//                    });
//
//                }
//                */
//            }
//        });
//
//
//
//
//    }

    private void showloginlayout() {


       final EditText username= findViewById(R.id.user_name);
        final EditText passwordd=findViewById(R.id.pass_word);
        TextView forgetpassword=findViewById(R.id.forget_password);
         Button sinin= findViewById(R.id.sin_in);
        TextView signUp = findViewById(R.id.sin_up_text);
forgetpassword.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
ResetPassword();
    }
});

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(SplashActivity.this,SignUp.class);
                startActivity(intent);
                finish();

            }
        });
        sinin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(!isConnected(SplashActivity.this)){
                    Toast.makeText(SplashActivity.this, " Please check Internet Connection!", Toast.LENGTH_SHORT).show();

                }
                else {
                    String email = username.getText().toString().trim();
                    String password = passwordd.getText().toString().trim();

                    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        username.setError("enter vaild email");
                        username.requestFocus();
                        return;
                    }
                    if(email.isEmpty()){
                        username .setError("Requer");
                        username.requestFocus();
                        return;
                    }
                    if(password.isEmpty()){
                        passwordd .setError("Requred");
                        passwordd.requestFocus();
                        return;
                    }

                    if(password.length()<6){
                        passwordd .setError("length must 6 Number");
                        passwordd.requestFocus();
                        return;
                    }

                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {


                                Intent intent=new Intent(SplashActivity.this,DriverHomeActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(v.getContext(), "failed password", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }



            }
        });



    }
    private boolean isConnected(SplashActivity splashActivity) {
        ConnectivityManager connectivityManager= (ConnectivityManager) splashActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifiConn!=null&&wifiConn.isConnected())||(mobileConn!=null&&mobileConn.isConnected())){
            return true;
        }else {
            return false;
        }

    }

 /*
    private void desplaysplash() {

        progressBar.setVisibility(View.VISIBLE);
        Completable.timer(5, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Toast.makeText(SplashActivity.this, " splash done", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==requst_cod) {
            IdpResponse response= IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser user=  FirebaseAuth.getInstance().getCurrentUser();



            }else
                Toast.makeText(this, "faild to sign in", Toast.LENGTH_SHORT).show();
        }
    }

     */
}
//public class SplashActivity extends AppCompatActivity