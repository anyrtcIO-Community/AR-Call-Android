package org.ar.arcall;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import org.ar.common.enums.ARNetQuality;
import org.ar.common.utils.ARUtils;
import org.ar.common.utils.LooperExecutor;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.EglBase;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoCapturerAndroid;

import java.util.concurrent.Exchanger;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;

/**
 * @author Skyline
 * @date 2019/3/18
 */

public class ARCallKit {
    private static final String TAG = "ARCallKit";
    /**
     * 构造访问jni底层库的对象
     */
    private long fNativeAppId;
    private ARCallEvent arCallEvent;
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
     * 初始化对象
     */
    public ARCallKit() {
        mExecutor = ARCallEngine.Inst().Executor();
        mEglBase = ARCallEngine.Inst().Egl();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                fNativeAppId = nativeCreate(mCallHelper);

            }
        });
    }

    public ARCallKit(ARCallEvent callEvent) {
        ARUtils.assertIsTrue(callEvent != null);
        this.arCallEvent = callEvent;
        mExecutor = ARCallEngine.Inst().Executor();
        mEglBase = ARCallEngine.Inst().Egl();
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
     * @param arCallEvent
     */
    public void setArCallEvent(final ARCallEvent arCallEvent) {
        ARUtils.assertIsTrue(arCallEvent != null);
        this.arCallEvent = arCallEvent;
    }

    /**
     * 加载本地摄像头
     * @param render 底层视频渲染对象
     * @return 打开本地预览返回值：0/1/2/3：没哟相机权限/打开成功/打开相机失败/相机已打开， 未释放
     */
    public int setLocalVideoCapturer(final long render) {
        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int permission = PermissionChecker.checkSelfPermission(ARCallEngine.Inst().context(), CAMERA);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    if (mVideoCapturer == null) {
                        mCameraId = 0;
                        String cameraDeviceName = CameraEnumerationAndroid.getDeviceName(mCameraId);
                        String frontCameraDeviceName =
                                CameraEnumerationAndroid.getNameOfFrontFacingDevice();
                        int numberOfCameras = CameraEnumerationAndroid.getDeviceCount();
                        if (numberOfCameras > 1 && frontCameraDeviceName != null && ARCallEngine.Inst().getARCallOption().isDefaultFrontCamera()) {
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
                        nativeSetVideoProfileMode(ARCallEngine.Inst().getARCallOption().getVideoProfile().level);
                        nativeSetVideoFpsProfile(ARCallEngine.Inst().getARCallOption().getVideoFps().level);
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
     * 重启本地摄像头
     * @param lRender 底层视频渲染对象
     * @return 打开本地预览返回值：0/1/2/3：没哟相机权限/打开成功/打开相机失败/相机已打开， 未释放
     */
    public int restartLocalVideoCapturer(final long lRender) {
        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (ARCallKit.this) {
                    int ret = 0;
                    int permission = ActivityCompat.checkSelfPermission(ARCallEngine.Inst().context(), CAMERA);
                    if (mVideoCapturer != null) {
                        try {
                            mVideoCapturer.stopCapture();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //nativeSetVideoCapturer(null, 0);
                        mVideoCapturer = null;
                    }
                    if (permission == PackageManager.PERMISSION_GRANTED) {
                        // We don't have permission so prompt the user
                        if (mVideoCapturer == null) {
                            mCameraId = 0;
                            String cameraDeviceName = CameraEnumerationAndroid.getDeviceName(mCameraId);
                            String frontCameraDeviceName =
                                    CameraEnumerationAndroid.getNameOfFrontFacingDevice();
                            int numberOfCameras = CameraEnumerationAndroid.getDeviceCount();
                            if (numberOfCameras > 1 && frontCameraDeviceName != null && ARCallEngine.Inst().getARCallOption().isDefaultFrontCamera()) {
                                cameraDeviceName = frontCameraDeviceName;
                                mCameraId = 1;
                            }
                            Log.d(TAG, "Opening camera: " + cameraDeviceName);
                            mVideoCapturer = VideoCapturerAndroid.create(cameraDeviceName, null);
                            if (mVideoCapturer == null) {
                                Log.e("sys", "Failed to open camera");
                                LooperExecutor.exchange(result, 2);
                            }
                            nativeSetVideoProfileMode(ARCallEngine.Inst().getARCallOption().getVideoProfile().level);
                            nativeSetVideoFpsProfile(ARCallEngine.Inst().getARCallOption().getVideoFps().level);
                            nativeSetVideoCapturer(mVideoCapturer, lRender);
                            LooperExecutor.exchange(result, 1);
                        } else {
                            LooperExecutor.exchange(result, 3);
                        }
                    } else {
                        LooperExecutor.exchange(result, 0);
                    }
                }
            }
        });
        return LooperExecutor.exchange(result, 0);
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
    public void setLocalVideoBitrate(final int bitrate) {
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
    public void setLocalVideoFps(final int fps) {
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
                int permission = ActivityCompat.checkSelfPermission(ARCallEngine.Inst().context(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
                int permission = ActivityCompat.checkSelfPermission(ARCallEngine.Inst().context(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
     * 停止相机预览
     */
    private int removeVideoCapture() {
        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (ARCallKit.this) {
                    if (mVideoCapturer != null) {
                        nativeSetVideoCapturer(null, 0);
                        try {
                            mVideoCapturer.stopCapture();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mVideoCapturer = null;
                        LooperExecutor.exchange(result, 1);
                    }
                }
            }
        });
        return LooperExecutor.exchange(result, 0);
    }

    /**
     * 上线
     *
     * @param userId 用户的userid
     * @return 上线返回值：ture/false 上线成功/上线失败
     */
    public boolean turnOn(final String userId) {
        final Exchanger<Boolean> result = new Exchanger<Boolean>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeSetDeviceInfo(ARCallEngine.Inst().getDeviceInfo());
                boolean ret = nativeTurnOn(userId, "", "", "");
                LooperExecutor.exchange(result, ret);
                isTurnOff = false;
            }
        });
        return LooperExecutor.exchange(result, false);
    }

    /**
     * 下线
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
     * 发起呼叫
     *
     * @param userId   用户的userid
     * @param callMode 呼叫的类型（ARCallMode）
     * @param userData 呼叫时的自定义数据
     * @return 呼叫结果；-5/-4/-3/-2/-1/0/1:userId为空字符串/正在通话中，呼叫失败/MemberList为空/不能自己呼叫自己/操作频繁/呼叫失败（没有RECORD_AUDIO权限）/呼叫成功
     */
    private int makeCall(final String userId, final ARCallMode callMode, final String userData) {
        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int ret = 0;
                int permission = ActivityCompat.checkSelfPermission(ARCallEngine.Inst().context(), RECORD_AUDIO);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    // We have permission granted to the user
                    ret = nativeMakeCall(userId, callMode.level, userData, "");
                    if (ret == 0) {
                        ret = 1;
                    }
                } else {
                    ret = 0;
                }

                LooperExecutor.exchange(result, ret);
            }
        });
        return LooperExecutor.exchange(result, 0);
    }
    /**
     * 设置客服是否可用
     * @param enable
     */
    public void setAvalible(final boolean enable){
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeSetAvalible(enable);
            }
        });
    }

    /**
     *
     * @param channelId 频道号
     */
    public void setAsClerk(final String channelId, final ARClertOption arClertOption){
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeSetAsClerk(arClertOption.area, channelId,arClertOption.business,arClertOption.level);
            }
        });
    }

    //呼叫个人
    public int makeCallUser(final String userId, final ARUserOption option){
        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int ret = 0;
                int permission = ActivityCompat.checkSelfPermission(ARCallEngine.Inst().context(), RECORD_AUDIO);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    // We have permission granted to the user
                    ret = nativeMakeCall(userId, option.callMode.level, option.userData, "");
                    if (ret == 0) {
                        ret = 1;
                    }
                } else {
                    ret = 0;
                }
                LooperExecutor.exchange(result, ret);
            }
        });
        return LooperExecutor.exchange(result, 0);
    }

    //呼叫群组
    public int makeCallGroup(final String groupId, final ARGroupOption groupOption) {
        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int ret = 0;
                int permission = ActivityCompat.checkSelfPermission(ARCallEngine.Inst().context(), RECORD_AUDIO);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    // We have permission granted to the user
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("MemberList", groupOption.userArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ret = nativeMakeCall(groupId, groupOption.callMode.level, groupOption.userData, jsonObject.toString());
                    if (ret == 0) {
                        ret = 1;
                    }
                } else {
                    ret = 0;
                }

                LooperExecutor.exchange(result, ret);
            }
        });
        return LooperExecutor.exchange(result, 0);
    }

    //呼叫客服
    public int makeCallQueue(final String queueId, final ARQueueOption arQueueOption) {
        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int ret = 0;
                int permission = ActivityCompat.checkSelfPermission(ARCallEngine.Inst().context(), RECORD_AUDIO);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    // We have permission granted to the user
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("Level", arQueueOption.level);
                        jsonObject.put("Area", arQueueOption.area);
                        jsonObject.put("Business", arQueueOption.business);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ret = nativeMakeCall(queueId, arQueueOption.callMode.level, arQueueOption.userData, jsonObject.toString());
                    if (ret == 0) {
                        ret = 1;
                    }
                } else {
                    ret = 0;
                }

                LooperExecutor.exchange(result, ret);
            }
        });
        return LooperExecutor.exchange(result, 0);
    }

    /**
     * 发起群组呼叫
     *
     * @param meetId    群组会议id
     * @param callMode  呼叫的类型（ARCallMode）
     * @param userData  呼叫时的自定义数据
     * @param strExtend 群组成员列表
     * @return 呼叫结果；-5/-4/-3/-2/-1/0/1:userId为空字符串/正在通话中，呼叫失败/MemberList为空/不能自己呼叫自己/操作频繁/呼叫失败（没有RECORD_AUDIO权限）/呼叫成功
     */
    private int makeCall(final String meetId, final ARCallMode callMode, final String userData, final String strExtend) {
        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int ret = 0;
                int permission = ActivityCompat.checkSelfPermission(ARCallEngine.Inst().context(), RECORD_AUDIO);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    // We have permission granted to the user
                    ret = nativeMakeCall(meetId, callMode.level, userData, strExtend);
                    if (ret == 0) {
                        ret = 1;
                    }
                } else {
                    ret = 0;
                }

                LooperExecutor.exchange(result, ret);
            }
        });
        return LooperExecutor.exchange(result, 0);
    }

    /**
     * 邀请用户
     *
     * @param userId 用户的userid
     * @return 邀请用户；0/1:失败（没有RECORD_AUDIO权限）/成功
     */
    public int inviteCall(final String userId) {
        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int ret = 0;
                int permission = ActivityCompat.checkSelfPermission(ARCallEngine.Inst().context(), RECORD_AUDIO);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    // We have permission granted to the user
                    nativeInviteCall(userId);
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
     * 接受呼叫
     *
     * @param userId 用户的userid
     * @return 接受呼叫结果；0/1:失败（没有RECORD_AUDIO权限）/成功
     */
    public int accpetCall(final String userId) {
        final Exchanger<Integer> result = new Exchanger<Integer>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int ret = 0;
                int permission = ActivityCompat.checkSelfPermission(ARCallEngine.Inst().context(), RECORD_AUDIO);
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
     * 拒绝呼叫
     *
     * @param userId 用户的userid
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
     * 挂断呼叫
     *
     * @param userId 用户的userid
     */
    public void endCall(final String userId) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeEndCall(userId);
//                if (mVideoCapturer != null) {
//                    nativeSetVideoCapturer(null, 0);
//                    mVideoCapturer = null;
//                }
            }
        });
    }

    /**
     * 停止预览
     */
    public void stopCapturer() {
        final Exchanger<Boolean> result = new Exchanger<Boolean>();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (mVideoCapturer != null) {
                    try {
                        mVideoCapturer.stopCapture();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mVideoCapturer = null;
                }
                nativeSetVideoCapturer(null, 0);
                LooperExecutor.exchange(result, true);
            }
        });
        LooperExecutor.exchange(result, false);
    }

    /**
     * 呼叫手机号
     */
    public void switchToPstn() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeSwitchToPstn();
                if (mVideoCapturer != null) {
                    nativeSetVideoCapturer(null, 0);
                    mVideoCapturer = null;
                }
            }
        });
    }

    /**
     * 呼叫分机号
     */
    public void switchToExtension() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeSwitchToExtension();
                if (mVideoCapturer != null) {
                    nativeSetVideoCapturer(null, 0);
                    mVideoCapturer = null;
                }
            }
        });
    }

    /**
     * 设置远端视频显示
     *
     * @param peerId
     * @param render
     */
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
     * 设置Zoom模式
     *
     * @param mode 0:normal 1:single 2:driver
     */
    public void setZoomMode(final ARMeetZoomMode mode) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                nativeSetZoomMode(mode.type);
            }
        });
    }

    /**
     * 设置页数
     *
     * @param nPages
     */
    public void setZoomPage(final int nPages) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (null != mVideoCapturer) {
                    nativeSetZoomPage(nPages);
                }
            }
        });
    }

    /**
     * 设置当前页数id及显示个数
     *
     * @param nIdx
     * @param showNum
     */
    public void setZoomPageIdx(final int nIdx, final int showNum) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (null != mVideoCapturer) {
                    nativeSetZoomPageIdx(nIdx, showNum);
                }
            }
        });
    }

    /**
     * Jni interface
     */
    private native long nativeCreate(Object obj);

    private native void nativeSetUserToken(String strUserToken);

    private native void nativeSetAsClerk(String strArea, String strCallId, String strBussiness, int nLevel);

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

    private native int nativeMakeCall(String userId, int callMode, String userData, String strExtend);

    private native void nativeAccpetCall(String userId);

    private native void nativeRejectCall(String userId);

    private native void nativeInviteCall(String userId);

    private native void nativeEndCall(String userId);

    private native void nativeSwitchToPstn();

    private native void nativeSwitchToExtension();

    private native void nativeSwtichToAudioMode();

    private native void nativeSendUserMessage(String userId, String strMessage);

    private native void nativeSetAvalible(boolean bEnable);

    private native void nativeSetVideoCapturer(VideoCapturer capturer, long nativeRenderer);

    private native void nativeSetRTCVideoRender(String strDevID, long nativeRenderer);

    private native void nativeSetZoomMode(int nMode/*0:normal 1:single 2:driver*/);

    private native void nativeSetZoomPage(int nPage);

    private native void nativeSetZoomPageIdx(int nIdx, int nShowNum);

    private native void nativeDestroy();

    /**
     * 回调对象
     */
    private ARCallHelper mCallHelper = new ARCallHelper() {
        @Override
        public void onConnected() {
            isTurnOff = false;
            if (null != arCallEvent) {
                arCallEvent.onConnected();
            }
        }

        @Override
        public void onDisconnect(int nErrCode) {
            isTurnOff = true;
            if (null != arCallEvent) {
                arCallEvent.onDisconnect(nErrCode);
            }
        }

        @Override
        public void onRTCMakeCall(String strMeetId, String strPeerUserId, int callMode, String userData, String strExtend) {
            if (null != arCallEvent) {
                arCallEvent.onRTCMakeCall(strMeetId, strPeerUserId, ARCallMode.getObject(callMode), userData, strExtend);
            }
        }

        @Override
        public void onRTCAcceptCall(String strPeerUserId) {
            if (null != arCallEvent) {
                arCallEvent.onRTCAcceptCall(strPeerUserId);
            }
        }

        @Override
        public void onRTCRejectCall(String strPeerUserId, int errCode) {
            if (null != arCallEvent) {
                arCallEvent.onRTCRejectCall(strPeerUserId, errCode);
            }
        }

        @Override
        public void onRTCEndCall(String strPeerUserId, int errCode) {
            if (null != arCallEvent) {
                arCallEvent.onRTCEndCall(strPeerUserId, errCode);
            }
        }

        @Override
        public void onRTCSipSupport(boolean bPstn, boolean bExtension, boolean bNull) {
            if (null != arCallEvent) {
                arCallEvent.onRTCSipSupport(bPstn, bExtension, bNull);
            }
        }

        @Override
        public void onRTCSwithToAudioMode() {
            if (null != arCallEvent) {
                arCallEvent.onRTCSwithToAudioMode();
            }
        }

        @Override
        public void onRTCUserMessage(String strPeerUserId, String strMessage) {
            if (null != arCallEvent) {
                arCallEvent.onRTCUserMessage(strPeerUserId, strMessage);
            }
        }

        @Override
        public void onRTCOpenVideoRender(String strPeerUserId, String strVidRenderId, String strUserData) {
            if (null != arCallEvent) {
                arCallEvent.onRTCOpenRemoteVideoRender(strPeerUserId, strVidRenderId, strUserData);
            }
        }

        @Override
        public void onRTCCloseVideoRender(String strPeerUserId, String strVidRenderId) {
            if (null != arCallEvent) {
                arCallEvent.onRTCCloseRemoteVideoRender(strPeerUserId, strVidRenderId);
            }
        }

        @Override
        public void onRTCOpenAudioTrack(String strPeerUserId, String strUserData) {
            if (null != arCallEvent) {
                arCallEvent.onRTCOpenRemoteAudioTrack(strPeerUserId, strUserData);
            }
        }

        @Override
        public void onRTCCloseAudioTrack(String strPeerUserId) {
            if (null != arCallEvent) {
                arCallEvent.onRTCCloseRemoteAudioTrack(strPeerUserId);
            }
        }

        @Override
        public void onRTCAudioLevel(String strPeerUserId, int nLevel, int nShowTime) {
            if (null != arCallEvent) {
                if (strPeerUserId.equals("RtcPublisher")) {
                    arCallEvent.onRTCLocalAudioActive(nLevel, nShowTime);
                } else {
                    arCallEvent.onRTCRemoteAudioActive(strPeerUserId, nLevel, nShowTime);
                }
            }
        }

        @Override
        public void onRTCNetworkStatus(String strPeerUserId, int nNetSpeed, int nPacketLost) {
            if (null != arCallEvent) {
                ARNetQuality netQuality = null;
                if (nPacketLost <= 1) {
                    netQuality = ARNetQuality.ARNetQualityExcellent;
                } else if (nPacketLost > 1 && nPacketLost <= 3) {
                    netQuality = ARNetQuality.ARNetQualityGood;
                } else if (nPacketLost > 3 && nPacketLost <= 5) {
                    netQuality = ARNetQuality.ARNetQualityAccepted;
                } else if (nPacketLost > 5 && nPacketLost <= 10) {
                    netQuality = ARNetQuality.ARNetQualityBad;
                } else {
                    netQuality = ARNetQuality.ARNetQualityVBad;
                }
                if (strPeerUserId.equals("RtcPublisher")) {
                    arCallEvent.onRTCLocalNetworkStatus(nNetSpeed, nPacketLost, netQuality);
                } else {
                    arCallEvent.onRTCRemoteNetworkStatus(strPeerUserId, nNetSpeed, nPacketLost, netQuality);
                }
            }
        }

        @Override
        public void onRTCZoomPageInfo(int nZoomMode, int nAllPages, int nCurPage, int nAllRender, int nScrnBeginIdx, int nNum) {
            if (null != arCallEvent) {
                arCallEvent.onRTCZoomPageInfo(ARMeetZoomMode.getObject(nZoomMode), nAllPages, nCurPage, nAllRender, nScrnBeginIdx, nNum);
            }
        }

        @Override
        public void onRTCUserCome(String userId, String vidRenderId, String userData) {
            if (null != arCallEvent) {
                arCallEvent.onRTCUserCome(userId, vidRenderId, userData);
            }
        }

        @Override
        public void onRTCUserOut(String userId, String vidRenderId) {
            if(null != arCallEvent) {
                arCallEvent.onRTCUserOut(userId, vidRenderId);
            }
        }

        @Override
        public void onRTCUserCTIStatus(int nQueueNum) {
            if(null != arCallEvent) {
                arCallEvent.onRTCUserCTIStatus(nQueueNum);
            }
        }

        @Override
        public void onRTCClerkCTIStatus(int nQueueNum, int nAllClerk, int nWorkingClerk) {
            if(null != arCallEvent) {
                arCallEvent.onRTCClerkCTIStatus(nQueueNum, nAllClerk,nWorkingClerk);
            }
        }

        @Override
        public void onRTCAVStatus(String strRTCPeerId, boolean bAudio, boolean bVideo) {
            if(null != arCallEvent) {
                arCallEvent.onRTCAVStatus(strRTCPeerId, bAudio,bVideo);
            }
        }
    };
}
