package org.anyrtc.p2p.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;

import org.anyrtc.p2p.P2PApplication;
import org.anyrtc.p2p.utils.SharePrefUtil;
import org.anyrtc.rtp2pcall.RTP2PCallKit;

/**
 * Created by Skyline on 2016/5/24.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected ImmersionBar mImmersionBar;
    protected RTP2PCallKit rtp2PCallKit;
    protected boolean isCallActivity=false;
    ConnListener connListener;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getLayoutId());
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.init();
        rtp2PCallKit= P2PApplication.the().getmP2pKit();
        this.initView(savedInstanceState);

    }

    interface ConnListener{
        void ConnSuccess();
        void ConnOff();
    }


    public void setConnListener(ConnListener connListener) {
        this.connListener = connListener;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null)
            mImmersionBar.destroy();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }


    public void startAnimActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }

    public void finishAnimActivity() {
        finish();
    }

    public void startAnimActivity(Class<?> cls, String key,boolean value) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(key,value);
        startActivity(intent);
    }

    public abstract int getLayoutId();

    public abstract void initView(Bundle savedInstanceState);


    @Override
    protected void onResume() {
        super.onResume();
        if (!isCallActivity) {
            rtp2PCallKit.setP2PCallHelper(p2PListener);
        }
    }


    P2PListener p2PListener=new P2PListener() {
        @Override
        public void onConnected() {
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (connListener!=null) {
                        connListener.ConnSuccess();
                    }
                    MainActivity.isTurnOn=true;
                    Toast.makeText(BaseActivity.this, "连接成功！", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onDisconnect(int nErrCode) {
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (connListener!=null) {
                        connListener.ConnOff();
                    }
                    Toast.makeText(BaseActivity.this, "连接断开！", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onRTCMakeCall(final String strPeerUserId, final int nCallMode, String strUserData) {
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Bundle bundle = new Bundle();
                    bundle.putString("userid", SharePrefUtil.getString("userid"));
                    bundle.putString("callid", strPeerUserId);
                    bundle.putBoolean("p2p_push", true);
                    bundle.putInt("p2p_mode", nCallMode);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(BaseActivity.this, P2PCallActivity.class);
                    startActivity(intent);
                }
            });
        }

        @Override
        public void onRTCRejectCall(String strPeerUserId, int errCode) {
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BaseActivity.this, "对方已挂断！", Toast.LENGTH_LONG).show();
                }
            });
        }
    };
}
