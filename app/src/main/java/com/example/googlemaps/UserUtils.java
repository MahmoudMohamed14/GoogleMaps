package com.example.googlemaps;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.googlemaps.EventBus.DriverRequestRecieve;
import com.example.googlemaps.EventBus.NotifyToRiderEvent;
import com.example.googlemaps.Model.FCMResponse;
import com.example.googlemaps.Model.FCMSendData;
import com.example.googlemaps.Model.TokenModel;
import com.example.googlemaps.Remote.IFCMService;
import com.example.googlemaps.Remote.RetrofitFCMClient;
import com.example.googlemaps.Services.MyFirebaseMessagingService;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class UserUtils {

    public static void updateToken(final Context context, String token) {
        TokenModel tokenModel=new TokenModel(token);
        FirebaseDatabase.getInstance()
                .getReference(Common.TOKEN_REFRANCE)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(tokenModel)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }


    public static void sendDeslineRequest(View view, Context context, String key) {
        CompositeDisposable compositeDisposable=new CompositeDisposable();
        IFCMService ifcmService= RetrofitFCMClient.getInstance().create(IFCMService.class);
        FirebaseDatabase.getInstance().getReference(Common.TOKEN_REFRANCE).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    TokenModel tokenModel=snapshot.getValue( TokenModel.class);
                    Map<String,String> notification=new HashMap<>();
                    notification.put(Common.NOTI_TITLE,Common.REQUEST_DRIVER_DECLINE);
                    notification.put(Common.NOTI_CONTANT,"This message repre sent for action driver decline");
                    notification.put(Common.DRIVER_KEY,FirebaseAuth.getInstance().getCurrentUser().getUid());

                    FCMSendData fcmSendData=new FCMSendData(tokenModel.getToken(),notification);
                    compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<FCMResponse>() {
                                @Override
                                public void accept(FCMResponse fcmResponse) throws Exception {
                                    if(fcmResponse.getSuccess()==0){
                                        compositeDisposable.clear();
                                        Snackbar.make(view,context.getString(R.string.decline_failed),Snackbar.LENGTH_LONG).show();

                                    }else {
                                        Snackbar.make(view,context.getString(R.string.decline_success),Snackbar.LENGTH_LONG).show();
                                    }

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    compositeDisposable.clear();
                                    Snackbar.make(view,throwable.getMessage()+"here 88",Snackbar.LENGTH_LONG).show();

                                }
                            }));
                }
                else {
                    compositeDisposable.clear();
                    Snackbar.make(view,context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                compositeDisposable.clear();
                Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();

            }
        });

    }

    public static void sendAcceptRequestToRider(View view, Context context, String key, String tripNumberId) {
        CompositeDisposable compositeDisposable=new CompositeDisposable();
        IFCMService ifcmService= RetrofitFCMClient.getInstance().create(IFCMService.class);
        FirebaseDatabase.getInstance().getReference(Common.TOKEN_REFRANCE).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    TokenModel tokenModel=snapshot.getValue( TokenModel.class);
                    Map<String,String> notification=new HashMap<>();
                    notification.put(Common.NOTI_TITLE,Common.REQUEST_DRIVER_ACCEPT);
                    notification.put(Common.NOTI_CONTANT,"This message repre sent for action driver accept");
                    notification.put(Common.DRIVER_KEY,FirebaseAuth.getInstance().getCurrentUser().getUid());
                    notification.put(Common.TRIP_KEY,tripNumberId);
                    FCMSendData fcmSendData=new FCMSendData(tokenModel.getToken(),notification);
                    compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<FCMResponse>() {
                                @Override
                                public void accept(FCMResponse fcmResponse) throws Exception {
                                    if(fcmResponse.getSuccess()==0){
                                        compositeDisposable.clear();
                                        Snackbar.make(view,context.getString(R.string.accept_failed),Snackbar.LENGTH_LONG).show();

                                    }

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    compositeDisposable.clear();
                                    Snackbar.make(view,throwable.getMessage()+"here 88",Snackbar.LENGTH_LONG).show();

                                }
                            }));
                }
                else {
                    compositeDisposable.clear();
                    Snackbar.make(view,context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                compositeDisposable.clear();
                Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();

            }
        });
    }

    public static void sendNotifytoReder(Context context,View view, String key) {

        CompositeDisposable compositeDisposable=new CompositeDisposable();
        IFCMService ifcmService= RetrofitFCMClient.getInstance().create(IFCMService.class);
        FirebaseDatabase.getInstance().getReference(Common.TOKEN_REFRANCE).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    TokenModel tokenModel=snapshot.getValue( TokenModel.class);
                    Map<String,String> notification=new HashMap<>();
                    notification.put(Common.NOTI_TITLE,context.getString(R.string.driver_arrived));
                    notification.put(Common.NOTI_CONTANT,context.getString(R.string.your_driver_arrived));
                    notification.put(Common.DRIVER_KEY,FirebaseAuth.getInstance().getCurrentUser().getUid());
                    notification.put(Common.RiDER_KEY,key);
                    FCMSendData fcmSendData=new FCMSendData(tokenModel.getToken(),notification);
                    compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<FCMResponse>() {
                                @Override
                                public void accept(FCMResponse fcmResponse) throws Exception {
                                    if(fcmResponse.getSuccess()==0){
                                        compositeDisposable.clear();
                                        Snackbar.make(view,context.getString(R.string.accept_failed),Snackbar.LENGTH_LONG).show();

                                    }else {
                                        EventBus.getDefault().postSticky(new NotifyToRiderEvent());
                                    }

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    compositeDisposable.clear();
                                    Snackbar.make(view,throwable.getMessage()+"here 88",Snackbar.LENGTH_LONG).show();

                                }
                            }));
                }
                else {
                    compositeDisposable.clear();
                    Snackbar.make(view,context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                compositeDisposable.clear();
                Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();

            }
        });
    }

    public static void sendDeslineAndRemoveTripRequest(View view, Context context, String key, String tripNumberId) {
        CompositeDisposable compositeDisposable=new CompositeDisposable();
        IFCMService ifcmService= RetrofitFCMClient.getInstance().create(IFCMService.class);
        FirebaseDatabase.getInstance().getReference(Common.Trips).child(tripNumberId)
                .removeValue()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      Snackbar.make(view,e.getMessage(),Snackbar.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseDatabase.getInstance().getReference(Common.TOKEN_REFRANCE).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            TokenModel tokenModel=snapshot.getValue( TokenModel.class);
                            Map<String,String> notification=new HashMap<>();
                            notification.put(Common.NOTI_TITLE,Common.REQUEST_DRIVER_DECLINE_AND_REMOVE_TRIP);
                            notification.put(Common.NOTI_CONTANT,"This message repre sent for action driver decline");
                            notification.put(Common.DRIVER_KEY,FirebaseAuth.getInstance().getCurrentUser().getUid());


                            FCMSendData fcmSendData=new FCMSendData(tokenModel.getToken(),notification);
                            compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<FCMResponse>() {
                                        @Override
                                        public void accept(FCMResponse fcmResponse) throws Exception {
                                            if(fcmResponse.getSuccess()==0){
                                                compositeDisposable.clear();
                                                Snackbar.make(view,context.getString(R.string.decline_failed),Snackbar.LENGTH_LONG).show();

                                            }else {
                                                Snackbar.make(view,context.getString(R.string.decline_success),Snackbar.LENGTH_LONG).show();
                                            }

                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            compositeDisposable.clear();
                                            Snackbar.make(view,throwable.getMessage()+"here 88",Snackbar.LENGTH_LONG).show();

                                        }
                                    }));
                        }
                        else {
                            compositeDisposable.clear();
                            Snackbar.make(view,context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        compositeDisposable.clear();
                        Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();

                    }
                });

            }
        });

    }

    public static void sendCompleteTripToRider(View view, Context context, String key, String tripNumberId) {
        CompositeDisposable compositeDisposable=new CompositeDisposable();
        IFCMService ifcmService= RetrofitFCMClient.getInstance().create(IFCMService.class);

                FirebaseDatabase.getInstance().getReference(Common.TOKEN_REFRANCE).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            TokenModel tokenModel=snapshot.getValue( TokenModel.class);
                            Map<String,String> notification=new HashMap<>();
                            notification.put(Common.NOTI_TITLE,Common.RIDER_COMPLETE_TRIP);
                            notification.put(Common.NOTI_CONTANT,"This message repre sent for action driver Complete");
                            notification.put(Common.TRIP_KEY,tripNumberId);

                            FCMSendData fcmSendData=new FCMSendData(tokenModel.getToken(),notification);
                            compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<FCMResponse>() {
                                        @Override
                                        public void accept(FCMResponse fcmResponse) throws Exception {
                                            if(fcmResponse.getSuccess()==0){
                                                compositeDisposable.clear();
                                                Snackbar.make(view,context.getString(R.string.complete_trip_failed),Snackbar.LENGTH_LONG).show();

                                            }else {
                                                Snackbar.make(view,context.getString(R.string.complete_trip_success),Snackbar.LENGTH_LONG).show();
                                            }

                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            compositeDisposable.clear();
                                            Snackbar.make(view,throwable.getMessage()+"here 88",Snackbar.LENGTH_LONG).show();

                                        }
                                    }));
                        }
                        else {
                            compositeDisposable.clear();
                            Snackbar.make(view,context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        compositeDisposable.clear();
                        Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();

                    }
                });

            }

}
