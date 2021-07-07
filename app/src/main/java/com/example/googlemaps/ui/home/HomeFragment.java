package com.example.googlemaps.ui.home;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.googlemaps.Common;

import com.example.googlemaps.EventBus.DriverRequestRecieve;
import com.example.googlemaps.EventBus.NotifyToRiderEvent;
import com.example.googlemaps.MechanicInfo;
import com.example.googlemaps.Model.DriverInfo;
import com.example.googlemaps.Model.RiderModel;
import com.example.googlemaps.Model.TripPlanModel;
import com.example.googlemaps.R;
import com.example.googlemaps.Remote.IgoogleApi;
import com.example.googlemaps.Remote.RetrofitClient;
import com.example.googlemaps.UserUtils;
import com.example.googlemaps.Utils.LocationUtils;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.SnackbarContentLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kusu.library.LoadingButton;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    @BindView(R.id.chip_decline)
    Chip chip_decline;
    @BindView(R.id.layout_accept)
    CardView layout_accept;
    @BindView(R.id.circularProgressBar)
    CircularProgressBar circularProgressBar;
    @BindView(R.id.txt_estimate_time)
    TextView txt_estimate_time;
    @BindView(R.id.txt_estimate_distance)
    TextView txt_estimate_distance;
    @BindView(R.id.root_layout)
    FrameLayout root_layout;

    @BindView(R.id.txt_ratting)
    TextView txt_ratting;
    @BindView(R.id.txt_type_carrescue)
    TextView txt_type_carrescue;
    @BindView(R.id.img_round)
    ImageView img_round;
    @BindView(R.id.layout_start_carRescue)
    CardView layout_start_carRescue;
    @BindView(R.id.txt_rider_name)
    TextView txt_rider_name;
    @BindView(R.id.txt_start_carrescue_estimate_distance)
    TextView txt_start_carrescue_estimate_distance;
    @BindView(R.id.txt_start_carrescue_estimate_time)
    TextView txt_start_carrescue_estimate_time;
    @BindView(R.id.img_phone_call)
    ImageView img_phone_call;
    @BindView(R.id.btn_start_carrescue)
    LoadingButton btn_start_carrescue;
    @BindView(R.id.btn_complete_trip)
    LoadingButton btn_complete_trip;
    @BindView(R.id.layout_notify_rider)
    LinearLayout layout_notify_rider;
    @BindView(R.id.txt_notify_rider)
    TextView txt_notify_rider;
    @BindView(R.id.progress_notify)
    ProgressBar progress_notify;

    @BindView(R.id.choose_owner)
    CardView choose_owner;
    @BindView(R.id.owner_car)
    Button owner_car;
    @BindView(R.id.owner_mechanic)
    Button owner_mechanic;
    @BindView(R.id.owner_winch)
    Button owner_winch;
    @BindView(R.id.relative_type_car)
    RelativeLayout relative_type_car;
    @BindView(R.id.txt_start_type_car)
    TextView txt_start_type_car;
    public int pointer;


    private String tripNumberId = "";
    private boolean isTripStart = false, onLineSystemAlreadyRegister = false;
    private GeoFire geoFire, pickupGeofire, destinationGeoFire;
    private GeoQuery pickupGeoQuery, destinationGeoQuery;
    private GeoQueryEventListener pickupGeoQueryListener = new GeoQueryEventListener() {
        @Override
        public void onKeyEntered(String key, GeoLocation location) {
//            if(pointer==1||pointer==3){
//                btn_start_carrescue.setEnabled(true);
//            }else  if(pointer==2){
//                btn_complete_trip.setEnabled(true);
//            }
          //when driver arrived in pickup location ,can star trip;
            UserUtils.sendNotifytoReder(getContext(), root_layout, key);
            if (pickupGeoQuery != null) {
                //remove geofire
                pickupGeofire.removeLocation(key);
                pickupGeofire = null;
                pickupGeoQuery.removeAllListeners();
            }
        }

        @Override
        public void onKeyExited(String key) {
          //  btn_start_carrescue.setEnabled(false);

        }

        @Override
        public void onKeyMoved(String key, GeoLocation location) {

        }

        @Override
        public void onGeoQueryReady() {

        }

        @Override
        public void onGeoQueryError(DatabaseError error) {

        }
    };
    private GeoQueryEventListener destinationGeoQueryListener = new GeoQueryEventListener() {
        @Override
        public void onKeyEntered(String key, GeoLocation location) {
//            btn_complete_trip.setEnabled(true);
            if (destinationGeoQuery != null) {
                destinationGeoFire.removeLocation(key);
                destinationGeoFire = null;
                destinationGeoQuery.removeAllListeners();

            }
        }

        @Override
        public void onKeyExited(String key) {

        }

        @Override
        public void onKeyMoved(String key, GeoLocation location) {

        }

        @Override
        public void onGeoQueryReady() {

        }

        @Override
        public void onGeoQueryError(DatabaseError error) {

        }
    };
    private CountDownTimer wait_time;
    private String refer = "";
    private String cityName="";
    private DatabaseReference myRef;

    @OnClick(R.id.chip_decline)
    void onCliclkDecline() {
        if (driverRequestRecieve != null) {
            if (TextUtils.isEmpty(tripNumberId)) {
                if (coundDownEvent != null)
                    coundDownEvent.dispose();
                chip_decline.setVisibility(View.GONE);
                layout_accept.setVisibility(View.GONE);
                mMap.clear();
                UserUtils.sendDeslineRequest(root_layout, getContext(), driverRequestRecieve.getKey());
                driverRequestRecieve = null;

            } else {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                mfusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                }).addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                      try{


                            MakeDriverOnline(location,refer);
                            UserUtils.sendDeslineAndRemoveTripRequest(root_layout, getContext(), driverRequestRecieve.getKey(), tripNumberId);
                      } catch (Exception e) {
                          Snackbar.make(mapFragment.requireView(),e.getMessage(), Snackbar.LENGTH_LONG).show();
                      }



                        chip_decline.setVisibility(View.GONE);
                        layout_start_carRescue.setVisibility(View.GONE);
                        mMap.clear();

                        tripNumberId = "";//set trip to impty
                        driverRequestRecieve = null;





                    }
                });
            }

        }

    }

    @OnClick(R.id.btn_start_carrescue)
    void onClickStart() {
        //clear route
        if (balckpolyline != null) balckpolyline.remove();
        if (greypolyline != null) greypolyline.remove();
        //cancel wait time
        if (wait_time != null) wait_time.cancel();
        layout_notify_rider.setVisibility(View.GONE);
        //up code use  in complete in mechanic ^^^
        if (driverRequestRecieve != null) {
            LatLng destinationlatlng = new LatLng(Double.parseDouble(driverRequestRecieve.getDestinationlocation().split(",")[0])
                    , Double.parseDouble(driverRequestRecieve.getDestinationlocation().split(",")[1]));
            mMap.addMarker(new MarkerOptions().position(destinationlatlng).title(driverRequestRecieve.getDestinationlocationstring())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            drawPathFromCurrentLocation(driverRequestRecieve.getDestinationlocation());

        }
        btn_start_carrescue.setVisibility(View.GONE);
        btn_complete_trip.setVisibility(View.VISIBLE);
        chip_decline.setVisibility(View.GONE);

    }

    @OnClick(R.id.btn_complete_trip)
    void onClickComplete() {
        //put isDone on tripinformation in firebase
        Map<String, Object> update_trip = new HashMap<>();
        update_trip.put("done", true);
        FirebaseDatabase.getInstance().getReference(Common.Trips).child(tripNumberId).updateChildren(update_trip)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(mapFragment.requireView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //go to location
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(requireView(), getString(R.string.permission_require), Snackbar.LENGTH_LONG).show();

                    return;
                }
                mfusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        UserUtils.sendCompleteTripToRider(mapFragment.requireView(),getContext(),driverRequestRecieve.getKey(),tripNumberId);
                        mMap.clear();
                        layout_notify_rider.setVisibility(View.GONE);
                        tripNumberId="";
                        isTripStart=false;
                        chip_decline.setVisibility(View.GONE);
                        layout_accept.setVisibility(View.GONE);
                        layout_start_carRescue.setVisibility(View.GONE);
                        circularProgressBar.setProgress(0);
                        progress_notify.setProgress(0);
                      //  btn_complete_trip.setEnabled(false);
                       // btn_complete_trip.setVisibility(View.GONE);
                       // btn_start_carrescue.setEnabled(false);
                       // btn_start_carrescue.setVisibility(View.VISIBLE);
                        destinationGeoFire=null;
                        pickupGeofire=null;
                        driverRequestRecieve=null;

                        MakeDriverOnline(location,refer);
                    }
                });

            }
        });

    }


    private void drawPathFromCurrentLocation(String destinationlocation) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(requireView(), getString(R.string.permission_require), Snackbar.LENGTH_LONG).show();
            return;
        }
        //get current location
        mfusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(requireView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                // Request Api
                compositeDisposable.add(igoogleApi.getDirections("driving", "less_driving",
                        new StringBuilder()
                                .append(location.getLatitude())
                                .append(",")
                                .append(location.getLongitude())
                                .toString(),
                        destinationlocation,



                        getString(R.string.google_api_key))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String returnRusult) throws Exception {


                                try {
                                    JSONObject jsonObject = new JSONObject(returnRusult);
                                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject route = jsonArray.getJSONObject(i);
                                        JSONObject poly = route.getJSONObject("overview_polyline");
                                        String polyline = poly.getString("points");
                                        polylinelist = Common.decodePoly(polyline);

                                    }
                                    //moving
                                    polylineOptions = new PolylineOptions();
                                    polylineOptions.color(Color.GRAY);
                                    polylineOptions.width(12);
                                    polylineOptions.startCap(new SquareCap());
                                    polylineOptions.jointType(JointType.ROUND);
                                    polylineOptions.addAll(polylinelist);
                                    greypolyline = mMap.addPolyline(polylineOptions);
                                    blaclpolylineoption = new PolylineOptions();
                                    blaclpolylineoption.color(Color.BLACK);
                                    blaclpolylineoption.width(5);
                                    blaclpolylineoption.startCap(new SquareCap());
                                    blaclpolylineoption.jointType(JointType.ROUND);
                                    blaclpolylineoption.addAll(polylinelist);
                                    balckpolyline = mMap.addPolyline(blaclpolylineoption);
                                    //animator

                                    LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
                                    LatLng destination = new LatLng(Double.parseDouble(destinationlocation.split(",")[0]),
                                            Double.parseDouble(destinationlocation.split(",")[1]));
                                    LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                            .include(origin)
                                            .include(destination)
                                            .build();
                                    CreateGeoFileDestinationLocation(driverRequestRecieve.getKey(),destination);


                                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 160));
                                    mMap.moveCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom - 1));

                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "e.getMessage()", Toast.LENGTH_SHORT).show();


                                }

                            }


                        }));

            }
        });

    }

    private void CreateGeoFileDestinationLocation(String key, LatLng destination) {
       DatabaseReference ref=FirebaseDatabase.getInstance().getReference(Common.RIDER_DESTINATION_LOCATION_REF);
       destinationGeoFire=new GeoFire(ref);
       destinationGeoFire.setLocation(key, new GeoLocation(destination.latitude, destination.longitude), new GeoFire.CompletionListener() {
           @Override
           public void onComplete(String key, DatabaseError error) {

           }
       });
    }


    //route
    //routes
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IgoogleApi igoogleApi;
    private Polyline balckpolyline, greypolyline;
    private PolylineOptions polylineOptions, blaclpolylineoption;
    private List<LatLng> polylinelist;
    private DriverRequestRecieve driverRequestRecieve;
    private Disposable coundDownEvent;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private HomeViewModel homeViewModel;
    private GoogleMap mMap;
    LocationManager locationManager;
    private String DRIVER_LOCATION_REFERANCE;
    FusedLocationProviderClient mfusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback callback;
    SupportMapFragment mapFragment;
     DatabaseReference onlineRef, driverlocationRef;
     public static  DatabaseReference  currnetRef;
    DriverInfo driverInfo;


    private boolean isFirstTime = true;
    ValueEventListener onlineValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists() && currnetRef != null) {
                currnetRef.onDisconnect().removeValue();



            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Snackbar.make(mapFragment.getView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    };

    @Override
    public void onDestroy() {

        mfusedLocationProviderClient.removeLocationUpdates(callback);
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            geoFire.removeLocation(FirebaseAuth.getInstance().getCurrentUser().getUid());
        onlineRef.removeEventListener(onlineValueEventListener);


        if (EventBus.getDefault().hasSubscriberForEvent(DriverRequestRecieve.class))
            EventBus.getDefault().removeStickyEvent(DriverRequestRecieve.class);
        if (EventBus.getDefault().hasSubscriberForEvent(NotifyToRiderEvent.class))
            EventBus.getDefault().removeStickyEvent(NotifyToRiderEvent.class);
        EventBus.getDefault().unregister(this);
        onLineSystemAlreadyRegister=false;
        compositeDisposable.clear();


        super.onDestroy();
    }

    @Override
    public void onDetach() {

        if(currnetRef!=null) currnetRef.removeValue();
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        registeronlinesystem();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }


    }

    private void registeronlinesystem() {
        if(!onLineSystemAlreadyRegister){
            onlineRef.addValueEventListener(onlineValueEventListener);
            onLineSystemAlreadyRegister=true;
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initView(root);
        init();
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return root;
    }

    private void initView(View root) {
        ButterKnife.bind(this, root);

    }

    private void init() {

        igoogleApi = RetrofitClient.getInstance().create(IgoogleApi.class);
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(root_layout, getString(R.string.permission_require), Snackbar.LENGTH_LONG).show();
            return;

        }
//        onClickWinch();
//        onClickMechanic();
//        onClickCar();
        uploadAndDownlod();

    }

    public void onClickCar() {


        owner_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointer=1;
                refer=Common.LOCATION_REFERANCE_Driver;

                choose_owner.setVisibility(View.GONE);
                buildLocationRequest();

                buildLocationCallback(Common.LOCATION_REFERANCE_Driver);
                updateLocation();

//                databaseReference=FirebaseDatabase.getInstance().getReference( "DriverInfo");
//                databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(snapshot.exists()) {
//                            driverInfo = snapshot.getValue(DriverInfo.class);
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

            }
        });
    }

    public void onClickMechanic() {

        owner_mechanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointer=2;
                refer=Common.LOCATION_REFERANCE_mechanic;

                choose_owner.setVisibility(View.GONE);
                buildLocationRequest();

                buildLocationCallback(Common.LOCATION_REFERANCE_mechanic);
                updateLocation();
//                databaseReference=FirebaseDatabase.getInstance().getReference( "MechanicInfo");
//                databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(snapshot.exists()) {
//                            driverInfo = snapshot.getValue(DriverInfo.class);
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });


            }
        });

    }
    public void uploadAndDownlod(){

        FirebaseDatabase.getInstance().getReference(Common.DRIVER__INFO) .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    driverInfo=snapshot.getValue(DriverInfo.class);
                    if(driverInfo!=null) {
                        if (driverInfo.getService().equals("Driver")) {
                            pointer = 1;
                            refer = Common.LOCATION_REFERANCE_Driver;


                            buildLocationRequest();

                            buildLocationCallback(Common.LOCATION_REFERANCE_Driver);
                            updateLocation();

                        }
                        else if (driverInfo.getService().equals("Mechanic")) {
                            pointer = 2;
                            refer = Common.LOCATION_REFERANCE_mechanic;


                            buildLocationRequest();

                            buildLocationCallback(Common.LOCATION_REFERANCE_mechanic);
                            updateLocation();

                        }
                        else if (driverInfo.getService().equals("RescueWinch")) {
                            pointer = 3;
                            refer = Common.LOCATION_REFERANCE_winch;


                            buildLocationRequest();

                            buildLocationCallback(Common.LOCATION_REFERANCE_winch);
                            updateLocation();

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "some thing rong"+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

            }



    public void onClickWinch() {

        owner_winch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointer=3;
                refer=Common.LOCATION_REFERANCE_winch;

                choose_owner.setVisibility(View.GONE);
                buildLocationRequest();

                buildLocationCallback(Common.LOCATION_REFERANCE_winch);
                updateLocation();
//                databaseReference=FirebaseDatabase.getInstance() .getReference( "RescueWinchInfo");
//                databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(snapshot.exists()) {
//                            driverInfo = snapshot.getValue(DriverInfo.class);
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

            }
        });
    }
    private void dexter(  ){
        Dexter.withContext(getContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(final PermissionGrantedResponse permissionGrantedResponse) {


                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {

                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return true;
                        }


                        mfusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (mMap.isMyLocationEnabled()) {
                                    LatLng userlocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userlocation, 18f));
                                } else {
                                    Toast.makeText(getContext(), "ok ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                        return true;
                    }
                });
                //layoutbutton
                View locationbutton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent())
                        .findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) locationbutton.getLayoutParams();
                //right button
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                params.setMargins(0, 0, 0, 50);
                // move location
