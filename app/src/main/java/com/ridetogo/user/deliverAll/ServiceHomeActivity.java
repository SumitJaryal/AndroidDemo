package com.ridetogo.user.deliverAll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.adapter.files.deliverAll.FoodDeliveryHomeAdapter;
import com.adapter.files.deliverAll.ServiceHomeAdapter;
import com.andremion.counterfab.CounterFab;
import com.ridetogo.user.R;
import com.fragments.MyBookingFragment;
import com.fragments.MyProfileFragment;
import com.fragments.MyWalletFragment;
import com.fragments.deliverall.FoodDeliveryHomeFragment;
import com.fragments.deliverall.RestaurantAllDetailsNewFragment;
import com.general.files.AddBottomBar;
import com.general.files.AppFunctions;
import com.general.files.DividerItemDecoration;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.IOnBackPressed;
import com.general.files.InternetConnection;
import com.general.files.MyApp;
import com.general.files.OpenAdvertisementDialog;
import com.general.files.StartActProcess;
import com.general.files.UpdateFrequentTask;
import com.general.files.showTermsDialog;
import com.realmModel.Cart;
import com.utils.Logger;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.LoopingCirclePageIndicator;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;

public class ServiceHomeActivity extends AppCompatActivity implements ServiceHomeAdapter.OnItemClickList, UpdateFrequentTask.OnTaskRunCalled,
        ViewPager.OnPageChangeListener {


    public String userProfileJson = "";
    public GeneralFunctions generalFunc;
    ServiceHomeAdapter serviceHomeAdapter;
    ArrayList<HashMap<String, String>> generalCategoryList;
    RecyclerView serviceListRecyclerView;
    DividerItemDecoration itemDecoration;
    MTextView headerTxt;
    MTextView titleTxt;
    JSONObject obj_userProfile;

    LinearLayout errorViewArea;
    ErrorView errorView;
    InternetConnection internetConnection;
    int position = -1;
    boolean isClicked = false;
    ImageView headerLogo;
    RelativeLayout headerArea;

    ViewPager bannerViewPager;
    LoopingCirclePageIndicator bannerCirclePageIndicator;
    FoodDeliveryHomeAdapter bannerAdapter;
    UpdateFrequentTask updateBannerChangeFreqTask;
    int BANNER_AUTO_ROTATE_INTERVAL = 4000;
    int currentBannerPosition = 0;
    LinearLayout bottomMenuArea;
    RelativeLayout cartArea;
    CounterFab fabcartIcon;
    AddBottomBar addBottomBar;
    FrameLayout container;
    private static final int SEL_CARD = 004;
    public static final int TRANSFER_MONEY = 87;
    boolean isFoodOnly = true;
    public String latitude = "0.0";
    public String longitude = "0.0";
    private RealmResults<Cart> realmCartList;
    private String cartDataServiceID="";
    boolean click = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_home);
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        internetConnection = new InternetConnection(getActContext());
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);

//        if (Utils.checkText(generalFunc.getMemberId())) {
        addBottomBar = new AddBottomBar(getActContext(), generalFunc.getJsonObject(userProfileJson));
