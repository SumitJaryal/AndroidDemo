package com.ridetogo.user.deliverAll;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.ridetogo.user.Help_MainCategory;
import com.ridetogo.user.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.google.gson.Gson;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;
import com.view.simpleratingbar.SimpleRatingBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FoodRatingActivity extends AppCompatActivity {

    ImageView backImgView;
    MaterialEditText res_commentBox, driver_commentBox;
    GeneralFunctions generalFun;
    MButton btn_type2;
    int submitBtnId;
    SimpleRatingBar ratingBar_res, ratingBar_driver;

    LinearLayout driverAreaRating, restaurantDriverRatingArea;
    private RelativeLayout driverFeedbackRatingArea;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_rating);

        generalFun = MyApp.getInstance().getGeneralFun(getActContext());

        driverFeedbackRatingArea = findViewById(R.id.driverFeedbackRatingArea);
        restaurantDriverRatingArea = findViewById(R.id.restaurantDriverRatingArea);

        backImgView = findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());
        MTextView titleTxt = findViewById(R.id.ordertitleTxt);
        titleTxt.setText(generalFun.retrieveLangLBl("", "LBL_RATING"));
        MTextView orderMsg = findViewById(R.id.orderMsg);
        orderMsg.setVisibility(View.VISIBLE);
        orderMsg.setText("(" + generalFun.retrieveLangLBl("", "LBL_ORDER") + " #" + getIntent().getStringExtra("vOrderNo") + ")");

        if (getIntent().getBooleanExtra("IS_NEW", false)) {
            driverFeedQuestionsInit();
        } else {
            restaurantDriverRatingInit();
        }

    }

    private void driverFeedQuestionsInit() {
        driverFeedbackRatingArea.setVisibility(View.VISIBLE);
        restaurantDriverRatingArea.setVisibility(View.GONE);

        LinearLayout llBannerArea = findViewById(R.id.llBannerArea);
        llBannerArea.setVisibility(View.VISIBLE);
        MTextView txtRateBy = findViewById(R.id.txtRateBy);
        txtRateBy.setText(generalFun.retrieveLangLBl("", "LBL_RATE_DELIVERY_BY"));

        MTextView providerNameTxt = findViewById(R.id.providerNameTxt);
        providerNameTxt.setText(getIntent().getStringExtra("driverName"));

        ImageView iv_info = findViewById(R.id.editCartImageview);
        iv_info.setImageDrawable(getActContext().getResources().getDrawable(R.drawable.ic_information));
        iv_info.setVisibility(View.VISIBLE);
        iv_info.setOnClickListener(v -> {
            Bundle bn = new Bundle();
            bn.putString("iOrderId", getIntent().getStringExtra("iOrderId"));
            new StartActProcess(getActContext()).startActWithData(Help_MainCategory.class, bn);
        });

        View disableArea = findViewById(R.id.disableArea);
        disableArea.setVisibility(View.VISIBLE);

        LinearLayout llQuestionArea = findViewById(R.id.llQuestionArea);
        LinearLayout mainDetailArea = findViewById(R.id.mainDetailArea);
        mainDetailArea.setVisibility(View.GONE);
        MaterialEditText txtTellUs = findViewById(R.id.txtTellUs);
        txtTellUs.setBothText(generalFun.retrieveLangLBl("", "LBL_DELIVERY_FEEDBACK"));
        MTextView thanksNoteTxt = findViewById(R.id.thanksNoteTxt);
        thanksNoteTxt.setText(generalFun.retrieveLangLBl("", "LBL_YOUR_WORDS"));

        MButton btn_type3 = ((MaterialRippleLayout) findViewById(R.id.btn_type3)).getChildView();
        btn_type3.setVisibility(View.GONE);
        btn_type3.setText(generalFun.retrieveLangLBl("", "LBL_SUBMIT_FEEDBACK"));

        // Star Rating Emoji images
        LinearLayout llStarEmojiArea = findViewById(R.id.llStarEmojiArea);
        llStarEmojiArea.setVisibility(View.VISIBLE);
        ImageView ivStarEmoji_1 = findViewById(R.id.ivStarEmoji_1);
        ImageView ivStarEmoji_2 = findViewById(R.id.ivStarEmoji_2);
        ImageView ivStarEmoji_3 = findViewById(R.id.ivStarEmoji_3);
        ImageView ivStarEmoji_4 = findViewById(R.id.ivStarEmoji_4);
        ImageView ivStarEmoji_5 = findViewById(R.id.ivStarEmoji_5);

        SimpleRatingBar ratingBarRestaurant = findViewById(R.id.ratingBarRestaurant);
        SimpleRatingBar ratingBarDriver = findViewById(R.id.ratingBarDriver);
        starRatingEmojiHide(ratingBarDriver.getRating(), ivStarEmoji_1, ivStarEmoji_2, ivStarEmoji_3, ivStarEmoji_4, ivStarEmoji_5);

        ratingBarDriver.setOnRatingBarChangeListener((simpleRatingBar, v, b) -> {
            if (simpleRatingBar.getRating() > 0) {

                if (llBannerArea.getVisibility() == View.VISIBLE) {
                    llBannerArea.setVisibility(View.GONE);
                    btn_type3.setVisibility(View.VISIBLE);
                    mainDetailArea.setVisibility(View.VISIBLE);
                    llQuestionArea.startAnimation(AnimationUtils.loadAnimation(getActContext(), R.anim.slide_up_anim));
                }
                if (ratingBarRestaurant.getRating() > 0) {
                    disableArea.setVisibility(View.GONE);
                } else {
                    disableArea.setVisibility(View.VISIBLE);
                }
            } else {
                disableArea.setVisibility(View.VISIBLE);
            }
            starRatingEmojiHide(simpleRatingBar.getRating(), ivStarEmoji_1, ivStarEmoji_2, ivStarEmoji_3, ivStarEmoji_4, ivStarEmoji_5);
        });

        // list data Container
        LinearLayout dataContainer = findViewById(R.id.dataContainer);
        JSONArray itemArray = generalFun.getJsonArray(getIntent().getStringExtra("listDriverFeedbackQuestions"));

        List<HashMap<String, String>> arrayList = new ArrayList<>();

        if (itemArray != null) {
            for (int i = 0; i < itemArray.length(); i++) {

                JSONObject data = generalFun.getJsonObject(itemArray, i);

                LayoutInflater layoutInflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                @SuppressLint("InflateParams") View driverFeedbackQuestionView = layoutInflater.inflate(R.layout.item_driver_feedback_questions, null);
                MTextView txtQuestion = driverFeedbackQuestionView.findViewById(R.id.txtQuestion);
                txtQuestion.setText(generalFun.getJsonValueStr("tQuestion", data));

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("iFeedbackId", generalFun.getJsonValueStr("iFeedbackId", data));
                hashMap.put("ans", "");
                arrayList.add(hashMap);

                MTextView btnYes = driverFeedbackQuestionView.findViewById(R.id.btnYes);
                MTextView btnNo = driverFeedbackQuestionView.findViewById(R.id.btnNo);
                btnYes.setOnClickListener(v -> {
                    btnYes.setBackground(getActContext().getResources().getDrawable(R.drawable.btn_border));
                    btnNo.setBackground(getActContext().getResources().getDrawable(R.drawable.border_gray_line));

                    btnYes.setTextColor(getActContext().getResources().getColor(R.color.white));
                    btnNo.setTextColor(getActContext().getResources().getColor(R.color.black));
                    hashMap.put("ans", "Yes");
                });
                btnNo.setOnClickListener(v -> {
                    btnYes.setBackground(getActContext().getResources().getDrawable(R.drawable.border_gray_line));
                    btnNo.setBackground(getActContext().getResources().getDrawable(R.drawable.btn_border));

                    btnYes.setTextColor(getActContext().getResources().getColor(R.color.black));
                    btnNo.setTextColor(getActContext().getResources().getColor(R.color.white));
                    hashMap.put("ans", "No");
                });

                dataContainer.addView(driverFeedbackQuestionView);
            }
        }

        // Restaurant Rating
        MTextView restaurantNameTxt = findViewById(R.id.restaurantNameTxt);
        restaurantNameTxt.setText(getIntent().getStringExtra("vCompany"));

        // Restaurant Star Rating Emoji images
        LinearLayout llStarEmojiRestaurantArea = findViewById(R.id.llStarEmojiRestaurantArea);
        llStarEmojiRestaurantArea.setVisibility(View.VISIBLE);
        ImageView ivStarEmoji_R1 = findViewById(R.id.ivStarEmoji_R1);
        ImageView ivStarEmoji_R2 = findViewById(R.id.ivStarEmoji_R2);
        ImageView ivStarEmoji_R3 = findViewById(R.id.ivStarEmoji_R3);
        ImageView ivStarEmoji_R4 = findViewById(R.id.ivStarEmoji_R4);
        ImageView ivStarEmoji_R5 = findViewById(R.id.ivStarEmoji_R5);

        starRatingEmojiHide(ratingBarRestaurant.getRating(), ivStarEmoji_R1, ivStarEmoji_R2, ivStarEmoji_R3, ivStarEmoji_R4, ivStarEmoji_R5);
        ratingBarRestaurant.setOnRatingBarChangeListener((simpleRatingBar, v, b) -> {
            starRatingEmojiHide(simpleRatingBar.getRating(), ivStarEmoji_R1, ivStarEmoji_R2, ivStarEmoji_R3, ivStarEmoji_R4, ivStarEmoji_R5);
            if (ratingBarDriver.getRating() > 0 && ratingBarRestaurant.getRating() > 0) {
                disableArea.setVisibility(View.GONE);
            } else {
                disableArea.setVisibility(View.VISIBLE);
            }
        });
        MaterialEditText txtTellUsRestaurant = findViewById(R.id.txtTellUsRestaurant);
        txtTellUsRestaurant.setBothText(generalFun.retrieveLangLBl("", "LBL_TELL_US"));

        btn_type3.setOnClickListener(v -> {
            //
            driverFeedQuestionsRating(ratingBarDriver, txtTellUs, new Gson().toJson(arrayList), ratingBarRestaurant, txtTellUsRestaurant);
        });

    }

    private void starRatingEmojiHide(float rating, ImageView ivStarEmoji_1, ImageView ivStarEmoji_2, ImageView ivStarEmoji_3, ImageView ivStarEmoji_4, ImageView ivStarEmoji_5) {
        ivStarEmoji_1.setVisibility(View.INVISIBLE);
        ivStarEmoji_2.setVisibility(View.INVISIBLE);
        ivStarEmoji_3.setVisibility(View.INVISIBLE);
        ivStarEmoji_4.setVisibility(View.INVISIBLE);
        ivStarEmoji_5.setVisibility(View.INVISIBLE);

        if (rating == 0) {
            ivStarEmoji_1.setVisibility(View.INVISIBLE);
        } else if (rating <= 1) {
            ivStarEmoji_1.setVisibility(View.VISIBLE);
        } else if (rating <= 2) {
            ivStarEmoji_2.setVisibility(View.VISIBLE);
        } else if (rating <= 3) {
            ivStarEmoji_3.setVisibility(View.VISIBLE);
        } else if (rating <= 4) {
            ivStarEmoji_4.setVisibility(View.VISIBLE);
        } else if (rating <= 5) {
            ivStarEmoji_5.setVisibility(View.VISIBLE);
        }
    }

    public void driverFeedQuestionsRating(SimpleRatingBar ratingBarDriver, MaterialEditText txtTellUs, String jsonArray, SimpleRatingBar ratingBarRestaurant, MaterialEditText txtTellUsRestaurant) {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "submitRating");
        parameters.put("iMemberId", generalFun.getMemberId());
        parameters.put("iOrderId", getIntent().getStringExtra("iOrderId"));

        parameters.put("ENABLE_FOOD_RATING_DETAIL_FLOW", "Yes");
        parameters.put("isDetailRatingForDriver", "Yes");
        parameters.put("driverFeedbackDetails", jsonArray);

        parameters.put("rating", ratingBarRestaurant.getRating() + "");
        parameters.put("message", Objects.requireNonNull(txtTellUsRestaurant.getText()).toString());
        parameters.put("tripID", "");
        parameters.put("rating1", ratingBarDriver.getRating() + "");
        parameters.put("message1", Objects.requireNonNull(txtTellUs.getText()).toString());

        parameters.put("eFromUserType", Utils.userType);
        parameters.put("eToUserType", "Company");
        parameters.put("eSystem", Utils.eSystem_Type);
        parameters.put("Platform", Utils.deviceType);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFun);
        exeWebServer.setDataResponseListener(responseString -> {
            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);
                if (isDataAvail) {
                    generalFun.storeData(Utils.USER_PROFILE_JSON, generalFun.getJsonValue(Utils.message_str_one, responseString));
                    final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setCancelable(false);
                    generateAlert.setBtnClickList(btn_id -> finish());
                    generateAlert.setContentMessage("", generalFun.retrieveLangLBl("", generalFun.getJsonValue(Utils.message_str, responseString)));
                    generateAlert.setPositiveBtn(generalFun.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    generateAlert.showAlertBox();
                } else {
                    generalFun.showGeneralMessage("", generalFun.retrieveLangLBl("", generalFun.getJsonValue(Utils.message_str, responseString)));
                }
            }
        });
        exeWebServer.execute();
    }

    @SuppressLint("SetTextI18n")
    private void restaurantDriverRatingInit() {
        restaurantDriverRatingArea.setVisibility(View.VISIBLE);
        driverFeedbackRatingArea.setVisibility(View.GONE);
        driverAreaRating = findViewById(R.id.driverAreaRating);

        MTextView ratingResNameTxt = findViewById(R.id.ratingResNameTxt);
        ratingResNameTxt.setText(getIntent().getStringExtra("vCompany"));
        MTextView ratingDriverNameTxt = findViewById(R.id.ratingDriverNameTxt);
        ratingDriverNameTxt.setText(generalFun.retrieveLangLBl("", "LBL_RATE_DELIVERY_BY") + " " + getIntent().getStringExtra("driverName"));

        res_commentBox = findViewById(R.id.res_commentBox);
        driver_commentBox = findViewById(R.id.driver_commentBox);
        ratingBar_res = findViewById(R.id.ratingBar_res);
        ratingBar_driver = findViewById(R.id.ratingBar_driver);
        View disableArea1 = findViewById(R.id.disableArea1);
        disableArea1.setVisibility(View.VISIBLE);

        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);
        res_commentBox.setHint(generalFun.retrieveLangLBl("Add Special Instruction for provider.", "LBL_RESTAURANT_RATING_NOTE"));
        driver_commentBox.setHint(generalFun.retrieveLangLBl("Add Special Instruction for provider.", "LBL_DRIVER_RATING_NOTE"));

        btn_type2.setText(generalFun.retrieveLangLBl("", "LBL_ENTER_DELIVERY_RATING"));

        ratingBar_res.setOnRatingBarChangeListener((simpleRatingBar, v, b) -> {
            if (getIntent().getStringExtra("eTakeaway") != null && getIntent().getStringExtra("eTakeaway").equalsIgnoreCase("Yes")) {
                if (ratingBar_res != null) {
                    if (ratingBar_res.getRating() > 0) {
                        disableArea1.setVisibility(View.GONE);
                    } else {
                        disableArea1.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                if (ratingBar_res != null && ratingBar_driver != null) {
                    if (ratingBar_res.getRating() > 0 && ratingBar_driver.getRating() > 0) {
                        disableArea1.setVisibility(View.GONE);
                    } else {
                        disableArea1.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        ratingBar_res.setRating(getIntent().getFloatExtra("rating", 0));

        ratingBar_driver.setOnRatingBarChangeListener((simpleRatingBar, v, b) -> {
            if (ratingBar_res != null && ratingBar_driver != null) {
                if (ratingBar_res.getRating() > 0 && ratingBar_driver.getRating() > 0) {
                    disableArea1.setVisibility(View.GONE);
                } else {
                    disableArea1.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_type2.setOnClickListener(new setOnClickList());


        if (getIntent().getStringExtra("eTakeaway") != null && getIntent().getStringExtra("eTakeaway").equalsIgnoreCase("Yes")) {
            driverAreaRating.setVisibility(View.GONE);
        }
    }


    public void ratingFood() {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "submitRating");
        parameters.put("iMemberId", generalFun.getMemberId());
        parameters.put("iOrderId", getIntent().getStringExtra("iOrderId"));

        parameters.put("rating", ratingBar_res.getRating() + "");
        parameters.put("message", Objects.requireNonNull(res_commentBox.getText()).toString());
        parameters.put("rating1", ratingBar_driver.getRating() + "");
        parameters.put("message1", Objects.requireNonNull(driver_commentBox.getText()).toString());

        parameters.put("eFromUserType", Utils.userType);
        parameters.put("eToUserType", "Company");
        parameters.put("eSystem", Utils.eSystem_Type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFun);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail) {
                    generalFun.storeData(Utils.USER_PROFILE_JSON, generalFun.getJsonValue(Utils.message_str_one, responseString));
                    final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setCancelable(false);
//                    generateAlert.setSystemAlertWindow(true);
                    generateAlert.setBtnClickList(btn_id -> {
                        // MyApp.getInstance().restartWithGetDataApp();
                        finish();
                    });
                    generateAlert.setContentMessage("", generalFun.retrieveLangLBl("", generalFun.getJsonValue(Utils.message_str, responseString)));
                    generateAlert.setPositiveBtn(generalFun.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    generateAlert.showAlertBox();
                } else {
                    generalFun.showGeneralMessage("", generalFun.retrieveLangLBl("", generalFun.getJsonValue(Utils.message_str, responseString)));
                }
            }
        });
        exeWebServer.execute();
    }

    private Activity getActContext() {
        return FoodRatingActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.backImgView) {
                FoodRatingActivity.super.onBackPressed();
            } else if (i == submitBtnId) {
                ratingFood();
            }
        }
    }
}