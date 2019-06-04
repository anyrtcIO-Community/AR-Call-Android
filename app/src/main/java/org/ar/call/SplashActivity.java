package org.ar.call;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

public class SplashActivity extends AppCompatActivity {

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

                if (TextUtils.isEmpty(SpUtil.getString(Contants.PHONE))){
                    intent = new Intent(SplashActivity.this, SetPhoneActivity.class);
                }else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
                startActivity(intent);
                finish();

    }
}
