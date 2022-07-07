package com.ridetogo.user.deliverAll;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.ViewPagerCards.RoundCornerDrawable;
import com.adapter.files.deliverAll.MultiItemOptionAddonPagerAdapter;
import com.ridetogo.user.PrescriptionActivity;
import com.ridetogo.user.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.realmModel.Cart;
import com.realmModel.Options;
import com.realmModel.Topping;
import com.shuhart.stepview.StepView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import com.utils.Logger;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class EditCartActivity extends AppCompatActivity {

    ImageView backImgView;
    MTextView titleTxt;
    private JSONObject obj_userProfile;
    GeneralFunctions generalFunc;
    MButton btn_type2;
    int submitBtnId;
    MTextView vHotelNameTxt;
    MTextView totalHTxt;
    MTextView totalVTxt;

    ImageView restaurantImgView;
    View restaurantImgContainerView;

    MTextView vHotelAddressTxt;
    LinearLayout itemContainer, bottomArea;
    double finalTotal = 0;
    RealmResults<Cart> realmCartList;
    ArrayList<Cart> cartList;
    ScrollView mainScrollView;

    RealmResults<Options> realmOptionResult;
    RealmResults<Topping> realmToppingResult;

    String currencySymbol;
    ImageView nocartImage;

    MTextView totalNoteTxt;
    CardView totalarea;

    RelativeLayout nocartarea;
    MTextView nocartTitleTxt, nocartMsgTxt;

    boolean isRestaurantDataLoaded = false;

    LinearLayout cartCountarea;
    StepView step_view;
    HorizontalScrollView stepScrollview;
    int x, y=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cart);
        initView();
        //  getLocalData();
        //  realmOptionResult = getOptionsData();
        //realmToppingResult = getToppingData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getLocalData();
    }

    public void getLocalData() {
        try {
            finalTotal = 0;
            realmCartList = getCartData();
            if (realmCartList.size() > 0) {
                cartList = new ArrayList<>(realmCartList);
                nocartarea.setVisibility(View.GONE);
                totalarea.setVisibility(View.VISIBLE);
                cartCountarea.setVisibility(View.VISIBLE);
                setData();
            } else {
                bottomArea.setVisibility(View.GONE);
                mainScrollView.setVisibility(View.GONE);
                nocartarea.setVisibility(View.VISIBLE);
                totalarea.setVisibility(View.GONE);
                cartCountarea.setVisibility(View.GONE);
                deleteOptionToRealm();
                if (Utils.checkText(generalFunc.getMemberId())) {
                    removeCartCall();
                }
            }
        } catch (Exception e) {
            Log.e("Ex", "::" + e.toString());

        }
    }

    public void removeCartCall() {
        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "removePrescriptionImagesForCart");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("eSystem", Utils.eSystem_Type);

        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString) == true) {


                } else {

                }
            } else {
            }
        });
        exeWebServer.execute();
    }


    public RealmResults<Options> getOptionsData(String iMenuItemId, String iFoodMenuId) {
        Realm realm = MyApp.getRealmInstance();
        return realm.where(Options.class).equalTo("iMenuItemId", iMenuItemId).equalTo("iFoodMenuId", iFoodMenuId).findAll();
    }

    public RealmResults<Topping> getToppingData(String iMenuItemId, String iFoodMenuId) {
        Realm realm = MyApp.getRealmInstance();
        return realm.where(Topping.class).equalTo("iMenuItemId", iMenuItemId).equalTo("iFoodMenuId", iFoodMenuId).findAll();
    }

    public RealmResults<Cart> getCartData() {
        Realm realm = MyApp.getRealmInstance();
        return realm.where(Cart.class).findAll();
    }


    public void setData() {
        if (itemContainer.getChildCount() > 0) {
            itemContainer.removeAllViewsInLayout();
        }
        addItemView();

        Cart data = cartList.get(0);
        if (!isRestaurantDataLoaded) {
            vHotelNameTxt.setText(data.getvCompany());
            vHotelAddressTxt.setText("");

            getRestaurantData(data.getiCompanyId());
        }
    }


    public Context getActContext() {
        return EditCartActivity.this;
    }

    public void initView() {
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        obj_userProfile = generalFunc.getJsonObject(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        cartCountarea = (LinearLayout) findViewById(R.id.cartCountarea);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        vHotelNameTxt = (MTextView) findViewById(R.id.vHotelNameTxt);
        vHotelAddressTxt = (MTextView) findViewById(R.id.vHotelAddressTxt);
        itemContainer = (LinearLayout) findViewById(R.id.itemContainer);
        nocartImage = (ImageView) findViewById(R.id.nocartImage);
        bottomArea = (LinearLayout) findViewById(R.id.bottomArea);
        totalVTxt = (MTextView) findViewById(R.id.totalVTxt);
        totalHTxt = (MTextView) findViewById(R.id.totalHTxt);
        mainScrollView = (ScrollView) findViewById(R.id.mainScrollView);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        totalNoteTxt = (MTextView) findViewById(R.id.totalNoteTxt);
        totalarea = (CardView) findViewById(R.id.totalarea);
        nocartarea = (RelativeLayout) findViewById(R.id.nocartarea);
        nocartTitleTxt = (MTextView) findViewById(R.id.nocartTitleTxt);
        nocartMsgTxt = (MTextView) findViewById(R.id.nocartMsgTxt);
        restaurantImgView = findViewById(R.id.restaurantImgView);
        restaurantImgContainerView = findViewById(R.id.restaurantImgContainerView);

        //submitBtnId = Utils.generateViewId();
        // btn_type2.setId(submitBtnId);
        btn_type2.setOnClickListener(new setOnClickList());
        backImgView.setOnClickListener(new setOnClickList());

        setLabel();


    }

    public void getRestaurantData(String iCompanyId) {
        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GetRestaurantDetails");
        parameters.put("iCompanyId", iCompanyId);
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("appType", Utils.app_type);
        if (generalFunc.getMemberId().equals("")) {
            parameters.put("vLang", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));
        }
        parameters.put("eSystem", Utils.eSystem_Type);

        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString) == true) {

                    JSONObject msg_obj = generalFunc.getJsonObject(Utils.message_str, responseString);

                    if (msg_obj != null) {
                        isRestaurantDataLoaded = true;
                        String imageURL = Utils.getResizeImgURL(getActContext(), generalFunc.getJsonValueStr("vImage", msg_obj), getResources().getDimensionPixelSize(R.dimen.cart_restaurant_img_size), getResources().getDimensionPixelSize(R.dimen.cart_restaurant_img_size));

                        String vCaddress = generalFunc.getJsonValueStr("vCaddress", msg_obj);

                        vHotelAddressTxt.setText(vCaddress);
                        int height = (int) getResources().getDimension(R.dimen._100sdp);
                        int width = (int) getResources().getDimension(R.dimen._100sdp);

                        Picasso.get()
                                .load(imageURL)
                                .placeholder(R.mipmap.ic_no_icon)
                                .resize(width, height)
                                .into(restaurantImgView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        try {
                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                                restaurantImgView.invalidate();
                                                CardView view = findViewById(R.id.mCardView);
                                                BitmapDrawable drawable = (BitmapDrawable) restaurantImgView.getDrawable();
                                                Bitmap bitmap = drawable.getBitmap();
                                                view.setPreventCornerOverlap(false);

                                                RoundCornerDrawable round = new RoundCornerDrawable(bitmap, getResources().getDimension(R.dimen._10sdp), 0);
                                                restaurantImgView.setVisibility(View.GONE);
                                                view.setBackground(round);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                    }

                } else {

                }
            } else {
            }
        });
        exeWebServer.execute();
    }

    public String getOptionPrice(String id) {
        String optionPrice = "";
        Realm realm = MyApp.getRealmInstance();
        Options options = realm.where(Options.class).equalTo("iOptionId", id).findFirst();
        if (options != null) {
            return options.getfUserPrice();
        }
        return optionPrice;
    }


    public Options getOptionObject(String id) {
        Realm realm = MyApp.getRealmInstance();
        Options options = realm.where(Options.class).equalTo("iOptionId", id).findFirst();
        if (options != null) {
            return options;
        }
        return null;
    }

    public Topping getToppingObject(String id) {

        Realm realm = MyApp.getRealmInstance();
        Topping topping = realm.where(Topping.class).equalTo("iOptionId", id).findFirst();
        if (topping != null) {
            return topping;
        }
        return topping;

    }

    public String getToppingPrice(String id) {
        String optionPrice = "";

        Realm realm = MyApp.getRealmInstance();
        Topping options = realm.where(Topping.class).equalTo("iOptionId", id).findFirst();

        if (options != null) {
            return options.getfUserPrice();
        }


        return optionPrice;

    }


    public Cart checksameRecordExist(int pos, String toppingId, String optionId, String iFoodMenuId, String iMenuItemId) {
        Cart cart = null;

        if (cartList.get(pos).getIsOption().equalsIgnoreCase("Yes")) {

            String[] list_topping_ids = toppingId.split(",");
            List<String> list_topping_ids_list = Arrays.asList(list_topping_ids);
            Collections.sort(list_topping_ids_list);
            Realm realm = MyApp.getRealmInstance();

            RealmResults<Cart> cartlist = realm.where(Cart.class).findAll();

            if (cartlist != null && cartlist.size() > 0)

                for (int i = 0; i < realmCartList.size(); i++) {
                    Cart indexData = realmCartList.get(i);
                    String[] topping_ids = indexData.getiToppingId().split(",");
                    List<String> topping_idsList = Arrays.asList(topping_ids);
                    Collections.sort(topping_idsList);
                    if (topping_idsList.equals(list_topping_ids_list) &&
                            indexData.getiOptionId().equalsIgnoreCase(optionId) && indexData.getiFoodMenuId().equalsIgnoreCase(iFoodMenuId) && indexData.getiMenuItemId().equalsIgnoreCase(iMenuItemId)) {
                        return realmCartList.get(i);
                    }
                }


            // cart = realm.where(Cart.class).("iToppingId", list_topping_ids).equalTo("iOptionId", optionId).findFirst();

            return cart;
        }


        return cart;


    }

    public void addItemView() {
        int color = getResources().getColor(R.color.appThemeColor_1);
        int btnRadius = Utils.dipToPixels(getActContext(), 15);
        int strokeColor = Color.parseColor("#FFFFFF");
        String LBL_CUSTOMIZE = generalFunc.retrieveLangLBl("", "LBL_CUSTOMIZE");

        for (int i = 0; i < cartList.size(); i++) {
            currencySymbol = cartList.get(i).getCurrencySymbol();
            int pos = i;
            LayoutInflater topinginflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View editCartView = topinginflater.inflate(R.layout.item_edit_cart_row, null);
            MTextView itemNameTxtView = (MTextView) editCartView.findViewById(R.id.itemNameTxtView);
            MTextView itemsubNameTxtView = (MTextView) editCartView.findViewById(R.id.itemsubNameTxtView);
            MTextView itemPriceTxtView = (MTextView) editCartView.findViewById(R.id.itemPriceTxtView);
            MTextView QTYNumberTxtView = (MTextView) editCartView.findViewById(R.id.QTYNumberTxtView);
            View editOptionsArea = editCartView.findViewById(R.id.editOptionsArea);
            MTextView customizeTxtView = (MTextView) editCartView.findViewById(R.id.customizeTxtView);
            ImageView minusImgView = (ImageView) editCartView.findViewById(R.id.minusImgView);
            ImageView addImgView = (ImageView) editCartView.findViewById(R.id.addImgView);

            ImageView itemTypeImgView = (ImageView) editCartView.findViewById(R.id.itemTypeImgView);

            Cart itemData = cartList.get(pos);


            customizeTxtView.setText(LBL_CUSTOMIZE);
            if (itemData.getIsTooping().equalsIgnoreCase("Yes") || itemData.getIsOption().equalsIgnoreCase("Yes")) {
                editOptionsArea.setVisibility(View.VISIBLE);
            } else {
                editOptionsArea.setVisibility(View.GONE);
            }

            if (itemData.geteFoodType().equalsIgnoreCase("NonVeg")) {
                itemTypeImgView.setVisibility(View.VISIBLE);
                itemTypeImgView.setImageResource(R.drawable.nonveg);
            } else if (itemData.geteFoodType().equalsIgnoreCase("Veg")) {
                itemTypeImgView.setVisibility(View.VISIBLE);
            }

            double total = GeneralFunctions.parseDoubleValue(0, cartList.get(i).getfDiscountPrice());
            String[] selToppingarray = itemData.getiToppingId().split(",");
            double toppingPrice = 0;

            if (selToppingarray != null && selToppingarray.length > 0) {
                for (String s : selToppingarray) {
                    toppingPrice = toppingPrice + GeneralFunctions.parseDoubleValue(0, getToppingPrice(s));
                }
            }
            double optionPrice = 0;
            String itemList = "";
            String optionId = itemData.getiOptionId();
            Options options = null;
            try {
                String[] selOptionarray = optionId.split(",");
                int optionSize = selOptionarray.length;
                if (selOptionarray != null && optionSize > 0) {
                    for (String s : selOptionarray) {
                        optionPrice = optionPrice + GeneralFunctions.parseDoubleValue(0, getOptionPrice(s));
                    }
                }

                if (optionId != null && !optionId.equalsIgnoreCase("")) {
                    for (String s : selOptionarray) {
                        options = getOptionObject(s);
                        if (options != null) {
                            if (itemList.equalsIgnoreCase("")) {
                                itemList = options.getvOptionName();
                            } else {
                                itemList = itemList + "," + options.getvOptionName();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                if (optionId != null && !optionId.equalsIgnoreCase("")) {
                    options = getOptionObject(optionId);
                    if (options != null) {
                        itemList = options.getvOptionName();
                        optionPrice = GeneralFunctions.parseDoubleValue(0, options.getfUserPrice());
                    }
                }
            }
            String toppingId = itemData.getiToppingId();
            if (toppingId != null && !toppingId.equalsIgnoreCase("")) {
                for (String s : selToppingarray) {
                    Topping topping = getToppingObject(s);
                    if (topping != null) {
                        if (itemList.equalsIgnoreCase("")) {
                            itemList = topping.getvOptionName();
                        } else {
                            itemList = itemList + "," + topping.getvOptionName();
                        }
                    }
                }
            }

            if (cartList.get(i).getIspriceshow().equalsIgnoreCase("separate") && options != null) {
                total = GeneralFunctions.parseDoubleValue(0, cartList.get(i).getQty()) * (optionPrice + toppingPrice);

            } else {
                total = GeneralFunctions.parseDoubleValue(0, cartList.get(i).getQty()) * (total + toppingPrice + optionPrice);

            }
            itemPriceTxtView.setText(currencySymbol + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(total)));
            finalTotal = finalTotal + total;
            itemNameTxtView.setText(cartList.get(i).getvItemType());


            if (!itemList.equalsIgnoreCase("")) {
                itemsubNameTxtView.setVisibility(View.VISIBLE);
                itemsubNameTxtView.setText(itemList);
            } else {
                itemsubNameTxtView.setVisibility(View.GONE);
            }

            QTYNumberTxtView.setText(generalFunc.convertNumberWithRTL(cartList.get(i).getQty()));

            minusImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int QUANTITY = Integer.parseInt(QTYNumberTxtView.getText().toString());
                    if (QUANTITY > 1) {
                        finalTotal = finalTotal - Math.abs(QUANTITY *
                                GeneralFunctions.parseDoubleValue(0, itemData.getfDiscountPrice()));

                        QUANTITY = QUANTITY - 1;
                        updateQty(pos, QUANTITY + "");
                        getLocalData();

                    } else {
                        GenerateAlertBox generateAlertBox = new GenerateAlertBox(getActContext());
                        generateAlertBox.setCancelable(false);
                        generateAlertBox.setContentMessage(generalFunc.retrieveLangLBl("", "LBL_REMOVE_TEXT"), generalFunc.retrieveLangLBl("", "LBL_DELETE_CART_ITEM"));
                        generateAlertBox.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_NO"));
                        generateAlertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_YES"));
                        generateAlertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                            @Override
                            public void handleBtnClick(int btn_id) {
                                if (btn_id == 1) {
                                    Realm realm = MyApp.getRealmInstance();
                                    realm.beginTransaction();
                                    Cart cart = realmCartList.get(pos);
                                    cart.deleteFromRealm();
                                    realm.commitTransaction();
                                    generateAlertBox.closeAlertBox();
                                    getLocalData();

                                } else {
                                    generateAlertBox.closeAlertBox();
                                }
                            }
                        });
                        generateAlertBox.showAlertBox();

                    }
                }
            });

            editOptionsArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String optionId = cartList.get(pos).getiOptionId();
                    if (optionId != null && !optionId.equalsIgnoreCase("")) {
                        String[] selOptionarray = optionId.split(",");
                        if (selOptionarray.length == 1) {
                            if (generalFunc.getJsonValueStr("ENABLE_MULTI_OPTIONS_ADDONS", obj_userProfile).equalsIgnoreCase("YES")) {
                                OpenMultiOptionDailog(pos, false);
                            } else {
                                OpenOptionDailog(pos, false);
                            }
                        } else if (selOptionarray.length > 1) {
                            OpenMultiOptionDailog(pos, false);
                        }
                    }
                }
            });

            addImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int QUANTITY = Integer.parseInt(QTYNumberTxtView.getText().toString());
                    finalTotal = finalTotal - Math.abs(QUANTITY *
                            GeneralFunctions.parseDoubleValue(0, itemData.getfDiscountPrice()));

                    if (QUANTITY >= 1) {
                        QUANTITY = QUANTITY + 1;


                        if (itemData.getIsOption().equalsIgnoreCase("Yes") || itemData.getIsTooping().equalsIgnoreCase("Yes")) {
                            openRepeatDialog(pos, QUANTITY);
                        } else {
                            updateQty(pos, QUANTITY + "");
                            getLocalData();
                        }
                    }

                }
            });

            itemContainer.addView(editCartView);
        }

        if (cartList.size() > 0) {
            mangeCartItemCnt(cartList.size() + "");
        }
        totalVTxt.setText(currencySymbol + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(finalTotal) + ""));
    }

    private int currentStep = 0;

    public void OpenMultiOptionDailog(int pos, boolean isNew) {
        selectoptionId = "";
        realmOptionResult = getOptionsData(cartList.get(pos).getiMenuItemId(), cartList.get(pos).getiFoodMenuId());
        realmToppingResult = getToppingData(cartList.get(pos).getiMenuItemId(), cartList.get(pos).getiFoodMenuId());

        String[] selToppingarray = cartList.get(pos).getiToppingId().split(",");
        selectoptionId = cartList.get(pos).getiOptionId();
        String[] selOptionarray = selectoptionId.split(",");

        ArrayList<String> mOptionIdList = new ArrayList<>(Arrays.asList(selOptionarray));
        final ArrayList<String>[] mToppingListId = new ArrayList[]{new ArrayList<>(Arrays.asList(selToppingarray))};

        final BottomSheetDialog optionDailog = new BottomSheetDialog(getActContext());

        View contentView = View.inflate(getActContext(), R.layout.dialog_cart_edit_options, null);
        if (generalFunc.isRTLmode()) {
            contentView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        optionDailog.setContentView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                Utils.dpToPx(500, getActContext())));
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        mBehavior.setPeekHeight(Utils.dpToPx(600, getActContext()));
        optionDailog.setCancelable(false);
        View bottomSheetView = optionDailog.getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);

        bottomSheetView.findViewById(R.id.singleOptionAddonCardView).setVisibility(View.GONE);
        CardView multiItemCardView = (CardView) bottomSheetView.findViewById(R.id.multiOptionAddonCardView);
        multiItemCardView.setVisibility(View.GONE);

        ViewPager mViewPager = (ViewPager) bottomSheetView.findViewById(R.id.multiItemViewPager);

        ImageView arrowPrevious = (ImageView) bottomSheetView.findViewById(R.id.arrowPrevious);
        ImageView arrowNext = (ImageView) bottomSheetView.findViewById(R.id.arrowNext);
        if (generalFunc != null && generalFunc.isRTLmode()) {
            arrowPrevious.setRotation(0);
            arrowNext.setRotation(180);
        }

        MultiItemOptionAddonPagerAdapter mMultiItemOptionAddonPagerAdapter = new MultiItemOptionAddonPagerAdapter(bottomSheetView.getContext(), generalFunc, new MultiItemOptionAddonPagerAdapter.MultiItemOptionAddonListener() {
            @Override
            public void radioButtonPressed(String iOptionsCategoryId, ArrayList<String> mOptionIdList, List<Double> mTotalAmountList, RealmList<Options> mRealmOptionsList) {
                selectoptionId = "";
                if (mOptionIdList.size() > 0) {
                    for (int i = 0; i < mOptionIdList.size(); i++) {
                        if (selectoptionId.equals("")) {
                            selectoptionId = mOptionIdList.get(i);
                        } else {
                            selectoptionId = selectoptionId + "," + mOptionIdList.get(i);
                        }
                    }
                }
            }

            @Override
            public void checkBoxPressed(String iOptionsCategoryId, ArrayList<String> toppingListId, List<Double> mToppingPriceAmountList, RealmList<Topping> mRealmToppingList) {
                mToppingListId[0] = toppingListId;
            }
        });
        mViewPager.setAdapter(mMultiItemOptionAddonPagerAdapter);

        LinearLayout previousArea = (LinearLayout) bottomSheetView.findViewById(R.id.previousArea);
        previousArea.setVisibility(View.INVISIBLE);
        LinearLayout nextArea = (LinearLayout) bottomSheetView.findViewById(R.id.nextArea);
        step_view = bottomSheetView.findViewById(R.id.step_view);
        stepScrollview=bottomSheetView.findViewById(R.id.stepScrollview);
        nextArea.setVisibility(View.INVISIBLE);

        TextView txtPrevious = (TextView) bottomSheetView.findViewById(R.id.txtPrevious);
        txtPrevious.setText(generalFunc.retrieveLangLBl("Previous", "LBL_PREVIOUS"));
        TextView txtNext = (TextView) bottomSheetView.findViewById(R.id.txtNext);
        txtNext.setText(generalFunc.retrieveLangLBl("Next", "LBL_NEXT"));

        previousArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
            }
        });
        nextArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
            }
        });

        step_view.setOnStepClickListener(new StepView.OnStepClickListener() {
            @Override
            public void onStepClick(int step) {

                if (generalFunc.isRTLmode()) {

                    currentStep = step_view.getStepCount() - step - 1;

                    //   Toast.makeText(SimpleActivity.this, "Step " + step, Toast.LENGTH_SHORT).show();


                    step_view.go(currentStep, true);

                    mViewPager.setCurrentItem(currentStep, true);
                    if(step_view.getStepCount()>5) {
                        x = (step_view.getStepCount() - currentStep) * Utils.dpToPx(70, getActContext());
                        stepScrollview.scrollTo(x, y);
                    }

                    return;

                }
                currentStep = step;
                //   Toast.makeText(SimpleActivity.this, "Step " + step, Toast.LENGTH_SHORT).show();
                step_view.go(step, true);
                mViewPager.setCurrentItem(step, true);

                if(step_view.getStepCount()>5) {
                    // x = x + Utils.dpToPx(50,getActContext());
                    x = step*Utils.dpToPx(90,getActContext());

                    stepScrollview.scrollTo(x, y);
                }
                Logger.d("TestSetpcount", "::" + step_view.getStepCount() + "::" + step);

                if (step_view.getStepCount() == step + 1) {
                    // addItemCartBtn.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_ITEM"));


                } else {
                    // addItemCartBtn.setText(generalFunc.retrieveLangLBl("", "LBL_NEXT"));
                }

            }
        });
        mViewPager.setOnTouchListener((v, event) -> true);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    previousArea.setVisibility(View.INVISIBLE);
                } else {
                    previousArea.setVisibility(View.VISIBLE);
                }
                if (position < mViewPager.getAdapter().getCount() - 1) {
                    nextArea.setVisibility(View.VISIBLE);
                } else {
                    nextArea.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        MTextView vItemNameTxt = bottomSheetView.findViewById(R.id.vItemNameTxt);
        vItemNameTxt.setText(cartList.get(pos).getvItemType());
        MTextView vCancelTxt = bottomSheetView.findViewById(R.id.vCancelTxt);
        vCancelTxt.setText(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"));
        vCancelTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionDailog.dismiss();
                getLocalData();
            }
        });

        MButton btn_type2 = ((MaterialRippleLayout) bottomSheetView.findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setText(generalFunc.retrieveLangLBl("Update Cart", "LBL_UPDATE_CART"));
        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);
        btn_type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String toppingId = "";
                Realm realm = MyApp.getRealmInstance();
                realm.beginTransaction();
                Cart cart = cartList.get(pos);
                if (mToppingListId[0].size() > 0) {
                    for (int i = 0; i < mToppingListId[0].size(); i++) {
                        if (toppingId.equals("")) {
                            toppingId = mToppingListId[0].get(i);
                        } else {
                            toppingId = toppingId + "," + mToppingListId[0].get(i);
                        }
                    }
                }
                if (isNew) {
                    Cart newCart = checksameRecordExist(pos, toppingId, selectoptionId, realmCartList.get(pos).getiFoodMenuId(), realmCartList.get(pos).getiMenuItemId());
                    if (newCart == null) {
                        newCart = new Cart();
                        newCart.setCurrencySymbol(cart.getCurrencySymbol());
                        newCart.setiToppingId(toppingId);
                        newCart.setiOptionId(selectoptionId);
                        newCart.setIsOption(cart.getIsOption());
                        newCart.setIsTooping(cart.getIsTooping());
                        newCart.setQty("1");
                        newCart.setiCompanyId(cart.getiCompanyId());
                        newCart.setvCompany(cart.getvCompany());
                        newCart.setiFoodMenuId(cart.getiFoodMenuId());
                        newCart.setiMenuItemId(cart.getiMenuItemId());
                        newCart.seteFoodType(cart.geteFoodType());
                        newCart.setiServiceId(cart.getiServiceId());
                        newCart.setvImage(cart.getvImage());
                        newCart.setfDiscountPrice(cart.getfDiscountPrice());
                        newCart.setvItemType(cart.getvItemType());
                        newCart.setMilliseconds(Calendar.getInstance().getTimeInMillis());

                        newCart.setIspriceshow(cart.getIspriceshow());
                    } else {
                        int qty = GeneralFunctions.parseIntegerValue(0, newCart.getQty()) + 1;
                        // int qty = GeneralFunctions.parseIntegerValue(0, newCart.getQty());
                        newCart.setQty(qty + "");
                    }
                    realm.insertOrUpdate(newCart);
                    realm.commitTransaction();

                } else {
                    Cart newCart = checksameRecordExist(pos, toppingId, selectoptionId, realmCartList.get(pos).getiFoodMenuId(), realmCartList.get(pos).getiMenuItemId());
                    if (newCart == null) {

                        cart.setiToppingId(toppingId);
                        cart.setiOptionId(selectoptionId);
                        realm.commitTransaction();
                    } else {
                        // newCart.setQty(GeneralFunctions.parseIntegerValue(0, newCart.getQty()) + "");
                        if (newCart.getMilliseconds() == cart.getMilliseconds()) {
                            //  newCart.setQty(GeneralFunctions.parseIntegerValue(0, newCart.getQty()) + 1 + "");
                            newCart.setQty(GeneralFunctions.parseIntegerValue(0, newCart.getQty()) + "");

                        } else {
                            newCart.setQty(GeneralFunctions.parseIntegerValue(0, newCart.getQty()) + GeneralFunctions.parseIntegerValue(0, cart.getQty()) + "");
                        }

                        realm.insertOrUpdate(newCart);

                        if (GeneralFunctions.parseIntegerValue(0, newCart.getQty()) != GeneralFunctions.parseIntegerValue(0, cart.getQty())) {
                            cart.deleteFromRealm();
                        }
                        realm.commitTransaction();


                    }
                }
                getLocalData();
                optionDailog.dismiss();
            }
        });

        mMultiItemOptionAddonPagerAdapter.setSelectedData(mOptionIdList, mToppingListId[0]);

        JSONArray multiData = generalFunc.getJsonArray(cartList.get(pos).getMultiItemJsonData());
        if (multiData != null) {
            if (multiData.length() > 0) {
                multiItemCardView.setVisibility(View.VISIBLE);
            }
            if (multiData.length() > 1) {
                nextArea.setVisibility(View.VISIBLE);
            }
            mMultiItemOptionAddonPagerAdapter.setCategoryArrayList(multiData, true);
            step_view.setStepsNumber(multiData.length());
           // btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_NEXT"));
            if(multiData.length()>5)
            {


                ViewGroup.LayoutParams layoutParams = step_view.getLayoutParams();
                layoutParams.width = Utils.dpToPx(multiData.length()*100,getActContext());
                step_view.setLayoutParams(layoutParams);
            }

            if (multiData.length() == 1) {
                step_view.setVisibility(View.GONE);
            }
        }
        optionDailog.show();
    }

    public void updateQty(int pos, String qty) {
        Cart cart = realmCartList.get(pos);
        Realm realm = MyApp.getRealmInstance();
        realm.beginTransaction();
        cart.setQty(qty);
        realm.insertOrUpdate(cart);
        realm.commitTransaction();
    }

    public void setLabel() {

        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_EDIT_CART"));
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CHECKOUT"));
        totalHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TOTAL_TXT"));
        totalNoteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_EXCLUDING_TAXES_TXT"));
        nocartTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_EMPTY_CART_H_LBL"));
        nocartMsgTxt.setText(generalFunc.retrieveLangLBl("", "LBL_EMPTY_CART_SUB_H_LBL"));

    }

    public void mangeCartItemCnt(String cnt) {

    }

    RadioButton lastCheckedRB = null;
    String selectoptionId = "";

    public void OpenOptionDailog(int pos, boolean isNew) {
        selectoptionId = "";


        realmOptionResult = getOptionsData(cartList.get(pos).getiMenuItemId(), cartList.get(pos).getiFoodMenuId());
        realmToppingResult = getToppingData(cartList.get(pos).getiMenuItemId(), cartList.get(pos).getiFoodMenuId());

        String[] selToppingarray = cartList.get(pos).getiToppingId().split(",");
        String selectOption = cartList.get(pos).getiOptionId();
        ArrayList<String> selToppingList = new ArrayList<>(Arrays.asList(selToppingarray));


        if (cartList.get(pos).getiOptionId() != null & !cartList.get(pos).getiOptionId().equalsIgnoreCase("")) {
            selectoptionId = cartList.get(pos).getiOptionId();
        }


        final BottomSheetDialog optionDailog = new BottomSheetDialog(getActContext());


        View contentView = View.inflate(getActContext(), R.layout.dialog_cart_edit_options, null);
        if (generalFunc.isRTLmode()) {
            contentView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        optionDailog.setContentView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                Utils.dpToPx(400, getActContext())));
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        mBehavior.setPeekHeight(Utils.dpToPx(400, getActContext()));
        optionDailog.setCancelable(false);
        View bottomSheetView = optionDailog.getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);

        bottomSheetView.findViewById(R.id.singleOptionAddonCardView).setVisibility(View.VISIBLE);
        bottomSheetView.findViewById(R.id.multiOptionAddonCardView).setVisibility(View.GONE);
        LinearLayout optionArea = bottomSheetView.findViewById(R.id.optionArea);
        LinearLayout topingArea = bottomSheetView.findViewById(R.id.topingArea);
        MTextView optionTitleTxt = bottomSheetView.findViewById(R.id.optionTitleTxt);
        MTextView topingTitleTxt = bottomSheetView.findViewById(R.id.topingTitleTxt);
        LinearLayout optionContainer = bottomSheetView.findViewById(R.id.optionContainer);
        LinearLayout topingContainer = bottomSheetView.findViewById(R.id.topingContainer);
        MTextView vItemNameTxt = bottomSheetView.findViewById(R.id.vItemNameTxt);
        MTextView vCancelTxt = bottomSheetView.findViewById(R.id.vCancelTxt);

        MButton btn_type2 = ((MaterialRippleLayout) bottomSheetView.findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setText(generalFunc.retrieveLangLBl("Update Cart", "LBL_UPDATE_CART"));
        vCancelTxt.setText(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"));
        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);
        btn_type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toppingId = "";
                Realm realm = MyApp.getRealmInstance();
                realm.beginTransaction();
                Cart cart = cartList.get(pos);
                if (selToppingList.size() > 0) {
                    for (int i = 0; i < selToppingList.size(); i++) {
                        if (toppingId.equals("")) {
                            toppingId = selToppingList.get(i).toString();
                        } else {
                            toppingId = toppingId + "," + selToppingList.get(i).toString();
                        }
                    }
                }

                if (isNew) {
                    Cart newCart = checksameRecordExist(pos, toppingId, selectoptionId, realmCartList.get(pos).getiFoodMenuId(), realmCartList.get(pos).getiMenuItemId());
                    if (newCart == null) {
                        newCart = new Cart();
                        newCart.setCurrencySymbol(cart.getCurrencySymbol());
                        newCart.setiToppingId(toppingId);
                        newCart.setiOptionId(selectoptionId);
                        newCart.setIsOption(cart.getIsOption());
                        newCart.setIsTooping(cart.getIsTooping());
                        newCart.setQty("1");
                        newCart.setiCompanyId(cart.getiCompanyId());
                        newCart.setvCompany(cart.getvCompany());
                        newCart.setiFoodMenuId(cart.getiFoodMenuId());
                        newCart.setiMenuItemId(cart.getiMenuItemId());
                        newCart.seteFoodType(cart.geteFoodType());
                        newCart.setiServiceId(cart.getiServiceId());
                        newCart.setvImage(cart.getvImage());
                        newCart.setfDiscountPrice(cart.getfDiscountPrice());
                        newCart.setvItemType(cart.getvItemType());
                        newCart.setMilliseconds(Calendar.getInstance().getTimeInMillis());

                        newCart.setIspriceshow(cart.getIspriceshow());
                    } else {
                        int qty = GeneralFunctions.parseIntegerValue(0, newCart.getQty()) + 1;
                        // int qty = GeneralFunctions.parseIntegerValue(0, newCart.getQty());
                        newCart.setQty(qty + "");
                    }
                    realm.insertOrUpdate(newCart);
                    realm.commitTransaction();

                } else {
                    Cart newCart = checksameRecordExist(pos, toppingId, selectoptionId, realmCartList.get(pos).getiFoodMenuId(), realmCartList.get(pos).getiMenuItemId());
                    if (newCart == null) {

                        cart.setiToppingId(toppingId);
                        cart.setiOptionId(selectoptionId);
                        realm.commitTransaction();
                    } else {
                        // newCart.setQty(GeneralFunctions.parseIntegerValue(0, newCart.getQty()) + "");
                        if (newCart.getMilliseconds() == cart.getMilliseconds()) {
                            //  newCart.setQty(GeneralFunctions.parseIntegerValue(0, newCart.getQty()) + 1 + "");
                            newCart.setQty(GeneralFunctions.parseIntegerValue(0, newCart.getQty()) + "");

                        } else {
                            newCart.setQty(GeneralFunctions.parseIntegerValue(0, newCart.getQty()) + GeneralFunctions.parseIntegerValue(0, cart.getQty()) + "");
                        }

                        realm.insertOrUpdate(newCart);

                        if (GeneralFunctions.parseIntegerValue(0, newCart.getQty()) != GeneralFunctions.parseIntegerValue(0, cart.getQty())) {
                            cart.deleteFromRealm();
                        }
                        realm.commitTransaction();


                    }
                }
                getLocalData();
                optionDailog.dismiss();


            }
        });
        vCancelTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionDailog.dismiss();
                getLocalData();

            }
        });

        topingTitleTxt.setText(generalFunc.retrieveLangLBl("Select Topping", "LBL_SELECT_TOPPING"));
        optionTitleTxt.setText(generalFunc.retrieveLangLBl("Select Options", "LBL_SELECT_OPTIONS"));
        vItemNameTxt.setText(cartList.get(pos).getvItemType());


        if (realmOptionResult != null && realmOptionResult.size() > 0) {
            optionArea.setVisibility(View.VISIBLE);

            for (int i = 0; i < realmOptionResult.size(); i++) {
                int optionpos = i;

                LayoutInflater optioninflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View optionview = optioninflater.inflate(R.layout.item_option, null);
                MTextView optionName = optionview.findViewById(R.id.optionName);
                MTextView optionPrice = optionview.findViewById(R.id.optionPrice);
                RadioGroup optionRadioGroup = optionview.findViewById(R.id.optionRadioGroup);
                RadioButton optionradioBtn = optionview.findViewById(R.id.optionradioBtn);
                LinearLayout rowArea = optionview.findViewById(R.id.rowArea);
                //optionradioBtn.setTag(realmOptionResult.get(i).getiOptionId());


                if (selectOption.contains(realmOptionResult.get(i).getiOptionId())) {
                    //optionRadioGroup.check(optionradioBtn.getId());
                    optionradioBtn.setChecked(true);
                    lastCheckedRB = optionradioBtn;
                }


                rowArea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optionradioBtn.setChecked(true);

                    }
                });


                optionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if (lastCheckedRB != null) {
                            if (lastCheckedRB == optionradioBtn) {
                                return;
                            }
                        }
                        if (lastCheckedRB != null) {
                            lastCheckedRB.setChecked(false);
                        }

                        selectoptionId = realmOptionResult.get(optionpos).getiOptionId();
                        optionradioBtn.setChecked(true);
                        lastCheckedRB = optionradioBtn;
                    }

                });


                optionName.setText(realmOptionResult.get(i).getvOptionName());
                optionPrice.setText(generalFunc.convertNumberWithRTL(realmOptionResult.get(i).getfUserPriceWithSymbol()));
                optionContainer.addView(optionview);
            }
        }
        if (realmToppingResult != null && realmToppingResult.size() > 0) {
            topingArea.setVisibility(View.VISIBLE);

            for (int i = 0; i < realmToppingResult.size(); i++) {
                LayoutInflater topinginflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View topingView = topinginflater.inflate(R.layout.item_toping, null);
                MTextView topingTxtView = topingView.findViewById(R.id.topingTxtView);
                MTextView topingPriceTxtView = topingView.findViewById(R.id.topingPriceTxtView);
                CheckBox topingCheckBox = topingView.findViewById(R.id.topingCheckBox);
                topingCheckBox.setTag(realmToppingResult.get(i).getiOptionId());
                LinearLayout row_area = topingView.findViewById(R.id.row_area);

                if (selToppingList != null && selToppingList.size() > 0) {
                    if (selToppingList.contains(realmToppingResult.get(i).getiOptionId())) {
                        topingCheckBox.setChecked(true);
                    }
                }

                row_area.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (topingCheckBox.isChecked()) {
                            topingCheckBox.setChecked(false);
                        } else {
                            topingCheckBox.setChecked(true);
                        }
                    }
                });

                topingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            selToppingList.add(topingCheckBox.getTag() + "");
                        } else {
                            if (selToppingList.size() > 0) {
                                if (selToppingList.contains(topingCheckBox.getTag() + "")) {
                                    selToppingList.remove(topingCheckBox.getTag() + "");
                                }
                            }
                        }
                    }
                });

                topingTxtView.setText(realmToppingResult.get(i).getvOptionName());
                topingPriceTxtView.setText(generalFunc.convertNumberWithRTL(realmToppingResult.get(i).getfUserPriceWithSymbol()));
                topingContainer.addView(topingView);
            }
        }


        optionDailog.show();


    }

    public void deleteOptionToRealm() {
        Realm realm = MyApp.getRealmInstance();
        realm.beginTransaction();
        realm.delete(Options.class);
        realm.commitTransaction();

        Realm realmTopping = MyApp.getRealmInstance();
        realmTopping.beginTransaction();
        realmTopping.delete(Topping.class);
        realmTopping.commitTransaction();
    }


    public void openRepeatDialog(int pos, int qty) {
        Cart cartObj = cartList.get(pos);

        final BottomSheetDialog repeatCartDailog = new BottomSheetDialog(getActContext());
        View contentView = View.inflate(getActContext(), R.layout.dialog_cart_repeat, null);

        repeatCartDailog.setContentView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                Utils.dpToPx(250, getActContext())));
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        mBehavior.setPeekHeight(Utils.dpToPx(250, getActContext()));
        repeatCartDailog.setCancelable(false);
        View bottomSheetView = repeatCartDailog.getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);

        ImageView vegImageView = (ImageView) bottomSheetView.findViewById(R.id.vegImageView);
        ImageView nonvegImageView = (ImageView) bottomSheetView.findViewById(R.id.nonvegImageView);
        MTextView vItemNameTxt = (MTextView) bottomSheetView.findViewById(R.id.vItemNameTxt);
        MTextView price = (MTextView) bottomSheetView.findViewById(R.id.price);
        MTextView offerPrice = (MTextView) bottomSheetView.findViewById(R.id.offerPrice);
        MTextView repeatTitleTxt = (MTextView) bottomSheetView.findViewById(R.id.repeatTitleTxt);
        MTextView repeatItemTxt = (MTextView) bottomSheetView.findViewById(R.id.repeatItemTxt);
        MTextView newItemBtn = (MTextView) bottomSheetView.findViewById(R.id.newItemBtn);
        MTextView repeatItemBtn = (MTextView) bottomSheetView.findViewById(R.id.repeatItemBtn);
        MTextView vCancelTxt = bottomSheetView.findViewById(R.id.vCancelTxt);
        vCancelTxt.setText(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"));

        vCancelTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatCartDailog.dismiss();


            }
        });
        String itemList = "";
        String optionId = cartList.get(pos).getiOptionId();
        if (optionId != null && !optionId.equalsIgnoreCase("")) {
            String[] selOptionarray = optionId.split(",");
            for (String s : selOptionarray) {
                Options options = getOptionObject(s);
                if (options != null) {
                    if (itemList.equalsIgnoreCase("")) {
                        itemList = options.getvOptionName();
                    } else {
                        itemList = itemList + "," + options.getvOptionName();
                    }
                }
            }
        }

        if (cartList.get(pos).getiToppingId() != null && !cartList.get(pos).getiToppingId().equalsIgnoreCase("")) {
            String[] selToppingarray = cartList.get(pos).getiToppingId().split(",");

            for (int i = 0; i < selToppingarray.length; i++) {
                Topping topping = getToppingObject(selToppingarray[i]);

                if (topping != null) {
                    if (itemList.equalsIgnoreCase("")) {
                        itemList = topping.getvOptionName();
                    } else {

                        itemList = itemList + "," + topping.getvOptionName();
                    }
                }
            }

        }

        repeatItemTxt.setText(itemList);

        if (cartObj.geteFoodType().equalsIgnoreCase("NonVeg")) {
            nonvegImageView.setVisibility(View.VISIBLE);
        } else if (cartObj.geteFoodType().equalsIgnoreCase("Veg")) {
            vegImageView.setVisibility(View.VISIBLE);
        }
        vItemNameTxt.setText(cartObj.getvItemType());
        price.setText(cartObj.getCurrencySymbol() + cartObj.getfDiscountPrice());
        repeatTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PREVIOUS_CUST_TITLE"));
        newItemBtn.setText(generalFunc.retrieveLangLBl("", "LBL_CHOOSE"));
        repeatItemBtn.setText(generalFunc.retrieveLangLBl("", "LBL_REPEAT"));


        newItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatCartDailog.dismiss();
                String optionId = cartList.get(pos).getiOptionId();
                if (optionId != null && !optionId.equalsIgnoreCase("")) {
                    String[] selOptionarray = optionId.split(",");
                    if (selOptionarray.length == 1) {
                        if (generalFunc.getJsonValueStr("ENABLE_MULTI_OPTIONS_ADDONS", obj_userProfile).equalsIgnoreCase("YES")) {
                            OpenMultiOptionDailog(pos, false);
                        } else {
                            OpenOptionDailog(pos, false);
                        }
                    } else if (selOptionarray.length > 1) {
                        OpenMultiOptionDailog(pos, false);
                    }
                }
            }
        });
        repeatItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQty(pos, qty + "");
                getLocalData();
                repeatCartDailog.dismiss();

            }
        });


        repeatCartDailog.show();

    }


    @Override
    public void onBackPressed() {

        if (getIntent().getBooleanExtra("isRestart", false)) {

            if (!generalFunc.getMemberId().equalsIgnoreCase("")) {
                MyApp.getInstance().restartWithGetDataApp();
                generalFunc.storeData(Utils.iServiceId_KEY, "");
            } else {
                generalFunc.restartApp();
            }

        } else {
            super.onBackPressed();
        }
    }


    public void checkPrescription() {
        ArrayList<String> menulist = new ArrayList<>();
        if (cartList != null && cartList.size() > 0) {

            for (int i = 0; i < cartList.size(); i++) {
                menulist.add(cartList.get(i).getiMenuItemId());
            }

        }
        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "CheckPrescriptionRequired");
        parameters.put("iMenuItemId", android.text.TextUtils.join(",", menulist));
        parameters.put("eSystem", Utils.eSystem_Type);
        boolean isValues = false;

        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString) == true) {
                    Bundle bn = new Bundle();
                    bn.putBoolean("isFromEditCard", true);
                    String iServiceId = realmCartList.get(0).getiServiceId();
                    bn.putString("iServiceId", iServiceId);
                    generalFunc.storeData(Utils.iServiceId_KEY, iServiceId);
                    new StartActProcess(getActContext()).startActWithData(PrescriptionActivity.class, bn);
                } else {
                    Bundle bn = new Bundle();
                    bn.putBoolean("isFromEditCard", true);
                    new StartActProcess(getActContext()).startActWithData(CheckOutActivity.class, bn);

                }
            } else {
                Bundle bn = new Bundle();
                bn.putBoolean("isFromEditCard", true);
                new StartActProcess(getActContext()).startActWithData(CheckOutActivity.class, bn);
            }
        });
        exeWebServer.execute();

    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActContext());
            int i = view.getId();
            if (i == R.id.backImgView) {
                onBackPressed();
            } else if (i == btn_type2.getId()) {

                if (generalFunc.retrieveValue(Utils.COMPANY_MINIMUM_ORDER) != null && !generalFunc.retrieveValue(Utils.COMPANY_MINIMUM_ORDER).equalsIgnoreCase("0")) {
                    double minimumAmt = GeneralFunctions.parseDoubleValue(0, generalFunc.retrieveValue(Utils.COMPANY_MINIMUM_ORDER));

                    if (finalTotal < minimumAmt) {

                        generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_MINIMUM_ORDER_NOTE") + " " + currencySymbol + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(GeneralFunctions.parseDoubleValue(0, generalFunc.retrieveValue(Utils.COMPANY_MINIMUM_ORDER)))));

                        return;

                    }
                }

                if (!generalFunc.getMemberId().equalsIgnoreCase("")) {
                    checkPrescription();
                } else {
                    Bundle bn = new Bundle();
                    bn.putBoolean("isFromEditCard", true);
                    new StartActProcess(getActContext()).startActWithData(CheckOutActivity.class, bn);
                    //   new StartActProcess(getActContext()).startAct(CheckOutActivity.class);
                }
            }
        }
    }
}
