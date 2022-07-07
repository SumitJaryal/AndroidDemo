package com.ridetogo.user.deliverAll;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.ridetogo.user.AccountverificationActivity;
import com.ridetogo.user.BuildConfig;
import com.ridetogo.user.ProfilePaymentActivity;
import com.ridetogo.user.R;
import com.ridetogo.user.VerifyInfoActivity;
import com.general.files.GeneralFunctions;
import com.general.files.ImageFilePath;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.squareup.picasso.Picasso;
import com.utils.Logger;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UploadProofActivity extends AppCompatActivity {

    GeneralFunctions generalFunc;
    private static final int RC_SIGN_IN_UP = 007;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Temp";
    private static final int SELECT_PICTURE = 2;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private Uri fileUri;
    private String selectedImagePath = "";
    private String pathForCameraImage = "";
    String userProfileJson;
    private String SYSTEM_PAYMENT_FLOW = "";
    private String APP_PAYMENT_MODE = "", APP_PAYMENT_METHOD = "";
    boolean isCODAllow = true;
    String ePaymentOption = "Cash";

    String selectedMethod = "";
    ImageView backImgView;
    String isWalletSelect = "No";
    String outStandingAmount = "";
    private String DisplayCardPayment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_proof);

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());

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
        isWalletSelect = getIntent().getStringExtra("isWallet");
        ePaymentOption = getIntent().getStringExtra("isCash");
        isCODAllow = getIntent().getBooleanExtra("isCODAllow", false);
        outStandingAmount = getIntent().getStringExtra("outStandingAmount");
        SYSTEM_PAYMENT_FLOW = getIntent().getStringExtra("DisplayCardPayment");
        selectedMethod = getIntent().getStringExtra("selectedMethod");


        MTextView titleTxt = (MTextView) findViewById(R.id.titleTxt);
        final MTextView uploadStatusTxt = (MTextView) findViewById(R.id.uploadStatusTxt);
        MTextView uploadTitleTxt = (MTextView) findViewById(R.id.uploadTitleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);


        final ImageView uploadImgVIew = (ImageView) findViewById(R.id.uploadImgVIew);
        LinearLayout uploadImgArea = (LinearLayout) findViewById(R.id.uploadImgArea);
        MButton btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();

        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_UPLOAD_IMAGE_SERVICE"));


        uploadTitleTxt.setText(generalFunc.retrieveLangLBl("Click and upload photo of your Proof(+)", "LBL_UPLOAD_ID_PROOF"));
        btn_type2.setText(generalFunc.retrieveLangLBl("Next", "LBL_NEXT"));


        btn_type2.setId(Utils.generateViewId());
        btn_type2.setTextSize(16);
        uploadImgArea.setOnClickListener(view -> {

            if (generalFunc.isCameraPermissionGranted()) {
                findViewById(R.id.uploadStatusTxt).setVisibility(View.GONE);
                new ImageSourceDialog().run();
            } else {
                uploadStatusTxt.setVisibility(View.VISIBLE);
                generalFunc.showMessage(uploadStatusTxt, "Allow this app to use camera.");
            }
        });
        btn_type2.setOnClickListener(view -> {

            if (TextUtils.isEmpty(selectedImagePath)) {
                uploadStatusTxt.setVisibility(View.VISIBLE);
                generalFunc.showMessage(uploadStatusTxt, generalFunc.retrieveLangLBl("Please select image", "LBL_PLEASE_SELECT_IMAGE"));

            } else {
                uploadStatusTxt.setVisibility(View.GONE);

                gotoPaymentScreen();

            }
        });

        backImgView.setOnClickListener(view -> onBackPressed());

    }

    private void gotoPaymentScreen() {
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


            if (generalFunc.getJsonValue("vPhone", userProfileJson).equals("") || generalFunc.getJsonValue("vEmail", userProfileJson).equals("")) {
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


            Bundle bn = new Bundle();
            //  new StartActProcess(getActContext()).startActForResult(BusinessSelectPaymentActivity.class, bn, Utils.SELECT_ORGANIZATION_PAYMENT_CODE);
            bn.putString("isWallet", isWalletSelect);
            bn.putBoolean("isCash", ePaymentOption.equals("Cash") ? true : false);
            bn.putBoolean("isCODAllow", isCODAllow);
            bn.putString("iServiceId", generalFunc.getServiceId());
            bn.putString("isCheckout", "");
            bn.putString("outStandingAmount", outStandingAmount);
            bn.putString("DisplayCardPayment", SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1") ? DisplayCardPayment : "");
            if (generalFunc.isDeliverOnlyEnabled()) {
                selectedMethod = "Instant";
            } else {
                selectedMethod = "Manual";
            }
            bn.putString("selectedMethod", selectedMethod);
            new StartActProcess(getActContext()).startActForResult(ProfilePaymentActivity.class, bn, Utils.SELECT_ORGANIZATION_PAYMENT_CODE);

        }

    }

    private void getUserProfileJson() {
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        SYSTEM_PAYMENT_FLOW = generalFunc.getJsonValue("SYSTEM_PAYMENT_FLOW", userProfileJson);
        APP_PAYMENT_MODE = generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson);
        APP_PAYMENT_METHOD = generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson);

    }


    class ImageSourceDialog implements Runnable {

        @Override
        public void run() {


            final Dialog dialog_img_update = new Dialog(getActContext(), R.style.ImageSourceDialogStyle);

            dialog_img_update.setContentView(R.layout.design_prescription_image_source_select);

            //  MTextView chooseImgHTxt = (MTextView) dialog_img_update.findViewById(R.id.chooseImgHTxt);
            MTextView cameraTxt = (MTextView) dialog_img_update.findViewById(R.id.cameraTxt);
            MTextView galleryTxt = (MTextView) dialog_img_update.findViewById(R.id.galleryTxt);
            MTextView prescriptionTxt = (MTextView) dialog_img_update.findViewById(R.id.prescriptionTxt);
            LinearLayout cameraView = (LinearLayout) dialog_img_update.findViewById(R.id.cameraView);
            LinearLayout galleryView = (LinearLayout) dialog_img_update.findViewById(R.id.galleryView);
            LinearLayout prescriptionArea = (LinearLayout) dialog_img_update.findViewById(R.id.prescriptionArea);
            prescriptionArea.setVisibility(View.GONE);
            MButton btn_type2 = ((MaterialRippleLayout) dialog_img_update.findViewById(R.id.btn_type2)).getChildView();
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
            cameraTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CAMERA"));
            galleryTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GALLERY"));


            // ImageView closeDialogImgView = (ImageView) dialog_img_update.findViewById(R.id.closeDialogImgView);

            btn_type2.setOnClickListener(v -> {
                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                }
            });


            cameraView.setOnClickListener(v -> {

                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                }

                if (!isDeviceSupportCamera()) {
                    generalFunc.showMessage(getCurrView(), generalFunc.retrieveLangLBl("", "LBL_NOT_SUPPORT_CAMERA_TXT"));
                } else {
                    chooseFromCamera();
                }

            });

            galleryView.setOnClickListener(v -> {

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

        }

    }


    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public void chooseFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

    }

    public void chooseFromCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {
//        return Uri.fromFile(getOutputMediaFile(type));

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {

            if (pathForCameraImage.equalsIgnoreCase("")) {
                selectedImagePath = new ImageFilePath().getPath(getActContext(), fileUri);
            } else {
                selectedImagePath = pathForCameraImage;
            }

            if (selectedImagePath == null || selectedImagePath.equalsIgnoreCase("")) {
                selectedImagePath = "";

                generalFunc.showMessage(generalFunc.getCurrentView((Activity) getActContext()), generalFunc.retrieveLangLBl("Can't read selected image. Please try again.", "LBL_IMAGE_READ_FAILED"));
                return;
            }


            if (selectedImagePath == null || selectedImagePath.equalsIgnoreCase("")) {
                selectedImagePath = "";

                generalFunc.showMessage(generalFunc.getCurrentView((Activity) getActContext()), generalFunc.retrieveLangLBl("Can't read selected image. Please try again.", "LBL_IMAGE_READ_FAILED"));
                return;
            }

            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);

                int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;

                double ratioOfImage = (double) imageWidth / (double) imageHeight;
                double widthOfImage = ratioOfImage * Utils.dipToPixels(getActContext(), 200);

                Glide.with(getActContext()).load(generalFunc.decodeFile(selectedImagePath, (int) widthOfImage, Utils.dipToPixels(getActContext(), 200), Utils.TempProfileImageName)).into(((ImageView) findViewById(R.id.uploadImgVIew)));
                //Picasso.get().load(fileUri).resize((int) widthOfImage, Utils.dipToPixels(getActContext(), 200)).into(((ImageView) findViewById(R.id.uploadImgVIew)));
            } catch (Exception e) {
                Picasso.get().load(fileUri).resize(Utils.dipToPixels(getActContext(), 400), Utils.dipToPixels(getActContext(), 200)).into(((ImageView) findViewById(R.id.uploadImgVIew)));
            }

            findViewById(R.id.camImgVIew).setVisibility(View.GONE);
            findViewById(R.id.ic_add).setVisibility(View.GONE);


        } else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {

            Uri selectedImageUri = data.getData();

            selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);

            if (selectedImagePath == null || selectedImagePath.equalsIgnoreCase("")) {
                selectedImagePath = "";

                generalFunc.showMessage(generalFunc.getCurrentView((Activity) getActContext()), generalFunc.retrieveLangLBl("Can't read selected image. Please try again.", "LBL_IMAGE_READ_FAILED"));
                return;
            }


            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);

                int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;

                double ratioOfImage = (double) imageWidth / (double) imageHeight;
                double widthOfImage = ratioOfImage * Utils.dipToPixels(getActContext(), 200);

                Picasso.get().load(selectedImageUri).resize((int) widthOfImage, Utils.dipToPixels(getActContext(), 200)).into(((ImageView) findViewById(R.id.uploadImgVIew)));


            } catch (Exception e) {
                Picasso.get().load(selectedImageUri).resize(Utils.dipToPixels(getActContext(), 400), Utils.dipToPixels(getActContext(), 200)).into(((ImageView) findViewById(R.id.uploadImgVIew)));
            }

            findViewById(R.id.camImgVIew).setVisibility(View.GONE);
            findViewById(R.id.ic_add).setVisibility(View.GONE);


        } else if (requestCode == Utils.SELECT_ORGANIZATION_PAYMENT_CODE) {

            if (resultCode == RESULT_OK) {

                Bundle bundle = new Bundle();
                bundle.putBoolean("isCash", data.getBooleanExtra("isCash", false));
                bundle.putBoolean("isWallet", data.getBooleanExtra("isWallet", false));
                bundle.putSerializable("data", data.getSerializableExtra("data"));
                bundle.putString("iTripReasonId", data.getStringExtra("iTripReasonId"));
                bundle.putString("vReasonTitle", data.getStringExtra("vReasonTitle"));
                bundle.putString("vReasonName", data.getStringExtra("vReasonName"));
                bundle.putInt("selectPos", data.getIntExtra("selectPos", 0));
                bundle.putString("vProfileName", data.getStringExtra("vProfileName"));
                bundle.putString("selectedImagePath", selectedImagePath);

                Logger.d("UPLPRF", data.getBooleanExtra("isCash", false) + "");
                Logger.d("UPLPRF", data.getBooleanExtra("isWallet", false) + "");
                Logger.d("UPLPRF", data.getSerializableExtra("data") + "");
                Logger.d("UPLPRF", selectedImagePath + "");


                new StartActProcess(getActContext()).setOkResult(bundle);
                finish();


               /* Handler handler  =new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                    }
                },50);*/


            }
        }

    }

    public void notifyVerifyMobile() {
        String vPhoneCode = generalFunc.retrieveValue(Utils.DefaultPhoneCode);
        Bundle bn = new Bundle();
        bn.putString("MOBILE", vPhoneCode + generalFunc.getJsonValue("vPhone", userProfileJson));
        bn.putString("msg", "DO_PHONE_VERIFY");
        bn.putBoolean("isrestart", false);

        new StartActProcess(getActContext()).startActForResult(VerifyInfoActivity.class, bn, Utils.VERIFY_MOBILE_REQ_CODE);

    }

    public View getCurrView() {
        return generalFunc.getCurrentView(UploadProofActivity.this);
    }

    private Context getActContext() {
        return UploadProofActivity.this;
    }
}
