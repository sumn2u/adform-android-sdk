package com.adform.adformdemo.support;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;
import android.webkit.WebView;
import android.util.Log;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.adform.adformdemo.R;

/**
 * Created by mariusmerkevicius on 4/30/15.
 */
public class EmptyWebviewFragment extends Fragment {
    public static final String INSTANCE_TEXT = "TEXT";
    private String text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_empty_webview, container, false);
        if (savedInstanceState != null)
            text = savedInstanceState.getString(INSTANCE_TEXT);
        if (text != null)
            ((TextView) rootView.findViewById(R.id.text_view)).setText(text);

        WebView webView = (WebView) rootView.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

    
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        //webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler,SslError error) {
                
                handler.proceed();
            }
        
        
        });
        webView.loadUrl("http://google.com");

        return rootView;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INSTANCE_TEXT, text);
    }

    //region Classes

    private class WebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }
    }

    //endregion

}
