package org.anyrtc.rtp2pcall;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import org.anyrtc.common.enums.AnyRTCP2PMediaType;
import org.anyrtc.common.enums.AnyRTCVideoQualityMode;
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
@Deprecated
public class RTP2PCallKit {
    private static final String TAG = "RTP2PCallKit";
    /**
     * 构造访问jni底层库的对象
     */
    private long fNativeAppId;
    private RTP2PCallHelper mP2PHelper;
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
    public RTP2PCallKit() {
        mExecutor = AnyRTCP2PEngine.Inst().Executor();
        mEglBase = AnyRTCP2PEngine.Inst().Egl();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                fNativeAppId = nativeCreate(mCallHelper);
            }
        });
    }

    /**
     * 设置回调接口对象
     *
     * @param p2PCallHelper
     */
    public void setP2PCallHelper(final RTP2PCallHelper p2PCallHelper) {
        AnyRTCUtils.assertIsTrue(p2PCallHelper != null);
        mP2PHelper = p2PCallHelper;
    }

    /**
     * 加载本地摄像头
     *
     * @param renderPointer 底层图像地址
     * @param front         是否是前置摄像头：true：前置摄像头，false：后置摄像头
     * @param videoMode     RTP2PVIDEOMODE 中的清晰度标准：RT_P2P_VIDEO_HD(0),        //* 1280*720 - 1024kbps
     *                      RT_P2P_VIDEO_QHD(1),       //* 960*540 - 768kbps
     *                      RT_P2P_VIDEO_SD(2),        //* 640*480 - 512kbps
     *                      RT_P2P_VIDEO_LOW(3);       //* 352*288 - 384kbps
     * @return 打开本地预览返回值：0/1/2/3：没哟相机权限/打开成功/打开相机失败/相机资源被占用
     */
    public int setLocalVideoCapturer(final long renderPointer, final boolean front, final AnyRTCVideoQualityMode videoMode) {

        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int permission = PermissionChecker.checkSelfPermission(AnyRTCP2PEngine.Inst().context(), CAMERA);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    if (mVideoCapturer == null) {
                        mCameraId = 0;
                        String cameraDeviceName = CameraEnumerationAndroid.getDeviceName(mCameraId);
                        String frontCameraDeviceName =
                                CameraEnumerationAndroid.getNameOfFrontFacingDevice();
                        int numberOfCameras = CameraEnumerationAndroid.getDeviceCount();
                        if (numberOfCameras > 1 && frontCameraDeviceName != null && front) {
                            cameraDeviceName = frontCameraDeviceName;
                            mCameraId = 1;
                        }
                        Log.d(TAG, "Opening camera: " + cameraDeviceName);
                        mVideoCapturer = VideoCapturerAndroid.create(cameraDeviceName, null);
                        //设置自动降帧率
                        mVideoCapturer.setReduceFPS(true);
                        if (mVideoCapturer == null) {
                            Log.e("sys", "Failed to open camera");
                            LooperExecutor.exchange(result, 2);
                        }
                        nativeSetVideoModeExcessive(videoMode.level);
                        nativeSetVideoCapturer(mVideoCapturer, renderPointer);
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
     * 设置一直打开音频通道权限
     *
     * @param enabled true/false
     */
    public void setEnableUseAudioAlways(final boolean enabled) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeEnableUseAudioAlways(enabled);
            }
        });
    }

    /**
     * 释放音频采集
     */
    private void stopAudioTrack() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeStopAudioTrack();
            }
        });
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
     * 抓拍图片
     *
     * @param strFileName 图片保存文件的全路径
     * @return 抓拍结果：0/1/2：抓拍失败（原因：文件路径不正确）/抓拍成功/抓拍时写文件失败（原因：无读写文件权限或者文件路径不正确）
     */
    public int snapPeerPicture(final String strFileName) {
        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int ret = 0;
//                int permission = ActivityCompat.checkSelfPermission(AnyRTCP2PEngine.Inst().context(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int permission = PermissionChecker.checkSelfPermission(AnyRTCP2PEngine.Inst().context(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    // We have permission granted to the user
                    boolean result = nativeSnapPeerPicture(strFileName);
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
     * 开始录像
     *
     * @param strFileName 录像保存文件的全路径
     * @return 录像结果：0/1/2：录像失败（原因：文件路径不正确）/录像成功/录像时写文件失败（原因：无读写文件权限或者文件路径不正确）
     */
    public int startRecordPeerVideo(final String strFileName) {
        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int ret = 0;
//                int permission = ActivityCompat.checkSelfPermission(AnyRTCP2PEngine.Inst().context(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int permission = PermissionChecker.checkSelfPermission(AnyRTCP2PEngine.Inst().context(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    // We have permission granted to the user
                    boolean result = nativeStartRecordPeerVideo(strFileName);
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
     * 停止录像
     */
    public void stopRecordPeerVideo() {
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
     * @param strUserId 用户的userid
     * @return 上线返回值：ture/false 上线成功/上线失败
     */
    public boolean turnOn(final String strUserId) {
        final Exchanger<Boolean> result = new Exchanger<Boolean>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeSetDeviceInfo(AnyRTCP2PEngine.Inst().getDeviceInfo());
                boolean ret = nativeTurnOn(strUserId, "", "", "");
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
     * @param strPeerUId  P2P用户的userid
     * @param nCallMode   P2P呼叫的类型（）
     * @param strUserData 呼叫时的自定义数据
     * @return 呼叫结果；0：呼叫成功：-1：操作频繁；-2：自己不能呼叫自己；-3：音频权限不足
     */
    public int makeCall(final String strPeerUId, final AnyRTCP2PMediaType nCallMode, final String strUserData) {
        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int ret = 0;
                int permission = PermissionChecker.checkSelfPermission(AnyRTCP2PEngine.Inst().context(), RECORD_AUDIO);

                if (permission == PackageManager.PERMISSION_GRANTED) {
                    // We have permission granted to the user
                    ret = nativeMakeCall(strPeerUId, nCallMode.level, strUserData);
                } else {
                    ret = -3;
                }

                LooperExecutor.exchange(result, ret);
            }
        });
        return LooperExecutor.exchange(result, 0);
    }

    /**
     * 接受P2P呼叫
     *
     * @param strPeerUId P2P用户的userid
     * @return 接受呼叫结果；0/1:失败（没有RECORD_AUDIO权限）/成功
     */
    public int accpetCall(final String strPeerUId) {
        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int ret = 0;
                int permission = PermissionChecker.checkSelfPermission(AnyRTCP2PEngine.Inst().context(), RECORD_AUDIO);

                if (permission == PackageManager.PERMISSION_GRANTED) {
                    // We have permission granted to the user
                    nativeAccpetCall(strPeerUId);
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
     * @param strPeerUId P2P用户的userid
     */
    public void rejectCall(final String strPeerUId) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeRejectCall(strPeerUId);
            }
        });
    }

    /**
     * 挂断P2P呼叫
     *
     * @param strPeerUId P2P用户的userid
     */
    public void endCall(final String strPeerUId) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeEndCall(strPeerUId);
            }
        });
    }

    public void setRTCVideoRender(final String strLivePeerID, final long renderPointer) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeSetRTCVideoRender(strLivePeerID, renderPointer);
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
     * @param strPeerUId 对方userid
     * @param strMessage 信令内容
     */
    public void SendUserMessage(final String strPeerUId, final String strMessage) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeSendUserMessage(strPeerUId, strMessage);
            }
        });
    }

    /**
     * Jni interface
     */
    private native long nativeCreate(Object obj);

    private native void nativeEnableUseAudioAlways(boolean bEnable);

    private native void nativeSetUserToken(String strUserToken);

    private native void nativeSetDeviceInfo(String strDevInfo);

    private native void nativeSetAudioEnable(boolean enabled);

    private native void nativeSetVideoEnable(boolean enabled);

    private native void nativeSetVideoSize(int width, int height);

    private native void nativeSetVideoBitrate(int bitrate);

    private native void nativeSetVideoFps(int fps);

    private native void nativeSetVideoModeExcessive(int nVideoMode);

    private native void nativeSetVideoProfileMode(int nVideoMode);

    private native boolean nativeSnapPeerPicture(String strFileName);

    private native boolean nativeStartRecordPeerVideo(String strFileName);

    private native void nativeStopRecordPeerVideo();

    private native void nativeStopAudioTrack();

    private native boolean nativeTurnOn(String strUserId, String strDevId, String strDevModel, String strOSVersion);

    private native void nativeTurnOff();

    private native int nativeMakeCall(String strPeerUId, int nCallMode, String strUserData);

    private native void nativeAccpetCall(String strPeerUId);

    private native void nativeRejectCall(String strPeerUId);

    private native void nativeEndCall(String strPeerUId);

    private native void nativeSwtichToAudioMode();

    private native void nativeSendUserMessage(String strPeerUId, String strMessage);

    private native void nativeSetVideoCapturer(VideoCapturer capturer, long nativeRenderer);

    private native void nativeSetRTCVideoRender(String strDevID, long nativeRenderer);

    private native void nativeDestroy();

    /**
     * 回调对象
     */
    private RTP2PCallHelper mCallHelper = new RTP2PCallHelper() {
        @Override
        public void onConnected() {
            isTurnOff = false;
            if (null != mP2PHelper) {
                mP2PHelper.onConnected();
            }
        }

        @Override
        public void onDisconnect(int nErrCode) {
            isTurnOff = true;
            if (null != mP2PHelper) {
                mP2PHelper.onDisconnect(nErrCode);
            }
        }

        @Override
        public void onRTCMakeCall(String strPeerUserId, int nCallMode, String strUserData) {
            if (null != mP2PHelper) {
                mP2PHelper.onRTCMakeCall(strPeerUserId, nCallMode, strUserData);
            }
        }

        @Override
        public void onRTCAcceptCall(String strPeerUserId) {
            if (null != mP2PHelper) {
                mP2PHelper.onRTCAcceptCall(strPeerUserId);
            }
        }

        @Override
        public void onRTCRejectCall(String strPeerUserId, int errCode) {
            if (null != mP2PHelper) {
                mP2PHelper.onRTCRejectCall(strPeerUserId, errCode);
            }
        }

        @Override
        public void onRTCEndCall(String strPeerUserId, int errCode) {
            if (null != mP2PHelper) {
                mP2PHelper.onRTCEndCall(strPeerUserId, errCode);
            }
        }

        @Override
        public void onRTCSwithToAudioMode() {
            if (null != mP2PHelper) {
                mP2PHelper.onRTCSwithToAudioMode();
            }
        }

        @Override
        public void onRTCUserMessage(String strPeerUserId, String strMessage) {
            if (null != mP2PHelper) {
                mP2PHelper.onRTCUserMessage(strPeerUserId, strMessage);
            }
        }

        @Override
        public void onRTCOpenVideoRender(String strDevId) {
            if (null != mP2PHelper) {
                mP2PHelper.onRTCOpenVideoRender(strDevId);
            }
        }

        @Override
        public void onRTCCloseVideoRender(String strDevId) {
            if (null != mP2PHelper) {
                mP2PHelper.onRTCCloseVideoRender(strDevId);
            }
        }
    };
}