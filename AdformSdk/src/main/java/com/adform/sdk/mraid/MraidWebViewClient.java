package com.adform.sdk.mraid;

import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.adform.sdk.mraid.commands.MraidBaseCommand;
import com.adform.sdk.resources.MraidJavascript;
import com.adform.sdk.utils.JsLoadBridge;
import com.adform.sdk.view.base.BaseInnerContainer;
import com.adform.sdk.view.inner.AdWebView;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mariusm on 06/05/14.
 */
public class MraidWebViewClient extends WebViewClient {
    private AdWebView mWebView;

    public MraidWebViewClient() {}

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Uri uri = Uri.parse(url);

        // Note that scheme will be null when we are passed a relative Uri
        String scheme = uri.getScheme();

        if (JsLoadBridge.NATIVE_JS_INTERFACE.equals(scheme)) {
            return true;
        }

        if (BaseInnerContainer.MRAID_JS_INTERFACE.equals(scheme)) {
            tryCommand(URI.create(url));
            return true;
        }

        return false;
    }

    private boolean tryCommand(URI uri) {
        String commandType = uri.getHost();
        List<NameValuePair> list = URLEncodedUtils.parse(uri, "UTF-8");
        Map<String, String> params = new HashMap<String, String>();
        for (NameValuePair pair : list) {
            params.put(pair.getName(), pair.getValue());
        }

        MraidBaseCommand command = MraidCommandFactory.create(commandType, params, mWebView);
        if (command != null) {
            command.execute();
            mWebView.fireNativeCommandCompleteEvent(commandType);
            return true;
        }
        return false;
    }

    public void setWebView(AdWebView webView) {
        this.mWebView = webView;
    }
}