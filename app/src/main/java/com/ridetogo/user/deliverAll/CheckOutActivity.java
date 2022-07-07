package com.ridetogo.user.deliverAll;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.AudioRecord.AudioListener;
import com.AudioRecord.AudioRecordButton;
import com.AudioRecord.AudioRecording;
import com.AudioRecord.RecordingItem;
import com.ViewPagerCards.RoundCornerDrawable;
import com.adapter.files.MoreInstructionAdapter;
import com.ridetogo.user.AccountverificationActivity;
import com.ridetogo.user.AddAddressActivity;
import com.ridetogo.user.BuildConfig;
import com.ridetogo.user.CardPaymentActivity;
import com.ridetogo.user.ContactUsActivity;
import com.ridetogo.user.CouponActivity;
import com.ridetogo.user.ListAddressActivity;
import com.ridetogo.user.MyWalletActivity;
import com.ridetogo.user.PrescriptionActivity;
import com.ridetogo.user.ProfilePaymentActivity;
import com.ridetogo.user.QuickPaymentActivity;
import com.ridetogo.user.R;
import com.ridetogo.user.VerifyInfoActivity;
import com.general.files.AppFunctions;
import com.general.files.DecimalDigitsInputFilter;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.ImageFilePath;
import com.general.files.InternetConnection;
import com.general.files.MyApp;
import com.general.files.SetGeneralData;
import com.general.files.StartActProcess;
import com.general.files.UploadProfileImage;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.realmModel.Cart;
import com.realmModel.Options;
import com.realmModel.Topping;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.Logger;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.ErrorView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.MyProgressDialog;
import com.view.SelectableRoundedImageView;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class CheckOutActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN_UP = 007;
    private static final int ADD_ADDRESS = 006;
    private static final int SEL_ADDRESS = 005;
    private static final int SEL_CARD = 004;
    private static final int MANAGE_APP_ALL_FILES_ACCESS_REQUEST_CODE = 2296;
    ImageView backImgView;
    MTextView titleTxt;
    GeneralFunctions generalFunc;
    MButton btn_type2, btn_clearcart;
    int submitBtnId;
    double finalTotal = 0;
    RealmResults<Cart> realmCartList;
    ArrayList<Cart> cartList;
    LinearLayout itemContainer;
    MTextView restaurantNameTxtView;
    String currencySymbol = "";
    MTextView subtotalHTxt, subtotalVTxt;
    MTextView applyCouponHTxt;
    MTextView appliedPromoHTxtView;
    ImageView couponCodeImgView, couponCodeCloseImgView;
    View couponCodeArea;
    boolean isLogin = false;
    MTextView noLoginNoteTxt;

    MTextView changeAddresssTxt;
    LinearLayout instantPaymentLL, useExistingCardLL;
    MTextView addressTxt;
    View delveryAddressArea;
    String userProfileJson;
    MTextView paymentModeTitle;

    String selAddress = "";
    String selLatitude = "";
    String selLongitude = "";
    String selAddressId = "";

    MaterialEditText deliveryInstructionBox;
    MTextView deliverinstruction;
    String LBL_MINIMUM_ORDER_NOTE = "";
    String CurrencySymbol = "";
    RadioGroup paymentRadioGroup;
    RadioButton cardRadio;
    RadioButton cashRadio;
    MTextView cardNumTxt;
    boolean isDialogOpen = false;
    LinearLayout promocodeArea;
    MTextView promocodeappliedHTxt;
    MTextView promocodeappliedVTxt;
    String ePaymentOption = "Cash";
    ErrorView errorView;
    InternetConnection internetConnection;
    ScrollView contentArea;
    NestedScrollView nestedScroll;
    LinearLayout topArea, bottomArea;
    String iOrderId = "";
    LinearLayout farecontainer;
    String restaurantstatus = "";
    String ToTalAddress = "0";
    boolean isselectaddress = false;
    boolean prevselectaddress = false;
    int totalqtycnt = 0;
    LinearLayout maxItemarea, btn_type2area;
    MTextView maxitemTitleTxtView, maxitemmsgTxtView;
    MTextView casenote;
    ArrayList<HashMap<String, String>> itemArraylist = new ArrayList<HashMap<String, String>>();
    AppCompatCheckBox checkboxWallet;
    String isWalletSelect = "No";
    MTextView walletAmountTxt;
    boolean isFromEditCard = false;
    boolean iswalletZero = false;
    String selectedMethod = ""; // "Instant","Manual"
    ImageView iv_edit_card, instant_arrow_right, manual_arrow_right;
    LinearLayout manual_arrow_area, editcard_area;
    MTextView cardTitleTxt, cardDescTxt, existingCardTxt, instantPaymentTxt;
    private String appliedPromoCode = "";
    private boolean isCardValidated;
    public String eWalletIgnore = "No";
    String SITE_TYPE = "";

    private String SYSTEM_PAYMENT_FLOW = "";
    private String APP_PAYMENT_MODE = "";
    private String APP_PAYMENT_METHOD = "";
    boolean isCODAllow = true;
    ImageView rightImgView;
    LinearLayout locationArea, EditAddressArea, AddAddressArea;
    MTextView addAddressHTxt, addAddressBtn, selLocTxt, resAddressTxtView;
    ImageView editCartImageview, storeImg;
    String outStandingAmount = "";
    private String DisplayCardPayment = "";

    LinearLayout deliveryTypeArea;
    MTextView tvDeliveryTypeTxt;
    RadioGroup takeAwayRadioGroups;
    RadioButton takeAwayRadio, deliverToDoorRadio;
    String eTakeAway = "No";

    /*Tip Feature View Declarion Start*/
    LinearLayout tipAmountArea1, tipAmountArea2, tipAmountArea3, tipAmountAreaOther;
    ImageView closeImg1, closeImg2, closeImg3, closeImgOther;
    MTextView tipAmountText1, tipAmountText2, tipAmountText3, tipAmountOtherText;
    MTextView tipTitleText;
    ImageView iv_tip_help;
    MTextView tipDescTitleText;
    MTextView tipDescHintTitleText;
    EditText tipAmountBox;
    MTextView errorTxt;
    LinearLayout addTipArea;
    LinearLayout amountArea;
    //    MButton tipGivenBtn;
    SelectableRoundedImageView tipGivenBtn;
    int giveTipId;
    String tipAmount = "";
    LinearLayout tipCardArea;
    private boolean isPercentage;
    /*Tip Feature View Declarion Start*/


    RecyclerView moreinstuction;

    MoreInstructionAdapter moreInstructionAdapter;
    LinearLayout moreinstructionLyout;
    ArrayList<HashMap<String, String>> instructionslit;
    MTextView textmoreinstruction;
    MTextView textVoiceinstruction, textVoiceTitle, textVoicesubTitle, timeTxt;
    MTextView textrecordTitle, textrecordsubTitle;
    LinearLayout playArea, miscArea, miscNoteArea;
    ImageView playBtn, dltBtn, miscImg;
    ImageButton recordBtn;
    AudioRecordButton audio_record_button;
    RecordingItem recordingItem1 = null;
    AudioRecording audioRecording;
    public SeekBar seekbar;
    boolean wasPlaying = false;
    private boolean intialStage = true;
    boolean isPause = false;
    ImageView voiceHelp;
    boolean isPreference = false;
    private List<HashMap<String, String>> currentSelectedItems = new ArrayList<>();
    ImageView PreferenceHelp;
    LinearLayout headerArea;
    LinearLayout delveryVoiceInstArea;
    RelativeLayout detailsArea;
    ProgressBar loading;

    //manage proof
    String eShowTerms;
    String eProofUpload;
    String tProofNote;
    Dialog uploadServicePicAlertBox = null;
    private String selectedImagePath = "";
    private String pathForCameraImage = "";
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Temp";
    private static final int SELECT_PICTURE = 2;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    String isFrom = "";
    String iIdProofImageId = "";

    ImageView help_deliverto_door, help_takeaway;

    String iVoiceDirectionFileId = "";
    RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        initViews();
        getLocalData();
        internetConnection = new InternetConnection(getActContext());
        if (internetConnection.isNetworkConnected()) {
            CheckOutOrderEstimateDetails("", false);
        } else {
            generateErrorView();
        }
    }


    public MyProgressDialog showLoader() {
        MyProgressDialog myPDialog = new MyProgressDialog(getActContext(), false, generalFunc.retrieveLangLBl("Loading", "LBL_LOADING_TXT"));
        myPDialog.show();

        return myPDialog;
    }


    public void getLocalData() {
        try {
            finalTotal = 0;
            realmCartList = getCartData();
            if (realmCartList.size() > 0) {
                cartList = new ArrayList<>(realmCartList);
                setData();
            }
        } catch (Exception e) {
        }


    }


    public RealmResults<Cart> getCartData() {
        Realm realm = MyApp.getRealmInstance();
        return realm.where(Cart.class).findAll();
    }


    public void setData() {

        restaurantNameTxtView.setText(cartList.get(0).getvCompany());

        //addItemView();
    }

    public void addItemView() {
        totalqtycnt = 0;

        if (itemContainer.getChildCount() > 0) {
            itemContainer.removeAllViewsInLayout();
        }
        for (int i = 0; i < itemArraylist.size(); i++) {

            int pos = i;

            LayoutInflater topinginflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View editCartView = topinginflater.inflate(R.layout.item_checkout_row, null);
            MTextView itemNameTxtView = (MTextView) editCartView.findViewById(R.id.itemNameTxtView);
            itemNameTxtView.setSelected(true);
            MTextView itemMenuNameTxtView = (MTextView) editCartView.findViewById(R.id.itemMenuNameTxtView);
            itemMenuNameTxtView.setSelected(true);
            MTextView itemPriceTxtView = (MTextView) editCartView.findViewById(R.id.itemPriceTxtView);
            MTextView itemstrikePriceTxtView = (MTextView) editCartView.findViewById(R.id.itemstrikePriceTxtView);
            MTextView QTYNumberTxtView = (MTextView) editCartView.findViewById(R.id.QTYNumberTxtView);
            ImageView cancelImg = (ImageView) editCartView.findViewById(R.id.cancelImg);
            LinearLayout layoutShape = (LinearLayout) editCartView.findViewById(R.id.layoutShape);
            SelectableRoundedImageView menuImage = (SelectableRoundedImageView) editCartView.findViewById(R.id.menuImage);
            if (generalFunc.isRTLmode()) {
                layoutShape.setBackgroundResource(R.drawable.ic_shape_rtl);
            }

            cancelImg.setTag(i);

            cancelImg.setOnClickListener(view -> {
                try {

                    final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setCancelable(false);
                    generateAlert.setBtnClickList(btn_id -> {
                        generateAlert.closeAlertBox();
                        if (btn_id == 1) {
                            Realm realm = MyApp.getRealmInstance();
                            realm.beginTransaction();
                            Cart cart = realmCartList.get((Integer) cancelImg.getTag());
                            cart.deleteFromRealm();
                            realm.commitTransaction();

                            realmCartList = getCartData();


                            if (realmCartList.size() == 0) {
                                onBackPressed();
                            }
                            CheckOutOrderEstimateDetails(appliedPromoCode, false);

                        }
                    });
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("Are you sure want to delete", "LBL_DELETE_CONFIRM_MSG"));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
                    generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("cANCEL", "LBL_CANCEL_TXT"));
                    generateAlert.showAlertBox();


                } catch (Exception e) {
                    Logger.e("TestCrash", "::" + e.toString());
                }


            });

            HashMap<String, String> itemData = itemArraylist.get(i);
            String fOfferAmt = itemData.get("fOfferAmt");
            double fOfferAmtVal = generalFunc.parseDoubleValue(0, fOfferAmt != null ? fOfferAmt : "");

            if (fOfferAmtVal > 0) {
                itemPriceTxtView.setText(/*currencySymbol +*/ generalFunc.convertNumberWithRTL(itemData.get("fPrice")));

                itemstrikePriceTxtView.setVisibility(View.VISIBLE);
                itemstrikePriceTxtView.setPaintFlags(itemstrikePriceTxtView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                itemstrikePriceTxtView.setText(/*currencySymbol +*/ generalFunc.convertNumberWithRTL(itemData.get("fOriginalPrice")));
            } else {
                itemstrikePriceTxtView.setVisibility(View.GONE);
                itemPriceTxtView.setText(/*currencySymbol +*/ generalFunc.convertNumberWithRTL(itemData.get("fPrice")));
            }


            String imagePath = itemData.get("vImage");
            if (imagePath.equalsIgnoreCase("")) {
                imagePath = "Demo";
            }

            int height = (int) getResources().getDimension(R.dimen._100sdp);
            int width = (int) getResources().getDimension(R.dimen._100sdp);

            Picasso.get()
                    .load(imagePath)
                    .placeholder(R.mipmap.ic_no_icon)
                    .resize(width, height)
                    .error(R.mipmap.ic_no_icon)
                    .into(menuImage);

            itemNameTxtView.setText(itemData.get("vItemType"));

            String optionaddonname = itemData.get("optionaddonname");

            if (optionaddonname != null && !optionaddonname.equalsIgnoreCase("")) {
                itemMenuNameTxtView.setText("(" + optionaddonname + ")");

                itemMenuNameTxtView.setVisibility(View.VISIBLE);
            } else {
                itemMenuNameTxtView.setVisibility(View.GONE);
            }
            QTYNumberTxtView.setText("x" + generalFunc.convertNumberWithRTL(itemData.get("iQty")));
            totalqtycnt = totalqtycnt + GeneralFunctions.parseIntegerValue(0, cartList.get(i).getQty());


            itemContainer.addView(editCartView);
        }

        //subtotalVTxt.setText(currencySymbol + finalTotal + "");

        manageMaxQtyArea();
        setButtonTxt();
    }

    public void manageMaxQtyArea() {

        int maxqty = GeneralFunctions.parseIntegerValue(0, generalFunc.retrieveValue(Utils.COMPANY_MAX_QTY));
        if (maxqty != 0) {
            if (totalqtycnt > maxqty) {
                maxItemarea.setVisibility(View.VISIBLE);
                btn_type2area.setVisibility(View.GONE);
            } else {
                maxItemarea.setVisibility(View.GONE);
                btn_type2area.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setButtonTxt() {
        if (generalFunc.getMemberId() != null && !generalFunc.getMemberId().equalsIgnoreCase("")) {
            //  btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_PAY") + " " + generalFunc.convertNumberWithRTL(currencySymbol + finalTotal + ""));
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_PAY"));

            if (eProofUpload.equalsIgnoreCase("Yes")) {
                btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_NEXT_TXT"));
            }
        }
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

    public String getToppingPrice(String id) {
        String optionPrice = "";

        Realm realm = MyApp.getRealmInstance();
        Topping options = realm.where(Topping.class).equalTo("iOptionId", id).findFirst();

        if (options != null) {
            return options.getfUserPrice();
        }


        return optionPrice;

    }


    public Context getActContext() {
        return CheckOutActivity.this;
    }

    public void initViews() {
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());

        /*Tip Feature Views casting*/
        tipCardArea = (LinearLayout) findViewById(R.id.tipCardArea);
        tipAmountOtherText = (MTextView) findViewById(R.id.tipAmountTextOther);
        tipAmountAreaOther = (LinearLayout) findViewById(R.id.tipAmountAreaOther);
        closeImgOther = (ImageView) findViewById(R.id.closeImgOther);

        tipAmountText1 = (MTextView) findViewById(R.id.tipAmountText1);
        tipAmountArea1 = (LinearLayout) findViewById(R.id.tipAmountArea1);
        closeImg1 = (ImageView) findViewById(R.id.closeImg1);

        tipAmountText2 = (MTextView) findViewById(R.id.tipAmountText2);
        tipAmountArea2 = (LinearLayout) findViewById(R.id.tipAmountArea2);
        closeImg2 = (ImageView) findViewById(R.id.closeImg2);

        tipAmountText3 = (MTextView) findViewById(R.id.tipAmountText3);
        tipAmountArea3 = (LinearLayout) findViewById(R.id.tipAmountArea3);
        closeImg3 = (ImageView) findViewById(R.id.closeImg3);

        tipAmountArea1.setOnClickListener(new setOnClickList());
        closeImg1.setOnClickListener(new setOnClickList());
        tipAmountArea2.setOnClickListener(new setOnClickList());
        closeImg2.setOnClickListener(new setOnClickList());
        tipAmountArea3.setOnClickListener(new setOnClickList());
        closeImg3.setOnClickListener(new setOnClickList());
        tipAmountAreaOther.setOnClickListener(new setOnClickList());
        closeImgOther.setOnClickListener(new setOnClickList());


        tipTitleText = (MTextView) findViewById(R.id.tipTitleText);
        iv_tip_help = (ImageView) findViewById(R.id.iv_tip_help);
        iv_tip_help.setOnClickListener(new setOnClickList());

        tipDescTitleText = (MTextView) findViewById(R.id.tipDescTitleText);
        tipDescHintTitleText = (MTextView) findViewById(R.id.tipDescHintTitleText);

        tipAmountBox = (EditText) findViewById(R.id.tipAmountBox);
        errorTxt = (MTextView) findViewById(R.id.errorTxt);
        addTipArea = (LinearLayout) findViewById(R.id.addTipArea);
        amountArea = (LinearLayout) findViewById(R.id.amountArea);

//        tipGivenBtn = ((MaterialRippleLayout) findViewById(R.id.tipGivenBtn)).getChildView();
        tipGivenBtn = (SelectableRoundedImageView) findViewById(R.id.tipGivenBtn);
//        giveTipId = Utils.generateViewId();
//        tipGivenBtn.setId(giveTipId);
        tipGivenBtn.setOnClickListener(new setOnClickList());

        new CreateRoundedView(getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), getResources().getDimension(R.dimen._30sdp)), 5,
                getResources().getColor(R.color.gray_holo_dark), tipGivenBtn);
//        tipGivenBtn.setColorFilter(getResources().getColor(R.color.appThemeColor_TXT_1));

        tipAmountBox.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        tipAmountBox.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});

        tipTitleText.setText(generalFunc.retrieveLangLBl("", "LBL_ORDER_TIP_TITLE_TXT"));
        tipDescTitleText.setText(generalFunc.retrieveLangLBl("", "LBL_ORDER_TIP_DESCRIPTION_TXT"));
        tipDescHintTitleText.setText(generalFunc.retrieveLangLBl("", "LBL_ORDER_TIP_HOW_WORKS_TXT"));
        tipAmountBox.setHint(generalFunc.retrieveLangLBl("", "LBL_TIP_AMOUNT_ENTER_TITLE"));
