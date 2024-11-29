package com.app.liverooms.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.databinding.DataBindingUtil;

import com.app.liverooms.R;
import com.app.liverooms.databinding.ActivityWebBinding;
import com.app.liverooms.retrofit.Const;

public class WebActivity extends BaseActivity {

    ActivityWebBinding binding;
    String website;
    String title;
    private boolean loadingFinished;
    private boolean redirect;

    public static void open(Context context, String title, String url) {
        context.startActivity(new Intent(context, WebActivity.class).putExtra(Const.TITLE, title).putExtra(Const.URL, url));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web);


        Intent intent = getIntent();
        if (intent != null) {
            website = intent.getStringExtra(Const.URL);
            title = intent.getStringExtra(Const.TITLE);
            binding.tvtitle.setText(title);
            loadUrl(website);
        }


    }

    private void loadUrl(String url) {
        if (url != null) {
            binding.webview.loadUrl(url);

            binding.webview.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                    if (!loadingFinished) {
                        redirect = true;
                    }

                    loadingFinished = false;
                    view.loadUrl(urlNewString);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                    loadingFinished = false;
                    //SHOW LOADING IF IT ISNT ALREADY VISIBLE
                    binding.pd.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (!redirect) {
                        loadingFinished = true;
                        binding.pd.setVisibility(View.GONE);
                    }

                    if (loadingFinished && !redirect) {
                        //HIDE LOADING IT HAS FINISHED
                        binding.pd.setVisibility(View.GONE);
                    } else {
                        redirect = false;
                        binding.pd.setVisibility(View.GONE);
                    }

                }
            });

        }

    }
}