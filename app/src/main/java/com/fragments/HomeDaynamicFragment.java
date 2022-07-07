package com.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.viewpager.widget.ViewPager;

import com.ViewPagerCards.CardPagerAdapter;
import com.ViewPagerCards.ShadowTransformer;
import com.adapter.files.DaynamicCategoryAdapter;
import com.adapter.files.UberXBannerPagerAdapter;
import com.adapter.files.UberXCategoryAdapter;
import com.ridetogo.user.MainActivity;
import com.ridetogo.user.R;
import com.ridetogo.user.SearchLocationActivity;
import com.ridetogo.user.UberXHomeActivity;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.general.files.AppFunctions;
import com.general.files.DividerItemDecoration;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.GetAddressFromLocation;
import com.general.files.GetLocationUpdates;
import com.general.files.InternetConnection;
import com.general.files.MyApp;
import com.general.files.OpenCatType;
import com.general.files.OpenNoLocationView;
import com.general.files.StartActProcess;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.skyfishjy.library.RippleBackground;
import com.utils.Logger;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class HomeDaynamicFragment extends Fragment implements UberXCategoryAdapter.OnItemClickList, ViewPager.OnPageChangeListener
        , GetAddressFromLocation.AddressFound, GetLocationUpdates.LocationUpdates, DaynamicCategoryAdapter.OnCategorySelectListener {

    View view;

    public String userProfileJson = "";
    public String UBERX_PARENT_CAT_ID = "";
    public String APP_TYPE = "";
    public JSONObject obj_userProfile;
    RecyclerView dataListRecyclerView;
    RecyclerView mainCategoryRecyclerView;
    //public View bannerArea;

    ProgressBar serviceLoadingProgressBar;

    // ViewPager bannerViewPager;
    UberXBannerPagerAdapter bannerAdapter;
    GeneralFunctions generalFunc;
    //AddDrawer addDrawer;
    public MTextView headerLocAddressTxt, LocStaticTxt, selectServiceTxt, headerLocAddressHomeTxt;
    HashMap<String, String> generalCategoryIconTypeDataMap = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> generalCategoryList = new ArrayList<>();
    ArrayList<HashMap<String, String>> generalMainCategoryList = new ArrayList<>();
    ArrayList<ArrayList<HashMap<String, String>>> generalCategoryWiseList;
    ArrayList<ArrayList<HashMap<String, String>>> generalCategoryWiseMoreList;
    ArrayList<HashMap<String, String>> mainCategoryList = new ArrayList<>();
    ArrayList<HashMap<String, String>> subCategoryList = new ArrayList<>();

    ArrayList<HashMap<String, String>> allMainCategoryList = new ArrayList<>();

    UberXCategoryAdapter ufxCatAdapter;
    public String CAT_TYPE_MODE = "0";
    public String latitude = "0.0";
    public String longitude = "0.0";

    // int currentBannerPosition = 0;
    public ImageView backImgView;
    String address;
    public GetLocationUpdates getLastLocation;
    public boolean isUfxaddress = false;
    GetAddressFromLocation getAddressFromLocation;
    boolean isback = false;
    public LinearLayout btnArea, MainLayout, MainArea;
    LinearLayout uberXHeaderLayout;
    InternetConnection intCheck;

    DividerItemDecoration itemDecoration;
    MButton btn_type2;
    int submitBtnId;
    public String vParentCategoryName = "";
    String parentId = "";
    boolean isbanner = false;
    RelativeLayout imagArea;

    public ArrayList<String> multiServiceSelect = new ArrayList<>();

    // private CardPagerAdapter mCardAdapter;
    //   private ShadowTransformer mCardShadowTransformer;
    //   private CardFragmentPagerAdapter mFragmentCardAdapter;


    AppBarLayout appBarLayout, appBarLayoutMain;
    public UberXHomeActivity uberXHomeActivity;
    String MORE_ICON = "";
    int noColumns;
    int color = Color.parseColor("#f3f3f3");
    int color1 = Color.parseColor("#f1f1f1");
    public boolean isLoading = false;
    SelectableRoundedImageView userImgView;
    ImageView backUserImg;
    View selectServiceArea, ourServiceArea;
    MTextView userNameTxt, userSinceTxt;
    DaynamicCategoryAdapter daynamicCategoryAdapter;
    boolean isAppType;
    public MTextView ourServiceTxt, moreTxt;
    private SkeletonScreen skeletonScreen;
    ImageView arrowImg;
    public View bannerBg, bannerArea;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    ViewPager bannerViewPager;
    View subCategoryToolArea, subCategoryToolHomeArea, masterArea;
    ImageView toproundImg, bottomRoundfImg;
    boolean isBannerView = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        if (view != null) {
//            return view;
//        }
        view = inflater.inflate(R.layout.activity_daynamic_uber_x, container, false);


        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        uberXHomeActivity = (UberXHomeActivity) getActivity();


        imagArea = view.findViewById(R.id.imagArea);
        toproundImg = view.findViewById(R.id.toproundImg);
        bottomRoundfImg = view.findViewById(R.id.bottomRoundfImg);
        if (generalFunc.isRTLmode()) {
            toproundImg.setRotation(180);
            bottomRoundfImg.setRotation(180);
        }
        subCategoryToolArea = view.findViewById(R.id.subCategoryToolArea);
        subCategoryToolHomeArea = view.findViewById(R.id.subCategoryToolHomeArea);
        masterArea = view.findViewById(R.id.masterArea);
        arrowImg = view.findViewById(R.id.arrowImg);
        bannerArea = view.findViewById(R.id.bannerArea);
        bannerBg = view.findViewById(R.id.bannerBg);
        bannerViewPager = (ViewPager) view.findViewById(R.id.bannerViewPager);
        userImgView = view.findViewById(R.id.userImgView);
        backUserImg = view.findViewById(R.id.backUserImg);
        userNameTxt = view.findViewById(R.id.userNameTxt);
        userSinceTxt = view.findViewById(R.id.userSinceTxt);
        ourServiceTxt = (MTextView) view.findViewById(R.id.ourServiceTxt);
        moreTxt = (MTextView) view.findViewById(R.id.moreTxt);
        moreTxt.setOnClickListener(new setOnClickLst());
        arrowImg.setOnClickListener(new setOnClickLst());

        selectServiceArea = view.findViewById(R.id.selectServiceArea);
        ourServiceArea = view.findViewById(R.id.ourServiceArea);
        if (generalFunc.isRTLmode()) {
            arrowImg.setRotation(180);
        }

        isUfxaddress = false;

        setUserProfileData("", false);

        if (generalFunc.getJsonValue("HOME_SCREEN_DESIGN_HEADER_VIEW", userProfileJson) != null &&
                generalFunc.getJsonValue("HOME_SCREEN_DESIGN_HEADER_VIEW", userProfileJson).equalsIgnoreCase("Banner")) {
            isBannerView = true;
            bannerArea.setVisibility(View.VISIBLE);
            bottomRoundfImg.setVisibility(View.GONE);
            toproundImg.setVisibility(View.GONE);
            userNameTxt.setVisibility(View.INVISIBLE);
            userSinceTxt.setVisibility(View.INVISIBLE);

            userImgView.setVisibility(View.INVISIBLE);
            backUserImg.setVisibility(View.INVISIBLE);
            imagArea.setVisibility(View.INVISIBLE);
        } else {
            isBannerView = false;
            bannerArea.setVisibility(View.GONE);
            bottomRoundfImg.setVisibility(View.VISIBLE);
            toproundImg.setVisibility(View.VISIBLE);
            userNameTxt.setVisibility(View.VISIBLE);
            userSinceTxt.setVisibility(View.VISIBLE);

        }
        btn_type2 = ((MaterialRippleLayout) view.findViewById(R.id.btn_type2)).getChildView();
        appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar_layout);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        View toolBarHeaderView = (LinearLayout) view.findViewById(R.id.toolBarHeaderView);
        appBarLayoutMain = (AppBarLayout) view.findViewById(R.id.appBarLayout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayoutMain.setOutlineProvider(null);
        }

        //  CollapsingToolbarLayout ctl = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        //  ctl.setTitle("Best Coupons Deals");


        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);

        btn_type2.setOnClickListener(new setOnClickList());
        (new AppFunctions(getActContext())).checkProfileImage(userImgView, userProfileJson, "vImgName");

        initializeLocation();

        itemDecoration = new DividerItemDecoration(getActContext(), DividerItemDecoration.VERTICAL_LIST);

        intCheck = new InternetConnection(getActContext());
        try {
            address = getArguments().getString("uberXAddress");
            latitude = getArguments().getDouble("uberXlat", 0.0) + "";
            longitude = getArguments().getDouble("uberXlong", 0.0) + "";

            isback = getArguments().getBoolean("isback", false);
        } catch (Exception e) {
        }


        btnArea = view.findViewById(R.id.btnArea);
        MainLayout = view.findViewById(R.id.MainLayout);
        MainArea = view.findViewById(R.id.MainArea);


        serviceLoadingProgressBar = view.findViewById(R.id.serviceLoadingProgressBar);


        uberXHeaderLayout = view.findViewById(R.id.uberXHeaderLayout);
        selectServiceTxt = (MTextView) view.findViewById(R.id.selectServiceTxt);


        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_NEXT_TXT"));


        dataListRecyclerView = (RecyclerView) view.findViewById(R.id.dataListRecyclerView);
        //  dataListRecyclerView.setBackgroundColor(getResources().getColor(R.color.white));
        mainCategoryRecyclerView = (RecyclerView) view.findViewById(R.id.mainCategoryRecyclerView);


        boolean isRideDeliveryUberx = APP_TYPE.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX);
        if (isRideDeliveryUberx ||
                APP_TYPE.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
            if (isRideDeliveryUberx) {
                uberXHeaderLayout.setVisibility(View.VISIBLE);
            }

            if (UBERX_PARENT_CAT_ID.equalsIgnoreCase("0")) {
                btnArea.setVisibility(View.GONE);
            } else {
                btnArea.setVisibility(View.VISIBLE);
            }

            //selectServiceTxt.setVisibility(View.GONE);
        } else {


            btnArea.setVisibility(View.VISIBLE);

            uberXHeaderLayout.setVisibility(View.VISIBLE);
            // selectServiceTxt.setVisibility(View.VISIBLE);


        }
        headerLocAddressTxt = (MTextView) view.findViewById(R.id.headerLocAddressTxt);
        headerLocAddressHomeTxt = (MTextView) view.findViewById(R.id.headerLocAddressHomeTxt);
        headerLocAddressHomeTxt.setOnClickListener(new setOnClickLst());
