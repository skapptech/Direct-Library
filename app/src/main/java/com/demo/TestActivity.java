package com.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.skdirect.activity.SocialMallLendingActivity;
import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {
    private  String MOBILE_NUMBER = "9752640201";
    private  String CUSTOMER_NAME = "Test";
    private  String SOURCEKEY = "73F6CF7B-7C14-48B1-A392-C0590AB6A06C";
    private  String ADDRESS = "Vijay Nagar";
    private  String PINCODE = "452010";
    private  double LATITUDE = 22.7533;
    private  double LONGITUDE = 75.8937;
    private  int REQUEST_CODE = 1199;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        callRunTimePermissions();
        findViewById(R.id.btnOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MarshmallowPermissions.checkPermissionLocation(TestActivity.this)) {
                    /* GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                Geocoder mGeocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = null;
                String address = "",pincode = "";
                try {
                    addresses = mGeocoder.getFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(), 1);
                    address =  addresses.get(0).getAddressLine(0);
                    pincode = addresses.get(0).getPostalCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                    Intent intent = new Intent(getApplicationContext(), SocialMallLendingActivity.class);
                    intent.putExtra("MOBILE_NUMBER", MOBILE_NUMBER);
                    intent.putExtra("BUYERNAME", CUSTOMER_NAME);
                    intent.putExtra("SOURCEKEY", SOURCEKEY);
                    intent.putExtra("ADDRESS", ADDRESS);
                    intent.putExtra("PINCODE", PINCODE);
                    intent.putExtra("LATITUDE", LATITUDE);
                    intent.putExtra("LONGITUDE", LONGITUDE);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    MarshmallowPermissions.requestPermissionLocation(TestActivity.this, 999);
                }
            }
        });

    }
    public void callRunTimePermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                Log.e("onDenied", "onGranted");
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                Log.e("onDenied", "onDenied" + deniedPermissions);

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (data != null && resultCode == RESULT_OK) {
                System.out.println("data::"+data.toString());
            }else
            {
                if (data != null)
                {
                    System.out.println("data::"+data.getStringExtra("Error"));
                }
                // Toast.makeText(this, data.getStringExtra("Error"), Toast.LENGTH_SHORT).show();
            }

        }
    }
}