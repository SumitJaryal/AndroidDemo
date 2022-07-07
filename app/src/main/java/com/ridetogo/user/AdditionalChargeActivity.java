package com.ridetogo.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.general.files.AppFunctions;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONObject;

import java.util.HashMap;

public class AdditionalChargeActivity extends BaseActivity {

    MTextView titleTxt;
    ImageView backImgView;
    LinearLayout confirmationCodeArea;
    LinearLayout btnArea;
    MTextView matrialfeeHTxt, miscfeeHTxt, discountHTxt, matrialfeeSTxt, miscfeeSTxt, discountSTxt;
    MTextView verificationCodeTxt;
    MTextView verificationCodeVTxt;
    MTextView finalvalTxt, finalHTxt, currentchargeHTxt, currentchargeVTxt, noteLbl, noteTxt;
    MTextView tollvalTxt, tollHTxt;
    MTextView  otherAmountHTxt, otherAmountVTxt;
    LinearLayout  discountArea,miscFeelayout,materialFeeLayout,serviceCostShowArea,otherFeelayout,tollFeelayout;
    MTextView timatrialfeeVTxt, miscfeeVTxt, discountVTxt;
    MTextView matrialfeeCurrancyTxt, miscfeeCurrancyTxt, discountCurrancyTxt;

