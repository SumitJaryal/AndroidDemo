package com.ridetogo.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.adapter.files.OnGoingTripDetailAdapter;
import com.dialogs.OpenListView;
import com.fragments.CustomSupportMapFragment;
import com.general.files.AppFunctions;
import com.general.files.ConfigPubNub;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.SinchService;
import com.general.files.StartActProcess;
import com.general.files.UpdateDirections;
import com.general.files.UpdateFrequentTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sinch.android.rtc.calling.Call;
import com.squareup.picasso.Picasso;
import com.utils.AnimateMarker;
import com.utils.CommonUtilities;
import com.utils.Logger;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.ErrorView;
import com.view.GenerateAlertBox;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;
import com.view.editBox.MaterialEditText;
import com.view.simpleratingbar.SimpleRatingBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.utils.Utils.APP_TYPE;

/**
 * Created by Admin on 22-02-2017.
 */
public class OnGoingTripDetailsActivity extends BaseActivity implements OnMapReadyCallback, UpdateFrequentTask.OnTaskRunCalled {


    ProgressBar loading_ongoing_trips_detail;
    RelativeLayout loadingArea;
    ErrorView errorView;
    RecyclerView onGoingTripsDetailListRecyclerView;
    ImageView backImgView;
    SelectableRoundedImageView user_img;
    MTextView userNameTxt, userAddressTxt, subTitleTxt, titleTxt;
    SimpleRatingBar ratingBar;
    OnGoingTripDetailAdapter onGoingTripDetailAdapter;
    ArrayList<HashMap<String, String>> list;
    //public HashMap<String, String> tripDetail = new HashMap<>();
    HashMap<String, String> tempMap;
    GeneralFunctions generalFunc;
    String server_time = "";
    String userProfileJson = "";
    JSONObject obj_userProfile;
    MTextView progressHinttext;
    String driverStatus = "";

    LatLng driverLocation;
    Marker driverMarker;
    int DRIVER_LOC_FETCH_TIME_INTERVAL;

    CustomSupportMapFragment map;
    boolean isTaskKilled = false;
    GoogleMap gMap;
    LinearLayout googlemaparea;
    boolean isarrived = false;
    boolean isarrivedpopup = false;
    boolean isstartpopup = false;

    MTextView timeTxt;
    boolean isDriverArrived = false;
    String eType = "";
    UpdateDirections updateDirections;
    Location destLoc;
    View marker_view = null;
    SelectableRoundedImageView providerImgView = null;
    AnimateMarker animDriverMarker;
    boolean ishowdialog = false;
    private LinearLayout contentArea;

    boolean isLiveTrack = false;

    float defaultMarkerAnimDuration = 1200;
    boolean isCancelTripWarning = true;
    AlertDialog dialog_declineOrder;

    String vName = "";
    String vImage = "";
    String vRating = "";
    int ADDITIONAL_CHARGES_CODE=12112;
    private boolean isVerifyCharges;
    public  String tripDetailJson;
    public HashMap<String, String> tripDetail = new HashMap<>();


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ongoing_trip_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        new AppFunctions(getApplicationContext()).setOverflowButtonColor(toolbar, getResources().getColor(R.color.white));

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        animDriverMarker = new AnimateMarker();
        map = (CustomSupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapV2);


        String PUBNUB_DISABLED = generalFunc.retrieveValue(Utils.PUBNUB_DISABLED_KEY);

        if (PUBNUB_DISABLED.equalsIgnoreCase("NO")) {
            defaultMarkerAnimDuration = 3000;
        }

        init();
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        obj_userProfile = generalFunc.getJsonObject(userProfileJson);

      //  setData();
       // tripDetail = (HashMap<String, String>) getIntent().getSerializableExtra("TripDetail");
        tripDetail = (HashMap<String, String>) getIntent().getSerializableExtra("TripDetail");
         tripDetailJson = generalFunc.getJsonValue("TripDetails", userProfileJson);

        String vChargesDetailDataAvailable=generalFunc.getJsonValue("vChargesDetailDataAvailable",tripDetailJson);
        getTripDeliveryLocations();
        isVerifyCharges=Utils.checkText(vChargesDetailDataAvailable)&&vChargesDetailDataAvailable.equalsIgnoreCase("Yes")?true:false;

        map.getMapAsync(this);
        if (!generalFunc.getJsonValue("eType",tripDetailJson).equalsIgnoreCase(Utils.eType_Multi_Delivery)) {
            progressHinttext.setText(generalFunc.retrieveLangLBl("JOB PROGRESS", "LBL_PROGRESS_HINT"));
        } else {
            progressHinttext.setText(generalFunc.retrieveLangLBl("Delivery Status", "LBL_DELIVERY_STATUS_TXT"));
        }

        //setLables();

        if (getIntent().hasExtra("showChargesScreen"))
        {
            redirectToDetailCharges("");
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
    }

    public void callUpdateDeirection(Location driverlocation) {
        if (destLoc == null) {
            return;

        }
        if (updateDirections == null) {
            updateDirections = new UpdateDirections(getActContext(), null, driverlocation, destLoc);
            updateDirections.scheduleDirectionUpdate();
        } else {
            updateDirections.changeUserLocation(driverlocation);
        }

    }


    public void setData() {
        if(tripDetail!=null) {

            if (tripDetail.get("eType")!=null && tripDetail.get("eType").equalsIgnoreCase(Utils.eType_Multi_Delivery)) {
                vName = generalFunc.getJsonValue("vName", tripDetailJson);
                vRating = generalFunc.getJsonValue("MemberRating", tripDetailJson);
                vImage = generalFunc.getJsonValue("vUserImage", tripDetailJson);
            } else {
                vName = generalFunc.getJsonValue("driverName", tripDetailJson);
                vRating = generalFunc.getJsonValue("driverRating", tripDetailJson);
                vImage = generalFunc.getJsonValue("driverImage", tripDetailJson);
            }
        }
        else
        {
            vName = generalFunc.getJsonValue("driverName", tripDetailJson);
            vRating = generalFunc.getJsonValue("driverRating", tripDetailJson);
            vImage = generalFunc.getJsonValue("driverImage", tripDetailJson);
        }


        setDriverDetail();



        setLables();

    }

    public void onResumeCalled() {


        //  subscribeToDriverLocChannel();
    }

    public void setTaskKilledValue(boolean isTaskKilled) {
        this.isTaskKilled = isTaskKilled;

        if (isTaskKilled == true) {
            onPauseCalled();
        }

    }

    public void onPauseCalled() {


        //  unSubscribeToDriverLocChannel();
    }

    public void subscribeToDriverLocChannel() {
        if (generalFunc.getJsonValue("eFareType",tripDetailJson).equals(Utils.CabFaretypeRegular) || !isarrived) {
            ArrayList<String> channelName = new ArrayList<>();
            channelName.add(Utils.pubNub_Update_Loc_Channel_Prefix +generalFunc.getJsonValue("iDriverId",tripDetailJson));
            ConfigPubNub.getInstance().subscribeToChannels(channelName);
        }
    }

