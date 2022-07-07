package com.ridetogo.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ViewPagerCards.CardPagerAdapter;
import com.ViewPagerCards.RideDeliveryCardPagerAdapter;
import com.ViewPagerCards.ShadowTransformer;
import com.adapter.files.DeliveryBannerAdapter;
import com.adapter.files.DeliveryIconAdapter;
import com.adapter.files.RideDeliveryCategoryAdapter;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.fragments.MyBookingFragment;
import com.fragments.MyProfileFragment;
import com.fragments.MyWalletFragment;
import com.general.files.AddBottomBar;
import com.general.files.ConfigPubNub;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.GetAddressFromLocation;
import com.general.files.GetLocationUpdates;
import com.general.files.InternetConnection;
import com.general.files.LoadAvailableCab;
import com.general.files.MyApp;
import com.general.files.OpenCatType;
import com.general.files.StartActProcess;
import com.general.files.UpdateFrequentTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.model.DeliveryIconDetails;
import com.rest.RestClient;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.Logger;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.LoopingCirclePageIndicator;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class RideDeliveryActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, RideDeliveryCardPagerAdapter.OnItemClickList, RideDeliveryCategoryAdapter.OnItemClickList, OnMapReadyCallback
        , GetLocationUpdates.LocationUpdates {


    GeneralFunctions generalFunc;
    ViewPager bannerViewPager;
    LoopingCirclePageIndicator bannerCirclePageIndicator;
    // private ShadowTransformer mCardShadowTransformer;
    private RideDeliveryCardPagerAdapter mCardAdapter;
    ArrayList<HashMap<String, String>> imagesList;
    ArrayList<HashMap<String, String>> itemList;
    RecyclerView dataListRecyclerView;
    RideDeliveryCategoryAdapter rideDeliveryCategoryAdapter;


    AddBottomBar addBottomBar;
    FrameLayout container;
    String userProfileJson = "";
    MyProfileFragment myProfileFragment;
    MyWalletFragment myWalletFragment;
    public MyBookingFragment myBookingFragment;

    private static final int SEL_CARD = 004;
    public static final int TRANSFER_MONEY = 87;
    public boolean iswallet = false;
    MTextView whereTxt, aroundTxt;
    MTextView homePlaceTxt, homePlaceHTxt;
    MTextView workPlaceTxt, workPlaceHTxt;
    LinearLayout homeLocArea, workLocArea;
    ImageView homeActionImgView, workActionImgView;
    private static final int RC_SIGN_IN_UP = 007;
    static LinearLayout MainLayout, bottomMenuArea, homeWorkArea;
    ProgressBar mProgressBar;

    boolean isRide = false;
    SupportMapFragment map;
    GoogleMap gMap;
    GetLocationUpdates getLastLocation;
    boolean isFirstLocation = true;
    public Location userLocation;
    LoadAvailableCab loadAvailCabs;
    private SkeletonScreen skeletonScreen;
    public ArrayList<HashMap<String, String>> listOfDrivers;
    ArrayList<Marker> driverMarkerList = new ArrayList<>();
    ImageView backImgView;
    ImageView headerLogo;
    Toolbar toolbar;
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 7500; // time in milliseconds between successive task executions.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ride_delivery);


        generalFunc = new GeneralFunctions(getActContext());
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        initView();
        getDetails();
        addBottomBar = new AddBottomBar(getActContext(), generalFunc.getJsonObject(userProfileJson));


    }


    public void initView() {
        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapV2);

        backImgView = (ImageView) findViewById(R.id.backImgView);
        headerLogo = (ImageView) findViewById(R.id.headerLogo);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backImgView.setVisibility(View.GONE);
        headerLogo.setPadding(0, 0, 0, 0);
        headerLogo.setVisibility(View.VISIBLE);
        container = (FrameLayout) findViewById(R.id.container);
        whereTxt = (MTextView) findViewById(R.id.whereTxt);
        aroundTxt = (MTextView) findViewById(R.id.aroundTxt);
        homePlaceTxt = (MTextView) findViewById(R.id.homePlaceTxt);
        homePlaceHTxt = (MTextView) findViewById(R.id.homePlaceHTxt);
        workPlaceTxt = (MTextView) findViewById(R.id.workPlaceTxt);
        workPlaceHTxt = (MTextView) findViewById(R.id.workPlaceHTxt);
        homeLocArea = (LinearLayout) findViewById(R.id.homeLocArea);
        workLocArea = (LinearLayout) findViewById(R.id.workLocArea);
        MainLayout = (LinearLayout) findViewById(R.id.MainLayout);
        bottomMenuArea = (LinearLayout) findViewById(R.id.bottomMenuArea);
        homeWorkArea = (LinearLayout) findViewById(R.id.homeWorkArea);
        bannerViewPager = (ViewPager) findViewById(R.id.bannerViewPager);
        bannerCirclePageIndicator = findViewById(R.id.bannerCirclePageIndicator);
        homeActionImgView = (ImageView) findViewById(R.id.homeActionImgView);
        workActionImgView = (ImageView) findViewById(R.id.workActionImgView);
        mProgressBar = (ProgressBar) findViewById(R.id.mProgressBar);
        homeLocArea.setOnClickListener(new setOnClickList());
        workLocArea.setOnClickListener(new setOnClickList());
        homeActionImgView.setOnClickListener(new setOnClickList());
        workActionImgView.setOnClickListener(new setOnClickList());

        bannerViewPager.addOnPageChangeListener(this);
        dataListRecyclerView = (RecyclerView) findViewById(R.id.dataListRecyclerView);
        //  dataListRecyclerView.setHasFixedSize(true);

        whereTxt.setText(generalFunc.retrieveLangLBl("", "LBL_WHERE_TO"));
        aroundTxt.setText(generalFunc.retrieveLangLBl("Around You", "LBL_AROUND_YOU"));
        checkPlaces();
        map.getMapAsync(RideDeliveryActivity.this);


        skeletonScreen = Skeleton.bind(MainLayout)
                .load(R.layout.ridedlivery_shimmer_view)
                .duration(1000)
                .color(R.color.shimmer_color)
                .angle(0)
                .show();

        MyHandler myHandler = new MyHandler(this);
        myHandler.sendEmptyMessageDelayed(1, 5000);

    }

    static WeakReference<RideDeliveryActivity> activityWeakReference;

    public static class MyHandler extends android.os.Handler {


        MyHandler(RideDeliveryActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (activityWeakReference.get() != null) {
                activityWeakReference.get().skeletonScreen.hide();

            }
        }
    }


    public GoogleMap getMap() {
        return this.gMap;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.gMap = googleMap;
        gMap.getUiSettings().setAllGesturesEnabled(false);
        if (generalFunc.checkLocationPermission(true)) {
            getMap().setMyLocationEnabled(false);
            //  getMap().setPadding(0, 0, 0, Utils.dipToPixels(getActContext(), 50));
            getMap().getUiSettings().setTiltGesturesEnabled(false);
            getMap().getUiSettings().setZoomControlsEnabled(false);
            getMap().getUiSettings().setCompassEnabled(false);
            getMap().getUiSettings().setMyLocationButtonEnabled(false);
        }


        getMap().setOnMarkerClickListener(marker -> {
            marker.hideInfoWindow();
            return true;
        });

        if (getLastLocation != null) {
            getLastLocation.stopLocationUpdates();
            getLastLocation = null;
        }

        GetLocationUpdates.locationResolutionAsked = false;
        getLastLocation = new GetLocationUpdates(getActContext(), Utils.LOCATION_UPDATE_MIN_DISTANCE_IN_MITERS, true, this);


    }

    @Override
    public void onLocationUpdate(Location location) {
        if (location == null) {
            return;
        }

        this.userLocation = location;


        if (isFirstLocation == true) {

            double currentZoomLevel = Utils.defaultZomLevel;

            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude()))
                    .zoom((float) currentZoomLevel).build();

            if (cameraPosition != null && getMap() != null) {
                getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }


            isFirstLocation = false;
            if (itemList != null) {
                getOnlineDriversRideDelivery(itemList.get(0).get("eCatType"));
            }
        }

    }


    public void initializeLoadCab() {


        loadAvailCabs = new LoadAvailableCab(getActContext(), generalFunc, "1", userLocation,
                getMap(), userProfileJson);

        // loadAvailCabs.pickUpAddress = pickUpLocationAddress;
        // loadAvailCabs.currentGeoCodeResult = currentGeoCodeObject;
        loadAvailCabs.checkAvailableCabs();
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();

            Bundle bndl = new Bundle();

            if (i == R.id.homeLocArea) {


                HashMap<String, String> data = new HashMap<>();
                data.put("userHomeLocationAddress", "");
                data.put("userHomeLocationLatitude", "");
                data.put("userHomeLocationLongitude", "");
                data = GeneralFunctions.retrieveValue(data, getActContext());

                final String home_address_str = data.get("userHomeLocationAddress");
                final String home_addr_latitude = data.get("userHomeLocationLatitude");
                final String home_addr_longitude = data.get("userHomeLocationLongitude");

                if (home_address_str != null && !home_address_str.equalsIgnoreCase("")) {
                    HashMap<String, String> hasmpObj = itemList.get(0);
//                    hasmpObj.put("latitude", home_addr_latitude);
//                    hasmpObj.put("longitude", home_addr_longitude);
//                    hasmpObj.put("address", home_address_str);
                    hasmpObj.put("isHome", "Yes");


                    (new OpenCatType(getActContext(), hasmpObj)).execute();

                }


            } else if (i == R.id.workLocArea) {

                HashMap<String, String> data = new HashMap<>();
                data.put("userWorkLocationAddress", "");
                data.put("userWorkLocationLatitude", "");
                data.put("userWorkLocationLongitude", "");
                data = generalFunc.retrieveValue(data);


                String work_address_str = data.get("userWorkLocationAddress");
                String work_addr_latitude = data.get("userWorkLocationLatitude");
                String work_addr_longitude = data.get("userWorkLocationLongitude");

                if (work_address_str != null && !work_address_str.equalsIgnoreCase("")) {
                    HashMap<String, String> hasmpObj = itemList.get(0);
                    hasmpObj.put("isWork", "Yes");
                    (new OpenCatType(getActContext(), hasmpObj)).execute();

                }
            } else if (i == R.id.homeActionImgView) {
                if (getIntent().hasExtra("eSystem") && !Utils.checkText(generalFunc.getMemberId())) {
                    Bundle bn = new Bundle();
                    bn.putBoolean("isRestart", false);
                    new StartActProcess(getActContext()).startActForResult(RideDeliveryActivity.class, bn, RC_SIGN_IN_UP);
                    return;
                }

                Bundle bn = new Bundle();
                bn.putString("isHome", "true");


                new StartActProcess(getActContext()).startActForResult(SearchPickupLocationActivity.class,
                        bn, Utils.ADD_HOME_LOC_REQ_CODE);

            } else if (i == R.id.workActionImgView) {
                if (getIntent().hasExtra("eSystem") && !Utils.checkText(generalFunc.getMemberId())) {
                    Bundle bn = new Bundle();
                    bn.putBoolean("isRestart", false);
                    new StartActProcess(getActContext()).startActForResult(RideDeliveryActivity.class, bn, RC_SIGN_IN_UP);
                    return;
                }

                Bundle bn = new Bundle();
                bn.putString("isWork", "true");


                new StartActProcess(getActContext()).startActForResult(SearchPickupLocationActivity.class,
                        bn, Utils.ADD_WORK_LOC_REQ_CODE);

            }


        }
    }


    public void checkPlaces() {

        String home_address_str = generalFunc.retrieveValue("userHomeLocationAddress");
//        if(home_address_str.equalsIgnoreCase("")){
//            home_address_str = "----";
//        }
        String work_address_str = generalFunc.retrieveValue("userWorkLocationAddress");
//        if(work_address_str.equalsIgnoreCase("")){
//            work_address_str = "----";
//        }

        if (home_address_str != null && !home_address_str.equalsIgnoreCase("")) {

            homePlaceTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HOME_PLACE"));
//            homePlaceHTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//            homePlaceTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//            homePlaceTxt.setTextColor(Color.parseColor("#909090"));
            homePlaceHTxt.setText("" + home_address_str);
            homePlaceHTxt.setVisibility(View.VISIBLE);
            //homePlaceHTxt.setTextColor(getResources().getColor(R.color.black));
            homeActionImgView.setImageResource(R.mipmap.ic_edit);

        } else {
            homePlaceHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HOME_PLACE"));
//            homePlaceHTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//            homePlaceTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            homePlaceTxt.setText("" + generalFunc.retrieveLangLBl("", "LBL_ADD_HOME_PLACE_TXT"));
            //      homePlaceTxt.setTextColor(Color.parseColor("#909090"));
            homeActionImgView.setImageResource(R.mipmap.ic_pluse);
        }

        if (work_address_str != null && !work_address_str.equalsIgnoreCase("")) {

            workPlaceTxt.setText(generalFunc.retrieveLangLBl("", "LBL_WORK_PLACE"));
            workPlaceHTxt.setText("" + work_address_str);
            //   workPlaceHTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            //  workPlaceTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            //  workPlaceTxt.setTextColor(getResources().getColor(R.color.gray));
            workPlaceHTxt.setVisibility(View.VISIBLE);
//            workPlaceTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, img_edit, null);
            //  workPlaceHTxt.setTextColor(getResources().getColor(R.color.black));
            workActionImgView.setImageResource(R.mipmap.ic_edit);

        } else {
            workPlaceHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_WORK_PLACE"));
            // workPlaceHTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            //workPlaceTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            workPlaceTxt.setText("" + generalFunc.retrieveLangLBl("", "LBL_ADD_WORK_PLACE_TXT"));
            // workPlaceTxt.setTextColor(Color.parseColor("#909090"));
            workActionImgView.setImageResource(R.mipmap.ic_pluse);
        }

        if (home_address_str != null && home_address_str.equalsIgnoreCase("")) {
            homePlaceHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_HOME_PLACE_TXT"));
            //homePlaceTxt.setText("----");
            homePlaceTxt.setVisibility(View.GONE);

            //  homePlaceHTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            //  homePlaceHTxt.setTextColor(getResources().getColor(R.color.black));

            //  homePlaceTxt.setTextColor(Color.parseColor("#909090"));
            //  homePlaceTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            homePlaceHTxt.setVisibility(View.VISIBLE);
            homeActionImgView.setImageResource(R.mipmap.ic_pluse);
        }

        if (work_address_str != null && work_address_str.equalsIgnoreCase("")) {
            workPlaceHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_WORK_PLACE_TXT"));
            workPlaceTxt.setText("----");
            workPlaceTxt.setVisibility(View.GONE);

            //  workPlaceHTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            // workPlaceHTxt.setTextColor(getResources().getColor(R.color.black));

            //  workPlaceTxt.setTextColor(Color.parseColor("#909090"));
            //   workPlaceTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            workPlaceHTxt.setVisibility(View.VISIBLE);
            workActionImgView.setImageResource(R.mipmap.ic_pluse);
        }
    }

    public Activity getActContext() {
        return RideDeliveryActivity.this;
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (myProfileFragment != null && isProfilefrg) {
            myProfileFragment.onResume();
        }

        if (myWalletFragment != null && isWalletfrg) {
            myWalletFragment.onResume();
        }

        if (myBookingFragment != null && isBookingfrg) {
            myBookingFragment.onResume();
        }

        if (generalFunc.retrieveValue(Utils.ISWALLETBALNCECHANGE).equalsIgnoreCase("Yes")) {
            // getWalletBalDetails();
        }

        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);

        //  setUserInfo();


        if (iswallet) {

            iswallet = false;
        }


    }

    public void openHistoryFragment() {
        this.getWindow().setStatusBarColor(getResources().getColor(R.color.appThemeColor_1));
        if (activityWeakReference.get() != null) {
            activityWeakReference.get().skeletonScreen.hide();

        }

        isProfilefrg = false;
        isWalletfrg = false;
        isBookingfrg = true;
        container.setVisibility(View.VISIBLE);
        if (myBookingFragment == null) {
            myBookingFragment = new MyBookingFragment();
        } else {
            myBookingFragment.onDestroy();
            myBookingFragment = new MyBookingFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, myBookingFragment).commit();
    }

    public void manageHome() {
        isProfilefrg = false;
        isWalletfrg = false;
        isBookingfrg = false;
        container.setVisibility(View.GONE);

        if (imagesList != null && imagesList.get(0).get("vStatusBarColor") != null) {
            bannerViewPager.setCurrentItem(0);
            toolbar.setBackgroundColor(Color.parseColor(imagesList.get(0).get("vStatusBarColor")));
            this.getWindow().setStatusBarColor(Color.parseColor(imagesList.get(0).get("vStatusBarColor")));
        }
    }

    public void openProfileFragment() {
        this.getWindow().setStatusBarColor(getResources().getColor(R.color.appThemeColor_1));
        if (activityWeakReference.get() != null) {
            activityWeakReference.get().skeletonScreen.hide();

        }
        isProfilefrg = true;
        isWalletfrg = false;
        isBookingfrg = false;
//        if (myProfileFragment != null) {
//            myProfileFragment = null;
//            Utils.runGC();
//        }


        container.setVisibility(View.VISIBLE);
        if (myProfileFragment == null) {
            myProfileFragment = new MyProfileFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, myProfileFragment).commit();


    }

    boolean isProfilefrg = false;
    boolean isWalletfrg = false;
    boolean isBookingfrg = false;

    public void openWalletFragment() {
        this.getWindow().setStatusBarColor(getResources().getColor(R.color.appThemeColor_1));
        if (activityWeakReference.get() != null) {
            activityWeakReference.get().skeletonScreen.hide();

        }
        isProfilefrg = false;
        isWalletfrg = true;
        isBookingfrg = false;

//        if (myProfileFragment != null) {
//            myProfileFragment = null;
//            Utils.runGC();
//        }


        container.setVisibility(View.VISIBLE);
        if (myWalletFragment == null) {
            myWalletFragment = new MyWalletFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, myWalletFragment).commit();


    }

    public void getDetails() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getServiceCategoryDetails");
        parameters.put("UserType", Utils.app_type);
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("iVehicleCategoryId", "0");


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), false, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {
            JSONObject responseObj = generalFunc.getJsonObject(responseString);

            if (responseObj != null && !responseObj.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseObj);

                if (isDataAvail) {
                    mProgressBar.setVisibility(View.GONE);
                    MainLayout.setVisibility(View.VISIBLE);
                    // bottomMenuArea.setVisibility(View.VISIBLE);

                    itemList = new ArrayList<>();
                    JSONArray itemarr = generalFunc.getJsonArray(Utils.message_str, responseObj);
                    for (int i = 0; i < itemarr.length(); i++) {
                        JSONObject obj_temp = generalFunc.getJsonObject(itemarr, i);
                        HashMap<String, String> itemObj = new HashMap<>();
                        itemObj.put("vCategory", generalFunc.getJsonValueStr("vCategory", obj_temp));
                        itemObj.put("vImage", generalFunc.getJsonValueStr("vImage", obj_temp));
                        itemObj.put("eCatType", generalFunc.getJsonValueStr("eCatType", obj_temp));
                        itemObj.put("iVehicleCategoryId", generalFunc.getJsonValueStr("iVehicleCategoryId", obj_temp));

                        itemList.add(itemObj);
                        if (generalFunc.getJsonValueStr("eCatType", obj_temp).equalsIgnoreCase("Ride")) {
                            isRide = true;
                        }

                    }


                    JSONArray bannerarr = generalFunc.getJsonArray("bannerArray", responseObj);
                    //  ArrayList<String> imagesList = new ArrayList<String>();
                    imagesList = new ArrayList<>();
                    mCardAdapter = new RideDeliveryCardPagerAdapter();


                    for (int i = 0; i < bannerarr.length(); i++) {
                        JSONObject obj_temp = generalFunc.getJsonObject(bannerarr, i);

                        String vImage = generalFunc.getJsonValueStr("vImage", obj_temp);
                        //  String imageURL = Utils.getResizeImgURL(getActContext(), vImage, Utils.getWidthOfBanner(getActContext(), 0), Utils.getHeightOfBanner(getActContext(), 0, "16:9"));

                        // String imageURL = vImage;

                        HashMap<String, String> bannerObj = new HashMap<>();
                        bannerObj.put("vTitle", generalFunc.getJsonValueStr("vTitle", obj_temp));
                        bannerObj.put("vSubtitle", generalFunc.getJsonValueStr("vSubtitle", obj_temp));
                        bannerObj.put("vBtnTtitle", generalFunc.getJsonValueStr("vBtnTtitle", obj_temp));
                        bannerObj.put("vTextColor", generalFunc.getJsonValueStr("vTextColor", obj_temp));
                        bannerObj.put("vBtnBgColor", generalFunc.getJsonValueStr("vBtnBgColor", obj_temp));
                        bannerObj.put("vBtnTextColor", generalFunc.getJsonValueStr("vBtnTextColor", obj_temp));
                        bannerObj.put("eCatType", generalFunc.getJsonValueStr("eCatType", obj_temp));
                        bannerObj.put("vCategory", generalFunc.getJsonValueStr("vCategory", obj_temp));
                        bannerObj.put("iVehicleCategoryId", generalFunc.getJsonValueStr("iVehicleCategoryId", obj_temp));
                        bannerObj.put("vImage", vImage);
                        bannerObj.put("vStatusBarColor", generalFunc.getJsonValueStr("vStatusBarColor", obj_temp));


                        imagesList.add(bannerObj);
                        mCardAdapter.addCardItem(bannerObj, getActContext(), this);
                    }
                    if (imagesList != null) {
                        toolbar.setBackgroundColor(Color.parseColor(imagesList.get(0).get("vStatusBarColor")));
                        this.getWindow().setStatusBarColor(Color.parseColor(imagesList.get(0).get("vStatusBarColor")));

                        int imageListSize = imagesList.size();
                        if (imageListSize > 2) {
                            bannerViewPager.setOffscreenPageLimit(3);
                        } else if (imageListSize > 1) {
                            bannerViewPager.setOffscreenPageLimit(2);
                        }
                    }

                    GridLayoutManager gridLay = new GridLayoutManager(getActContext(), 3);
                    dataListRecyclerView.setLayoutManager(gridLay);


                    // mCardShadowTransformer = new ShadowTransformer(bannerViewPager, mCardAdapter);
                    //mFragmentCardShadowTransformer = new ShadowTransformer(bannerViewPager, mFragmentCardAdapter);

                    bannerViewPager.setAdapter(mCardAdapter);
                    //  bannerViewPager.setPageTransformer(false, mCardShadowTransformer);
                    bannerViewPager.setOffscreenPageLimit(3);

                    rideDeliveryCategoryAdapter = new RideDeliveryCategoryAdapter(getActContext(), itemList, generalFunc);
                    rideDeliveryCategoryAdapter.setOnItemClickList(this);
                    dataListRecyclerView.setAdapter(rideDeliveryCategoryAdapter);

                    if (isRide) {
                        homeWorkArea.setVisibility(View.VISIBLE);
                        whereTxt.setVisibility(View.VISIBLE);

                    } else {
                        homeWorkArea.setVisibility(View.GONE);
                        whereTxt.setVisibility(View.GONE);
                    }


                    getOnlineDriversRideDelivery(itemList.get(0).get("eCatType"));
                    bannerCirclePageIndicator.setDataSize(imagesList.size());
                    bannerCirclePageIndicator.setViewPager(bannerViewPager);
                    manageAutoScroll();

                } else {
                    final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setCancelable(false);
                    generateAlert.setBtnClickList(btn_id -> {
                        generateAlert.closeAlertBox();
                        if (btn_id == 1) {
                            finish();

                        }
                    });
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseObj)));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
                    generateAlert.showAlertBox();

                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        try {


            if (imagesList.get(position).get("vStatusBarColor") != null) {
                toolbar.setBackgroundColor(Color.parseColor(imagesList.get(position).get("vStatusBarColor")));
                this.getWindow().setStatusBarColor(Color.parseColor(imagesList.get(position).get("vStatusBarColor")));
            }
        } catch (Exception e) {

        }


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onItemClick(int position) {


        (new OpenCatType(getActContext(), itemList.get(position))).execute();

    }

    @Override
    public void onBannerItemClick(int position) {
        (new OpenCatType(getActContext(), imagesList.get(position))).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN_UP && resultCode == RESULT_OK) {

        } else if (requestCode == Utils.ADD_HOME_LOC_REQ_CODE && resultCode == RESULT_OK && data != null) {

            String Latitude = data.getStringExtra("Latitude");
            String Longitude = data.getStringExtra("Longitude");
            String Address = data.getStringExtra("Address");

            generalFunc.storeData("userHomeLocationLatitude", "" + Latitude);
            generalFunc.storeData("userHomeLocationLongitude", "" + Longitude);
            generalFunc.storeData("userHomeLocationAddress", "" + Address);

            homePlaceTxt.setText(Address);
            checkPlaces();


            double lati = generalFunc.parseDoubleValue(0.0, Latitude);
            double longi = generalFunc.parseDoubleValue(0.0, Longitude);
            //  resetOrAddDest(data.getIntExtra("pos", -1), Address, lati, longi, "" + false);


        } else if (requestCode == Utils.ADD_MAP_LOC_REQ_CODE && resultCode == RESULT_OK && data != null) {

            String Latitude = data.getStringExtra("Latitude");
            String Longitude = data.getStringExtra("Longitude");
            String Address = data.getStringExtra("Address");

            double lati = generalFunc.parseDoubleValue(0.0, Latitude);
            double longi = generalFunc.parseDoubleValue(0.0, Longitude);

            //  resetOrAddDest(data.getIntExtra("pos", -1), Address, lati, longi, "" + false);

        } else if (requestCode == Utils.ADD_WORK_LOC_REQ_CODE && resultCode == RESULT_OK && data != null) {
            String Latitude = data.getStringExtra("Latitude");
            String Longitude = data.getStringExtra("Longitude");
            String Address = data.getStringExtra("Address");


            generalFunc.storeData("userWorkLocationLatitude", "" + Latitude);
            generalFunc.storeData("userWorkLocationLongitude", "" + Longitude);
            generalFunc.storeData("userWorkLocationAddress", "" + Address);

            workPlaceTxt.setText(Address);
            checkPlaces();

            double lati = generalFunc.parseDoubleValue(0.0, Latitude);
            double longi = generalFunc.parseDoubleValue(0.0, Longitude);
            // resetOrAddDest(data.getIntExtra("pos", -1), Address, lati, longi, "" + false);

        }
    }

    public void manageAutoScroll() {
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (!isProfilefrg && !iswallet && !isBookingfrg) {
                    if (currentPage == imagesList.size()) {
                        currentPage = 0;
                    }
                    bannerViewPager.setCurrentItem(currentPage++, true);
                }
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    public void getOnlineDriversRideDelivery(String etype) {
        if (userLocation == null) {
            return;
        }


        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getOnlineDriversRideDelivery");
        parameters.put("PickUpLatitude", "" + userLocation.getLatitude());
        parameters.put("PickUpLongitude", "" + userLocation.getLongitude());
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("PickUpAddress", "");
        parameters.put("eType", etype);


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);

        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                JSONArray availCabArr = generalFunc.getJsonArray("DriverList", responseString);

                if (availCabArr != null) {
                    listOfDrivers = new ArrayList<>();
                    for (int i = 0; i < availCabArr.length(); i++) {
                        JSONObject obj_temp = generalFunc.getJsonObject(availCabArr, i);

                        JSONObject carDetailsJson = generalFunc.getJsonObject("DriverCarDetails", obj_temp);
                        HashMap<String, String> driverDataMap = new HashMap<String, String>();
                        driverDataMap.put("driver_id", generalFunc.getJsonValueStr("iDriverId", obj_temp));
                        driverDataMap.put("Name", generalFunc.getJsonValueStr("vName", obj_temp));
                        driverDataMap.put("eIsFeatured", generalFunc.getJsonValueStr("eIsFeatured", obj_temp));
                        driverDataMap.put("LastName", generalFunc.getJsonValueStr("vLastName", obj_temp));
                        driverDataMap.put("Latitude", generalFunc.getJsonValueStr("vLatitude", obj_temp));
                        driverDataMap.put("Longitude", generalFunc.getJsonValueStr("vLongitude", obj_temp));
                        driverDataMap.put("GCMID", generalFunc.getJsonValueStr("iGcmRegId", obj_temp));
                        driverDataMap.put("iAppVersion", generalFunc.getJsonValueStr("iAppVersion", obj_temp));
                        driverDataMap.put("driver_img", generalFunc.getJsonValueStr("vImage", obj_temp));
                        driverDataMap.put("average_rating", generalFunc.getJsonValueStr("vAvgRating", obj_temp));
                        driverDataMap.put("DIST_TO_PICKUP_INT", generalFunc.getJsonValueStr("distance", obj_temp));
                        driverDataMap.put("vPhone_driver", generalFunc.getJsonValueStr("vPhone", obj_temp));
                        driverDataMap.put("vPhoneCode_driver", generalFunc.getJsonValueStr("vCode", obj_temp));
                        driverDataMap.put("tProfileDescription", generalFunc.getJsonValueStr("tProfileDescription", obj_temp));
                        driverDataMap.put("ACCEPT_CASH_TRIPS", generalFunc.getJsonValueStr("ACCEPT_CASH_TRIPS", obj_temp));
                        driverDataMap.put("vWorkLocationRadius", generalFunc.getJsonValueStr("vWorkLocationRadius", obj_temp));
                        driverDataMap.put("PROVIDER_RADIUS", generalFunc.getJsonValueStr("vWorkLocationRadius", obj_temp));
                        driverDataMap.put("iGcmRegId", generalFunc.getJsonValueStr("iGcmRegId", obj_temp));

                        driverDataMap.put("DriverGender", generalFunc.getJsonValueStr("eGender", obj_temp));
                        driverDataMap.put("eFemaleOnlyReqAccept", generalFunc.getJsonValueStr("eFemaleOnlyReqAccept", obj_temp));

                        driverDataMap.put("eHandiCapAccessibility", generalFunc.getJsonValueStr("eHandiCapAccessibility", carDetailsJson));
                        driverDataMap.put("eChildSeatAvailable", generalFunc.getJsonValueStr("eChildSeatAvailable", carDetailsJson));
                        driverDataMap.put("eWheelChairAvailable", generalFunc.getJsonValueStr("eWheelChairAvailable", carDetailsJson));
                        driverDataMap.put("vCarType", generalFunc.getJsonValueStr("vCarType", carDetailsJson));
                        driverDataMap.put("vColour", generalFunc.getJsonValueStr("vColour", carDetailsJson));
                        driverDataMap.put("vLicencePlate", generalFunc.getJsonValueStr("vLicencePlate", carDetailsJson));
                        driverDataMap.put("make_title", generalFunc.getJsonValueStr("make_title", carDetailsJson));
                        driverDataMap.put("model_title", generalFunc.getJsonValueStr("model_title", carDetailsJson));
                        driverDataMap.put("fAmount", generalFunc.getJsonValueStr("fAmount", carDetailsJson));
                        driverDataMap.put("eRental", generalFunc.getJsonValueStr("vRentalCarType", carDetailsJson));
                        /*End of the day feature - driver is in destination Mode*/
                        driverDataMap.put("eDestinationMode", generalFunc.getJsonValueStr("eDestinationMode", obj_temp));


                        driverDataMap.put("vCurrencySymbol", generalFunc.getJsonValueStr("vCurrencySymbol", carDetailsJson));

                        driverDataMap.put("PROVIDER_RATING_COUNT", generalFunc.getJsonValueStr("PROVIDER_RATING_COUNT", obj_temp));

                        driverDataMap.put("eFareType", generalFunc.getJsonValueStr("eFareType", carDetailsJson));
                        driverDataMap.put("ePoolRide", generalFunc.getJsonValueStr("ePoolRide", carDetailsJson));
                        driverDataMap.put("fMinHour", generalFunc.getJsonValueStr("fMinHour", carDetailsJson));
                        driverDataMap.put("eTripStatusActive", generalFunc.getJsonValueStr("eTripStatusActive", obj_temp));
                        driverDataMap.put("eFavDriver", generalFunc.getJsonValueStr("eFavDriver", obj_temp));
                        driverDataMap.put("iStopId", generalFunc.getJsonValueStr("iStopId", carDetailsJson));
                        driverDataMap.put("eIconType", generalFunc.getJsonValueStr("eIconType", carDetailsJson));

                        driverDataMap.put("IS_PROVIDER_ONLINE", generalFunc.getJsonValueStr("IS_PROVIDER_ONLINE", obj_temp));
                        listOfDrivers.add(driverDataMap);
                    }

                }


                filterDrivers(false);


            } else {
                removeDriversFromMap(true);

            }

        });
        exeWebServer.execute();
    }

    public void removeDriversFromMap(boolean isUnSubscribeAll) {
        if (driverMarkerList.size() > 0) {
            ArrayList<Marker> tempDriverMarkerList = new ArrayList<>();
            tempDriverMarkerList.addAll(driverMarkerList);
            for (int i = 0; i < tempDriverMarkerList.size(); i++) {
                Marker marker_temp = driverMarkerList.get(0);
                marker_temp.remove();
                driverMarkerList.remove(0);

            }
        }


    }

    public void filterDrivers(boolean isCheckAgain) {
        ArrayList<Marker> driverMarkerList_temp = new ArrayList<>();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (listOfDrivers != null) {
            for (int i = 0; i < listOfDrivers.size(); i++) {
                HashMap<String, String> driverData = listOfDrivers.get(i);
                double driverLocLatitude = GeneralFunctions.parseDoubleValue(0.0, driverData.get("Latitude"));
                double driverLocLongitude = GeneralFunctions.parseDoubleValue(0.0, driverData.get("Longitude"));
                builder.include(new LatLng(driverLocLatitude, driverLocLongitude));
                Marker driverMarker = drawMarker(new LatLng(driverLocLatitude, driverLocLongitude), "", driverData);
                driverMarkerList_temp.add(driverMarker);


            }

            driverMarkerList.addAll(driverMarkerList_temp);
        }
    }

    public Marker drawMarker(LatLng point, String Name, HashMap<String, String> driverData) {

        MarkerOptions markerOptions = new MarkerOptions();
        //String eIconType = generalFunc.getSelectedCarTypeData(selectedCabTypeId, cabTypesArrList, "eIconType");
        String eIconType = driverData.get("eIconType");

        int iconId = R.mipmap.car_driver;
        if (eIconType.equalsIgnoreCase("Bike")) {
            iconId = R.mipmap.car_driver_1;
        } else if (eIconType.equalsIgnoreCase("Cycle")) {
            iconId = R.mipmap.car_driver_2;
        } else if (eIconType.equalsIgnoreCase("Truck")) {
            iconId = R.mipmap.car_driver_4;
        } else if (eIconType.equalsIgnoreCase("Fly")) {
            iconId = R.mipmap.ic_fly_icon;
        }

        SelectableRoundedImageView providerImgView = null;
        View marker_view = null;

        markerOptions.position(point).title("DriverId" + driverData.get("driver_id")).icon(BitmapDescriptorFactory.fromResource(iconId))
                .anchor(0.5f, 0.5f).flat(true);


        // Adding marker on the Google Map
        final Marker marker = gMap.addMarker(markerOptions);
        marker.setRotation(0);
        marker.setVisible(true);


        return marker;
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public void pubNubMsgArrived(String message) {

        String driverMsg = generalFunc.getJsonValue("Message", message);
        String eType = generalFunc.getJsonValue("eType", message);
        //   String app_type = APP_TYPE;

        if (driverMsg.equals("CabRequestAccepted")) {
            String eSystem = generalFunc.getJsonValue("eSystem", userProfileJson);
            if (eSystem != null && eSystem.equalsIgnoreCase("DeliverAll")) {
                generalFunc.showGeneralMessage("", generalFunc.getJsonValue("vTitle", message));
                return;
            }


            if (eType.equalsIgnoreCase(Utils.eType_Multi_Delivery)) {

                return;
            }

            if (generalFunc.isJSONkeyAvail("iCabBookingId", message) && !generalFunc.getJsonValue("iCabBookingId", message).trim().equals("")) {
                MyApp.getInstance().restartWithGetDataApp();
            } else {
                if (eType.equalsIgnoreCase(Utils.CabGeneralType_UberX) || eType.equalsIgnoreCase(Utils.eType_Multi_Delivery)) {
                    return;
                } else {
                    MyApp.getInstance().restartWithGetDataApp();
                }
            }

        }

    }

}