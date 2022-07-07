package com.ridetogo.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;

import com.general.files.CustomHorizontalScrollView;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.OnSwipeTouchListener;
import com.general.files.SetOnTouchList;
import com.general.files.SlideAnimationUtil;
import com.general.files.StartActProcess;
import com.kyleduo.switchbutton.SwitchButton;
import com.model.Trip_Status;
import com.squareup.picasso.Picasso;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.ErrorView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;
import com.view.editBox.MaterialEditText;
import com.view.simpleratingbar.SimpleRatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.ridetogo.user.R.id.ratingBar;

public class HistoryDetailActivity extends AppCompatActivity {

    public GeneralFunctions generalFunc;
    MTextView titleTxt;
    MTextView subTitleTxt;
    ImageView backImgView;
    ImageView receiptImgView;
    LinearLayout fareDetailDisplayArea;
    View convertView = null;

    LinearLayout beforeServiceArea, afterServiceArea;
    String before_serviceImg_url = "";
    String after_serviceImg_url = "";
    String isRatingDone = "";
    MButton btn_type2;
    String userProfileJson;
    MTextView cartypeTxt;
    MTextView ufxratingDriverHTxt;
    SimpleRatingBar ufxratingBar;

    MTextView tipHTxt, tipamtTxt, tipmsgTxt;
    LinearLayout tipArea;
    private int rateBtnId;
    private MaterialEditText commentBox;
    ImageView helpTxt;
    ImageView tipPluseImage;
    MTextView vReasonTitleTxt;

    private String tripData = "";
    /*Multi Delivery Rlated fields*/
    private ArrayList<Trip_Status> recipientDetailList = new ArrayList<>();
    private Dialog signatureImageDialog;
    private String senderImage;
    String iTripId = "";

    ProgressBar loading;
    ErrorView errorView;
    RelativeLayout container;

    CardView viewReqServicesArea;
    ImageView driverImageview;

    /* Fav Driver variable declaration start */
    LinearLayout favArea;
    MTextView favDriverTitleTxt;
    SwitchButton favSwitch;
    /* Fav Driver variable declaration end */
    FrameLayout paymentMainArea;


