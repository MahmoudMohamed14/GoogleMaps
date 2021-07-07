package com.example.googlemaps;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.googlemaps.Model.DriverInfo;
import com.example.googlemaps.ui.home.HomeFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.googlemaps.ui.home.HomeFragment.currnetRef;


@SuppressWarnings("unchecked")
public class DriverHomeActivity extends AppCompatActivity {




    private static final int PICK_IMAGE_REQUST = 3 ;
    private AppBarConfiguration mAppBarConfiguration;
    NavigationView navigationView;
    DrawerLayout drawer;
    AlertDialog waitdialog;
    Uri uriprofile;
    StorageTask storageTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String uridawnlaod;
    CircleImageView image_profile;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        storageReference=FirebaseStorage.getInstance().getReference("uploads");
        mStorageRef= FirebaseStorage.getInstance().getReference();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        init();
    }

    private void init() {
        waitdialog = new AlertDialog.Builder(this).setCancelable(false)
                .setMessage("waiting...").create();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.nav_sign_out){
                    //sign out message
                    AlertDialog.Builder builder=new     AlertDialog.Builder(DriverHomeActivity.this);
                    builder.setTitle("Sign Out").setMessage("Do you really want to Sign out").setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("SIGN OUT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            FirebaseAuth.getInstance().signOut();
                            if(currnetRef!=null)
                                currnetRef.removeValue();

                            Intent intent=new Intent(DriverHomeActivity.this,SplashActivity.class);
                           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        }
                    }).setCancelable(false);
                     final AlertDialog dialog=builder.create();
                    //control button
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface DialogInterface) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(DriverHomeActivity.this,android.R.color.holo_red_dark));
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(DriverHomeActivity.this,R.color.colorAccent));

                        }
                    });
                    dialog.show();

                }
                return true;
            }
        });
        View headerView=navigationView.getHeaderView(0);
        TextView name=headerView.findViewById(R.id.name_profile);
        TextView phone=headerView.findViewById(R.id.phone_profile);
        TextView star=headerView.findViewById(R.id.star_profile);

         image_profile=headerView.findViewById(R.id.image_profile);
         FirebaseDatabase.getInstance().getReference(Common.DRIVER__INFO).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if(snapshot.exists()){
                     Common.driverInfo=snapshot.getValue(DriverInfo.class);
                     name.setText(Common.driverInfo.getName());
                     phone.setText(Common.driverInfo.getPhone());
                     if (!Common.driverInfo.getImage().equals("default")) {
                         Glide.with(DriverHomeActivity.this).load(Common.driverInfo.getImage()).into(image_profile);
                     }

                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });






        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showimage();

            }
        });
//      name.setText(Common.buildWelcomeMessage());
//       // phone.setText(Common.driverInfo!=null? Common.driverInfo.getPhone():"010987 ");
//        star.setText(Common.driverInfo!=null? Common.driverInfo.getRating():"0.0 ");




    }
    private    String getfileextention(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap typeMap=MimeTypeMap.getSingleton();
        return typeMap.getExtensionFromMimeType(cr.getType(uri));

    }

    private void showimage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUST);
    }


    private void uploadimage() {
        AlertDialog.Builder builder=new     AlertDialog.Builder(DriverHomeActivity.this);
        builder.setTitle("Change Image")
                .setMessage("Do you really want to Change Image ")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("UPLOAD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(uriprofile!=null){
                    waitdialog.setMessage("Uploading...");
                    waitdialog.show();
                }
                String unique_name=FirebaseAuth.getInstance().getCurrentUser().getUid();
                final StorageReference imagefolder=mStorageRef.child("image/"+unique_name);
                imagefolder.putFile(uriprofile).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitdialog.dismiss();
                        Snackbar.make(drawer,e.getMessage(),Snackbar.LENGTH_LONG).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            imagefolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HashMap<String,Object> updatedata=new HashMap<>() ;
                                    updatedata.put("image",uri.toString());
                                    FirebaseDatabase.getInstance().getReference(Common.DRIVER__INFO)
                                            .child(FirebaseAuth.getInstance().getCurrentUser()
                                                    .getUid()).updateChildren(updatedata);
                                }
                            });
                        }
                        waitdialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress=(100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                        waitdialog.setMessage(new StringBuilder("Uploading: ").append(progress).append("%"));
                    }
                });

            }
        }).setCancelable(false);
        final AlertDialog dialog=builder.create();
        //control button
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface DialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(DriverHomeActivity.this,android.R.color.holo_red_dark));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(DriverHomeActivity.this,R.color.colorAccent));

            }
        });
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUST&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            if(data!=null&&data.getData()!=null){
                uriprofile = data.getData();
                Glide.with(this).load(uriprofile).into(image_profile);
                uploadimage();
            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.off_location:
                if(currnetRef!=null) currnetRef.removeValue();
               return true;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
