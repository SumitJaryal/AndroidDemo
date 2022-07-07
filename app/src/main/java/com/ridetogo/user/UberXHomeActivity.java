package com.ridetogo.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.fragments.HomeDaynamicFragment;
import com.fragments.HomeFragment;
import com.fragments.MyBookingFragment;
import com.fragments.MyProfileFragment;
import com.fragments.MyWalletFragment;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.OpenAdvertisementDialog;
import com.utils.Logger;
import com.utils.Utils;
import com.view.MTextView;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class UberXHomeActivity extends BaseActivity {


    public LinearLayout historyArea, walletArea, profileArea, homeArea, rduTopArea;
    MTextView historyTxt, walletTxt, profileTxt, homeTxt;
    ImageView home_img, bookingImg, walletImg, profileImg;
    public GeneralFunctions generalFunc;

    public HomeFragment homeFragment;
    public HomeDaynamicFragment homeDaynamicFragment;
    public MyBookingFragment myBookingFragment;
    MyProfileFragment myProfileFragment;
    MyWalletFragment myWalletFragment;
    String userProfileJson;

    int bottomBtnpos = 1;

    int appThemeColor1, appThemeGetColor1, deSelectedGetColor, deSelectedColor, dayanamicSelColor;
    String APP_TYPE;
    public String address;
    public String latitude = "0.0", longitude = "0.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber_xhome2);

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());

        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        APP_TYPE = generalFunc.getJsonValue("APP_TYPE", userProfileJson);
        int color = R.color.appThemeColor_1;
        int dayanamiccolor = R.color.black;
        appThemeColor1 = getActContext().getResources().getColor(color);
        dayanamicSelColor = getActContext().getResources().getColor(dayanamiccolor);
        appThemeGetColor1 = ContextCompat.getColor(getActContext(), color);
        int deSelectColor = R.color.homedeSelectColor;
        deSelectedColor = getActContext().getResources().getColor(deSelectColor);
        deSelectedGetColor = ContextCompat.getColor(getActContext(), deSelectColor);
        historyTxt = findViewById(R.id.historyTxt);
        walletTxt = findViewById(R.id.walletTxt);
        profileTxt = findViewById(R.id.profileTxt);
        homeTxt = findViewById(R.id.homeTxt);
        home_img = findViewById(R.id.home_img);
        bookingImg = findViewById(R.id.bookingImg);
        walletImg = findViewById(R.id.walletImg);
        profileImg = findViewById(R.id.profileImg);


        rduTopArea = findViewById(R.id.rduTopArea);
        historyArea = findViewById(R.id.historyArea);
        walletArea = findViewById(R.id.walletArea);
        profileArea = findViewById(R.id.profileArea);
        homeArea = findViewById(R.id.homeArea);


        if (generalFunc.getMemberId().equalsIgnoreCase("")) {
            historyArea.setVisibility(View.GONE);
            walletArea.setVisibility(View.GONE);
            profileArea.setVisibility(View.GONE);
        }

        historyArea.setOnClickListener(new setOnClickList());
        walletArea.setOnClickListener(new setOnClickList());
        profileArea.setOnClickListener(new setOnClickList());

        homeArea.setOnClickListener(new setOnClickList());


        setLabel();
        manageBottomMenu(homeTxt);
        openHomeFragment();

        String advertise_banner_data = generalFunc.getJsonValue("advertise_banner_data", userProfileJson);
        if (advertise_banner_data != null && !advertise_banner_data.equalsIgnoreCase("")) {

            String image_url = generalFunc.getJsonValue("image_url", advertise_banner_data);
            if (image_url != null && !image_url.equalsIgnoreCase("")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image_url", image_url);
                map.put("tRedirectUrl", generalFunc.getJsonValue("tRedirectUrl", advertise_banner_data));
                map.put("vImageWidth", generalFunc.getJsonValue("vImageWidth", advertise_banner_data));
                map.put("vImageHeight", generalFunc.getJsonValue("vImageHeight", advertise_banner_data));
                new OpenAdvertisementDialog(getActContext(), map, generalFunc);
            }
        }

    }
    public void pubNubMsgArrived(String message) {

        String driverMsg = generalFunc.getJsonValue("Message", message);
        String eType = generalFunc.getJsonValue("eType", message);
        String app_type = APP_TYPE;

        if (driverMsg.equals("CabRequestAccepted")) {
            String eSystem = generalFunc.getJsonValue("eSystem", userProfileJson);
            if (eSystem != null && eSystem.equalsIgnoreCase("DeliverAll")) {
                generalFunc.showGeneralMessage("", generalFunc.getJsonValue("vTitle", message));
                return;
            }


            if (eType.equalsIgnoreCase(Utils.eType_Multi_Delivery)) {
                return;
            } else if (app_type != null && app_type.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {

                if (!eType.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                    MyApp.getInstance().restartWithGetDataApp();
                    return;
                }
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

//    public static class MyHandler extends android.os.Handler {
//        private final WeakReference<UberXHomeActivity> activityWeakReference;
//
//
//        MyHandler(UberXHomeActivity activity) {
//            this.activityWeakReference = new WeakReference<>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (activityWeakReference.get() != null) {
//                activityWeakReference.get().skeletonScreen.hide();
//            }
//        }
//    }

  //  private SkeletonScreen skeletonScreen;

    public void openHomeFragment() {

        if (generalFunc.getJsonValue("ENABLE_NEW_HOME_SCREEN_LAYOUT_APP",userProfileJson) != null &&generalFunc.getJsonValue("ENABLE_NEW_HOME_SCREEN_LAYOUT_APP",userProfileJson).equalsIgnoreCase("Yes")) {
            if (homeDaynamicFragment == null) {
                homeDaynamicFragment = new HomeDaynamicFragment();
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("uberXAddress", address);
                bundle.putString("uberXlat", latitude);
                bundle.putString("uberXlong", longitude);
                homeDaynamicFragment.setArguments(bundle);
            }
            openPageFrag(1, homeDaynamicFragment);

        } else {

            if (homeFragment == null) {
                homeFragment = new HomeFragment();
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("uberXAddress", address);
                bundle.putString("uberXlat", latitude);
                bundle.putString("uberXlong", longitude);
                homeFragment.setArguments(bundle);
            }
            openPageFrag(1, homeFragment);
            bottomBtnpos = 1;
            //getSupportFragmentManager().beginTransaction().replace(R.id.fragContainer, homeFragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Logger.d("Onresume", ":: called");

        if (myProfileFragment != null && isProfilefrg) {
            myProfileFragment.onResume();
        }

        if (myWalletFragment != null && isWalletfrg) {

            myWalletFragment.onResume();
        }


        Log.d("uberxac", "onResume: ");

        if (myBookingFragment != null && isBookingfrg) {

            myBookingFragment.onResume();
        }
    }

    public void openProfileFragment() {

//        if (myProfileFragment != null) {
//            myProfileFragment = null;
//            Utils.runGC();
//        }
        if (myProfileFragment == null) {
            myProfileFragment = new MyProfileFragment();
        }
        openPageFrag(4, myProfileFragment);
        bottomBtnpos = 4;
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragContainer, myProfileFragment).commit();

    }

    public void openWalletFragment() {

//        if (myProfileFragment != null) {
//            myProfileFragment = null;
//            Utils.runGC();
//        }
        if (myWalletFragment == null) {
            myWalletFragment = new MyWalletFragment();
        }


        openPageFrag(3, myWalletFragment);

        bottomBtnpos = 3;


    }


    public void openPageFrag(int position, Fragment fragToOpen) {
        int leftAnim = bottomBtnpos > position ? R.anim.slide_from_left : R.anim.slide_from_right;
        int rightAnim = bottomBtnpos > position ? R.anim.slide_to_right : R.anim.slide_to_left;

        try {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(leftAnim,
                    rightAnim)
                    .replace(R.id.fragContainerone, fragToOpen).commit();


        } catch (Exception e) {
            Logger.e("ExceptionFrag", "::" + e.toString());
        }

//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragContainer, fragToOpen).commit();

    }


    public void openHistoryFragment() {

        if (myBookingFragment == null) {
            myBookingFragment = new MyBookingFragment();
        }

        openPageFrag(2, myBookingFragment);
        bottomBtnpos = 2;
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragContainer, myBookingFragment).commit();

    }

    public void setLabel() {
        profileTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HEADER_RDU_PROFILE"));

        homeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HOME_BOTTOM_MENU"));
        walletTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HEADER_RDU_WALLET"));
        historyTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HEADER_RDU_BOOKINGS"));

    }

    public void manageBottomMenu(MTextView selTextView) {
        //manage Select deselect Bottom Menu
        if (selTextView.getId() == homeTxt.getId()) {

            homeTxt.setTextColor(appThemeColor1);
            home_img.setColorFilter(appThemeGetColor1, android.graphics.PorterDuff.Mode.SRC_IN);
            if (generalFunc.getJsonValue("ENABLE_NEW_HOME_SCREEN_LAYOUT_APP", userProfileJson) != null && generalFunc.getJsonValue("ENABLE_NEW_HOME_SCREEN_LAYOUT_APP", userProfileJson).equalsIgnoreCase("Yes")) {
                homeTxt.setTextColor(dayanamicSelColor);
                home_img.setColorFilter(dayanamicSelColor, android.graphics.PorterDuff.Mode.SRC_IN);

            }
        } else {
            homeTxt.setTextColor(deSelectedColor);
            home_img.setColorFilter(deSelectedGetColor, android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if (selTextView.getId() == historyTxt.getId()) {
            historyTxt.setTextColor(appThemeColor1);
            bookingImg.setColorFilter(appThemeGetColor1, android.graphics.PorterDuff.Mode.SRC_IN);
            if (generalFunc.getJsonValue("ENABLE_NEW_HOME_SCREEN_LAYOUT_APP", userProfileJson) != null && generalFunc.getJsonValue("ENABLE_NEW_HOME_SCREEN_LAYOUT_APP", userProfileJson).equalsIgnoreCase("Yes")) {
                historyTxt.setTextColor(dayanamicSelColor);
                bookingImg.setColorFilter(dayanamicSelColor, android.graphics.PorterDuff.Mode.SRC_IN);

            }
        } else {
            historyTxt.setTextColor(deSelectedColor);
            bookingImg.setColorFilter(deSelectedGetColor, android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if (selTextView.getId() == walletTxt.getId()) {
            walletTxt.setTextColor(appThemeColor1);
            walletImg.setColorFilter(appThemeGetColor1, android.graphics.PorterDuff.Mode.SRC_IN);
            if (generalFunc.getJsonValue("ENABLE_NEW_HOME_SCREEN_LAYOUT_APP", userProfileJson) != null && generalFunc.getJsonValue("ENABLE_NEW_HOME_SCREEN_LAYOUT_APP", userProfileJson).equalsIgnoreCase("Yes")) {
                walletTxt.setTextColor(dayanamicSelColor);
                walletImg.setColorFilter(dayanamicSelColor, android.graphics.PorterDuff.Mode.SRC_IN);

            }
        } else {
            walletTxt.setTextColor(deSelectedColor);
            walletImg.setColorFilter(deSelectedGetColor, android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if (selTextView.getId() == profileTxt.getId()) {
            profileTxt.setTextColor(appThemeColor1);
            profileImg.setColorFilter(appThemeGetColor1, android.graphics.PorterDuff.Mode.SRC_IN);
            if (generalFunc.getJsonValue("ENABLE_NEW_HOME_SCREEN_LAYOUT_APP", userProfileJson) != null && generalFunc.getJsonValue("ENABLE_NEW_HOME_SCREEN_LAYOUT_APP", userProfileJson).equalsIgnoreCase("Yes")) {
                profileTxt.setTextColor(dayanamicSelColor);
                profileImg.setColorFilter(dayanamicSelColor, android.graphics.PorterDuff.Mode.SRC_IN);

            }
        } else {
            profileTxt.setTextColor(deSelectedColor);
            profileImg.setColorFilter(deSelectedGetColor, android.graphics.PorterDuff.Mode.SRC_IN);
        }

    }

    boolean isHomeFrg = true;
    boolean isWalletfrg = false;
    boolean isProfilefrg = false;
    boolean isBookingfrg = false;

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Bundle bn = new Bundle();
            isHomeFrg = false;
            isWalletfrg = false;
            isProfilefrg = false;
            isBookingfrg = false;

            if (i == walletArea.getId()) {

                isWalletfrg = true;
                manageBottomMenu(walletTxt);
                openWalletFragment();

            } else if (i == profileArea.getId()) {
                isProfilefrg = true;
                manageBottomMenu(profileTxt);
                openProfileFragment();
            } else if (i == historyArea.getId()) {
                isBookingfrg = true;
                manageBottomMenu(historyTxt);
                openHistoryFragment();

            } else if (i == homeArea.getId()) {
                isHomeFrg = true;
                manageBottomMenu(homeTxt);
                if (generalFunc.prefHasKey(Utils.isMultiTrackRunning) && generalFunc.retrieveValue(Utils.isMultiTrackRunning).equalsIgnoreCase("Yes")) {
                    MyApp.getInstance().restartWithGetDataApp();
                } else {
                    openHomeFragment();
                }

            }
        }


    }

    @Override
    public void onBackPressed() {

        if (generalFunc.getJsonValue("ENABLE_NEW_HOME_SCREEN_LAYOUT_APP",userProfileJson) != null &&generalFunc.getJsonValue("ENABLE_NEW_HOME_SCREEN_LAYOUT_APP",userProfileJson).equalsIgnoreCase("Yes")) {

            if (homeDaynamicFragment != null) {
                if (homeDaynamicFragment.isLoading) {
                    return;
                }
                if (homeDaynamicFragment.CAT_TYPE_MODE.equals("1") && generalFunc.getJsonValue(Utils.UBERX_PARENT_CAT_ID, userProfileJson).equalsIgnoreCase("0")) {
                    homeDaynamicFragment.multiServiceSelect.clear();
                    homeDaynamicFragment.backImgView.setVisibility(View.GONE);
                    homeDaynamicFragment.manageToolBarAddressView(false);
                    homeDaynamicFragment.uberXHomeActivity.rduTopArea.setVisibility(View.VISIBLE);
                    //homeDaynamicFragment.MainLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                 //   homeDaynamicFragment.MainArea.setBackgroundColor(Color.parseColor("#FFFFFF"));
                   // homeDaynamicFragment.selectServiceTxt.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    homeDaynamicFragment.configCategoryView();
                    return;
                }
            }
        }
        else
        {
            if (homeFragment != null) {
                if (homeFragment.isLoading) {
                    return;
                }
                if (homeFragment.CAT_TYPE_MODE.equals("1") && generalFunc.getJsonValue(Utils.UBERX_PARENT_CAT_ID, userProfileJson).equalsIgnoreCase("0")) {
                    homeFragment.multiServiceSelect.clear();
                    homeFragment.backImgView.setVisibility(View.GONE);
                    homeFragment.manageToolBarAddressView(false);
                    homeFragment.uberXHomeActivity.rduTopArea.setVisibility(View.VISIBLE);
                    homeFragment.MainLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    homeFragment.MainArea.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    homeFragment.bannerArea.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    homeFragment.selectServiceTxt.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    homeFragment.configCategoryView();
                    return;
                }
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (homeFragment != null && isHomeFrg) {
            homeFragment.onActivityResult(requestCode, resultCode, data);
        }
        else if(homeDaynamicFragment!=null && isHomeFrg)
        {
            homeDaynamicFragment.onActivityResult(requestCode, resultCode, data);
        }
        else if (myWalletFragment != null && isWalletfrg) {
            myWalletFragment.onActivityResult(requestCode, resultCode, data);
        } else if (myProfileFragment != null && isProfilefrg) {
            myProfileFragment.onActivityResult(requestCode, resultCode, data);
        } else if (myBookingFragment != null && isBookingfrg) {
            myBookingFragment.onActivityResult(requestCode, resultCode, data);
        }

    }

    public Context getActContext() {
        return UberXHomeActivity.this;
    }

}
