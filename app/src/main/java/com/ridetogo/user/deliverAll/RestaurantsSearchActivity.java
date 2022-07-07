package com.ridetogo.user.deliverAll;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adapter.files.ViewPagerAdapter;
import com.adapter.files.deliverAll.RestaurantSearchAdapter;
import com.adapter.files.deliverAll.RestaurantmenuAdapter;
import com.ridetogo.user.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.google.android.material.tabs.TabLayout;
import com.utils.Logger;
import com.utils.Utils;
import com.view.MTextView;
import com.view.anim.loader.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RestaurantsSearchActivity extends AppCompatActivity implements RestaurantSearchAdapter.RestaurantOnClickListener, RestaurantmenuAdapter.OnItemClickListener {

    GeneralFunctions generalFunc;
    EditText searchTxtView;
    ImageView imageCancel;
    MTextView cancelTxt;
    MTextView cusineTitleTxt;
    MTextView restaurantsTitleTxt;
    LinearLayout cusinecontainArea;
    Double latitude;
    Double longitude;
    RestaurantSearchAdapter restaurantAdapter;

    ArrayList<HashMap<String, String>> cuisineList = new ArrayList<>();

    ArrayList<HashMap<String, String>> restaurantArr_List = new ArrayList<>();
    RecyclerView restaurantSearchRecycView;

    LinearLayout cusineArea, restaurantsArea;
    ImageView noSearchImage;
    String userProfileJson;
    MTextView norecordTxt;
    AVLoadingIndicatorView loaderView;
    boolean isFavChange=false;

    private CharSequence[] titles;
    private ArrayList<Fragment> fragmentList=new ArrayList<>();
    RecyclerView itemSearchRecycView;
    LinearLayout tabsArea;
    LinearLayout itemsArea;
    LinearLayout tabArea;
    private ViewPager appLogin_view_pager;
    TabLayout material_tabs;
    RestaurantmenuAdapter restaurantItemAdapter;
    ArrayList<HashMap<String, String>> itemArr_List = new ArrayList<>();
    private String LBL_ADD = "";
    MTextView itemsTitleTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_search);


        initView();


        Utils.showSoftKeyboard(this,searchTxtView);

    }

    public void initView() {
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());

        searchTxtView = (EditText) findViewById(R.id.searchTxtView);
        imageCancel = (ImageView) findViewById(R.id.imageCancel);
        cancelTxt = (MTextView) findViewById(R.id.cancelTxt);
        cusineTitleTxt = (MTextView) findViewById(R.id.cusineTitleTxt);
        cusinecontainArea = (LinearLayout) findViewById(R.id.cusinecontainArea);
        restaurantsTitleTxt = (MTextView) findViewById(R.id.restaurantsTitleTxt);
        restaurantSearchRecycView = (RecyclerView) findViewById(R.id.restaurantSearchRecycView);
        cusineArea = (LinearLayout) findViewById(R.id.cusineArea);
        restaurantsArea = (LinearLayout) findViewById(R.id.restaurantsArea);
        noSearchImage = (ImageView) findViewById(R.id.noSearchImage);
        norecordTxt = (MTextView) findViewById(R.id.norecordTxt);
        loaderView = (AVLoadingIndicatorView) findViewById(R.id.loaderView);

        restaurantSearchRecycView.setLayoutManager(new LinearLayoutManager(getActContext()));
        cancelTxt.setOnClickListener(new setOnClickList());
        imageCancel.setOnClickListener(new setOnClickList());

        latitude = getIntent().getDoubleExtra("lat", 0);
        longitude = getIntent().getDoubleExtra("long", 0);

        restaurantAdapter = new RestaurantSearchAdapter(getActContext(), restaurantArr_List);
