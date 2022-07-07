package com.ridetogo.user.deliverAll;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.files.deliverAll.TrackOrderAdapter;
import com.ridetogo.user.BaseActivity;
import com.ridetogo.user.CardPaymentActivity;
import com.ridetogo.user.Help_MainCategory;
import com.ridetogo.user.ProfilePaymentActivity;
import com.ridetogo.user.QuickPaymentActivity;
import com.ridetogo.user.R;
import com.ridetogo.user.UberXHomeActivity;
import com.fragments.ScrollSupportMapFragment;
import com.general.files.AppFunctions;
import com.general.files.ConfigPubNub;
import com.general.files.DecimalDigitsInputFilter;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.LocalNotification;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.general.files.UpdateDirections;
import com.general.files.UpdateFrequentTask;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;
import com.utils.AnimateMarker;
import com.utils.CommonUtilities;
import com.utils.Logger;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TrackOrderActivity extends BaseActivity implements OnMapReadyCallback, TrackOrderAdapter.ReviewItemClickListener {

    RecyclerView dataRecyclerView;
    TrackOrderAdapter adapter;
    GoogleMap gMap;
    ScrollSupportMapFragment map;
    ArrayList<HashMap<String, String>> listData = new ArrayList<>();
    private ImageView backImgView;
    GeneralFunctions generalFunctions;
    MTextView titleTxt, ordertitleTxt, orderMsg;
    UpdateFrequentTask updateDriverLocTask;
    boolean isTaskKilled = false;
    public String iDriverId = "";
    Marker driverMarker;
    View marker_view = null;
    LatLng driverLocation;
    AnimateMarker animDriverMarker;
    UpdateDirections updateDirections;
    Polyline polyline = null;

    // Tip feature - View Declarion Start
    androidx.appcompat.app.AlertDialog giveTipAlertDialog;
    boolean showTipAddArea = false;
    androidx.appcompat.app.AlertDialog alertDialog;
    private String tipAmount = "";
    private Dialog dialog_sucess;
    private boolean isPercentage;
    // Tip feature - View Declarion End

    MTextView pickedUpTimeTxtView;
    MTextView pickedUpTimeAbbrTxtView;
    MTextView pickedUpTxtView;
    MTextView distanceVTxtView;
    MTextView distanceVAbbrTxtView;
    MTextView distanceTxtView;

    public LatLng userLatLng = null;
    public LatLng restLatLng = null;

    String eDisplayDottedLine = "";
    String eDisplayRouteLine = "";
    String CompanyAddress = "";
    String DeliveryAddress = "";
    Marker destMarker;

    String etaVal = "";
    String distanceValue = "";
    String distanceUnit = "";

    LinearLayout finaldelArea, btn_cancelArea, vieDetailsArea;
    MTextView delTitleTxtView, delMsgTxtViewm, btn_help, btn_confirm, btn_help_txt, btn_confirm_txt, btn_cancel;
    LinearLayout btnConfirmarea;
    public static String serviceId = "";
    public static String ETA = "";

    public String iOrderId = "";
    String userprofileJson = "";
    CardView timeArea;

    private String vImageDeliveryPref;
    private int imageWidth;
    String isContactLessDeliverySelected = "";
    private AlertDialog preferenceAlertDialog;
    LinearLayout contactLessDeliveryArea;
    MTextView contactLessDeliveryTxt;
    MTextView contactLessDeliveryHelpTxt;
    ImageView iv_preferenceImg;

    LinearLayout takeAwayArea;
    MTextView takeAwayDetailTxt;
    MTextView takeAwayPickedUpLocTxt;
    MTextView navigateBtn;
    String eTakeAway = "";
    String fTotalItemPrice = "";
    String LBL_TIP_AMOUNT = "";
    String userCurrencySymbol = "";
    String TIP_AMOUNT_1_VALUE = "";
    String TIP_AMOUNT_2_VALUE = "";
    String TIP_AMOUNT_3_VALUE = "";
    String eTakeAwayPickedUpNote = "";
    String prepareTime = "";
    private Double CompanyLat = 0.00, CompanyLong = 0.00;
    androidx.appcompat.app.AlertDialog list_navigation;
    androidx.appcompat.app.AlertDialog ConfirmGenieAlert;
    androidx.appcompat.app.AlertDialog billDeatilsGenieAlert;
    String eBuyAnyService = "";
    boolean fromNoti = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);
        Toolbar toolbar = findViewById(R.id.toolbar);
        new AppFunctions(getApplicationContext()).setOverflowButtonColor(toolbar, getResources().getColor(R.color.white));

        fromNoti = getIntent().getBooleanExtra("fromNoti", false);


        if (getIntent().getStringExtra("iOrderId") != null) {
            iOrderId = getIntent().getStringExtra("iOrderId");
        }

        generalFunctions = MyApp.getInstance().getGeneralFun(getActContext());
        userprofileJson = generalFunctions.retrieveValue(Utils.USER_PROFILE_JSON);
        LBL_TIP_AMOUNT = generalFunctions.retrieveLangLBl("", "LBL_TIP_AMOUNT");
        animDriverMarker = new AnimateMarker();
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        btnConfirmarea = (LinearLayout) findViewById(R.id.btnConfirmarea);
        btnConfirmarea.setOnClickListener(new setOnClickList());

        // new CreateRoundedView(getActContext().getResources().getColor(R.color.appThemeColor_1), 5, 0, 0, btnConfirmarea);
        imageWidth = (int) this.getResources().getDimension(R.dimen._90sdp);


        setSupportActionBar(mToolbar);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        ordertitleTxt = (MTextView) findViewById(R.id.ordertitleTxt);
        orderMsg = (MTextView) findViewById(R.id.orderMsg);
        titleTxt.setVisibility(View.GONE);
        ordertitleTxt.setVisibility(View.VISIBLE);
        orderMsg.setVisibility(View.VISIBLE);
        dataRecyclerView = (RecyclerView) findViewById(R.id.dataRecyclerView);
        delTitleTxtView = (MTextView) findViewById(R.id.delTitleTxtView);
        finaldelArea = (LinearLayout) findViewById(R.id.finaldelArea);
        iv_preferenceImg = (ImageView) findViewById(R.id.iv_preferenceImg);

        takeAwayArea = (LinearLayout) findViewById(R.id.takeAwayArea);
        takeAwayDetailTxt = (MTextView) findViewById(R.id.takeAwayDetailTxt);
        takeAwayPickedUpLocTxt = (MTextView) findViewById(R.id.takeAwayPickedUpLocTxt);
        navigateBtn = (MTextView) findViewById(R.id.navigateBtn);
        navigateBtn.setOnClickListener(new setOnClickList());
        navigateBtn.setText(generalFunctions.retrieveLangLBl("", "LBL_NAVIGATE"));

        btn_cancelArea = (LinearLayout) findViewById(R.id.btn_cancelArea);
        contactLessDeliveryArea = (LinearLayout) findViewById(R.id.contactLessDeliveryArea);
        contactLessDeliveryTxt = (MTextView) findViewById(R.id.contactLessDeliveryTxt);
        contactLessDeliveryHelpTxt = (MTextView) findViewById(R.id.contactLessDeliveryHelpTxt);
        contactLessDeliveryHelpTxt.setText(generalFunctions.retrieveLangLBl("", "LBL_DIS_HOW_IT_WORKS"));
        contactLessDeliveryTxt.setText(generalFunctions.retrieveLangLBl("ContactLess Delivery", "LBL_CONTACT_LESS_DELIVERY_TXT"));
        contactLessDeliveryArea.setOnClickListener(new setOnClickList());
        vieDetailsArea = (LinearLayout) findViewById(R.id.vieDetailsArea);
        vieDetailsArea.setOnClickListener(new setOnClickList());


        delMsgTxtViewm = (MTextView) findViewById(R.id.delMsgTxtView);
        btn_help = (MTextView) findViewById(R.id.btn_help);
        btn_cancel = (MTextView) findViewById(R.id.btn_cancel);
        btn_help_txt = (MTextView) findViewById(R.id.btn_help_txt);
        btn_confirm = (MTextView) findViewById(R.id.btn_confirm);
        btn_confirm_txt = (MTextView) findViewById(R.id.btn_confirm_txt);
        //btn_help.setOnClickListener(new setOnClickList());
        btn_cancel.setOnClickListener(new setOnClickList());
        map = (ScrollSupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapV2);

        timeArea = (CardView) findViewById(R.id.timeArea);

        new CreateRoundedView(getActContext().getResources().getColor(R.color.appThemeColor_1), 5, 0, 0, btn_cancel);

        pickedUpTimeTxtView = (MTextView) findViewById(R.id.pickedUpTimeTxtView);
        pickedUpTimeAbbrTxtView = (MTextView) findViewById(R.id.pickedUpTimeAbbrTxtView);
        pickedUpTxtView = (MTextView) findViewById(R.id.pickedUpTxtView);
        distanceVTxtView = (MTextView) findViewById(R.id.distanceVTxtView);
        distanceVAbbrTxtView = (MTextView) findViewById(R.id.distanceVAbbrTxtView);
        distanceTxtView = (MTextView) findViewById(R.id.distanceTxtView);

        map.getMapAsync(this);

        map.setListener(() -> ((NestedScrollView) findViewById(R.id.mScrollView)).requestDisallowInterceptTouchEvent(true));

        delTitleTxtView.setText(generalFunctions.retrieveLangLBl("", "LBL_ORDER_DELIVERED"));
        delMsgTxtViewm.setText(generalFunctions.retrieveLangLBl("", "LBL_ORDER_DELIVER_MSG"));
        btn_confirm.setText(generalFunctions.retrieveLangLBl("", "LBL_OK_GOT_IT"));
        btn_cancel.setText(generalFunctions.retrieveLangLBl("", "LBL_OK_GOT_IT"));
        btn_help.setText(generalFunctions.retrieveLangLBl("", "LBL_NOT_DELIVERD"));
        btn_confirm_txt.setText(generalFunctions.retrieveLangLBl("", "LBL_DELIVERD_NOTE"));
        btn_help_txt.setText(generalFunctions.retrieveLangLBl("", "LBL_NOTDELIVERD_NOTE"));
        pickedUpTxtView.setText(generalFunctions.retrieveLangLBl("", "LBL_PICKED_UP"));
        distanceTxtView.setText(generalFunctions.retrieveLangLBl("", "LBL_ETA_TXT"));

        distanceVTxtView.setText("--");
        distanceVAbbrTxtView.setText("");

        pickedUpTimeTxtView.setText("--");
        pickedUpTimeAbbrTxtView.setText("");
        if (getIntent().getStringExtra("iOrderId") != null) {
            iOrderId = getIntent().getStringExtra("iOrderId");
        }

        adapter = new TrackOrderAdapter(getActContext(), listData);
        adapter.onClickListener(this);
        dataRecyclerView.setAdapter(adapter);
    }


    public void setEta(String val, String distanceValue, String distanceUnit) {
        if (ETA.equalsIgnoreCase("")) {
            if (eDisplayRouteLine.equalsIgnoreCase("yes")) {
                runOnUiThread(() -> {
                    if (restLatLng != null && userLatLng != null) {
                        etaVal = val;
                        this.distanceValue = distanceValue;
                        this.distanceUnit = distanceUnit;

                        distanceVTxtView.setText(generalFunctions.convertNumberWithRTL(etaVal));

                        generateMapLocations(restLatLng.latitude, restLatLng.longitude, userLatLng.latitude, userLatLng.longitude);
                        if (driverLocation != null) {
                            updateDriverLocation(driverLocation);
                        }
                    }
                });
            }
        }

    }

    public void setTaskKilledValue(boolean isTaskKilled) {
        this.isTaskKilled = isTaskKilled;

        if (isTaskKilled == true) {
            onPauseCalled();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        setTaskKilledValue(true);

        unSubscribeToDriverLocChannel();

    }


    public void onPauseCalled() {

        if (updateDriverLocTask != null) {
            updateDriverLocTask.stopRepeatingTask();
        }
        updateDriverLocTask = null;

        unSubscribeToDriverLocChannel();
    }


    public void subscribeToDriverLocChannel() {

        if (!iDriverId.equalsIgnoreCase("")) {

            ArrayList<String> channelName = new ArrayList<>();
            channelName.add(Utils.pubNub_Update_Loc_Channel_Prefix + iDriverId);
            ConfigPubNub.getInstance().subscribeToChannels(channelName);
        }


    }

    String messageStr = "";

    public void pubnubMessage(JSONObject obj_msg) {
        Logger.d("TrackOrderPubNub", "::" + obj_msg);


        if (generalFunctions.getJsonValueStr("CustomNotification", obj_msg) != null && generalFunctions.getJsonValueStr("CustomNotification", obj_msg).equalsIgnoreCase("yes") && MyApp.getInstance().isMyAppInBackGround()) {
            MyApp.getInstance().getLoclaNotificationObj().customNotification(getApplicationContext(), obj_msg.toString());

            String messageStr = generalFunctions.getJsonValueStr("Message", obj_msg);
            String iOrderId_ = generalFunctions.getJsonValueStr("iOrderId", obj_msg);
            if (messageStr.equalsIgnoreCase("OrderDelivered") && iOrderId_.equalsIgnoreCase(iOrderId)) {

            } else if (messageStr.equalsIgnoreCase("OrderDeclineByRestaurant") || (messageStr.equalsIgnoreCase("OrderCancelByAdmin")) && iOrderId_.equalsIgnoreCase(iOrderId)) {

            } else {
                return;
            }
        }

        String messageStr = generalFunctions.getJsonValueStr("Message", obj_msg);
        String iOrderId_ = generalFunctions.getJsonValueStr("iOrderId", obj_msg);
        String eTakeAway = generalFunctions.getJsonValueStr("eTakeAway", obj_msg);

        String vImageDeliveryPref_ = generalFunctions.getJsonValueStr("vImageDeliveryPref", obj_msg);

        if (!messageStr.equals("")) {
            String vTitle = generalFunctions.getJsonValueStr("vTitle", obj_msg);
            //            Logger.d("messageStr", "IN Track Order::" + messageStr);

            if (messageStr.equalsIgnoreCase("OrderPickedup")) {
//                Logger.d("messageStr", "INside OrderPickedup::" + messageStr);
                handleDailog(false, vTitle);

                if (messageStr.equalsIgnoreCase("OrderPickedup") && !eTakeAway.equalsIgnoreCase("Yes")) {
                    timeArea.setVisibility(View.VISIBLE);
                }

            } else if (messageStr.equalsIgnoreCase("OrderConfirmByRestaurant")) {
                handleDailog(false, vTitle);
            } else if (messageStr.equalsIgnoreCase("OrderDeclineByRestaurant") || (messageStr.equalsIgnoreCase("OrderCancelByAdmin")) && iOrderId_.equalsIgnoreCase(iOrderId)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finaldelArea.setVisibility(View.VISIBLE);
                        btn_help_txt.setVisibility(View.GONE);
                        btn_confirm_txt.setVisibility(View.GONE);
                        btnConfirmarea.setVisibility(View.GONE);
                        btn_cancelArea.setVisibility(View.VISIBLE);
                        vieDetailsArea.setVisibility(View.GONE);
                        findViewById(R.id.btnMainConfirmarea).setVisibility(View.GONE);
                        delTitleTxtView.setText(generalFunctions.retrieveLangLBl("", "LBL_ORDER_CANCELLED"));
                        delTitleTxtView.setTextColor(getActContext().getResources().getColor(R.color.red));
                        delMsgTxtViewm.setText(vTitle);


                    }
                });


            } else if (messageStr.equalsIgnoreCase("OrderDelivered") && iOrderId_.equalsIgnoreCase(iOrderId)) {
                try {


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Logger.d("messageStr", "INside OrderDelivered::" + messageStr);

                            getTrackDetails();

                            if (Utils.checkText(vImageDeliveryPref_)) {
                                Picasso.get().load(Utils.getResizeImgURL(getActContext(), vImageDeliveryPref_, imageWidth, imageWidth)).placeholder(R.mipmap.ic_no_icon).error(R.mipmap.ic_no_icon).into(iv_preferenceImg);
                                iv_preferenceImg.setVisibility(View.VISIBLE);
                            }

                            finaldelArea.setVisibility(View.VISIBLE);
                            if (eTakeAway.equalsIgnoreCase("Yes")) {

                                delMsgTxtViewm.setText(eTakeAwayPickedUpNote);
                                takeAwayArea.setVisibility(View.GONE);
                                btn_confirm_txt.setVisibility(View.GONE);

                            }

                        }
                    });


                } catch (Exception e) {
                    Logger.d("OrderDeliveredException", "::" + e.toString());

                }

            } else if (messageStr.equalsIgnoreCase("CabRequestAccepted")) {
                handleDailog(false, vTitle);
            } else if (messageStr.equalsIgnoreCase("OrderReviewItems")) {
                if (this.messageStr.equalsIgnoreCase(messageStr)) {
                    return;
                }
                this.messageStr = messageStr;
                handleDailog(false, vTitle);
            }


        } else if (generalFunctions.getJsonValueStr("MsgType", obj_msg) != null && !generalFunctions.getJsonValueStr("MsgType", obj_msg).equalsIgnoreCase("")) {

            pubNubMsgArrived(obj_msg.toString(), true);

        }

    }

    public void handleDailog(boolean isfinish, String vTitle) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isActionTaken = false;
                try {
                    isActionTaken = true;
//                    Logger.d("messageStr", "INside handleDailog::");
                    final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setCancelable(false);
                    generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                        @Override
                        public void handleBtnClick(int btn_id) {
                            if (isfinish) {
                                finish();
                            } else {
                                getTrackDetails();
                            }
                        }
                    });
                    generateAlert.setContentMessage("", vTitle);
                    generateAlert.setPositiveBtn(generalFunctions.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    generateAlert.showAlertBox();
                } catch (Exception e) {
                    isActionTaken = true;
//                    Logger.d("messageStr", "INside Exception OrderPickedup::"+e.getMessage() );

                    getTrackDetails();
                    e.printStackTrace();
                }

                if (!isActionTaken) {
//                    Logger.d("messageStr", "INside handleDailog OrderPickedup::" );
                    getTrackDetails();
                }

            }
        });

    }

    public void unSubscribeToDriverLocChannel() {

        if (!iDriverId.equalsIgnoreCase("")) {
            ArrayList<String> channelName = new ArrayList<>();
            channelName.add(Utils.pubNub_Update_Loc_Channel_Prefix + iDriverId);
            ConfigPubNub.getInstance().unSubscribeToChannels(channelName);
        }

    }


    public void pubNubMsgArrived(final String message, final Boolean ishow) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String msgType = generalFunctions.getJsonValue("MsgType", message);
                String DriverId = generalFunctions.getJsonValue("iDriverId", message);

                if (msgType.equals("LocationUpdateOnTrip") && DriverId.equalsIgnoreCase(iDriverId)) {
                    String vLatitude = generalFunctions.getJsonValue("vLatitude", message);
                    String vLongitude = generalFunctions.getJsonValue("vLongitude", message);

                    Location driverLoc = new Location("Driverloc");
                    driverLoc.setLatitude(generalFunctions.parseDoubleValue(0.0, vLatitude));
                    driverLoc.setLongitude(generalFunctions.parseDoubleValue(0.0, vLongitude));


                    if (updateDirections != null) {
                        updateDirections.changeUserLocation(driverLoc);
                    }

                    if (eDisplayRouteLine.equalsIgnoreCase("Yes")) {
                        callUpdateDeirection(driverLoc);
                    }

                    LatLng driverLocation_update = new LatLng(generalFunctions.parseDoubleValue(0.0, vLatitude),
                            generalFunctions.parseDoubleValue(0.0, vLongitude));

                    driverLocation = driverLocation_update;
                    updateDriverLocation(driverLocation_update);

                }
            }
        });
    }


    public void callUpdateDeirection(Location driverlocation) {
        if (userLatLng == null) {
            return;

        }
        if (updateDirections == null) {

            Location location = new Location("userLocation");
            location.setLatitude(userLatLng.latitude);
            location.setLongitude(userLatLng.longitude);
            updateDirections = new UpdateDirections(getActContext(), gMap, driverlocation, location);
            updateDirections.scheduleDirectionUpdate();
        }

    }


    public void updateDriverLocation(LatLng latlog) {
        if (latlog == null) {
            return;
        }

        if (driverMarker == null) {
            try {
                if (gMap != null) {
                    MarkerOptions markerOptions = new MarkerOptions();

                    int iconId = R.mipmap.car_driver_6;

                    markerOptions.position(latlog).title("DriverId" + iDriverId).icon(BitmapDescriptorFactory.fromResource(iconId))
                            .anchor(0.5f, 0.5f).flat(true);

                    driverMarker = gMap.addMarker(markerOptions);

                    driverLocation = latlog;
                    CameraPosition cameraPosition = cameraForDriverPosition();
                    gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                }
            } catch (Exception e) {
                Logger.d("markerException", e.toString());
            }
        } else {

            if (driverMarker != null) {
                driverMarker.remove();
            }
            if (gMap != null) {
                MarkerOptions markerOptions = new MarkerOptions();

                int iconId = R.mipmap.car_driver_6;

                markerOptions.position(latlog).title("DriverId" + iDriverId).icon(BitmapDescriptorFactory.fromResource(iconId))
                        .anchor(0.5f, 0.5f).flat(true);

                driverMarker = gMap.addMarker(markerOptions);

                driverLocation = latlog;

            }


            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            builder.include(driverLocation);
            if (eDisplayDottedLine.equalsIgnoreCase("No") && eDisplayRouteLine.equalsIgnoreCase("No")) {
                //driver  to resturant bounding
                builder.include(restLatLng);

            } else if (eDisplayRouteLine.equalsIgnoreCase("Yes")) {
                //driver to user bounding
                builder.include(userLatLng);

            }

            LatLngBounds bounds = builder.build();

            if (map != null) {
                try {
                    int width = map.getView().getMeasuredWidth();
                    int height = map.getView().getMeasuredHeight();
                    int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, Utils.dpToPx(getActContext(), 150), padding);

                    gMap.animateCamera(cu);
                } catch (Exception e) {

                }
            }

        }


    }

    public CameraPosition cameraForDriverPosition() {

        double currentZoomLevel = gMap == null ? Utils.defaultZomLevel : gMap.getCameraPosition().zoom;

        if (Utils.defaultZomLevel > currentZoomLevel) {
            currentZoomLevel = Utils.defaultZomLevel;
        }
        if (driverLocation != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(this.driverLocation.latitude, this.driverLocation.longitude))
                    .zoom((float) currentZoomLevel).build();


            return cameraPosition;
        } else {
            return null;
        }
    }

    public Context getActContext() {
        return TrackOrderActivity.this;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;
        gMap.getUiSettings().setMapToolbarEnabled(false);

        googleMap.setPadding(0, 0, 0, 70);
    }

    Marker sourceMarker;
    Polyline linePoly;

    public void generateMapLocations(double resLat, double resLong, double userLat, double userLong) {
        LatLng sourceLnt = new LatLng(resLat, resLong);
        restLatLng = sourceLnt;

        if (sourceMarker != null) {
            sourceMarker.remove();
        }
        if (destMarker != null) {
            destMarker.remove();
        }
        if (!iDriverId.equalsIgnoreCase("")) {
            if (linePoly != null) {
                linePoly.remove();
            }

        }

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View restaurantMarkerView = inflater.inflate(R.layout.marker_view, null);


        ImageView restaurantPinImgView = (ImageView) restaurantMarkerView.findViewById(R.id.pinImgView);
        restaurantPinImgView.setImageResource(R.mipmap.ic_track_restaurant);

        MTextView restaurantMarkerTxtView = (MTextView) restaurantMarkerView.findViewById(R.id.addressTxtView);
        restaurantMarkerTxtView.setText(CompanyAddress);

        View userMarkerView = inflater.inflate(R.layout.marker_view, null);
        ImageView userPinImgView = (ImageView) userMarkerView.findViewById(R.id.pinImgView);
        userPinImgView.setImageResource(R.mipmap.ic_track_user);


        MTextView userMarkerTxtView = (MTextView) userMarkerView.findViewById(R.id.addressTxtView);
        userMarkerTxtView.setText(DeliveryAddress);
        userMarkerTxtView.setTextColor(getResources().getColor(R.color.white));
        userMarkerTxtView.setBackgroundColor(getResources().getColor(R.color.black));

        sourceMarker = gMap.addMarker(new MarkerOptions().position(sourceLnt).icon(BitmapDescriptorFactory.fromBitmap(Utils.getBitmapFromView(restaurantMarkerView))));


        LatLng destLnt = new LatLng(userLat, userLong);
        userLatLng = destLnt;
        if (!eTakeAway.equalsIgnoreCase("Yes")) {
            destMarker = gMap.addMarker(new MarkerOptions().position(destLnt).icon(BitmapDescriptorFactory.fromBitmap(Utils.getBitmapFromView(userMarkerView))));
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(sourceMarker.getPosition());
        if (!eTakeAway.equalsIgnoreCase("Yes")) {
            builder.include(destMarker.getPosition());
        }

        LatLngBounds bounds = builder.build();


        if (map != null && map.getView() != null) {
            try {
                int width = map.getView().getMeasuredWidth();
                int height = map.getView().getMeasuredHeight();
                int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, Utils.dpToPx(getActContext(), 150), padding);
                gMap.animateCamera(cu);
            } catch (Exception e) {

            }

        }
        if (!eTakeAway.equalsIgnoreCase("Yes")) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions
                    .jointType(JointType.ROUND)
                    .pattern(Arrays.asList(new Gap(20), new Dash(20)))
                    .add(sourceMarker.getPosition())
                    .add(destMarker.getPosition());
            polylineOptions.width(Utils.dipToPixels(getActContext(), 4));

            if (iDriverId.equalsIgnoreCase("")) {
                linePoly = gMap.addPolyline(polylineOptions);
                linePoly.setColor(Color.parseColor("#cecece"));
                linePoly.setStartCap(new RoundCap());
                linePoly.setEndCap(new RoundCap());
            }


            if (!eTakeAway.equalsIgnoreCase("Yes")) {
                buildArcLine(sourceLnt, destLnt, 0.050);
            }

        }


    }

    private void buildArcLine(LatLng p1, LatLng p2, double arcCurvature) {
        //Calculate distance and heading between two points
        double d = SphericalUtil.computeDistanceBetween(p1, p2);
        double h = SphericalUtil.computeHeading(p1, p2);

        if (h < 0) {
            LatLng tmpP1 = p1;
            p1 = p2;
            p2 = tmpP1;

            d = SphericalUtil.computeDistanceBetween(p1, p2);
            h = SphericalUtil.computeHeading(p1, p2);
        }

        //Midpoint position
        LatLng midPointLnt = SphericalUtil.computeOffset(p1, d * 0.5, h);

        //Apply some mathematics to calculate position of the circle center
        double x = (1 - arcCurvature * arcCurvature) * d * 0.5 / (2 * arcCurvature);
        double r = (1 + arcCurvature * arcCurvature) * d * 0.5 / (2 * arcCurvature);

        LatLng centerLnt = SphericalUtil.computeOffset(midPointLnt, x, h + 90.0);

        //Polyline options
        PolylineOptions options = new PolylineOptions();
        List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dash(30), new Gap(20));

        //Calculate heading between circle center and two points
        double h1 = SphericalUtil.computeHeading(centerLnt, p1);
        double h2 = SphericalUtil.computeHeading(centerLnt, p2);

        //Calculate positions of points on circle border and add them to polyline options
        int numPoints = 100;
        double step = (h2 - h1) / numPoints;

        for (int i = 0; i < numPoints; i++) {
            LatLng middlePointTemp = SphericalUtil.computeOffset(centerLnt, r, h1 + i * step);
            options.add(middlePointTemp);
        }


        if (!eDisplayDottedLine.equalsIgnoreCase("") && eDisplayDottedLine.equalsIgnoreCase("Yes")) {
            //Draw polyline
            if (polyline != null) {
                polyline.remove();
                polyline = null;


            }
            polyline = gMap.addPolyline(options.width(10).color(Color.BLACK).geodesic(false).pattern(pattern));
        } else {
            if (polyline != null) {
                polyline.remove();
                polyline = null;


            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getTrackDetails();

    }

    public void getTrackDetails() {
        try {
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("type", "getOrderDeliveryLog");
            parameters.put("iOrderId", iOrderId);
            parameters.put("iUserId", generalFunctions.getMemberId());
            parameters.put("UserType", Utils.userType);
            parameters.put("eSystem", Utils.eSystem_Type);

            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);

            exeWebServer.setLoaderConfig(getActContext(), true, generalFunctions);
            exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
                @Override
                public void setResponse(String responseString) {

                    if (responseString != null && !responseString.equals("")) {
                        String action = generalFunctions.getJsonValue(Utils.action_str, responseString);
                        if (action.equals("1")) {
                            listData.clear();

                            if (generalFunctions.getJsonValue("iServiceId", responseString) != null && !generalFunctions.getJsonValue("iServiceId", responseString).equalsIgnoreCase("")) {
                                serviceId = generalFunctions.getJsonValue("iServiceId", responseString);
                            }
                            ETA = generalFunctions.getJsonValue("ETA", responseString);

                            eBuyAnyService = generalFunctions.getJsonValue("eBuyAnyService", responseString);

                            eDisplayDottedLine = generalFunctions.getJsonValue("eDisplayDottedLine", responseString);
                            eDisplayRouteLine = generalFunctions.getJsonValue("eDisplayRouteLine", responseString);

                            CompanyAddress = generalFunctions.getJsonValue("CompanyAddress", responseString);
                            DeliveryAddress = generalFunctions.getJsonValue("DeliveryAddress", responseString);
                            isContactLessDeliverySelected = generalFunctions.getJsonValue("isContactLessDeliverySelected", responseString);
                            contactLessDeliveryArea.setVisibility(isContactLessDeliverySelected.equalsIgnoreCase("Yes") ? View.VISIBLE : View.GONE);
                            prepareTime = generalFunctions.getJsonValue("prepareTime", responseString);
                            eTakeAway = generalFunctions.getJsonValue("eTakeAway", responseString);
                            TIP_AMOUNT_1_VALUE = generalFunctions.getJsonValue("TIP_AMOUNT_1_VALUE", responseString);
                            fTotalItemPrice = generalFunctions.getJsonValue("fTotalItemPrice", responseString);
                            userCurrencySymbol = generalFunctions.getJsonValue("userCurrencySymbol", responseString);
                            TIP_AMOUNT_2_VALUE = generalFunctions.getJsonValue("TIP_AMOUNT_2_VALUE", responseString);
                            TIP_AMOUNT_3_VALUE = generalFunctions.getJsonValue("TIP_AMOUNT_3_VALUE", responseString);
                            showTipAddArea = generalFunctions.getJsonValue("showTipAddArea", responseString).equalsIgnoreCase("Yes");
                            menuInvisible();
                            if (eTakeAway.equalsIgnoreCase("Yes")) {
                                vieDetailsArea.setVisibility(View.GONE);
                            }
                            eTakeAwayPickedUpNote = generalFunctions.getJsonValue("eTakeAwayPickedUpNote", responseString);

                            takeAwayDetailTxt.setText(prepareTime);

                            boolean isETakeAway = Utils.checkText(eTakeAway) && eTakeAway.equalsIgnoreCase("Yes");

                            if (finaldelArea.getVisibility() != View.VISIBLE) {
                                delTitleTxtView.setText(generalFunctions.retrieveLangLBl("", isETakeAway ? "LBL_TAKE_AWAY_ORDER_PICKEDUP_TXT" : "LBL_ORDER_DELIVERED"));
                                delMsgTxtViewm.setText(isETakeAway ? eTakeAwayPickedUpNote : generalFunctions.retrieveLangLBl("", "LBL_ORDER_DELIVER_MSG"));
                                btn_confirm_txt.setVisibility(isETakeAway ? View.GONE : View.VISIBLE);

                                if (eBuyAnyService.equalsIgnoreCase("Yes")) {
                                    btn_confirm_txt.setVisibility(View.GONE);
                                }
                                timeArea.setVisibility(View.GONE);
                                btn_help.setText(generalFunctions.retrieveLangLBl("", isETakeAway ? "LBL_TAKE_AWAY_HELP_TXT" : "LBL_NOT_DELIVERD"));

                                takeAwayPickedUpLocTxt.setText(CompanyAddress);
                                takeAwayArea.setVisibility(Utils.checkText(prepareTime) && isETakeAway ? View.VISIBLE : View.GONE);
                            } else {
                                takeAwayArea.setVisibility(View.GONE);
                            }

                            String DriverPhone = generalFunctions.getJsonValue("DriverPhone", responseString);
                            String OrderPickedUpDate = generalFunctions.getJsonValue("OrderPickedUpDate", responseString);

                            if (OrderPickedUpDate.trim().equalsIgnoreCase("")) {
                                pickedUpTimeTxtView.setText("--");
                                pickedUpTimeAbbrTxtView.setText("");
                            } else {
                                String pickUpDate = generalFunctions.getDateFormatedType(generalFunctions.getJsonValue("OrderPickedUpDate", responseString), Utils.OriginalDateFormate, "hh:mm aa");
                                String[] pickUpDateArr = pickUpDate.split(" ");

                                pickedUpTimeTxtView.setText(generalFunctions.convertNumberWithRTL(pickUpDateArr[0]));
                                pickedUpTimeAbbrTxtView.setText(pickUpDateArr[1]);
                            }


                            if (generalFunctions.getJsonValue("iDriverId", responseString) != null && !generalFunctions.getJsonValue("iDriverId", responseString).equalsIgnoreCase("")) {
                                iDriverId = generalFunctions.getJsonValue("iDriverId", responseString);


                            }
                            CompanyLat = GeneralFunctions.parseDoubleValue(0, generalFunctions.getJsonValue("CompanyLat", responseString));
                            CompanyLong = GeneralFunctions.parseDoubleValue(0, generalFunctions.getJsonValue("CompanyLong", responseString));

                            generateMapLocations(CompanyLat, CompanyLong, GeneralFunctions.parseDoubleValue(0, generalFunctions.getJsonValue("PassengerLat", responseString)), GeneralFunctions.parseDoubleValue(0, generalFunctions.getJsonValue("PassengerLong", responseString)));

                            ordertitleTxt.setText("#" + generalFunctions.convertNumberWithRTL(generalFunctions.getJsonValue("vOrderNo", responseString)) + ", " +
                                    generalFunctions.convertNumberWithRTL(generalFunctions.getJsonValue("TotalOrderItems", responseString)) + ", " + generalFunctions.convertNumberWithRTL(generalFunctions.getJsonValue("fNetTotal", responseString)));

                            String tOrderRequestDate = generalFunctions.getJsonValue("tOrderRequestDate", responseString);

                            String formattedDate = generalFunctions.getDateFormatedType(tOrderRequestDate, Utils.OriginalDateFormate, CommonUtilities.OriginalDateFormate) + ", " + generalFunctions.getDateFormatedType(tOrderRequestDate, Utils.OriginalDateFormate, CommonUtilities.OriginalTimeFormate);

                            orderMsg.setText(generalFunctions.convertNumberWithRTL(formattedDate));


                            JSONArray messageArr = null;
                            messageArr = generalFunctions.getJsonArray(Utils.message_str, responseString);

                            if (generalFunctions.getJsonValue("iDriverId", responseString) != null && !generalFunctions.getJsonValue("iDriverId", responseString).equalsIgnoreCase("")
                                    && !generalFunctions.getJsonValue("iDriverId", responseString).equalsIgnoreCase("0")) {
                                iDriverId = generalFunctions.getJsonValue("iDriverId", responseString);

                                //  unSubscribeToDriverLocChannel();

                                LatLng driverLocation_update = new LatLng(generalFunctions.parseDoubleValue(0.0, generalFunctions.getJsonValue("DriverLat", responseString)),
                                        generalFunctions.parseDoubleValue(0.0, generalFunctions.getJsonValue("DriverLong", responseString)));

                                driverLocation = driverLocation_update;
                                //timeArea.setVisibility(View.GONE);
                                if (eDisplayRouteLine.equalsIgnoreCase("Yes") && !eTakeAway.equalsIgnoreCase("Yes")) {
                                    Location driverLoc = new Location("Driverloc");
                                    driverLoc.setLatitude(driverLocation_update.latitude);
                                    driverLoc.setLongitude(driverLocation_update.longitude);
                                    callUpdateDeirection(driverLoc);
                                    timeArea.setVisibility(View.VISIBLE);
                                }

                                updateDriverLocation(driverLocation);
                                subscribeToDriverLocChannel();

                            }

                            String RIDE_DRIVER_CALLING_METHOD = generalFunctions.getJsonValue("RIDE_DRIVER_CALLING_METHOD", userprofileJson);
                            String CALLMASKING_ENABLED = generalFunctions.getJsonValue("CALLMASKING_ENABLED", userprofileJson);
                            String vName = generalFunctions.getJsonValue("vName", userprofileJson);
                            String vImgName = generalFunctions.getJsonValue("vImgName", userprofileJson);

                            String LBL_INCOMING_CALL = generalFunctions.retrieveLangLBl("", "LBL_INCOMING_CALL");

                            for (int i = 0; i < messageArr.length(); i++) {
                                JSONObject rowObject = generalFunctions.getJsonObject(messageArr, i);
                                HashMap<String, String> mapData = new HashMap<>();
                                mapData.put("vStatus", generalFunctions.getJsonValueStr("vStatus_Track", rowObject));
                                mapData.put("vStatusTitle", generalFunctions.getJsonValueStr("vStatus", rowObject));
                                mapData.put("eShowCallImg", generalFunctions.getJsonValueStr("eShowCallImg", rowObject));
                                mapData.put("iStatusCode", generalFunctions.getJsonValueStr("iStatusCode", rowObject));
                                mapData.put("OrderCurrentStatusCode", generalFunctions.getJsonValueStr("OrderCurrentStatusCode", rowObject));
                                mapData.put("eCompleted", generalFunctions.getJsonValueStr("eCompleted", rowObject));
                                mapData.put("driverImage", generalFunctions.getJsonValueStr("driverImage", rowObject));
                                mapData.put("driverName", generalFunctions.getJsonValueStr("driverName", rowObject));
                                mapData.put("iDriverId", generalFunctions.getJsonValueStr("iDriverId", rowObject));
                                mapData.put("driverImageUrl", CommonUtilities.PROVIDER_PHOTO_PATH + mapData.get("iDriverId") + "/"
                                        + mapData.get("driverImage"));
                                mapData.put("DriverPhone", DriverPhone);
                                mapData.put("RIDE_DRIVER_CALLING_METHOD", RIDE_DRIVER_CALLING_METHOD);
                                mapData.put("CALLMASKING_ENABLED", CALLMASKING_ENABLED);
                                mapData.put("vName", vName);
                                mapData.put("vImgName", vImgName);
                                mapData.put("LBL_INCOMING_CALL", LBL_INCOMING_CALL);
                                mapData.put("showPaymentButton", generalFunctions.getJsonValueStr("showPaymentButton", rowObject));
                                mapData.put("showViewBillButton", generalFunctions.getJsonValueStr("showViewBillButton", rowObject));
                                mapData.put("fStoreBillAmount", generalFunctions.getJsonValueStr("fStoreBillAmount", rowObject));
                                if (generalFunctions.getJsonValueStr("genieWaitingForUserApproval", rowObject) != null) {
                                    JSONObject itemForReview = generalFunctions.getJsonObject("itemForReview", rowObject);
                                    mapData.put("genieWaitingForUserApproval", generalFunctions.getJsonValueStr("genieWaitingForUserApproval", rowObject));
                                    mapData.put("iOrderId", generalFunctions.getJsonValueStr("iOrderId", itemForReview));
                                    mapData.put("iOrderDetailId", generalFunctions.getJsonValueStr("iOrderDetailId", itemForReview));
                                    mapData.put("iItemDetailsId", generalFunctions.getJsonValueStr("iItemDetailsId", itemForReview));
                                    mapData.put("iQty", generalFunctions.getJsonValueStr("iQty", itemForReview));
                                    mapData.put("MenuItem", generalFunctions.getJsonValueStr("MenuItem", itemForReview));
                                    mapData.put("fTotPrice", generalFunctions.getJsonValueStr("fTotPrice", itemForReview));
                                    mapData.put("vImage", generalFunctions.getJsonValueStr("vImage", itemForReview));
                                    mapData.put("eItemAvailable", generalFunctions.getJsonValueStr("eItemAvailable", itemForReview));
                                    mapData.put("eExtraPayment", generalFunctions.getJsonValueStr("eExtraPayment", itemForReview));
                                } else {
                                    mapData.put("genieWaitingForUserApproval", "Yes");

                                }
                                if (generalFunctions.getJsonValueStr("genieUserApproved", rowObject) != null) {
                                    mapData.put("genieUserApproved", generalFunctions.getJsonValueStr("genieUserApproved", rowObject));


                                } else {
                                    mapData.put("genieUserApproved", "Yes");

                                }

                                Object iorderId = generalFunctions.getJsonValue("iOrderId", rowObject);
                                if (iorderId != null && Utils.checkText(iorderId.toString())) {
                                    mapData.put("iOrderId", iorderId.toString());
                                    iOrderId = iorderId.toString();
                                }

                                if (mapData.get("vStatus").equalsIgnoreCase("Delivered")) {
                                    vImageDeliveryPref = generalFunctions.getJsonValueStr("vImageDeliveryPref", rowObject);
                                }

                                String dDate = generalFunctions.getJsonValue("dDate", rowObject).toString();

                                if (!dDate.trim().equalsIgnoreCase("")) {
                                    String dLogDate = generalFunctions.getDateFormatedType(dDate, Utils.OriginalDateFormate, "hh:mm aa");
                                    String[] dLogDateArr = dLogDate.split(" ", 2);
                                    String date = dLogDateArr[0];
                                    mapData.put("dDate", date);
                                    mapData.put("dDateConverted", generalFunctions.convertNumberWithRTL(date));
                                    mapData.put("dDateAMPM", dLogDateArr[1]);
                                } else {
                                    mapData.put("dDate", "");
                                    mapData.put("dDateConverted", "");
                                    mapData.put("dDateAMPM", "");
                                }


                                listData.add(mapData);
                            }


                            if (ETA != null && !ETA.equalsIgnoreCase("")) {
                                long time = ((Long.parseLong(ETA) / 60));
                                distanceVTxtView.setText(getTimeTxt((int) time));
                            }


                            adapter.notifyDataSetChanged();


                        } else {
                        }
                    } else {
                        generalFunctions.showError();
                    }
                }
            });
            exeWebServer.execute();
        } catch (Exception e) {

        }
    }

    public String getTimeTxt(int duration) {

        if (duration < 1) {
            duration = 1;
        }
        String durationTxt = "";
        String timeToreach = duration == 0 ? "--" : "" + duration;

        timeToreach = duration >= 60 ? formatHoursAndMinutes(duration) : timeToreach;


        durationTxt = (duration < 60 ? generalFunctions.retrieveLangLBl("", "LBL_MINS_SMALL") : generalFunctions.retrieveLangLBl("", "LBL_HOUR_TXT"));

        durationTxt = duration == 1 ? generalFunctions.retrieveLangLBl("", "LBL_MIN_SMALL") : durationTxt;
        durationTxt = duration > 120 ? generalFunctions.retrieveLangLBl("", "LBL_HOURS_TXT") : durationTxt;

        Logger.d("durationTxt", "::" + durationTxt);
        return timeToreach + " " + durationTxt;
    }

    public static String formatHoursAndMinutes(int totalMinutes) {
        String minutes = Integer.toString(totalMinutes % 60);
        minutes = minutes.length() == 1 ? "0" + minutes : minutes;
        return (totalMinutes / 60) + ":" + minutes;
    }

    Menu menu;
    MenuItem menuTipItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.track_order_activity, menu);
        this.menu = menu;
        menu.findItem(R.id.menu_help).setTitle(generalFunctions.retrieveLangLBl("", "LBL_HELP"));
        menu.findItem(R.id.menu_order_details).setTitle(generalFunctions.retrieveLangLBl("", "LBL_VIEW_ORDER_DETAILS"));
        menu.findItem(R.id.menu_tip).setTitle(generalFunctions.retrieveLangLBl("", "LBL_ORDER_TIP_TITLE_TXT"));
        Utils.setMenuTextColor(menu.findItem(R.id.menu_order_details), getResources().getColor(R.color.black));
        Utils.setMenuTextColor(menu.findItem(R.id.menu_help), getResources().getColor(R.color.black));
        menu.findItem(R.id.menu_tip).setVisible(showTipAddArea);
        Utils.setMenuTextColor(menu.findItem(R.id.menu_tip), getResources().getColor(R.color.black));


        return true;
    }


    public void menuInvisible() {
        if (menuTipItem != null) {
            invalidateOptionsMenu();
            menuTipItem.setVisible(showTipAddArea); // make true to make the menu item visible.
        } else if (menu != null) {
            invalidateOptionsMenu();
            // TIP MENu
            menu.findItem(R.id.menu_tip).setVisible(showTipAddArea);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bn = new Bundle();
        switch (item.getItemId()) {

            case R.id.menu_order_details:
                bn.putString("iOrderId", iOrderId);
                new StartActProcess(getActContext()).startActWithData(OrderDetailsActivity.class, bn);
                return true;

            // TIP MENU
            case R.id.menu_tip:
                this.menuTipItem = item;
                showTipDialog();
                return true;

            case R.id.menu_help:

                bn.putString("iOrderId", iOrderId);
                new StartActProcess(getActContext()).startActWithData(Help_MainCategory.class, bn);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // TIP Feature Functionality Start
    MTextView tipAmountText1, tipAmountText2, tipAmountText3, tipAmountTextOther;
    MTextView errorTxt;
    MTextView tv_percentageAmount;
    LinearLayout tipAmountAreaOther, tipAmountArea1, tipAmountArea2, tipAmountArea3, addTipArea;
    LinearLayout amountArea;
    ImageView closeImgOther, closeImg1, closeImg2, closeImg3, iv_tip_help;
    EditText tipAmountBox;

    public void showTipDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.design_order_tip_layout, null);
        builder.setView(dialogView);

        ImageView iv_giveTip = dialogView.findViewById(R.id.iv_giveTip);
        MTextView noThanksArea = dialogView.findViewById(R.id.noThanksArea);
        FrameLayout btnArea = dialogView.findViewById(R.id.tipBtnArea);
        amountArea = dialogView.findViewById(R.id.amountArea);
        errorTxt = dialogView.findViewById(R.id.errorTxt);
        tv_percentageAmount = dialogView.findViewById(R.id.tv_percentageAmount);
//        MTextView giveTipArea = dialogView.findViewById(R.id.giveTipArea);
        MTextView tipTitleText = (MTextView) dialogView.findViewById(R.id.tipTitleText);
        MTextView tipDescTitleText = (MTextView) dialogView.findViewById(R.id.tipDescTitleText);
        MTextView tipDescHintTitleText = (MTextView) dialogView.findViewById(R.id.tipDescHintTitleText);
        tipAmountBox = (EditText) dialogView.findViewById(R.id.tipAmountBox);
        addTipArea = (LinearLayout) dialogView.findViewById(R.id.addTipArea);
        noThanksArea.setText(generalFunctions.retrieveLangLBl("", "LBL_NO_THANKS"));
//        MButton tipGivenBtn = ((MaterialRippleLayout) dialogView.findViewById(R.id.tipGivenBtn)).getChildView();
        SelectableRoundedImageView tipGivenBtn = (SelectableRoundedImageView) dialogView.findViewById(R.id.tipGivenBtn);
//        giveTipId = Utils.generateViewId();
//        tipGivenBtn.setId(giveTipId);

        MButton giveTipArea = ((MaterialRippleLayout) dialogView.findViewById(R.id.giveTipArea)).getChildView();
        btnArea.setVisibility(View.GONE);
        iv_giveTip.setVisibility(View.GONE);
        giveTipArea.setText(generalFunctions.retrieveLangLBl("", "LBL_GIVE_TIP"));
        tipAmountTextOther = (MTextView) dialogView.findViewById(R.id.tipAmountTextOther);
        tipAmountAreaOther = (LinearLayout) dialogView.findViewById(R.id.tipAmountAreaOther);
        closeImgOther = (ImageView) dialogView.findViewById(R.id.closeImgOther);

        tipAmountText1 = (MTextView) dialogView.findViewById(R.id.tipAmountText1);
        tipAmountArea1 = (LinearLayout) dialogView.findViewById(R.id.tipAmountArea1);
        closeImg1 = (ImageView) dialogView.findViewById(R.id.closeImg1);

        tipAmountText2 = (MTextView) dialogView.findViewById(R.id.tipAmountText2);
        tipAmountArea2 = (LinearLayout) dialogView.findViewById(R.id.tipAmountArea2);
        closeImg2 = (ImageView) dialogView.findViewById(R.id.closeImg2);

        tipAmountText3 = (MTextView) dialogView.findViewById(R.id.tipAmountText3);
        tipAmountArea3 = (LinearLayout) dialogView.findViewById(R.id.tipAmountArea3);
        closeImg3 = (ImageView) dialogView.findViewById(R.id.closeImg3);
        iv_tip_help = (ImageView) dialogView.findViewById(R.id.iv_tip_help);
        iv_tip_help.setVisibility(View.VISIBLE);
        tipDescHintTitleText.setVisibility(View.GONE);

        tipAmountBox.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        tipAmountBox.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});

        new CreateRoundedView(getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), getResources().getDimension(R.dimen._30sdp)), 10,
                getResources().getColor(R.color.gray_holo_dark), tipGivenBtn);