//        tipGivenBtn.setText(generalFunc.retrieveLangLBl("","LBL_BTN_SUBMIT_TXT"));


        tipDescHintTitleText.setOnClickListener(new setOnClickList());
        tipAmountOtherText.setText(generalFunc.retrieveLangLBl("", "LBL_OTHER_TXT"));

        /*Tip Feature Views casting*/

        rootLayout = findViewById(R.id.rootLayout);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootLayout.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    bottomArea.setVisibility(View.GONE);
                    locationArea.setVisibility(View.GONE);
                } else {
                    bottomArea.setVisibility(View.VISIBLE);
                    locationArea.setVisibility(View.VISIBLE);
                }
            }
        });

        getUserProfileJson();

        /*Tip Feature- Tip Area Show Setting*/
        String ENABLE_TIP_MODULE_DELIVERALL_ = generalFunc.getJsonValue("ENABLE_TIP_MODULE_DELIVERALL", userProfileJson);
        if (!generalFunc.getMemberId().equals("") && ENABLE_TIP_MODULE_DELIVERALL_.equalsIgnoreCase("Yes") && eTakeAway.equalsIgnoreCase("No")) {
            tipCardArea.setVisibility(View.VISIBLE);
        } else {
            tipCardArea.setVisibility(View.GONE);
        }

        SITE_TYPE = generalFunc.getJsonValue("SITE_TYPE", userProfileJson);
        headerArea = (LinearLayout) findViewById(R.id.headerArea);
        delveryVoiceInstArea = (LinearLayout) findViewById(R.id.delveryVoiceInstArea);
        textVoiceinstruction = findViewById(R.id.textVoiceinstruction);
        textVoiceTitle = findViewById(R.id.textVoiceTitle);
        textVoicesubTitle = findViewById(R.id.textVoicesubTitle);
        textrecordTitle = findViewById(R.id.textrecordTitle);
        textrecordsubTitle = findViewById(R.id.textrecordsubTitle);
        timeTxt = findViewById(R.id.timeTxt);
        playArea = findViewById(R.id.playArea);
        playBtn = findViewById(R.id.playBtn);
        dltBtn = findViewById(R.id.dltBtn);
        miscImg = findViewById(R.id.miscImg);
        recordBtn = findViewById(R.id.recordBtn);
        audioRecording = new AudioRecording(getActContext());
        audio_record_button = findViewById(R.id.audio_record_button);


        seekbar = findViewById(R.id.seekbar);
        miscArea = findViewById(R.id.miscArea);
        miscNoteArea = findViewById(R.id.miscNoteArea);
        textVoiceinstruction.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_INS"));
        textVoiceTitle.setText(generalFunc.retrieveLangLBl("", "LBL_VOICE_DIRECTIONS"));
        textVoicesubTitle.setText(generalFunc.retrieveLangLBl("", "LBL_VOICE_DETAILS"));
        textrecordTitle.setText(generalFunc.retrieveLangLBl("", "LBL_TAP_HOLD_RECORD"));
        textrecordsubTitle.setText(generalFunc.retrieveLangLBl("", "LBL_KEEP_RECORDING_TIME"));
        if (generalFunc.getJsonValue("ENABLE_DELIVERY_INSTRUCTIONS_ORDERS", userProfileJson) != null &&
                generalFunc.getJsonValue("ENABLE_DELIVERY_INSTRUCTIONS_ORDERS", userProfileJson).equalsIgnoreCase(
                        "Yes")) {
            delveryVoiceInstArea.setVisibility(View.VISIBLE);
        } else {
            delveryVoiceInstArea.setVisibility(View.GONE);

        }
        voiceHelp = findViewById(R.id.voiceHelp);
        voiceHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPreferenceHelp(getResources().getDrawable(R.drawable.ic_microphone_help), "LBL_DELIVERY_INS_NOTE", generalFunc.retrieveLangLBl("", "LBL_DELIVERY_INS"));
            }
        });

        audio_record_button.setOnAudioListener(new AudioListener() {
            @Override
            public void onStop(RecordingItem recordingItem) {
                recordingItem1 = recordingItem;

                try {
                    Logger.d("audio_record_button", "onStop");
                    long milliseconds = recordingItem.getLength();

                    long minutes = (milliseconds / 1000) / 60;
                    long seconds = (milliseconds / 1000) % 60;
                    Logger.d("Timer", "::" + minutes + "::" + seconds);
                    NumberFormat f = new DecimalFormat("00");
                    timeTxt.setText(f.format(minutes) + ":" + f.format(seconds));
                    miscArea.setVisibility(View.GONE);
                    playArea.setVisibility(View.VISIBLE);

                } catch (Exception e) {

                }
            }

            @Override
            public void onCancel() {
                Logger.d("audio_record_button", "onCancel");
            }

            @Override
            public void onError(Exception e) {
                Logger.d("audio_record_button", "onError");

            }
        });
        dltBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playBtn.setImageDrawable(ContextCompat.getDrawable(CheckOutActivity.this, R.drawable.ic_baseline_play_arrow_24));
                isPause = false;
                intialStage = true;
                recordingItem1 = null;
                if (audioRecording.mMediaPlayer != null) {
                    audioRecording.mMediaPlayer.stop();
                }
                miscArea.setVisibility(View.VISIBLE);
                playArea.setVisibility(View.GONE);
                audioRecording.mMediaPlayer = null;
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (audioRecording.mMediaPlayer != null && audioRecording.mMediaPlayer.isPlaying()) {
                        // clearMediaPlayer();
                        // seekbar.setProgress(0);
                        isPause = true;
                        pauseMediaPlayer();
                        Log.d("wasPlaying", "::00::" + wasPlaying);
                        playBtn.setImageDrawable(ContextCompat.getDrawable(CheckOutActivity.this, R.drawable.ic_baseline_play_arrow_24));
                        return;
                    }
                } catch (Exception e) {
                    Logger.d("Exception", "::" + e.toString());
                }

                if (isPause && audioRecording.mMediaPlayer != null) {
                    audioRecording.mMediaPlayer.start();
                    audioRecording.play();
                    playBtn.setImageDrawable(ContextCompat.getDrawable(CheckOutActivity.this, R.drawable.ic_baseline_pause_24));
                    isPause = false;

                } else if (!wasPlaying) {
                    Log.d("wasPlaying", "::11::" + wasPlaying);
                    if (intialStage) {
                        intialStage = false;
                        playBtn.setImageDrawable(ContextCompat.getDrawable(CheckOutActivity.this, R.drawable.ic_baseline_pause_24));

                        seekbar.setProgress(0);
                        audioRecording.play(recordingItem1);
                    } else {
                        playBtn.setImageDrawable(ContextCompat.getDrawable(CheckOutActivity.this, R.drawable.ic_baseline_pause_24));

                        try {
                            audioRecording.mMediaPlayer.prepare();
                        } catch (IOException e) {
                            Logger.d("intialStage", "::" + e.toString());
                        } catch (IllegalStateException e) {
                            Logger.d("intialStage", "::" + e.toString());
                        } catch (Exception e) {
                            Logger.d("intialStage", "::" + e.toString());
                        }

                        try {
                            audioRecording.mMediaPlayer.start();
                        } catch (IllegalStateException e) {
                            Logger.d("intialStage", "::" + e.toString());
                        } catch (Exception e) {
                            Logger.d("intialStage", "::" + e.toString());
                        }
                        audioRecording.pauseplay();

                    }

                }
                wasPlaying = false;


                seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {


                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

                        if (audioRecording.mMediaPlayer == null) {
                            seekBar.setProgress(0);
                            return;
                        }

                        int x = (int) Math.ceil(progress / 1000f);

                        Log.d("Progress", "::" + progress);

                        if (x < 10) {
                            timeTxt.setText("00:0" + x);
                        } else if (x >= 60) {

                            long minutes = x / 60;
                            long seconds = x % 60;
                            NumberFormat f = new DecimalFormat("00");
                            timeTxt.setText(f.format(minutes) + ":" + f.format(seconds));
                        } else {
                            timeTxt.setText("00:" + x);
                        }


                        if (progress > 0 && audioRecording.mMediaPlayer != null && !audioRecording.mMediaPlayer.isPlaying()) {
                            //  clearMediaPlayer();
                            if (!isPause) {
                                playBtn.setImageDrawable(ContextCompat.getDrawable(CheckOutActivity.this, R.drawable.ic_baseline_play_arrow_24));
                                seekBar.setProgress(0);
                            }

                        }
                        if (audioRecording.mMediaPlayer != null && progress == audioRecording.mMediaPlayer.getDuration()) {
                            playBtn.setImageDrawable(ContextCompat.getDrawable(CheckOutActivity.this, R.drawable.ic_baseline_play_arrow_24));
                            seekBar.setProgress(0);
                            isPause = false;
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {


                        if (audioRecording.mMediaPlayer != null && audioRecording.mMediaPlayer.isPlaying()) {
                            audioRecording.mMediaPlayer.seekTo(seekBar.getProgress());
                        }
                    }
                });
            }
        });


        detailsArea = (RelativeLayout) findViewById(R.id.detailsArea);
        loading = (ProgressBar) findViewById(R.id.loading);
        headerArea.setVisibility(View.GONE);
        detailsArea.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        LBL_MINIMUM_ORDER_NOTE = generalFunc.retrieveLangLBl("", "LBL_MINIMUM_ORDER_NOTE");
        editCartImageview = (ImageView) findViewById(R.id.editCartImageview);
        editCartImageview.setVisibility(View.VISIBLE);
        editCartImageview.setOnClickListener(new setOnClickList());

        rightImgView = (ImageView) findViewById(R.id.rightImgView);
        if (getIntent().getBooleanExtra("isPrescription", false)) {
            rightImgView.setImageDrawable(getActContext().getResources().getDrawable(R.drawable.ic_medical_prescription));
            rightImgView.setVisibility(View.VISIBLE);
            rightImgView.setOnClickListener(new setOnClickList());

        }


//        SYSTEM_PAYMENT_FLOW="Method-1";
//        APP_PAYMENT_MODE="Card";


        instructionslit = new ArrayList<>();
        moreinstructionLyout = findViewById(R.id.moreinstructionLyout);
        moreinstuction = findViewById(R.id.moreinstuction);
        moreInstructionAdapter = new MoreInstructionAdapter(getActContext(), instructionslit, new MoreInstructionAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(HashMap<String, String> map) {
                currentSelectedItems.add(map);

                getPreferenceIds();
            }

            @Override
            public void onItemUncheck(HashMap<String, String> map) {
                currentSelectedItems.remove(map);

                getPreferenceIds();
            }
        });
        moreinstuction.setAdapter(moreInstructionAdapter);

        PreferenceHelp = findViewById(R.id.PreferenceHelp);
        PreferenceHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreferenceHelp(null, "LBL_DELIVERY_PREFERENCE_NOTE", "");
            }
        });



        /*Take away*/
        deliveryTypeArea = (LinearLayout) findViewById(R.id.deliveryTypeArea);
        deliveryTypeArea.setOnClickListener(new setOnClickList());
        //deliveryTypeArea.setVisibility(generalFunc.retrieveValue("ENABLE_TAKE_AWAY").equalsIgnoreCase("Yes") ? View.VISIBLE : View.GONE);

        tvDeliveryTypeTxt = (MTextView) findViewById(R.id.tvDeliveryTypeTxt);
        takeAwayRadioGroups = (RadioGroup) findViewById(R.id.takeAwayRadioGroups);
        takeAwayRadio = (RadioButton) findViewById(R.id.takeAwayRadio);
        deliverToDoorRadio = (RadioButton) findViewById(R.id.deliverToDoorRadio);
        tvDeliveryTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_TYPE"));
        takeAwayRadio.setText(generalFunc.retrieveLangLBl("", "LBL_TAKE_AWAY"));
        deliverToDoorRadio.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVER_TO_YOUR_DOORS"));
        help_deliverto_door = (ImageView) findViewById(R.id.help_deliverto_door);
        help_takeaway = (ImageView) findViewById(R.id.help_takeaway);
        help_deliverto_door.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreferenceHelp(getResources().getDrawable(R.drawable.ic_delivertodoor), "LBL_NOTE_DELIVER_TO_DOOR", "");
            }
        });
        help_takeaway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreferenceHelp(getResources().getDrawable(R.drawable.ic_takeaway), "LBL_NOTE_TAKE_AWAY", "");

            }
        });


        eTakeAway = deliverToDoorRadio.getHint().toString();
        deliverToDoorRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeAwayRadio.setChecked(false);
                eTakeAway = deliverToDoorRadio.getHint().toString();
                CheckOutOrderEstimateDetails(appliedPromoCode, false);

                if (generalFunc.getJsonValue("ENABLE_DELIVERY_INSTRUCTIONS_ORDERS", userProfileJson) != null &&
                        generalFunc.getJsonValue("ENABLE_DELIVERY_INSTRUCTIONS_ORDERS", userProfileJson).equalsIgnoreCase(
                                "Yes")) {
                    delveryVoiceInstArea.setVisibility(View.VISIBLE);
                } else {
                    delveryVoiceInstArea.setVisibility(View.GONE);

                }
            }
        });

        takeAwayRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliverToDoorRadio.setChecked(false);
                eTakeAway = takeAwayRadio.getHint().toString();
                //Remove selected Tip
                removeSelectedTip(true);

                if (generalFunc.getJsonValue("ENABLE_DELIVERY_INSTRUCTIONS_ORDERS", userProfileJson) != null &&
                        generalFunc.getJsonValue("ENABLE_DELIVERY_INSTRUCTIONS_ORDERS", userProfileJson).equalsIgnoreCase(
                                "Yes")) {
                    delveryVoiceInstArea.setVisibility(View.GONE);
                } else {
                    delveryVoiceInstArea.setVisibility(View.GONE);

                }
            }
        });



        /*Take away*/

        textmoreinstruction = findViewById(R.id.textmoreinstruction);
        storeImg = findViewById(R.id.storeImg);

        locationArea = (LinearLayout) findViewById(R.id.locationArea);
        EditAddressArea = (LinearLayout) findViewById(R.id.EditAddressArea);
        AddAddressArea = (LinearLayout) findViewById(R.id.AddAddressArea);
        addAddressHTxt = (MTextView) findViewById(R.id.addAddressHTxt);
        addAddressBtn = (MTextView) findViewById(R.id.addAddressBtn);
        addAddressBtn.setOnClickListener(new setOnClickList());
        selLocTxt = (MTextView) findViewById(R.id.selLocTxt);
        resAddressTxtView = (MTextView) findViewById(R.id.resAddressTxtView);
        errorView = (ErrorView) findViewById(R.id.errorView);
        deliveryInstructionBox = (MaterialEditText) findViewById(R.id.deliveryInstructionBox);
        deliverinstruction = (MTextView) findViewById(R.id.deliverinstruction);
        deliverinstruction.setText(generalFunc.retrieveLangLBl("Instruction for Restaurant", "LBL_TITLE_STORE_INS"));
        checkboxWallet = (AppCompatCheckBox) findViewById(R.id.checkboxWallet);
        walletAmountTxt = (MTextView) findViewById(R.id.walletAmountTxt);
        contentArea = (ScrollView) findViewById(R.id.contentArea);
        nestedScroll = (NestedScrollView) findViewById(R.id.nestedScroll);


        topArea = (LinearLayout) findViewById(R.id.topArea);
        bottomArea = (LinearLayout) findViewById(R.id.bottomArea);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        promocodeArea = (LinearLayout) findViewById(R.id.promocodeArea);
        promocodeappliedHTxt = (MTextView) findViewById(R.id.promocodeappliedHTxt);
        promocodeappliedVTxt = (MTextView) findViewById(R.id.promocodeappliedVTxt);
        appliedPromoHTxtView = (MTextView) findViewById(R.id.appliedPromoHTxtView);
        appliedPromoHTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_APPLIED_COUPON_CODE"));


        backImgView = (ImageView) findViewById(R.id.backImgView);

        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        btn_clearcart = ((MaterialRippleLayout) findViewById(R.id.btn_clearcart)).getChildView();
        itemContainer = (LinearLayout) findViewById(R.id.itemContainer);
        restaurantNameTxtView = (MTextView) findViewById(R.id.restaurantNameTxtView);
        restaurantNameTxtView.setSelected(true);
        subtotalVTxt = (MTextView) findViewById(R.id.subtotalVTxt);
        subtotalHTxt = (MTextView) findViewById(R.id.subtotalHTxt);
        applyCouponHTxt = (MTextView) findViewById(R.id.applyCouponHTxt);
        couponCodeArea = findViewById(R.id.couponCodeArea);
        noLoginNoteTxt = (MTextView) findViewById(R.id.noLoginNoteTxt);

        changeAddresssTxt = (MTextView) findViewById(R.id.changeAddresssTxt);
        paymentModeTitle = (MTextView) findViewById(R.id.paymentModeTitle);
        deliveryInstructionBox.setHideUnderline(true);
        if (generalFunc.isRTLmode()) {
            deliveryInstructionBox.setPaddings(0, 0, (int) getResources().getDimension(R.dimen._10sdp), 0);
        } else {
            deliveryInstructionBox.setPaddings((int) getResources().getDimension(R.dimen._10sdp), 0, 0, 0);
        }

        deliveryInstructionBox.setSingleLine(false);
        deliveryInstructionBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        deliveryInstructionBox.setGravity(Gravity.TOP);
        paymentRadioGroup = (RadioGroup) findViewById(R.id.paymentRadioGroup);
        cardRadio = (RadioButton) findViewById(R.id.cardRadio);
        cashRadio = (RadioButton) findViewById(R.id.cashRadio);
        cardNumTxt = (MTextView) findViewById(R.id.cardNumTxt);
        farecontainer = (LinearLayout) findViewById(R.id.farecontainer);
        maxItemarea = (LinearLayout) findViewById(R.id.maxItemarea);
        btn_type2area = (LinearLayout) findViewById(R.id.btn_type2area);
        maxitemTitleTxtView = (MTextView) findViewById(R.id.maxitemTitleTxtView);
        maxitemmsgTxtView = (MTextView) findViewById(R.id.maxitemmsgTxtView);
        casenote = (MTextView) findViewById(R.id.casenote);
        cashRadio.setChecked(true);

        instantPaymentLL = (LinearLayout) findViewById(R.id.instantPaymentLL);
        useExistingCardLL = (LinearLayout) findViewById(R.id.useExistingCardLL);
        iv_edit_card = (ImageView) findViewById(R.id.iv_edit_card);
        manual_arrow_right = (ImageView) findViewById(R.id.manual_arrow_right);
        instant_arrow_right = (ImageView) findViewById(R.id.instant_arrow_right);
        couponCodeImgView = (ImageView) findViewById(R.id.couponCodeImgView);
        couponCodeCloseImgView = (ImageView) findViewById(R.id.couponCodeCloseImgView);

        manual_arrow_area = (LinearLayout) findViewById(R.id.manual_arrow_area);
        editcard_area = (LinearLayout) findViewById(R.id.editcard_area);

        instantPaymentTxt = (MTextView) findViewById(R.id.instantPaymentTxt);
        existingCardTxt = (MTextView) findViewById(R.id.existingCardTxt);
        cardDescTxt = (MTextView) findViewById(R.id.cardDescTxt);
        cardTitleTxt = (MTextView) findViewById(R.id.cardTitleTxt);

        cardTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVER_CARD_TITLE_TXT"));
        cardDescTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVER_CARD_DESC_TXT"));
        existingCardTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVER_MANAGE_CARD"));
        instantPaymentTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVER_INSTANT_PAYMENT"));

        if (generalFunc.isRTLmode()) {
            couponCodeImgView.setRotation(180);
        }
