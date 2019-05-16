package org.ar.arp2p.widgets;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import org.ar.arp2p.R;
import org.anyrtc.common.utils.ScreenUtils;
import org.webrtc.EglBase;
import org.webrtc.PercentFrameLayout;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Eric on 2016/7/26.
 */
public class RTCVideoView implements RTCViewHelper, View.OnTouchListener {
    private static final int SUB_X = 72;
    private static final int SUB_Y = 2;
    private static final int SUB_WIDTH = 24;
    private static final int SUB_HEIGHT = 20;

    private static int mScreenWidth;
    private static int mScreenHeight;

    private Context mContext;


    protected static class VideoView {
        public String strPeerId;
        public int index;
        public int x;
        public int y;
        public int w;
        public int h;
        public PercentFrameLayout mLayout = null;
        public SurfaceViewRenderer mView = null;
        public VideoRenderer mRenderer = null;
        private RelativeLayout layoutCamera = null;

        public VideoView(String strPeerId, Context ctx, EglBase eglBase, int index, int x, int y, int w, int h) {
            this.strPeerId = strPeerId;
            this.index = index;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;

            mLayout = new PercentFrameLayout(ctx);
//            mLayout.setBackgroundResource(R.drawable.background);
            mLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            View view = View.inflate(ctx, R.layout.layout_top_right, null);
            mView = (SurfaceViewRenderer) view.findViewById(R.id.suface_view);
            layoutCamera = (RelativeLayout) view.findViewById(R.id.layout_camera);
            mView.init(eglBase.getEglBaseContext(), null);
            mView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            mLayout.addView(view);
        }

        public Boolean Fullscreen() {
            return w == 100 || h == 100;
        }

        public Boolean Hited(int px, int py) {
            if (!Fullscreen()) {
                int left = x * mScreenWidth / 100;
                int top = y * mScreenHeight / 100;
                int right = (x + w) * mScreenWidth / 100;
                int bottom = (y + h) * mScreenHeight / 100;
                if ((px >= left && px <= right) && (py >= top && py <= bottom)) {
                    return true;
                }
            }
            return false;
        }

        public void close() {
            mLayout.removeView(mView);
            mView.release();
            mView = null;
            mRenderer = null;
        }
    }

    private boolean mAutoLayout;
    private EglBase mRootEglBase;
    private RelativeLayout mVideoView;
    private VideoView mLocalRender;
    private HashMap<String, VideoView> mRemoteRenders;

    public RTCVideoView(RelativeLayout videoView, Context ctx, EglBase eglBase) {
        this.mContext = ctx;
        mAutoLayout = false;
        mVideoView = videoView;
        mRootEglBase = eglBase;
        mLocalRender = null;
        mRemoteRenders = new HashMap<>();
        mScreenWidth = ScreenUtils.getScreenWidth(mContext);
        mScreenHeight = ScreenUtils.getScreenHeight(mContext) - ScreenUtils.getStatusHeight(mContext);
    }

    private int GetVideoRenderSize() {
        int size = mRemoteRenders.size();
        if (mLocalRender != null) {
            size += 1;
        }
        return size;
    }

    /**
     * 切换本地图像和远程图像
     *
     * @param peerid 远程图像的peerid
     */
    public void SwitchLocalViewToOtherView(String peerid) {
        VideoView fullscrnView = mLocalRender;
        VideoView view1 = mRemoteRenders.get(peerid);
        int index, x, y, w, h;

        index = view1.index;
        x = view1.x;
        y = view1.y;
        w = view1.w;
        h = view1.h;

        view1.index = fullscrnView.index;
        view1.x = fullscrnView.x;
        view1.y = fullscrnView.y;
        view1.w = fullscrnView.w;
        view1.h = fullscrnView.h;

        fullscrnView.index = index;
        fullscrnView.x = x;
        fullscrnView.y = y;
        fullscrnView.w = w;
        fullscrnView.h = h;

        fullscrnView.mLayout.setPosition(fullscrnView.x, fullscrnView.y, fullscrnView.w, fullscrnView.h);
        view1.mLayout.setPosition(view1.x, view1.y, view1.w, view1.h);
        updateVideoLayout(view1, fullscrnView);
    }

    /**
     * 交换两个图像的位置
     *
     * @param peerid1 图像1的peerid
     * @param peerid2 图像2的peerid
     */
    public void SwitchViewByPeerId(String peerid1, String peerid2) {
        VideoView view1 = mRemoteRenders.get(peerid1);
        VideoView view2 = mRemoteRenders.get(peerid2);
        int index, x, y, w, h;
        index = view1.index;
        x = view1.x;
        y = view1.y;
        w = view1.w;
        h = view1.h;

        view1.index = view2.index;
        view1.x = view2.x;
        view1.y = view2.y;
        view1.w = view2.w;
        view1.h = view2.h;

        view2.index = index;
        view2.x = x;
        view2.y = y;
        view2.w = w;
        view2.h = h;

        view2.mLayout.setPosition(view2.x, view2.y, view2.w, view2.h);
        view1.mLayout.setPosition(view1.x, view1.y, view1.w, view1.h);
        updateVideoLayout(view1, view2);
    }