//        restaurantListRecycView.setAdapter(restaurantAdapter);
        restaurantSearchRecycView.setAdapter(restaurantAdapter);
        restaurantAdapter.setOnRestaurantItemClick(this);
        cusineTitleTxt.setText(generalFunc.retrieveLangLBl("","LBL_CUISINES"));

        itemSearchRecycView = (RecyclerView) findViewById(R.id.itemSearchRecycView);
        appLogin_view_pager = (ViewPager) findViewById(R.id.appLogin_view_pager);
        tabsArea = (LinearLayout) findViewById(R.id.tabsArea);
        itemsArea = (LinearLayout) findViewById(R.id.itemsArea);
        itemSearchRecycView.setLayoutManager(new LinearLayoutManager(getActContext()));
        itemsTitleTxt = (MTextView) findViewById(R.id.itemsTitleTxt);
        itemsTitleTxt.setText(generalFunc.retrieveLangLBl("","LBL_MENU_TITLE"));

        restaurantItemAdapter = new RestaurantmenuAdapter(getActContext(), itemArr_List, generalFunc, itemSearchRecycView.getLayoutManager());
        itemSearchRecycView.setAdapter(restaurantItemAdapter);
        restaurantItemAdapter.setOnItemClickListener(this);


        material_tabs = (TabLayout) findViewById(R.id.material_tabs);

        String TAB1_LBL = generalFunc.retrieveLangLBl("", "LBL_RESTAURANTS_TXT");
        String TAB2_LBL = generalFunc.retrieveLangLBl("", "LBL_MENU_TITLE");
        String TAB3_LBL = generalFunc.retrieveLangLBl("", "LBL_CUISINES");

        titles = new CharSequence[]{TAB1_LBL, TAB2_LBL, TAB3_LBL};

        fragmentList.add(new Fragment());
        fragmentList.add(new Fragment());
        fragmentList.add(new Fragment());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), titles, fragmentList);
        appLogin_view_pager.setAdapter(adapter);
        material_tabs.setupWithViewPager(appLogin_view_pager);
        setPos1Data();
        appLogin_view_pager.setCurrentItem(0);

        setLabel();


        searchTxtView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 2) {

                    getRestaurantList(searchTxtView.getText().toString().trim());

                } else {
                    noSearchImage.setVisibility(View.VISIBLE);
                    tabsArea.setVisibility(View.VISIBLE);
                    cuisineList.clear();
                    addCusineView();
                    restaurantArr_List.clear();
                    itemArr_List.clear();
                    restaurantAdapter.notifyDataSetChanged();
                    restaurantItemAdapter.notifyDataSetChanged();
                    restaurantsArea.setVisibility(View.GONE);
                    itemsArea.setVisibility(View.GONE);
                    cusineArea.setVisibility(View.GONE);

                }

            }
        });
    }

    private void setPos1Data() {
        if (restaurantArr_List != null && restaurantArr_List.size() == 0) {
            norecordTxt.setVisibility(View.VISIBLE);
            restaurantsArea.setVisibility(View.GONE);
            itemsArea.setVisibility(View.GONE);
            cusineArea.setVisibility(View.GONE);
            noSearchImage.setVisibility(View.VISIBLE);

        }else
        {
            norecordTxt.setVisibility(View.GONE);
            restaurantsArea.setVisibility(View.VISIBLE);
            itemsArea.setVisibility(View.GONE);
            cusineArea.setVisibility(View.GONE);
            noSearchImage.setVisibility(View.GONE);
        }

    }

    public void setLabel() {
        searchTxtView.setHint(generalFunc.retrieveLangLBl("", "LBL_SEARCH_RESTAURANT"));
        cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
        norecordTxt.setText(generalFunc.retrieveLangLBl("", "LBL_NO_RECORD_FOUND"));

        restaurantsTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RESTAURANTS_TXT"));
        LBL_ADD = generalFunc.retrieveLangLBl("", "LBL_ADD");

    }

    public Context getActContext() {
        return RestaurantsSearchActivity.this;
    }

    @Override
    public void setOnRestaurantclick(int position) {
        HashMap<String, String> posData = restaurantArr_List.get(position);
        Bundle bn = new Bundle();
        bn.putString("iCompanyId", posData.get("iCompanyId"));
        bn.putString("Restaurant_Status", posData.get("Restaurant_Status"));
        bn.putString("Restaurant_Safety_Status",  posData.get("Restaurant_Safety_Status"));
        bn.putString("Restaurant_Safety_Icon",  posData.get("Restaurant_Safety_Icon"));
        bn.putString("Restaurant_Safety_URL",  posData.get("Restaurant_Safety_URL"));
        bn.putString("ispriceshow", posData.get("ispriceshow"));
        bn.putString("lat", latitude + "");
        bn.putString("long", longitude + "");
        bn.putString("eAvailable", posData.get("eAvailable"));
        bn.putString("timeslotavailable", posData.get("timeslotavailable"));

        new StartActProcess(getActContext()).startActForResult(RestaurantAllDetailsNewActivity.class, bn,111);

    }

    @Override
    public void setOnRestaurantclick(int position, boolean liked) {

    }

    @Override
    public void onItemClickList(View v, int position) {
        HashMap<String, String> mapData = itemArr_List.get(position);
        Bundle bn = new Bundle();
        HashMap<String, String> map = new HashMap<>();
        map.put("iMenuItemId", mapData.get("iMenuItemId"));
        map.put("iFoodMenuId", mapData.get("iFoodMenuId"));
        map.put("vItemType", mapData.get("vItemType"));
        map.put("vItemDesc", mapData.get("vItemDesc"));
        map.put("fPrice", mapData.get("fPrice"));
        map.put("eFoodType", mapData.get("eFoodType"));
        map.put("fOfferAmt", mapData.get("fOfferAmt"));
        map.put("vImage", mapData.get("vImage"));
        map.put("iDisplayOrder", mapData.get("iDisplayOrder"));
        map.put("StrikeoutPrice", mapData.get("StrikeoutPrice"));
        map.put("fDiscountPrice", mapData.get("fDiscountPrice"));
        map.put("fDiscountPricewithsymbol", mapData.get("fDiscountPricewithsymbol"));
        map.put("MenuItemOptionToppingArr", mapData.get("MenuItemOptionToppingArr"));
        map.put("currencySymbol", mapData.get("currencySymbol"));
        map.put("iCompanyId", mapData.get("iCompanyId"));
        map.put("vCompany", mapData.get("vCompany"));
        map.put("fMinOrderValue", mapData.get("fMinOrderValue"));
        map.put("iMaxItemQty", mapData.get("iMaxItemQty"));
        map.put("Restaurant_Status", getIntent().getStringExtra("Restaurant_Status"));
        map.put("ispriceshow", mapData.get("ispriceshow"));
        bn.putSerializable("data", map);
        bn.putString("timeslotavailable",mapData.get("timeslotavailable"));
        bn.putString("eAvailable", mapData.get("eAvailable"));
        new StartActProcess(getActContext()).startActForResult(AddBasketActivity.class, bn, Utils.ADD_TO_BASKET);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(getActContext());

            if (i == R.id.imageCancel) {
                loaderView.setVisibility(View.GONE);
                norecordTxt.setVisibility(View.GONE);
                searchTxtView.setText("");
            } else if (i == R.id.cancelTxt) {
                onBackPressed();
            }


        }
    }


    @Override
    public void onBackPressed() {

        if (isFavChange) {
            Bundle bn = new Bundle();
            (new StartActProcess(getActContext())).setOkResult(bn);
            finish();
            return;
        }

        super.onBackPressed();
    }

    private void getRestaurantList(String searchword) {
        loaderView.setVisibility(View.VISIBLE);
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "loadSearchRestaurants");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("PassengerLat", "" + latitude);
        parameters.put("PassengerLon", "" + longitude);
        parameters.put("searchword", searchword.trim());
        if (getIntent().getStringExtra("address") != null && !getIntent().getStringExtra("address").equals("")) {
            parameters.put("vAddress", getIntent().getStringExtra("address"));
        }
        parameters.put("eSystem", Utils.eSystem_Type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {
                JSONObject responseObj=generalFunc.getJsonObject(responseString);

                if (responseObj != null && !responseObj.equals("")) {

                    loaderView.setVisibility(View.GONE);
                    boolean isShowSearchedItemEnabled=generalFunc.getJsonValueStr("isShowSearchedItemEnabled",responseObj).equalsIgnoreCase("Yes");
                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseObj);

                    if (cusinecontainArea.getChildCount() > 0) {
                        cusinecontainArea.removeAllViewsInLayout();
                    }
                    restaurantArr_List.clear();
                    itemArr_List.clear();
                    norecordTxt.setVisibility(View.GONE);
                    if (isDataAvail == true) {

                        JSONArray messageItemObj = generalFunc.getJsonArray("message_item", responseObj);
                        int width = (int) getActContext().getResources().getDimension(R.dimen._225sdp);
                        int heightOfImage = (int) getActContext().getResources().getDimension(R.dimen._95sdp);

                        if (messageItemObj != null) {
                            for (int j = 0; j < messageItemObj.length(); j++) {
                                JSONObject category_obj = generalFunc.getJsonObject(messageItemObj, j);

                                HashMap<String, String> MenuObj = new HashMap<>();


                                MenuObj.put("fPrice", generalFunc.getJsonValueStr("fPrice", category_obj));
                                MenuObj.put("isFromSearch", "Yes");
                                MenuObj.put("iDisplayOrder", generalFunc.getJsonValueStr("iDisplayOrder", category_obj));
                                MenuObj.put("iCompanyId", generalFunc.getJsonValueStr("iCompanyId", category_obj));
                                MenuObj.put("iFoodMenuId", generalFunc.getJsonValueStr("iFoodMenuId", category_obj));
                                MenuObj.put("iMenuItemId", generalFunc.getJsonValueStr("iMenuItemId", category_obj));
                                String vImage = generalFunc.getJsonValueStr("vImage", category_obj);
                                MenuObj.put("vImage", Utils.checkText(vImage) ? vImage : "");
                                MenuObj.put("vImageResized", Utils.checkText(vImage) ? Utils.getResizeImgURL(getActContext(), vImage, 0, heightOfImage, width) : "");

                                MenuObj.put("vItemType", generalFunc.getJsonValueStr("vItemType", category_obj));
                                MenuObj.put("vCompany", generalFunc.getJsonValueStr("vCompany", category_obj));
                                MenuObj.put("vItemDesc", generalFunc.getJsonValueStr("vItemDesc", category_obj));
                                String fOfferAmt = generalFunc.getJsonValueStr("fOfferAmt", category_obj);
                                MenuObj.put("fOfferAmt", fOfferAmt);
                                MenuObj.put("fOfferAmtNotZero", generalFunc.parseDoubleValue(0, fOfferAmt) > 0 ? "Yes" : "No");

                                String StrikeoutPrice = generalFunc.getJsonValueStr("StrikeoutPrice", category_obj);
                                MenuObj.put("StrikeoutPrice", StrikeoutPrice);
                                MenuObj.put("StrikeoutPriceConverted", generalFunc.convertNumberWithRTL(StrikeoutPrice));

                                MenuObj.put("fDiscountPrice", generalFunc.getJsonValueStr("fDiscountPrice", category_obj));
                                MenuObj.put("eFoodType", generalFunc.getJsonValueStr("eFoodType", category_obj));

                                String fDiscountPricewithsymbol = generalFunc.getJsonValueStr("fDiscountPricewithsymbol", category_obj);
                                MenuObj.put("fDiscountPricewithsymbol", fDiscountPricewithsymbol);
                                MenuObj.put("fDiscountPricewithsymbolConverted", generalFunc.convertNumberWithRTL(generalFunc.convertNumberWithRTL(fDiscountPricewithsymbol)));

                                MenuObj.put("currencySymbol", generalFunc.getJsonValueStr("currencySymbol", category_obj));
                                MenuObj.put("Restaurant_Status", generalFunc.getJsonValueStr("Restaurant_Status", category_obj));
                                MenuObj.put("fMinOrderValue", generalFunc.getJsonValueStr("fMinOrderValue", category_obj));
                                MenuObj.put("iMaxItemQty", generalFunc.getJsonValueStr("iMaxItemQty", category_obj));
                                MenuObj.put("ispriceshow", generalFunc.getJsonValueStr("ispriceshow", category_obj));
                                MenuObj.put("MenuItemOptionToppingArr", generalFunc.getJsonValueStr("MenuItemOptionToppingArr", category_obj));
                                String vAvgRating = generalFunc.getJsonValueStr("vAvgRating", category_obj);
                                MenuObj.put("vAvgRating", vAvgRating);
                                MenuObj.put("vAvgRatingConverted", generalFunc.convertNumberWithRTL(vAvgRating));
                                String vHighlightName = generalFunc.getJsonValueStr("vHighlightName", category_obj);
                                MenuObj.put("vHighlightName", vHighlightName);
                                MenuObj.put("vHighlightNameLBL", generalFunc.retrieveLangLBl("", vHighlightName));


                                MenuObj.put("prescription_required", generalFunc.getJsonValueStr("prescription_required", category_obj));
                                MenuObj.put("LBL_ADD", LBL_ADD);

                                MenuObj.put("isLastLine", "Yes");

                                if (j == messageItemObj.length() - 1 && j != messageItemObj.length() - 1) {
                                    MenuObj.put("isLastLine", "No");
                                }

                                MenuObj.put("Type", "LIST");
                                MenuObj.put("isExpand", "No");
                                //  MenuObj.put("Type", "GRID");

//                                int numOfColumn = getActContext() == null ? 2 : getNumOfColumns();
//                                int heightOfImage = (int) (((Utils.getScreenPixelWidth(getActContext()) / numOfColumn) - Utils.dipToPixels(getActContext(), 16)) / 1.77777778);
//                                int width = ((int) Utils.getScreenPixelWidth(getActContext()) / numOfColumn) - Utils.dipToPixels(getActContext(), 16);
//                                MenuObj.put("vImage", Utils.getResizeImgURL(getActContext(), generalFunc.getJsonValueStr("vImage", category_obj), width, heightOfImage));
//                                MenuObj.put("heightOfImage", "" + heightOfImage);

//                                int numOfColumn = getActContext() == null ? 2 : getNumOfColumns();
//                                Logger.d("numOfColumn", "::" + numOfColumn);
//                                int heightOfImage = (int) (((Utils.getScreenPixelWidth(getActContext()) / numOfColumn) - Utils.dipToPixels(getActContext(), 16)) / 1.77777778);
//                                int width = ((int) Utils.getScreenPixelWidth(getActContext()) / numOfColumn) - Utils.dipToPixels(getActContext(), 16);
//                                MenuObj.put("vImage", Utils.getResizeImgURL(getActContext(), generalFunc.getJsonValueStr("vImage", category_obj), width, heightOfImage));
//                                MenuObj.put("heightOfImage", "" + heightOfImage);


                                itemArr_List.add(MenuObj);

                            }
                        }


                        JSONArray restaurant_Arr = generalFunc.getJsonArray("message", responseObj);

                        if (restaurant_Arr != null)
                        {
                            String LBL_OPEN_NOW="",LBL_CLOSED_TXT="",LBL_NOT_ACCEPT_ORDERS_TXT="";

                            if (restaurant_Arr.length()>0)
                            {
                                LBL_OPEN_NOW=generalFunc.retrieveLangLBl("Open Now", "LBL_OPEN_NOW");
                                LBL_CLOSED_TXT=generalFunc.retrieveLangLBl("close", "LBL_CLOSED_TXT");
                                LBL_NOT_ACCEPT_ORDERS_TXT= generalFunc.retrieveLangLBl("", "LBL_NOT_ACCEPT_ORDERS_TXT");
                            }
                            for (int i = 0; i < restaurant_Arr.length(); i++) {

                                HashMap<String, String> map = new HashMap<String, String>();
                                JSONObject restaurant_Obj = generalFunc.getJsonObject(restaurant_Arr, i);
                                map.put("vCompany", generalFunc.getJsonValueStr("vCompany", restaurant_Obj));
                                map.put("tClocation", generalFunc.getJsonValueStr("tClocation", restaurant_Obj));
                                map.put("iCompanyId", generalFunc.getJsonValueStr("iCompanyId", restaurant_Obj));
                                map.put("vPhone", generalFunc.getJsonValueStr("vPhone", restaurant_Obj));
                                String vImage=generalFunc.getJsonValueStr("vImage", restaurant_Obj);
                                map.put("vImage", Utils.checkText(vImage)?vImage:"https");

                                map.put("vLatitude", generalFunc.getJsonValueStr("vLatitude", restaurant_Obj));
                                map.put("vLongitude", generalFunc.getJsonValueStr("vLongitude", restaurant_Obj));

                                map.put("vFromTimeSlot1", generalFunc.getJsonValueStr("vFromTimeSlot1", restaurant_Obj));
                                map.put("vToTimeSlot1", generalFunc.getJsonValueStr("vToTimeSlot1", restaurant_Obj));
                                map.put("vFromTimeSlot2", generalFunc.getJsonValueStr("vFromTimeSlot2", restaurant_Obj));
                                map.put("vToTimeSlot2", generalFunc.getJsonValueStr("vToTimeSlot2", restaurant_Obj));
                                map.put("fMinOrderValue", generalFunc.getJsonValueStr("fMinOrderValue", restaurant_Obj));
                                map.put("Restaurant_Cuisine", generalFunc.getJsonValueStr("Restaurant_Cuisine", restaurant_Obj));
                                map.put("fPrepareTime", generalFunc.getJsonValueStr("fPrepareTime", restaurant_Obj));
                                map.put("Restaurant_Status", generalFunc.getJsonValueStr("restaurantstatus", restaurant_Obj));
                                map.put("Restaurant_Safety_Status", generalFunc.getJsonValueStr("Restaurant_Safety_Status", restaurant_Obj));
                                map.put("Restaurant_Safety_Icon", generalFunc.getJsonValueStr("Restaurant_Safety_Icon", restaurant_Obj));
                                map.put("Restaurant_Safety_URL", generalFunc.getJsonValueStr("Restaurant_Safety_URL", restaurant_Obj));

                                String Restaurant_Opentime = generalFunc.getJsonValueStr("Restaurant_Opentime", restaurant_Obj);
                                map.put("Restaurant_Opentime", Restaurant_Opentime);
                                map.put("Restaurant_OpentimeConverted", generalFunc.convertNumberWithRTL(Restaurant_Opentime));

                                map.put("Restaurant_Closetime", generalFunc.getJsonValueStr("Restaurant_Closetime", restaurant_Obj));
                                map.put("Restaurant_OfferMessage", generalFunc.getJsonValueStr("Restaurant_OfferMessage", restaurant_Obj));
                                String vAvgRating = generalFunc.getJsonValueStr("vAvgRating", restaurant_Obj);
                                map.put("vAvgRating", vAvgRating);
                                map.put("vAvgRatingConverted", generalFunc.convertNumberWithRTL(vAvgRating));

                                String Restaurant_PricePerPerson = generalFunc.getJsonValueStr("Restaurant_PricePerPerson", restaurant_Obj);
                                map.put("Restaurant_PricePerPerson", Restaurant_PricePerPerson);
                                map.put("Restaurant_PricePerPersonConverted", generalFunc.convertNumberWithRTL(Restaurant_PricePerPerson));

                                String Restaurant_OrderPrepareTime = generalFunc.getJsonValueStr("Restaurant_OrderPrepareTime", restaurant_Obj);
                                map.put("Restaurant_OrderPrepareTime", Restaurant_OrderPrepareTime);
                                map.put("Restaurant_OrderPrepareTimeConverted", generalFunc.convertNumberWithRTL(Restaurant_OrderPrepareTime));


                               // String Restaurant_OfferMessage_short = generalFunc.getJsonValueStr("Restaurant_OfferMessage_short", restaurant_Obj);
                                String Restaurant_OfferMessage_short = generalFunc.getJsonValueStr("Restaurant_OfferMessage", restaurant_Obj);
                                map.put("Restaurant_OfferMessage_short", Restaurant_OfferMessage_short);
                                map.put("Restaurant_OfferMessage_shortConverted", generalFunc.convertNumberWithRTL(Restaurant_OfferMessage_short));

                                map.put("Restaurant_MinOrderValue", generalFunc.getJsonValueStr("Restaurant_MinOrderValue_Orig", restaurant_Obj));
                                map.put("eAvailable", generalFunc.getJsonValueStr("eAvailable", restaurant_Obj));
                                map.put("timeslotavailable", generalFunc.getJsonValueStr("timeslotavailable", restaurant_Obj));
                                map.put("eFavStore", generalFunc.getJsonValueStr("eFavStore", restaurant_Obj));

                                map.put("LBL_OPEN_NOW", LBL_OPEN_NOW);
                                map.put("LBL_CLOSED_TXT",LBL_CLOSED_TXT);
                                map.put("LBL_NOT_ACCEPT_ORDERS_TXT",LBL_NOT_ACCEPT_ORDERS_TXT);

                                map.put("ispriceshow", generalFunc.getJsonValueStr("ispriceshow", responseObj));
                                restaurantArr_List.add(map);
                            }
                    }

                        cuisineList.clear();
                        JSONArray cusine_array = generalFunc.getJsonArray("message_cusine", responseObj);

                        if(cusine_array!=null) {
                            for (int i = 0; i < cusine_array.length(); i++) {

                                HashMap<String, String> map = new HashMap<String, String>();
                                JSONObject cusine_Obj = generalFunc.getJsonObject(cusine_array, i);
                                map.put("cuisineId", generalFunc.getJsonValueStr("cuisineId", cusine_Obj));
                                map.put("cuisineName", generalFunc.getJsonValueStr("cuisineName", cusine_Obj));
                                map.put("TotalRestaurant", generalFunc.getJsonValueStr("TotalRestaurant", cusine_Obj));
                                map.put("TotalRestaurantWithLabel", generalFunc.getJsonValueStr("TotalRestaurantWithLabel", cusine_Obj));
                                cuisineList.add(map);
                            }
                        }

                        if (isShowSearchedItemEnabled)
                        {
                            restaurantsTitleTxt.setVisibility(View.GONE);
                            cusineTitleTxt.setVisibility(View.GONE);
                            setPos1Data();
                            appLogin_view_pager.setCurrentItem(0);
                            tabsArea.setVisibility(View.VISIBLE);
                            setUpViewPagerListner();
                        }
                        else
                        {
                            tabsArea.setVisibility(View.GONE);
                            appLogin_view_pager.setOnPageChangeListener(null);
                            restaurantsTitleTxt.setVisibility(View.VISIBLE);
                            cusineTitleTxt.setVisibility(View.GONE);

                            if (restaurant_Arr != null && restaurant_Arr.length() > 0) {
                                restaurantsArea.setVisibility(View.VISIBLE);
                            } else {
                                restaurantsArea.setVisibility(View.GONE);
                            }

                            if (cuisineList != null && cuisineList.size() > 0) {
                                cusineArea.setVisibility(View.VISIBLE);
                            } else {
                                cusineArea.setVisibility(View.GONE);
                            }

                            if (restaurant_Arr.length() == 0 && cuisineList.size() == 0) {
                                noSearchImage.setVisibility(View.VISIBLE);
                            } else {
                                noSearchImage.setVisibility(View.GONE);
                            }

                        }

                        addCusineView();



                    } else {
                        if (isShowSearchedItemEnabled)
                        {
                            restaurantsTitleTxt.setVisibility(View.GONE);
                            cusineTitleTxt.setVisibility(View.GONE);
                            setPos1Data();
                            appLogin_view_pager.setCurrentItem(0);
                            tabsArea.setVisibility(View.VISIBLE);
                        }else {
                            tabsArea.setVisibility(View.GONE);
                            if (restaurantArr_List != null && restaurantArr_List.size() == 0) {
                                norecordTxt.setVisibility(View.VISIBLE);
                                restaurantsArea.setVisibility(View.GONE);
                                cusineArea.setVisibility(View.GONE);
                                noSearchImage.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                    restaurantAdapter.notifyDataSetChanged();
                    restaurantItemAdapter.notifyDataSetChanged();
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    private void setUpViewPagerListner() {

        appLogin_view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Logger.d("onPageScrolled", "::" + position);

            }

            @Override
            public void onPageSelected(int position) {
//                selTabPos = position;
                Logger.d("onPageScrolled", "::" + "onPageSelected");
                fragmentList.get(position).onResume();
                if (position==0)
                {
                    setPos1Data();

                }else if (position==1)
                {
                    if (itemArr_List != null && itemArr_List.size() == 0) {
                        norecordTxt.setVisibility(View.VISIBLE);
                        restaurantsArea.setVisibility(View.GONE);
                        itemsArea.setVisibility(View.GONE);
                        cusineArea.setVisibility(View.GONE);
                        noSearchImage.setVisibility(View.VISIBLE);

                    }else
                    {
                        norecordTxt.setVisibility(View.GONE);
                        restaurantsArea.setVisibility(View.GONE);
                        itemsArea.setVisibility(View.VISIBLE);
                        cusineArea.setVisibility(View.GONE);
                        noSearchImage.setVisibility(View.GONE);
                    }
                }else if (position==2)
                {
                    if (cuisineList != null && cuisineList.size() == 0) {
                        norecordTxt.setVisibility(View.VISIBLE);
                        restaurantsArea.setVisibility(View.GONE);
                        cusineArea.setVisibility(View.GONE);
                        itemsArea.setVisibility(View.GONE);
                        noSearchImage.setVisibility(View.VISIBLE);

                    }else
                    {
                        norecordTxt.setVisibility(View.GONE);
                        restaurantsArea.setVisibility(View.GONE);
                        itemsArea.setVisibility(View.GONE);
                        cusineArea.setVisibility(View.VISIBLE);
                        noSearchImage.setVisibility(View.GONE);
                    }
                }
//                selFilterType = "";
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Logger.d("onPageScrolled", "::" + "onPageScrollStateChanged");
            }
        });
    }

    public void addCusineView() {

        if (cuisineList != null && cuisineList.size() > 0) {

            for (int i = 0; i < cuisineList.size(); i++) {
                int pos = i;
                HashMap<String, String> posData = cuisineList.get(i);

                LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.item_cusines, null);
                MTextView cusineName = (MTextView) view.findViewById(R.id.cusineName);
                MTextView totalRest = (MTextView) view.findViewById(R.id.totalRest);
                LinearLayout rowArea = (LinearLayout) view.findViewById(R.id.rowArea);
                cusineName.setText(posData.get("cuisineName"));
                totalRest.setText(posData.get("TotalRestaurantWithLabel"));
                rowArea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bn = new Bundle();
                        bn.putString("cid", cuisineList.get(pos).get("cuisineId"));
                        bn.putString("cname", cuisineList.get(pos).get("cuisineName"));
                        bn.putDouble("lat", latitude);
                        bn.putDouble("long", longitude);
                        new StartActProcess(getActContext()).startActWithData(SearchRestaurantListActivity.class, bn);
                    }
                });

                cusinecontainArea.addView(view);
            }
        } else {
            if (cusinecontainArea.getChildCount() > 0) {
                cusinecontainArea.removeAllViewsInLayout();
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


          if (requestCode == 111 && resultCode == RESULT_OK) {
              isFavChange=data.getBooleanExtra("isFavChange",false);
              getRestaurantList(searchTxtView.getText().toString().trim());

        }
    }
}