    public void unSubscribeToDriverLocChannel() {

        ArrayList<String> channelName = new ArrayList<>();
        channelName.add(Utils.pubNub_Update_Loc_Channel_Prefix + generalFunc.getJsonValue("iDriverId",tripDetailJson));
        ConfigPubNub.getInstance().unSubscribeToChannels(channelName);


        if (updateDirections != null) {
            updateDirections.releaseTask();
            updateDirections = null;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        subscribeToDriverLocChannel();
        if (updateDirections != null) {
            updateDirections.scheduleDirectionUpdate();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();


        unSubscribeToDriverLocChannel();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {

            // perform your desired action here

            // return 'true' to prevent further propagation of the key event
            return true;
        }

        // let the system handle all other key events
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.my_ongoing_activity, menu);
        setLablesAsPerCurrentFrag(menu);

        return true;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {

        setLablesAsPerCurrentFrag(menu);

        super.onOptionsMenuClosed(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        setLablesAsPerCurrentFrag(menu);
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        setLablesAsPerCurrentFrag(menu);

        return super.onPrepareOptionsMenu(menu);
    }

    public void setLablesAsPerCurrentFrag(Menu menu) {
        if (menu != null) {
            menu.findItem(R.id.menu_view_tutor_detail).setTitle(generalFunc.retrieveLangLBl("Driver/Service Provider", "LBL_MYTRIP_DRIVER"));

            if (driverStatus == null) {
                if (driverStatus.isEmpty() && getIntent().hasExtra("driverStatus")) {
                    driverStatus = getIntent().getStringExtra("driverStatus");
                }
            }

            if (Utils.checkText(driverStatus) && !driverStatus.equals("On Going Trip") && !driverStatus.equals("finished") && !driverStatus.equals("NONE") && !driverStatus.equals("Cancelled") && !driverStatus.equals("Canceled")) {
                menu.findItem(R.id.menu_cancel_trip).setVisible(true);
                menu.findItem(R.id.menu_cancel_trip).setTitle(generalFunc.retrieveLangLBl("Cancel Job", "LBL_CANCEL_BOOKING"));
            } else {
                menu.findItem(R.id.menu_cancel_trip).setVisible(false);

            }

            if (generalFunc.getJsonValue("eType",tripDetailJson).equalsIgnoreCase(Utils.eType_Multi_Delivery)) {
                menu.findItem(R.id.menu_cancel_trip).setVisible(false);
            }

            if (generalFunc.getJsonValue("moreServices",tripDetailJson) != null && generalFunc.getJsonValue("moreServices",tripDetailJson).equalsIgnoreCase("Yes")) {
                menu.findItem(R.id.menu_service).setVisible(true);
                menu.findItem(R.id.menu_service).setTitle(generalFunc.retrieveLangLBl("", "LBL_TITLE_REQUESTED_SERVICES"));
            } else {
                menu.findItem(R.id.menu_service).setVisible(false);
            }

            if (isVerifyCharges) {
                menu.findItem(R.id.menu_service_charges).setVisible(true);
                menu.findItem(R.id.menu_service_charges).setTitle(generalFunc.retrieveLangLBl("", "LBL_VERIFY_ADDITIONAL_CHARGES_TXT"));
            } else {
                menu.findItem(R.id.menu_service_charges).setVisible(false);
            }


            if (driverStatus.equals("Arrived")) {
                isarrived = true;

            }

            if (driverStatus.equals("On Going Trip")) {
                isarrived = true;

            }

            if (generalFunc.getJsonValue("eType",tripDetailJson).equalsIgnoreCase(Utils.eType_Multi_Delivery)) {
                menu.findItem(R.id.menu_track).setVisible(false);
            }


            if (onGoingTripsDetailListRecyclerView.getVisibility() == View.VISIBLE) {

                if (generalFunc.getJsonValue("eServiceLocation",tripDetailJson).equalsIgnoreCase("Driver")) {

                    menu.findItem(R.id.menu_track).setTitle(generalFunc.retrieveLangLBl("Live Track", "LBL_NAVIGATE_TO_PROVIDER"));
                } else {
                    menu.findItem(R.id.menu_track).setTitle(generalFunc.retrieveLangLBl("Live Track", "LBL_LIVE_TRACK_TXT"));
                }
            } else {
                menu.findItem(R.id.menu_track).setTitle(generalFunc.retrieveLangLBl("JOB PROGRESS", "LBL_JOB_PROGRESS"));

            }


            menu.findItem(R.id.menu_call).setTitle(generalFunc.retrieveLangLBl("Call", "LBL_CALL_ACTIVE_TRIP"));
            menu.findItem(R.id.menu_message).setTitle(generalFunc.retrieveLangLBl("Message", "LBL_MESSAGE_ACTIVE_TRIP"));

            if (!generalFunc.getJsonValue("eFareType",tripDetailJson).equals(Utils.CabFaretypeRegular)) {
                if (isarrived) {
                    menu.findItem(R.id.menu_track).setTitle(generalFunc.retrieveLangLBl("JOB PROGRESS", "LBL_PROGRESS_HINT")).setVisible(false);
                    onGoingTripsDetailListRecyclerView.setVisibility(View.VISIBLE);
                    progressHinttext.setText(generalFunc.retrieveLangLBl("JOB PROGRESS", "LBL_PROGRESS_HINT"));
                    googlemaparea.setVisibility(View.GONE);
                    timeTxt.setVisibility(View.GONE);
                    invalidateOptionsMenu();
                    isLiveTrack = false;

                }
            }


            menu.findItem(R.id.menu_sos).setTitle(generalFunc.retrieveLangLBl("Emergency or SOS", "LBL_EMERGENCY_SOS_TXT"));

            Utils.setMenuTextColor(menu.findItem(R.id.menu_cancel_trip), getResources().getColor(R.color.black));
            Utils.setMenuTextColor(menu.findItem(R.id.menu_view_tutor_detail), getResources().getColor(R.color.black));
            Utils.setMenuTextColor(menu.findItem(R.id.menu_sos), getResources().getColor(R.color.black));
            Utils.setMenuTextColor(menu.findItem(R.id.menu_track), getResources().getColor(R.color.black));
            Utils.setMenuTextColor(menu.findItem(R.id.menu_call), getResources().getColor(R.color.black));
            Utils.setMenuTextColor(menu.findItem(R.id.menu_message), getResources().getColor(R.color.black));
            Utils.setMenuTextColor(menu.findItem(R.id.menu_service), getResources().getColor(R.color.black));
            Utils.setMenuTextColor(menu.findItem(R.id.menu_service_charges), getResources().getColor(R.color.black));

        }
    }

    public void getDeclineReasonsList() {
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("type", "GetCancelReasons");
        parameters.put("iTripId",getIntent().getStringExtra("iTripId"));
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("eUserType", Utils.app_type);

        ExecuteWebServerUrl exeServerTask = new ExecuteWebServerUrl(getActContext(), parameters);
        exeServerTask.setLoaderConfig(getActContext(), true, generalFunc);
        exeServerTask.setDataResponseListener(responseString -> {

            if (!responseString.equals("")) {

                boolean isDataAvail = generalFunc.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail) {


                    showDeclineReasonsAlert(responseString);
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }

            } else {
                generalFunc.showError();
            }

        });
        exeServerTask.execute();
    }

    String titleDailog = "";
    int selCurrentPosition= -1;
    public void showDeclineReasonsAlert(String responseString) {
        selCurrentPosition= -1;
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActContext());
//        builder.setTitle(generalFunc.retrieveLangLBl("", "LBL_CANCEL_BOOKING"));

        if (generalFunc.getJsonValue("eType",tripDetailJson).equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
            titleDailog=(generalFunc.retrieveLangLBl("", "LBL_CANCEL_BOOKING"));
        } else {
            titleDailog=(generalFunc.retrieveLangLBl("", "LBL_CANCEL_DELIVERY"));
        }

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.decline_order_dialog_design, null);
        builder.setView(dialogView);
        MTextView cancelTxt = (MTextView) dialogView.findViewById(R.id.cancelTxt);
        MTextView submitTxt = (MTextView) dialogView.findViewById(R.id.submitTxt);
        MTextView subTitleTxt = (MTextView) dialogView.findViewById(R.id.subTitleTxt);
        subTitleTxt.setText(titleDailog);
        MaterialEditText reasonBox = (MaterialEditText) dialogView.findViewById(R.id.inputBox);
        RelativeLayout commentArea = (RelativeLayout) dialogView.findViewById(R.id.commentArea);
        reasonBox.setVisibility(View.GONE);
        ImageView cancelImg = (ImageView) dialogView.findViewById(R.id.cancelImg);
        cancelImg.setOnClickListener(v -> {
            Utils.hideKeyboard(getActContext());
            dialog_declineOrder.dismiss();
        });

        reasonBox.setHideUnderline(true);
        if (generalFunc.isRTLmode()) {
            reasonBox.setPaddings(0, 0, (int) getResources().getDimension(R.dimen._10sdp),0 );
        } else {
            reasonBox.setPaddings((int) getResources().getDimension(R.dimen._10sdp), 0, 0, 0);
        }
        reasonBox.setSingleLine(false);
        reasonBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        reasonBox.setGravity(Gravity.TOP);

        reasonBox.setVisibility(View.GONE);
        commentArea.setVisibility(View.GONE);
        new CreateRoundedView(Color.parseColor("#ffffff"),5,1,Color.parseColor("#C5C3C3"),commentArea);
        reasonBox.setBothText("", generalFunc.retrieveLangLBl("", "LBL_ENTER_REASON"));


        submitTxt.setText(generalFunc.retrieveLangLBl("", "LBL_YES"));
        cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_NO"));

        MTextView declinereasonBox = (MTextView)dialogView.findViewById(R.id.declinereasonBox) ;
        declinereasonBox.setText(generalFunc.retrieveLangLBl("Select Reason", "LBL_SELECT_CANCEL_REASON"));
        submitTxt.setClickable(false);
        submitTxt.setTextColor(getResources().getColor(R.color.gray_holo_light));
        cancelTxt.setOnClickListener(v -> {
            Utils.hideKeyboard(getActContext());
            dialog_declineOrder.dismiss();
        });


        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
      //  HashMap<String, String> map = new HashMap<>();
       // map.put("title", "-- " + generalFunc.retrieveLangLBl("Select Reason", "LBL_SELECT_CANCEL_REASON") + " --");
       // map.put("id", "");
      //  list.add(map);
        JSONArray arr_msg = generalFunc.getJsonArray(Utils.message_str, responseString);
        if (arr_msg != null) {

            for (int i = 0; i < arr_msg.length(); i++) {

                JSONObject obj_tmp = generalFunc.getJsonObject(arr_msg, i);


                HashMap<String, String> datamap = new HashMap<>();
                datamap.put("title", generalFunc.getJsonValueStr("vTitle", obj_tmp));
                datamap.put("id", generalFunc.getJsonValueStr("iCancelReasonId", obj_tmp));
                list.add(datamap);
            }

            HashMap<String, String> othermap = new HashMap<>();
            othermap.put("title", generalFunc.retrieveLangLBl("", "LBL_OTHER_TXT"));
            othermap.put("id", "");
            list.add(othermap);
           // AppCompatSpinner spinner = (AppCompatSpinner) dialogView.findViewById(R.id.declineReasonsSpinner);
           /* CustSpinnerAdapter adapter = new CustSpinnerAdapter(getActContext(), list);
            spinner.setAdapter(adapter);


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (spinner.getSelectedItemPosition() == (list.size() - 1)) {
                        reasonBox.setVisibility(View.VISIBLE);
                        commentArea.setVisibility(View.VISIBLE);
                        //dialog_declineOrder.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                      ////  dialog_declineOrder.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(true);
                      //  dialog_declineOrder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getActContext().getResources().getColor(R.color.black));
                    } else if (spinner.getSelectedItemPosition() == 0) {
                        //  dialog_declineOrder.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                       // dialog_declineOrder.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(false);
                       // dialog_declineOrder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getActContext().getResources().getColor(R.color.gray));
                        reasonBox.setVisibility(View.GONE);
                        commentArea.setVisibility(View.GONE);
                    } else {
                        //dialog_declineOrder.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                      //  dialog_declineOrder.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(true);
                       // dialog_declineOrder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getActContext().getResources().getColor(R.color.black));
                        reasonBox.setVisibility(View.GONE);
                        commentArea.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });*/

            declinereasonBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenListView.getInstance(getActContext(), generalFunc.retrieveLangLBl("", "LBL_SELECT_REASON"), list, OpenListView.OpenDirection.CENTER, true, position -> {


                        selCurrentPosition = position;
                        HashMap<String, String> mapData = list.get(position);
                        declinereasonBox.setText(mapData.get("title"));
                        if (selCurrentPosition == (list.size() - 1)) {
                            reasonBox.setVisibility(View.VISIBLE);
                            commentArea.setVisibility(View.VISIBLE);
                        }  else {
                            reasonBox.setVisibility(View.GONE);
                            commentArea.setVisibility(View.GONE);
                        }

                        submitTxt.setClickable(true);
                        submitTxt.setTextColor(getResources().getColor(R.color.white));



                    }).show(selCurrentPosition, "title");
                }
            });





            submitTxt.setOnClickListener(v -> {
                Utils.hideKeyboard(getActContext());

                if (selCurrentPosition == -1) {
                    return;
                }
                if (Utils.checkText(reasonBox) == false && selCurrentPosition == (list.size() - 1)) {
                    reasonBox.setError(generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD"));
                    return;
                }
                cancelTrip("No", list.get(selCurrentPosition).get("id"), reasonBox.getText().toString().trim());

                dialog_declineOrder.dismiss();
            });


            dialog_declineOrder = builder.create();
            dialog_declineOrder.getWindow().setBackgroundDrawable(getActContext().getResources().getDrawable(R.drawable.all_roundcurve_card));
            dialog_declineOrder.show();

            //  dialog_declineOrder.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
          /*  dialog_declineOrder.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(false);
            dialog_declineOrder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getActContext().getResources().getColor(R.color.gray));*/

           /* dialog_declineOrder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (spinner.getSelectedItemPosition() == 0) {
                        return;
                    }

                    if (Utils.checkText(reasonBox) == false && spinner.getSelectedItemPosition() == (list.size() - 1)) {
                        reasonBox.setError(generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD"));
                        return;
                    }

                    // declineOrder(arrListIDs.get(spinner.getSelectedItemPosition()), Utils.getText(reasonBox));
//                    new CancelTripDialog(getActContext(), data_trip, generalFunc, arrListIDs.get(spinner.getSelectedItemPosition()), Utils.getText(reasonBox), isTripStart);

                    cancelTrip("No", list.get(spinner.getSelectedItemPosition()).get("id"), reasonBox.getText().toString().trim());

                    dialog_declineOrder.dismiss();
                }
            });*/
/*
            dialog_declineOrder.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_declineOrder.dismiss();
                }
            });*/
        } else {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_NO_DATA_AVAIL"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_view_tutor_detail:
                 HashMap<String, String> tripDetail = new HashMap<>();
                 tripDetail.put("driverRating",vRating);
                 tripDetail.put("driverName",vName);
                 tripDetail.put("iDriverId",generalFunc.getJsonValue("iDriverId",tripDetailJson));
                 tripDetail.put("driverImage",vImage);
                 tripDetail.put("vCode",generalFunc.getJsonValue("vCode",tripDetailJson));
                 tripDetail.put("driverMobile",generalFunc.getJsonValue("driverMobile",tripDetailJson));
                 tripDetail.put("iTripid",getIntent().getStringExtra("iTripId"));
                return true;

            case R.id.menu_cancel_trip:
                //buildWarningMessage(generalFunc.retrieveLangLBl("Are you sure, you want to cancel your Job?", "LBL_BOOKING_CANCEL_TXT"),
                //  generalFunc.retrieveLangLBl("", "LBL_YES"),
                //  generalFunc.retrieveLangLBl("Continue Job", "LBL_NO"), true);

                isCancelTripWarning = true;
                getDeclineReasonsList();
                return true;
            case R.id.menu_sos:
                Bundle bn = new Bundle();
                // bn.putString("UserProfileJson", userProfileJson);
                bn.putString("TripId", getIntent().getStringExtra("iTripId"));
                new StartActProcess(getActContext()).startActWithData(ConfirmEmergencyTapActivity.class, bn);

                return true;

            case R.id.menu_track:

                String eServiceLocation = generalFunc.getJsonValue("eServiceLocation",tripDetailJson);
                if (item.getTitle().toString().equalsIgnoreCase(generalFunc.retrieveLangLBl("Live Track", "LBL_LIVE_TRACK_TXT"))) {
                    item.setTitle(generalFunc.retrieveLangLBl("JOB PROGRESS", "LBL_PROGRESS_HINT"));
                    onGoingTripsDetailListRecyclerView.setVisibility(View.GONE);

                    if (eServiceLocation.equalsIgnoreCase("Driver")) {
                        progressHinttext.setText(generalFunc.retrieveLangLBl("Live Track", "LBL_NAVIGATE_TO_PROVIDER"));
                    } else {
                        progressHinttext.setText(generalFunc.retrieveLangLBl("Live Tarck", "LBL_LIVE_TRACK_TXT"));
                    }

                    googlemaparea.setVisibility(View.VISIBLE);
                    if (timeTxt.length() > 0) {
                        timeTxt.setVisibility(View.VISIBLE);
                    } else {
                        timeTxt.setVisibility(View.GONE);
                    }

                    //
                    isLiveTrack = true;
                    subscribeToDriverLocChannel();

                } else if (item.getTitle().toString().equalsIgnoreCase(generalFunc.retrieveLangLBl("Live Track", "LBL_NAVIGATE_TO_PROVIDER"))) {

                    try {
                        isLiveTrack = false;
                        if (eServiceLocation.equalsIgnoreCase("Driver")) {
                            item.setTitle(generalFunc.retrieveLangLBl("Live Track", "LBL_NAVIGATE_TO_PROVIDER"));
                        } else {
                            item.setTitle(generalFunc.retrieveLangLBl("Live Tarck", "LBL_LIVE_TRACK_TXT"));
                        }
                        onGoingTripsDetailListRecyclerView.setVisibility(View.VISIBLE);
                        progressHinttext.setText(generalFunc.retrieveLangLBl("JOB PROGRESS", "LBL_PROGRESS_HINT"));
                        googlemaparea.setVisibility(View.GONE);
                        timeTxt.setVisibility(View.GONE);

                        //

                        String url_view = "http://maps.google.com/maps?daddr=" + generalFunc.getJsonValue("tSaddress",tripDetailJson);
                        (new StartActProcess(getActContext())).openURL(url_view, "com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    } catch (Exception e) {

                    }


                } else {
                    isLiveTrack = false;
                    if (eServiceLocation.equalsIgnoreCase("Driver")) {
                        item.setTitle(generalFunc.retrieveLangLBl("Live Track", "LBL_NAVIGATE_TO_PROVIDER"));
                    } else {
                        item.setTitle(generalFunc.retrieveLangLBl("Live Tarck", "LBL_LIVE_TRACK_TXT"));
                    }
                    onGoingTripsDetailListRecyclerView.setVisibility(View.VISIBLE);
                    progressHinttext.setText(generalFunc.retrieveLangLBl("JOB PROGRESS", "LBL_PROGRESS_HINT"));
                    googlemaparea.setVisibility(View.GONE);
                    timeTxt.setVisibility(View.GONE);
                    unSubscribeToDriverLocChannel();
                }

                return true;

            case R.id.menu_call:
                if (generalFunc.getJsonValue("RIDE_DRIVER_CALLING_METHOD", userProfileJson).equals("Voip")) {
                    sinchCall();
                } else {
                    getMaskNumber();
                }
                return true;
            case R.id.menu_message:

                Bundle bnChat = new Bundle();
                bnChat.putString("iFromMemberId", generalFunc.getJsonValue("iDriverId",tripDetailJson));
                bnChat.putString("FromMemberImageName",vImage);
                bnChat.putString("iTripId", getIntent().getStringExtra("iTripId"));
                bnChat.putString("FromMemberName",generalFunc.getJsonValue("iDriverId",tripDetailJson));
                bnChat.putString("vBookingNo",generalFunc.getJsonValue("vRideNo",tripDetailJson));



                new StartActProcess(getActContext()).startActWithData(ChatActivity.class, bnChat);
                return true;

            case R.id.menu_service:
                Bundle bnService = new Bundle();
                bnService.putString("iTripId",getIntent().getStringExtra("iTripId"));
                bnService.putString("iDriverId",generalFunc.getJsonValue("iDriverId",tripDetailJson));
                return true;


            case R.id.menu_service_charges:
                redirectToDetailCharges("");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void redirectToDetailCharges(String verifyPushMsg) {
        Bundle bnService1 = new Bundle();
        bnService1.putString("iTripId",getIntent().getStringExtra("iTripId"));
        bnService1.putString("iDriverId", generalFunc.getJsonValue("iDriverId",tripDetailJson));
        HashMap<String, String> tripDetail = new HashMap<>();
        tripDetail.put("driverRating",vRating);
        tripDetail.put("driverName",vName);
        tripDetail.put("iDriverId",generalFunc.getJsonValue("iDriverId",tripDetailJson));
        tripDetail.put("driverImage",vImage);
        tripDetail.put("vCode",generalFunc.getJsonValue("vCode",tripDetailJson));
        tripDetail.put("driverMobile",generalFunc.getJsonValue("driverMobile",tripDetailJson));
        tripDetail.put("iTripid",getIntent().getStringExtra("iTripId"));
        bnService1.putSerializable("TripDetail",tripDetail);

        if (Utils.checkText(verifyPushMsg)) {
            bnService1.putString("fMaterialFee", generalFunc.getJsonValue("fMaterialFee", verifyPushMsg));
            bnService1.putString("fMiscFee", generalFunc.getJsonValue("fMiscFee", verifyPushMsg));
            bnService1.putString("fDriverDiscount", generalFunc.getJsonValue("fDriverDiscount", verifyPushMsg));
            bnService1.putString("vConfirmationCode", generalFunc.getJsonValue("vConfirmationCode", verifyPushMsg));
            bnService1.putString("eApproveRequestSentByDriver", generalFunc.getJsonValue("eApproveRequestSentByDriver", verifyPushMsg));
            bnService1.putString("serviceCost", generalFunc.getJsonValue("serviceCost", verifyPushMsg));
            bnService1.putString("totalAmount", generalFunc.getJsonValue("totalAmount", verifyPushMsg));
        }
        else
        {
            bnService1.putString("fMaterialFee",generalFunc.getJsonValue("fMaterialFee",tripDetailJson));
            bnService1.putString("fMiscFee", generalFunc.getJsonValue("fMiscFee",tripDetailJson));
            bnService1.putString("fDriverDiscount", generalFunc.getJsonValue("fDriverDiscount",tripDetailJson));
            bnService1.putString("vConfirmationCode",generalFunc.getJsonValue("vConfirmationCode",tripDetailJson));
            bnService1.putString("eApproveRequestSentByDriver",generalFunc.getJsonValue("eApproveRequestSentByDriver",tripDetailJson));
            bnService1.putString("serviceCost",generalFunc.getJsonValue("serviceCost",tripDetailJson));
            bnService1.putString("totalAmount", generalFunc.getJsonValue("totalAmount",tripDetailJson));

        }
        new StartActProcess(getActContext()).startActForResult(AdditionalChargeActivity.class, bnService1,ADDITIONAL_CHARGES_CODE);

    }

    public void sinchCall() {


        if (generalFunc.isCallPermissionGranted(false) == false) {
            generalFunc.isCallPermissionGranted(true);
        } else {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("Id", generalFunc.getMemberId());
            hashMap.put("Name", generalFunc.getJsonValue("vName", userProfileJson));
            hashMap.put("PImage", generalFunc.getJsonValue("vImgName", userProfileJson));
            hashMap.put("type", Utils.userType);

            if (new AppFunctions(getActContext()).checkSinchInstance(getSinchServiceInterface())) {
                getSinchServiceInterface().getSinchClient().setPushNotificationDisplayName(generalFunc.retrieveLangLBl("", "LBL_INCOMING_CALL"));
                Call call = getSinchServiceInterface().callUser(Utils.CALLTODRIVER + "_" +generalFunc.getJsonValue("iDriverId",tripDetailJson), hashMap);

                String callId = call.getCallId();

                Intent callScreen = new Intent(getActContext(), CallScreenActivity.class);
                callScreen.putExtra(SinchService.CALL_ID, callId);
                String image_url = CommonUtilities.PROVIDER_PHOTO_PATH + generalFunc.getJsonValue("iDriverId",tripDetailJson) + "/" + vImage;
                callScreen.putExtra("vImage", image_url);
                callScreen.putExtra("vName", vName);
                callScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(callScreen);
            }
        }


    }


    public void call(String phoneNumber) {
        try {

            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);

        } catch (Exception e) {
            // TODO: handle exception
        }


    }

    public void getMaskNumber() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getCallMaskNumber");
        parameters.put("iTripid",generalFunc.getJsonValue("iTripId",tripDetailJson));
        parameters.put("UserType", Utils.userType);
        parameters.put("iMemberId", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);

        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {
                    String message = generalFunc.getJsonValue(Utils.message_str, responseString);

                    call(message);
                } else {
                    call("+" +generalFunc.getJsonValue("vCode",tripDetailJson)  + "" +generalFunc.getJsonValue("driverMobile",tripDetailJson) );

                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();


    }


    public void cancelTrip(String eConfirmByUser, String iCancelReasonId, String reason) {


        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "cancelTrip");
        parameters.put("UserType", Utils.app_type);
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("iDriverId", generalFunc.getJsonValue("iDriverId",tripDetailJson));
        parameters.put("iTripId", getIntent().getStringExtra("iTripId"));
        parameters.put("eConfirmByUser", eConfirmByUser);
        parameters.put("iCancelReasonId", iCancelReasonId);
        parameters.put("Reason", reason);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {
                    // finish();
                    GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setCancelable(false);
                    generateAlert.setBtnClickList(btn_id -> MyApp.getInstance().restartWithGetDataApp());

                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue("message1", responseString)));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    generateAlert.showAlertBox();

                } else {
                    if (generalFunc.getJsonValue("isCancelChargePopUpShow", responseString).equalsIgnoreCase("Yes")) {

                        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                        generateAlert.setCancelable(false);
                        generateAlert.setBtnClickList(btn_id -> {
                            if (btn_id == 0) {
                                generateAlert.closeAlertBox();

                            } else {
                                generateAlert.closeAlertBox();
                                cancelTrip("Yes", iCancelReasonId, reason);

                            }

                        });
                        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_YES"));
                        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_NO"));
                        generateAlert.showAlertBox();

                        return;
                    }

//                    buildWarningMessage(generalFunc.retrieveLangLBl("", "LBL_REQUEST_FAILED_PROCESS"),
//                            generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), "", false);
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public void getTripDeliveryLocations() {
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        loadingArea.setVisibility(View.VISIBLE);
        final HashMap<String, String> parameters = new HashMap<String, String>();
//        parameters.put("type", "getTripDeliveryLocations");
        parameters.put("iTripId", getIntent().getStringExtra("iTripId"));
        parameters.put("iCabBookingId", "");
        parameters.put("type", getIntent().getStringExtra("eType").equalsIgnoreCase(Utils.eType_Multi_Delivery) ? "getTripDeliveryDetails" : "getTripDeliveryLocations");
       // parameters.put("type", "getTripDeliveryDetails" );

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                closeLoader();

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    list = new ArrayList<>();

                    String message = generalFunc.getJsonValue(Utils.message_str, responseString);
                    server_time = generalFunc.getJsonValue("SERVER_TIME", responseString);
                    String driverDetails = generalFunc.getJsonValue("driverDetails", message);

                    destLoc = new Location("Dest");
                    destLoc.setLatitude(GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValue("tStartLat", driverDetails)));
                    destLoc.setLongitude(GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValue("tStartLong", driverDetails)));


                    eType = generalFunc.getJsonValue("eType", driverDetails);

                    driverStatus = generalFunc.getJsonValue("driverStatus", driverDetails);

                    JSONArray tripLocations = generalFunc.getJsonArray("States", message);

                    if (!driverStatus.equalsIgnoreCase("Active")) {
                        isarrived = true;
                    } else {
                        isarrived = false;
                    }

                    Logger.e("DRIVER_STATUS", "::" + isarrived);

                    list.clear();
                    if (tripLocations != null)
                        for (int i = 0; i < tripLocations.length(); i++) {
                            tempMap = new HashMap<>();

                            JSONObject jobject1 = generalFunc.getJsonObject(tripLocations, i);
                            tempMap.put("status", generalFunc.getJsonValue("type", jobject1.toString()));
                            tempMap.put("iTripId", generalFunc.getJsonValue("text", jobject1.toString()));
                            tempMap.put("value", generalFunc.getJsonValue("timediff", jobject1.toString()));
                            tempMap.put("Booking_LBL", generalFunc.retrieveLangLBl("", "LBL_BOOKING"));
                            tempMap.put("time", generalFunc.getJsonValue("time", jobject1.toString()));

                            if(tripDetail!=null) {
                                tempMap.put("eType", tripDetail.get("eType"));
                                /*Multi Related Details*/
                                if (tripDetail.get("eType").equalsIgnoreCase(Utils.eType_Multi_Delivery) && Utils.checkText(generalFunc.getJsonValue("vDeliveryConfirmCode", jobject1.toString()))) {
                                    tempMap.put("msg", generalFunc.getJsonValue("text", jobject1.toString()) + " " + generalFunc.retrieveLangLBl("Delivery Confirmation Code", "LBL_DELIVERY_CONFIRMATION_CODE_TXT") + " " + generalFunc.getJsonValue("vDeliveryConfirmCode", jobject1.toString()));

                                } else {

                                    tempMap.put("msg", generalFunc.getJsonValue("text", jobject1.toString()));
                                }
                            }
                            else
                            {
                                tempMap.put("msg", generalFunc.getJsonValue("text", jobject1.toString()));
                            }
                            list.add(tempMap);
                        }
                    setView();
                        if(tripDetail!=null) {
                            if (tripDetail.get("eType").equalsIgnoreCase(Utils.eType_Multi_Delivery)) {
                                tripDetailJson = generalFunc.getJsonValue("MemberDetails", message);

                            } else {
                                tripDetailJson = generalFunc.getJsonValue("driverDetails", message);
                            }
                        }
                        else
                        {
                            tripDetailJson = generalFunc.getJsonValue("driverDetails", message);
                        }

                    if (!generalFunc.getJsonValue("eType",tripDetailJson).equalsIgnoreCase(Utils.eType_Multi_Delivery)) {
                        progressHinttext.setText(generalFunc.retrieveLangLBl("JOB PROGRESS", "LBL_PROGRESS_HINT"));
                    } else {
                        progressHinttext.setText(generalFunc.retrieveLangLBl("Delivery Status", "LBL_DELIVERY_STATUS_TXT"));
                    }

                    String vChargesDetailDataAvailable=generalFunc.getJsonValue("vChargesDetailDataAvailable",tripDetailJson);
                    isVerifyCharges=Utils.checkText(vChargesDetailDataAvailable)&&vChargesDetailDataAvailable.equalsIgnoreCase("Yes")?true:false;



                       setData();
                } else {
                    generateErrorView();
                }
            } else {
                generateErrorView();
            }
        });
        exeWebServer.execute();
    }

    private void setView() {
        onGoingTripDetailAdapter = new OnGoingTripDetailAdapter(getActContext(), list, generalFunc);
        onGoingTripsDetailListRecyclerView.setAdapter(onGoingTripDetailAdapter);
        onGoingTripDetailAdapter.notifyDataSetChanged();

    }

    public void generateErrorView() {

        closeLoader();

        generalFunc.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT");

        if (errorView.getVisibility() != View.VISIBLE) {
            errorView.setVisibility(View.VISIBLE);
        }
        errorView.setOnRetryListener(() -> getTripDeliveryLocations());
    }

    public void closeLoader() {
        if (loadingArea.getVisibility() == View.VISIBLE) {
            loadingArea.setVisibility(View.GONE);
        }
    }

    private void setDriverDetail() {
        String image_url = CommonUtilities.PROVIDER_PHOTO_PATH + generalFunc.getJsonValue("iDriverId",tripDetailJson) + "/" + vImage;

        Picasso.get()
                .load(image_url)
                .placeholder(R.mipmap.ic_no_pic_user)
                .error(R.mipmap.ic_no_pic_user)
                .into(((ImageView) findViewById(R.id.user_img)));

        userNameTxt.setText(vName);
        userAddressTxt.setText(generalFunc.getJsonValue("tSaddress",tripDetailJson));
        ratingBar.setRating(generalFunc.parseFloatValue(0, vRating));

    }

    public void setTimetext(String distance, String time) {
        try {
            String userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
            String distance_str = "";
            if (generalFunc.retrieveValue(APP_TYPE).equalsIgnoreCase("UberX") || eType.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {

                if (googlemaparea.getVisibility() == View.VISIBLE) {
                    timeTxt.setVisibility(View.VISIBLE);
                }


                Logger.d("eUnit", "::" + generalFunc.getJsonValue("eUnit", userProfileJson));
                if (userProfileJson != null && !generalFunc.getJsonValue("eUnit", userProfileJson).equalsIgnoreCase("KMs")) {
                    timeTxt.setText(time + " " + generalFunc.retrieveLangLBl("to reach", "LBL_REACH_TXT") + " & " + distance + " " + generalFunc.retrieveLangLBl("", "LBL_MILE_DISTANCE_TXT") + " " + generalFunc.retrieveLangLBl("away", "LBL_AWAY_TXT"));
                } else {
                    timeTxt.setText(time + " " + generalFunc.retrieveLangLBl("to reach", "LBL_REACH_TXT") + " & " + distance + " " + generalFunc.retrieveLangLBl("", "LBL_KM_DISTANCE_TXT") + " " + generalFunc.retrieveLangLBl("away", "LBL_AWAY_TXT"));

                }


            } else {
                if ( generalFunc.getJsonValue("eFareType",tripDetailJson).equalsIgnoreCase(Utils.CabFaretypeRegular)) {

                    timeTxt.setVisibility(View.VISIBLE);
                } else {
                    timeTxt.setVisibility(View.GONE);
                }

            }
        } catch (Exception e) {

        }

    }

    private void setLables() {

        titleTxt.setText(generalFunc.retrieveLangLBl("Booking No", "LBL_BOOKING") + "# " +generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("vRideNo",tripDetailJson)) );
        subTitleTxt.setVisibility(View.VISIBLE);

        if (generalFunc.getJsonValue("eServiceLocation",tripDetailJson).equalsIgnoreCase("Driver")) {
            subTitleTxt.setText(generalFunc.retrieveLangLBl("Live Track", "LBL_NAVIGATE_TO_PROVIDER"));
        } else {
            subTitleTxt.setText(generalFunc.retrieveLangLBl("Live Track", "LBL_LIVE_TRACK_TXT"));
        }
        subTitleTxt.setVisibility(View.GONE);

    }

    private void init() {
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        subTitleTxt = (MTextView) findViewById(R.id.subTitleTxt);

        loading_ongoing_trips_detail = (ProgressBar) findViewById(R.id.loading_ongoing_trips_detail);
        loadingArea = (RelativeLayout) findViewById(R.id.loadingArea);
        onGoingTripsDetailListRecyclerView = (RecyclerView) findViewById(R.id.onGoingTripsDetailListRecyclerView);
        contentArea = (LinearLayout) findViewById(R.id.contentArea);
        errorView = (ErrorView) findViewById(R.id.errorView);
        user_img = (SelectableRoundedImageView) findViewById(R.id.user_img);
        userNameTxt = (MTextView) findViewById(R.id.userNameTxt);
        userAddressTxt = (MTextView) findViewById(R.id.userAddressTxt);
        ratingBar = (SimpleRatingBar) findViewById(R.id.ratingBar);
        progressHinttext = (MTextView) findViewById(R.id.progressHinttext);
        timeTxt = (MTextView) findViewById(R.id.timeTxt);

        googlemaparea = (LinearLayout) findViewById(R.id.googlemaparea);
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        backImgView.setOnClickListener(new setOnClickList());
        subTitleTxt.setOnClickListener(new setOnClickList());


        map.setListener(() -> {
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setTaskKilledValue(true);

        unSubscribeToDriverLocChannel();
    }

    private Activity getActContext() {
        return OnGoingTripDetailsActivity.this;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;

        this.gMap.setOnMarkerClickListener(marker -> true);


        if (tripDetail!=null && !tripDetail.get("vTripStatus").equals("Arrived")) {
            //subscribeToDriverLocChannel();
            LatLng driverLocation_update = new LatLng(Double.parseDouble(tripDetail.get("vLatitude")),Double.parseDouble(tripDetail.get("vLongitude")));

            updateDriverLocation(driverLocation_update);

            Handler handler = new Handler();
            handler.postDelayed(() -> {


            }, 500);

        }


    }

    @Override
    public void onTaskRun() {


        updateDriverLocations();
    }

    public void updateDriverLocations() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getDriverLocations");
        parameters.put("iDriverId", generalFunc.getJsonValue("iDriverId",tripDetailJson));
        parameters.put("UserType", Utils.app_type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {

                    String vLatitude = generalFunc.getJsonValue("vLatitude", responseString);
                    String vLongitude = generalFunc.getJsonValue("vLongitude", responseString);
                    String vTripStatus = generalFunc.getJsonValue("vTripStatus", responseString);

                    if (vTripStatus.equals("Arrived")) {
                        isarrived = true;
                        invalidateOptionsMenu();


                    }

                    LatLng driverLocation_update = new LatLng(generalFunc.parseDoubleValue(0.0, vLatitude),
                            generalFunc.parseDoubleValue(0.0, vLongitude));

                    if (driverMarker != null) {
                        driverMarker.remove();
                    }
                    driverLocation = driverLocation_update;


                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.car_driver).copy(Bitmap.Config.ARGB_8888, true);
                    driverMarker = gMap.addMarker(
                            new MarkerOptions().position(driverLocation)
                                    .icon(BitmapDescriptorFactory.fromBitmap(bm)));

                    driverMarker.setFlat(true);
                    driverMarker.setAnchor(0.5f, 1);


                    /*}*/

                    CameraPosition cameraPosition = cameraForDriverPosition();
                    gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            } else {
//                    generalFunc.showError();
            }
        });
        exeWebServer.execute();
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

    public void updateDriverLocation(LatLng latlog) {
        if (latlog == null) {
            return;
        }

        if (driverMarker == null) {
            try {
                if (gMap != null) {
                    marker_view = ((LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                            .inflate(R.layout.ufx_live_track, null);
                    providerImgView = (SelectableRoundedImageView) marker_view
                            .findViewById(R.id.providerImgView);


                    driverMarker = gMap.addMarker(
                            new MarkerOptions().position(latlog)
                                    .title("DriverId" + generalFunc.getJsonValue("iDriverId",tripDetailJson)).
                                    icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActContext(), marker_view)))
                                    .anchor(0.5f, 1.0f).flat(true));


                    driverLocation = latlog;
                    CameraPosition cameraPosition = cameraForDriverPosition();
                    gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                    providerImgView.setImageResource(R.mipmap.pdefault);

                    driverMarker.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActContext(), marker_view)));


                }
            } catch (Exception e) {
                Logger.d("markerException", e.toString());
            }
        } else {
            double currentZoomLevel = gMap.getCameraPosition().zoom;

            if (Utils.defaultZomLevel > currentZoomLevel) {
                currentZoomLevel = Utils.defaultZomLevel;
            }
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latlog)
                    .zoom((float) currentZoomLevel).build();

            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            Location location = new Location("livetrack");
            location.setLatitude(latlog.latitude);
            location.setLongitude(latlog.longitude);
            animDriverMarker.animateMarker(driverMarker, gMap, location, 0, defaultMarkerAnimDuration,generalFunc.getJsonValue("iDriverId",tripDetailJson), "");

        }


    }

    public void pubNubMsgArrived(final String message, final Boolean ishow) {

        runOnUiThread(() -> {

            String msgType = generalFunc.getJsonValue("MsgType", message);
            String iDriverId = generalFunc.getJsonValue("iDriverId", message);
            String iTripId = generalFunc.getJsonValue("iTripId", message);

            if (generalFunc.getJsonValue("iDriverId",tripDetailJson).equals(iDriverId)) {
                if (msgType.equals("LocationUpdateOnTrip")) {
                    String vLatitude = generalFunc.getJsonValue("vLatitude", message);
                    String vLongitude = generalFunc.getJsonValue("vLongitude", message);

                    Location driverLoc = new Location("Driverloc");
                    driverLoc.setLatitude(GeneralFunctions.parseDoubleValue(0.0, vLatitude));
                    driverLoc.setLongitude(GeneralFunctions.parseDoubleValue(0.0, vLongitude));

                    Logger.e("DRIVER_STATUS", "::" + isarrived);

                    if (!isarrived) {
                        callUpdateDeirection(driverLoc);
                    } else {
                        timeTxt.setVisibility(View.GONE);
                    }

                    LatLng driverLocation_update = new LatLng(GeneralFunctions.parseDoubleValue(0.0, vLatitude),
                            GeneralFunctions.parseDoubleValue(0.0, vLongitude));

                    driverLocation = driverLocation_update;

                    if (googlemaparea.getVisibility() == View.VISIBLE) {
                        updateDriverLocation(driverLocation_update);
                    }
                } else if (msgType.equals("DriverArrived")) {

                    isarrived = true;
                    invalidateOptionsMenu();

                    if (!generalFunc.getJsonValue("eFareType",tripDetailJson).equals(Utils.CabFaretypeRegular)) {
                        unSubscribeToDriverLocChannel();
                    }

                    if (!isarrivedpopup) {
                        isarrivedpopup = true;
                        getTripDeliveryLocations();
                    }
                } else if (msgType.equals("VerifyCharges")) {
                   /* isVerifyCharges=true;
                    verifyPushMsg=message;*/
                   onBackPressed();
                } else {
                    onGcmMessageArrived(message, ishow);
                }


            }


        });
    }

    private void showCharges() {

    }


    public void onGcmMessageArrived(final String message, boolean ishow) {

        String driverMsg = generalFunc.getJsonValue("Message", message);

        if (driverMsg.equals("DriverArrived")) {
            getTripDeliveryLocations();
        }


        if (driverMsg.equals("TripEnd")) {

            if (ishow) {
                if (!ishowdialog) {

                    GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
                    alertBox.setContentMessage("", generalFunc.getJsonValue("vTitle", message));
                    alertBox.setPositiveBtn(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));

                    if (generalFunc.getJsonValue("eType", message).equalsIgnoreCase(Utils.eType_Multi_Delivery) && generalFunc.getJsonValue("Is_Last_Delivery", message).equalsIgnoreCase("Yes")) {

                        alertBox.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));

                    }

                    alertBox.setCancelable(false);
                    alertBox.setBtnClickList(btn_id -> {

                        if (btn_id == 0) {
                            onBackPressed();
                        } else {

                            Bundle bn = new Bundle();
                            bn.putString("iTripId", generalFunc.getJsonValue("iTripId", message));

                            if (generalFunc.getJsonValue("eType", message).equalsIgnoreCase(Utils.eType_Multi_Delivery)) {

                                if (generalFunc.getJsonValue("Is_Last_Delivery", message).equalsIgnoreCase("Yes")) {

                                    if (!Utils.checkText(generalFunc.getJsonValue("iTripId", message))) {
                                        return;
                                    }

                                    bn.putBoolean("isUfx", false);
                                    ishowdialog = false;
                                    new StartActProcess(getActContext()).startActForResult(HistoryDetailActivity.class, bn, Utils.MULTIDELIVERY_HISTORY_RATE_CODE);
                                } else if (generalFunc.getJsonValue("Is_Last_Delivery", message).equalsIgnoreCase("NO")) {
                                    getTripDeliveryLocations();
                                    return;
                                }

                            } else {
                                ishowdialog = false;

                                String eType = generalFunc.getJsonValue("eType", message);
                                Logger.d("eTypeNotiFication", "::" + eType);
                                if (eType.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                                    bn.putBoolean("isUfx", true);
                                } else {
                                    bn.putBoolean("isUfx", false);
                                }

                                bn.putString("iTripId", generalFunc.getJsonValue("iTripId", message));
                                new StartActProcess(getActContext()).startActWithData(RatingActivity.class, bn);
                                finish();
                            }

                        }

                    });
                    alertBox.showAlertBox();
                }
            }

        } else if (driverMsg.equals("TripStarted")) {

            getTripDeliveryLocations();


        }else if (driverMsg.equals("VerifyCharges")) {
           /* isVerifyCharges=true;
            verifyPushMsg=message;*/
            onBackPressed();
        }  else if (driverMsg.equals("TripCancelledByDriver") || driverMsg.equalsIgnoreCase("TripCancelled")) {

            if (ishow) {
                if (!ishowdialog) {
                    ishowdialog = true;
                    String reason = generalFunc.getJsonValue("Reason", message);


                    GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
                    alertBox.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER") + " " + reason);
                    alertBox.setPositiveBtn(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));

                    alertBox.setCancelable(false);
                    alertBox.setBtnClickList(btn_id -> {


                        String eType = generalFunc.getJsonValue("eType", message);
                        if (generalFunc.getJsonValue("ShowTripFare", message).equalsIgnoreCase("true") || (!eType.equalsIgnoreCase(Utils.eType_Multi_Delivery) && !eType.equalsIgnoreCase(Utils.CabGeneralType_UberX))) {
                            Bundle bn = new Bundle();
                            Logger.d("eTypeNotiFication", "::" + eType);
                            if (eType.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                                bn.putBoolean("isUfx", true);
                            } else {
                                bn.putBoolean("isUfx", false);
                            }

                            bn.putString("iTripId", generalFunc.getJsonValue("iTripId", message));
                            new StartActProcess(getActContext()).startActWithData(RatingActivity.class, bn);
                            finish();
                        } else {
                            backImgView.performClick();
                        }

                    });

                    alertBox.showAlertBox();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(getIntent().getBooleanExtra("isRestart",false)){
            MyApp.getInstance().restartWithGetDataApp();
        }
        else
        {
            super.onBackPressed();
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.backImgView) {
              onBackPressed();
            } else if (view.getId() == R.id.subTitleTxt) {
                MyApp.getInstance().restartWithGetDataApp(getIntent().getStringExtra("iTripId"));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.MULTIDELIVERY_HISTORY_RATE_CODE) {
            finish();
        }else if (requestCode == ADDITIONAL_CHARGES_CODE && resultCode == RESULT_OK ) {
            finish();
        }
    }

    public void verifyCharges(String message)
    {
        redirectToDetailCharges(message);
    }

}
