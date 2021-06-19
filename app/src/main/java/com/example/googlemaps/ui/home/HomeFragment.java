package com.example.googlemaps.ui.home;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.googlemaps.Common;
import com.example.googlemaps.DriverInfo;
import com.example.googlemaps.EventBus.DriverRequestRecieve;
import com.example.googlemaps.MechanicInfo;
import com.example.googlemaps.R;
import com.example.googlemaps.Remote.IgoogleApi;
import com.example.googlemaps.Remote.RetrofitClient;
import com.example.googlemaps.UserUtils;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    @BindView(R.id.choose_owner)
    LinearLayout choose_owner;
    @BindView(R.id.owner_car)
    Button owner_car;
    @BindView(R.id.owner_mechanic)
    Button owner_mechanic;
    @BindView(R.id.owner_winch)
    Button owner_winch;
   @OnClick(R.id.chip_decline)
   void onCliclkDecline(){
       if(driverRequestRecieve!=null){
           if(coundDownEvent!=null)
               coundDownEvent.dispose();
           chip_decline.setVisibility(View.GONE);
           layout_accept.setVisibility(View.GONE);
           mMap.clear();
           UserUtils.sendDeslineRequest(root_layout,getContext(),driverRequestRecieve.getKey());
           driverRequestRecieve=null;

       }

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

    private HomeViewModel homeViewModel;
    private GoogleMap mMap;
    LocationManager locationManager;
    private String  DRIVER_LOCATION_REFERANCE;
    FusedLocationProviderClient mfusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback callback;
    SupportMapFragment mapFragment;
    DatabaseReference onlineRef, currnetRef, driverlocationRef;
   public static GeoFire geoFire;
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
        EventBus.getDefault().unregister(this);
        compositeDisposable.clear();

        super.onDestroy();
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
        onlineRef.addValueEventListener(onlineValueEventListener);
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

       onClickCar();
        onClickMechanic();
        onClickWinch();





    }

    public void onClickCar(){

        owner_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose_owner.setVisibility(View.GONE);
                buildLocationRequest();
                buildLocationCallback(Common.LOCATION_REFERANCE_Driver);
                updateLocation();
            }
        });
    }

    public void onClickMechanic(){

        owner_mechanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose_owner.setVisibility(View.GONE);
                buildLocationRequest();
                buildLocationCallback(Common.LOCATION_REFERANCE_mechanic);
                updateLocation();
            }
        });

    }

    public void onClickWinch(){

        owner_winch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose_owner.setVisibility(View.GONE);
                buildLocationRequest();
                buildLocationCallback(Common.LOCATION_REFERANCE_winch);
                updateLocation();
            }
        });
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

    private void buildLocationCallback( final String reference) {
        if (callback == null) {
            callback = new LocationCallback() {
                @Override
                public void onLocationResult(final LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    LatLng newpostion = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newpostion, 18f));
                    //we will get addriss name
                  Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    List<Address> addressList;
                    try {

                        addressList = geocoder.getFromLocation(locationResult.getLastLocation().getLatitude()
                                , locationResult.getLastLocation().getLongitude(), 1);
                        String cityName = addressList.get(0).getLocality();

                            driverlocationRef = FirebaseDatabase.getInstance().getReference(reference).child(cityName);

                            currnetRef = driverlocationRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            geoFire = new GeoFire(driverlocationRef);

                            //update location
                            geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    new GeoLocation(locationResult.getLastLocation().getLatitude(),
                                            locationResult.getLastLocation().getLongitude()),
                                    new GeoFire.CompletionListener() {
                                        @Override
                                        public void onComplete(String key, DatabaseError error) {
                                            if (error != null)
                                                Snackbar.make(mapFragment.getView(), error.getMessage() + " her error", Snackbar.LENGTH_LONG).show();


                                        }
                                    });

                            registeronlinesystem();//only register when setup





                    } catch (IOException e) {
                        e.printStackTrace();
                        Snackbar.make(mapFragment.getView(), e.getMessage() + " 274", Snackbar.LENGTH_LONG).show();
                    }


                }
            };
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
                                            if( mMap.isMyLocationEnabled()){
                                                LatLng userlocation = new LatLng(location.getLatitude(), location.getLongitude());
                                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userlocation, 18f));
                                            }else {
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
                onClickCar();
                onClickMechanic();
                onClickWinch();

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
        driverRequestRecieve=event;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(requireView(),getString(R.string.permission_require) , Snackbar.LENGTH_LONG).show();
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
                compositeDisposable.add(igoogleApi.getDirections("driving","less_driving",
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
                                    JSONObject jsonObject= new JSONObject(returnRusult);
                                    JSONArray jsonArray=jsonObject.getJSONArray("routes");
                                    for(int i=0 ;i<jsonArray.length();i++){
                                        JSONObject route=jsonArray.getJSONObject(i);
                                        JSONObject poly=route.getJSONObject("overview_polyline");
                                        String polyline=poly.getString("points");
                                        polylinelist=Common.decodePoly(polyline);

                                    }
                                    //moving
                                    polylineOptions=new PolylineOptions();
                                    polylineOptions.color(Color.GRAY);
                                    polylineOptions.width(12);
                                    polylineOptions.startCap(new SquareCap());
                                    polylineOptions.jointType(JointType.ROUND);
                                    polylineOptions.addAll(polylinelist);
                                    greypolyline=mMap.addPolyline(polylineOptions);
                                    blaclpolylineoption=new PolylineOptions();
                                    blaclpolylineoption.color(Color.BLACK);
                                    blaclpolylineoption.width(5);
                                    blaclpolylineoption.startCap(new SquareCap());
                                    blaclpolylineoption.jointType(JointType.ROUND);
                                    blaclpolylineoption.addAll(polylinelist);
                                    balckpolyline=mMap.addPolyline( blaclpolylineoption);
                                    //animator
                                    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
                                    valueAnimator.setDuration(1100);
                                    valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
                                    valueAnimator.setInterpolator(new LinearInterpolator());
                                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            List<LatLng>points=greypolyline.getPoints();
                                            int percentvalue=(int)animation.getAnimatedValue();
                                            int size=points.size();
                                            int newsize=(int)(size*(percentvalue/100.0f));
                                            List<LatLng>p=points.subList(0,newsize);
                                            balckpolyline.setPoints(p);

                                        }
                                    });
                                    valueAnimator.start();
                                    LatLng origin=new LatLng(location.getLatitude(),location.getLongitude());
                                    LatLng destination =new LatLng(Double.parseDouble(event.getPickuplocation().split(",")[0]),
                                            Double.parseDouble(event.getPickuplocation().split(",")[1] ));
                                    LatLngBounds latLngBounds=new LatLngBounds.Builder()
                                            .include(origin)
                                            .include(destination)
                                            .build();
                                    //add car icon for origin
                                    JSONObject object=jsonArray.getJSONObject(0);
                                    JSONArray legs=object.getJSONArray("legs");
                                    JSONObject legsobject=legs.getJSONObject(0);
                                    JSONObject time=legsobject.getJSONObject("duration");
                                    String duration=time.getString("text");

                                    JSONObject distanceEstimate=legsobject.getJSONObject("distance");
                                    String distance=distanceEstimate.getString("text");
                                    txt_estimate_time.setText(duration);
                                    txt_estimate_distance.setText(distance);

                                  mMap.addMarker(new MarkerOptions()
                                          .position(destination)
                                          .icon(BitmapDescriptorFactory.defaultMarker())
                                          .title("Pickup Location")
                                  );
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,160));
                                    mMap.moveCamera(CameraUpdateFactory.zoomTo(  mMap.getCameraPosition().zoom-1));
                                    //show layout
                                    chip_decline.setVisibility(View.VISIBLE);
                                    layout_accept.setVisibility(View.VISIBLE);
                                    //count down

                                    coundDownEvent= Observable.interval(100, TimeUnit.MILLISECONDS)
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
                                                      circularProgressBar.setProgress(0);
                                                      Toast.makeText(getContext(), " Fake accept Action", Toast.LENGTH_SHORT).show();

                                                  }
                                              }).subscribe();








                                } catch (Exception e){
                                    Toast.makeText(getContext(), "e.getMessage()", Toast.LENGTH_SHORT).show();



                                }

                            }


                        }));

            }
        });

    }
}