//        Drawable drawable;
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            drawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_place_address, getActContext().getTheme());
//        } else {
//            drawable = AppCompatResources.getDrawable(getActContext(), R.drawable.ic_place_address);
//        }
//
//
//        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
//        Drawable mutableDrawable = wrappedDrawable.mutate();
//        DrawableCompat.setTint(mutableDrawable, ContextCompat.getColor(getActContext(), R.color.white));
//
//        Drawable arrowdrawable;
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            arrowdrawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_down_arrow_header, getActContext().getTheme());
//        } else {
//            arrowdrawable = AppCompatResources.getDrawable(getActContext(), R.drawable.ic_down_arrow_header);
//        }
//
//
//        Drawable arrowwrappedDrawable = DrawableCompat.wrap(arrowdrawable);
//        Drawable arrowmutableDrawable = arrowwrappedDrawable.mutate();
//        DrawableCompat.setTint(arrowmutableDrawable, ContextCompat.getColor(getActContext(), R.color.white));
//
//        if (generalFunc.isRTLmode()) {
//            headerLocAddressTxt.setCompoundDrawablesWithIntrinsicBounds(arrowmutableDrawable, null, mutableDrawable, null);
//           headerLocAddressHomeTxt.setCompoundDrawablesWithIntrinsicBounds(arrowmutableDrawable, null, mutableDrawable, null);
//        } else {
//            headerLocAddressTxt.setCompoundDrawablesWithIntrinsicBounds(mutableDrawable, null, arrowmutableDrawable, null);
//            headerLocAddressHomeTxt.setCompoundDrawablesWithIntrinsicBounds(mutableDrawable, null, arrowmutableDrawable, null);
//        }

        manageHedaer(headerLocAddressHomeTxt);


        LocStaticTxt = (MTextView) view.findViewById(R.id.LocStaticTxt);
        LocStaticTxt.setVisibility(View.GONE);

        backImgView = (ImageView) view.findViewById(R.id.backImgView);


        backImgView.setOnClickListener(new setOnClickLst());


        if (isback) {

            backImgView.setVisibility(View.VISIBLE);
            manageToolBarAddressView(true);
        }

        //addDrawer = new AddDrawer(getActContext(), userProfileJson, isback);

        ufxCatAdapter = new UberXCategoryAdapter(getActContext(), generalCategoryList, generalFunc);

        if (UBERX_PARENT_CAT_ID.equalsIgnoreCase("0")) {
            CAT_TYPE_MODE = "0";
            noColumns = getNumOfColumns();
            setParentCategoryLayoutManager();
            btnArea.setVisibility(View.GONE);
        } else {
            dataListRecyclerView.setLayoutManager(new LinearLayoutManager(getActContext()));

            CAT_TYPE_MODE = "1";
        }
        dataListRecyclerView.setAdapter(ufxCatAdapter);

        uberXHeaderLayout.setOnClickListener(new setOnClickLst());


        setData();

        if (!APP_TYPE.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {
            // getBanners();
        }

        mainCategoryList.clear();
        subCategoryList.clear();
        Logger.e("geCtaegory", "::oncreatecalled");
        selPos = 0;
        getCategory(UBERX_PARENT_CAT_ID, CAT_TYPE_MODE);

        String OPEN_CHAT = generalFunc.retrieveValue("OPEN_CHAT");
        if (Utils.checkText(OPEN_CHAT)) {
            JSONObject OPEN_CHAT_DATA_OBJ = generalFunc.getJsonObject(OPEN_CHAT);
            generalFunc.removeValue("OPEN_CHAT");
//            if (OPEN_CHAT_DATA_OBJ != null) {
//                new StartActProcess(getActContext()).startActWithData(ChatActivity.class, generalFunc.createChatBundle(OPEN_CHAT_DATA_OBJ));
//            }
        }
        skeletonScreen = Skeleton.bind(MainLayout)
                .load(R.layout.daynamic_home_shimmer_view)
                .duration(1000)
                .color(R.color.shimmer_color)
                .angle(0)
                .show();
        MyHandler myHandler = new MyHandler(this);
        myHandler.sendEmptyMessageDelayed(1, 3000);

        return view;
    }

    public static class MyHandler extends android.os.Handler {
        private final WeakReference<HomeDaynamicFragment> activityWeakReference;


        MyHandler(HomeDaynamicFragment activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (activityWeakReference.get() != null) {
                activityWeakReference.get().skeletonScreen.hide();
            }
        }
    }

    private void setUserProfileData(String userProfileData, boolean isSet) {
        userProfileJson = isSet ? userProfileData : generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        obj_userProfile = generalFunc.getJsonObject(userProfileJson);
        UBERX_PARENT_CAT_ID = generalFunc.getJsonValueStr(Utils.UBERX_PARENT_CAT_ID, obj_userProfile);
        APP_TYPE = generalFunc.getJsonValueStr("APP_TYPE", obj_userProfile);

    }

    public void initializeLocation() {
        stopLocationUpdates();
        GetLocationUpdates.locationResolutionAsked = false;
        getLastLocation = new GetLocationUpdates(getActContext(), Utils.LOCATION_UPDATE_MIN_DISTANCE_IN_MITERS, true, this);
    }

    public void stopLocationUpdates() {
        if (getLastLocation != null) {
            getLastLocation.stopLocationUpdates();
        }
    }

    private void setParentCategoryLayoutManager() {
        GridLayoutManager gridLay = new GridLayoutManager(getActContext(), noColumns);
        gridLay.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (generalCategoryIconTypeDataMap.get("" + position) != null && !generalCategoryIconTypeDataMap.get("" + position).equalsIgnoreCase("ICON")) {
                    if (noColumns > gridLay.getSpanCount()) {
                        return gridLay.getSpanCount();
                    } else {
                        return noColumns;

                    }
                }
                return 1;
            }
        });
        dataListRecyclerView.setLayoutManager(gridLay);
    }

    private void setData() {
        headerLocAddressTxt.setHint(generalFunc.retrieveLangLBl("Enter Location...", "LBL_ENTER_LOC_HINT_TXT"));
        headerLocAddressHomeTxt.setHint(generalFunc.retrieveLangLBl("Enter Location...", "LBL_ENTER_LOC_HINT_TXT"));
        LocStaticTxt.setText(generalFunc.retrieveLangLBl("Location For availing Service", "LBL_LOCATION_FOR_AVAILING_TXT"));
        // selectServiceTxt.setText(generalFunc.retrieveLangLBl("Select Service", "LBL_RDU_SERVICES_TITLE"));
        selectServiceTxt.setText(generalFunc.retrieveLangLBl("Select Service", "LBL_SELECT_SERVICE_TXT"));
        ourServiceTxt.setText(generalFunc.retrieveLangLBl("Our Services", "LBL_OUR_PRODUCTS"));
        moreTxt.setText(generalFunc.retrieveLangLBl("More", "LBL_MORE"));
        if (isback) {
            String address = getArguments().getString("address");
            if (address != null && !address.equalsIgnoreCase("")) {
                headerLocAddressTxt.setText(address);
                headerLocAddressHomeTxt.setText(address);
                latitude = getArguments().getString("lat");
                longitude = getArguments().getString("long");

            }
        }

    }

    public Context getActContext() {
        return MyApp.getInstance().getCurrentAct();
    }

    public void getCategory(String parentId, final String CAT_TYPE_MODE) {

        this.parentId = parentId;
        generalCategoryList.clear();

        if (!CAT_TYPE_MODE.equals("0")) {
            subCategoryList.clear();
            manageToolBarAddressView(true);
            MainLayout.setBackgroundColor(color1);
            MainArea.setBackgroundColor(color1);
            selectServiceTxt.setBackgroundColor(color1);

            int categoryId = generalFunc.parseIntegerValue(0, UBERX_PARENT_CAT_ID);
            Logger.d("CatID", "categoryId" + categoryId);
            if (categoryId > 0) {
                backImgView.setVisibility(View.GONE);
                uberXHomeActivity.rduTopArea.setVisibility(View.VISIBLE);
            } else {
                backImgView.setVisibility(View.VISIBLE);
                uberXHomeActivity.rduTopArea.setVisibility(View.GONE);
            }

        } else {
            backImgView.setVisibility(View.GONE);
            manageToolBarAddressView(false);
            uberXHomeActivity.rduTopArea.setVisibility(View.VISIBLE);
            MainLayout.setBackgroundColor(color);
            MainArea.setBackgroundColor(color);

            selectServiceTxt.setBackgroundColor(color);
        }


        //   serviceLoadingProgressBar.setVisibility(View.VISIBLE);

        if (ufxCatAdapter != null) {
            dataListRecyclerView.getRecycledViewPool().clear();
            ufxCatAdapter.notifyDataSetChanged();
        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getServiceCategories");
        parameters.put("parentId", "" + parentId);
        parameters.put("userId", generalFunc.getMemberId());
//        Logger.d("RESPONSE_TRACKING", "Before Call" + System.currentTimeMillis());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);

        exeWebServer.setCancelAble(false);
        exeWebServer.setLoaderConfig(getActContext(), false, generalFunc);
//        Logger.d("RESPONSE_TRACKING", "Before Response Listener" + System.currentTimeMillis());
        exeWebServer.setDataResponseListener(responseString -> {
//            Logger.d("RESPONSE_TRACKING", "Before Response got" + System.currentTimeMillis());
            JSONObject responseObj = generalFunc.getJsonObject(responseString);
            //   mainCategoryList.clear();
            subCategoryList.clear();
            generalCategoryList.clear();
            if (responseObj != null && !responseObj.equals("")) {

                MORE_ICON = generalFunc.getJsonValueStr("MORE_ICON", responseObj);

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseObj);
                if (this.CAT_TYPE_MODE.equalsIgnoreCase("0")
                        && !CAT_TYPE_MODE.equalsIgnoreCase("0")) {
                    serviceLoadingProgressBar.setVisibility(View.GONE);

                    return;

                }


                if (isDataAvail) {
                    isLoading = false;
                    generalCategoryList.clear();
                    //     mainCategoryList.clear();
                    int GRID_TILES_MAX_COUNT = GeneralFunctions.parseIntegerValue(1, generalFunc.getJsonValueStr("GRID_TILES_MAX_COUNT", obj_userProfile));

                    JSONArray mainCataArray = generalFunc.getJsonArray("message", responseObj);

                    ArrayList<HashMap<String, String>> bannerList = new ArrayList<>();
                    ArrayList<HashMap<String, String>> serviceList = new ArrayList<>();
                    ArrayList<HashMap<String, String>> moreItemList = new ArrayList<>();

                    int gridCount = 0;
                    if (CAT_TYPE_MODE.equals("0")) {
                        allMainCategoryList.clear();
                    }

                    vParentCategoryName = generalFunc.getJsonValueStr("vParentCategoryName", responseObj);

                    int mainCatArraySize = mainCataArray.length();
                    String LBL_BOOK_NOW = generalFunc.retrieveLangLBl("", "LBL_BOOK_NOW");
                    boolean APP_HOME_PAGE_LIST_VIEW_ENABLED = generalFunc.getJsonValue("APP_HOME_PAGE_LIST_VIEW_ENABLED", userProfileJson).equalsIgnoreCase("Yes");
                    isAppType = APP_TYPE.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)
                            || (APP_TYPE.equalsIgnoreCase(Utils.CabGeneralType_UberX));

                    for (int i = 0; i < mainCatArraySize; i++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        JSONObject categoryObj = generalFunc.getJsonObject(mainCataArray, i);
                        map.put("eCatType", generalFunc.getJsonValueStr("eCatType", categoryObj));
                        map.put("iServiceId", generalFunc.getJsonValueStr("iServiceId", categoryObj));
                        map.put("vCategory", generalFunc.getJsonValueStr("vCategory", categoryObj));
                        map.put("vLogo_image", generalFunc.getJsonValueStr("vLogo_image", categoryObj));
                        map.put("iVehicleCategoryId", generalFunc.getJsonValueStr("iVehicleCategoryId", categoryObj));
                        map.put("vCategoryBanner", generalFunc.getJsonValueStr("vCategoryBanner", categoryObj));
                        map.put("vBannerImage", generalFunc.getJsonValueStr("vBannerImage", categoryObj));
                        map.put("tBannerButtonText", generalFunc.getJsonValueStr("tBannerButtonText", categoryObj));
                        String eShowTerms = generalFunc.getJsonValueStr("eShowTerms", categoryObj);
                        map.put("eShowTerms", Utils.checkText(eShowTerms) ? eShowTerms : "");
                        map.put("LBL_BOOK_NOW", LBL_BOOK_NOW);
                        subCategoryList.add((HashMap<String, String>) map.clone());

                    }


                    generalCategoryList.addAll(subCategoryList);


                    //   ufxCatAdapter = null;
                    //  ufxCatAdapter = new UberXCategoryAdapter(getActContext(), generalCategoryList, generalFunc);

                    if (!CAT_TYPE_MODE.equalsIgnoreCase("0")) {
                    } else {
                        dataListRecyclerView.removeItemDecoration(itemDecoration);
                    }

                    // ufxCatAdapter.setCategoryMode(CAT_TYPE_MODE);
                    // dataListRecyclerView.setAdapter(ufxCatAdapter);
                    // dataListRecyclerView.getRecycledViewPool().clear();
                    //  ufxCatAdapter.notifyDataSetChanged();
                    // ufxCatAdapter.setOnItemClickList(this);


                    if (CAT_TYPE_MODE.equals("0")) {
                        CollapsingToolbarLayout.LayoutParams marginLayoutParams = new CollapsingToolbarLayout.LayoutParams(bannerArea.getLayoutParams());

                        marginLayoutParams.setMargins(0, (int) getActContext().getResources().getDimension(R.dimen._30sdp), 0, 0);

                        bannerArea.setLayoutParams(marginLayoutParams);
                        JSONArray masterCategoryArr = generalFunc.getJsonArray("MASTER_SERVICE_CATEGORIES", responseObj);
                        if (masterCategoryArr != null) {
                            generalMainCategoryList.clear();
                            //generalCategoryWiseList.clear();
                            ArrayList<HashMap<String, String>> tempgeneralCategoryWiseList;
                            ArrayList<HashMap<String, String>> tempgeneraliconList;
                            ArrayList<HashMap<String, String>> tempgeneralList;
                            ArrayList<HashMap<String, String>> tempgeneralBannerList;
                            generalCategoryWiseList = new ArrayList<ArrayList<HashMap<String, String>>>();
                            generalCategoryWiseMoreList = new ArrayList<ArrayList<HashMap<String, String>>>();
                            for (int i = 0; i < masterCategoryArr.length(); i++) {
                                tempgeneralBannerList = new ArrayList<>();
                                tempgeneralList = new ArrayList<>();
                                JSONObject obj_temp = generalFunc.getJsonObject(masterCategoryArr, i);
                                HashMap<String, String> map = new HashMap<String, String>();
                                //temp check
                                allMainCategoryList = new ArrayList<>();

                                map.put("eCatType", generalFunc.getJsonValueStr("eCatType", obj_temp));
                                map.put("eType", generalFunc.getJsonValueStr("eType", obj_temp));
                                map.put("vBgImage", generalFunc.getJsonValueStr("vBgImage", obj_temp));
                                map.put("vIconImage", generalFunc.getJsonValueStr("vIconImage", obj_temp));
                                map.put("vTextColor", generalFunc.getJsonValueStr("vTextColor", obj_temp));
                                map.put("vCategoryName", generalFunc.getJsonValueStr("vCategoryName", obj_temp));
                                tempgeneralCategoryWiseList = new ArrayList<>();
                                JSONArray subCategoryArr = generalFunc.getJsonArray("SubCategories", obj_temp);


                                for (int j = 0; j < subCategoryArr.length(); j++) {
                                    tempgeneraliconList = new ArrayList<>();

                                    HashMap<String, String> catemap = new HashMap<String, String>();
                                    JSONObject categoryObj = generalFunc.getJsonObject(subCategoryArr, j);
                                    catemap.put("eCatType", generalFunc.getJsonValueStr("eCatType", categoryObj));
                                    catemap.put("iServiceId", generalFunc.getJsonValueStr("iServiceId", categoryObj));
                                    catemap.put("vCategory", generalFunc.getJsonValueStr("vCategory", categoryObj));
                                    catemap.put("vLogo_image", generalFunc.getJsonValueStr("vLogo_image", categoryObj));
                                    catemap.put("iVehicleCategoryId", generalFunc.getJsonValueStr("iVehicleCategoryId", categoryObj));
                                    catemap.put("vCategoryBanner", generalFunc.getJsonValueStr("vCategoryBanner", categoryObj));
                                    catemap.put("vBannerImage", generalFunc.getJsonValueStr("vBannerImage", categoryObj));
                                    catemap.put("tBannerButtonText", generalFunc.getJsonValueStr("tBannerButtonText", categoryObj));
                                    String eShowTerms = generalFunc.getJsonValueStr("eShowTerms", categoryObj);
                                    catemap.put("eShowTerms", Utils.checkText(eShowTerms) ? eShowTerms : "");
                                    catemap.put("LBL_BOOK_NOW", LBL_BOOK_NOW);


                                    btnArea.setVisibility(View.GONE);
                                    allMainCategoryList.add(catemap);
                                    if (isAppType) {
                                        String eShowType = generalFunc.getJsonValueStr("eShowType", categoryObj);
                                        catemap.put("eShowType", eShowType);

                                        //add condition for manage lsiting in home screen
                                        if (!APP_HOME_PAGE_LIST_VIEW_ENABLED) {

                                            if (eShowType.equalsIgnoreCase("ICON") || eShowType.equalsIgnoreCase("ICON-BANNER")) {
                                                catemap.put("eShowType", "Icon");

                                                if (gridCount < GRID_TILES_MAX_COUNT) {
                                                    mainCategoryList.add((HashMap<String, String>) catemap.clone());
                                                    tempgeneraliconList.add((HashMap<String, String>) catemap.clone());
                                                } else {
                                                    moreItemList.add((HashMap<String, String>) catemap.clone());
                                                    tempgeneraliconList.add((HashMap<String, String>) catemap.clone());
                                                }
                                                gridCount = gridCount + 1;

                                                if (eShowType.equalsIgnoreCase("ICON-BANNER")) {
                                                    catemap.put("eShowType", "Banner");
                                                    bannerList.add((HashMap<String, String>) catemap.clone());
                                                    tempgeneralBannerList.add((HashMap<String, String>) catemap.clone());
                                                }
                                            } else {
                                                tempgeneralBannerList.add((HashMap<String, String>) catemap.clone());
                                                bannerList.add((HashMap<String, String>) catemap.clone());
                                            }
                                        } else {
                                            String eCatViewType = generalFunc.getJsonValueStr("eCatViewType", categoryObj);
                                            if (eCatViewType.equalsIgnoreCase("Icon") || eCatViewType.equalsIgnoreCase("Icon,Banner") ||
                                                    eCatViewType.equalsIgnoreCase("Icon,Banner,List") ||
                                                    eCatViewType.equalsIgnoreCase("Icon,List")) {
                                                catemap.put("eShowType", "Icon");

                                                Logger.d("eCatViewType", "::" + eCatViewType);

                                                if (gridCount < GRID_TILES_MAX_COUNT) {
                                                    mainCategoryList.add((HashMap<String, String>) catemap.clone());
                                                    tempgeneraliconList.add((HashMap<String, String>) catemap.clone());
                                                } else {
                                                    moreItemList.add((HashMap<String, String>) catemap.clone());
                                                    tempgeneraliconList.add((HashMap<String, String>) catemap.clone());
                                                }
                                                gridCount = gridCount + 1;

                                                if (eCatViewType.equalsIgnoreCase("Icon,Banner")) {
                                                    catemap.put("eShowType", "Banner");
                                                    bannerList.add((HashMap<String, String>) catemap.clone());
                                                    tempgeneralBannerList.add((HashMap<String, String>) catemap.clone());
                                                }
                                                if (eCatViewType.equalsIgnoreCase("Icon,Banner,List")) {
                                                    catemap.put("eShowType", "Banner");
                                                    bannerList.add((HashMap<String, String>) catemap.clone());
                                                    tempgeneralBannerList.add((HashMap<String, String>) catemap.clone());
                                                }

                                                //added in list for eCatViewType=" icon banner list"
                                                if (eCatViewType.equalsIgnoreCase("Icon,Banner,List")) {
                                                    catemap.put("eShowType", "List");
                                                    catemap.put("vListLogo", generalFunc.getJsonValueStr("vListLogo", categoryObj));
                                                    catemap.put("tListDescription", generalFunc.getJsonValueStr("tListDescription", categoryObj));
                                                    serviceList.add((HashMap<String, String>) catemap.clone());
                                                    tempgeneralList.add((HashMap<String, String>) catemap.clone());

                                                }
                                                if (eCatViewType.equalsIgnoreCase("Icon,List")) {
                                                    catemap.put("eShowType", "List");
                                                    catemap.put("vListLogo", generalFunc.getJsonValueStr("vListLogo", categoryObj));
                                                    catemap.put("tListDescription", generalFunc.getJsonValueStr("tListDescription", categoryObj));
                                                    serviceList.add((HashMap<String, String>) catemap.clone());
                                                    tempgeneralList.add((HashMap<String, String>) catemap.clone());

                                                }

                                            } else if (eCatViewType.equalsIgnoreCase("Banner") ||
                                                    eCatViewType.equalsIgnoreCase("Banner,List")) {
                                                catemap.put("eShowType", "Banner");
                                                bannerList.add((HashMap<String, String>) catemap.clone());
                                                tempgeneralBannerList.add((HashMap<String, String>) catemap.clone());

                                                //added in list for eCatViewType="  banner list"
                                                if (eCatViewType.equalsIgnoreCase("Banner,List")) {
                                                    catemap.put("eShowType", "List");
                                                    catemap.put("vListLogo", generalFunc.getJsonValueStr("vListLogo", categoryObj));
                                                    catemap.put("tListDescription", generalFunc.getJsonValueStr("tListDescription", categoryObj));
                                                    serviceList.add((HashMap<String, String>) catemap.clone());
                                                    tempgeneralList.add((HashMap<String, String>) catemap.clone());
                                                }
                                            } else {
                                                catemap.put("eShowType", "List");
                                                catemap.put("tListDescription", generalFunc.getJsonValueStr("tListDescription", categoryObj));
                                                catemap.put("vListLogo", generalFunc.getJsonValueStr("vListLogo", categoryObj));
                                                serviceList.add((HashMap<String, String>) catemap.clone());
                                                tempgeneralList.add((HashMap<String, String>) catemap.clone());
                                                // added in list only
                                            }

                                        }
                                    } else {
                                        // map.put("eShowType", "Icon");
                                        mainCategoryList.add((HashMap<String, String>) catemap.clone());
                                    }


//                                if (serviceList.size() > 0) {
//                                    tempgeneralCategoryWiseListEshowType.addAll(serviceList);
//                                }
//                                if (generalFunc.getJsonValue("RDU_HOME_PAGE_LAYOUT_DESIGN", userProfileJson).equalsIgnoreCase("Banner/Icon")) {
//                                    tempgeneralCategoryWiseListEshowType.addAll(0, bannerList);
//                                } else {
//                                    tempgeneralCategoryWiseListEshowType.addAll(bannerList);
//                                }


                                    tempgeneralCategoryWiseList.addAll(tempgeneraliconList);


                                }
                                if (tempgeneralList != null && tempgeneralList.size() > 0) {
                                    map.put("isList", "Yes");
                                    tempgeneralCategoryWiseList.addAll(tempgeneralList);
                                } else {
                                    map.put("isList", "No");
                                }

                                if (tempgeneralBannerList != null) {
                                    tempgeneralCategoryWiseList.addAll(tempgeneralBannerList);
                                }
                                generalCategoryWiseList.add(i, tempgeneralCategoryWiseList);
                                generalCategoryWiseMoreList.add(i, allMainCategoryList);


                                generalMainCategoryList.add(map);

                            }


                            if (isAppType) {
                                int moreItemListSize = moreItemList.size();
                                if (moreItemListSize > 0) {

                                    HashMap<String, String> mapDataMore = new HashMap<>();
                                    mapDataMore.put("eCatType", "More");
                                    mapDataMore.put("eShowType", "Icon");
                                    mapDataMore.put("vCategory", generalFunc.retrieveLangLBl("", "LBL_MORE"));
                                    //mapDataMore.put("vLogo_image", "" + R.mipmap.ic_more);
                                    mapDataMore.put("vLogo_image", MORE_ICON);
                                    mapDataMore.put("eShowTerms", "No");
                                    mainCategoryList.add(mapDataMore);

                                    int mainCategoryListSize = mainCategoryList.size();
                                    if (mainCategoryListSize % noColumns != 0) {
                                        mainCategoryList.remove(mainCategoryListSize - 1);
                                        mainCategoryListSize = mainCategoryList.size();
                                        int totCount = 0;
                                        while ((mainCategoryListSize + 1) % noColumns != 0) {
                                            if (totCount >= moreItemListSize) {
                                                break;
                                            }
                                            mainCategoryList.add(moreItemList.get(totCount));
                                            totCount = totCount + 1;

                                            if (totCount >= mainCategoryListSize) {
                                                break;
                                            }
                                        }
                                        moreItemListSize = moreItemList.size();
                                        if (totCount < moreItemListSize) {
                                            mainCategoryList.add(mapDataMore);
                                        }
                                    }
                                }
                                //temp added service list
                                if (serviceList.size() > 0) {
                                    mainCategoryList.addAll(serviceList);
                                }
                                if (generalFunc.getJsonValue("RDU_HOME_PAGE_LAYOUT_DESIGN", userProfileJson).equalsIgnoreCase("Banner/Icon")) {
                                    mainCategoryList.addAll(0, bannerList);
                                } else {
                                    mainCategoryList.addAll(bannerList);
                                }


                            }

                            generalCategoryList.addAll(mainCategoryList);

                            if (isAppType) {
                                int genCatSize = generalCategoryWiseList.get(0).size();
                                ArrayList<HashMap<String, String>> generalCategoryList = generalCategoryWiseList.get(0);
                                for (int i = 0; i < genCatSize; i++) {

                                    String eShowType = generalCategoryList.get(i).get("eShowType");
                                    generalCategoryIconTypeDataMap.put("" + i, "" + (eShowType == null ? "" : eShowType));
                                }
                            }


                            daynamicCategoryAdapter = new DaynamicCategoryAdapter(generalFunc, generalMainCategoryList, getActContext());


                            daynamicCategoryAdapter.setCategorySelectListener(this);
                            mainCategoryRecyclerView.setAdapter(daynamicCategoryAdapter);
                            if (filtergeneralCategoryList != null) {
                                filtergeneralCategoryList.clear();
                            }
                            filtergeneralCategoryList = generalCategoryWiseList.get(0);
                            manageRecycleview();
                            filterListCategoryWise();


                        }
                    } else {
                        bannerArea.setVisibility(View.VISIBLE);

                        CollapsingToolbarLayout.LayoutParams marginLayoutParams = new CollapsingToolbarLayout.LayoutParams(bannerArea.getLayoutParams());

                        marginLayoutParams.setMargins(0, (int) getResources().getDimension(R.dimen._minus5sdp), 0, 0);

                        bannerArea.setLayoutParams(marginLayoutParams);

                        bannerArea.setBackgroundColor(Color.parseColor("#f3f3f3"));
                        bannerBg.setBackgroundColor(getActContext().getResources().getColor(R.color.appThemeColor_1));
                        ufxCatAdapter = null;
                        ufxCatAdapter = new UberXCategoryAdapter(getActContext(), generalCategoryList, generalFunc);


                        ufxCatAdapter.setCategoryMode(CAT_TYPE_MODE);
                        dataListRecyclerView.setAdapter(ufxCatAdapter);
                        dataListRecyclerView.getRecycledViewPool().clear();
                        ufxCatAdapter.notifyDataSetChanged();
                        ufxCatAdapter.setOnItemClickList(this);
                    }
                    JSONArray arr = generalFunc.getJsonArray("BANNER_DATA", responseObj);

                    if (arr != null && arr.length() > 0) {
                        isbanner = true;
                        if (isBannerView && CAT_TYPE_MODE.equals("0")) {
                            bannerArea.setVisibility(View.VISIBLE);
                        }
                        //selectServiceTxt.setVisibility(View.VISIBLE);
                        int arrSize = arr.length();
                        ArrayList<String> imagesList = new ArrayList<String>();
                        mCardAdapter = new CardPagerAdapter();
                        int margin = MyApp.getInstance().getCurrentAct().getResources().getDimensionPixelSize(R.dimen._20sdp);
                        int width = margin * 2;
                        int getBannerWidth = Utils.getWidthOfBanner(MyApp.getInstance().getCurrentAct(), width);
                        int size155sdp = MyApp.getInstance().getCurrentAct().getResources().getDimensionPixelSize(R.dimen._155sdp);

                        for (int i = 0; i < arrSize; i++) {
                            JSONObject obj_temp = generalFunc.getJsonObject(arr, i);

                            String vImage = generalFunc.getJsonValueStr("vImage", obj_temp);
                            // String imageURL = Utils.getResizeImgURL(getActContext(), vImage, Utils.getWidthOfBanner(getActContext(), width), Utils.getHeightOfBanner(getActContext(), width, "16:9"));
                            String imageURL = Utils.getResizeImgURL(MyApp.getInstance().getCurrentAct(), vImage, getBannerWidth, size155sdp);

                            imagesList.add(imageURL);
                            mCardAdapter.addCardItem(imageURL, getActContext());
                        }

                        int imageListSize = imagesList.size();
                        if (imageListSize > 2) {
                            bannerViewPager.setOffscreenPageLimit(3);
                        } else if (imageListSize > 1) {
                            bannerViewPager.setOffscreenPageLimit(2);
                        }

                        // UberXBannerPagerAdapter bannerAdapter = new UberXBannerPagerAdapter(getActContext(), imagesList);
                        // bannerViewPager.setAdapter(bannerAdapter);

//                            mFragmentCardAdapter = new CardFragmentPagerAdapter(getActivity().getSupportFragmentManager(),
//                                    dpToPixels(2, getActivity()));

                        mCardShadowTransformer = new ShadowTransformer(bannerViewPager, mCardAdapter);
                        //mFragmentCardShadowTransformer = new ShadowTransformer(bannerViewPager, mFragmentCardAdapter);

                        bannerViewPager.setAdapter(mCardAdapter);
                        bannerViewPager.setPageTransformer(false, mCardShadowTransformer);
                        bannerViewPager.setOffscreenPageLimit(3);

                        this.bannerAdapter = bannerAdapter;

                        // bannerCirclePageIndicator.setDataSize(imagesList.size());
                        //bannerCirclePageIndicator.setViewPager(bannerViewPager);


                    } else {
                        isbanner = false;
                        if (generalFunc.getJsonValue("HOME_SCREEN_DESIGN_HEADER_VIEW", userProfileJson) != null &&
                                generalFunc.getJsonValue("HOME_SCREEN_DESIGN_HEADER_VIEW", userProfileJson).equalsIgnoreCase("Banner")) {
                            toproundImg.setVisibility(View.GONE);
                            bottomRoundfImg.setVisibility(View.GONE);
                            bannerArea.setVisibility(View.GONE);
                            userImgView.setVisibility(View.GONE);
                            backUserImg.setVisibility(View.GONE);
                            imagArea.setVisibility(View.GONE);
                            CollapsingToolbarLayout.LayoutParams marginLayoutParams = new CollapsingToolbarLayout.LayoutParams(masterArea.getLayoutParams());

                            marginLayoutParams.setMargins(0, 0, 0, 0);
                            masterArea.setLayoutParams(marginLayoutParams);
                        }
                        if (CAT_TYPE_MODE.equalsIgnoreCase("0")) {
                            //selectServiceTxt.setVisibility(View.GONE);
                        } else {
                            //selectServiceTxt.setVisibility(View.VISIBLE);
                        }
                    }


//                    if (isbanner) {
//                        bannerArea.setVisibility(View.VISIBLE);
//                    }


                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr("message", responseObj)));
                }

            } else {
                generalFunc.showError();
            }

            serviceLoadingProgressBar.setVisibility(View.GONE);
        });
        exeWebServer.execute();
    }

    ArrayList<HashMap<String, String>> filtergeneralCategoryList;

    public void filterListCategoryWise() {


        if (filtergeneralCategoryList != null) {
            setParentCategoryLayoutManager();
            ufxCatAdapter = null;
            ufxCatAdapter = new UberXCategoryAdapter(getActContext(), filtergeneralCategoryList, generalFunc);

            ufxCatAdapter.setCategoryMode("0");

            ufxCatAdapter.setOnItemClickList(this);
            // setParentCategoryLayoutManager();
            dataListRecyclerView.setAdapter(ufxCatAdapter);
            dataListRecyclerView.getRecycledViewPool().clear();
            ufxCatAdapter.notifyDataSetChanged();
            ufxCatAdapter.setOnItemClickList(this);
        }

    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    @Override
    public void onItemClick(int position) {
        if (headerLocAddressTxt.getText().toString().equalsIgnoreCase(generalFunc.retrieveLangLBl("Enter Location...", "LBL_ENTER_LOC_HINT_TXT"))) {
            openSourceLocationView();
            return;
        }
        onItemClickHandle(position, "HOME");
    }

    @Override
    public void onMultiItem(String id, boolean b) {

        if (multiServiceSelect.contains(id)) {
            if (!b) {
                while (multiServiceSelect.remove(id)) {
                }
            }

        } else {
            multiServiceSelect.add(id);
        }
    }

    HashMap<String, String> mapData = null;

    public void onItemClickHandle(int position, String type) {

        Utils.hideKeyboard(getActContext());
        mapData = null;

        if (type.equalsIgnoreCase("HOME")) {
            mapData = filtergeneralCategoryList.get(position);
        } else {
            mapData = allMainCategoryList.get(position);
            mapData = generalCategoryWiseMoreList.get(selPos).get(position);
        }
        String eCatType = mapData.get("eCatType");
        if (eCatType != null) {
            String s = eCatType.toUpperCase(Locale.US);
            if ("MORE".equals(s)) {

                openMoreDialog();
                return;
            }

            mapData.put("latitude", latitude);
            mapData.put("longitude", longitude);
            mapData.put("address", headerLocAddressTxt.getText().toString());
            (new OpenCatType(getActContext(), mapData)).execute();
        }

        if (eCatType.equalsIgnoreCase("ServiceProvider")) {
            if (CAT_TYPE_MODE.equals("0")) {
                setSubCategoryList(mapData);
                return;
            }

            if (latitude.equalsIgnoreCase("0.0") || longitude.equalsIgnoreCase("0.0")) {
                generalFunc.showMessage(generalFunc.getCurrentView((Activity) getActContext()), generalFunc.retrieveLangLBl("", "LBL_SET_LOCATION"));
            } else {
                checkServiceAvailableOrNot(mapData.get("iVehicleCategoryId"), latitude, longitude, position);
            }
        }

    }

    public void setMainCategory() {
        CollapsingToolbarLayout.LayoutParams marginLayoutParams = new CollapsingToolbarLayout.LayoutParams(bannerArea.getLayoutParams());

        marginLayoutParams.setMargins(0, (int) getResources().getDimension(R.dimen._30sdp), 0, 0);

        bannerArea.setLayoutParams(marginLayoutParams);


        manageHedaer(headerLocAddressHomeTxt);
        manageRecycleview();
        subCategoryToolArea.setVisibility(View.GONE);
        subCategoryToolHomeArea.setVisibility(View.VISIBLE);
        masterArea.setVisibility(View.VISIBLE);
        //selectServiceTxt.setText(generalFunc.retrieveLangLBl("Select Service", "LBL_RDU_SERVICES_TITLE"));
        selectServiceTxt.setText(generalFunc.retrieveLangLBl("Select Service", "LBL_SELECT_SERVICE_TXT"));
        //bannerArea.setVisibility(View.GONE);
        bannerArea.setBackgroundColor(getActContext().getResources().getColor(android.R.color.transparent));
        bannerBg.setBackgroundColor(getActContext().getResources().getColor(android.R.color.transparent));
        CAT_TYPE_MODE = "0";

        selectServiceArea.setVisibility(View.VISIBLE);
        ourServiceArea.setVisibility(View.VISIBLE);

        btnArea.setVisibility(View.GONE);
        if(isBannerView) {
            userNameTxt.setVisibility(View.INVISIBLE);
            userSinceTxt.setVisibility(View.INVISIBLE);
            userImgView.setVisibility(View.INVISIBLE);
            backUserImg.setVisibility(View.INVISIBLE);
            bannerArea.setVisibility(View.VISIBLE);
            toproundImg.setVisibility(View.GONE);
            bottomRoundfImg.setVisibility(View.GONE);
        } else {
            userNameTxt.setVisibility(View.VISIBLE);
            userSinceTxt.setVisibility(View.VISIBLE);
            userImgView.setVisibility(View.VISIBLE);
            backUserImg.setVisibility(View.VISIBLE);
            bannerArea.setVisibility(View.GONE);
            toproundImg.setVisibility(View.VISIBLE);
            bottomRoundfImg.setVisibility(View.VISIBLE);
        }

        //   ((CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar)).setLayoutTransition(new LayoutTransition());
        generalCategoryList.clear();
        scrollAppBarLayout();

        generalCategoryList.addAll(mainCategoryList);
//        ufxCatAdapter = null;
//        ufxCatAdapter = new UberXCategoryAdapter(getActContext(), generalCategoryList, generalFunc);
//
//        ufxCatAdapter.setCategoryMode("0");
//
//        ufxCatAdapter.setOnItemClickList(this);
//        dataListRecyclerView.setAdapter(ufxCatAdapter);
        filterListCategoryWise();

        boolean isRideDeliveryUberx = APP_TYPE.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX);
        if (isRideDeliveryUberx
                || (APP_TYPE.equalsIgnoreCase(Utils.CabGeneralType_UberX))) {
            if (isRideDeliveryUberx) {
                uberXHeaderLayout.setVisibility(View.VISIBLE);
            }

            // selectServiceTxt.setVisibility(View.GONE);

            if (multiServiceSelect != null) {
                multiServiceSelect.clear();
            }
        } else {


            uberXHeaderLayout.setVisibility(View.VISIBLE);
            //selectServiceTxt.setVisibility(View.VISIBLE);
        }


        setParentCategoryLayoutManager();

        dataListRecyclerView.removeItemDecoration(itemDecoration);
        dataListRecyclerView.getRecycledViewPool().clear();
        ufxCatAdapter.notifyDataSetChanged();
//        if (addDrawer != null) {
//            addDrawer.setMenuState(true);
//        }

        if (isbanner) {

            //selectServiceTxt.setVisibility(View.VISIBLE);
        } else {
            //selectServiceTxt.setVisibility(View.GONE);
        }

        // getCategory(generalFunc.getJsonValue(Utils.UBERX_PARENT_CAT_ID, userProfileJson), CAT_TYPE_MODE);
    }

    public void manageHedaer(MTextView headerTxtView) {
        Drawable drawable;
        Drawable arrowdrawable;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_place_address, getActContext().getTheme());
        } else {
            drawable = AppCompatResources.getDrawable(getActContext(), R.drawable.ic_place_address);
        }


        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        Drawable mutableDrawable = wrappedDrawable.mutate();
        DrawableCompat.setTint(mutableDrawable, ContextCompat.getColor(getActContext(), R.color.white));


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            arrowdrawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_down_arrow_header, getActContext().getTheme());
        } else {
            arrowdrawable = AppCompatResources.getDrawable(getActContext(), R.drawable.ic_down_arrow_header);
        }


        Drawable arrowwrappedDrawable = DrawableCompat.wrap(arrowdrawable);
        Drawable arrowmutableDrawable = arrowwrappedDrawable.mutate();
        DrawableCompat.setTint(arrowmutableDrawable, ContextCompat.getColor(getActContext(), R.color.white));

        if (generalFunc.isRTLmode()) {
            headerTxtView.setCompoundDrawablesWithIntrinsicBounds(arrowmutableDrawable, null, mutableDrawable, null);
        } else {
            headerTxtView.setCompoundDrawablesWithIntrinsicBounds(mutableDrawable, null, arrowmutableDrawable, null);

        }
    }

    public void setSubCategoryList(HashMap<String, String> dataItem) {

        manageHedaer(headerLocAddressTxt);
        dataListRecyclerView.setBackgroundResource(0);
        RelativeLayout.LayoutParams marginLayoutParams = new RelativeLayout.LayoutParams(dataListRecyclerView.getLayoutParams());

        marginLayoutParams.setMargins(0, 0, 0, 0);

        dataListRecyclerView.setLayoutParams(marginLayoutParams);

        subCategoryToolArea.setVisibility(View.VISIBLE);
        subCategoryToolHomeArea.setVisibility(View.GONE);
        masterArea.setVisibility(View.GONE);
        skeletonScreen = Skeleton.bind(MainArea)
                .load(R.layout.subcategory_shimmer_view)
                .duration(1000)
                .color(R.color.shimmer_color)
                .angle(0)
                .show();
        MyHandler myHandler = new MyHandler(this);
        myHandler.sendEmptyMessageDelayed(1, 3000);

        ufxCatAdapter.setCategoryMode("1");


        uberXHeaderLayout.setVisibility(View.VISIBLE);
        //selectServiceTxt.setVisibility(View.VISIBLE);

        CAT_TYPE_MODE = "1";

        selectServiceArea.setVisibility(View.GONE);
        ourServiceArea.setVisibility(View.GONE);
        userNameTxt.setVisibility(View.GONE);
        userSinceTxt.setVisibility(View.GONE);
        userImgView.setVisibility(View.GONE);
        backUserImg.setVisibility(View.GONE);

        dataListRecyclerView.setLayoutManager(new LinearLayoutManager(getActContext()));
        if (UBERX_PARENT_CAT_ID.equalsIgnoreCase("0")) {

            // scrollAppBarLayout();

            // ((CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar)).setLayoutTransition(null);
            //bannerArea.setVisibility(View.GONE);
            // bannerArea.setBackgroundColor(getActContext().getResources().getColor(android.R.color.transparent));
            //   bannerBg.setBackgroundColor(getActContext().getResources().getColor(android.R.color.transparent));
            selectServiceTxt.setText(dataItem.get("vCategory"));

        }
        String iVehicleCategoryId = dataItem.get("iVehicleCategoryId");
        Logger.e("geCtaegory", "::setSubCategoryList");

        getCategory(iVehicleCategoryId, "1");
        isLoading = true;


//        if (addDrawer != null) {
//            addDrawer.setMenuState(false);
//        }

        if (generalFunc.getJsonValueStr("SERVICE_PROVIDER_FLOW", obj_userProfile).equalsIgnoreCase("PROVIDER")) {
            btnArea.setVisibility(View.VISIBLE);
        }
    }

    public void scrollAppBarLayout() {
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
//        appBarLayout.setLayoutParams(params);
//        appBarLayout.setExpanded(true);
    }

    @Override
    public void onResume() {
        super.onResume();


        if (generalFunc.prefHasKey(Utils.iServiceId_KEY) && generalFunc != null /*&& !generalFunc.isDeliverOnlyEnabled()*/) {
            generalFunc.removeValue(Utils.iServiceId_KEY);
        }

        try {
//            if (addDrawer != null) {
//                this.userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
//                obj_userProfile = generalFunc.getJsonObject(userProfileJson);
//                addDrawer.userProfileJson = this.userProfileJson;
//                addDrawer.obj_userProfile = generalFunc.getJsonObject(this.userProfileJson);
//                addDrawer.buildDrawer();
//            }


            if (generalFunc.retrieveValue(Utils.ISWALLETBALNCECHANGE).equalsIgnoreCase("Yes")) {
                getWalletBalDetails();
            } else {
                setUserProfileData("", false);
                setUserInfo();
            }


        } catch (Exception e) {
            Logger.d("Ex", "::" + e.toString());

        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void getWalletBalDetails() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GetMemberWalletBalance");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {


            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail) {
                    try {
                        generalFunc.storeData(Utils.ISWALLETBALNCECHANGE, "No");
                        String userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
                        JSONObject object = generalFunc.getJsonObject(userProfileJson);
                        object.put("user_available_balance", generalFunc.getJsonValue("MemberBalance", responseString));
                        generalFunc.storeData(Utils.USER_PROFILE_JSON, object.toString());

                        setUserProfileData(userProfileJson, true);
                        setUserInfo();
                    } catch (Exception e) {
                        Logger.d("Ex", "::" + e.toString());
                    }
                }
            }
        });
        exeWebServer.execute();
    }

    public void setUserInfo() {
        View view = ((Activity) getActContext()).findViewById(android.R.id.content);
        String WalletTextAmount = generalFunc.retrieveLangLBl("", "LBL_WALLET_BALANCE") + ": " + generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("user_available_balance", obj_userProfile));
        userSinceTxt.setText(WalletTextAmount);
        //userSinceTxt.setText(generalFunc.getJsonValueStr("tMemberSince", obj_userProfile));
        userNameTxt.setText(generalFunc.getJsonValueStr("vName", obj_userProfile) + " "
                + generalFunc.getJsonValueStr("vLastName", obj_userProfile));
        ((MTextView) view.findViewById(R.id.walletbalncetxt)).setText(WalletTextAmount);

        (new AppFunctions(getActContext())).checkProfileImage((SelectableRoundedImageView) view.findViewById(R.id.userImgView), userProfileJson, "vImgName");
    }

    @Override
    public void onAddressFound(String address, double latitude, double longitude, String geocodeobject) {
        if (isback) {
            if (getArguments().getString("address") != null) {
                return;
            }
        }

        if (getArguments() != null && getArguments().getString("uberXAddress") != null) {
            address = getArguments().getString("uberXAddress");
            headerLocAddressTxt.setText(address);
            headerLocAddressHomeTxt.setText(address);
            this.latitude = getArguments().getString("uberXlat", "0.0");
            this.longitude = getArguments().getString("uberXlong", "0.0");
        } else {
            if (address != null && !address.equals("")) {
                this.latitude = latitude + "";
                this.longitude = longitude + "";
                headerLocAddressTxt.setText(address);
                headerLocAddressHomeTxt.setText(address);
                if (noSourceLocationdialog != null) {
                    noSourceLocationdialog.dismiss();
                }
            }
        }


    }

    AppCompatDialog noSourceLocationdialog;

    private void openSourceLocationView() {
        noSourceLocationdialog = new AppCompatDialog(getActContext(), android.R.style.Theme_Translucent_NoTitleBar);
        noSourceLocationdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        noSourceLocationdialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        noSourceLocationdialog.setContentView(R.layout.no_source_location_design);
        Objects.requireNonNull(noSourceLocationdialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        noSourceLocationdialog.setCancelable(false);

        ((RippleBackground) noSourceLocationdialog.findViewById(R.id.rippleBgView)).startRippleAnimation();

        ImageView closeImage = noSourceLocationdialog.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(v -> {
            noSourceLocationdialog.dismiss();
        });
        MTextView locationHintText = noSourceLocationdialog.findViewById(R.id.locationHintText);
        MTextView locationDescText = noSourceLocationdialog.findViewById(R.id.locationDescText);
        MTextView btnTxt = noSourceLocationdialog.findViewById(R.id.btnTxt);
        ImageView btnImg = noSourceLocationdialog.findViewById(R.id.btnImg);
        LinearLayout btnArea = noSourceLocationdialog.findViewById(R.id.btnArea);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        RippleBackground.LayoutParams buttonLayoutParams = new RippleBackground.LayoutParams(RippleBackground.LayoutParams.MATCH_PARENT, RippleBackground.LayoutParams.MATCH_PARENT);
        buttonLayoutParams.setMargins(0, 0, 0, -(height / 2));
        ((RippleBackground) noSourceLocationdialog.findViewById(R.id.rippleBgView)).setLayoutParams(buttonLayoutParams);


        if (generalFunc.isRTLmode()) {
            btnImg.setRotation(180);
            btnArea.setBackground(getActContext().getResources().getDrawable(R.drawable.login_border_rtl));
        }

        btnTxt.setText(generalFunc.retrieveLangLBl("ENTER", "LBL_ADD_ADDRESS_TXT"));
        locationDescText.setText(generalFunc.retrieveLangLBl("Please wait while we are trying to access your location. meanwhile you can enter your source location.", "LBL_FETCHING_LOCATION_NOTE_TEXT"));
        locationHintText.setText(generalFunc.retrieveLangLBl("Location", "LBL_LOCATION_FOR_FRONT"));

        btnArea.setOnClickListener(v -> {
            Bundle bn = new Bundle();
            bn.putString("locationArea", "source");
//                    bn.putBoolean("isaddressview", true);
            if (!latitude.equalsIgnoreCase("0.0") && !longitude.equalsIgnoreCase("0.0")) {
                bn.putDouble("lat", GeneralFunctions.parseDoubleValue(0.0, latitude));
                bn.putDouble("long", GeneralFunctions.parseDoubleValue(0.0, longitude));
            }
            bn.putString("address", headerLocAddressTxt.getText().toString() + "");

            new StartActProcess(getActContext()).startActForResult(SearchLocationActivity.class,
                    bn, Utils.UBER_X_SEARCH_PICKUP_LOC_REQ_CODE);

        });

        noSourceLocationdialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.MY_PROFILE_REQ_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            setUserProfileData("", false);
            //addDrawer.changeUserProfileJson(userProfileJson);
        } else if (requestCode == Utils.UBER_X_SEARCH_PICKUP_LOC_REQ_CODE && resultCode == getActivity().RESULT_OK && data != null) {

            headerLocAddressTxt.setText(data.getStringExtra("Address"));
            headerLocAddressHomeTxt.setText(data.getStringExtra("Address"));
            this.latitude = data.getStringExtra("Latitude") == null ? "0.0" : data.getStringExtra("Latitude");
            this.longitude = data.getStringExtra("Longitude") == null ? "0.0" : data.getStringExtra("Longitude");

            if (!this.latitude.equalsIgnoreCase("0.0") && !this.longitude.equalsIgnoreCase("0.0")) {
                isUfxaddress = true;
            }
            uberXHomeActivity.address = data.getStringExtra("Address");
            uberXHomeActivity.latitude = latitude;
            uberXHomeActivity.longitude = longitude;
        } else if (requestCode == Utils.CARD_PAYMENT_REQ_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            setUserProfileData("", false);
            //addDrawer.changeUserProfileJson(userProfileJson);
        } else if (requestCode == Utils.SEARCH_PICKUP_LOC_REQ_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            isUfxaddress = true;
            headerLocAddressTxt.setText(data.getStringExtra("Address"));
            headerLocAddressHomeTxt.setText(data.getStringExtra("Address"));
            this.latitude = data.getStringExtra("Latitude");
            this.longitude = data.getStringExtra("Longitude");


        } else if (requestCode == Utils.UBER_X_SEARCH_PICKUP_LOC_REQ_CODE && resultCode == getActivity().RESULT_OK && data != null) {

            headerLocAddressTxt.setText(data.getStringExtra("Address").trim());
            headerLocAddressHomeTxt.setText(data.getStringExtra("Address").trim());
            this.latitude = data.getStringExtra("Latitude") == null ? "0.0" : data.getStringExtra("Latitude");
            this.longitude = data.getStringExtra("Longitude") == null ? "0.0" : data.getStringExtra("Longitude");

            if (!this.latitude.equalsIgnoreCase("0.0") && !this.longitude.equalsIgnoreCase("0.0")) {
                isUfxaddress = true;
            }


            try {
                ViewGroup viewGroup = (ViewGroup) MyApp.getInstance().getCurrentAct().findViewById(android.R.id.content);
                OpenNoLocationView.getInstance(MyApp.getInstance().getCurrentAct(), viewGroup).configView(false);
            } catch (Exception e) {

            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopLocationUpdates();
        releaseResources();
    }

    public void configCategoryView() {
        setMainCategory();
    }

    public void releaseResources() {
        if (getAddressFromLocation != null) {
            getAddressFromLocation.setAddressList(null);
            getAddressFromLocation = null;
        }
    }

    @Override
    public void onLocationUpdate(Location mLastLocation) {
        stopLocationUpdates();
        latitude = mLastLocation.getLatitude() + "";
        longitude = mLastLocation.getLongitude() + "";
        if (getAddressFromLocation == null) {
            getAddressFromLocation = new GetAddressFromLocation(getActContext(), generalFunc);
            getAddressFromLocation.setLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            getAddressFromLocation.setAddressList(this);
            getAddressFromLocation.execute();
        }
    }

    public void checkServiceAvailableOrNot(String iVehicleCategoryId, String latitude, String longitude, int position) {

        HashMap<String, String> parameters = new HashMap<String, String>();

        parameters.put("type", "getServiceCategoryTypes");
        parameters.put("iVehicleCategoryId", iVehicleCategoryId);
        parameters.put("userId", generalFunc.getMemberId());
        parameters.put("vLatitude", latitude);
        parameters.put("vLongitude", longitude);
        parameters.put("UserType", Utils.userType);
        parameters.put("eCheck", "Yes");

        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setCancelAble(false);
        exeWebServer.setDataResponseListener(responseString -> {
            JSONObject responseObj = generalFunc.getJsonObject(responseString);

            if (responseObj != null && !responseObj.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseObj);

                if (isDataAvail) {
                    Bundle bundle = new Bundle();
                    bundle.putString("iVehicleCategoryId", generalCategoryList.get(position).get("iVehicleCategoryId"));
                    bundle.putString("vCategoryName", generalCategoryList.get(position).get("vCategory"));
                    bundle.putString("latitude", latitude);
                    bundle.putString("longitude", longitude);
                    bundle.putString("address", headerLocAddressTxt.getText().toString());
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr("message", responseObj)));
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    int selPos = 0;

    @Override
    public void onCategorySelect(int pos) {
        // filtergeneralCategoryList.clear();
        selPos = pos;
        filtergeneralCategoryList = generalCategoryWiseList.get(pos);
        if (isAppType) {
            int genCatSize = generalCategoryWiseList.get(pos).size();
            ArrayList<HashMap<String, String>> generalCategoryList = generalCategoryWiseList.get(pos);
            for (int i = 0; i < genCatSize; i++) {

                String eShowType = generalCategoryList.get(i).get("eShowType");
                generalCategoryIconTypeDataMap.put("" + i, "" + (eShowType == null ? "" : eShowType));
            }
        }
        manageRecycleview();

        filterListCategoryWise();

    }

    public void manageRecycleview() {
        if (!generalMainCategoryList.get(selPos).get("isList").equalsIgnoreCase("Yes")) {

            dataListRecyclerView.setBackground(getResources().getDrawable(R.drawable.all_roundcurve_card));
            RelativeLayout.LayoutParams marginLayoutParams = new RelativeLayout.LayoutParams(dataListRecyclerView.getLayoutParams());

            marginLayoutParams.setMargins((int) getResources().getDimension(R.dimen._10sdp), 0, (int) getResources().getDimension(R.dimen._10sdp), 0);

            dataListRecyclerView.setLayoutParams(marginLayoutParams);
        } else {
            dataListRecyclerView.setBackgroundResource(0);
            RelativeLayout.LayoutParams marginLayoutParams = new RelativeLayout.LayoutParams(dataListRecyclerView.getLayoutParams());

            marginLayoutParams.setMargins(0, 0, 0, 0);

            dataListRecyclerView.setLayoutParams(marginLayoutParams);

        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Bundle bn = new Bundle();
            if (i == submitBtnId) {
                if (latitude.equalsIgnoreCase("0.0") || longitude.equalsIgnoreCase("0.0")) {
                    generalFunc.showMessage(view, generalFunc.retrieveLangLBl("", "LBL_SET_LOCATION"));
                } else {

                    String SelectedVehicleTypeId = "";
                    if (multiServiceSelect.size() > 0) {

                        SelectedVehicleTypeId = android.text.TextUtils.join(",", multiServiceSelect);
                    } else {
                        generalFunc.showMessage(view, generalFunc.retrieveLangLBl("Please Select Service", "LBL_SELECT_SERVICE_TXT"));
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isufx", true);
                    bundle.putString("latitude", latitude);
                    bundle.putString("longitude", longitude);
                    bundle.putString("address", headerLocAddressTxt.getText().toString());
                    bundle.putString("SelectvVehicleType", vParentCategoryName);
                    bundle.putString("SelectedVehicleTypeId", SelectedVehicleTypeId);
                    bundle.putString("parentId", parentId);
                    bundle.putBoolean("isCarwash", true);

                    new StartActProcess(getActContext()).startActWithData(MainActivity.class, bundle);

                }
            }

        }
    }

    private void setBounceAnimation(View view, BounceAnimListener bounceAnimListener) {
        Animation anim = AnimationUtils.loadAnimation(getActContext(), R.anim.bounce_interpolator);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (bounceAnimListener != null) {
                    bounceAnimListener.onAnimationFinished();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(anim);
    }

    private interface BounceAnimListener {
        void onAnimationFinished();
    }

    public class setOnClickLst implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Utils.hideKeyboard(getActContext());
            switch (v.getId()) {
                case R.id.headerLocAddressHomeTxt:
                case R.id.uberXHeaderLayout:
                    Bundle bn = new Bundle();
                    bn.putString("locationArea", "source");
//                    bn.putBoolean("isaddressview", true);
                    if (!latitude.equalsIgnoreCase("0.0") && !longitude.equalsIgnoreCase("0.0")) {
                        bn.putDouble("lat", GeneralFunctions.parseDoubleValue(0.0, latitude));
                        bn.putDouble("long", GeneralFunctions.parseDoubleValue(0.0, longitude));
                    }
                    bn.putString("address", headerLocAddressTxt.getText().toString() + "");

                    new StartActProcess(getActContext()).startActForResult(SearchLocationActivity.class,
                            bn, Utils.UBER_X_SEARCH_PICKUP_LOC_REQ_CODE);

                    break;

                case R.id.backImgView:
                    if (CAT_TYPE_MODE.equals("1") && UBERX_PARENT_CAT_ID.equalsIgnoreCase("0") && !isLoading) {
                        isLoading = false;
                        multiServiceSelect.clear();
                        backImgView.setVisibility(View.GONE);
                        manageToolBarAddressView(false);
                        uberXHomeActivity.rduTopArea.setVisibility(View.VISIBLE);

                        MainLayout.setBackgroundColor(color);
                        MainArea.setBackgroundColor(color);
                        selectServiceTxt.setBackgroundColor(color);

                        configCategoryView();
                        return;
                    }
                    break;
                case R.id.arrowImg:
                case R.id.moreTxt:
                    openMoreDialog();
                    break;


            }
        }
    }

    public void manageToolBarAddressView(boolean isback) {
        if (isback) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int size2sdp = (int) getResources().getDimension(R.dimen._2sdp);
            int size15sdp = (int) getResources().getDimension(R.dimen._15sdp);
            params.setMargins(generalFunc.isRTLmode() ? size15sdp : size2sdp, 0, generalFunc.isRTLmode() ? size2sdp : size15sdp, 0);
            headerLocAddressTxt.setLayoutParams(params);

        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int size20sdp = (int) getResources().getDimension(R.dimen._20sdp);
            int size15sdp = (int) getResources().getDimension(R.dimen._15sdp);
            params.setMargins(generalFunc.isRTLmode() ? size15sdp : size20sdp, 0, generalFunc.isRTLmode() ? size20sdp : size15sdp, 0);
            headerLocAddressTxt.setLayoutParams(params);
        }
    }

    BottomSheetDialog moreDialog;

    public void openMoreDialog() {
        if (moreDialog != null && moreDialog.isShowing()) {
            return;
        }
        moreDialog = new BottomSheetDialog(getActContext());

        View contentView = View.inflate(getActContext(), R.layout.dialog_more, null);
        if (generalFunc.isRTLmode()) {
            contentView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        moreDialog.setContentView(contentView);

        moreDialog.setCancelable(true);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        mBehavior.setPeekHeight(Utils.dipToPixels(getActContext(), 335));
        mBehavior.setHideable(true);
        MTextView moreTitleTxt = contentView.findViewById(R.id.moreTitleTxt);
        MTextView cancelTxt = contentView.findViewById(R.id.cancelTxt);

        moreTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SELECT_SERVICE"));
        cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
        cancelTxt.setOnClickListener(v -> moreDialog.dismiss());

        RecyclerView dataListRecyclerView_more = contentView.findViewById(R.id.dataListRecyclerView_more);
        dataListRecyclerView_more.setLayoutManager(new GridLayoutManager(getActContext(), noColumns));
        UberXCategoryAdapter ufxCatAdapter = new UberXCategoryAdapter(getActContext(), generalCategoryWiseMoreList.get(selPos), generalFunc, true);
        dataListRecyclerView_more.setAdapter(ufxCatAdapter);


        ufxCatAdapter.setOnItemClickList(new UberXCategoryAdapter.OnItemClickList() {
            @Override
            public void onItemClick(int position) {
                moreDialog.cancel();
                onItemClickHandle(position, "MORE");
            }

            @Override
            public void onMultiItem(String id, boolean b) {

            }
        });


        moreDialog.show();
    }

    public Integer getNumOfColumns() {
        try {
            DisplayMetrics displayMetrics = getActContext().getResources().getDisplayMetrics();
            float dpWidth = (displayMetrics.widthPixels - Utils.dipToPixels(getActContext(), 10)) / displayMetrics.density;
            int margin_int_value = getActContext().getResources().getDimensionPixelSize(R.dimen._10sdp) * 2;
            int menuItem_int_value = getActContext().getResources().getDimensionPixelSize(R.dimen._5sdp) * 2;
            int catWidth_int_value = getActContext().getResources().getDimensionPixelSize(R.dimen.category_grid_size);
            int screenWidth_int_value = displayMetrics.widthPixels - margin_int_value - menuItem_int_value;
            int noOfColumns = (int) (screenWidth_int_value / catWidth_int_value);
            return noOfColumns;
        } catch (Exception e) {

        }
        return -1;
    }
}
