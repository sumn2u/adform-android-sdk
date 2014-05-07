package com.adform.sample.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.adform.sdk2.utils.Utils;
import com.adform.sdk2.view.CoreAdView;

public class MainActivity extends Activity implements CoreAdView.CoreAdViewListener {

    private View mPlaceHolder;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CoreAdView mAdView = (CoreAdView) findViewById(R.id.custom_ad_view);
        mPlaceHolder = findViewById(R.id.place_holder);
        mPlaceHolder.setVisibility(
                (mAdView.getViewState() == CoreAdView.ViewState.SHOWN)?View.VISIBLE:View.GONE);
    }

    @Override
    public void onAdVisibilityChange(final CoreAdView.ViewState viewState) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPlaceHolder.setVisibility(
                        (viewState == CoreAdView.ViewState.SHOWN) ? View.VISIBLE : View.GONE);
            }
        });
    }
}
