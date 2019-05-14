package org.ar.rtp2pcall;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import org.anyrtc.common.utils.AnyRTCUtils;
import org.anyrtc.common.utils.LooperExecutor;
import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.EglBase;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoCapturerAndroid;

import java.util.concurrent.Exchanger;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;

/**
 * Created by Eric on 2016/11/8.
 */

public class ARP2PKit {
    private static final String TAG = "ARP2PKit";
    /**
     * 构造访问jni底层库的对象
     */
    private long fNativeAppId;
    private ARP2PEvent p2pEvent;
    private final LooperExecutor mExecutor;
    private final EglBase mEglBase;

    private int mCameraId = 0;
    private VideoCapturerAndroid mVideoCapturer;
    private boolean isTurnOff = true;

    /**
     * 用户是否下线
     *
     * @return
     */
    public boolean isTurnOff() {
        return isTurnOff;
    }

    /**
     * 初始化P2P对象
     */
    public ARP2PKit() {
        mExecutor = ARP2PEngine.Inst().Executor();
        mEglBase = ARP2PEngine.Inst().Egl();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                fNativeAppId = nativeCreate(mCallHelper);
                nativeSetVideoProfileMode(ARP2PEngine.Inst().getP2POption().getVideoProfile().level);
                nativeSetVideoFpsProfile(ARP2PEngine.Inst().getP2POption().getVideoFps().level);
            }
        });
    }

    public ARP2PKit(ARP2PEvent p2pEvent) {
        AnyRTCUtils.assertIsTrue(p2pEvent != null);
        this.p2pEvent = p2pEvent;
        mExecutor = ARP2PEngine.Inst().Executor();
        mEglBase = ARP2PEngine.Inst().Egl();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                fNativeAppId = nativeCreate(mCallHelper);
                nativeSetVideoProfileMode(ARP2PEngine.Inst().getP2POption().getVideoProfile().level);
                nativeSetVideoFpsProfile(ARP2PEngine.Inst().getP2POption().getVideoFps().level);
            }
        });
    }

    /**
     * 设置回调接口对象
     *
     * @param p2pEvent
     */
    public void setP2PEvent(final ARP2PEvent p2pEvent) {
        AnyRTCUtils.assertIsTrue(p2pEvent != null);
        this.p2pEvent = p2pEvent;
    }

    /**
     * 加载本地摄像头
     *
     * @param render 底层图像地址
     *               RT_P2P_VIDEO_QHD(1),       //* 960*540 - 768kbps
     *               RT_P2P_VIDEO_SD(2),        //* 640*480 - 512kbps
     *               RT_P2P_VIDEO_LOW(3);       //* 352*288 - 384kbps
     * @return 打开本地预览返回值：0/1/2/3：没哟相机权限/打开成功/打开相机失败/相机已打开， 未释放
     */
    public int setLocalVideoCapturer(final long render) {

        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int permission = PermissionChecker.checkSelfPermission(ARP2PEngine.Inst().context(), CAMERA);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    if (mVideoCapturer == null) {
                        mCameraId = 0;
                        String cameraDeviceName = CameraEnumerationAndroid.getDeviceName(mCameraId);
                        String frontCameraDeviceName =
                                CameraEnumerationAndroid.getNameOfFrontFacingDevice();
                        int numberOfCameras = CameraEnumerationAndroid.getDeviceCount();
                        if (numberOfCameras > 1 && frontCameraDeviceName != null && ARP2PEngine.Inst().getP2POption().isDefaultFrontCamera()) {
                            cameraDeviceName = frontCameraDeviceName;
                            mCameraId = 1;
                        }
                        Log.d(TAG, "Opening camera: " + cameraDeviceName);
                        mVideoCapturer = VideoCapturerAndroid.create(cameraDeviceName, null);
                        //设置自动降帧率
//                        mVideoCapturer.setReduceFPS(true);
                        if (mVideoCapturer == null) {
                            Log.e("sys", "Failed to open camera");
                            LooperExecutor.exchange(result, 2);
                        }

                        nativeSetVideoCapturer(mVideoCapturer, render);
                        LooperExecutor.exchange(result, 1);
                    } else {
                        LooperExecutor.exchange(result, 3);
                    }
                } else {
                    LooperExecutor.exchange(result, 0);
                }
            }
        });
        return LooperExecutor.exchange(result, 0);
    }

    /**
     * 停止预览
     */
    public void stopCapturer() {
        final Exchanger<Boolean> result = new Exchanger<Boolean>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeSetVideoCapturer(null, 0);
                if (mVideoCapturer != null) {
                    try {
                        mVideoCapturer.stopCapture();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mVideoCapturer = null;
                }
                LooperExecutor.exchange(result, true);
            }
        });
        LooperExecutor.exchange(result, false);
    }

    /**
     * 清除对象
     */
    public void clear() {
        final Exchanger<Boolean> result = new Exchanger<Boolean>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (!isTurnOff) {
                    nativeTurnOff();
                }
                if (mVideoCapturer != null) {
                    try {
                        mVideoCapturer.stopCapture();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    nativeSetVideoCapturer(null, 0);
                    mVideoCapturer = null;
                }
                nativeDestroy();
                LooperExecutor.exchange(result, true);
            }
        });
        LooperExecutor.exchange(result, false);
    }

    /**
     * 设置验证token
     *
     * @param strUserToken token字符串:客户端向自己服务器申请
     * @return true：设置成功；false：设置失败
     */
    public boolean setUserToken(final String strUserToken) {
        final Exchanger<Boolean> result = new Exchanger<Boolean>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                boolean ret = false;
                if (null == strUserToken || strUserToken.equals("")) {
                    ret = false;
                } else {
                    nativeSetUserToken(strUserToken);
                    ret = true;
                }
                LooperExecutor.exchange(result, ret);
            }
        });
        return LooperExecutor.exchange(result, false);
    }

    /**
     * 设置本地音频是否可用
     *
     * @param enabled
     */
    public void setLocalAudioEnable(final boolean enabled) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeSetAudioEnable(enabled);
            }
        });
    }

    /**
     * 设置本地视频是否可用
     *
     * @param enabled
     */
    public void setLocalVideoEnable(final boolean enabled) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeSetVideoEnable(enabled);
            }
        });
    }

    /**
     * 设置本地视频编码码率
     *
     * @param bitrate
     */
    protected void setLocalVideoBitrate(final int bitrate) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeSetVideoBitrate(bitrate);
            }
        });
    }

    /**
     * 设置本地视频编码帧率
     *
     * @param fps
     */
    protected void setLocalVideoFps(final int fps) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeSetVideoFps(fps);
            }
        });
    }

    /**
     * 抓拍对方图片
     *
     * @param fileName 图片保存文件的全路径
     * @return 抓拍结果：0/1/2：抓拍失败（原因：文件路径不正确）/抓拍成功/抓拍时写文件失败（原因：无读写文件权限或者文件路径不正确）
     */
    public int snapPicture(final String fileName) {
        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int ret = 0;
                int permission = ActivityCompat.checkSelfPermission(ARP2PEngine.Inst().context(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    // We have permission granted to the user
                    boolean result = nativeSnapPeerPicture(fileName);
                    ret = result ? 1 : 0;
                } else {
                    ret = 2;
                }

                LooperExecutor.exchange(result, ret);
            }
        });
        return LooperExecutor.exchange(result, 0);
    }

    /**
     * 开始录制对方视频
     *
     * @param fileName 录像保存文件的全路径
     * @return 录像结果：0/1/2：录像失败（原因：文件路径不正确）/录像成功/录像时写文件失败（原因：无读写文件权限或者文件路径不正确）
     */
    public int startRecordVideo(final String fileName) {
        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int ret = 0;
                int permission = ActivityCompat.checkSelfPermission(ARP2PEngine.Inst().context(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    // We have permission granted to the user
                    boolean result = nativeStartRecordPeerVideo(fileName);
                    ret = result ? 1 : 0;
                } else {
                    ret = 2;
                }

                LooperExecutor.exchange(result, ret);
            }
        });
        return LooperExecutor.exchange(result, 0);
    }

    /**
     * 停止录制对方视频
     */
    public void stopRecordVideo() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeStopRecordPeerVideo();
            }
        });
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (mVideoCapturer != null && CameraEnumerationAndroid.getDeviceCount() > 1) {
                    mCameraId = (mCameraId + 1) % CameraEnumerationAndroid.getDeviceCount();
                    mVideoCapturer.switchCamera(null);
                }
            }
        });
    }

    /**
     * P2P 上线
     *
     * @param userId 用户的userid
     * @return 上线返回值：ture/false 上线成功/上线失败
     */
    public boolean turnOn(final String userId) {
        final Exchanger<Boolean> result = new Exchanger<Boolean>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeSetDeviceInfo(ARP2PEngine.Inst().getDeviceInfo());
                boolean ret = nativeTurnOn(userId, "", "", "");
                LooperExecutor.exchange(result, ret);
                isTurnOff = false;
            }
        });
        return LooperExecutor.exchange(result, false);
    }

    /**
     * P2P 下线
     */
    public void turnOff() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeTurnOff();
                isTurnOff = true;
            }
        });
    }

    /**
     * 发起P2P呼叫
     *
     * @param userId   P2P用户的userid
     * @param callMode P2P呼叫的类型（）
     * @param userData 呼叫时的自定义数据
     * @return 呼叫结果；0/1:呼叫失败（没有RECORD_AUDIO权限）/呼叫成功
     */
    public int makeCall(final String userId, final ARP2PCallMode callMode, final String userData) {
        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int ret = 0;
                int permission = ActivityCompat.checkSelfPermission(ARP2PEngine.Inst().context(), RECORD_AUDIO);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    // We have permission granted to the user
                    nativeMakeCall(userId, callMode.level, userData);
                    ret = 1;
                } else {
                    ret = 0;
                }

                LooperExecutor.exchange(result, ret);
            }
        });
        return LooperExecutor.exchange(result, 0);
    }

    /**
     * 接受P2P呼叫
     *
     * @param userId P2P用户的userid
     * @return 接受呼叫结果；0/1:失败（没有RECORD_AUDIO权限）/成功
     */
    public int accpetCall(final String userId) {
        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int ret = 0;
                int permission = ActivityCompat.checkSelfPermission(ARP2PEngine.Inst().context(), RECORD_AUDIO);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    // We have permission granted to the user
                    nativeAccpetCall(userId);
                    ret = 1;
                } else {
                    ret = 0;
                }

                LooperExecutor.exchange(result, ret);
            }
        });
        return LooperExecutor.exchange(result, 0);
    }

    /**
     * 拒绝P2P呼叫
     *
     * @param userId P2P用户的userid
     */
    public void rejectCall(final String userId) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeRejectCall(userId);
                if (mVideoCapturer != null) {
                    nativeSetVideoCapturer(null, 0);
                    mVideoCapturer = null;
                }
            }
        });
    }

    /**
     * 挂断P2P呼叫
     *
     * @param userId P2P用户的userid
     */
    public void endCall(final String userId) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeEndCall(userId);
                if (mVideoCapturer != null) {
                    nativeSetVideoCapturer(null, 0);
                    mVideoCapturer = null;
                }
            }
        });
    }

    public void setRTCRemoteVideoRender(final String peerId, final long render) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeSetRTCVideoRender(peerId, render);
            }
        });
    }

    /**
     * 切换至音频模式
     */
    public void swtichToAudioMode() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeSwtichToAudioMode();
            }
        });
    }

    /**
     * 发送消息信令
     *
     * @param userId  对方userid
     * @param message 信令内容
     */
    public void sendMessage(final String userId, final String message) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeSendUserMessage(userId, message);
            }
        });
    }

    /**
     * Jni interface
     */
    private native long nativeCreate(Object obj);

    private native void nativeSetUserToken(String strUserToken);

    private native void nativeSetDeviceInfo(String strDevInfo);

    private native void nativeSetAudioEnable(boolean enabled);

    private native void nativeSetVideoEnable(boolean enabled);

    private native void nativeSetVideoSize(int width, int height);

    private native void nativeSetVideoBitrate(int bitrate);

    private native void nativeSetVideoFps(int fps);

    private native void nativeSetVideoModeExcessive(int nVideoMode);

    private native void nativeSetVideoProfileMode(int nVideoMode);

    private native void nativeSetVideoFpsProfile(int nFpsMode);

    private native boolean nativeSnapPeerPicture(String fileName);

    private native boolean nativeStartRecordPeerVideo(String fileName);

    private native void nativeStopRecordPeerVideo();

    private native boolean nativeTurnOn(String userId, String strDevId, String strDevModel, String strOSVersion);

    private native void nativeTurnOff();

    private native void nativeMakeCall(String userId, int callMode, String userData);

    private native void nativeAccpetCall(String userId);

    private native void nativeRejectCall(String userId);

    private native void nativeEndCall(String userId);

    private native void nativeSwtichToAudioMode();

    private native void nativeSendUserMessage(String userId, String strMessage);

    private native void nativeSetVideoCapturer(VideoCapturer capturer, long nativeRenderer);

    private native void nativeSetRTCVideoRender(String strDevID, long nativeRenderer);

    private native void nativeDestroy();

    /**
     * 回调对象
     */
    private ARP2PHelper mCallHelper = new ARP2PHelper() {
        @Override
        public void onConnected() {
            isTurnOff = false;
            if (null != p2pEvent) {
                p2pEvent.onConnected();
            }
        }

        @Override
        public void onDisconnect(int nErrCode) {
            isTurnOff = true;
            if (null != p2pEvent) {
                p2pEvent.onDisconnect(nErrCode);
            }
        }

        @Override
        public void onRTCMakeCall(String strPeerUserId, int callMode, String userData) {
            if (null != p2pEvent) {
                p2pEvent.onRTCMakeCall(strPeerUserId, ARP2PCallMode.values()[callMode], userData);
            }
        }

        @Override
        public void onRTCAcceptCall(String strPeerUserId) {
            if (null != p2pEvent) {
                p2pEvent.onRTCAcceptCall(strPeerUserId);
            }
        }

        @Override
        public void onRTCRejectCall(String strPeerUserId, int errCode) {
            if (null != p2pEvent) {
                p2pEvent.onRTCRejectCall(strPeerUserId, errCode);
            }
        }

        @Override
        public void onRTCEndCall(String strPeerUserId, int errCode) {
            if (null != p2pEvent) {
                p2pEvent.onRTCEndCall(strPeerUserId, errCode);
            }
        }

        @Override
        public void onRTCSwithToAudioMode() {
            if (null != p2pEvent) {
                p2pEvent.onRTCSwithToAudioMode();
            }
        }

        @Override
        public void onRTCUserMessage(String strPeerUserId, String strMessage) {
            if (null != p2pEvent) {
                p2pEvent.onRTCUserMessage(strPeerUserId, strMessage);
            }
        }

        @Override
        public void onRTCOpenVideoRender(String strDevId) {
            if (null != p2pEvent) {
                p2pEvent.onRTCOpenRemoteVideoRender(strDevId);
            }
        }

        @Override
        public void onRTCCloseVideoRender(String strDevId) {
            if (null != p2pEvent) {
                p2pEvent.onRTCCloseRemoteVideoRender(strDevId);
            }
        }
    };
}
