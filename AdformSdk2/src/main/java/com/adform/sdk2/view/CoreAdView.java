package com.adform.sdk2.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.adform.sdk2.network.app.entities.entities.AdServingEntity;
import com.adform.sdk2.network.app.services.AdService;
import com.adform.sdk2.network.base.ito.network.NetworkError;
import com.adform.sdk2.utils.SlidingManager;
import com.adform.sdk2.utils.Utils;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by mariusm on 24/04/14.
 * Base view that should be implemented when adding a banner
 */
public class CoreAdView extends RelativeLayout implements Observer,
        SlidingManager.SliderableWidget, BannerView.BannerViewListener {


    private Context mContext;

    private AdService mAdService;
    /** Variable used to save time counter when reinitializing AdService */
    private int mLastTime = -1;
    /** Bundle that packs AdService last state when saving view instance */
    private Bundle mServiceInstanceBundle;

    private SlidingManager mSlidingManager;
    private BannerView mBannerView;
    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                stopService();
            }
            else if (intent != null && intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                startService();
            }
        }
    };

    public CoreAdView(Context context) {
        this(context, null);
    }

    public CoreAdView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoreAdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mSlidingManager = new SlidingManager(this);
        setBackgroundResource(android.R.color.transparent);

        final float scale = mContext.getResources().getDisplayMetrics().density;
        ViewGroup.LayoutParams params = new RelativeLayout.LayoutParams(
                (int)(Utils.getWidthDeviceType(mContext) * scale+0.5f),
                (int)(Utils.getHeightDeviceType(mContext) * scale+0.5f));
        setLayoutParams(params);

        mBannerView = new BannerView(mContext);
        mBannerView.setListener(this);
        // TODO: Change this to something nicer. This must be binded, as this lets instance to be saved
        mBannerView.setId(156554);
        addView(mBannerView);

        setVisibility(INVISIBLE);
    }

    /** An update from configuration json */
    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof NetworkError)
            return;
        // Loading banner
        if (data != null) {
            String content = ((AdServingEntity) data).getAdEntity().getTagDataEntity().getSrc();
            mBannerView.loadContent(content);
        }
    }

    @Override
    public void onContentLoadSuccessful() {
        mSlidingManager.turnOn();
    }

    @Override
    public void onContentRestore() {
        mSlidingManager.turnOnImmediate();
    }

    @Override
    public void onNewContentLoad() {}

    @Override
    public void onContentLoadFailed() {}

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mScreenStateReceiver, filter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mContext.unregisterReceiver(mScreenStateReceiver);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            if (mServiceInstanceBundle == null) {
                startService();
            } else {
                resumeService();
            }
        } else {
            stopService();
        }
    }

    private void stopService() {
        mLastTime = mAdService.getTimePassed();
        mAdService.deleteObserver(this);
        mAdService.stopService();
    }

    private void resumeService() {
        mAdService = new AdService();
        mAdService.addObserver(this);
        mAdService.restoreInstanceWithBundle(mServiceInstanceBundle, mLastTime);
        mServiceInstanceBundle = null;
    }

    private void startService() {
        mAdService = new AdService();
        mAdService.addObserver(this);
        if (mBannerView != null && mBannerView.getTimesLoaded() > 0)
            resumeService();
        else
            mAdService.startService();
    }


    /** Widget height return for SliderManager */
    @Override
    public float getWidgetHeight() {
        return getHeight();
    }

    // TODO: AdService should not be packed outside, but inside, though at the moment this does work properly

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.saveBundle = mAdService.getSaveInstanceBundle();
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState savedState = (SavedState)state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mServiceInstanceBundle = savedState.saveBundle;
    }

    private static class SavedState extends BaseSavedState {
        public Bundle saveBundle;

        public SavedState(Parcel source) {
            super(source);
            saveBundle = source.readBundle();
        }
        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeBundle(saveBundle);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }


}