//                onClickWinch();
//                onClickMechanic();
//                onClickCar();
                uploadAndDownlod();

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(getContext(), "Permission Granter", Toast.LENGTH_SHORT).show();
                /*
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);

                 */

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();

            }
        }).check();
    }

    private void updateLocation() {
        if (mfusedLocationProviderClient == null) {
            mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(root_layout, getString(R.string.permission_require), Snackbar.LENGTH_LONG).show();
                return;
            }
            mfusedLocationProviderClient.requestLocationUpdates(locationRequest, callback, Looper.myLooper());

        }


    }

    private void buildLocationCallback(final String reference) {
        if (callback == null) {
            callback = new LocationCallback() {
                @Override
                public void onLocationResult(final LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    LatLng newpostion = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                    if(pickupGeofire!=null){//after create on firebase
                        pickupGeoQuery=pickupGeofire.queryAtLocation(new GeoLocation(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude())
                                ,Common.MIN_RANGE_PICKUP_IN_KM);
                        pickupGeoQuery.addGeoQueryEventListener(pickupGeoQueryListener);

                    }
                    if(destinationGeoFire!=null){//after create on firebase
                        destinationGeoQuery=destinationGeoFire.queryAtLocation(new GeoLocation(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude())
                                ,Common.MIN_RANGE_PICKUP_IN_KM);
                        destinationGeoQuery.addGeoQueryEventListener(destinationGeoQueryListener);

                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newpostion, 18f));
                    //we will get addriss name
                    MakeDriverOnline(locationResult.getLastLocation(),reference);
                    if(!isTripStart) {
                      MakeDriverOnline(locationResult.getLastLocation(),reference);
                    }else {
                        if(!TextUtils.isEmpty(tripNumberId)){
                            //update location driver
                            Map<String,Object>update_data=new HashMap<>();
                            update_data.put("currentLat",locationResult.getLastLocation().getLatitude());
                            update_data.put("currentLog",locationResult.getLastLocation().getLongitude());
                            FirebaseDatabase.getInstance().getReference(Common.Trips).child(tripNumberId).updateChildren(update_data)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Snackbar.make(mapFragment.getView(),e.getMessage(),Snackbar.LENGTH_LONG).show();

                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                        }
                    }


                }
            };
        }
    }

    private void MakeDriverOnline(Location location, String reference) {
        String saveCityName= cityName;
        cityName= LocationUtils.getAddressFromLocation(getContext(),location);
        if(!cityName.equals(saveCityName))//old and new location are equal
        {
            if(currnetRef!=null){
                currnetRef.removeValue().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(mapFragment.getView(),e.getMessage(),Snackbar.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateDriverLocation(location,reference);
                    }
                });
            }
        }else {
            updateDriverLocation(location,reference);
        }

    }

    private void updateDriverLocation(Location location, String reference) {
        if(!TextUtils.isEmpty(cityName)){
            driverlocationRef = FirebaseDatabase.getInstance().getReference(reference).child(cityName);

            currnetRef = driverlocationRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            geoFire = new GeoFire(driverlocationRef);

            //update location
            geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    new GeoLocation(location.getLatitude(),
                            location.getLongitude()),
                    new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error != null)
                                Snackbar.make(mapFragment.getView(), error.getMessage() + " her error", Snackbar.LENGTH_LONG).show();


                        }
                    });

            registeronlinesystem();//only register when setup
        }else {
            Snackbar.make(mapFragment.getView(),"sever unavailable her!",Snackbar.LENGTH_LONG).show();
        }
    }


    private void buildLocationRequest() {
        if (locationRequest == null) {
            locationRequest = new LocationRequest();
            locationRequest.setSmallestDisplacement(50f);
            locationRequest.setInterval(15000);
            locationRequest.setFastestInterval(10000);
            locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // mMap.getUiSettings().setZoomControlsEnabled(true);

        dexter( );



        try {
            Boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.uber_maps_style));
            if (!success) {
                Log.e("EDMT_ERROR", "style parsing Error");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("EDMT_ERROR", e.getMessage());
        }


        Snackbar.make(mapFragment.getView(), "you are online", Snackbar.LENGTH_LONG).show();


    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onDriverRequestRecieve(final DriverRequestRecieve event) {
        driverRequestRecieve = event;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(requireView(), getString(R.string.permission_require), Snackbar.LENGTH_LONG).show();
            return;
        }
        //get current location
        mfusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(requireView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                // Request Api
                compositeDisposable.add(igoogleApi.getDirections("driving", "less_driving",
                        new StringBuilder()
                                .append(location.getLatitude())
                                .append(",")
                                .append(location.getLongitude())
                                .toString(),
                        event.getPickuplocation(),


                        getString(R.string.google_api_key))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String returnRusult) throws Exception {


                                try {
                                    JSONObject jsonObject = new JSONObject(returnRusult);
                                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject route = jsonArray.getJSONObject(i);
                                        JSONObject poly = route.getJSONObject("overview_polyline");
                                        String polyline = poly.getString("points");
                                        polylinelist = Common.decodePoly(polyline);

                                    }
                                    //moving
                                    polylineOptions = new PolylineOptions();
                                    polylineOptions.color(Color.GRAY);
                                    polylineOptions.width(12);
                                    polylineOptions.startCap(new SquareCap());
                                    polylineOptions.jointType(JointType.ROUND);
                                    polylineOptions.addAll(polylinelist);
                                    greypolyline = mMap.addPolyline(polylineOptions);
                                    blaclpolylineoption = new PolylineOptions();
                                    blaclpolylineoption.color(Color.BLACK);
                                    blaclpolylineoption.width(5);
                                    blaclpolylineoption.startCap(new SquareCap());
                                    blaclpolylineoption.jointType(JointType.ROUND);
                                    blaclpolylineoption.addAll(polylinelist);
                                    balckpolyline = mMap.addPolyline(blaclpolylineoption);
                                    //animator
                                    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
                                    valueAnimator.setDuration(1100);
                                    valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
                                    valueAnimator.setInterpolator(new LinearInterpolator());
                                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            List<LatLng> points = greypolyline.getPoints();
                                            int percentvalue = (int) animation.getAnimatedValue();
                                            int size = points.size();
                                            int newsize = (int) (size * (percentvalue / 100.0f));
                                            List<LatLng> p = points.subList(0, newsize);
                                            balckpolyline.setPoints(p);

                                        }
                                    });
                                    valueAnimator.start();
                                    LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
                                    LatLng destination = new LatLng(Double.parseDouble(event.getPickuplocation().split(",")[0]),
                                            Double.parseDouble(event.getPickuplocation().split(",")[1]));
                                    LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                            .include(origin)
                                            .include(destination)
                                            .build();
                                    //add car icon for origin
                                    JSONObject object = jsonArray.getJSONObject(0);
                                    JSONArray legs = object.getJSONArray("legs");
                                    JSONObject legsobject = legs.getJSONObject(0);
                                    JSONObject time = legsobject.getJSONObject("duration");
                                    String duration = time.getString("text");

                                    JSONObject distanceEstimate = legsobject.getJSONObject("distance");
                                    String distance = distanceEstimate.getString("text");
                                    txt_estimate_time.setText(duration);
                                    txt_estimate_distance.setText(distance);

                                    mMap.addMarker(new MarkerOptions()
                                            .position(destination)
                                            .icon(BitmapDescriptorFactory.defaultMarker())
                                            .title("Pickup Location")
                                    );
                                    createGeofirePickupLocation(event.getKey(),destination);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 160));
                                    mMap.moveCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom - 1));
                                    //show layout
                                    chip_decline.setVisibility(View.VISIBLE);
                                    layout_accept.setVisibility(View.VISIBLE);
                                    //count down

                                    coundDownEvent = Observable.interval(100, TimeUnit.MILLISECONDS)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .doOnNext(new Consumer<Long>() {
                                                @Override
                                                public void accept(Long x) throws Exception {
                                                    circularProgressBar.setProgress(circularProgressBar.getProgress() + 1f);


                                                }
                                            }).takeUntil(new Predicate<Long>() {
                                                @Override
                                                public boolean test(Long along) throws Exception {
                                                    return along == 100;
                                                }
                                            })       //10 sc
                                            .doOnComplete(new Action() {
                                                @Override
                                                public void run() throws Exception {
//                                                      circularProgressBar.setProgress(0);
//                                                      Toast.makeText(getContext(), " Fake accept Action", Toast.LENGTH_SHORT).show();
                                                    createTripePlan(event, duration, distance);

                                                }
                                            }).subscribe();


                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "e.getMessage()", Toast.LENGTH_SHORT).show();


                                }

                            }


                        }));

            }
        });

    }

    private void createGeofirePickupLocation(String key, LatLng destination) {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference(Common.TRIP_PICKUP_REF);
        pickupGeofire=new GeoFire(ref);
        pickupGeofire.setLocation(key, new GeoLocation(destination.latitude, destination.longitude), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

                if(error!=null){
                    Snackbar.make(root_layout,error.getMessage(),Snackbar.LENGTH_LONG).show();
                }else{
                    Log.d("carrescue",key+"was create  success on geofire");
                }
            }
        });


    }

    private void createTripePlan(DriverRequestRecieve event, String duration, String distance) {
        setprocesslayout(true);
        //sync server time with device
        FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timeoffset = snapshot.getValue(Long.class);
                FirebaseDatabase.getInstance().getReference(Common.RIDER__INFO).child(event.getKey())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    //get rider information
                                    RiderModel riderModel = snapshot.getValue(RiderModel.class);
                                    //get location
                                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                            ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        Snackbar.make(mapFragment.getView(),getContext().getString(R.string.permission_require),Snackbar.LENGTH_LONG).show();
                                        return;
                                    }
                                    mfusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Snackbar.make(mapFragment.getView(),e.getMessage(),Snackbar.LENGTH_LONG).show();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            TripPlanModel tripPlanModel=new TripPlanModel();
                                            tripPlanModel.setDriver(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            tripPlanModel.setRider(event.getKey());
                                            tripPlanModel.setDriverInfo(driverInfo);
                                            tripPlanModel.setRiderModel(riderModel);
                                            tripPlanModel.setOrigin(event.getPickuplocation());
                                            tripPlanModel.setOriginString(event.getPickuplocationstring());
                                            tripPlanModel.setDestination(event.getDestinationlocation());
                                            tripPlanModel.setDestinationString(event.getDestinationlocationstring());
                                            tripPlanModel.setDistancePickup(distance);
                                            tripPlanModel.setDurationPickup(duration);
                                            tripPlanModel.setCurrentLat(location.getLatitude());
                                            tripPlanModel.setCurrentLog(location.getLongitude());
                                            tripNumberId = Common.createUniqueTripeIdNumber(timeoffset);
                                            FirebaseDatabase.getInstance().getReference(Common.Trips).child(tripNumberId).setValue(tripPlanModel).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Snackbar.make(mapFragment.getView(),e.getMessage(),Snackbar.LENGTH_LONG).show();
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    txt_rider_name.setText(riderModel.getName());
                                                    txt_start_carrescue_estimate_time.setText(duration);
                                                    txt_start_carrescue_estimate_distance.setText(distance);
                                                    if(pointer==2||pointer==3){
                                                        txt_start_type_car.setText(event.getCartype());
                                                    }else if(pointer==1){
                                                        relative_type_car.setVisibility(View.GONE);
                                                    }

                                                    setOfflineModeForDriver(event,duration,distance);

                                                }
                                            });


                                        }
                                    });

                              }else {
                                  Snackbar.make(mapFragment.getView(),getContext().getString(R.string.rider_not_found)+" " +event.getKey(),Snackbar.LENGTH_LONG).show();
                              }

                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError error) {
                              Snackbar.make(mapFragment.getView(),error.getMessage(),Snackbar.LENGTH_LONG).show();
                          }
                      });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(mapFragment.getView(),error.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setOfflineModeForDriver(DriverRequestRecieve event, String duration, String distance) {
        UserUtils.sendAcceptRequestToRider(mapFragment.getView(),getContext(),event.getKey(),tripNumberId);
        //go to offline
        if(currnetRef!=null)
            currnetRef.removeValue();
        setprocesslayout(false);
        layout_accept.setVisibility(View.GONE);
        layout_start_carRescue.setVisibility(View.VISIBLE);
        if(pointer==2){
            btn_complete_trip.setVisibility(View.VISIBLE);
            btn_start_carrescue.setVisibility(View.GONE);
        }
        isTripStart=true;
    }



    private void setprocesslayout(boolean isprocess) {
        int color=-1;
        if(isprocess) {
          color = ContextCompat.getColor(getContext(), R.color.dark_gray);
            circularProgressBar.setIndeterminateMode(true);
            txt_ratting.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_star_24_dark_gray, 0);
        }
             else {
            color = ContextCompat.getColor(getContext(), android.R.color.white);
            circularProgressBar.setIndeterminateMode(false);
            circularProgressBar.setProgress(0);
            txt_ratting.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_star_24, 0);

            }
        txt_estimate_distance.setTextColor(color);
        txt_estimate_time.setTextColor(color);
        ImageViewCompat.setImageTintList(img_round, ColorStateList.valueOf(color));
        txt_ratting.setTextColor(color);

        txt_type_carrescue.setTextColor(color);



    }


    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
   public void  onNotifyToRider(NotifyToRiderEvent event){
        layout_notify_rider.setVisibility(View.VISIBLE);
        progress_notify.setMax(Common.WAIT_TIME_IN_MIN*60);
        wait_time=new CountDownTimer(Common.WAIT_TIME_IN_MIN*60*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                progress_notify.setProgress(progress_notify.getProgress()+1);
                txt_notify_rider.setText(String.format("%02d:%02d",TimeUnit.MILLISECONDS.toMinutes(1)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(1)),
                        TimeUnit.MILLISECONDS.toSeconds(1)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(1))));


            }

            @Override
            public void onFinish() {
                Snackbar.make(root_layout, getString(R.string.time_over), Snackbar.LENGTH_LONG).show();
            }
        }.start();



    }
}