    int width;
    int width_D;
    int width_;
    LinearLayout safetyRatingArea;
    SimpleRatingBar safetyRatingBar;
    MTextView tripRatingTitleTxt;
    MTextView safetyRatingTitleTxt;
    CustomHorizontalScrollView hScrollView;
    String ShowSafetyRating = "";
    LinearLayout lineArea;
    boolean isAnimated = false;
    LinearLayout rideRatingArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);


        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);


        helpTxt = (ImageView) findViewById(R.id.helpTxt);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        subTitleTxt = (MTextView) findViewById(R.id.subTitleTxt);
        receiptImgView = (ImageView) findViewById(R.id.receiptImgView);
        commentBox = (MaterialEditText) findViewById(R.id.commentBox);
        vReasonTitleTxt = (MTextView) findViewById(R.id.vReasonTitleTxt);
        viewReqServicesArea = (CardView) findViewById(R.id.viewReqServicesArea);
        receiptImgView.setOnClickListener(new setOnClickList());
        paymentMainArea = findViewById(R.id.paymentMainArea);
        View commentArea = findViewById(R.id.commentArea);
        tipPluseImage = (ImageView) findViewById(R.id.tipPluseImage);
        commentBox.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        commentBox.setSingleLine(false);
        commentBox.setHideUnderline(true);
        commentBox.setGravity(GravityCompat.START | Gravity.TOP);
        commentBox.setLines(5);

        commentBox.setBothText("", generalFunc.retrieveLangLBl("", "LBL_WRITE_COMMENT_HINT_TXT"));

        new CreateRoundedView(Color.parseColor("#FFFFFF"), 0, Utils.dipToPixels(getActContext(), 1), Color.parseColor("#F2F2F2"), commentArea);

        backImgView = (ImageView) findViewById(R.id.backImgView);
        fareDetailDisplayArea = (LinearLayout) findViewById(R.id.fareDetailDisplayArea);
        afterServiceArea = (LinearLayout) findViewById(R.id.afterServiceArea);
        beforeServiceArea = (LinearLayout) findViewById(R.id.beforeServiceArea);
        cartypeTxt = (MTextView) findViewById(R.id.cartypeTxt);

        ufxratingDriverHTxt = (MTextView) findViewById(R.id.ufxratingDriverHTxt);
        ufxratingBar = (SimpleRatingBar) findViewById(R.id.ufxratingBar);


        tipHTxt = (MTextView) findViewById(R.id.tipHTxt);
        tipamtTxt = (MTextView) findViewById(R.id.tipamtTxt);
        tipmsgTxt = (MTextView) findViewById(R.id.tipmsgTxt);

        loading = (ProgressBar) findViewById(R.id.loading);
        errorView = (ErrorView) findViewById(R.id.errorView);
        container = (RelativeLayout) findViewById(R.id.container);

        tipArea = (LinearLayout) findViewById(R.id.tipArea);
        driverImageview = (SelectableRoundedImageView) findViewById(R.id.driverImgView);


        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        rateBtnId = Utils.generateViewId();
        btn_type2.setId(rateBtnId);

        btn_type2.setOnClickListener(new setOnClickList());
        viewReqServicesArea.setOnClickListener(new setOnClickList());


        getMemberBookings();



        /*
        if (getIntent().hasExtra("iTripId")) {
            iTripId = getIntent().getStringExtra("iTripId");

        } else {
            tripData = getIntent().getStringExtra("TripData");
            setLabels(tripData);
            setData(tripData);
        }*/

       /* setLabels();
        setData();*/


        commentBox.setTextColor(getResources().getColor(R.color.mdtp_transparent_black));

        backImgView.setOnClickListener(new setOnClickList());
        subTitleTxt.setOnClickListener(new setOnClickList());
        afterServiceArea.setOnClickListener(new setOnClickList());
        beforeServiceArea.setOnClickListener(new setOnClickList());
        helpTxt.setOnClickListener(new setOnClickList());
        commentBox.setOnTouchListener((v, event) -> {
            ((NestedScrollView) findViewById(R.id.scrollContainer)).requestDisallowInterceptTouchEvent(true);
            return false;
        });
    }


    private void getMemberBookings() {
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (container.getVisibility() == View.VISIBLE) {
            container.setVisibility(View.GONE);
            paymentMainArea.setVisibility(View.GONE);
        }
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }


        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getMemberBookings");
        parameters.put("memberId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);
        parameters.put("memberType", Utils.app_type);
        if (getIntent().hasExtra("iTripId")) {
            parameters.put("iTripId", getIntent().getExtras().getString("iTripId"));
        }
        if (getIntent().hasExtra("iCabBookingId")) {
            parameters.put("iCabBookingId", getIntent().getExtras().getString("iCabBookingId"));
        }


        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(responseString -> {
            JSONObject responseObj = generalFunc.getJsonObject(responseString);

            if (responseObj != null && !responseObj.equals("")) {
                closeLoader();

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseObj)) {

                    JSONArray arr_rides = generalFunc.getJsonArray(Utils.message_str, responseObj);


                    if (arr_rides != null && arr_rides.length() > 0) {
                        for (int i = 0; i < arr_rides.length(); i++) {
                            JSONObject obj_temp = generalFunc.getJsonObject(arr_rides, i);
                            HashMap<String, String> map = new HashMap<String, String>();

                            ShowSafetyRating = generalFunc.getJsonValueStr("ShowSafetyRating", obj_temp);
                            map.put("tTripRequestDateOrig", generalFunc.getJsonValueStr("tTripRequestDateOrig", obj_temp));
                            map.put("CurrencySymbol", generalFunc.getJsonValueStr("CurrencySymbol", obj_temp));
                            map.put("tSaddress", generalFunc.getJsonValueStr("tSaddress", obj_temp));
                            map.put("tDaddress", generalFunc.getJsonValueStr("tDaddress", obj_temp));
                            map.put("vRideNo", generalFunc.getJsonValueStr("vRideNo", obj_temp));
                            map.put("eFly", generalFunc.getJsonValueStr("eFly", obj_temp));

                            map.put("is_rating", generalFunc.getJsonValueStr("is_rating", obj_temp));
                            map.put("iTripId", generalFunc.getJsonValueStr("iTripId", obj_temp));
                            senderImage = generalFunc.getJsonValueStr("vSignImage", obj_temp);

                            if (Utils.checkText(senderImage)) {
                                findViewById(R.id.signArea).setVisibility(View.VISIBLE);
                                findViewById(R.id.signArea).setOnClickListener(new setOnClickList());
                            }

                            if (generalFunc.getJsonValueStr("eType", obj_temp).equalsIgnoreCase("deliver") || generalFunc.getJsonValue("eType", obj_temp).equals(Utils.eType_Multi_Delivery)) {
                                map.put("eType", generalFunc.retrieveLangLBl("Delivery", "LBL_DELIVERY"));
                                map.put("LBL_PICK_UP_LOCATION", generalFunc.retrieveLangLBl("Sender Location", "LBL_SENDER_LOCATION"));
                                map.put("LBL_DEST_LOCATION", generalFunc.retrieveLangLBl("Receiver's Location", "LBL_RECEIVER_LOCATION"));
                            } else {
                                map.put("LBL_PICK_UP_LOCATION", generalFunc.retrieveLangLBl("", "LBL_PICK_UP_LOCATION"));
                                map.put("eType", generalFunc.getJsonValueStr("eType", obj_temp));
                                map.put("LBL_DEST_LOCATION", generalFunc.retrieveLangLBl("", "LBL_DEST_LOCATION"));
                            }
                            map.put("eFareType", generalFunc.getJsonValueStr("eFareType", obj_temp));
                            map.put("appType", generalFunc.getJsonValue("APP_TYPE", userProfileJson));

                            if (generalFunc.getJsonValueStr("eCancelled", obj_temp).equals("Yes")) {
                                map.put("iActive", generalFunc.retrieveLangLBl("", "LBL_CANCELED_TXT"));
                            } else {
                                if (generalFunc.getJsonValueStr("iActive", obj_temp).equals("Canceled")) {
                                    map.put("iActive", generalFunc.retrieveLangLBl("", "LBL_CANCELED_TXT"));
                                } else if (generalFunc.getJsonValueStr("iActive", obj_temp).equals("Finished")) {
                                    map.put("iActive", generalFunc.retrieveLangLBl("", "LBL_FINISHED_TXT"));
                                } else {
                                    map.put("iActive", generalFunc.getJsonValueStr("iActive", obj_temp));
                                }
                            }

                            if (generalFunc.retrieveValue(Utils.APP_DESTINATION_MODE).equalsIgnoreCase(Utils.NONE_DESTINATION)) {
                                map.put("DESTINATION", "No");
                            } else {
                                map.put("DESTINATION", "Yes");
                            }


                            map.put("JSON", obj_temp.toString());
                            map.put("JSON", obj_temp.toString());
                            map.put("APP_TYPE", generalFunc.getJsonValue("APP_TYPE", userProfileJson));

                            if (generalFunc.getJsonValueStr("eType", obj_temp).equals(Utils.CabGeneralType_UberX) &&
                                    !generalFunc.getJsonValueStr("eFareType", obj_temp).equalsIgnoreCase(Utils.CabFaretypeRegular)) {

                                map.put("SelectedVehicle", generalFunc.getJsonValueStr("carTypeName", obj_temp));
                                map.put("SelectedCategory", generalFunc.getJsonValueStr("vVehicleCategory", obj_temp));


                            }
                            map.put("moreServices", generalFunc.getJsonValueStr("moreServices", obj_temp));
                            if (generalFunc.getJsonValueStr("eFareType", obj_temp).equalsIgnoreCase(Utils.CabFaretypeFixed) && generalFunc.getJsonValueStr("moreServices", obj_temp).equalsIgnoreCase("No")) {
                                map.put("SelectedCategory", generalFunc.getJsonValueStr("vCategory", obj_temp));

                            }

                            map.put("LBL_BOOKING_NO", generalFunc.retrieveLangLBl("Delivery No", "LBL_DELIVERY_NO"));
                            map.put("LBL_CANCEL_BOOKING", generalFunc.retrieveLangLBl("Cancel Delivery", "LBL_CANCEL_DELIVERY"));
                            map.put("LBL_BOOKING_NO", generalFunc.retrieveLangLBl("", "LBL_BOOKING"));
                            map.put("LBL_Status", generalFunc.retrieveLangLBl("", "LBL_Status"));
                            map.put("LBL_JOB_LOCATION_TXT", generalFunc.retrieveLangLBl("", "LBL_JOB_LOCATION_TXT"));

                            String paymentDoneByDetail = generalFunc.getJsonValueStr("PaymentPerson", obj_temp);

                            if (Utils.checkText(paymentDoneByDetail)) {
                                ((MTextView) findViewById(R.id.recipientTxt)).setVisibility(View.VISIBLE);
                                ((MTextView) findViewById(R.id.recipientTxt)).setText(" " + generalFunc.retrieveLangLBl("Paid By", "LBL_PAID_BY_TXT") + " " + paymentDoneByDetail);
                            }


                            if (map != null) {
                                tripData = map.get("JSON");
                                setLabels(tripData);
                                setData(tripData);
                            }

                            if (ShowSafetyRating.equalsIgnoreCase("Yes")) {
                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                width = displayMetrics.widthPixels;
                                width_D = (int) (width * 0.349);
                                width_ = (int) (width * 0.397);
                                safetyRatingBar = findViewById(R.id.safetyRatingBar);
                                tripRatingTitleTxt = findViewById(R.id.tripRatingTitleTxt);
                                safetyRatingTitleTxt = findViewById(R.id.safetyRatingTitleTxt);
                                safetyRatingTitleTxt.setText(generalFunc.retrieveLangLBl("Safety Rating", "LBL_SAFETY_RATING_TXT"));
                                tripRatingTitleTxt.setText(generalFunc.retrieveLangLBl("Trip Rating", "LBL_TRIP_RATING_TXT"));
                                safetyRatingTitleTxt.setVisibility(View.VISIBLE);
                                tripRatingTitleTxt.setVisibility(View.VISIBLE);

                                hScrollView = (CustomHorizontalScrollView) findViewById(R.id.hScrollView);
                                rideRatingArea = (LinearLayout) findViewById(R.id.rideRatingArea);
                                lineArea = (LinearLayout) findViewById(R.id.lineArea);
                                safetyRatingArea = (LinearLayout) findViewById(R.id.ratingArea);
                                lineArea.setOnTouchListener(new SetOnTouchList());

                                hScrollView.setOnTouchListener(new SetOnTouchList());
                                hScrollView.setOnClickListener(new setOnClickList());

                                ufxratingBar.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
                                    @Override
                                    public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {

                                        if (safetyRatingArea.getVisibility() == View.GONE) {
                                            showSafetyRatingArea(true);

                                        }
                                    }
                                });
                                hScrollView.setOnTouchListener(new OnSwipeTouchListener(getActContext()) {
                                    public void onSwipeTop() {

                                    }

                                    public void onSwipeRight() {
                                        scrollWithanimation();
                                    }

                                    public void onSwipeLeft() {
                                        scrollWithanimation();
                                    }

                                    public void onSwipeBottom() {

                                    }

                                });
                            }

                        }
                    }


                } else {
                    generateErrorView();
                }
            } else {
                generateErrorView();
            }
        });
        exeWebServer.execute();
    }

    private void showSafetyRatingArea(boolean show) {
        if (show) {

            if (generalFunc.isRTLmode()) {
                SlideAnimationUtil.slideInFromLeft(getActContext(), safetyRatingArea);
            } else {
                SlideAnimationUtil.slideOutToRight(getActContext(), safetyRatingArea);
            }

            if (!isAnimated) {
                hScrollView.setScrollingEnabled(true);

                ufxratingBar.animate().translationXBy(generalFunc.isRTLmode() ? width_ : -width_);
                rideRatingArea.animate().translationXBy(generalFunc.isRTLmode() ? width_ : -width_);

                ufxratingBar.setIndicator(true);
                ufxratingBar.setFocusable(false);

                safetyRatingArea.setVisibility(View.VISIBLE);
                safetyRatingArea.animate().translationXBy(generalFunc.isRTLmode() ? width_D : -width_D);

                isAnimated = true;

                hScrollView.setScrollingEnabled(false);
            }

        } else {
//

            if (generalFunc.isRTLmode()) {
                SlideAnimationUtil.slideInFromRight(getActContext(), ufxratingBar);
                SlideAnimationUtil.slideInFromRight(getActContext(), rideRatingArea);
            } else {
                SlideAnimationUtil.slideOutToLeft(getActContext(), ufxratingBar);
                SlideAnimationUtil.slideOutToLeft(getActContext(), rideRatingArea);
            }

            if (isAnimated) {
                hScrollView.setScrollingEnabled(true);

                ufxratingBar.animate().translationXBy(generalFunc.isRTLmode() ? -width_ : width_);
                rideRatingArea.animate().translationXBy(generalFunc.isRTLmode() ? -width_ : width_);

                ufxratingBar.setIndicator(false);
                ufxratingBar.setFocusable(true);

                safetyRatingArea.animate().translationXBy(generalFunc.isRTLmode() ? -width_D : width_D);
                safetyRatingArea.setVisibility(View.GONE);

                isAnimated = false;

                hScrollView.setScrollingEnabled(false);
            }


        }
        lineArea.setOnClickListener(show ? new setOnClickList() : null);
    }

    private void scrollWithanimation() {
        if (!ufxratingBar.isFocusable()) {
            lineArea.performClick();
        } else {
            showSafetyRatingArea(true);
        }
    }

    public void closeLoader() {
        if (loading.getVisibility() == View.VISIBLE) {
            loading.setVisibility(View.GONE);
        }

        if (container.getVisibility() == View.GONE) {
            container.setVisibility(View.VISIBLE);
            paymentMainArea.setVisibility(View.VISIBLE);
        }
    }

    public void generateErrorView() {

        closeLoader();

        generalFunc.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT");

        if (errorView.getVisibility() != View.VISIBLE) {
            errorView.setVisibility(View.VISIBLE);
        }

        if (container.getVisibility() == View.VISIBLE) {
            container.setVisibility(View.GONE);
            paymentMainArea.setVisibility(View.GONE);
        }

        errorView.setOnRetryListener(() -> getMemberBookings());
    }


    String headerLable = "", noVal = "", driverhVal = "";

    public void setLabels(String tripData) {
        String eType = generalFunc.getJsonValue("eType", tripData);
        /*Multi related new lable*/
        ((MTextView) findViewById(R.id.viewSingleDeliveryTitleTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_DETAILS"));

        ((MTextView) findViewById(R.id.passengerSignTxt)).setText(generalFunc.retrieveLangLBl("View Signature", "LBL_VIEW_MULTI_SENDER_SIGN"));
        ((MTextView) findViewById(R.id.viewDeliveryDetails)).setText(generalFunc.retrieveLangLBl("View Delivery Details", "LBL_VIEW_DELIVERY_DETAILS"));
        titleTxt.setText(generalFunc.retrieveLangLBl("RECEIPT", "LBL_RECEIPT_HEADER_TXT"));
        subTitleTxt.setText(generalFunc.retrieveLangLBl("GET RECEIPT", "LBL_GET_RECEIPT_TXT"));
        ((MTextView) findViewById(R.id.viewReqServicesTxtView)).setText(generalFunc.retrieveLangLBl("", "LBL_VIEW_REQUESTED_SERVICES"));

        boolean eFly = generalFunc.getJsonValue("eFly", tripData).equalsIgnoreCase("Yes");

        if (eType.equals(Utils.CabGeneralType_UberX)) {
//            headerLable = generalFunc.retrieveLangLBl("", "LBL_THANKS_JOB_TXT");
            headerLable = generalFunc.retrieveLangLBl("", "LBL_THANKS_TXT");
            noVal = generalFunc.retrieveLangLBl("", "LBL_SERVICES");
            driverhVal = generalFunc.retrieveLangLBl("", "LBL_SERVICE_PROVIDER_TXT");
        } else if (eType.equals("Deliver") || eType.equals(Utils.eType_Multi_Delivery)) {
//            headerLable = generalFunc.retrieveLangLBl("", "LBL_THANKS_DELIVERY_TXT");
            headerLable = generalFunc.retrieveLangLBl("", "LBL_THANKS_TXT");
            noVal = generalFunc.retrieveLangLBl("", "LBL_DELIVERY");
            driverhVal = generalFunc.retrieveLangLBl("", "LBL_CARRIER");
        } else {
//            headerLable = generalFunc.retrieveLangLBl("", "LBL_THANKS_RIDING_TXT");
            headerLable = generalFunc.retrieveLangLBl("", "LBL_THANKS_TXT");
            noVal = generalFunc.retrieveLangLBl("", eFly ? "LBL_HEADER_RDU_FLY_RIDE" : "LBL_RIDE");
            driverhVal = generalFunc.retrieveLangLBl("", "LBL_DRIVER");
        }

        ((MTextView) findViewById(R.id.headerTxt)).setText(generalFunc.retrieveLangLBl("", headerLable));


        ((MTextView) findViewById(R.id.rideNoHTxt)).setText(noVal);
//        ((MTextView) findViewById(R.id.ratingHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_RATING"));
        String dateLable = "";
        String pickupHval = "";

        if (eType.equals(Utils.CabGeneralType_UberX)) {
            dateLable = generalFunc.retrieveLangLBl("", "LBL_JOB_REQ_DATE");
            pickupHval = generalFunc.retrieveLangLBl("", "LBL_JOB_LOCATION_TXT");
        } else if (eType.equals("Deliver") || eType.equals(Utils.eType_Multi_Delivery)) {
            dateLable = generalFunc.retrieveLangLBl("", "LBL_DELIVERY_REQUEST_DATE");
            pickupHval = generalFunc.retrieveLangLBl("", "LBL_SENDER_LOCATION");
        } else {
            dateLable = generalFunc.retrieveLangLBl("", "LBL_TRIP_REQUEST_DATE_TXT");
            pickupHval = generalFunc.retrieveLangLBl("", "LBL_PICKUP_LOCATION_TXT");
        }

        ((MTextView) findViewById(R.id.pickUpHTxt)).setText(pickupHval);
        ((MTextView) findViewById(R.id.pickUpAddressHTxt)).setText(pickupHval);

        if (eType.equals("Deliver") || eType.equals(Utils.eType_Multi_Delivery)) {
            ((MTextView) findViewById(R.id.dropOffHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_DETAILS_TXT"));
        } else if (eType.equals(Utils.CabGeneralType_Ride)) {
            ((MTextView) findViewById(R.id.dropOffHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_DEST_LOCATION"));
        } else {
            ((MTextView) findViewById(R.id.dropOffHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_DEST_LOCATION"));

        }

        ((MTextView) findViewById(R.id.chargesHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_CHARGES_TXT"));
        btn_type2.setText(generalFunc.retrieveLangLBl("Rate", "LBL_RATE_DRIVER_TXT"));

        if (eType.equals(Utils.eType_Multi_Delivery)) {
            ufxratingDriverHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RATE_HEADING_CARRIER"));
        } else {
            ufxratingDriverHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RATE_HEADING_DRIVER_TXT"));
        }

        tipHTxt.setText(generalFunc.retrieveLangLBl("Tip Amount", "LBL_TIP_AMOUNT"));
        tipmsgTxt.setText(generalFunc.retrieveLangLBl("Thank you for giving tip for this trip.", "LBL_TIP_INFO_SHOW_RIDER"));


    }

    public void setData(String tripData) {
        String eType = generalFunc.getJsonValue("eType", tripData);
        if (generalFunc.getJsonValue("vReasonTitle", tripData) != null && !generalFunc.getJsonValue("vReasonTitle", tripData).equalsIgnoreCase("")) {
            vReasonTitleTxt.setVisibility(View.VISIBLE);
            vReasonTitleTxt.setText(generalFunc.getJsonValue("vReasonTitle", tripData));
        }

        String vImage = generalFunc.getJsonValue("vImage", tripData);
        if (vImage == null || vImage.equals("") || vImage.equals("NONE")) {
            (driverImageview).setImageResource(R.mipmap.ic_no_pic_user);
        } else {
            Picasso.get()
                    .load(vImage)
                    .placeholder(R.mipmap.ic_no_pic_user)
                    .error(R.mipmap.ic_no_pic_user)
                    .into(driverImageview);
        }


        if (generalFunc.retrieveValue(Utils.ENABLE_FAVORITE_DRIVER_MODULE_KEY).equalsIgnoreCase("Yes")) {

            /*Fav Driver Feature Start*/

            favSwitch = (SwitchButton) findViewById(R.id.favSwitch);
            favArea = (LinearLayout) findViewById(R.id.favArea);
            favArea.setVisibility(View.VISIBLE);
            favDriverTitleTxt = (MTextView) findViewById(R.id.favDriverTitleTxt);
            if (eType.equals("Deliver") || eType.equals(Utils.eType_Multi_Delivery)) {
                favDriverTitleTxt.setText(generalFunc.retrieveLangLBl("Save as Favourite Carrier?", "LBL_SAVE_FAVOUTITE_CARRIER"));
            } else {
                favDriverTitleTxt.setText(generalFunc.retrieveLangLBl("save as Favorite Driver", "LBL_SAVE_AS_FAV_DRIVER"));
            }

            favSwitch.setOnCheckedChangeListener((compoundButton, b) -> {

                if (b == true) {
                    favSwitch.setThumbColorRes(R.color.Green);
                } else {
                    favSwitch.setThumbColorRes(android.R.color.holo_red_dark);
                }


            });

            String eFavDriver = generalFunc.getJsonValue("eFavDriver", tripData);
            favSwitch.setChecked(eFavDriver.equalsIgnoreCase("Yes"));

            /*Fav Driver Feature End*/

        }


        ((MTextView) findViewById(R.id.rideNoVTxt)).setText("#" + generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("vRideNo", tripData)));
        if (generalFunc.getJsonValue("eChargeViewShow", tripData) != null && generalFunc.getJsonValue("eChargeViewShow", tripData).equalsIgnoreCase("No")) {
            if (!generalFunc.getJsonValue("iActive", tripData).equalsIgnoreCase("Canceled")) {
                findViewById(R.id.headerTxt).setVisibility(View.GONE);
            }
            findViewById(R.id.chargeArea).setVisibility(View.GONE);
            findViewById(R.id.paymentarea).setVisibility(View.GONE);
            findViewById(R.id.helpTxt).setVisibility(View.GONE);
            ((MTextView) findViewById(R.id.rideNoVTxt)).setText("#" + generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("vRideNo", tripData)));
            LinearLayout.LayoutParams txtParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            txtParam.setMargins(2, 10, 2, 0);
            ((MTextView) findViewById(R.id.rideNoVTxt)).setLayoutParams(txtParam);
            ((MTextView) findViewById(R.id.rideNoHTxt)).setLayoutParams(txtParam);

        }


        ((MTextView) findViewById(R.id.nameDriverVTxt)).setText(generalFunc.getJsonValue("vName", generalFunc.getJsonValue("DriverDetails", tripData)) + " " +
                generalFunc.getJsonValue("vLastName", generalFunc.getJsonValue("DriverDetails", tripData)) + " (" + driverhVal + ")");
        ((MTextView) findViewById(R.id.tripdateVTxt)).setText(generalFunc.convertNumberWithRTL(generalFunc.getDateFormatedType(generalFunc.getJsonValue("dBooking_dateOrig", tripData), Utils.OriginalDateFormate, Utils.getDetailDateFormat(getActContext()))));

        ((MTextView) findViewById(R.id.pickUpVTxt)).setText(generalFunc.getJsonValue("tSaddress", tripData));
        ((MTextView) findViewById(R.id.pickUpAddressVTxt)).setText(generalFunc.getJsonValue("tSaddress", tripData));

        if (eType.equals("Deliver")) {

            (findViewById(R.id.viewDeliveryDetailsArea)).setVisibility(View.VISIBLE);
            (findViewById(R.id.addressArea)).setVisibility(View.GONE);
            (findViewById(R.id.sourceLocCardArea)).setVisibility(View.VISIBLE);
            ((MTextView) findViewById(R.id.viewSingleDeliveryTitleTxt)).setVisibility(View.VISIBLE);
            ((MTextView) findViewById(R.id.viewSingleDeliveryDetails)).setVisibility(View.VISIBLE);
            ((MTextView) findViewById(R.id.viewDeliveryDetails)).setVisibility(View.GONE);
            ((MTextView) findViewById(R.id.viewSingleDeliveryDetails)).setText(generalFunc.retrieveLangLBl("", "LBL_RECEIVER_NAME") + ": " + generalFunc.getJsonValue("vReceiverName", tripData) + "\n\n" +
                    generalFunc.retrieveLangLBl("", "LBL_RECEIVER_LOCATION") + ": " + generalFunc.getJsonValue("tDaddress", tripData) + "\n\n" +
                    generalFunc.retrieveLangLBl("", "LBL_PACKAGE_TYPE_TXT") + ": " + generalFunc.getJsonValue("PackageType", tripData) + "\n\n" +
                    generalFunc.retrieveLangLBl("", "LBL_PACKAGE_DETAILS") + ": " + generalFunc.getJsonValue("tPackageDetails", tripData)
            );
        } else {
            ((MTextView) findViewById(R.id.dropOffVTxt)).setText(generalFunc.getJsonValue("tDaddress", tripData));
        }

        if (!generalFunc.getJsonValue("tDaddress", tripData).equals("")) {
            ((MTextView) findViewById(R.id.dropOffVTxt)).setText(generalFunc.getJsonValue("tDaddress", tripData));
            (findViewById(R.id.destarea)).setVisibility(View.VISIBLE);
            (findViewById(R.id.aboveLine)).setVisibility(View.VISIBLE);
        } else {
            (findViewById(R.id.addressArea)).setVisibility(View.GONE);
            (findViewById(R.id.sourceLocCardArea)).setVisibility(View.VISIBLE);
        }


        if (!generalFunc.getJsonValue("fTipPrice", tripData).equals("0") && !generalFunc.getJsonValue("fTipPrice", tripData).equals("0.0") &&
                !generalFunc.getJsonValue("fTipPrice", tripData).equals("0.00") &&
                !generalFunc.getJsonValue("fTipPrice", tripData).equals("")) {
            tipArea.setVisibility(View.VISIBLE);
            tipamtTxt.setText(generalFunc.getJsonValue("fTipPrice", tripData));

        } else {
            tipArea.setVisibility(View.GONE);
        }

        cartypeTxt.setText(generalFunc.getJsonValue("vServiceDetailTitle", tripData));

        String trip_status_str = generalFunc.getJsonValue("iActive", tripData);

        isRatingDone = generalFunc.getJsonValue("is_rating", tripData);

        if (isRatingDone.equalsIgnoreCase("No") && trip_status_str.contains("Finished")) {
            findViewById(R.id.rateDriverArea).setVisibility(View.VISIBLE);
            findViewById(R.id.rateCardDriverArea).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.rateDriverArea).setVisibility(View.GONE);
            findViewById(R.id.rateCardDriverArea).setVisibility(View.GONE);
        }

        if (trip_status_str.contains("Canceled")) {

            String cancelLable = "";
            String cancelableReason = generalFunc.getJsonValue("vCancelReason", tripData);

            if (generalFunc.getJsonValue("eCancelledBy", tripData).equalsIgnoreCase("DRIVER")) {

                if (eType.equals("Deliver") || eType.equals(Utils.eType_Multi_Delivery)) {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_DELIVERY_CANCEL_DRIVER_TXT");
                } else if (eType.equals(Utils.CabGeneralType_UberX)) {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_JOB_CANCEL_PROVIDER_TXT");
                } else {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER_TXT");
                }

            } else {

                if (eType.equals(Utils.CabGeneralType_UberX)) {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_JOB");
                } else if (eType.equals("Deliver") || eType.equals(Utils.eType_Multi_Delivery)) {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_DELIVERY_TXT");
                } else {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_TRIP_TXT");
                }
            }


            (findViewById(R.id.cancelReasonArea)).setVisibility(View.VISIBLE);
            (findViewById(R.id.tripStatusArea)).setVisibility(View.GONE);
            ((MTextView) findViewById(R.id.vReasonHTxt)).setText(cancelLable);
            ((MTextView) findViewById(R.id.vReasonVTxt)).setText(cancelableReason);

            if (!generalFunc.getJsonValue("tDaddress", tripData).equals("")) {
                (findViewById(R.id.destarea)).setVisibility(View.VISIBLE);
                (findViewById(R.id.aboveLine)).setVisibility(View.VISIBLE);
            }

        } else if (trip_status_str.contains("Finished")) {

            String finishLable = "";
            if (eType.equals(Utils.CabGeneralType_UberX)) {
                finishLable = generalFunc.retrieveLangLBl("", "LBL_FINISHED_JOB_TXT");
            } else if (eType.equals("Deliver") || eType.equals(Utils.eType_Multi_Delivery)) {
                finishLable = generalFunc.retrieveLangLBl("", "LBL_FINISHED_DELIVERY_TXT");
            } else {
                finishLable = generalFunc.retrieveLangLBl("", "LBL_FINISHED_TRIP_TXT");
            }

            ((MTextView) findViewById(R.id.tripStatusTxt)).setText(generalFunc.retrieveLangLBl("", finishLable));

            if (!generalFunc.getJsonValue("tDaddress", tripData).equals("")) {
                (findViewById(R.id.destarea)).setVisibility(View.VISIBLE);
                (findViewById(R.id.aboveLine)).setVisibility(View.VISIBLE);
            }

            // subTitleTxt.setVisibility(View.VISIBLE);
            receiptImgView.setVisibility(View.VISIBLE);
        } else {
            ((MTextView) findViewById(R.id.tripStatusTxt)).setText(trip_status_str);

        }

        if (generalFunc.getJsonValue("vTripPaymentMode", tripData).equals("Cash")) {
            ((ImageView) findViewById(R.id.paymentTypeImgeView)).setImageResource(R.drawable.ic_cash_payment);
            ((MTextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_CASH_PAYMENT_TXT"));
        } else {
            ((MTextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("Card Payment", "LBL_CARD_PAYMENT"));
            ((ImageView) findViewById(R.id.paymentTypeImgeView)).setImageResource(R.mipmap.ic_card_new);

        }

        if (generalFunc.getJsonValue("ePayWallet", tripData).equals("Yes")) {
            ((MTextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("Paid By Wallet", "LBL_PAID_VIA_WALLET"));
            ((ImageView) findViewById(R.id.paymentTypeImgeView)).setImageResource(R.mipmap.ic_menu_wallet);
        }


        if (generalFunc.getJsonValue("vTripPaymentMode", tripData).equalsIgnoreCase("Organization")) {
            ((MTextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_PAYMENT_BY_TXT") + " " + generalFunc.getJsonValue("OrganizationName", tripData));
            ((ImageView) findViewById(R.id.paymentTypeImgeView)).setImageResource(R.drawable.ic_business_pay);
            ((ImageView) findViewById(R.id.paymentTypeImgeView)).setColorFilter(getResources().getColor(R.color.appThemeColor_1), PorterDuff.Mode.SRC_IN);
        }

        if (generalFunc.getJsonValue("eCancelled", tripData).equals("Yes")) {

            String cancelledLable = "";
            String cancelableReason = generalFunc.getJsonValue("vCancelReason", tripData);

            if (generalFunc.getJsonValue("eCancelledBy", tripData).equalsIgnoreCase("DRIVER")) {

                if (eType.equals(Utils.CabGeneralType_UberX)) {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_JOB_CANCEL_PROVIDER_TXT") + " " + cancelableReason;
                } else if (eType.equals("Deliver") || eType.equals(Utils.eType_Multi_Delivery)) {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_DELIVERY_CANCEL_DRIVER_TXT") + " " + cancelableReason;
                } else {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER_TXT") + " " + cancelableReason;
                }

            } else {

                if (eType.equals(Utils.CabGeneralType_UberX)) {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_JOB");
                } else if (eType.equals("Deliver") || eType.equals(Utils.eType_Multi_Delivery)) {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_DELIVERY_TXT");
                } else {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_TRIP_TXT");
                }
            }

            ((MTextView) findViewById(R.id.tripStatusTxt)).setText(cancelledLable);
        }

        ((SimpleRatingBar) findViewById(ratingBar)).setRating(GeneralFunctions.parseFloatValue(0, generalFunc.getJsonValue("TripRating", tripData)));


        if (eType.equalsIgnoreCase("UberX") && generalFunc.getJsonValue("SERVICE_PROVIDER_FLOW", userProfileJson).equalsIgnoreCase("Provider")) {
            viewReqServicesArea.setVisibility(View.VISIBLE);
        }

        if (eType.equalsIgnoreCase("UberX") || generalFunc.getJsonValue("eFareType", tripData).equalsIgnoreCase("Fixed")) {
            findViewById(R.id.service_area).setVisibility(View.GONE);
            findViewById(R.id.serviceHTxt).setVisibility(View.GONE);
            findViewById(R.id.photoArea).setVisibility(View.VISIBLE);

            ((MTextView) findViewById(R.id.beforeImgHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_BEFORE_SERVICE"));
            ((MTextView) findViewById(R.id.afterImgHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_AFTER_SERVICE"));

            if (!TextUtils.isEmpty(generalFunc.getJsonValue("vBeforeImage", tripData))) {
                findViewById(R.id.beforeServiceArea).setVisibility(View.VISIBLE);
                before_serviceImg_url = generalFunc.getJsonValue("vBeforeImage", tripData);

                String vBeforeImage = Utils.getResizeImgURL(getActContext(), before_serviceImg_url, getResources().getDimensionPixelSize(R.dimen.before_after_img_size), getResources().getDimensionPixelSize(R.dimen.before_after_img_size));

                displayPic(vBeforeImage, (ImageView) findViewById(R.id.iv_before_img), "before");

                findViewById(R.id.iv_before_img).setOnClickListener(v -> (new StartActProcess(getActContext())).openURL(before_serviceImg_url));
            } else {
                findViewById(R.id.beforeServiceArea).setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(generalFunc.getJsonValue("vAfterImage", tripData))) {
                findViewById(R.id.afterServiceArea).setVisibility(View.VISIBLE);
                after_serviceImg_url = generalFunc.getJsonValue("vAfterImage", tripData);

                String vAfterImage = Utils.getResizeImgURL(getActContext(), after_serviceImg_url, getResources().getDimensionPixelSize(R.dimen.before_after_img_size), getResources().getDimensionPixelSize(R.dimen.before_after_img_size));
                displayPic(vAfterImage, (ImageView) findViewById(R.id.iv_after_img), "after");

                findViewById(R.id.iv_after_img).setOnClickListener(v -> (new StartActProcess(getActContext())).openURL(after_serviceImg_url));
            } else {
                findViewById(R.id.afterServiceArea).setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(generalFunc.getJsonValue("vBeforeImage", tripData)) && TextUtils.isEmpty(generalFunc.getJsonValue("vAfterImage", tripData))) {

                findViewById(R.id.photoArea).setVisibility(View.GONE);

            }
            ((MTextView) findViewById(R.id.pickUpVTxt)).setText(generalFunc.getJsonValue("tSaddress", tripData));

        } else {
            if (!generalFunc.getJsonValue("tDaddress", tripData).equals("")) {
                (findViewById(R.id.destarea)).setVisibility(View.VISIBLE);
                (findViewById(R.id.aboveLine)).setVisibility(View.VISIBLE);
            }
            findViewById(R.id.service_area).setVisibility(View.GONE);
            findViewById(R.id.serviceHTxt).setVisibility(View.GONE);
            findViewById(R.id.photoArea).setVisibility(View.GONE);
        }

        /*Show Multi Delivery Details*/
        if (eType.equals(Utils.eType_Multi_Delivery)) {

            (findViewById(R.id.addressArea)).setVisibility(View.GONE);
            (findViewById(R.id.sourceLocCardArea)).setVisibility(View.VISIBLE);
            (findViewById(R.id.viewDeliveryDetailsArea)).setVisibility(View.VISIBLE);
            (findViewById(R.id.viewDeliveryDetailsArea)).setOnClickListener(new setOnClickList());
        }

        boolean FareDetailsArrNew = generalFunc.isJSONkeyAvail("HistoryFareDetailsNewArr", tripData);

        JSONArray FareDetailsArrNewObj = null;
        if (FareDetailsArrNew == true) {
            FareDetailsArrNewObj = generalFunc.getJsonArray("HistoryFareDetailsNewArr", tripData);
        }
        if (FareDetailsArrNewObj != null) {
            addFareDetailLayout(FareDetailsArrNewObj);
        }
    }

    /*Start of Multi Delivery Data*/

    public void showSignatureImage(String Name, String image_url, boolean isSender) {
        signatureImageDialog = new Dialog(getActContext(), R.style.Theme_Dialog);
        signatureImageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        signatureImageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        signatureImageDialog.setContentView(R.layout.multi_show_sign_design);

        final ProgressBar LoadingProgressBar = ((ProgressBar) signatureImageDialog.findViewById(R.id.LoadingProgressBar));

        ((MTextView) signatureImageDialog.findViewById(R.id.nameTxt)).setText(" " + Name);

        if (isSender) {
            ((MTextView) signatureImageDialog.findViewById(R.id.passengerDTxt)).setText(generalFunc.retrieveLangLBl("Sender Signature", "LBL_SENDER_SIGN"));
            ((MTextView) signatureImageDialog.findViewById(R.id.nameTxt)).setVisibility(View.GONE);

        } else {
            ((MTextView) signatureImageDialog.findViewById(R.id.passengerDTxt)).setText(generalFunc.retrieveLangLBl("Receiver Signature", "LBL_RECEIVER_SIGN"));
            ((MTextView) signatureImageDialog.findViewById(R.id.nameTxt)).setVisibility(View.VISIBLE);

        }

        if (Utils.checkText(image_url)) {

            Picasso.get()
                    .load(image_url)
                    .into(((ImageView) signatureImageDialog.findViewById(R.id.passengerImgView)), new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            LoadingProgressBar.setVisibility(View.GONE);
                            ((ImageView) signatureImageDialog.findViewById(R.id.passengerImgView)).setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            LoadingProgressBar.setVisibility(View.VISIBLE);
                            ((ImageView) signatureImageDialog.findViewById(R.id.passengerImgView)).setVisibility(View.GONE);

                        }
                    });
        } else {
            LoadingProgressBar.setVisibility(View.VISIBLE);
            ((ImageView) signatureImageDialog.findViewById(R.id.passengerImgView)).setVisibility(View.GONE);

        }
        (signatureImageDialog.findViewById(R.id.cancelArea)).setOnClickListener(view -> {

            if (signatureImageDialog != null) {
                signatureImageDialog.dismiss();
            }
        });

        signatureImageDialog.setCancelable(false);
        signatureImageDialog.setCanceledOnTouchOutside(false);

        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(signatureImageDialog);
        }
        signatureImageDialog.show();

    }

    /*End of Multi Delivery Data*/

    public void displayPic(String image_url, ImageView view, final String imgType) {

        Picasso.get()
                .load(image_url)
                .placeholder(R.mipmap.ic_no_icon)
                .into(view, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        if (imgType.equalsIgnoreCase("before")) {
                            findViewById(R.id.before_loading).setVisibility(View.GONE);
                            findViewById(R.id.iv_before_img).setVisibility(View.VISIBLE);
                        } else if (imgType.equalsIgnoreCase("after")) {
                            findViewById(R.id.after_loading).setVisibility(View.GONE);
                            findViewById(R.id.iv_after_img).setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onError(Exception e) {
                        if (imgType.equalsIgnoreCase("before")) {
                            findViewById(R.id.before_loading).setVisibility(View.VISIBLE);
                            findViewById(R.id.iv_before_img).setVisibility(View.GONE);
                        } else if (imgType.equalsIgnoreCase("after")) {
                            findViewById(R.id.after_loading).setVisibility(View.VISIBLE);
                            findViewById(R.id.iv_after_img).setVisibility(View.GONE);

                        }
                    }
                });

    }

    private void addFareDetailLayout(JSONArray jobjArray) {

        if (fareDetailDisplayArea.getChildCount() > 0) {
            fareDetailDisplayArea.removeAllViewsInLayout();
        }

        for (int i = 0; i < jobjArray.length(); i++) {
            JSONObject jobject = generalFunc.getJsonObject(jobjArray, i);
            try {
                addFareDetailRow(jobject.names().getString(0), jobject.get(jobject.names().getString(0)).toString(), (jobjArray.length() - 1) == i ? true : false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void addFareDetailRow(String row_name, String row_value, boolean isLast) {
        View convertView = null;
        if (row_name.equalsIgnoreCase("eDisplaySeperator")) {
            convertView = new View(getActContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dipToPixels(getActContext(), 1));
            params.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen._5sdp));
            convertView.setBackgroundColor(Color.parseColor("#dedede"));
            convertView.setLayoutParams(params);
        } else {
            LayoutInflater infalInflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.design_fare_deatil_row, null);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, (int) getResources().getDimension(R.dimen._10sdp), 0, isLast ? (int) getResources().getDimension(R.dimen._10sdp) : 0);
            convertView.setLayoutParams(params);

            MTextView titleHTxt = (MTextView) convertView.findViewById(R.id.titleHTxt);
            MTextView titleVTxt = (MTextView) convertView.findViewById(R.id.titleVTxt);

            titleHTxt.setText(generalFunc.convertNumberWithRTL(row_name));
            titleVTxt.setText(generalFunc.convertNumberWithRTL(row_value));

            if (isLast) {
                // convertView.setMinimumHeight(Utils.dipToPixels(getActContext(), 40));

                // CALCULATE individual fare & show
                titleHTxt.setTextColor(getResources().getColor(R.color.black));
                titleHTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Poppins_SemiBold.ttf");
                titleHTxt.setTypeface(face);
                titleVTxt.setTypeface(face);
                titleVTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                titleVTxt.setTextColor(getResources().getColor(R.color.appThemeColor_1));

            }

            fareDetailDisplayArea.addView(convertView);
        }
    }


    public void sendReceipt() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getReceipt");
        parameters.put("UserType", Utils.app_type);
        parameters.put("iTripId", generalFunc.getJsonValue("iTripId", tripData));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail) {
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                } else {
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public Context getActContext() {
        return HistoryDetailActivity.this;
    }

    public void submitRating() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "submitRating");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("iGeneralUserId", generalFunc.getMemberId());
        parameters.put("tripID", generalFunc.getJsonValue("iTripId", tripData));
        parameters.put("rating", "" + ufxratingBar.getRating());
        parameters.put("message", Utils.getText(commentBox));
        parameters.put("UserType", Utils.app_type);
        if (favSwitch != null) {
            parameters.put("eFavDriver", favSwitch.isChecked() ? "Yes" : "No");
        }
        if (ShowSafetyRating.equalsIgnoreCase("Yes")) {
            parameters.put("safetyRating", "" + safetyRatingBar.getRating());
        }

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail) {

                    final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setCancelable(true);
                    generateAlert.setBtnClickList(btn_id -> {
                        generateAlert.closeAlertBox();

                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    });
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_SUCCESS_RATING_SUBMIT_TXT"));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    generateAlert.showAlertBox();
                    generateAlert.setCancelable(false);


                } else {
                    resetRatingData();
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    private void resetRatingData() {
        commentBox.setText("");
        ((RatingBar) findViewById(ratingBar)).setRating(0);
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("isRestart", false)) {
            MyApp.getInstance().restartWithGetDataApp();
        } else {
            super.onBackPressed();
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActContext());
            Bundle bn = new Bundle();

            switch (view.getId()) {
                case R.id.backImgView:
                    onBackPressed();
                    break;

                case R.id.receiptImgView:
                    sendReceipt();
                    break;

                case R.id.beforeServiceArea:
                    new StartActProcess(getActContext()).openURL(before_serviceImg_url);
                    break;

                case R.id.afterServiceArea:
                    new StartActProcess(getActContext()).openURL(after_serviceImg_url);
                    break;
                case R.id.viewReqServicesArea:
                    bn.putString("iTripId", generalFunc.getJsonValue("iTripId", tripData));
                    break;

                case R.id.viewDeliveryDetailsArea:
                    bn.putString("iTripId", generalFunc.getJsonValue("iTripId", tripData));
                    new StartActProcess(getActContext()).startActWithData(ViewMultiDeliveryDetailsActivity.class, bn);
                    break;
                case R.id.helpTxt:
                    bn.putString("iTripId", generalFunc.getJsonValue("iTripId", tripData));
                    new StartActProcess(getActContext()).startActWithData(Help_MainCategory.class, bn);
                    break;

                case R.id.signArea:
                    //new StartActProcess(getActContext()).startActWithData(UberXSelectServiceActivity.class, bundle);
                    showSignatureImage(generalFunc.getJsonValue("vName", tripData) + " " +
                            generalFunc.getJsonValue("vLastName", tripData), senderImage, true);
                    break;
                case R.id.lineArea:

                    showSafetyRatingArea(false);
                    break;
                case R.id.hScrollView:

                    if (!ufxratingBar.isFocusable()) {
                        lineArea.performClick();
                    }
                    break;

            }

            if (view.getId() == rateBtnId) {
                if (((SimpleRatingBar) findViewById(R.id.ufxratingBar)).getRating() <= 0.0) {
                    generalFunc.showMessage(generalFunc.getCurrentView(HistoryDetailActivity.this), generalFunc.retrieveLangLBl("", "LBL_ERROR_RATING_DIALOG_TXT"));
                    return;
                }
                if (safetyRatingArea != null && safetyRatingArea.getVisibility() == View.VISIBLE && safetyRatingBar.getRating() < 0.5) {
                    generalFunc.showMessage(generalFunc.getCurrentView(HistoryDetailActivity.this), generalFunc.retrieveLangLBl("", "LBL_ERROR_SAFETY_RATING_DIALOG_TXT"));
                    return;
                }
                submitRating();
            }
        }
    }
}