/*
        takeAwayRadioGroups.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                currentSelectedItems.clear();


                if (checkedId == takeAwayRadio.getId()) {
                    eTakeAway = takeAwayRadio.getHint().toString();

                } else if (checkedId == deliverToDoorRadio.getId()) {
                    eTakeAway = deliverToDoorRadio.getHint().toString();
                }
                //comment By CS Because getting issue for getting preference List
                //if (isPreference) {
                CheckOutOrderEstimateDetails(appliedPromoCode, false);
                // }

            }
        });*/
        paymentRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == cardRadio.getId()) {

                ePaymentOption = "Card";

                if (SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
                    if (!generalFunc.isDeliverOnlyEnabled()) {
                        findViewById(R.id.cardDetailsArea).setVisibility(View.VISIBLE);

                        if (!Utils.checkText(selectedMethod)) {
//                        useExistingCardLL.performClick();

                            selectedMethod = "Manual";

                            if (checkCardConfig(isWalletSelect, false, false)) {
                                existingPaymentView(true);
                            } else {
                                defaultPaymentView(true);
                            }

                        }

                        contentArea.post(() -> contentArea.fullScroll(ScrollView.FOCUS_DOWN));
                    } else if (generalFunc.isDeliverOnlyEnabled()) {
//                        selectedMethod = "Instant";
                        selectedMethod = "Manual";
                    }
                }

            } else if (checkedId == cashRadio.getId()) {
                ePaymentOption = "Cash";
                selectedMethod = "";
                findViewById(R.id.cardDetailsArea).setVisibility(View.GONE);
            }
        });
        checkboxWallet.setOnClickListener(view -> {

            if (iswalletZero) {
                final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(btn_id -> {
                    if (btn_id == 0) {
                        checkboxWallet.setChecked(false);
                        generateAlert.closeAlertBox();
                    } else {


                        checkboxWallet.setChecked(false);
                        if (APP_PAYMENT_MODE.equalsIgnoreCase("Cash")) {
                            new StartActProcess(getActContext()).startAct(ContactUsActivity.class);
                        } else {
                            Bundle bn = new Bundle();
                            bn.putString("iServiceId", generalFunc.getServiceId());
                            bn.putString("isCheckout", "");
                            new StartActProcess(getActContext()).startActWithData(MyWalletActivity.class, bn);
                        }

                    }

                });
                generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_LOW_WALLET_BAL"));

                if (APP_PAYMENT_MODE.equalsIgnoreCase("Cash")) {
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_CONTACT_US_TXT"));
                } else {
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_ADD_NOW"));
                }
                generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                generateAlert.showAlertBox();


            } else {
                if (iswalletZero) {
                    return;
                }
                if (checkboxWallet.isChecked()) {
                    isWalletSelect = "Yes";
                } else {
                    isWalletSelect = "No";

                }
                CheckOutOrderEstimateDetails(appliedPromoCode, false);
            }

        });

        changeAddresssTxt.setOnClickListener(new setOnClickList());

        instantPaymentLL.setOnClickListener(new setOnClickList());
        useExistingCardLL.setOnClickListener(new setOnClickList());

        addressTxt = (MTextView) findViewById(R.id.addressTxt);
        delveryAddressArea = findViewById(R.id.delveryAddressArea);
        couponCodeArea.setOnClickListener(new setOnClickList());
        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);
        btn_type2.setOnClickListener(new setOnClickList());
        btn_clearcart.setOnClickListener(new setOnClickList());
        backImgView.setOnClickListener(new setOnClickList());
        editcard_area.setOnClickListener(new setOnClickList());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isFromEditCard = bundle.getBoolean("isFromEditCard");
        }

        setLabel();

        if (!generalFunc.retrieveValue(Utils.SELECT_ADDRESSS).equalsIgnoreCase("")) {
            selAddress = generalFunc.retrieveValue(Utils.SELECT_ADDRESSS);
            selLatitude = generalFunc.retrieveValue(Utils.SELECT_LATITUDE);
            selLongitude = generalFunc.retrieveValue(Utils.SELECT_LONGITUDE);
            selAddressId = generalFunc.retrieveValue(Utils.SELECT_ADDRESS_ID);
            addressTxt.setText(selAddress);
        }


        if (APP_PAYMENT_MODE.equalsIgnoreCase("Cash")) {
            cashRadio.setVisibility(View.VISIBLE);
            cardRadio.setVisibility(View.GONE);
            cashRadio.setChecked(true);
        } else if (APP_PAYMENT_MODE.equalsIgnoreCase("Card")) {
            cashRadio.setVisibility(View.GONE);
            cardRadio.setVisibility(View.VISIBLE);
            cardRadio.setChecked(true);
        } else {
            cardRadio.setVisibility(View.VISIBLE);
            cashRadio.setVisibility(View.VISIBLE);
        }
        handleWalletView();

        if (!SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
            cardRadio.setText(generalFunc.retrieveLangLBl("", "LBL_PAY_BY_WALLET_TXT"));
            checkboxWallet.setVisibility(View.GONE);
            walletAmountTxt.setVisibility(View.GONE);

        }


    }


    private void pauseMediaPlayer() {
        if (audioRecording != null && audioRecording.mMediaPlayer != null) {
            isPause = true;
            audioRecording.mMediaPlayer.pause();
            //   audioRecording.mMediaPlayer.release();


            playBtn.setImageDrawable(ContextCompat.getDrawable(CheckOutActivity.this, R.drawable.ic_baseline_play_arrow_24));

        }
    }

    private void clearMediaPlayer() {
        if (audioRecording != null && audioRecording.mMediaPlayer != null) {
            audioRecording.mMediaPlayer.stop();
            audioRecording.mMediaPlayer.release();
            audioRecording.mMediaPlayer = null;

            seekbar.setProgress(0);
            wasPlaying = true;
            Log.d("wasPlaying", "::00::" + wasPlaying);
            playBtn.setImageDrawable(ContextCompat.getDrawable(CheckOutActivity.this, R.drawable.ic_baseline_play_arrow_24));

        }
    }

    private String getPreferenceIds() {
        int size = currentSelectedItems.size();
        String ids = "";
        if (isPreference && size > 0) {
            for (int i = 0; i < size; i++) {
                HashMap<String, String> map = currentSelectedItems.get(i);
                if (i == size - 1) {
                    ids = ids + map.get("iPreferenceId");
                } else {
                    ids = map.get("iPreferenceId") + "," + ids;
                }
            }
        }

        Logger.d("SelectedIds", "getPreferenceIds" + ids);
        return ids;
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseMediaPlayer();
    }

    public void handleWalletView() {
        if (SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
            String WALLET_ENABLE = userProfileJson != null ? generalFunc.getJsonValue(Utils.WALLET_ENABLE, userProfileJson) : "";

            if (!WALLET_ENABLE.equals("") && WALLET_ENABLE.equalsIgnoreCase("Yes") && !generalFunc.getJsonValue("user_available_balance_amount", userProfileJson).equalsIgnoreCase("") &&
                    GeneralFunctions.parseDoubleValue(0, generalFunc.getJsonValue("user_available_balance_amount", userProfileJson)) > 0) {
                checkboxWallet.setVisibility(View.VISIBLE);
                walletAmountTxt.setVisibility(View.VISIBLE);
                iswalletZero = false;


            } else {
                iswalletZero = true;
                checkboxWallet.setVisibility(View.VISIBLE);
                walletAmountTxt.setVisibility(View.VISIBLE);
            }
        }
    }

    public void generateErrorView() {
//        contentArea.setVisibility(View.GONE);
//        topArea.setVisibility(View.GONE);
//        bottomArea.setVisibility(View.GONE);

        generalFunc.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT");

        manageView();
        errorView.setOnRetryListener(() -> CheckOutOrderEstimateDetails(appliedPromoCode, false));
    }

    public void setLabel() {


        selLocTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_LOCATION_TXT"));
//        addAddressHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_DELIVERY_LOCATION"));
        addAddressHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_LOCATION_TXT"));
        addAddressBtn.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_ADDRESS_TXT"));
        cardRadio.setText(generalFunc.retrieveLangLBl("Pay Online", "LBL_PAY_ONLINE_TXT"));

        if (SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {


            cardRadio.setText(generalFunc.retrieveLangLBl("", "LBL_CARD"));


        } else if (!SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
            cardRadio.setText(generalFunc.retrieveLangLBl("", "LBL_PAY_BY_WALLET_TXT"));


        }
        cashRadio.setText(generalFunc.retrieveLangLBl("Cash Payment", "LBL_CASH_PAYMENT_TXT"));

        paymentModeTitle.setText(generalFunc.retrieveLangLBl("", "LBL_SELECET_PAYMENT_MODE"));
//        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CART_SUMMARY"));
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CHECKOUT"));


        checkboxWallet.setText(generalFunc.retrieveLangLBl("Use Wallet Balance", "LBL_USE_WALLET_BAL"));

        subtotalHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SUB_TOTAL"));

        applyCouponHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_APPLY_COUPON"));
        changeAddresssTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE"));

        deliveryInstructionBox.setHint(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_INSTRUCTION"));
        if (generalFunc.getMemberId().equalsIgnoreCase("")) {
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_LOGIN_TO_CONTINUE"));
        } else {
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_PAY"));


        }
        noLoginNoteTxt.setText(generalFunc.retrieveLangLBl("You will be able to review delivery address and payment options after login", "LBL_NO_LOGIN_NOTE"));

        if (!generalFunc.getMemberId().equalsIgnoreCase("")) {
            noLoginNoteTxt.setVisibility(View.GONE);
            if (GeneralFunctions.parseIntegerValue(0, generalFunc.getJsonValue("ToTalAddress", userProfileJson)) > 0) {
                delveryAddressArea.setVisibility(View.VISIBLE);
                EditAddressArea.setVisibility(View.VISIBLE);
                AddAddressArea.setVisibility(View.GONE);
            } else {
                EditAddressArea.setVisibility(View.GONE);
                AddAddressArea.setVisibility(View.VISIBLE);

            }
        } else {
            noLoginNoteTxt.setVisibility(View.VISIBLE);
            delveryAddressArea.setVisibility(View.GONE);

        }

        maxitemTitleTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_TO_MANY_ITEMS"));
        maxitemmsgTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_MAX_QTY_NOTE") + " " + generalFunc.retrieveValue(Utils.COMPANY_MAX_QTY) + " " + generalFunc.retrieveLangLBl("", "LBL_TO_PROCEED"));
        btn_clearcart.setText(generalFunc.retrieveLangLBl("", "LBL_CLEAR_CART"));

    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            getUserProfileJson();

            handleWalletView();
            if (!generalFunc.getMemberId().equalsIgnoreCase("")) {
                noLoginNoteTxt.setVisibility(View.GONE);
                locationArea.setVisibility(View.VISIBLE);
                if (GeneralFunctions.parseIntegerValue(0, ToTalAddress) > 0) {
                    delveryAddressArea.setVisibility(View.VISIBLE);
                }

                if (GeneralFunctions.parseIntegerValue(0, generalFunc.getJsonValue("ToTalAddress", userProfileJson)) > 0) {
                    delveryAddressArea.setVisibility(View.VISIBLE);
                    EditAddressArea.setVisibility(!eTakeAway.equalsIgnoreCase("Yes") ? View.VISIBLE : View.GONE);
                    AddAddressArea.setVisibility(View.GONE);
                } else {
                    EditAddressArea.setVisibility(View.GONE);
                    AddAddressArea.setVisibility(!eTakeAway.equalsIgnoreCase("Yes") ? View.VISIBLE : View.GONE);

                }

            } else {
                locationArea.setVisibility(View.GONE);
                noLoginNoteTxt.setVisibility(View.VISIBLE);
                delveryAddressArea.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }
    }

    private void getUserProfileJson() {
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        SYSTEM_PAYMENT_FLOW = generalFunc.getJsonValue("SYSTEM_PAYMENT_FLOW", userProfileJson);
        APP_PAYMENT_MODE = generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson);
        APP_PAYMENT_METHOD = generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson);
        // Tip Feature - set Values
        String CurrencySymbol = generalFunc.getJsonValue("CurrencySymbol", userProfileJson);
        String DELIVERY_TIP_AMOUNT_TYPE_DELIVERALL = generalFunc.getJsonValue("DELIVERY_TIP_AMOUNT_TYPE_DELIVERALL", userProfileJson);
        String tipAMount1 = generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("TIP_AMOUNT_1", userProfileJson));
        String tipAMount2 = generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("TIP_AMOUNT_2", userProfileJson));
        String tipAMount3 = generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("TIP_AMOUNT_3", userProfileJson));
        isPercentage = DELIVERY_TIP_AMOUNT_TYPE_DELIVERALL.equalsIgnoreCase("Percentage");
        boolean eReverseSymbolEnable = generalFunc.retrieveValue("eReverseSymbolEnable").equalsIgnoreCase("Yes");
        tipAmountText1.setTag(isPercentage ? 1 : tipAMount1);
        tipAmountText1.setText(isPercentage ? tipAMount1 : eReverseSymbolEnable ? tipAMount1 + " " + CurrencySymbol : CurrencySymbol + tipAMount1);
        tipAmountText2.setTag(isPercentage ? 2 : tipAMount2);
        tipAmountText2.setText(isPercentage ? tipAMount2 : eReverseSymbolEnable ? tipAMount2 + " " + CurrencySymbol : CurrencySymbol + tipAMount2);
        tipAmountText3.setTag(isPercentage ? 3 : tipAMount3);
        tipAmountText3.setText(isPercentage ? tipAMount3 : eReverseSymbolEnable ? tipAMount3 + " " + CurrencySymbol : CurrencySymbol + tipAMount3);
        tipAmountOtherText.setTag(isPercentage ? 4 : "");
    }

    //    public void openAddressDailog(String manageNote) {
