package com.example.googlemaps.Services;

import androidx.annotation.NonNull;

import com.example.googlemaps.Common;
import com.example.googlemaps.EventBus.DriverRequestRecieve;
import com.example.googlemaps.UserUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            UserUtils.updateToken(this,s);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String,String>dataReceive=remoteMessage.getData();
        if(dataReceive!=null){
            if(dataReceive.get(Common.NOTI_TITLE).equals(Common.REQUEST_DRIVER_TITLE)){
                EventBus.getDefault().postSticky(new DriverRequestRecieve(dataReceive.get(Common.RiDER_KEY)
                        ,dataReceive.get(Common.RIDER_PICKUP_LOCATION),dataReceive.get("TYPE_CAR")));

            }else {


                Common.ShowNotfication(this, new Random().nextInt(),
                        dataReceive.get(Common.NOTI_TITLE),
                        dataReceive.get(Common.NOTI_CONTANT), null);
            }

        }
    }
}
