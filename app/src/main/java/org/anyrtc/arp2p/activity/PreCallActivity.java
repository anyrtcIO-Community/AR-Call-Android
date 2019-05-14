package org.anyrtc.arp2p.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.anyrtc.arp2p.R;
import org.anyrtc.arp2p.utils.ExampleUtil;
import org.anyrtc.arp2p.utils.SharePrefUtil;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class PreCallActivity extends BaseActivity {
    private String mUserid = "";
    private TextView mTxtUserid;
    private EditText mEditText;
    private View space;

    CallRecordDialog mCallRecordDialog;
    private ImageView iv_icon;

    public void OnTopBtnClicked(View btn){
        switch (btn.getId()){
            case R.id.iv_back: {
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
                break;
            case R.id.tv_call_record: {
                if (mCallRecordDialog==null){
                    mCallRecordDialog=new CallRecordDialog();
                }
                mCallRecordDialog.show(getSupportFragmentManager(),"a");
            }
                break;
            default: {

            }
        }
    }

    /**
     * 按钮点击事件
     *
     * @param btn
     */
    public void OnBtnClicked(View btn) {
                String callid = mEditText.getText().toString();
                if (TextUtils.isEmpty(callid)) {
                    Toast.makeText(PreCallActivity.this, "请输入呼叫对方的userid", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!ExampleUtil.isValidTagAndAlias(callid)) {
                    Toast.makeText(PreCallActivity.this, "数据格式只能是数字,英文字母和中文", Toast.LENGTH_LONG).show();
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString("userid", mUserid);
                bundle.putString("callid", callid);
                bundle.putBoolean("p2p_push", false);
                if (btn.getId() == R.id.btn_p2p_video) {
                    bundle.putInt("p2p_mode", 0);
                } else if (btn.getId() == R.id.btn_p2p_video_pro) {
                    bundle.putInt("p2p_mode", 1);
                } else if (btn.getId() == R.id.btn_p2p_audio) {
                    bundle.putInt("p2p_mode", 2);
                }else if (btn.getId() == R.id.btn_p2p_watch_mode){
                    bundle.putInt("p2p_mode", 3);
                }
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(this, P2PCallActivity.class);
                startActivity(intent);

    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (isOnline){
            iv_icon.setImageResource(R.drawable.img_on_line);
            iv_icon.requestLayout();
        }else {
            iv_icon.setImageResource(R.drawable.img_off_line);
            iv_icon.requestLayout();
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_pre_call;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        space = findViewById(R.id.view_space);
        mImmersionBar.titleBar(space).init();
        mTxtUserid = (TextView) findViewById(R.id.txt_userid);
        mEditText = (EditText) findViewById(R.id.edit_p2p);
        mUserid = SharePrefUtil.getString("userid");
        mTxtUserid.setText("本机用户id：" + mUserid);
        iv_icon= (ImageView) findViewById(R.id.iv_icon);


    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onConnected() {
        PreCallActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (iv_icon!=null) {
                    iv_icon.setImageResource(R.drawable.img_on_line);
                }
            }
        });
    }

    @Override
    public void onDisconnect(int nErrCode) {
        PreCallActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (iv_icon!=null) {
                    iv_icon.setImageResource(R.drawable.img_off_line);
                }
            }
        });
    }

    @Override
    public void onRTCMakeCall(String strPeerUserId, int nCallMode, String strUserData) {

    }

    @Override
    public void onRTCAcceptCall(String strPeerUserId) {

    }

    @Override
    public void onRTCRejectCall(String strPeerUserId, int nErrCode) {

    }

    @Override
    public void onRTCEndCall(String strPeerUserId, int nErrCode) {

    }

    @Override
    public void onRTCSwithToAudioMode() {

    }

    @Override
    public void onRTCUserMessage(String strPeerUserId, String strMessage) {

    }

    @Override
    public void onRTCOpenVideoRender(String strDevId) {

    }

    @Override
    public void onRTCCloseVideoRender(String strDevId) {

    }
}