//
//        final BottomSheetDialog addAddressDailog = new BottomSheetDialog(getActContext());
//        View contentView = View.inflate(getActContext(), R.layout.dialog_add_address, null);
//
//        addAddressDailog.setContentView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
//                Utils.dpToPx(280, getActContext())));
//        addAddressDailog.setCancelable(false);
//        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
//        mBehavior.setPeekHeight(Utils.dpToPx(280, getActContext()));
//        View bottomSheetView = addAddressDailog.getWindow().getDecorView().findViewById(android.support.design.R.id.design_bottom_sheet);
//
//        MTextView selAddressTxt = (MTextView) bottomSheetView.findViewById(R.id.selAddressTxt);
//        MTextView cancelTxt = (MTextView) bottomSheetView.findViewById(R.id.cancelTxt);
//        MTextView noaddressNote = (MTextView) bottomSheetView.findViewById(R.id.noaddressNote);
//        MTextView addAddressBtn = (MTextView) bottomSheetView.findViewById(R.id.addAddressBtn);
//        addAddressBtn.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_ADDRESS"));
//
//        cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
//        if (manageNote.equalsIgnoreCase("")) {
//            noaddressNote.setText(generalFunc.retrieveLangLBl("", "LBL_NO_ADDRESS_AVAILABLE_NOTE"));
//        } else {
//            noaddressNote.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE_ADDRESS_AVAILABLE_NOTE"));
//        }
//
//        cancelTxt.setOnClickListener(v -> {
//            addAddressDailog.dismiss();
//            finish();
//        });
//
//        addAddressBtn.setOnClickListener(v -> {
//
//            addAddressDailog.dismiss();
//
//            Bundle bn = new Bundle();
//            bn.putString("iCompanyId", cartList.get(0).getiCompanyId());
//            new StartActProcess(getActContext()).startActForResult(AddAddressActivity.class, bn, ADD_ADDRESS);
//
//        });
//
//
//        addAddressDailog.show();
//
//
//    }
    public View getCurrView() {
        return generalFunc.getCurrentView(CheckOutActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO: 28-04-2021 / Viral | Android 11 (30) Store permission code remove | revert 29
        /*if (requestCode == MANAGE_APP_ALL_FILES_ACCESS_REQUEST_CODE) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (generalFunc.isCameraPermissionGranted()) {
                    if (Environment.isExternalStorageManager()) {
                        takeAndUploadPic(getActContext());
                    } else {
                        generalFunc.showMessage(getCurrView(), "Allow this app to use storage access.");
                    }
                } else {
                    generalFunc.showMessage(getCurrView(), "Allow this app to use camera.");
                }

            }
        } else */
        if (requestCode == RC_SIGN_IN_UP && resultCode == RESULT_OK) {
            noLoginNoteTxt.setVisibility(View.GONE);
            getUserProfileJson();
            setGeneralData();
            setButtonTxt();
//            if (GeneralFunctions.parseIntegerValue(0, ToTalAddress) > 0) {
//                Bundle bn = new Bundle();
//                bn.putString("iCompanyId", cartList.get(0).getiCompanyId());
//                new StartActProcess(getActContext()).startActForResult(ListAddressActivity.class, bn, SEL_ADDRESS);
//            } else {
//                openAddressDailog();
//            }
            if (internetConnection.isNetworkConnected()) {
                CheckOutOrderEstimateDetails("", false);
            } else {
                generateErrorView();
            }
        } else if (requestCode == SEL_ADDRESS && resultCode == RESULT_OK) {

            getUserProfileJson();
            if (data.getStringExtra("ToTalAddress") != null) {
                ToTalAddress = data.getStringExtra("ToTalAddress");
            }
            if (GeneralFunctions.parseIntegerValue(0, ToTalAddress) > 0) {
                if (data != null && data.getStringExtra("address") != null) {
                    if (!selAddressId.equalsIgnoreCase(data.getStringExtra("addressId"))) {
                        appliedPromoCode = "";
                    }
                    addressTxt.setText(data.getStringExtra("address"));
                    selAddress = data.getStringExtra("address");
                    selAddressId = data.getStringExtra("addressId");

                    isselectaddress = true;

                    HashMap<String, String> storeData = new HashMap<>();
                    storeData.put(Utils.SELECT_ADDRESS_ID, selAddressId);
                    storeData.put(Utils.SELECT_ADDRESSS, data.getStringExtra("address"));
                    storeData.put(Utils.SELECT_LATITUDE, data.getStringExtra("vLatitude"));
                    storeData.put(Utils.SELECT_LONGITUDE, data.getStringExtra("vLongitude"));
                    generalFunc.storeData(storeData);

                    CheckOutOrderEstimateDetails(appliedPromoCode, false);
                }
                delveryAddressArea.setVisibility(View.VISIBLE);
            } else {
                delveryAddressArea.setVisibility(View.GONE);
                //openAddressDailog("");
                appliedPromoCode = "";
                CheckOutOrderEstimateDetails(appliedPromoCode, false);

            }

        } else if (requestCode == SEL_ADDRESS) {

            if (data != null && data.getBooleanExtra("isDeleted", false)) {

                ArrayList<String> removeData = new ArrayList<>();
                removeData.add(Utils.SELECT_ADDRESS_ID);
                removeData.add(Utils.SELECT_ADDRESSS);
                removeData.add(Utils.SELECT_LATITUDE);
                removeData.add(Utils.SELECT_LONGITUDE);
                generalFunc.removeValue(removeData);
                appliedPromoCode = "";
                CheckOutOrderEstimateDetails(appliedPromoCode, false);
            }

        } else if (requestCode == ADD_ADDRESS) {

            if (resultCode == RESULT_OK) {
                getUserProfileJson();
                if (internetConnection.isNetworkConnected()) {
                    generalFunc.removeValue(Utils.SELECT_ADDRESS_ID);
                    appliedPromoCode = "";
                    CheckOutOrderEstimateDetails(appliedPromoCode, false);
                } else {
                    generateErrorView();
                }
            } else {
                onBackPressed();
            }
        } else if (requestCode == Utils.VERIFY_MOBILE_REQ_CODE) {
            getUserProfileJson();
        } else if (requestCode == Utils.SOCIAL_LOGIN_REQ_CODE && resultCode == RESULT_OK) {
            getUserProfileJson();

            String ePhoneVerified = generalFunc.getJsonValue("ePhoneVerified", userProfileJson);

            if (!ePhoneVerified.equals("Yes")) {
                notifyVerifyMobile();
                return;
            }


        } else if (requestCode == Utils.CARD_PAYMENT_REQ_CODE && resultCode == RESULT_OK && data != null) {

            getUserProfileJson();
            isCardValidated = true;

            existingPaymentView(false);

            // addDrawer.changeUserProfileJson(this.userProfileJson);
        } else if (requestCode == Utils.SELECT_COUPON_REQ_CODE && resultCode == RESULT_OK) {
            String couponCode = data.getStringExtra("CouponCode");
            if (couponCode == null) {
                couponCode = "";
            }
            appliedPromoCode = couponCode;
            CheckOutOrderEstimateDetails(couponCode, true);
            setPromoCode();
        } else if (requestCode == Utils.REQ_VERIFY_INSTANT_PAYMENT_CODE && resultCode == RESULT_OK && data != null) {

            if (data != null) {
                orderCompleted();
            } else {
                generalFunc.showError();
            }


        } else if (requestCode == Utils.SELECT_ORGANIZATION_PAYMENT_CODE) {

            if (resultCode == RESULT_OK) {
                if (data.getSerializableExtra("data").equals("")) {


                    if (data.getBooleanExtra("isCash", false)) {
                        ePaymentOption = "Cash";
                    } else {
                        ePaymentOption = "Card";

                       /* if (generalFunc.isDeliverOnlyEnabled()) {
                            selectedMethod = "Instant";
                        } else {*/
                        selectedMethod = "Manual";
                        //}
                    }


                    if (data.getBooleanExtra("isWallet", false)) {
                        isWalletSelect = "Yes";

                    } else {
                        isWalletSelect = "No";

                    }
                    if (Utils.checkText(data.getStringExtra("eTakeAway"))) {
                        eTakeAway = data.getStringExtra("eTakeAway");
                    }

                    placeOrder();


                }

            }
        }  else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            /*boolean isStoragePermissionAvail = false;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    if (generalFunc.isCameraPermissionGranted()) {
                        isStoragePermissionAvail = true;
                    }
                }
            } else {
                isStoragePermissionAvail = generalFunc.isCameraStoragePermissionGranted();
            }*/
            // TODO: 28-04-2021 / Viral | Android 11 (30) Store permission code remove | revert 29
            boolean isStoragePermissionAvail = generalFunc.isCameraStoragePermissionGranted();


            ArrayList<String[]> paramsList = new ArrayList<>();
            paramsList.add(generalFunc.generateImageParams("iMemberId", generalFunc.getMemberId()));
            paramsList.add(generalFunc.generateImageParams("MemberType", Utils.app_type));
            paramsList.add(generalFunc.generateImageParams("type", "uploadImage"));

            if (isStoragePermissionAvail) {

                isFrom = "Camera";
                if (fileUri != null && uploadServicePicAlertBox != null) {
//                        selectedImagePath = ImageFilePath.getPath(getApplicationContext(), fileUri);

                    if (pathForCameraImage.equalsIgnoreCase("")) {
                        selectedImagePath = ImageFilePath.getPath(getActContext(), fileUri);
                    } else {
                        selectedImagePath = pathForCameraImage;
                    }

                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(selectedImagePath, options);

                        int imageHeight = options.outHeight;
                        int imageWidth = options.outWidth;

                        double ratioOfImage = (double) imageWidth / (double) imageHeight;
                        double widthOfImage = ratioOfImage * Utils.dipToPixels(getActContext(), 200);

                        Picasso.get().load(fileUri).resize((int) widthOfImage, Utils.dipToPixels(getActContext(), 200)).into(((ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew)));
                    } catch (Exception e) {
                        Picasso.get().load(fileUri).resize(Utils.dipToPixels(getActContext(), 400), Utils.dipToPixels(getActContext(), 200)).into(((ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew)));
                    }

                    uploadServicePicAlertBox.findViewById(R.id.camImgVIew).setVisibility(View.GONE);
                    uploadServicePicAlertBox.findViewById(R.id.ic_add).setVisibility(View.GONE);
                }
            }
        } else if (resultCode == RESULT_CANCELED) {

        } else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            /*boolean isStoragePermissionAvail = false;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    if (generalFunc.isCameraPermissionGranted()) {
                        isStoragePermissionAvail = true;
                    }
                }
            } else {
                isStoragePermissionAvail = generalFunc.isCameraStoragePermissionGranted();
            }
            if (!isStoragePermissionAvail) {
                return;
            }*/
            // TODO: 28-04-2021 / Viral | Android 11 (30) Store permission code remove | revert 29
            boolean isStoragePermissionAvail = generalFunc.isCameraStoragePermissionGranted();
            if (!isStoragePermissionAvail) {
                return;
            }

            ArrayList<String[]> paramsList = new ArrayList<>();
            paramsList.add(generalFunc.generateImageParams("iMemberId", generalFunc.getMemberId()));
            paramsList.add(generalFunc.generateImageParams("type", "uploadImage"));
            paramsList.add(generalFunc.generateImageParams("MemberType", Utils.app_type));

            Uri selectedImageUri = data.getData();

            selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);

            if (selectedImagePath == null || selectedImagePath.equalsIgnoreCase("")) {
                selectedImagePath = "";
                try {
                    if (uploadServicePicAlertBox != null) {
                        uploadServicePicAlertBox.dismiss();
                    }
                } catch (Exception e) {

                }
                generalFunc.showMessage(generalFunc.getCurrentView((Activity) getActContext()), generalFunc.retrieveLangLBl("Can't read selected image. Please try again.", "LBL_IMAGE_READ_FAILED"));
                return;
            }

            if (isStoragePermissionAvail) {
                isFrom = "Gallary";
                if (selectedImageUri != null && uploadServicePicAlertBox != null) {

                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(selectedImagePath, options);

                        int imageHeight = options.outHeight;
                        int imageWidth = options.outWidth;

                        double ratioOfImage = (double) imageWidth / (double) imageHeight;
                        double widthOfImage = ratioOfImage * Utils.dipToPixels(getActContext(), 200);

                        Picasso.get().load(selectedImageUri).resize((int) widthOfImage, Utils.dipToPixels(getActContext(), 200)).into(((ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew)));


                    } catch (Exception e) {
                        Picasso.get().load(selectedImageUri).resize(Utils.dipToPixels(getActContext(), 400), Utils.dipToPixels(getActContext(), 200)).into(((ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew)));
                    }

                    uploadServicePicAlertBox.findViewById(R.id.camImgVIew).setVisibility(View.GONE);
                    uploadServicePicAlertBox.findViewById(R.id.ic_add).setVisibility(View.GONE);
                }
            }
        }

    }

    public boolean checkCardConfig(String isWalletSelect, boolean redirect, boolean openAddCardView) {

        boolean isCardAdded = false;
        if (selectedMethod.equalsIgnoreCase("Manual")) {
            if (APP_PAYMENT_METHOD.equalsIgnoreCase("Stripe")) {
                String vStripeCusId = generalFunc.getJsonValue("vStripeCusId", userProfileJson);
                if (vStripeCusId.equals("")) {
                    if (openAddCardView) {
                        OpenCardPaymentAct(isWalletSelect);
                    }
                    isCardAdded = false;
                } else {
                    if (redirect) {
                        showPaymentBox(isWalletSelect);
                    }
                    isCardAdded = true;
                }
            }
        } /*else if (selectedMethod.equalsIgnoreCase("Instant")) {
            orderPlaceCallForCard("", isWalletSelect);
        } else {
            orderPlaceCallForCard("", isWalletSelect);
        }*/
        return isCardAdded;
    }

    public void OpenCardPaymentAct(String isWalletSelect) {


        Bundle bn = new Bundle();
        bn.putBoolean("fromCheckout", true);
        bn.putString("isWalletSelect", isWalletSelect);
        new StartActProcess(getActContext()).startActForResult(CardPaymentActivity.class, bn, Utils.CARD_PAYMENT_REQ_CODE);
    }

    public void showPaymentBox(String isWalletSelect) {
        AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.input_box_view, null);
        builder.setView(dialogView);

        final MaterialEditText input = (MaterialEditText) dialogView.findViewById(R.id.editBox);
        final MTextView subTitleTxt = (MTextView) dialogView.findViewById(R.id.subTitleTxt);

        Utils.removeInput(input);

        subTitleTxt.setVisibility(View.VISIBLE);
        subTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TITLE_PAYMENT_ALERT"));
        input.setText(generalFunc.getJsonValue("vCreditCard", userProfileJson));

        builder.setPositiveButton(generalFunc.retrieveLangLBl("Confirm", "LBL_BTN_TRIP_CANCEL_CONFIRM_TXT"), (dialog, which) -> {
            dialog.cancel();
            placeOrder();

        });
        builder.setNeutralButton(generalFunc.retrieveLangLBl("Change", "LBL_CHANGE"), (dialog, which) -> {
            dialog.cancel();
            OpenCardPaymentAct(isWalletSelect);
            //ridelaterclick = false;

        });
        builder.setNegativeButton(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), (dialog, which) -> {
            dialog.cancel();
            //ridelaterclick = false;

        });


        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void checkPaymentCard(String isWalletSelect, boolean validate) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "CheckCard");
        parameters.put("iUserId", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                String action = generalFunc.getJsonValue(Utils.action_str, responseString);
                if (action.equals("1")) {

                    isCardValidated = true;

                    /*if (isWalletSelect.equalsIgnoreCase("Yes") && iswalletZero) {
                        addMoneyToWallet("", isWalletSelect);
                    }else {
                        orderPlaceCallForCard("", isWalletSelect);
                    }*/

                    if (validate) {
                        placeOrder();
                    }
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }


    private void addMoneyToWallet(String token, String CheckUserWallet) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "addMoneyUserWalletByChargeCard");
        parameters.put("CheckUserWallet", CheckUserWallet);
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("fAmount", getIntent().getStringExtra("fAmount"));
        parameters.put("UserType", Utils.app_type);
        parameters.put("vStripeToken", token);
        parameters.put("eSystem", Utils.eSystem_Type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {

                    generalFunc.storeData(Utils.USER_PROFILE_JSON, generalFunc.getJsonValue(Utils.message_str, responseString));
                    getUserProfileJson();

                    GenerateAlertBox generateAlertBox = new GenerateAlertBox(getActContext());
                    generateAlertBox.setCancelable(false);
                    generateAlertBox.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str_one, responseString)));

                    generateAlertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_OK"));
                    generateAlertBox.setBtnClickList(btn_id -> {
                        generateAlertBox.closeAlertBox();
                        orderPlaceCallForCard("", isWalletSelect, "");
                    });
                    generateAlertBox.showAlertBox();
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


    public void orderPlaceCallForCard(String token, String CheckUserWallet, String txref) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "CaptureCardPaymentOrder");
        parameters.put("iIdProofImageId", iIdProofImageId);

        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("ePaymentOption", "Card");
        parameters.put("iOrderId", iOrderId);
        parameters.put("vStripeToken", token);
        parameters.put("CheckUserWallet", CheckUserWallet);
        parameters.put("returnUrl", CommonUtilities.WEBSERVICE);
        parameters.put("eSystem", Utils.eSystem_Type);
        parameters.put("vPayMethod", selectedMethod);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                String action = generalFunc.getJsonValue(Utils.action_str, responseString);
                if (action.equals("1")) {


                    if (generalFunc.getJsonValue("full_wallet_adjustment", responseString) != null && generalFunc.getJsonValue("full_wallet_adjustment", responseString).equalsIgnoreCase("Yes")) {

                        orderCompleted();
                        return;

                    }

                    if (selectedMethod.equalsIgnoreCase("Manual")) {
                        orderCompleted();
                    } else if (selectedMethod.equalsIgnoreCase("Instant")) {

                        if (APP_PAYMENT_METHOD.equalsIgnoreCase("Stripe")) {
                            Bundle bn = new Bundle();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("URL", generalFunc.getJsonValue(Utils.message_str, responseString));
                            map.put("vPageTitle", generalFunc.retrieveLangLBl("", "LBL_INSTANT_PAYMENT_PAGE_TITLE_TXT"));
                            bn.putSerializable("data", map);
                            new StartActProcess(getActContext()).startActForResult(QuickPaymentActivity.class, bn, Utils.REQ_VERIFY_INSTANT_PAYMENT_CODE);
                        }
                    }

                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();

    }

    private void orderCompleted() {
        Bundle bn = new Bundle();
        bn.putBoolean("isRestart", true);
//                        bn.putString("iOrderId", getIntent().getStringExtra("iOrderId"));
        bn.putString("iOrderId", iOrderId);
        bn.putString("eTakeAway", eTakeAway);
        new StartActProcess(getActContext()).startActWithData(OrderPlaceConfirmActivity.class, bn);
    }

    public void notifyVerifyMobile() {
        String vPhoneCode = generalFunc.retrieveValue(Utils.DefaultPhoneCode);
        Bundle bn = new Bundle();
        bn.putString("MOBILE", vPhoneCode + generalFunc.getJsonValue("vPhone", userProfileJson));
        bn.putString("msg", "DO_PHONE_VERIFY");
        bn.putBoolean("isrestart", false);

        new StartActProcess(getActContext()).startActForResult(VerifyInfoActivity.class, bn, Utils.VERIFY_MOBILE_REQ_CODE);

    }

    private void existingPaymentView(boolean highlightView) {
        existingCardTxt.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        setViewBasedOnSelection(highlightView);
        cardNumTxt.setVisibility(View.VISIBLE);
        editcard_area.setVisibility(View.VISIBLE);
        manual_arrow_right.setVisibility(View.GONE);
        cardNumTxt.setText(generalFunc.getJsonValue("vCreditCard", userProfileJson));
    }

    private void defaultPaymentView(boolean highlightView) {
        existingCardTxt.getLayoutParams().height = Utils.dpToPx(35, getActContext());
        setViewBasedOnSelection(highlightView);
        cardNumTxt.setVisibility(View.GONE);
        editcard_area.setVisibility(View.GONE);
        manual_arrow_right.setVisibility(View.VISIBLE);
    }

    private void setViewBasedOnSelection(boolean highlightView) {
        if (highlightView) {
            if (selectedMethod.equalsIgnoreCase("Manual")) {
                existingCardTxt.setTextColor(getResources().getColor(R.color.appThemeColor_1));
                instantPaymentTxt.setTextColor(getResources().getColor(R.color.black));
                instant_arrow_right.setColorFilter(ContextCompat.getColor(getActContext(), R.color.black), PorterDuff.Mode.MULTIPLY);
                manual_arrow_right.setColorFilter(ContextCompat.getColor(getActContext(), R.color.appThemeColor_1), PorterDuff.Mode.MULTIPLY);

            } else if (selectedMethod.equalsIgnoreCase("Instant")) {
                existingCardTxt.setTextColor(getResources().getColor(R.color.black));
                instantPaymentTxt.setTextColor(getResources().getColor(R.color.appThemeColor_1));
                instant_arrow_right.setColorFilter(ContextCompat.getColor(getActContext(), R.color.appThemeColor_1), PorterDuff.Mode.MULTIPLY);
                manual_arrow_right.setColorFilter(ContextCompat.getColor(getActContext(), R.color.black), PorterDuff.Mode.MULTIPLY);
            }
        }
    }

    public void defaultPromoView() {
        promocodeArea.setVisibility(View.GONE);
        appliedPromoHTxtView.setVisibility(View.GONE);


        couponCodeImgView.setVisibility(View.VISIBLE);
        couponCodeCloseImgView.setVisibility(View.GONE);
        applyCouponHTxt.setTextColor(Color.parseColor("#333333"));
        applyCouponHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_APPLY_COUPON"));

        promocodeappliedHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_APPLIED_COUPON_CODE"));

    }

    public void appliedPromoView() {
        appliedPromoHTxtView.setVisibility(View.VISIBLE);
        applyCouponHTxt.setText(appliedPromoCode);
        applyCouponHTxt.setTextColor(getResources().getColor(R.color.appThemeColor_1));
        couponCodeImgView.setVisibility(View.GONE);
        couponCodeCloseImgView.setVisibility(View.VISIBLE);
        couponCodeCloseImgView.setOnClickListener(new setOnClickList());
        appliedPromoHTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_APPLIED_COUPON_CODE"));
    }

    public void setCancelable(Dialog dialogview, boolean cancelable) {
        final Dialog dialog = dialogview;
        View touchOutsideView = dialog.getWindow().getDecorView().findViewById(R.id.touch_outside);
        View bottomSheetView = dialog.getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);

        if (cancelable) {
            touchOutsideView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog.isShowing()) {
                        dialog.cancel();
                    }
                }
            });
            BottomSheetBehavior.from(bottomSheetView).setHideable(true);
        } else {
            touchOutsideView.setOnClickListener(null);
            BottomSheetBehavior.from(bottomSheetView).setHideable(false);
        }
    }

    public void manageWalletCheckBox() {
        if (SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
            checkboxWallet.setVisibility(View.VISIBLE);
            walletAmountTxt.setVisibility(View.VISIBLE);
            iswalletZero = true;
        }
    }

    public void CheckOutOrderEstimateDetails(String promoCode, boolean isPromoApplied) {
        Logger.d("CheckPromoValue", "::" + promoCode);

        try {
            getLocalData();
            JSONArray orderedItemArr = new JSONArray();
            for (int i = 0; i < cartList.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("iMenuItemId", cartList.get(i).getiMenuItemId());
                object.put("iFoodMenuId", cartList.get(i).getiFoodMenuId());
                object.put("vOptionId", cartList.get(i).getiOptionId());
                object.put("vAddonId", cartList.get(i).getiToppingId());
                object.put("iQty", cartList.get(i).getQty());
                orderedItemArr.put(object);
            }


            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("type", "CheckOutOrderEstimateDetails");
            parameters.put("iUserId", generalFunc.getMemberId());
            parameters.put("iCompanyId", cartList.get(0).getiCompanyId());
            if (!eTakeAway.equalsIgnoreCase("Yes")) {
                HashMap<String, String> data = new HashMap<>();
                data.put(Utils.SELECT_ADDRESS_ID, "");
                data.put(Utils.SELECT_LATITUDE, "");
                data.put(Utils.SELECT_LONGITUDE, "");
                data = generalFunc.retrieveValue(data);
                parameters.put("iUserAddressId", data.get(Utils.SELECT_ADDRESS_ID));
                parameters.put("PassengerLat", data.get(Utils.SELECT_LATITUDE));
                parameters.put("PassengerLon", data.get(Utils.SELECT_LONGITUDE));
            }
            parameters.put("vCouponCode", promoCode.trim());
            parameters.put("ePaymentOption", ePaymentOption);
            parameters.put("vInstruction", deliveryInstructionBox.getText().toString().trim());
            parameters.put("OrderDetails", orderedItemArr.toString());
            parameters.put("CheckUserWallet", isWalletSelect);
            parameters.put("eSystem", Utils.eSystem_Type);

            if (Utils.checkText(eTakeAway)) {
                parameters.put("eTakeAway", eTakeAway);
            }
            /*Tip Feature - Parameters */
            if (Utils.checkText(tipAmount)) {
                if (isPercentage) {
                    parameters.put("selectedTipPos", tipAmount);
                    if (tipAmount.equalsIgnoreCase("4")) {
                        parameters.put("fTipAmount", Utils.getText(tipAmountBox));
                    }
                } else {
                    parameters.put("fTipAmount", tipAmount);

                }
            }

            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
            if (headerArea.getVisibility() == View.VISIBLE) {
                exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
            }
            exeWebServer.setDataResponseListener(responseString -> {
                JSONObject responseObj = generalFunc.getJsonObject(responseString);

                if (responseObj != null && !responseObj.equals("")) {
                    manageView();

                    String action = generalFunc.getJsonValueStr(Utils.action_str, responseObj);

                    if (action.equals("1")) {

                        eProofUpload = generalFunc.getJsonValueStr("eProofUpload", responseObj);
                        eShowTerms = generalFunc.getJsonValueStr("eShowTerms", responseObj);
                        tProofNote = generalFunc.getJsonValueStr("tProofNote", responseObj);
                        headerArea.setVisibility(View.VISIBLE);
                        detailsArea.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);
                        errorView.setVisibility(View.GONE);
                        appliedPromoCode = promoCode;

                        int height = (int) getResources().getDimension(R.dimen._100sdp);
                        int width = (int) getResources().getDimension(R.dimen._100sdp);

                        Picasso.get()
                                .load(generalFunc.getJsonValueStr("vImage", responseObj))
                                .placeholder(R.mipmap.ic_no_icon)
                                .error(R.mipmap.ic_no_icon)
                                .resize(width, height)
                                .into(storeImg, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        try {
                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                                storeImg.invalidate();
                                                BitmapDrawable drawable = (BitmapDrawable) storeImg.getDrawable();
                                                Bitmap bitmap = drawable.getBitmap();

                                                CardView mCardView = findViewById(R.id.mCardView);
                                                mCardView.setPreventCornerOverlap(false);

                                                RoundCornerDrawable round = new RoundCornerDrawable(bitmap, getResources().getDimension(R.dimen._10sdp), 0);
                                                storeImg.setVisibility(View.GONE);
                                                mCardView.setBackground(round);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                        resAddressTxtView.setText(generalFunc.getJsonValueStr("vCaddress", responseObj));


                        // if (SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {

                        if (generalFunc.getJsonValueStr("DISABLE_CASH_PAYMENT_OPTION", responseObj).equalsIgnoreCase("yes")) {

                            if (Utils.checkText(APP_PAYMENT_MODE)/* && !APP_PAYMENT_MODE.equalsIgnoreCase("Cash")*/) {
                                cashRadio.setEnabled(false);
                                cashRadio.setChecked(false);
                                isCODAllow = false;
                                outStandingAmount = generalFunc.getJsonValueStr("fOutStandingAmount", responseObj);

                                if (!APP_PAYMENT_MODE.equalsIgnoreCase("Card")) {
                                    casenote.setVisibility(View.VISIBLE);
                                    casenote.setText(generalFunc.retrieveLangLBl("", "LBL_COD_NOT_AVAILABLE_TXT") + " " + generalFunc.getJsonValue("fOutStandingAmount", responseObj));
                                }
                                if (!APP_PAYMENT_MODE.equalsIgnoreCase("Cash")) {
                                    cardRadio.setChecked(true);
                                }
                            }

                            manageWalletCheckBox();

                        } else {
                            String WALLET_ENABLE = userProfileJson != null ? generalFunc.getJsonValue(Utils.WALLET_ENABLE, userProfileJson) : "";
                            if (!WALLET_ENABLE.equals("") && WALLET_ENABLE.equalsIgnoreCase("Yes") && !generalFunc.getJsonValue("user_available_balance_amount", userProfileJson).equalsIgnoreCase("") &&
                                    GeneralFunctions.parseDoubleValue(0, generalFunc.getJsonValue("user_available_balance_amount", userProfileJson)) > 0) {

                                casenote.setVisibility(View.GONE);
                                cashRadio.setEnabled(true);
                                manageWalletCheckBox();

                            } else {
                                manageWalletCheckBox();
                            }


                        }
                        // }


                        JSONObject DeliveryPreferences = generalFunc.getJsonObject("DeliveryPreferences", responseObj);

                        isPreference = generalFunc.getJsonValueStr("Enable", DeliveryPreferences).equalsIgnoreCase("Yes") ? true : false;
                        if (isPreference) {
                            moreinstructionLyout.setVisibility(View.VISIBLE);
                            instructionslit.clear();
                            textmoreinstruction.setText(generalFunc.getJsonValueStr("vTitle", DeliveryPreferences));
                            JSONArray Data = generalFunc.getJsonArray("Data", DeliveryPreferences);
                            for (int i = 0; i < Data.length(); i++) {
                                try {
                                    JSONObject jsonObject = (JSONObject) Data.get(i);
                                    String tTitle = generalFunc.getJsonValueStr("tTitle", jsonObject);
                                    String tDescription = generalFunc.getJsonValueStr("tDescription", jsonObject);
                                    String ePreferenceFor = generalFunc.getJsonValueStr("ePreferenceFor", jsonObject);
                                    String eImageUpload = generalFunc.getJsonValueStr("eImageUpload", jsonObject);
                                    String iDisplayOrder = generalFunc.getJsonValueStr("iDisplayOrder", jsonObject);
                                    String eContactLess = generalFunc.getJsonValueStr("eContactLess", jsonObject);
                                    String eStatus = generalFunc.getJsonValueStr("eStatus", jsonObject);
                                    String iPreferenceId = generalFunc.getJsonValueStr("iPreferenceId", jsonObject);
                                    HashMap<String, String> hashMap = new HashMap<>();

                                    hashMap.put("tTitle", tTitle);
                                    hashMap.put("tDescription", tDescription);
                                    hashMap.put("ePreferenceFor", ePreferenceFor);
                                    hashMap.put("eImageUpload", eImageUpload);
                                    hashMap.put("iDisplayOrder", iDisplayOrder);
                                    hashMap.put("eContactLess", eContactLess);
                                    hashMap.put("eStatus", eStatus);
                                    hashMap.put("iPreferenceId", iPreferenceId);
                                    instructionslit.add(hashMap);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            moreInstructionAdapter.notifyDataSetChanged();

                        } else {
                            moreinstructionLyout.setVisibility(View.GONE);
                        }

                        currencySymbol = generalFunc.getJsonValueStr("currencySymbol", responseObj);
                        itemArraylist.clear();
                        JSONArray OrderDetailsItemsArr = generalFunc.getJsonArray("OrderDetailsItemsArr", responseObj);

                        if (itemContainer.getChildCount() > 0) {
                            itemContainer.removeAllViewsInLayout();
                        }

                        if (OrderDetailsItemsArr != null && OrderDetailsItemsArr.length() > 0) {
                            for (int j = 0; j < OrderDetailsItemsArr.length(); j++) {
                                JSONObject itemObj = generalFunc.getJsonObject(OrderDetailsItemsArr, j);
                                HashMap<String, String> itemmap = new HashMap<>();
                                itemmap.put("iMenuItemId", generalFunc.getJsonValueStr("iMenuItemId", itemObj));
                                itemmap.put("iFoodMenuId", generalFunc.getJsonValueStr("iFoodMenuId", itemObj));
                                itemmap.put("vItemType", generalFunc.getJsonValueStr("vItemType", itemObj));
                                itemmap.put("iQty", generalFunc.getJsonValueStr("iQty", itemObj));
                                itemmap.put("fOfferAmt", generalFunc.getJsonValueStr("fOfferAmt", itemObj));
                                itemmap.put("fOriginalPrice", generalFunc.getJsonValueStr("fOriginalPrice", itemObj));
                                itemmap.put("fPrice", generalFunc.getJsonValueStr("fPrice", itemObj));
                                itemmap.put("currencySymbol", generalFunc.getJsonValueStr("currencySymbol", itemObj));
                                itemmap.put("optionaddonname", generalFunc.getJsonValueStr("optionaddonname", itemObj));
                                itemmap.put("vImage", generalFunc.getJsonValueStr("vImage", itemObj));
                                itemArraylist.add(itemmap);

                            }
                            addItemView();
                        }

                        if (eTakeAway.equalsIgnoreCase("Yes")) {
                            prevselectaddress = isselectaddress;
                            isselectaddress = true;
                            EditAddressArea.setVisibility(View.GONE);
                            AddAddressArea.setVisibility(View.GONE);
                        } else {
                            isselectaddress = prevselectaddress;
                            ToTalAddress = generalFunc.getJsonValueStr("ToTalAddress", responseObj);
                            EditAddressArea.setVisibility(GeneralFunctions.parseIntegerValue(0, ToTalAddress) > 0 ? View.VISIBLE : View.GONE);
                            AddAddressArea.setVisibility(GeneralFunctions.parseIntegerValue(0, ToTalAddress) > 0 ? View.GONE : View.VISIBLE);
                        }
                        //Tip Feature - Tip Area Show Setting
                        String ENABLE_TIP_MODULE_DELIVERALL_ = generalFunc.getJsonValue("ENABLE_TIP_MODULE_DELIVERALL", userProfileJson);
                        tipCardArea.setVisibility(eTakeAway.equalsIgnoreCase("No") && !generalFunc.getMemberId().equals("") && ENABLE_TIP_MODULE_DELIVERALL_.equalsIgnoreCase("Yes") ? View.VISIBLE : View.GONE);

                        if (!isselectaddress) {
                            String UserSelectedAddressId = generalFunc.getJsonValueStr("UserSelectedAddressId", responseObj);

                            if (UserSelectedAddressId != null && !UserSelectedAddressId.equalsIgnoreCase("")) {
                                HashMap<String, String> storeData = new HashMap<>();
                                storeData.put(Utils.SELECT_ADDRESSS, generalFunc.getJsonValueStr("UserSelectedAddress", responseObj));
                                storeData.put(Utils.SELECT_LATITUDE, generalFunc.getJsonValueStr("UserSelectedLatitude", responseObj));
                                storeData.put(Utils.SELECT_LONGITUDE, generalFunc.getJsonValueStr("UserSelectedLongitude", responseObj));
                                storeData.put(Utils.SELECT_ADDRESS_ID, UserSelectedAddressId);
                                generalFunc.storeData(storeData);

                                addressTxt.setText(generalFunc.retrieveValue(Utils.SELECT_ADDRESSS));

                            } else {
                                isselectaddress = false;
                                addressTxt.setText(generalFunc.retrieveValue(Utils.SELECT_ADDRESSS));
                            }
                        } else {
                            isselectaddress = false;
                            addressTxt.setText(generalFunc.retrieveValue(Utils.SELECT_ADDRESSS));

                        }

                        subtotalVTxt.setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("fSubTotal", responseObj)));
                        restaurantstatus = generalFunc.getJsonValueStr("restaurantstatus", responseObj);
                        //LBL_DELIVERY_CHARGE
                        if (!generalFunc.getMemberId().equals("") && !generalFunc.getJsonValueStr("fNetTotalAmount", responseObj).equalsIgnoreCase("0")) {
                            //  btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_PAY") + " " + generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("fNetTotal", responseObj)));
                            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_PAY"));
                            if (eProofUpload.equalsIgnoreCase("Yes")) {
                                btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_NEXT_TXT"));
                            }
                        } else {
                            if (!generalFunc.getMemberId().equals("")) {
                                btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_PAY"));
                                if (eProofUpload.equalsIgnoreCase("Yes")) {
                                    btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_NEXT_TXT"));
                                }
                            }
                        }

                        if (SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {

                            DisplayCardPayment = generalFunc.getJsonValueStr("DisplayCardPayment", responseObj);
                            if (!DisplayCardPayment.equalsIgnoreCase("") && DisplayCardPayment.equalsIgnoreCase("Yes")) {
                                cardRadio.setEnabled(true);
                            } else {
                                cardRadio.setEnabled(false);
                                cardRadio.setChecked(false);
                                cashRadio.setChecked(true);
                            }

                            String DisplayUserWalletDebitAmount = generalFunc.getJsonValueStr("DisplayUserWalletDebitAmount", responseObj);
                            if (DisplayUserWalletDebitAmount != null &&
                                    !DisplayUserWalletDebitAmount.equalsIgnoreCase("")) {
                                walletAmountTxt.setText(generalFunc.convertNumberWithRTL(DisplayUserWalletDebitAmount));
                            } else {
                                walletAmountTxt.setText("");
                            }
                        }
                        Double fDiscount = generalFunc.parseDoubleValue(0, generalFunc.getJsonValueStr("fDiscount", responseObj));

                        if (generalFunc.getJsonValueStr("eFreeDelivery", responseObj).equalsIgnoreCase("")
                                || generalFunc.getJsonValueStr("eFreeDelivery", responseObj).equalsIgnoreCase("No")) {
                            if (fDiscount > 0) {
                                appliedPromoView();
                            } else {
                                defaultPromoView();
                            }
                        }


//                            if (isPromoApplied) {
//                                // generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_APPLIED"));
//                            } else {
//                                defaultPromoView();
//                            }


                        finalTotal = Double.parseDouble(generalFunc.getJsonValueStr("fSubTotalamount", responseObj));

                        JSONArray FareDetailsArr = null;
                        FareDetailsArr = generalFunc.getJsonArray("FareDetailsArrNew", responseObj);

                        if (farecontainer.getChildCount() > 0) {
                            farecontainer.removeAllViews();
                        }
                        addFareDetailLayout(FareDetailsArr);


                        if (generalFunc.getMemberId().equalsIgnoreCase("")) {
                            isLogin = false;
                        } else {
                            ToTalAddress = generalFunc.getJsonValueStr("ToTalAddress", responseObj);
                            isLogin = true;
                            if (GeneralFunctions.parseIntegerValue(0, ToTalAddress) > 0) {
                                ToTalAddress = generalFunc.getJsonValueStr("ToTalAddress", responseObj);
                                try {
                                    onResume();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                // openAddressDailog(generalFunc.getJsonValueStr("RestaurantAddressNotMatch", responseObj));
                                AddAddressArea.setVisibility(View.VISIBLE);
                                EditAddressArea.setVisibility(View.GONE);
                            }
                        }
                    } else if (action.equals("01")) {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMOCODE_ALREADY_USED"));
                    } else {


                        if (generalFunc.getJsonValueStr(Utils.message_str, responseObj) != null && generalFunc.getJsonValueStr(Utils.message_str, responseObj).equalsIgnoreCase("LBL_INVALID_PROMOCODE")) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_INVALID_PROMOCODE"));
                            return;
                        }

                        if (generalFunc.getJsonValueStr(Utils.message_str, responseObj) != null && generalFunc.getJsonValueStr(Utils.message_str, responseObj).equalsIgnoreCase("LBL_PROMOCODE_COMPLETE_USAGE_LIMIT")) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMOCODE_COMPLETE_USAGE_LIMIT"));
                            return;
                        }

                        if (generalFunc.getMemberId().equalsIgnoreCase("")) {
                            isLogin = false;
                        } else {

                            if (generalFunc.getJsonValueStr("ToTalAddress", responseObj) != null) {
                                ToTalAddress = generalFunc.getJsonValueStr("ToTalAddress", responseObj);
                                isLogin = true;
                                if (GeneralFunctions.parseIntegerValue(0, ToTalAddress) > 0) {
                                    ToTalAddress = generalFunc.getJsonValueStr("ToTalAddress", responseObj);
                                    onResume();
                                } else {
                                    //openAddressDailog(generalFunc.getJsonValueStr("RestaurantAddressNotMatch", responseObj));
                                    AddAddressArea.setVisibility(View.VISIBLE);
                                    EditAddressArea.setVisibility(View.GONE);
                                    return;
                                }
                            }
                        }

                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseObj)));


                    }

                    if (generalFunc.getJsonValue("eTakeaway", responseObj) != null) {
                        deliveryTypeArea.setVisibility(generalFunc.getJsonValue("eTakeaway", responseObj).equals("Yes") ? View.VISIBLE : View.GONE);
                    }
                    if (generalFunc.getMemberId().equalsIgnoreCase("")) {
                        deliveryTypeArea.setVisibility(View.GONE);
                    }
                } else {
                    generalFunc.showError();
                }
            });
            exeWebServer.execute();
        } catch (Exception e) {
            Logger.d("CheckEx", "::" + e.toString());
        }

    }

    private void addFareDetailLayout(JSONArray jobjArray) {


        boolean islast = false;
        for (int i = 0; i < jobjArray.length(); i++) {
            JSONObject jobject = generalFunc.getJsonObject(jobjArray, i);
            try {
                if ((i == jobjArray.length() - 1)) {
                    islast = true;
                } else {
                    islast = false;
                }
                addFareDetailRow(jobject.names().getString(0), jobject.get(jobject.names().getString(0)).toString(), islast);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void addFareDetailRow(String row_name, String row_value, boolean islast) {
        LayoutInflater infalInflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = infalInflater.inflate(R.layout.design_fare_breakdown_row, null);

        MTextView titleHTxt = (MTextView) convertView.findViewById(R.id.titleHTxt);
        MTextView titleVTxt = (MTextView) convertView.findViewById(R.id.titleVTxt);


        titleHTxt.setText(generalFunc.convertNumberWithRTL(row_name));
        titleVTxt.setText(generalFunc.convertNumberWithRTL(row_value));


        if (islast) {
            convertView.setMinimumHeight(Utils.dipToPixels(getActContext(), 40));

            titleHTxt.setTextColor(getResources().getColor(R.color.black));
            titleHTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Poppins_SemiBold.ttf");
            titleHTxt.setTypeface(face);
            titleVTxt.setTypeface(face);
            titleVTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            titleVTxt.setTextColor(getResources().getColor(R.color.appThemeColor_1));
        }

        farecontainer.addView(convertView);
    }

    public void manageView() {
        if (internetConnection.isNetworkConnected()) {
            errorView.setVisibility(View.GONE);
        } else {
            errorView.setVisibility(View.VISIBLE);
        }
    }

    public void manageRecordView(boolean isStart) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                if (isStart) {
                    if (miscNoteArea != null) {
                        miscImg.setVisibility(View.GONE);
                        miscNoteArea.setVisibility(View.GONE);


                    }
                    if (headerArea != null) {
                        headerArea.requestDisallowInterceptTouchEvent(true);
                        moreinstructionLyout.requestDisallowInterceptTouchEvent(true);
                        nestedScroll.setNestedScrollingEnabled(false);


                    }

                } else {
                    if (miscNoteArea != null) {
                        miscImg.setVisibility(View.VISIBLE);
                        miscNoteArea.setVisibility(View.VISIBLE);


                        if (headerArea != null) {
                            headerArea.requestDisallowInterceptTouchEvent(false);
                            moreinstructionLyout.requestDisallowInterceptTouchEvent(false);
                            nestedScroll.setNestedScrollingEnabled(true);


                        }


                    }
                }
            }
        });

    }

    public void orderPlaceCall() {
        try {

            clearMediaPlayer();
            JSONArray orderedItemArr = new JSONArray();
            for (int i = 0; i < cartList.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("iMenuItemId", cartList.get(i).getiMenuItemId());
                object.put("iFoodMenuId", cartList.get(i).getiFoodMenuId());
                object.put("vOptionId", cartList.get(i).getiOptionId());
                object.put("vAddonId", cartList.get(i).getiToppingId());
                object.put("iQty", cartList.get(i).getQty());
                orderedItemArr.put(object);
            }
            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("type", "CheckOutOrderDetails");
            parameters.put("iVoiceDirectionFileId", iVoiceDirectionFileId);
            parameters.put("iIdProofImageId", iIdProofImageId);
            parameters.put("iServiceId", generalFunc.getServiceId());
            parameters.put("iUserId", generalFunc.getMemberId());
            parameters.put("iCompanyId", cartList.get(0).getiCompanyId());
            parameters.put("iUserAddressId", generalFunc.retrieveValue(Utils.SELECT_ADDRESS_ID));
            parameters.put("vCouponCode", appliedPromoCode);
            parameters.put("ePaymentOption", ePaymentOption);
            parameters.put("vInstruction", deliveryInstructionBox.getText().toString().trim());
            parameters.put("OrderDetails", orderedItemArr.toString());
            parameters.put("CheckUserWallet", isWalletSelect);
//            parameters.put("iOrderId", iOrderId);
            /*Tip Feature - Parameters */
            if (Utils.checkText(tipAmount)) {
                if (isPercentage) {
                    parameters.put("selectedTipPos", tipAmount);
                    if (tipAmount.equalsIgnoreCase("4")) {
                        parameters.put("fTipAmount", Utils.getText(tipAmountBox));
                    }
                } else {
                    parameters.put("fTipAmount", tipAmount);

                }
            }

            int size = currentSelectedItems.size();
            if (isPreference && size > 0) {
                parameters.put("selectedprefrences", getPreferenceIds());
            }


            if (!ePaymentOption.equalsIgnoreCase("Cash") && (!SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1"))) {
                parameters.put("CheckUserWallet", "Yes");
                parameters.put("ePayWallet", "Yes");

            }
            parameters.put("eSystem", Utils.eSystem_Type);
            parameters.put("eWalletIgnore", eWalletIgnore);

            if (Utils.checkText(eTakeAway)) {
                parameters.put("eTakeAway", eTakeAway);
            }

            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
            exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);
            exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
            exeWebServer.setDataResponseListener(responseString -> {
                if (generalFunc.getJsonValue(Utils.message_str, responseString) != null && generalFunc.getJsonValue(Utils.message_str, responseString).equalsIgnoreCase("LBL_PROMOCODE_COMPLETE_USAGE_LIMIT")) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMOCODE_COMPLETE_USAGE_LIMIT"));
                    return;
                }


                if (responseString != null && !responseString.equals("")) {

                    String action = generalFunc.getJsonValue(Utils.action_str, responseString);
                    if (action.equals("1")) {

                        iOrderId = generalFunc.getJsonValue("iOrderId", responseString);
                        Bundle bn = new Bundle();
                        bn.putString("iOrderId", iOrderId);
                        if (ePaymentOption.equalsIgnoreCase("card") && SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
                            orderPlaceCallForCard("", isWalletSelect, "");
                        } else {
                            bn.putString("eTakeAway", eTakeAway);
                            new StartActProcess(getActContext()).startActWithData(OrderPlaceConfirmActivity.class, bn);
                        }

                    } else {

                        iOrderId = generalFunc.getJsonValue("iOrderId", responseString);
                        if (generalFunc.getJsonValue(Utils.message_str, responseString).equalsIgnoreCase("LOW_WALLET_AMOUNT")) {


                            String walletMsg = "";
                            String low_balance_content_msg = generalFunc.getJsonValue("low_balance_content_msg", responseString);

                            if (low_balance_content_msg != null && !low_balance_content_msg.equalsIgnoreCase("")) {
                                walletMsg = low_balance_content_msg;
                            } else {
                                walletMsg = generalFunc.retrieveLangLBl("", "LBL_WALLET_LOW_AMOUNT_MSG_TXT");
                            }

                            if (!isCODAllow && SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-2")) {
                                generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("", "LBL_LOW_WALLET_BALANCE"), walletMsg,
                                        generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("", "LBL_ADD_MONEY"), button_Id -> {
                                            if (button_Id == 1) {
                                                Bundle bn = new Bundle();
                                                bn.putString("iServiceId", generalFunc.getServiceId());
                                                bn.putString("isCheckout", "");
                                                new StartActProcess(getActContext()).startAct(MyWalletActivity.class);
                                            } else if (button_Id == 2) {

                                            }
                                        });

                            } else {

                                generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("", "LBL_LOW_WALLET_BALANCE"), walletMsg, generalFunc.getJsonValue("IS_RESTRICT_TO_WALLET_AMOUNT", responseString).equalsIgnoreCase("No") ? generalFunc.retrieveLangLBl("", "LBL_CONTINUE_BTN") :
                                        generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("", "LBL_ADD_MONEY"), generalFunc.getJsonValue("IS_RESTRICT_TO_WALLET_AMOUNT", responseString).equalsIgnoreCase("NO") ? generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT") :
                                        "", button_Id -> {
                                    if (button_Id == 1) {

                                        new StartActProcess(getActContext()).startAct(MyWalletActivity.class);
                                    } else if (button_Id == 0) {
                                        if (generalFunc.getJsonValue("IS_RESTRICT_TO_WALLET_AMOUNT", responseString).equalsIgnoreCase("No")) {
                                            eWalletIgnore = "Yes";
                                            placeOrder();
                                        }
                                    } else if (button_Id == 2) {
                                        iOrderId = "";
                                        ePaymentOption = "Cash";
                                        isWalletSelect = "No";
                                    }
                                });
                            }

                            return;

                        }

                        if (generalFunc.getJsonValue(Utils.message_str, responseString).equalsIgnoreCase("LBL_RESTAURANTS_CLOSE_NOTE")) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_RESTAURANTS_CLOSE_NOTE"));

                        } else if (generalFunc.getJsonValue(Utils.message_str, responseString).equalsIgnoreCase("LBL_MENU_ITEM_NOT_AVAILABLE_TXT")) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_MENU_ITEM_NOT_AVAILABLE_TXT"));

                        } else if (generalFunc.getJsonValue(Utils.message_str, responseString).equalsIgnoreCase("LBL_DELIVERY_LOCATION_NOT_ALLOW")) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_DELIVERY_LOCATION_NOT_ALLOW"));
                        } else {

                            String ShowContactUsBtn = generalFunc.getJsonValue("ShowContactUsBtn", responseString);
                            ShowContactUsBtn = (ShowContactUsBtn == null || ShowContactUsBtn.isEmpty()) ? "No" : ShowContactUsBtn;
                            if (ShowContactUsBtn.equalsIgnoreCase("Yes")) {
                                closeuploadServicePicAlertBox();
                                String outstanding_restriction_label = generalFunc.getJsonValue(Utils.message_str, responseString);
                                if (outstanding_restriction_label != null && !outstanding_restriction_label.isEmpty()) {
                                    final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                                    generateAlert.setCancelable(false);
                                    generateAlert.setBtnClickList(btn_id -> {
                                        if (btn_id == 1) {
                                            new StartActProcess(getActContext()).startAct(ContactUsActivity.class);
                                        }
                                    });
                                    generateAlert.setContentMessage("", outstanding_restriction_label);
                                    generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_OK"));
                                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_CONTACT_US_TXT"));

                                    generateAlert.showAlertBox();
                                    return;
                                }
                            }
                            generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));

                        }


                    }
                } else {
                    generalFunc.showError();
                }
            });
            exeWebServer.execute();
        } catch (Exception e) {

        }

    }

    public String[] generateImageParams(String key, String content) {
        String[] tempArr = new String[2];
        tempArr[0] = key;
        tempArr[1] = content;

        return tempArr;
    }

    public void OrdePalceWithAudio() {
        ArrayList<String[]> paramsList = new ArrayList<>();
        paramsList.add(generalFunc.generateImageParams("type", "UploadVoiceDirectionFile"));
        paramsList.add(generalFunc.generateImageParams("UserType", Utils.app_type));
        paramsList.add(Utils.generateImageParams("iMemberId", generalFunc.getMemberId()));
        paramsList.add(Utils.generateImageParams("iUserId", generalFunc.getMemberId()));
        paramsList.add(Utils.generateImageParams("MemberType", Utils.app_type));
        paramsList.add(Utils.generateImageParams("tSessionId", generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY)));
        paramsList.add(Utils.generateImageParams("GeneralUserType", Utils.app_type));
        paramsList.add(Utils.generateImageParams("GeneralMemberId", generalFunc.getMemberId()));
        paramsList.add((Utils.generateImageParams("eSystem", Utils.eSystem_Type)));

        new UploadProfileImage(CheckOutActivity.this, recordingItem1.getFilePath(), recordingItem1.getName(), paramsList, true).execute();

    }

    public void setGeneralData() {
        // HashMap<String, String> storeData = new HashMap<>();

//        storeData.put(Utils.PUBNUB_PUB_KEY, generalFunc.getJsonValue("PUBNUB_PUBLISH_KEY", userProfileJson));
//        storeData.put(Utils.PUBNUB_SUB_KEY, generalFunc.getJsonValue("PUBNUB_SUBSCRIBE_KEY", userProfileJson));
//        storeData.put(Utils.PUBNUB_SEC_KEY, generalFunc.getJsonValue("PUBNUB_SECRET_KEY", userProfileJson));
//        storeData.put(Utils.SESSION_ID_KEY, generalFunc.getJsonValue("tSessionId", userProfileJson));
//        storeData.put(Utils.RIDER_REQUEST_ACCEPT_TIME_KEY, generalFunc.getJsonValue("RIDER_REQUEST_ACCEPT_TIME", userProfileJson));
//        storeData.put(Utils.DEVICE_SESSION_ID_KEY, generalFunc.getJsonValue("tDeviceSessionId", userProfileJson));
//        storeData.put(Utils.FETCH_TRIP_STATUS_TIME_INTERVAL_KEY, generalFunc.getJsonValue("FETCH_TRIP_STATUS_TIME_INTERVAL", userProfileJson));
//        storeData.put(Utils.APP_DESTINATION_MODE, generalFunc.getJsonValue("APP_DESTINATION_MODE", userProfileJson));
//        storeData.put(Utils.APP_TYPE, generalFunc.getJsonValue("APP_TYPE", userProfileJson));
//        storeData.put(Utils.SITE_TYPE_KEY, generalFunc.getJsonValue("SITE_TYPE", userProfileJson));
//        storeData.put(Utils.ENABLE_TOLL_COST, generalFunc.getJsonValue("ENABLE_TOLL_COST", userProfileJson));
//        storeData.put(Utils.TOLL_COST_APP_ID, generalFunc.getJsonValue("TOLL_COST_APP_ID", userProfileJson));
//        storeData.put(Utils.TOLL_COST_APP_CODE, generalFunc.getJsonValue("TOLL_COST_APP_CODE", userProfileJson));
//        storeData.put(Utils.HANDICAP_ACCESSIBILITY_OPTION, generalFunc.getJsonValue("HANDICAP_ACCESSIBILITY_OPTION", userProfileJson));
//        storeData.put(Utils.FEMALE_RIDE_REQ_ENABLE, generalFunc.getJsonValue("FEMALE_RIDE_REQ_ENABLE", userProfileJson));
//        storeData.put(Utils.PUBNUB_DISABLED_KEY, generalFunc.getJsonValue("PUBNUB_DISABLED", userProfileJson));
//
//        storeData.put(Utils.ENABLE_SOCKET_CLUSTER_KEY, generalFunc.getJsonValue("ENABLE_SOCKET_CLUSTER", userProfileJson));
//        storeData.put(Utils.SC_CONNECT_URL_KEY, generalFunc.getJsonValue("SC_CONNECT_URL", userProfileJson));
//        storeData.put(Utils.GOOGLE_SERVER_ANDROID_PASSENGER_APP_KEY, generalFunc.getJsonValue("GOOGLE_SERVER_ANDROID_PASSENGER_APP_KEY", userProfileJson));
//        storeData.put("DESTINATION_UPDATE_TIME_INTERVAL", generalFunc.getJsonValue("DESTINATION_UPDATE_TIME_INTERVAL", userProfileJson));
//        storeData.put(Utils.DELIVERALL_KEY, generalFunc.getJsonValue(Utils.DELIVERALL_KEY, userProfileJson));
//        storeData.put(Utils.ONLYDELIVERALL_KEY, generalFunc.getJsonValue(Utils.ONLYDELIVERALL_KEY, userProfileJson));
//        storeData.put("showCountryList", generalFunc.getJsonValue("showCountryList", userProfileJson));
//
//        storeData.put(Utils.ISWALLETBALNCECHANGE, "No");
//
//        /*Multi Delivery Enabled*/
//        storeData.put(Utils.ENABLE_MULTI_DELIVERY_KEY, generalFunc.getJsonValue(Utils.ENABLE_MULTI_DELIVERY_KEY, userProfileJson));
//        storeData.put(Utils.ALLOW_MULTIPLE_DEST_ADD_KEY, generalFunc.getJsonValue(Utils.ALLOW_MULTIPLE_DEST_ADD_KEY, userProfileJson));
//        generalFunc.storeData(storeData);
        JSONObject userProfileJsonObj = generalFunc.getJsonObject(userProfileJson);
        new SetGeneralData(generalFunc, userProfileJsonObj);

    }

    public void setPromoCode() {
        if (appliedPromoCode.equalsIgnoreCase("")) {
            defaultPromoView();
        } else {
            appliedPromoView();
        }
    }

    private void placeOrder() {
        if (internetConnection.isNetworkConnected()) {


            if (SITE_TYPE.equalsIgnoreCase("Demo")) {

                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_NOTE_PLACE_ORDER_DEMO"), generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("", "LBL_CONTINUE_BTN"), buttonId -> {
                    if (buttonId == 1) {

                        if (audioRecording != null && recordingItem1 != null) {
                            OrdePalceWithAudio();
                        } else {
                            orderPlaceCall();
                        }
                    }
                });
            } else {
                if (audioRecording != null && recordingItem1 != null) {
                    OrdePalceWithAudio();
                } else {
                    orderPlaceCall();
                }
            }
        } else {
            generalFunc.showError();

        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActContext());
            int i = view.getId();
            // Tip Features Handing of views clicks Start
            if (i == R.id.tipAmountArea1) {
                setSelected(tipAmountText1, tipAmountArea1);
            }
            if (i == R.id.tipAmountArea2) {
                setSelected(tipAmountText2, tipAmountArea2);
            }
            if (i == R.id.tipAmountArea3) {
                setSelected(tipAmountText3, tipAmountArea3);
            }
            if (i == R.id.closeImg1) {
                removeSelectedTip(true);
            }
            if (i == R.id.closeImg2) {
                removeSelectedTip(true);
            }
            if (i == R.id.closeImg3) {
                removeSelectedTip(true);
            }
            if (i == R.id.closeImgOther) {
                removeSelectedTip(true);
            }
            if (i == R.id.tipAmountAreaOther) {
                setSelected(tipAmountOtherText, tipAmountAreaOther);
            }
            if (i == R.id.tipDescHintTitleText) {
                showTipInfoDialog(getActContext().getResources().getDrawable(R.drawable.ic_save_money), "LBL_DELIVERY_TIP_DESC");
            }
            if (i == R.id.iv_tip_help) {
                showTipInfoDialog(getActContext().getResources().getDrawable(R.drawable.ic_save_money), "LBL_DELIVERY_TIP_DESC");
            }
            if (i == R.id.tipGivenBtn) {
                tipAmount = "";

                final boolean tipAmountEntered = Utils.checkText(tipAmountBox) ? true : false;
                if (tipAmountEntered == false) {
                    amountArea.setBackground(getActContext().getResources().getDrawable(R.drawable.roundrecterrorwithdesign));
                    errorTxt.setText(generalFunc.retrieveLangLBl("", "LBL_REQUIRED"));
                    errorTxt.setVisibility(View.VISIBLE);
                    return;
                } else {
                    resetErrorTxt();

                }

                if (GeneralFunctions.parseDoubleValue(0, tipAmountBox.getText().toString()) > 0) {
                    resetErrorTxt();
                    setSelected(tipAmountOtherText, tipAmountAreaOther);

                } else {
                    amountArea.setBackground(getActContext().getResources().getDrawable(R.drawable.roundrecterrorwithdesign));
                    errorTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ENTER_GRATER_ZERO_VALUE_TXT"));
                    errorTxt.setVisibility(View.VISIBLE);

                }

            }
            // Tip Features Handing of views clicks end
            if (i == R.id.backImgView) {
                onBackPressed();
            } else if (i == submitBtnId) {
                if (generalFunc.getMemberId().equalsIgnoreCase("")) {
                    Bundle bn = new Bundle();
                    bn.putBoolean("isRestart", false);
                    new StartActProcess(getActContext()).startActForResult(LoginActivity.class, bn, RC_SIGN_IN_UP);
                } else {
                    getUserProfileJson();
                    if (ePaymentOption.equalsIgnoreCase("")) {

                        generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_CHOOSE_PAYMENT_METHOD"));
                        return;
                    }

                  /*  if (APP_PAYMENT_MODE.equalsIgnoreCase("Cash") && !checkboxWallet.isChecked() && casenote.getVisibility()==View.VISIBLE) {
                        generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_COD_NOT_AVAILABLE_TXT"));
                        return;
                    }
*/
                    if (!restaurantstatus.equalsIgnoreCase("") && restaurantstatus.equalsIgnoreCase("closed")) {
                        generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_RESTAURANTS_CLOSE_NOTE"));
                        return;
                    }


                    boolean isEmailBlankAndOptional = new AppFunctions(getActContext()).isEmailBlankAndOptional(generalFunc, generalFunc.getJsonValue("vEmail", userProfileJson));
                    if (generalFunc.getJsonValue("vPhone", userProfileJson).equals("") || (generalFunc.getJsonValue("vEmail", userProfileJson).equals("") && !isEmailBlankAndOptional)) {
                        //open account verification screen
                        if (generalFunc.getMemberId() != null && !generalFunc.getMemberId().equals("")) {
                            if (!generalFunc.getMemberId().equals("")) {
                                Bundle bn = new Bundle();
                                bn.putBoolean("isbackshow", true);
                                new StartActProcess(getActContext()).startActForResult(AccountverificationActivity.class, bn, Utils.SOCIAL_LOGIN_REQ_CODE);
                                return;
                            }
                        }
                    }


                    String ePhoneVerified = generalFunc.getJsonValue("ePhoneVerified", userProfileJson);
                    if (!ePhoneVerified.equalsIgnoreCase("Yes")) {
                        notifyVerifyMobile();
                        return;
                    }

                    if (AddAddressArea.getVisibility() == View.VISIBLE) {
                        generalFunc.showMessage(btn_type2, generalFunc.retrieveLangLBl("", "LBL_SELECT_ADDRESS_TITLE_TXT"));
                        return;
                    }


                    Bundle bn = new Bundle();
                    //  new StartActProcess(getActContext()).startActForResult(BusinessSelectPaymentActivity.class, bn, Utils.SELECT_ORGANIZATION_PAYMENT_CODE);
                    bn.putString("isWallet", isWalletSelect);
                    bn.putBoolean("isCash", ePaymentOption.equals("Cash") ? true : false);

                    boolean isDefaultSelected = false;
                    for (int i1 = 0; i1 < currentSelectedItems.size(); i1++) {
                        if (currentSelectedItems.get(i1).get("eContactLess").equalsIgnoreCase("Yes")) {
                            isDefaultSelected = true;
                            break;
                        }
                    }
                    if (isPreference && isDefaultSelected) {
                        bn.putBoolean("isCODAllow", false);
                        bn.putBoolean("isPreference", true);
                    } else {
                        bn.putBoolean("isCODAllow", isCODAllow);
                    }
                    bn.putBoolean("isCODAllow", isCODAllow);
                    bn.putString("iServiceId", generalFunc.getServiceId());
                    bn.putString("isCheckout", "");
                    bn.putString("outStandingAmount", outStandingAmount);
                    bn.putString("DisplayCardPayment", SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1") ? DisplayCardPayment : "");
                   /* if (generalFunc.isDeliverOnlyEnabled()) {
                        selectedMethod = "Instant";
                    } else {*/
                    selectedMethod = "Manual";
//                    }
                    bn.putString("selectedMethod", selectedMethod);
                    bn.putString("eTakeAway", eTakeAway);
                    String COMPANY_MINIMUM_ORDER = generalFunc.retrieveValue(Utils.COMPANY_MINIMUM_ORDER);
                    if (COMPANY_MINIMUM_ORDER != null && !COMPANY_MINIMUM_ORDER.equalsIgnoreCase("0")) {
                        double minimumAmt = GeneralFunctions.parseDoubleValue(0, COMPANY_MINIMUM_ORDER);
                        Cart cartData = cartList.get(0);
                        CurrencySymbol = cartData.getCurrencySymbol();
                        if (finalTotal < minimumAmt) {

                            generalFunc.showMessage(backImgView, LBL_MINIMUM_ORDER_NOTE + " " + CurrencySymbol + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(GeneralFunctions.parseDoubleValue(0, COMPANY_MINIMUM_ORDER))));
                            return;
                        }
                    }


                    //manage age proof
                    if (eProofUpload.equalsIgnoreCase("Yes")) {
                        //  clearMediaPlayer();
                        pauseMediaPlayer();
                        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (Environment.isExternalStorageManager()) {
                                if (generalFunc.isCameraPermissionGranted()) {
                                    takeAndUploadPic(getActContext());
                                } else {
                                    generalFunc.showMessage(getCurrView(), "Allow this app to use camera.");
                                }
                            } else {
                                try {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                    intent.addCategory("android.intent.category.DEFAULT");
                                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                                    startActivityForResult(intent, MANAGE_APP_ALL_FILES_ACCESS_REQUEST_CODE);
                                } catch (Exception e) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                    startActivityForResult(intent, MANAGE_APP_ALL_FILES_ACCESS_REQUEST_CODE);
                                }
                            }
                        } else {
                            takeAndUploadPic(getActContext());
                        }*/
                        // TODO: 28-04-2021 / Viral | Android 11 (30) Store permission code remove | revert 29
                        takeAndUploadPic(getActContext());

                        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_NEXT_TXT"));

                    } else {
                        //  clearMediaPlayer();
                        pauseMediaPlayer();
                        new StartActProcess(getActContext()).startActForResult(ProfilePaymentActivity.class, bn, Utils.SELECT_ORGANIZATION_PAYMENT_CODE);
                    }