    private void SwitchViewToFullscreen(VideoView view1, VideoView fullscrnView) {
        int index, x, y, w, h;

        index = view1.index;
        x = view1.x;
        y = view1.y;
        w = view1.w;
        h = view1.h;

        view1.index = fullscrnView.index;
        view1.x = fullscrnView.x;
        view1.y = fullscrnView.y;
        view1.w = fullscrnView.w;
        view1.h = fullscrnView.h;

        fullscrnView.index = index;
        fullscrnView.x = x;
        fullscrnView.y = y;
        fullscrnView.w = w;
        fullscrnView.h = h;

        fullscrnView.mLayout.setPosition(fullscrnView.x, fullscrnView.y, fullscrnView.w, fullscrnView.h);
        view1.mLayout.setPosition(view1.x, view1.y, view1.w, view1.h);

        updateVideoLayout(view1, fullscrnView);
    }

    private void SwitchViewPosition(VideoView view1, VideoView view2) {
        int index, x, y, w, h;
        index = view1.index;
        x = view1.x;
        y = view1.y;
        w = view1.w;
        h = view1.h;

        view1.index = view2.index;
        view1.x = view2.x;
        view1.y = view2.y;
        view1.w = view2.w;
        view1.h = view2.h;

        view2.index = index;
        view2.x = x;
        view2.y = y;
        view2.w = w;
        view2.h = h;

        view1.mLayout.setPosition(view1.x, view1.y, view1.w, view1.h);
        view2.mLayout.setPosition(view2.x, view2.y, view2.w, view2.h);
        updateVideoLayout(view1, view2);
    }

    /**
     * 视频切换后更新视频的布局
     *
     * @param view1
     * @param view2
     */
    private void updateVideoLayout(VideoView view1, VideoView view2) {
        if (view1.Fullscreen()) {
            view1.mView.setZOrderMediaOverlay(false);
            view2.mView.setZOrderMediaOverlay(true);
            view1.mLayout.requestLayout();
            view2.mLayout.requestLayout();
            mVideoView.removeView(view1.mLayout);
            mVideoView.removeView(view2.mLayout);
            mVideoView.addView(view1.mLayout, -1);
            mVideoView.addView(view2.mLayout, 0);
        } else if (view2.Fullscreen()) {
            view1.mView.setZOrderMediaOverlay(true);
            view2.mView.setZOrderMediaOverlay(false);
            view2.mLayout.requestLayout();
            view1.mLayout.requestLayout();
            mVideoView.removeView(view1.mLayout);
            mVideoView.removeView(view2.mLayout);
            mVideoView.addView(view1.mLayout, 0);
            mVideoView.addView(view2.mLayout, -1);
        } else {
            view1.mLayout.requestLayout();
            view2.mLayout.requestLayout();
            mVideoView.removeView(view1.mLayout);
            mVideoView.removeView(view2.mLayout);
            mVideoView.addView(view1.mLayout, 0);
            mVideoView.addView(view2.mLayout, 0);
        }
    }