    GeneralFunctions generalFunc;
    MButton submitBtn;
    MButton skipBtn;
    public HashMap<String, String> tripDetail = new HashMap<>();
    boolean isTollOrOtherCharges=false;
    private JSONObject userProfileJsonObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_charge);
        tripDetail = (HashMap<String, String>) getIntent().getSerializableExtra("TripDetail");

        initViews();
    }

    public Context getActContext() {
        return AdditionalChargeActivity.this;
    }


    public void initViews() {
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        userProfileJsonObj = generalFunc.getJsonObject(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));

        backImgView = (ImageView) findViewById(R.id.backImgView);
        confirmationCodeArea = (LinearLayout) findViewById(R.id.confirmationCodeArea);
        btnArea = (LinearLayout) findViewById(R.id.btnArea);
        backImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titleTxt = (MTextView) findViewById(R.id.titleTxt);

        verificationCodeTxt = (MTextView) findViewById(R.id.verificationCodeTxt);
        verificationCodeVTxt = (MTextView) findViewById(R.id.verificationCodeVTxt);
        matrialfeeHTxt = (MTextView) findViewById(R.id.matrialfeeHTxt);
        miscfeeHTxt = (MTextView) findViewById(R.id.miscfeeHTxt);
        discountHTxt = (MTextView) findViewById(R.id.discountHTxt);

        matrialfeeSTxt = (MTextView) findViewById(R.id.matrialfeeSTxt);
        miscfeeSTxt = (MTextView) findViewById(R.id.miscfeeSTxt);
        discountSTxt = (MTextView) findViewById(R.id.discountSTxt);

        finalvalTxt = (MTextView) findViewById(R.id.finalvalTxt);
        finalHTxt = (MTextView) findViewById(R.id.finalHTxt);


        tollFeelayout = (LinearLayout) findViewById(R.id.tollFeelayout);
        tollHTxt = (MTextView) findViewById(R.id.tollHTxt);
        tollvalTxt = (MTextView) findViewById(R.id.tollvalTxt);

        serviceCostShowArea = (LinearLayout) findViewById(R.id.serviceCostShowArea);
        materialFeeLayout = (LinearLayout) findViewById(R.id.materialFeeLayout);
        miscFeelayout = (LinearLayout) findViewById(R.id.miscFeelayout);
        discountArea = (LinearLayout) findViewById(R.id.discountArea);

        otherFeelayout = (LinearLayout) findViewById(R.id.otherFeelayout);
        otherAmountHTxt = (MTextView) findViewById(R.id.otherAmountHTxt);
        otherAmountVTxt = (MTextView) findViewById(R.id.otherAmountVTxt);

        currentchargeHTxt = (MTextView) findViewById(R.id.currentchargeHTxt);
        currentchargeVTxt = (MTextView) findViewById(R.id.currentchargeVTxt);

        noteLbl = (MTextView) findViewById(R.id.noteLbl);
        noteTxt = (MTextView) findViewById(R.id.noteTxt);

        matrialfeeCurrancyTxt = (MTextView) findViewById(R.id.matrialfeeCurrancyTxt);
        miscfeeCurrancyTxt = (MTextView) findViewById(R.id.miscfeeCurrancyTxt);
        discountCurrancyTxt = (MTextView) findViewById(R.id.discountCurrancyTxt);

        submitBtn = ((MaterialRippleLayout) findViewById(R.id.submitBtn)).getChildView();
        skipBtn = ((MaterialRippleLayout) findViewById(R.id.skipBtn)).getChildView();



        submitBtn.setId(Utils.generateViewId());
        skipBtn.setId(Utils.generateViewId());


        submitBtn.setOnClickListener(new setOnClickList());
        skipBtn.setOnClickListener(new setOnClickList());



        timatrialfeeVTxt = (MTextView) findViewById(R.id.timatrialfeeVTxt);
        miscfeeVTxt = (MTextView) findViewById(R.id.miscfeeVTxt);
        discountVTxt = (MTextView) findViewById(R.id.discountVTxt);

        setLabel();

        String fMaterialFee = getIntent().getStringExtra("fMaterialFee");
        String isFromToll = getIntent().getStringExtra("isFromToll");
        if (Utils.checkText(isFromToll)){
            backImgView.setVisibility(View.GONE);
        }
        String fMiscFee = getIntent().getStringExtra("fMiscFee");
        String fDriverDiscount = getIntent().getStringExtra("fDriverDiscount");
        String serviceCost = getIntent().getStringExtra("serviceCost");
        String fOtherAmount = getIntent().getStringExtra("fOtherCharges");
        String fTollAmount = getIntent().getStringExtra("fTollPrice");
        String totalAmount = getIntent().getStringExtra("totalAmount");
        String vConfirmationCode = getIntent().getStringExtra("vConfirmationCode");
        String CurrencySymbol = Utils.checkText(getIntent().getStringExtra("CurrencySymbol"))?getIntent().getStringExtra("CurrencySymbol"): generalFunc.getJsonValueStr("CurrencySymbol",userProfileJsonObj);
        verificationCodeVTxt.setText("" + vConfirmationCode);

        timatrialfeeVTxt.setText(Utils.checkText(fMaterialFee) ? ""+new AppFunctions(getActContext()).formatNumAsPerCurrency(generalFunc,fMaterialFee.replace(CurrencySymbol,""),CurrencySymbol,true):"");
        miscfeeVTxt.setText(Utils.checkText(fMiscFee) ?"" + new AppFunctions(getActContext()).formatNumAsPerCurrency(generalFunc,fMiscFee.replace(CurrencySymbol,""),CurrencySymbol,true):"");
        discountVTxt.setText(Utils.checkText(fDriverDiscount) ? "" + new AppFunctions(getActContext()).formatNumAsPerCurrency(generalFunc,fDriverDiscount.replace(CurrencySymbol,"")
                ,CurrencySymbol,true):"");
        currentchargeVTxt.setText(Utils.checkText(serviceCost) ? "" + new AppFunctions(getActContext()).formatNumAsPerCurrency(generalFunc,serviceCost.replace(CurrencySymbol,""),CurrencySymbol,true):"");
        finalvalTxt.setText(Utils.checkText(totalAmount) ? "" + new AppFunctions(getActContext()).formatNumAsPerCurrency(generalFunc,totalAmount.replace(CurrencySymbol,""),CurrencySymbol,true):"");

        String eApproveRequestSentByDriver=getIntent().getStringExtra("eApproveRequestSentByDriver");
        if (Utils.checkText(eApproveRequestSentByDriver) && eApproveRequestSentByDriver.equalsIgnoreCase("Yes") && !Utils.checkText(vConfirmationCode))
        {
            btnArea.setVisibility(View.VISIBLE);
            confirmationCodeArea.setVisibility(View.GONE);
            noteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_APPROVE_DECLINE_CHARGES_BY_USER_TXT"));

        }else
        {
            btnArea.setVisibility(View.GONE);
            confirmationCodeArea.setVisibility(View.VISIBLE);
            noteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GIVE_CHARGES_CONFIRMATION_CODE_MSG"));

        }

        if (Utils.checkText(fTollAmount))
        {
            isTollOrOtherCharges=true;
            tollFeelayout.setVisibility(View.VISIBLE);
            serviceCostShowArea.setVisibility(View.GONE);
            materialFeeLayout.setVisibility(View.GONE);
            miscFeelayout.setVisibility(View.GONE);
            discountArea.setVisibility(View.GONE);
        }

        if (Utils.checkText(fOtherAmount))
        {
            isTollOrOtherCharges=true;
            otherFeelayout.setVisibility(View.VISIBLE);
            serviceCostShowArea.setVisibility(View.GONE);
            materialFeeLayout.setVisibility(View.GONE);
            miscFeelayout.setVisibility(View.GONE);
            discountArea.setVisibility(View.GONE);
        }

        finalHTxt.setText(isTollOrOtherCharges?generalFunc.retrieveLangLBl("Extra Charges", "LBL_TOTAL_EXTRA_CHARGES_TXT"):generalFunc.retrieveLangLBl("FINAL TOTAL", "LBL_FINAL_TOTAL_HINT"));

        otherAmountVTxt.setText(Utils.checkText(fOtherAmount) ? "" + new AppFunctions(getActContext()).formatNumAsPerCurrency(generalFunc,fOtherAmount.replace(CurrencySymbol,""),CurrencySymbol,true):"");
        tollvalTxt.setText(Utils.checkText(fTollAmount) ? "" + new AppFunctions(getActContext()).formatNumAsPerCurrency(generalFunc,fTollAmount.replace(CurrencySymbol,""),CurrencySymbol,true):"");
    }

    public void setLabel() {
        matrialfeeHTxt.setText(generalFunc.retrieveLangLBl("Material fee", "LBL_MATERIAL_FEE"));
        miscfeeHTxt.setText(generalFunc.retrieveLangLBl("Misc fee", "LBL_MISC_FEE"));
        discountHTxt.setText(generalFunc.retrieveLangLBl("Provider Discount", "LBL_PROVIDER_DISCOUNT"));
        finalHTxt.setText(generalFunc.retrieveLangLBl("FINAL TOTAL", "LBL_FINAL_TOTAL_HINT"));
        verificationCodeTxt.setText(generalFunc.retrieveLangLBl("Confirmation Code", "LBL_CONFIRMATION_CODE"));
        tollHTxt.setText(generalFunc.retrieveLangLBl("","LBL_TOLL_CHARGES"));
        otherAmountHTxt.setText(generalFunc.retrieveLangLBl("","LBL_OTHER_CHARGES"));
        currentchargeHTxt.setText(generalFunc.retrieveLangLBl("Service Cost", "LBL_SERVICE_COST"));
        noteLbl.setText(generalFunc.retrieveLangLBl("", "LBL_NOTE") + ":-");
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADDITIONAL_CHARGES"));
        submitBtn.setText(generalFunc.retrieveLangLBl("", "LBL_APPROVE_TXT"));
        skipBtn.setText(generalFunc.retrieveLangLBl("", "LBL_DECLINE_TXT"));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void pubNubMsgArrived(final String message, final Boolean ishow) {

        runOnUiThread(() -> {

            String msgType = generalFunc.getJsonValue("MsgType", message);
            String iDriverId = generalFunc.getJsonValue("iDriverId", message);
            String iTripId = generalFunc.getJsonValue("iTripId", message);

            if (tripDetail.get("iDriverId").equals(iDriverId)) {
              if (msgType.equals("VerifyCharges")) {
                   /* isVerifyCharges=true;
                    verifyPushMsg=message;*/
                  Intent returnIntent = new Intent();
                  setResult(Activity.RESULT_OK, returnIntent);
                  finish();
                } else {
                    onGcmMessageArrived(message, ishow);
                }


            }


        });
    }

    public void onGcmMessageArrived(final String message, boolean ishow) {

        String driverMsg = generalFunc.getJsonValue("Message", message);

        if (driverMsg.equals("VerifyCharges")) {
           /* isVerifyCharges=true;
            verifyPushMsg=message;*/
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else if (driverMsg.equals("TripEnd")) {


            GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
            alertBox.setContentMessage("", generalFunc.getJsonValue("vTitle", message));
            alertBox.setPositiveBtn(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));


            alertBox.setCancelable(false);
            alertBox.setBtnClickList(btn_id -> {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            });
            alertBox.showAlertBox();


        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == skipBtn.getId()) {
                sendChargeVerificationCode("No");
            }else if (i == submitBtn.getId()) {
                GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
                alertBox.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_PASSENGER_APPROVE_YES"));
                alertBox.setPositiveBtn(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
                alertBox.setCancelable(false);
                alertBox.setBtnClickList(btn_id -> {
                    sendChargeVerificationCode("Yes");
                });
                alertBox.showAlertBox();
            }
        }


    }

    private void sendChargeVerificationCode(String eApproveByUser) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "sendChargeVerificationCode");
        parameters.put("TripId", tripDetail.get("iTripId"));
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);
        parameters.put("eApproveByUser", eApproveByUser);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> verificationResponse(responseString,eApproveByUser));
        exeWebServer.execute();
    }


    private void verificationResponse(String responseString,String eApproveByUser) {

        if (responseString != null && !responseString.equals("")) {

            boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);
            String MSG_TYPE = generalFunc.getJsonValue("MSG_TYPE", responseString);
            generalFunc.showGeneralMessage("",
                    generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));

            if (isDataAvail == true) {
                if (isTollOrOtherCharges)
                {
                    if (eApproveByUser.equalsIgnoreCase("No") || MSG_TYPE.equalsIgnoreCase("DO_RESTART")) {
                        MyApp.getInstance().restartWithGetDataApp();
                    }
                }else {
                    backImgView.performClick();
                }
            }
        }
    }


}