//                    if (ePaymentOption.equalsIgnoreCase("card") && !generalFunc.isDeliverOnlyEnabled() && SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
//                        if (!Utils.checkText(selectedMethod)) {
//                            generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("Choose payment method for card payment", "LBL_CHOOSE_PAY_ONLINE_METHOD"));
//                            return;
//
//                        }
//
//                        if (Utils.checkText(selectedMethod) && selectedMethod.equalsIgnoreCase("Manual") && cardNumTxt.getVisibility() == View.GONE) {
//                            useExistingCardLL.performClick();
//                            return;
//                        }
//
//                        if (selectedMethod.equalsIgnoreCase("Manual") && cardNumTxt.getVisibility() == View.VISIBLE && isCardValidated == false) {
//                            checkPaymentCard(isWalletSelect, true);
//                            return;
//                        }
//
//                        placeOrder();
//                    } else {
//                        placeOrder();
//                    }


                }
            } else if (i == editCartImageview.getId()) {
                if (isFromEditCard) {
                    onBackPressed();
                } else {
                    new StartActProcess(getActContext()).startAct(EditCartActivity.class);
                    finish();
                }
            } else if (i == couponCodeArea.getId()) {
                if (generalFunc.getMemberId().equalsIgnoreCase("")) {
                    showMessageWithAction(applyCouponHTxt, generalFunc.retrieveLangLBl("", "LBL_PROMO_CODE_LOGIN_HINT"));
//                    generalFunc.showMessage(applyCouponHTxt, generalFunc.retrieveLangLBl("", "LBL_PROMO_CODE_LOGIN_HINT"));
                } else {
                    Bundle bn = new Bundle();
                    bn.putString("CouponCode", appliedPromoCode);
                    bn.putString("eSystem", Utils.eSystem_Type);
                    bn.putString("iCompanyId", cartList.get(0).getiCompanyId());

                    HashMap<String, String> data = new HashMap<>();
                    data.put(Utils.SELECT_ADDRESS_ID, "");
                    data.put(Utils.SELECT_LATITUDE, "");
                    data.put(Utils.SELECT_LONGITUDE, "");
                    data = generalFunc.retrieveValue(data);

                    bn.putString("iUserAddressId", data.get(Utils.SELECT_ADDRESS_ID));

                    bn.putString("vSourceLatitude", data.get(Utils.SELECT_LATITUDE));
                    bn.putString("vSourceLongitude", data.get(Utils.SELECT_LONGITUDE));

                    bn.putString("vDestLatitude", data.get(Utils.SELECT_LATITUDE));
                    bn.putString("vDestLongitude", data.get(Utils.SELECT_LONGITUDE));

                    bn.putString("eTakeAway", eTakeAway);
                    new StartActProcess(getActContext()).startActForResult(CouponActivity.class, bn, Utils.SELECT_COUPON_REQ_CODE);
                }
            } else if (i == changeAddresssTxt.getId()) {

                Bundle bn = new Bundle();
                bn.putString("iCompanyId", cartList.get(0).getiCompanyId());
                bn.putString("addressid", generalFunc.retrieveValue(Utils.SELECT_ADDRESS_ID));
                bn.putString("eSystem", Utils.eSystem_Type);
                new StartActProcess(getActContext()).startActForResult(ListAddressActivity.class, bn, SEL_ADDRESS);
            } else if (i == R.id.addAddressBtn) {
                Bundle bn = new Bundle();
                bn.putString("iCompanyId", cartList.get(0).getiCompanyId());
                new StartActProcess(getActContext()).startActForResult(AddAddressActivity.class, bn, ADD_ADDRESS);
            } else if (i == useExistingCardLL.getId()) {

                selectedMethod = "Manual";

                if (checkCardConfig(isWalletSelect, false, true)) {
                    existingPaymentView(true);
                } else {
                    defaultPaymentView(true);
                }

            } else if (i == instantPaymentLL.getId()) {
                selectedMethod = "Instant";
                defaultPaymentView(true);
            } else if (i == R.id.editcard_area) {
                OpenCardPaymentAct(isWalletSelect);
            } else if (i == R.id.couponCodeCloseImgView) {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_DELETE_CONFIRM_COUPON_MSG"), generalFunc.retrieveLangLBl("", "LBL_NO"), generalFunc.retrieveLangLBl("", "LBL_YES"), buttonId -> {
                    if (buttonId == 1) {
                        appliedPromoCode = "";
                        CheckOutOrderEstimateDetails("", false);
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_COUPON_REMOVE_SUCCESS"));
                    }
                });

            } else if (i == btn_clearcart.getId()) {

                Realm realm = MyApp.getRealmInstance();
                generalFunc.removeAllRealmData(realm);
                Bundle bn = new Bundle();
                bn.putBoolean("isRestart", true);
                new StartActProcess(getActContext()).startActWithData(EditCartActivity.class, bn);

            } else if (i == rightImgView.getId()) {
                Bundle bn = new Bundle();
                bn.putBoolean("isBack", true);
                // new StartActProcess(mContext).startActForResult(MyProfileActivity.class, bn, Utils.MY_PROFILE_REQ_CODE);
                new StartActProcess(getActContext()).startActWithData(PrescriptionActivity.class, bn);
            }
        }

    }

    ///  Tip Feature - Functions Start
    private void removeSelectedTip(boolean isCallFareEstimate) {
        tipAmount = "";
        tipAmountBox.setText("");
        tipAmountArea1.setBackground(getActContext().getResources().getDrawable(R.drawable.button_normal_background));
        tipAmountArea2.setBackground(getActContext().getResources().getDrawable(R.drawable.button_normal_background));
        tipAmountArea3.setBackground(getActContext().getResources().getDrawable(R.drawable.button_normal_background));
        tipAmountAreaOther.setBackground(getActContext().getResources().getDrawable(R.drawable.button_normal_background));
        closeImg1.setVisibility(View.GONE);
        closeImg2.setVisibility(View.GONE);
        closeImg3.setVisibility(View.GONE);
        closeImgOther.setVisibility(View.GONE);
        if (!isPercentage) {
            tipAmountOtherText.setTag("");
        }
        tipAmountOtherText.setText(generalFunc.retrieveLangLBl("", "LBL_OTHER_TXT"));
        addTipArea.setVisibility(View.GONE);
        resetErrorTxt();
        if (isCallFareEstimate)
            CheckOutOrderEstimateDetails(appliedPromoCode, false);
    }

    private void setSelected(MTextView selectedTextAreaTxtView, LinearLayout selectedArea) {
        tipAmount = selectedTextAreaTxtView.getTag().toString();
        addTipArea.setVisibility(selectedArea == tipAmountAreaOther ? View.VISIBLE : View.GONE);
        resetErrorTxt();
        errorTxt.setVisibility(selectedArea == tipAmountAreaOther ? View.VISIBLE : View.GONE);
        tipAmountArea1.setBackground(getActContext().getResources().getDrawable(selectedArea == tipAmountArea1 ? R.drawable.button_background : R.drawable.button_normal_background));
        closeImg1.setVisibility(selectedArea == tipAmountArea1 ? View.VISIBLE : View.GONE);
        tipAmountArea2.setBackground(getActContext().getResources().getDrawable(selectedArea == tipAmountArea2 ? R.drawable.button_background : R.drawable.button_normal_background));
        closeImg2.setVisibility(selectedArea == tipAmountArea2 ? View.VISIBLE : View.GONE);
        tipAmountArea3.setBackground(getActContext().getResources().getDrawable(selectedArea == tipAmountArea3 ? R.drawable.button_background : R.drawable.button_normal_background));
        closeImg3.setVisibility(selectedArea == tipAmountArea3 ? View.VISIBLE : View.GONE);
        tipAmountAreaOther.setBackground(getActContext().getResources().getDrawable(selectedArea == tipAmountAreaOther ? R.drawable.button_background : R.drawable.button_normal_background));
        closeImgOther.setVisibility(selectedArea == tipAmountAreaOther && Utils.checkText(Utils.getText(tipAmountBox)) ? View.VISIBLE : View.GONE);


        if (selectedArea == tipAmountAreaOther && Utils.checkText(Utils.getText(tipAmountBox))) {
            if (!isPercentage) {
                tipAmountOtherText.setTag(Utils.getText(tipAmountBox));
                tipAmount = Utils.getText(tipAmountBox);

            }
            tipAmountOtherText.setText(currencySymbol + Utils.getText(tipAmountBox));
            closeImgOther.setVisibility(View.VISIBLE);
            addTipArea.setVisibility(View.GONE);
            resetErrorTxt();
            CheckOutOrderEstimateDetails(appliedPromoCode, false);
        } else if (selectedArea != tipAmountAreaOther) {
            if (!isPercentage) {
                tipAmountOtherText.setTag("");
            }

            tipAmountBox.setText("");
            tipAmountOtherText.setText(generalFunc.retrieveLangLBl("", "LBL_OTHER_TXT"));
            CheckOutOrderEstimateDetails(appliedPromoCode, false);
        } else if ((selectedArea == tipAmountAreaOther && !Utils.checkText(Utils.getText(tipAmountBox))) && Utils.checkText(tipAmount)) {
            if (!isPercentage) {
                tipAmountOtherText.setTag("");
            }
            tipAmountBox.setText("");
            tipAmount = "";
            tipAmountOtherText.setText(generalFunc.retrieveLangLBl("", "LBL_OTHER_TXT"));
            CheckOutOrderEstimateDetails(appliedPromoCode, false);
        } else {
            tipAmountBox.setText("");
            if (!isPercentage) {
                tipAmountOtherText.setTag("");
            }
            tipAmountOtherText.setText(generalFunc.retrieveLangLBl("", "LBL_OTHER_TXT"));
        }
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
        titileTxt.setText(generalFunc.retrieveLangLBl("Tip your delivery partner", "LBL_TIP_DIALOG_TITLE"));
        okTxt.setText(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
        msgTxt.setText("" + generalFunc.retrieveLangLBl("You can set a delivery preference before the delivery agent attempts to deliver your package at your door. Your preferences will be taken care by delivery driver.", LBL_DATA));
        msgTxt.setMovementMethod(new ScrollingMovementMethod());
        okTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveTipAlertDialog.dismiss();
            }
        });
        giveTipAlertDialog = builder.create();
        giveTipAlertDialog.setCancelable(true);
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(giveTipAlertDialog);
        }
        giveTipAlertDialog.getWindow().setBackgroundDrawable(getActContext().getResources().getDrawable(R.drawable.all_roundcurve_card));
        giveTipAlertDialog.show();


    }