    /**
     * 切换第一个视频为全屏
     *
     * @param fullscrnView
     */
    private void SwitchIndex1ToFullscreen(VideoView fullscrnView) {
        VideoView view1 = null;
        if (mLocalRender != null && mLocalRender.index == 1) {
            view1 = mLocalRender;
        } else {
            Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, VideoView> entry = iter.next();
                VideoView render = entry.getValue();
                if (render.index == 1) {
                    view1 = render;
                    break;
                }
            }
        }
        if (view1 != null) {
            SwitchViewPosition(view1, fullscrnView);
        }
    }

    public void BubbleSortSubView(VideoView view) {
        if (mLocalRender != null && view.index + 1 == mLocalRender.index) {
            SwitchViewPosition(mLocalRender, view);
        } else {
            Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, VideoView> entry = iter.next();
                VideoView render = entry.getValue();
                if (view.index + 1 == render.index) {
                    SwitchViewPosition(render, view);
                    break;
                }
            }
        }
        if (view.index < mRemoteRenders.size()) {
            BubbleSortSubView(view);
        }
    }

    /**
     * 获取全屏的界面
     *
     * @return
     */
    private VideoView GetFullScreen() {
        if (mLocalRender.Fullscreen()) {
            return mLocalRender;
        }
        Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, VideoView> entry = iter.next();
            String peerId = entry.getKey();
            VideoView render = entry.getValue();
            if (render.Fullscreen())
                return render;
        }
        return null;
    }

    /**
     * Implements for AnyRTCViewEvents.
     */
    @Override
    public VideoRenderer OnRtcOpenLocalRender() {
        int size = GetVideoRenderSize();
        if (size == 0) {
            mLocalRender = new VideoView("localRender", mVideoView.getContext(), mRootEglBase, 0, 0, 0, 100, 100);
        } else {
            mLocalRender = new VideoView("localRender", mVideoView.getContext(), mRootEglBase, size, SUB_X, (100 - size * (SUB_HEIGHT + SUB_Y)), SUB_WIDTH, SUB_HEIGHT);
            mLocalRender.mView.setZOrderMediaOverlay(true);
        }
        mLocalRender.mView.setBackgroundResource(R.drawable.background);
        mVideoView.addView(mLocalRender.mLayout);
        mLocalRender.mLayout.setPosition(
                mLocalRender.x, mLocalRender.y, mLocalRender.w, mLocalRender.h);
        mLocalRender.mView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        mLocalRender.mRenderer = new VideoRenderer(mLocalRender.mView);
        mLocalRender.mLayout.setBackgroundResource(R.drawable.background);
        return mLocalRender.mRenderer;
    }

    @Override
    public void OnRtcRemoveLocalRender() {
        if (mLocalRender != null) {
            mLocalRender.close();
            mLocalRender.mRenderer = null;

            mVideoView.removeView(mLocalRender.mLayout);
            mLocalRender = null;
        }
    }

    @Override
    public VideoRenderer OnRtcOpenRemoteRender(final String strRtcPeerId) {
        VideoView remoteRender = mRemoteRenders.get(strRtcPeerId);
        if (remoteRender == null) {
            int size = GetVideoRenderSize();
            if (size == 0) {
                remoteRender = new VideoView(strRtcPeerId, mVideoView.getContext(), mRootEglBase, 0, 0, 0, 100, 100);
            } else {
                remoteRender = new VideoView(strRtcPeerId, mVideoView.getContext(), mRootEglBase, size, 4, 4, SUB_WIDTH, SUB_HEIGHT);
                remoteRender.mView.setZOrderMediaOverlay(true);
            }

            mVideoView.addView(remoteRender.mLayout);

            remoteRender.mLayout.setPosition(
                    remoteRender.x, remoteRender.y, remoteRender.w, remoteRender.h);
            remoteRender.mView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
            remoteRender.mRenderer = new VideoRenderer(remoteRender.mView);

            mRemoteRenders.put(strRtcPeerId, remoteRender);

            if (mRemoteRenders.size() == 1 && mLocalRender != null) {
                SwitchViewToFullscreen(remoteRender, mLocalRender);
            }
        }
        return remoteRender.mRenderer;
    }

    @Override
    public void OnRtcRemoveRemoteRender(String peerId) {
        VideoView remoteRender = mRemoteRenders.get(peerId);
        if (remoteRender != null) {
            if (remoteRender.Fullscreen()) {
                SwitchIndex1ToFullscreen(remoteRender);
            }
            if (mRemoteRenders.size() > 1 && remoteRender.index <= mRemoteRenders.size()) {
                BubbleSortSubView(remoteRender);
            }
            remoteRender.close();
            mVideoView.removeView(remoteRender.mLayout);
            mRemoteRenders.remove(peerId);
        }
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int startX = (int) event.getX();
            int startY = (int) event.getY();
            if (mLocalRender.Hited(startX, startY)) {
                return true;
            } else {
                Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, VideoView> entry = iter.next();
                    String peerId = entry.getKey();
                    VideoView render = entry.getValue();
                    if (render.Hited(startX, startY)) {
                        return true;
                    }
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            int startX = (int) event.getX();
            int startY = (int) event.getY();
            if (mLocalRender.Hited(startX, startY)) {
                SwitchViewToFullscreen(mLocalRender, GetFullScreen());
                return true;
            } else {
                Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, VideoView> entry = iter.next();
                    String peerId = entry.getKey();
                    VideoView render = entry.getValue();
                    if (render.Hited(startX, startY)) {
                        SwitchViewToFullscreen(render, GetFullScreen());
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public VideoRenderer OnRtcOpenPreViewRender(String strRtcPeerId){
        VideoView remoteRender = mRemoteRenders.get(strRtcPeerId);
        if (remoteRender == null) {
            int size = GetVideoRenderSize();
            if (size == 0) {
                remoteRender = new VideoView(strRtcPeerId, mVideoView.getContext(), mRootEglBase, 0, 0, 0, 100, 100);
            } else {
                remoteRender = new VideoView(strRtcPeerId, mVideoView.getContext(), mRootEglBase, size, (100-SUB_WIDTH)/2, 12, SUB_WIDTH, SUB_HEIGHT);
                remoteRender.mView.setZOrderMediaOverlay(true);
            }
            mVideoView.addView(remoteRender.mLayout);
            remoteRender.mLayout.setPosition(
                    remoteRender.x, remoteRender.y, remoteRender.w, remoteRender.h);
            remoteRender.mView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
            remoteRender.mRenderer = new VideoRenderer(remoteRender.mView);
            mRemoteRenders.put(strRtcPeerId, remoteRender);
        }
        return remoteRender.mRenderer;
    }


    public void preView2Normol() {

        Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, VideoView> entry = iter.next();
            VideoView render = entry.getValue();
            render.x=4;
            render.y=4;
            render.w=SUB_WIDTH;
            render.h=SUB_HEIGHT;
            if (mRemoteRenders.size() == 1 && mLocalRender != null) {
                SwitchViewToFullscreen(render, mLocalRender);
            }
        }
    }

    public void removeLocalRenderBg(){
        mLocalRender.mLayout.setBackgroundResource(0);
        mLocalRender.mView.setBackgroundResource(0);
    }
}
