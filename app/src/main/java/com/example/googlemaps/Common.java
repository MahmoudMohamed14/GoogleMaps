package com.example.googlemaps;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.googlemaps.Model.DriverInfo;
import com.example.googlemaps.Services.MyFirebaseMessagingService;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Common {
    public static final String DRIVER_KEY ="DriverKey" ;
    public static final String REQUEST_DRIVER_ACCEPT ="Accept" ;
    public static final String TRIP_KEY ="TripKey" ;
    public static final String TRIP_PICKUP_REF = "TripPickupLocation";
    public static final double MIN_RANGE_PICKUP_IN_KM =.05 ;//50m
    public static final int WAIT_TIME_IN_MIN =1 ;
    public static final String RIDER_DESTINATION_LOCATION_REF= "TripDestinationLocation";
    public static final String REQUEST_DRIVER_DECLINE_AND_REMOVE_TRIP ="DeclineAndRemoveTrip" ;
    public static final String RIDER_COMPLETE_TRIP ="RiderCompleteTrip" ;
    public  static    String  DRIVER__INFO="";
    public static   final String LOCATION_REFERANCE_Driver="DriverLocation";
    public static   final String LOCATION_REFERANCE_mechanic="MechanicLocation";
    public static   final String LOCATION_REFERANCE_winch="RescueWinchLocation";
    public static final String TOKEN_REFRANCE = "Token";
    public static final String NOTI_TITLE = "title";
    public static final String NOTI_CONTANT ="body" ;
    public static DriverInfo driverInfo;
    public static final String RIDER_PICKUP_LOCATION ="PickupLocation" ;
    public static final String RiDER_KEY = "RiderKey";
    public static final String REQUEST_DRIVER_TITLE ="RequestDriver" ;
    public static final String REQUEST_DRIVER_DECLINE="Decline" ;
    public static final String RIDER_PICKUP_LOCATION_STRING = "PickupLocationString" ;
    public static final String RIDER_DESTINATION_STRING ="DestinationLocationString" ;
    public static final String RIDER_DESTINATION = "DestinationLocation";
    public static final String TYPE_CAR="TYPE_CAR";
    public static final String RIDER__INFO ="Riders" ;
    public static String Trips="Trips";

    //DECODE POLY
    public static List<LatLng> decodePoly(String encoded) {
        List poly = new ArrayList();
        int index=0,len=encoded.length();
        int lat=0,lng=0;
        while(index < len)
        {
            int b,shift=0,result=0;
            do{
                b=encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift+=5;

            }while(b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1):(result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do{
                b = encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift +=5;
            }while(b >= 0x20);
            int dlng = ((result & 1)!=0 ? ~(result >> 1): (result >> 1));
            lng +=dlng;

            LatLng p = new LatLng((((double)lat / 1E5)),
                    (((double)lng/1E5)));
            poly.add(p);
        }
        return poly;
    }

    public static String buildWelcomeMessage() {
        if(Common.driverInfo !=null){
            return  new StringBuilder("Welcome ")
                    .append(Common.driverInfo.getName()).toString();
        }
        else
            return " ";
    }


    public static void ShowNotfication(Context context, int id, String title, String body, Intent intent) {
        PendingIntent pendingIntent=null;
        if(intent!=null)
            pendingIntent=PendingIntent.getActivity(context,id,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        String NOTIFICATION_CHANNEL_ID="car_rescue_remake";
        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "CARRESCUE Remake",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("CARRESCUE Remake");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);

        }
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title);
        builder.setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.ic_baseline_directions_car_24)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_baseline_directions_car_24));
        if(pendingIntent!=null){
            builder.setContentIntent(pendingIntent);
        }
        Notification notification=builder.build();
        notificationManager.notify(id,notification);



    }

    public static String createUniqueTripeIdNumber(long timeoffset) {
        Random random= new Random();
        Long current=System.currentTimeMillis()+timeoffset;
        Long unique=current+random.nextLong();
        if(unique<0) unique*=-1;
        return String.valueOf(unique);
    }
}