///  Tip Feature - Functions End

    Snackbar snackbar = null;

    public void showMessageWithAction(View view, String message) {


        snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_INDEFINITE).setAction(generalFunc.retrieveLangLBl("Dismiss", "LBL_dismiss"), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();

                    }
                });
        snackbar.setActionTextColor(getActContext().getResources().getColor(R.color.verfiybtncolor));
        snackbar.setDuration(10000);
        snackbar.show();

    }

    androidx.appcompat.app.AlertDialog giveTipAlertDialog;

    private void showPreferenceHelp(Drawable drawable, String LBL_DATA, String title) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActContext());
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.desgin_preference_help, null);
        builder.setView(dialogView);


        final ImageView iamage_source = (ImageView) dialogView.findViewById(R.id.iamage_source);
        if (drawable != null) {
            iamage_source.setImageDrawable(drawable);
        }
        final MTextView okTxt = (MTextView) dialogView.findViewById(R.id.okTxt);
        final MTextView titileTxt = (MTextView) dialogView.findViewById(R.id.titileTxt);
        final WebView msgTxt = (WebView) dialogView.findViewById(R.id.msgTxt);
        titileTxt.setText(generalFunc.retrieveLangLBl("User Preferences", "LBL_USER_PREF"));
        if (!title.equalsIgnoreCase("")) {
            titileTxt.setText(title);
        }
        okTxt.setText(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT "));
        // msgTxt.setText("" + generalFunc.retrieveLangLBl("You can set a delivery preference before the delivery agent attempts to deliver your package at your door. Your preferences will be taken care by delivery driver.", LBL_DATA));
        //  msgTxt.setMovementMethod(new ScrollingMovementMethod());

        msgTxt.loadDataWithBaseURL(null, generalFunc.wrapHtml(getActContext(), generalFunc.retrieveLangLBl("You can set a delivery preference before the delivery agent attempts to deliver your package at your door. Your preferences will be taken care by delivery driver.", LBL_DATA)), "text/html", "UTF-8", null);

        okTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveTipAlertDialog.dismiss();
            }
        });
        giveTipAlertDialog = builder.create();
        giveTipAlertDialog.setCancelable(true);
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(giveTipAlertDialog);
        }
        giveTipAlertDialog.getWindow().setBackgroundDrawable(getActContext().getResources().getDrawable(R.drawable.all_roundcurve_card));
        giveTipAlertDialog.show();


    }


    public void takeAndUploadPic(final Context mContext) {
        /*boolean isStoragePermissionAvail = false;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                if (generalFunc.isCameraPermissionGranted()) {
                    isStoragePermissionAvail = true;
                }
            }
        } else {
            isStoragePermissionAvail = generalFunc.isCameraStoragePermissionGranted();
        }*/
        // TODO: 28-04-2021 / Viral | Android 11 (30) Store permission code remove | revert 29
        boolean isStoragePermissionAvail = generalFunc.isCameraStoragePermissionGranted();
        if (!isStoragePermissionAvail) {
            return;
        }
        selectedImagePath = "";

        uploadServicePicAlertBox = new Dialog(mContext, R.style.Theme_Dialog);
        uploadServicePicAlertBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
        uploadServicePicAlertBox.setCancelable(true);
        uploadServicePicAlertBox.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        uploadServicePicAlertBox.setContentView(R.layout.design_upload_service_pic);

        MTextView titleTxt = (MTextView) uploadServicePicAlertBox.findViewById(R.id.titleTxt);
        final MTextView uploadStatusTxt = (MTextView) uploadServicePicAlertBox.findViewById(R.id.uploadStatusTxt);
        MTextView uploadTitleTxt = (MTextView) uploadServicePicAlertBox.findViewById(R.id.uploadTitleTxt);
        ImageView backImgView = (ImageView) uploadServicePicAlertBox.findViewById(R.id.backImgView);

        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
        MTextView skipTxt = (MTextView) uploadServicePicAlertBox.findViewById(R.id.skipTxt);
        MTextView noteTxt = (MTextView) uploadServicePicAlertBox.findViewById(R.id.noteTxt);
        MTextView noteLbl = (MTextView) uploadServicePicAlertBox.findViewById(R.id.noteLbl);
        final ImageView uploadImgVIew = (ImageView) uploadServicePicAlertBox.findViewById(R.id.uploadImgVIew);
        LinearLayout uploadImgArea = (LinearLayout) uploadServicePicAlertBox.findViewById(R.id.uploadImgArea);
        MButton btn_type2 = ((MaterialRippleLayout) uploadServicePicAlertBox.findViewById(R.id.btn_type2)).getChildView();

        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_IDENTIFICATION"));
        skipTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SKIP_TXT"));
        skipTxt.setVisibility(View.GONE);

        noteLbl.setText(generalFunc.retrieveLangLBl("", "LBL_NOTE"));
        noteTxt.setText(tProofNote);


        uploadTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SELECT_ID_IMAGE"));
        btn_type2.setText(generalFunc.retrieveLangLBl("Save Photo", "LBL_BTN_NEXT_TXT"));


        btn_type2.setId(Utils.generateViewId());
        btn_type2.setTextSize(16);
        uploadImgArea.setOnClickListener(view -> {

            if (generalFunc.isCameraPermissionGranted()) {
                uploadServicePicAlertBox.findViewById(R.id.uploadStatusTxt).setVisibility(View.GONE);
                new ImageSourceDialog().run();
            } else {
                uploadStatusTxt.setVisibility(View.VISIBLE);
                generalFunc.showMessage(uploadStatusTxt, "Allow this app to use camera.");
            }
        });
        btn_type2.setOnClickListener(view -> {

            if (TextUtils.isEmpty(selectedImagePath)) {
                //   uploadStatusTxt.setVisibility(View.VISIBLE);
                generalFunc.showMessage(uploadStatusTxt, generalFunc.retrieveLangLBl("Please select image", "LBL_PLEASE_SELECT_IMAGE"));


            } else {
                uploadProof();
                uploadStatusTxt.setVisibility(View.GONE);
            }
        });

        skipTxt.setOnClickListener(view -> {


            selectedImagePath = "";
            uploadImgVIew.setImageURI(null);

        });

        backImgView.setOnClickListener(v -> {
            closeuploadServicePicAlertBox();

        });

        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(uploadServicePicAlertBox);
        }

        uploadServicePicAlertBox.show();

    }

    public void closeuploadServicePicAlertBox() {
        if (uploadServicePicAlertBox != null) {
            uploadServicePicAlertBox.dismiss();
        }
    }

    class ImageSourceDialog implements Runnable {

        @Override
        public void run() {
            final Dialog dialog_img_update = new Dialog(getActContext(), R.style.ImageSourceDialogStyle);
            dialog_img_update.setContentView(R.layout.design_image_source_select);

            MTextView chooseImgHTxt = (MTextView) dialog_img_update.findViewById(R.id.chooseImgHTxt);
            MTextView cameraTxt = (MTextView) dialog_img_update.findViewById(R.id.cameraTxt);
            MTextView galleryTxt = (MTextView) dialog_img_update.findViewById(R.id.galleryTxt);
            chooseImgHTxt.setText(generalFunc.retrieveLangLBl("Choose option", "LBL_CHOOSE_OPTION"));
            SelectableRoundedImageView cameraIconImgView = (SelectableRoundedImageView) dialog_img_update.findViewById(R.id.cameraIconImgView);
            SelectableRoundedImageView galleryIconImgView = (SelectableRoundedImageView) dialog_img_update.findViewById(R.id.galleryIconImgView);
            ImageView closeDialogImgView = (ImageView) dialog_img_update.findViewById(R.id.closeDialogImgView);

            MButton btn_type2 = ((MaterialRippleLayout) dialog_img_update.findViewById(R.id.btn_type2)).getChildView();
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));

            cameraTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CAMERA"));
            galleryTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GALLERY"));

            btn_type2.setOnClickListener(v -> {


                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                }
            });

            //  int backColor = getResources().getColor(R.color.appThemeColor_Dark_1);
            //  int strokeColor = Color.parseColor("#00000000");
            //  int colorFilter = getResources().getColor(R.color.appThemeColor_TXT_1);
            // int radius = Utils.dipToPixels(getActContext(), 25);

            // new CreateRoundedView(backColor, radius, 0, strokeColor, cameraIconImgView);
            //  cameraIconImgView.setColorFilter(colorFilter);
            // new CreateRoundedView(backColor, radius, 0, strokeColor, galleryIconImgView);
            //  galleryIconImgView.setColorFilter(colorFilter);

            cameraIconImgView.setOnClickListener(v -> {
                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                }
                if (!isDeviceSupportCamera()) {
                    generalFunc.showMessage(generalFunc.getCurrentView(CheckOutActivity.this), generalFunc.retrieveLangLBl("", "LBL_NOT_SUPPORT_CAMERA_TXT"));
                } else {
                    chooseFromCamera();
                }
            });

            galleryIconImgView.setOnClickListener(v -> {
                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                }
                chooseFromGallery();
            });
            dialog_img_update.setCanceledOnTouchOutside(true);
            Window window = dialog_img_update.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog_img_update.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if (generalFunc.isRTLmode()) {
                dialog_img_update.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            dialog_img_update.show();
          /*  dialog_img_update.setCanceledOnTouchOutside(true);
            Window window = dialog_img_update.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog_img_update.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog_img_update.show();*/
        }
    }


    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void chooseFromCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {
        return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            pathForCameraImage = mediaFile.getAbsolutePath();
        } else {
            return null;
        }

        return mediaFile;
    }

    public void chooseFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void uploadProof() {
        ArrayList<String[]> paramsList = new ArrayList<>();
        paramsList.add(generalFunc.generateImageParams("type", "UploadIdProof"));
        paramsList.add(generalFunc.generateImageParams("UserType", Utils.app_type));
        paramsList.add(Utils.generateImageParams("iMemberId", generalFunc.getMemberId()));
        paramsList.add(Utils.generateImageParams("iUserId", generalFunc.getMemberId()));
        paramsList.add(Utils.generateImageParams("MemberType", Utils.app_type));
        paramsList.add(Utils.generateImageParams("tSessionId", generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY)));
        paramsList.add(Utils.generateImageParams("GeneralUserType", Utils.app_type));
        paramsList.add(Utils.generateImageParams("GeneralMemberId", generalFunc.getMemberId()));
        paramsList.add((Utils.generateImageParams("eSystem", Utils.eSystem_Type)));

        new UploadProfileImage(CheckOutActivity.this, selectedImagePath, Utils.TempProfileImageName, paramsList).execute();

    }

    public void handleImgUploadResponse(String responseString, boolean isAudio) {

        if (responseString != null && !responseString.equals("")) {

            Logger.d("handleImgUploadResponse", "::" + responseString);
            if (isAudio) {
                iVoiceDirectionFileId = generalFunc.getJsonValue("iVoiceDirectionFileId", responseString);
                orderPlaceCall();
                return;
            }
            iIdProofImageId = generalFunc.getJsonValue("iIdProofImageId", responseString).toString();

            Bundle bn = new Bundle();
            //  new StartActProcess(getActContext()).startActForResult(BusinessSelectPaymentActivity.class, bn, Utils.SELECT_ORGANIZATION_PAYMENT_CODE);
            bn.putString("isWallet", isWalletSelect);
            bn.putBoolean("isCash", ePaymentOption.equals("Cash") ? true : false);

            boolean isDefaultSelected = false;
            for (int i1 = 0; i1 < currentSelectedItems.size(); i1++) {
                if (currentSelectedItems.get(i1).get("eContactLess").equalsIgnoreCase("Yes")) {
                    isDefaultSelected = true;
                    break;
                }
            }
            if (isPreference && isDefaultSelected) {
                bn.putBoolean("isCODAllow", false);
                bn.putBoolean("isPreference", true);
            } else {
                bn.putBoolean("isCODAllow", isCODAllow);
            }
            bn.putBoolean("isCODAllow", isCODAllow);
            bn.putString("iServiceId", generalFunc.getServiceId());
            bn.putString("isCheckout", "");
            bn.putString("outStandingAmount", outStandingAmount);
            bn.putString("DisplayCardPayment", SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1") ? DisplayCardPayment : "");
                   /* if (generalFunc.isDeliverOnlyEnabled()) {
                        selectedMethod = "Instant";
                    } else {*/
            selectedMethod = "Manual";
//                    }
            bn.putString("selectedMethod", selectedMethod);
            bn.putString("eTakeAway", eTakeAway);
            String COMPANY_MINIMUM_ORDER = generalFunc.retrieveValue(Utils.COMPANY_MINIMUM_ORDER);
            if (COMPANY_MINIMUM_ORDER != null && !COMPANY_MINIMUM_ORDER.equalsIgnoreCase("0")) {
                double minimumAmt = GeneralFunctions.parseDoubleValue(0, COMPANY_MINIMUM_ORDER);
                Cart cartData = cartList.get(0);
                CurrencySymbol = cartData.getCurrencySymbol();
                if (finalTotal < minimumAmt) {

                    generalFunc.showMessage(backImgView, LBL_MINIMUM_ORDER_NOTE + " " + CurrencySymbol + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(GeneralFunctions.parseDoubleValue(0, COMPANY_MINIMUM_ORDER))));
                    return;
                }
            }

            //   clearMediaPlayer();
            new StartActProcess(getActContext()).startActForResult(ProfilePaymentActivity.class, bn, Utils.SELECT_ORGANIZATION_PAYMENT_CODE);
        } else {
            generalFunc.showError();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (audioRecording.mMediaPlayer != null && audioRecording.mMediaPlayer.isPlaying()) {
            clearMediaPlayer();
        }

    }
}
