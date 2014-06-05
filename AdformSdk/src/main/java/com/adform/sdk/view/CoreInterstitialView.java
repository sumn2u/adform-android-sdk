package com.adform.sdk.view;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import com.adform.sdk.mraid.properties.MraidDeviceIdProperty;
import com.adform.sdk.resources.AdDimension;
import com.adform.sdk.resources.CloseImageView;
import com.adform.sdk.utils.AdformEnum;
import com.adform.sdk.view.base.BaseCoreContainer;
import com.adform.sdk.view.base.BaseInnerContainer;
import com.adform.sdk.view.inner.InnerInterstitialView;

/**
 * Created by mariusm on 27/05/14.
 */
public class CoreInterstitialView extends BaseCoreContainer implements View.OnClickListener {

    private CloseImageView mCloseImageView;

    public interface CoreInterstitialListener {
        public void onAdClose();
        public void onAdOrientationChange(int orientation);
        public void onAdShown();
    }

    private CoreInterstitialListener mListener;

    public CoreInterstitialView(Context context, BaseInnerContainer innerContainer) {
        this(context, null, 0, innerContainer);
    }

    public CoreInterstitialView(Context context) {
        this(context, null, 0, null);
    }

    public CoreInterstitialView(Context context, AttributeSet attrs) {
        this(context, attrs, 0, null);
    }

    public CoreInterstitialView(Context context, AttributeSet attrs, int defStyle, BaseInnerContainer innerContainer) {
        super(context, attrs, defStyle, innerContainer);
        if (mContext instanceof CoreInterstitialListener)
            mListener = (CoreInterstitialListener)mContext;
        setAnimating(false);
        mCloseImageView = new CloseImageView(mContext);
        mCloseImageView.setVisible(true);
        mCloseImageView.setOnClickListener(this);
        addView(mCloseImageView, mCloseImageView.getStandardLayoutParams());
        mCloseImageView.bringToFront();
    }

    public void showContent(String content) {
        // Loaded content will always be loaded and mraid type
        setViewState(AdformEnum.VisibilityGeneralState.LOAD_SUCCESSFUL);
        mInnerContainer.showContent(content);
    }

    @Override
    protected BaseInnerContainer getInnerView() {
        if (mInnerContainer == null) {
            mInnerContainer = new InnerInterstitialView(mContext);
        }
        return mInnerContainer;
    }

    @Override
    protected AdDimension initAdDimen() {
        return new AdDimension(mContext);
    }

    @Override
    protected void onVisibilityCallback(boolean isVisible) {
        mInnerContainer.getMraidBridge().changeVisibility(isVisible, false);
    }

    @Override
    public MraidDeviceIdProperty getDeviceId() {
        return null;
    }

    @Override
    public String getUserAgent() {
        return mInnerContainer.getUserAgent();
    }

    @Override
    public void onContentRestore(boolean state) {}

    @Override
    public void onContentRender() {
        if (mListener != null)
            mListener.onAdShown();
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onAdClose();
        }
    }

    public void setListener(CoreInterstitialListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onMraidClose() {
        // Closing functionality is passed to its listener
        if (mListener != null)
            mListener.onAdClose();
    }

    @Override
    public void onMraidSetOrientation(boolean allowOrientationChange, AdformEnum.ForcedOrientation forcedOrientation) {
        switch (forcedOrientation) {
            case NONE:{
                if (allowOrientationChange)
                    mListener.onAdOrientationChange(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                else
                    mListener.onAdOrientationChange(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                break;
            }
            case LANDSCAPE:{
                    mListener.onAdOrientationChange(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            }
            case PORTRAIT:{
                    mListener.onAdOrientationChange(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            }
            default:
                mListener.onAdOrientationChange(Configuration.ORIENTATION_UNDEFINED);
        }
    }

    @Override
    public void onMraidUseCustomClose(boolean shouldUseCustomClose) {
        mCloseImageView.setVisible(shouldUseCustomClose);
    }

    @Override
    public void onMraidExpand() {
        // Nothing to do here
    }
}
