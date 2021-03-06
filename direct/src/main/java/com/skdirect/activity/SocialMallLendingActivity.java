package com.skdirect.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.JsonObject;
import com.skdirect.R;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivitySocialMallLendingBinding;
import com.skdirect.model.AppVersionModel;
import com.skdirect.model.TokenModel;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.DirectSDK;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.TextUtils;
import com.skdirect.utils.Utils;

import org.jetbrains.annotations.NotNull;

import io.reactivex.observers.DisposableObserver;

public class SocialMallLendingActivity extends AppCompatActivity {
    ActivitySocialMallLendingBinding mBinding;
    private DBHelper dbHelper;
    private String mobileNumber = "";
    private String address = "";
    private String pincode = "";
    private double latitude = 0;
    private double longitude = 0;
    private CommonClassForAPI commonClassForAPI;
    private String fcmToken = "";
    private String SOURCEKEY = "";
    private String BUYERNAME = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_social_mall_lending);
        dbHelper = DirectSDK.getInstance().dbHelper;
        commonClassForAPI = CommonClassForAPI.getInstance(this);
        mBinding.tvPlzWait.setText(dbHelper.getString(R.string.plz_wait));
        fcmToken = Utils.getFcmToken();
        if (getIntent().getExtras() != null) {
            mobileNumber = getIntent().getStringExtra("MOBILE_NUMBER");
            BUYERNAME = getIntent().getStringExtra("BUYERNAME");
            address = getIntent().getStringExtra("ADDRESS");
            pincode = getIntent().getStringExtra("PINCODE");
            latitude = getIntent().getDoubleExtra("LATITUDE", 0);
            longitude = getIntent().getDoubleExtra("LONGITUDE", 0);
            SOURCEKEY = getIntent().getStringExtra("SOURCEKEY");
            if (!TextUtils.isNullOrEmpty(SOURCEKEY)) {
                SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.SOURCEKEY, SOURCEKEY);
            } else {
                SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.SOURCEKEY, "");
            }
            if (Utils.isNetworkAvailable(this)) {
                if (commonClassForAPI != null) {
                    commonClassForAPI.getAppInfo(new DisposableObserver<AppVersionModel>() {
                        @Override
                        public void onNext(@NotNull AppVersionModel appVersionModels) {
                            if (appVersionModels != null) {
                                if (appVersionModels.isSuccess()) {
                                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.SELLER_URL, appVersionModels.getResultItem().getSellerUrl());
                                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.BUYER_URL, appVersionModels.getResultItem().getBuyerUrl());
                                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.PRIVACY_POLICY, appVersionModels.getResultItem().getPrivacyPolicy());
                                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.TERMS_CONDITION, appVersionModels.getResultItem().getTermsCondition());
                                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.ABOUT_APP, appVersionModels.getResultItem().getAboutApp());
                                    if (latitude == 0 || longitude == 0 || TextUtils.isNullOrEmpty(pincode)) {
                                        SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.FIRST_NAME, BUYERNAME);
                                        SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.MOBILE_NUMBER, mobileNumber);
                                        startActivity(new Intent(getApplicationContext(), PlaceSearchActivity.class));
                                    } else {
                                        commonClassForAPI.getTokenwithphoneNo(callToken, "password", Utils.getDeviceUniqueID(getApplicationContext()), Utils.getDeviceUniqueID(getApplicationContext()), true, true, "BUYERAPP", true, Utils.getDeviceUniqueID(getApplicationContext()), latitude, longitude, pincode, "", mobileNumber,BUYERNAME, SOURCEKEY);

                                    }

                                }
                            }
                        }
                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                            Utils.hideProgressDialog();
                        }
                    });
                }
            } else {
                Utils.setToast(this, dbHelper.getString(R.string.no_internet_connection));
            }

        } else {
            Intent intent = new Intent();
            intent.putExtra("Error", "No data found");
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
        }
    }

    private final DisposableObserver<TokenModel> callToken = new DisposableObserver<TokenModel>() {
        @Override
        public void onNext(@NotNull TokenModel model) {
            try {
                Utils.hideProgressDialog();
                if (model != null) {
                    commonClassForAPI.getUpdateToken(new DisposableObserver<JsonObject>() {
                        @Override
                        public void onNext(@NotNull JsonObject model) {
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                            Utils.hideProgressDialog();
                        }
                    }, fcmToken);
                    Utils.getTokenData(getApplicationContext(), model);
                    SharePrefs.setSharedPreference(getApplicationContext(), SharePrefs.Is_First_Time, true);
                    if (model.getIsRegistrationComplete()) {
                        SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_LOGIN, true);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), RegisterationActivity.class));
                    }
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable e) {
            Utils.hideProgressDialog();
            e.printStackTrace();
            Utils.setToast(getApplicationContext(), "Wrong key");
            Intent intent = new Intent();
            intent.putExtra("Error", "Wrong key");
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
        }

        @Override
        public void onComplete() {
            Utils.hideProgressDialog();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        // intent.putExtra("CustomerId", "1");
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }
}