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

import kotlin.UInt;

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
                DriverRequestRecieve driverRequestRecieve=new DriverRequestRecieve();
                driverRequestRecieve.setKey(dataReceive.get(Common.RiDER_KEY));
                driverRequestRecieve.setCartype(dataReceive.get(Common.TYPE_CAR));
                driverRequestRecieve.setPickuplocation(dataReceive.get(Common.RIDER_PICKUP_LOCATION));
                driverRequestRecieve.setPickuplocationstring(dataReceive.get(Common.RIDER_PICKUP_LOCATION_STRING));
                driverRequestRecieve.setDestinationlocation(dataReceive.get(Common.RIDER_DESTINATION));
                driverRequestRecieve.setDestinationlocationstring(dataReceive.get(Common.RIDER_DESTINATION_STRING));
                EventBus.getDefault().postSticky(driverRequestRecieve);

            }else {


                Common.ShowNotfication(this, new Random().nextInt(),
                        dataReceive.get(Common.NOTI_TITLE),
                        dataReceive.get(Common.NOTI_CONTANT), null);
            }

        }
    }
}