//        }
        String advertise_banner_data = generalFunc.getJsonValue("advertise_banner_data", userProfileJson);
        if (advertise_banner_data != null && !advertise_banner_data.equalsIgnoreCase("")) {

            if (generalFunc.getJsonValue("image_url", advertise_banner_data) != null && !generalFunc.getJsonValue("image_url", advertise_banner_data).equalsIgnoreCase("")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image_url", generalFunc.getJsonValue("image_url", advertise_banner_data));
                map.put("tRedirectUrl", generalFunc.getJsonValue("tRedirectUrl", advertise_banner_data));
                map.put("vImageWidth", generalFunc.getJsonValue("vImageWidth", advertise_banner_data));
                map.put("vImageHeight", generalFunc.getJsonValue("vImageHeight", advertise_banner_data));
                new OpenAdvertisementDialog(getActContext(), map, generalFunc);
            }
        }

        container = (FrameLayout) findViewById(R.id.container);
        bottomMenuArea = (LinearLayout) findViewById(R.id.bottomMenuArea);
        cartArea = (RelativeLayout) findViewById(R.id.cartArea);
        fabcartIcon = (CounterFab) findViewById(R.id.fabcartIcon);
        fabcartIcon.setOnClickListener(new setOnClickList());
        headerLogo = (ImageView) findViewById(R.id.headerLogo);
        headerTxt = (MTextView) findViewById(R.id.headerTxt);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);

        headerArea = (RelativeLayout) findViewById(R.id.headerArea);
        headerArea.setVisibility(View.VISIBLE);
        if (getIntent().hasExtra("showBackBtn")) {

            bottomMenuArea.setVisibility(View.GONE);
            findViewById(R.id.backImgView).setVisibility(View.VISIBLE);
            findViewById(R.id.backImgView).setOnClickListener(new setOnClickList());
            headerLogo.setVisibility(View.GONE);
            titleTxt.setVisibility(View.VISIBLE);
            titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVER_ALL_APP_DELIVERY"));
            headerArea.setVisibility(View.GONE);

        } else {
            bottomMenuArea.setVisibility(View.VISIBLE);

            findViewById(R.id.backImgView).setVisibility(View.GONE);


            headerArea.setVisibility(View.VISIBLE);
            headerLogo.setVisibility(View.VISIBLE);
            titleTxt.setVisibility(View.GONE);
        }

        itemDecoration = new DividerItemDecoration(getActContext(), DividerItemDecoration.VERTICAL_LIST);
        serviceListRecyclerView = (RecyclerView) findViewById(R.id.serviceListRecyclerView);

        errorViewArea = (LinearLayout) findViewById(R.id.errorViewArea);
        errorView = (ErrorView) findViewById(R.id.errorView);


        isFoodOnly = getIntent().getBooleanExtra("isfoodOnly", false);
        //temp set always true
     //   isFoodOnly =true;

        if (isFoodOnly) {

            manageHome();
        } else {
            generalCategoryList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("servicedata");
            serviceHomeAdapter = new ServiceHomeAdapter(getActContext(), generalCategoryList, generalFunc);
//            serviceHomeAdapter.setrealmCartList(realmCartList);
            serviceHomeAdapter.setOnItemClickList(ServiceHomeActivity.this);
            serviceListRecyclerView.setAdapter(serviceHomeAdapter);
            serviceHomeAdapter.notifyDataSetChanged();

            bannerViewPager = (ViewPager) findViewById(R.id.bannerViewPager);
            bannerCirclePageIndicator = (LoopingCirclePageIndicator) findViewById(R.id.bannerCirclePageIndicator);
        }


    }


    MyProfileFragment myProfileFragment;
    MyWalletFragment myWalletFragment;
    public MyBookingFragment myBookingFragment;
    FoodDeliveryHomeFragment foodDeliveryHomeFragment;
    RestaurantAllDetailsNewFragment restaurantAllDetailsNewFragment;

    boolean isProfilefrg = false;
    boolean isWalletfrg = false;
    boolean isBookingfrg = false;

    public void openProfileFragment() {
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



    public void openHistoryFragment() {

        isProfilefrg = false;
        isWalletfrg = false;
        isBookingfrg = false;

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


    public void openWalletFragment() {
        isProfilefrg = false;
        isWalletfrg = true;
        isBookingfrg = true;

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


    public void manageHome() {
        String eShowTerms=generalFunc.getJsonValue("eShowTerms",userProfileJson);
        if (Utils.checkText(eShowTerms) && eShowTerms.equalsIgnoreCase("yes")) {
            new showTermsDialog(getActContext(), generalFunc, position, generalFunc.getJsonValue("vCategory", userProfileJson), click, new showTermsDialog.OnClickList() {
                @Override
                public void onClick() {
                    redirectToHome();
                }
            },true);
        }
        redirectToHome();
    }

    private void redirectToHome() {
        isProfilefrg = false;
        isWalletfrg = false;
        isBookingfrg = false;
        container.setVisibility(View.GONE);


        if (isFoodOnly) {

            if (generalFunc.retrieveValue("CHECK_SYSTEM_STORE_SELECTION").equalsIgnoreCase("Yes")) {
                container.setVisibility(View.VISIBLE);
                if (restaurantAllDetailsNewFragment == null) {
                    restaurantAllDetailsNewFragment = new RestaurantAllDetailsNewFragment();
                }

                Bundle bn = new Bundle();
                bn.putBoolean("isBack", false);
                bn.putString("iCompanyId", getIntent().getStringExtra("iCompanyId"));
                restaurantAllDetailsNewFragment.setArguments(bn);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, restaurantAllDetailsNewFragment).commit();


            } else {


                container.setVisibility(View.VISIBLE);
                    foodDeliveryHomeFragment = new FoodDeliveryHomeFragment();

                Bundle bn = new Bundle();
                bn.putBoolean("isBackHide", true);
                bn.putBoolean("isback", true);

                foodDeliveryHomeFragment.setArguments(bn);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, foodDeliveryHomeFragment).commit();

            }


        }
    }


    public void setUserInfo() {
        View view = ((Activity) getActContext()).findViewById(android.R.id.content);
        ((MTextView) view.findViewById(R.id.userNameTxt)).setText(generalFunc.getJsonValueStr("vName", obj_userProfile) + " "
                + generalFunc.getJsonValueStr("vLastName", obj_userProfile));
        ((MTextView) view.findViewById(R.id.walletbalncetxt)).setText(generalFunc.retrieveLangLBl("", "LBL_WALLET_BALANCE") + ": " + generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("user_available_balance", obj_userProfile)));

        (new AppFunctions(getActContext())).checkProfileImage((SelectableRoundedImageView) view.findViewById(R.id.userImgView), userProfileJson, "vImgName");
    }


    void getLanguageLabelServiceWise(int position) {
        generateErrorView();

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getUserLanguagesAsPerServiceType");
        parameters.put("LanguageCode", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));
        parameters.put("eSystem", Utils.eSystem_Type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {
                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail) {
                        HashMap<String, String> storeData = new HashMap<>();
                        storeData.put(Utils.languageLabelsKey, generalFunc.getJsonValue(Utils.message_str, responseString));
                        Logger.d("MESSAGE_SRVCHOME",generalFunc.getJsonValue(Utils.message_str, responseString));
                        storeData.put(Utils.LANGUAGE_CODE_KEY, generalFunc.getJsonValue("vLanguageCode", responseString));
                        storeData.put(Utils.LANGUAGE_IS_RTL_KEY, generalFunc.getJsonValue("langType", responseString));

                        storeData.put(Utils.GOOGLE_MAP_LANGUAGE_CODE_KEY, generalFunc.getJsonValue("vGMapLangCode", responseString));

                        generalFunc.storeData(storeData);

                        GeneralFunctions.clearAndResetLanguageLabelsData(MyApp.getInstance().getApplicationContext());

                        Utils.setAppLocal(getActContext());

                        Bundle bn = new Bundle();
                        bn.putBoolean("isback", true);
                        resetData();

                        if (generalFunc.retrieveValue("CHECK_SYSTEM_STORE_SELECTION").equalsIgnoreCase("Yes")) {


                            bn.putString("iCompanyId", generalCategoryList.get(position).get("iCompanyId") );
                            bn.putString("Restaurant_Status", "");
                            bn.putString("ispriceshow", generalCategoryList.get(position).get("ispriceshow"));
                            bn.putString("eAvailable", generalCategoryList.get(position).get("eAvailable"));
                            bn.putString("timeslotavailable", generalCategoryList.get(position).get("timeslotavailable"));
                            bn.putString("lat", "");
                            bn.putString("long", "");

                            new StartActProcess(getActContext()).startActWithData(RestaurantAllDetailsNewActivity.class, bn);


                        } else {
                            new StartActProcess(getActContext()).startActWithData(FoodDeliveryHomeActivity.class, bn);
                        }


                    } else {
                        resetData();
                    }
                } else {
                    resetData();
                }

            }
        });
        exeWebServer.execute();
    }

    private void resetData() {
        isClicked = false;
        position = -1;
    }

    public Context getActContext() {
        return ServiceHomeActivity.this;
    }


    public void generateErrorView() {


        generalFunc.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT");


        if (internetConnection.isNetworkConnected()) {

            errorViewArea.setVisibility(View.GONE);
        } else {
            if (errorViewArea.getVisibility() != View.VISIBLE) {
                errorViewArea.setVisibility(View.VISIBLE);
            }
            errorView.setOnRetryListener(new ErrorView.RetryListener() {
                @Override
                public void onRetry() {

                    getLanguageLabelServiceWise(position);

                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (generalFunc.prefHasKey(Utils.iServiceId_KEY) && generalFunc != null /*&& !generalFunc.isDeliverOnlyEnabled()*/) {

                generalFunc.removeValue(Utils.iServiceId_KEY);
            }

            if (myProfileFragment != null && isProfilefrg) {
                myProfileFragment.onResume();
            }

            if (myWalletFragment != null && isWalletfrg) {
                myWalletFragment.onResume();
            }

            if (myBookingFragment != null && isBookingfrg) {
                myBookingFragment.onResume();
            }


            userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);


            obj_userProfile = generalFunc.getJsonObject(userProfileJson);
            setUserInfo();

        } catch (Exception e) {

        }

        realmCartList = getCartData();
        cartArea.setVisibility(realmCartList!=null&&realmCartList.size()>0?View.VISIBLE:View.GONE);
        int cnt = 0;

        if (realmCartList != null) {
            for (int i = 0; i < realmCartList.size(); i++) {
                cartDataServiceID=realmCartList.get(i).getiServiceId();
                cnt = cnt + GeneralFunctions.parseIntegerValue(0, realmCartList.get(i).getQty());
            }
        }

        fabcartIcon.setCount(cnt);
       /* if (serviceHomeAdapter!= null)
        {
            serviceHomeAdapter.setrealmCartList(realmCartList);
            serviceHomeAdapter.notifyDataSetChanged();
        }*/
    }

    public RealmResults<Cart> getCartData() {
        try {
            Realm realm = MyApp.getRealmInstance();
            return realm.where(Cart.class).findAll();
        } catch (Exception e) {

        }
        return null;
    }
    @Override
    public void onBackPressed() {


        if (findViewById(R.id.backImgView).getVisibility() == View.VISIBLE) {
            if (generalFunc.prefHasKey(Utils.iServiceId_KEY) && generalFunc != null) {
                generalFunc.removeValue(Utils.iServiceId_KEY);
            }
        }

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onFragmentBackPressed()) {
            super.onBackPressed();
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActContext());
            int i = view.getId();
            if (i == R.id.backImgView) {
                onBackPressed();
            } if (i == R.id.fabcartIcon) {
                if (!generalFunc.retrieveValue(Utils.iServiceId_KEY).equalsIgnoreCase(cartDataServiceID)) {
                    generalFunc.storeData(Utils.iServiceId_KEY, cartDataServiceID);
                    getLanguageLabelServiceWise();
                } else {

                    new StartActProcess(getActContext()).startAct(EditCartActivity.class);
                }
            }
        }
    }

    private void getLanguageLabelServiceWise() {
        {
            generateErrorView();

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("type", "getUserLanguagesAsPerServiceType");
            parameters.put("LanguageCode", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));
            parameters.put("eSystem", Utils.eSystem_Type);

            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
            exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
            exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
                @Override
                public void setResponse(String responseString) {
                    if (responseString != null && !responseString.equals("")) {

                        boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                        if (isDataAvail) {
                            HashMap<String, String> storeData = new HashMap<>();
                            storeData.put(Utils.languageLabelsKey, generalFunc.getJsonValue(Utils.message_str, responseString));
                            Logger.d("MESSAGE_SRVCHOME",generalFunc.getJsonValue(Utils.message_str, responseString));
                            storeData.put(Utils.LANGUAGE_CODE_KEY, generalFunc.getJsonValue("vLanguageCode", responseString));
                            storeData.put(Utils.LANGUAGE_IS_RTL_KEY, generalFunc.getJsonValue("langType", responseString));

                            storeData.put(Utils.GOOGLE_MAP_LANGUAGE_CODE_KEY, generalFunc.getJsonValue("vGMapLangCode", responseString));

                            generalFunc.storeData(storeData);

                            GeneralFunctions.clearAndResetLanguageLabelsData(MyApp.getInstance().getApplicationContext());

                            Utils.setAppLocal(getActContext());

                            Bundle bn = new Bundle();
                            bn.putBoolean("isback", true);
//                            resetData();

                            new StartActProcess(getActContext()).startAct(EditCartActivity.class);

                        } else {
//                            resetData();
                        }
                    } else {
//                        resetData();
                    }

                }
            });
            exeWebServer.execute();
        }
    }

    @Override
    public void onItemClick(int position) {
        if (!isClicked && this.position != position) {
            isClicked = true;
            this.position = position;
            generalFunc.storeData(Utils.iServiceId_KEY, generalCategoryList.get(position).get("iServiceId"));
            getLanguageLabelServiceWise(position);
        }
    }

    @Override
    public void redirectToCheckout() {
        new StartActProcess(getActContext()).startAct(EditCartActivity.class);
    }

    @Override
    public void onTaskRun() {
        if (currentBannerPosition < (bannerAdapter.getCount() - 1)) {
            bannerViewPager.setCurrentItem((bannerViewPager.getCurrentItem() + 1), true);
        } else {
            bannerViewPager.setCurrentItem(0, true);

        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentBannerPosition = position;

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(restaurantAllDetailsNewFragment != null) {
            restaurantAllDetailsNewFragment.onActivityResult(requestCode,resultCode,data);
        }
        if(foodDeliveryHomeFragment != null) {
            foodDeliveryHomeFragment.onActivityResult(requestCode,resultCode,data);
        }

        if (requestCode == Utils.MY_PROFILE_REQ_CODE && resultCode == RESULT_OK && data != null) {
            userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
            if (myProfileFragment != null) {
                myProfileFragment.onActivityResult(requestCode, resultCode, data);
            }
        } else if (resultCode == RESULT_OK && requestCode == SEL_CARD) {
            if (myWalletFragment != null) {
                myWalletFragment.onActivityResult(requestCode, resultCode, data);
            }
        } else if (resultCode == RESULT_OK && requestCode == TRANSFER_MONEY) {
            if (myWalletFragment != null) {
                myWalletFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }



}