//        tipGivenBtn.setColorFilter(getResources().getColor(R.color.appThemeColor_TXT_1));


        tipTitleText.setText(generalFunctions.retrieveLangLBl("", "LBL_ORDER_TIP_TITLE_TXT"));
        tipDescTitleText.setText(generalFunctions.retrieveLangLBl("", "LBL_ORDER_TIP_DESCRIPTION_TXT"));
        tipDescHintTitleText.setText(generalFunctions.retrieveLangLBl("", "LBL_ORDER_TIP_HOW_WORKS_TXT"));
        tipAmountBox.setHint(generalFunctions.retrieveLangLBl("", "LBL_TIP_AMOUNT_ENTER_TITLE"));
//        tipGivenBtn.setText(generalFunctions.retrieveLangLBl("", "LBL_BTN_SUBMIT_TXT"));

        String CurrencySymbol = generalFunctions.getJsonValue("CurrencySymbol", userprofileJson);
        String DELIVERY_TIP_AMOUNT_TYPE_DELIVERALL = generalFunctions.getJsonValue("DELIVERY_TIP_AMOUNT_TYPE_DELIVERALL", userprofileJson);
        String tipAMount1 = generalFunctions.convertNumberWithRTL(generalFunctions.getJsonValue("TIP_AMOUNT_1", userprofileJson));
        String tipAMount2 = generalFunctions.convertNumberWithRTL(generalFunctions.getJsonValue("TIP_AMOUNT_2", userprofileJson));
        String tipAMount3 = generalFunctions.convertNumberWithRTL(generalFunctions.getJsonValue("TIP_AMOUNT_3", userprofileJson));
        isPercentage = DELIVERY_TIP_AMOUNT_TYPE_DELIVERALL.equalsIgnoreCase("Percentage");
        boolean eReverseSymbolEnable = generalFunctions.retrieveValue("eReverseSymbolEnable").equalsIgnoreCase("Yes");
        tipAmountText1.setTag(isPercentage ? 1 : tipAMount1);
        tipAmountText1.setText(isPercentage ? tipAMount1 : eReverseSymbolEnable ? tipAMount1 + " " + CurrencySymbol : CurrencySymbol + tipAMount1);
        tipAmountText2.setTag(isPercentage ? 2 : tipAMount2);
        tipAmountText2.setText(isPercentage ? tipAMount2 : eReverseSymbolEnable ? tipAMount2 + " " + CurrencySymbol : CurrencySymbol + tipAMount2);
        tipAmountText3.setTag(isPercentage ? 3 : tipAMount3);
        tipAmountText3.setText(isPercentage ? tipAMount3 : eReverseSymbolEnable ? tipAMount3 + " " + CurrencySymbol : CurrencySymbol + tipAMount3);
        tipAmountTextOther.setTag(isPercentage ? 4 : "");

        tipAmountArea1.setOnClickListener(view -> {
            setSelected(tipAmountText1, tipAmountArea1);
        });

        closeImg1.setOnClickListener(view -> {
            removeSelectedTip();
        });

        iv_tip_help.setOnClickListener(view -> {
//            showTipInfoDialog(getActContext().getResources().getDrawable(R.drawable.ic_save_money),"LBL_TIP_DIALOG_DESCRIPTION");
            showTipInfoDialog(getActContext().getResources().getDrawable(R.drawable.ic_save_money), "LBL_DELIVERY_TIP_DESC");

        });
        tipAmountArea2.setOnClickListener(view -> {
            setSelected(tipAmountText2, tipAmountArea2);
        });

        closeImg2.setOnClickListener(view -> {
            removeSelectedTip();
        });
        tipAmountArea3.setOnClickListener(view -> {
            setSelected(tipAmountText3, tipAmountArea3);
        });

        closeImg3.setOnClickListener(view -> {
            removeSelectedTip();
        });
        tipAmountAreaOther.setOnClickListener(view -> {
            setSelected(tipAmountTextOther, tipAmountAreaOther);
        });

        closeImgOther.setOnClickListener(view -> {
            removeSelectedTip();
        });

        tipAmountBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tipAmount.equalsIgnoreCase("4")) {
                    resetErrorTxt();
                    if (Utils.checkText(s.toString())) {
                        tv_percentageAmount.setText(LBL_TIP_AMOUNT + ": " + new AppFunctions(getActContext()).formatNumAsPerCurrency(generalFunctions, Utils.getText(tipAmountBox), userCurrencySymbol, true));
                        tv_percentageAmount.setVisibility(View.VISIBLE);
                    } else {
                        tv_percentageAmount.setText("");
                        tv_percentageAmount.setVisibility(View.GONE);
                    }
                }
            }
        });
        noThanksArea.setOnClickListener(view -> {

            if (alertDialog != null) {
                alertDialog.dismiss();
            }
        });

        giveTipArea.setOnClickListener(view -> {


            if (addTipArea.getVisibility() == View.VISIBLE) {
                final boolean tipAmountEntered = Utils.checkText(tipAmountBox) ? true : false;
                if (tipAmountEntered == false) {
                    amountArea.setBackground(getActContext().getResources().getDrawable(R.drawable.roundrecterrorwithdesign));
                    errorTxt.setText(generalFunctions.retrieveLangLBl("", "LBL_REQUIRED"));
                    tv_percentageAmount.setText("");
                    tv_percentageAmount.setVisibility(View.GONE);
                    errorTxt.setVisibility(View.VISIBLE);
                    return;
                } else {
                    resetErrorTxt();
                }


                if (GeneralFunctions.parseDoubleValue(0, tipAmountBox.getText().toString()) > 0) {
                    resetErrorTxt();
                    giveTip();

                } else {
                    tv_percentageAmount.setText("");
                    tv_percentageAmount.setVisibility(View.GONE);
                    amountArea.setBackground(getActContext().getResources().getDrawable(R.drawable.roundrecterrorwithdesign));
                    errorTxt.setText(generalFunctions.retrieveLangLBl("", "LBL_ENTER_GRATER_ZERO_VALUE_TXT"));
                    errorTxt.setVisibility(View.VISIBLE);

                }

            } else {
                giveTip();
            }


        });


        alertDialog = builder.create();
        alertDialog.setCancelable(false);

        if (generalFunctions.isRTLmode()) {
            generalFunctions.forceRTLIfSupported(alertDialog);
        }
        alertDialog.show();
    }

    private void resetErrorTxt() {
        errorTxt.setText("");
        errorTxt.setVisibility(View.GONE);
        amountArea.setBackground(getActContext().getResources().getDrawable(R.drawable.roundrectwithdesign));
    }

    private void showTipInfoDialog(Drawable drawable, String LBL_DATA) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActContext());
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.design_tip_help, null);
        builder.setView(dialogView);


        final ImageView iamage_source = (ImageView) dialogView.findViewById(R.id.iamage_source);
        if (drawable != null) {
            iamage_source.setImageDrawable(drawable);
        }
        final MTextView okTxt = (MTextView) dialogView.findViewById(R.id.okTxt);
        final MTextView titileTxt = (MTextView) dialogView.findViewById(R.id.titileTxt);
        final MTextView msgTxt = (MTextView) dialogView.findViewById(R.id.msgTxt);
        titileTxt.setText(generalFunctions.retrieveLangLBl("Tip your delivery partner", "LBL_TIP_DIALOG_TITLE"));
        okTxt.setText(generalFunctions.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT "));
        msgTxt.setText("" + generalFunctions.retrieveLangLBl("You can set a delivery preference before the delivery agent attempts to deliver your package at your door. Your preferences will be taken care by delivery driver.", LBL_DATA));
        msgTxt.setMovementMethod(new ScrollingMovementMethod());
        okTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveTipAlertDialog.dismiss();
            }
        });
        giveTipAlertDialog = builder.create();
        giveTipAlertDialog.setCancelable(true);
        if (generalFunctions.isRTLmode() == true) {
            generalFunctions.forceRTLIfSupported(giveTipAlertDialog);
        }
        giveTipAlertDialog.getWindow().setBackgroundDrawable(getActContext().getResources().getDrawable(R.drawable.all_roundcurve_card));
        giveTipAlertDialog.show();


    }

    private void giveTip() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "OrderCollectTip");
        parameters.put("iOrderId", iOrderId);
        parameters.put("iUserId", generalFunctions.getMemberId());
        parameters.put("UserType", Utils.userType);
        parameters.put("eSystem", Utils.eSystem_Type);
        if (isPercentage && Utils.checkText(tipAmount)) {

            parameters.put("selectedTipPos", tipAmount);
            if (tipAmount.equalsIgnoreCase("4")) {
                parameters.put("fTipAmount", Utils.getText(tipAmountBox));
            }

        } else {
            parameters.put("fTipAmount", Utils.checkText(tipAmountBox) ? Utils.getText(tipAmountBox) : tipAmount);
        }

        parameters.put("iMemberId", generalFunctions.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);

        exeWebServer.setLoaderConfig(getActContext(), true, generalFunctions);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {
                    String action = generalFunctions.getJsonValue(Utils.action_str, responseString);
                    if (action.equals("1")) {
                        String paymentUrl = generalFunctions.getJsonValue("paymentUrl", responseString);
                        if (Utils.checkText(paymentUrl)) {
                            Bundle bn = new Bundle();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("URL", paymentUrl);
                            map.put("vPageTitle", generalFunctions.retrieveLangLBl("", "LBL_GIVE_TIP"));
                            bn.putSerializable("data", map);
                            new StartActProcess(getActContext()).startActForResult(QuickPaymentActivity.class, bn, Utils.REQ_VERIFY_INSTANT_PAYMENT_CODE);
                            return;
                        }
                        showTipSuccessMsg();
                    } else {

                        String message = generalFunctions.getJsonValue(Utils.message_str, responseString);
                        if (message.equalsIgnoreCase("LBL_NO_CARD_AVAIL_NOTE")) {

                            final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                            generateAlert.setContentMessage("", generalFunctions.retrieveLangLBl("", generalFunctions.getJsonValue(Utils.message_str, responseString)));
                            generateAlert.setPositiveBtn(generalFunctions.retrieveLangLBl("", "LBL_ADD_CARD"));
                            generateAlert.setNegativeBtn(generalFunctions.retrieveLangLBl("", "LBL_CANCEL_TXT"));
                            generateAlert.setBtnClickList(btn_id -> {
                                if (btn_id == 1) {
                                    generateAlert.closeAlertBox();
                                    Bundle bn = new Bundle();
                                    new StartActProcess(getActContext()).startActForResult(CardPaymentActivity.class, bn, Utils.CARD_PAYMENT_REQ_CODE);
                                } else {
                                    generateAlert.closeAlertBox();
                                }
                            });
                            generateAlert.showAlertBox();
                        } else if (message.equalsIgnoreCase("LBL_REQUIRED_MINIMUM_AMOUT")) {
                            generalFunctions.showGeneralMessage("", generalFunctions.retrieveLangLBl("", generalFunctions.getJsonValue(Utils.message_str_one, responseString)));
                        } else {
                            generalFunctions.showGeneralMessage("", generalFunctions.retrieveLangLBl("", generalFunctions.getJsonValue(Utils.message_str, responseString)));
                        }

                    }
                } else {
                    generalFunctions.showError();

                }
            }


        });
        exeWebServer.execute();

    }

    private void showTipSuccessMsg() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        openSucessDialog();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.REQ_VERIFY_INSTANT_PAYMENT_CODE && resultCode == RESULT_OK && data != null) {

            if (data != null) {
                showTipSuccessMsg();
            } else {
                generalFunctions.showError();
            }
        } else if (requestCode == Utils.SELECT_ORGANIZATION_PAYMENT_CODE && resultCode == RESULT_OK && data != null) {
            handlePayment(data);

        }

    }

    public void handlePayment(Intent data) {
        boolean iscash = true;
        boolean eWalletDebitAllow = false;
        if (data.getBooleanExtra("isCash", false)) {
            iscash = true;
        } else {
            iscash = false;
        }
        if (data.getBooleanExtra("isWallet", false)) {
            eWalletDebitAllow = true;
        } else {
            eWalletDebitAllow = false;
        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "CollectPaymentBuyAnything");
        parameters.put("iOrderId", iOrderId);
        parameters.put("CheckUserWallet", eWalletDebitAllow ? "Yes" : "No");
        parameters.put("UserType", Utils.userType);
        parameters.put("eSystem", Utils.eSystem_Type);


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunctions);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {
                    String action = generalFunctions.getJsonValue(Utils.action_str, responseString);
                    if (action.equals("1")) {


                        generalFunctions.showGeneralMessage("", generalFunctions.retrieveLangLBl("", generalFunctions.getJsonValue(Utils.message_str_one, responseString)), "", generalFunctions.retrieveLangLBl("", "LBL_BTN_OK_TXT"), button_Id -> {

                            getTrackDetails();

                        });


                    }
                } else {
                    generalFunctions.showError();
                }
            }
        });
        exeWebServer.execute();

    }

    private void setSelected(MTextView selectedTextAreaTxtView, LinearLayout selectedArea) {
        tv_percentageAmount.setVisibility(isPercentage ? View.VISIBLE : View.GONE);
        tipAmount = selectedTextAreaTxtView.getTag().toString();
        tipAmountBox.setText("");
        addTipArea.setVisibility(selectedArea == tipAmountAreaOther ? View.VISIBLE : View.GONE);
        resetErrorTxt();
        errorTxt.setVisibility(selectedArea == tipAmountAreaOther ? View.VISIBLE : View.GONE);
        tipAmountArea1.setBackground(getActContext().getResources().getDrawable(selectedArea == tipAmountArea1 ? R.drawable.button_background : R.drawable.button_normal_background));
        closeImg1.setVisibility(selectedArea == tipAmountArea1 ? View.VISIBLE : View.GONE);
        if (selectedArea == tipAmountArea1) {
            tv_percentageAmount.setText(LBL_TIP_AMOUNT + ": " + new AppFunctions(getActContext()).formatNumAsPerCurrency(generalFunctions, TIP_AMOUNT_1_VALUE, userCurrencySymbol, true));
        }
        tipAmountArea2.setBackground(getActContext().getResources().getDrawable(selectedArea == tipAmountArea2 ? R.drawable.button_background : R.drawable.button_normal_background));
        closeImg2.setVisibility(selectedArea == tipAmountArea2 ? View.VISIBLE : View.GONE);
        if (selectedArea == tipAmountArea2) {
            tv_percentageAmount.setText(LBL_TIP_AMOUNT + ": " + new AppFunctions(getActContext()).formatNumAsPerCurrency(generalFunctions, TIP_AMOUNT_2_VALUE, userCurrencySymbol, true));
        }
        tipAmountArea3.setBackground(getActContext().getResources().getDrawable(selectedArea == tipAmountArea3 ? R.drawable.button_background : R.drawable.button_normal_background));
        closeImg3.setVisibility(selectedArea == tipAmountArea3 ? View.VISIBLE : View.GONE);
        if (selectedArea == tipAmountArea3) {
            tv_percentageAmount.setText(LBL_TIP_AMOUNT + ": " + new AppFunctions(getActContext()).formatNumAsPerCurrency(generalFunctions, TIP_AMOUNT_3_VALUE, userCurrencySymbol, true));
        }
        tipAmountAreaOther.setBackground(getActContext().getResources().getDrawable(selectedArea == tipAmountAreaOther ? R.drawable.button_background : R.drawable.button_normal_background));
        closeImgOther.setVisibility(selectedArea == tipAmountAreaOther ? View.VISIBLE : View.GONE);
        if (!isPercentage) {
            tipAmountTextOther.setTag("");
        }
        if (selectedArea == tipAmountAreaOther) {
            tv_percentageAmount.setVisibility(Utils.checkText(tipAmountBox) ? View.VISIBLE : View.GONE);
            tv_percentageAmount.setText("");
        }
        tipAmountTextOther.setText(generalFunctions.retrieveLangLBl("", "LBL_OTHER_TXT"));
    }

    private void removeSelectedTip() {
        tipAmount = "";
        tipAmountBox.setText("");
        tv_percentageAmount.setText("");
        tipAmountArea1.setBackground(getActContext().getResources().getDrawable(R.drawable.button_normal_background));
        tipAmountArea2.setBackground(getActContext().getResources().getDrawable(R.drawable.button_normal_background));
        tipAmountArea3.setBackground(getActContext().getResources().getDrawable(R.drawable.button_normal_background));
        tipAmountAreaOther.setBackground(getActContext().getResources().getDrawable(R.drawable.button_normal_background));
        closeImg1.setVisibility(View.GONE);
        closeImg2.setVisibility(View.GONE);
        closeImg3.setVisibility(View.GONE);
        closeImgOther.setVisibility(View.GONE);
        if (!isPercentage) {
            tipAmountTextOther.setTag("");
        }
        tipAmountTextOther.setText(generalFunctions.retrieveLangLBl("", "LBL_OTHER_TXT"));
        addTipArea.setVisibility(View.GONE);
        resetErrorTxt();
    }

    public void openSucessDialog() {

        dialog_sucess = new Dialog(getActContext(), R.style.ImageSourceDialogStyle);
        dialog_sucess.setContentView(R.layout.sucess_layout_nw);
        MTextView titleTxt = (MTextView) dialog_sucess.findViewById(R.id.titleTxt);
        MTextView msgTxt = (MTextView) dialog_sucess.findViewById(R.id.msgTxt);

        msgTxt.setText(generalFunctions.retrieveLangLBl("", "LBL_DELIVERY_TIP_SUCCESS_TXT"));
//        titleTxt.setText(generalFunctions.retrieveLangLBl("", ""));


        MButton btn_type2 = ((MaterialRippleLayout) dialog_sucess.findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setText(generalFunctions.retrieveLangLBl("", "LBL_BTN_OK_TXT"));


        btn_type2.setOnClickListener(view -> {
            dialog_sucess.dismiss();
            getTrackDetails();
        });

        dialog_sucess.setCancelable(false);
        dialog_sucess.show();

    }

    // TIP Feature Functionality End
    @Override
    public void onOptionsMenuClosed(Menu menu) {


        super.onOptionsMenuClosed(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {

        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {

        if (fromNoti) {
            Bundle bn = new Bundle();
            new StartActProcess(getActContext()).startActWithData(UberXHomeActivity.class, bn);
            finishAffinity();
            return;
        }

        if (getIntent().getBooleanExtra("isRestart", false)) {
            MyApp.getInstance().restartWithGetDataApp();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void setOnreviewClick(int position) {
        openconfirmGenieItem(position);

    }

    @Override
    public void setOnMakePaymentClick(int position) {
        Bundle bn = new Bundle();
        //  new StartActProcess(getActContext()).startActForResult(BusinessSelectPaymentActivity.class, bn, Utils.SELECT_ORGANIZATION_PAYMENT_CODE);

        bn.putBoolean("isGenie", true);
        bn.putBoolean("isCODAllow", false);
        new StartActProcess(getActContext()).startActForResult(ProfilePaymentActivity.class, bn, Utils.SELECT_ORGANIZATION_PAYMENT_CODE);

    }

    @Override
    public void setOnviewBill(int position) {
        viewBillDetails();
    }

    public void viewBillDetails() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GetBuyAnyServiceBillDetails");
        parameters.put("iOrderId", iOrderId);
        parameters.put("UserType", Utils.userType);
        parameters.put("eSystem", Utils.eSystem_Type);


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunctions);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {
                    String action = generalFunctions.getJsonValue(Utils.action_str, responseString);
                    if (action.equals("1")) {

                        openViewBillDialog(responseString);

                    }
                } else {
                    generalFunctions.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    LinearLayout fareDetailDisplayArea;

    public void openViewBillDialog(String responseString) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActContext());

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);




        titleTxt.setText(generalFunctions.retrieveLangLBl("Bill Details", "LBL_INVOICE_DETAILS"));

        JSONArray FareDetailsArrNewObj = null;
        FareDetailsArrNewObj = generalFunctions.getJsonArray("FareDetailsArr", responseString);


        if (fareDetailDisplayArea.getChildCount() > 0) {
            fareDetailDisplayArea.removeAllViewsInLayout();
        }

        for (int i = 0; i < FareDetailsArrNewObj.length(); i++) {
            JSONObject jobject = generalFunctions.getJsonObject(FareDetailsArrNewObj, i);
            try {
                String data = jobject.names().getString(0);
                addFareDetailRow(data, jobject.get(data).toString(), (FareDetailsArrNewObj.length() - 1) == i ? true : false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        billDeatilsGenieAlert = builder.create();
        if (generalFunctions.isRTLmode()) {
            generalFunctions.forceRTLIfSupported(billDeatilsGenieAlert);
        }
        billDeatilsGenieAlert.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.all_roundcurve_card));
        billDeatilsGenieAlert.show();
        billDeatilsGenieAlert.setCancelable(false);
        billDeatilsGenieAlert.setOnCancelListener(dialogInterface -> Utils.hideKeyboard(getActContext()));
    }

    private void addFareDetailRow(String row_name, String row_value, boolean isLast) {
        View convertView = null;
        if (row_name.equalsIgnoreCase("eDisplaySeperator")) {
            convertView = new View(getActContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dipToPixels(getActContext(), 1));
            int dip = Utils.dipToPixels(getActContext(), 10);
            params.setMarginStart(dip);
            params.setMarginEnd(dip);
            convertView.setBackgroundColor(Color.parseColor("#dedede"));
            convertView.setLayoutParams(params);
        } else {
            LayoutInflater infalInflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.design_fare_breakdown_row, null);

            convertView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


            MTextView titleHTxt = (MTextView) convertView.findViewById(R.id.titleHTxt);
            MTextView titleVTxt = (MTextView) convertView.findViewById(R.id.titleVTxt);

            titleHTxt.setText(generalFunctions.convertNumberWithRTL(row_name));
            titleVTxt.setText(generalFunctions.convertNumberWithRTL(row_value));


            if (isLast) {
                convertView.setMinimumHeight(Utils.dipToPixels(getActContext(), 40));

                titleHTxt.setTextColor(getResources().getColor(R.color.black));
                titleHTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Poppins_SemiBold.ttf");
                titleHTxt.setTypeface(face);
                titleVTxt.setTypeface(face);
                titleVTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                titleVTxt.setTextColor(getResources().getColor(R.color.appThemeColor_1));
            }
        }


        if (convertView != null)
            fareDetailDisplayArea.addView(convertView);
    }

    public void openconfirmGenieItem(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActContext());

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        btn_confirm.setOnClickListener(view -> {
            final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
            generateAlert.setContentMessage("", generalFunctions.retrieveLangLBl("", "LBL_GENIE_REVIEW_ALERT"));
            generateAlert.setPositiveBtn(generalFunctions.retrieveLangLBl("", "LBL_YES"));
            generateAlert.setNegativeBtn(generalFunctions.retrieveLangLBl("", "LBL_NO"));
            generateAlert.setBtnClickList(btn_id -> {
                if (btn_id == 1) {
                    handleConfirmReviewItems(pos, "Yes", "NO");
                } else {
                    generateAlert.closeAlertBox();
                }
            });
            generateAlert.showAlertBox();
        });

        btn_confirm.setText(generalFunctions.retrieveLangLBl("Confirm", "LBL_CONFIRM_TXT"));


        if (listData.get(pos).get("eExtraPayment") != null && listData.get(pos).get("eExtraPayment").equalsIgnoreCase("No")) {

        }


        ConfirmGenieAlert = builder.create();
        if (generalFunctions.isRTLmode()) {
            generalFunctions.forceRTLIfSupported(ConfirmGenieAlert);
        }
        ConfirmGenieAlert.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.all_roundcurve_card));
        ConfirmGenieAlert.show();
        ConfirmGenieAlert.setCancelable(false);
        ConfirmGenieAlert.setOnCancelListener(dialogInterface -> Utils.hideKeyboard(getActContext()));

    }

    public void handleConfirmReviewItems(int pos, String econfirm, String eDecline) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "ConfirmReviewItems");
        parameters.put("iOrderId", iOrderId);
        parameters.put("iItemDetailsId", listData.get(pos).get("iItemDetailsId"));
        parameters.put("eConfirm", econfirm);
        parameters.put("eDecline", eDecline);
        parameters.put("UserType", Utils.userType);
        parameters.put("eSystem", Utils.eSystem_Type);


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunctions);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {
                    String action = generalFunctions.getJsonValue(Utils.action_str, responseString);
                    if (action.equals("1")) {
                        if (ConfirmGenieAlert != null) {
                            ConfirmGenieAlert.dismiss();
                        }
                        getTrackDetails();

                    }
                } else {
                    generalFunctions.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    onBackPressed();
                    break;

                case R.id.navigateBtn:
                    openNavigationDialog("" + CompanyLat, "" + CompanyLong);
                    break;
                case R.id.contactLessDeliveryArea:
                    showPreferenceHelp();
                    break;
                case R.id.btn_cancel:
                case R.id.btnConfirmarea:
                    finaldelArea.setVisibility(View.GONE);
                    MyApp.getInstance().restartWithGetDataApp();
                    break;
                case R.id.vieDetailsArea:
                    finaldelArea.setVisibility(View.GONE);
                    Bundle bn = new Bundle();
                    bn.putString("iOrderId", iOrderId);
                    new StartActProcess(getActContext()).startActWithData(Help_MainCategory.class, bn);
                    break;
            }
        }
    }

    private void showPreferenceHelp() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActContext());
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.desgin_preference_help, null);
        builder.setView(dialogView);


        final MTextView okTxt = (MTextView) dialogView.findViewById(R.id.okTxt);
        final MTextView titileTxt = (MTextView) dialogView.findViewById(R.id.titileTxt);
        final WebView msgTxt = (WebView) dialogView.findViewById(R.id.msgTxt);
        titileTxt.setText(generalFunctions.retrieveLangLBl("Contactless delivery", "LBL_CONTACT_LESS_DELIVERY_TXT"));
        okTxt.setText(generalFunctions.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT "));
      /*  msgTxt.setText(Html.fromHtml("" + generalFunctions.retrieveLangLBl("Customer has selected contactless delivery option. We have introduced this feature to break infectious. To fulfil this requirement you will have to follow below steps:\n" +
                "<br>" +
                "- Stay away from customer<br>" +
                "- Put parcel at customer's door.<br>" +
                "- Capture a photo of parcel at customer's door as a proof of delivery.<br>" +
                "- Mark order as delivered", "LBL_CONTACTLESSDELIVERYUSER_NOTE_TXT ")));
        */
        //  msgTxt.setText(generalFunctions.retrieveLangLBl("Customer has selected contactless delivery option. We have introduced this feature to break infectious. To fulfil this requirement you will have to follow below steps:\n - Stay away from customer\n - Put parcel at customer's door\n - Capture a photo of parcel at customer's door as a proof of delivery.\n - Mark order as delivered", "LBL_CONTACTLESS_DELIVERYUSER_NOTE_TXT"));

        //  msgTxt.setMovementMethod(new ScrollingMovementMethod());

        msgTxt.loadDataWithBaseURL(null, generalFunctions.wrapHtml(getActContext(), generalFunctions.retrieveLangLBl("Customer has selected contactless delivery option. We have introduced this feature to break infectious. To fulfil this requirement you will have to follow below steps:\n - Stay away from customer\n - Put parcel at customer's door\n - Capture a photo of parcel at customer's door as a proof of delivery.\n - Mark order as delivered", "LBL_CONTACTLESS_DELIVERYUSER_NOTE_TXT")), "text/html", "UTF-8", null);


        okTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceAlertDialog.dismiss();
            }
        });
        preferenceAlertDialog = builder.create();
        preferenceAlertDialog.setCancelable(true);
        if (generalFunctions.isRTLmode() == true) {
            generalFunctions.forceRTLIfSupported(preferenceAlertDialog);
        }
        preferenceAlertDialog.getWindow().setBackgroundDrawable(getActContext().getResources().getDrawable(R.drawable.all_roundcurve_card));
        preferenceAlertDialog.show();


    }

    public void openNavigationDialog(final String dest_lat, final String dest_lon) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActContext());

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_selectnavigation_view, null);

        final MTextView NavigationTitleTxt = (MTextView) dialogView.findViewById(R.id.NavigationTitleTxt);
        final MTextView wazemapTxtView = (MTextView) dialogView.findViewById(R.id.wazemapTxtView);
        final MTextView googlemmapTxtView = (MTextView) dialogView.findViewById(R.id.googlemmapTxtView);
        final RadioButton radiogmap = (RadioButton) dialogView.findViewById(R.id.radiogmap);
        final RadioButton radiowazemap = (RadioButton) dialogView.findViewById(R.id.radiowazemap);
        ImageView cancelImg = (ImageView) dialogView.findViewById(R.id.cancelImg);
        radiogmap.setOnClickListener(v -> googlemmapTxtView.performClick());
        radiowazemap.setOnClickListener(v -> wazemapTxtView.performClick());

        builder.setView(dialogView);
        NavigationTitleTxt.setText(generalFunctions.retrieveLangLBl("Choose Option", "LBL_CHOOSE_OPTION"));
        googlemmapTxtView.setText(generalFunctions.retrieveLangLBl("Google map navigation", "LBL_NAVIGATION_GOOGLE_MAP"));
        wazemapTxtView.setText(generalFunctions.retrieveLangLBl("Waze navigation", "LBL_NAVIGATION_WAZE"));

        cancelImg.setOnClickListener(v -> {

            list_navigation.dismiss();
        });
        googlemmapTxtView.setOnClickListener(v -> {
            try {
                list_navigation.dismiss();
                String url_view = "http://maps.google.com/maps?daddr=" + dest_lat + "," + dest_lon;
                (new StartActProcess(getActContext())).openURL(url_view, "com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            } catch (Exception e) {
                generalFunctions.showMessage(wazemapTxtView, generalFunctions.retrieveLangLBl("Please install Google Maps in your device.", "LBL_INSTALL_GOOGLE_MAPS"));
            }
        });

        wazemapTxtView.setOnClickListener(v -> {
            try {

                String uri = "waze://?ll=" + dest_lat + "," + dest_lon + "&navigate=yes";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
                list_navigation.dismiss();

            } catch (Exception e) {
                generalFunctions.showMessage(wazemapTxtView, generalFunctions.retrieveLangLBl("Please install Waze navigation app in your device.", "LBL_INSTALL_WAZE"));
            }
        });


        list_navigation = builder.create();
        if (generalFunctions.isRTLmode()) {
            generalFunctions.forceRTLIfSupported(list_navigation);
        }
        list_navigation.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.all_roundcurve_card));
        list_navigation.show();
        list_navigation.setOnCancelListener(dialogInterface -> Utils.hideKeyboard(getActContext()));
    }

}
