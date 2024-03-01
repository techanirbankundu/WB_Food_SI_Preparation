package com.faststudy.foodsipre;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class MainActivity2 extends AppCompatActivity {
    private AdView mAdView;
    WebView webView;
    private InterstitialAd mInterstitialAd;
    private InterstitialAd mInterstitialAdonPageLoad;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adview2);
        AdRequest adRequest = new AdRequest.Builder().build();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE ,WindowManager.LayoutParams.FLAG_SECURE);
        mAdView.loadAd(adRequest);
        webView = findViewById(R.id.web);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportMultipleWindows(true);
        String indc = getIntent().getExtras().getString("num");
        if (indc.equals("1")){
            webView.loadUrl("https://faststudy.xyz/food_si/gen.php");
        } else if (indc.equals("0")) {
            webView.loadUrl("https://faststudy.xyz/food_si/old.php");
        }
        else if (indc.equals("2")) {
            webView.loadUrl("https://faststudy.xyz/food_si/math.php");
        }
        else if (indc.equals("3")) {
            webView.loadUrl("https://faststudy.xyz/food_si/mock.php");
        }
        else if (indc.equals("4")) {
            webView.loadUrl("https://faststudy.xyz/food_si/spec.php");
        }else if (indc.startsWith("https://")){
            webView.loadUrl(indc);
        }else if (indc.equals("5")) {
            webView.loadUrl("https://faststudy.xyz/food_si/cur.php");
        }
        else if (indc.startsWith("ad_link@")) {
            String[] arrOfStr = indc.split("@", 2);
            webView.loadUrl(arrOfStr[1]);
        }


        loadads();
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (webView.getUrl().startsWith("https://faststudy.xyz/food_si/view.php")){
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(MainActivity2.this);
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(TAG, "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                mInterstitialAd = null;
                                loadads();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(TAG, "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                            }
                        });
                    }
                }
                super.onPageStarted(view, url, favicon);
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){


            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity2.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdClicked() {
                            // Called when a click is recorded for an ad.
                            Log.d(TAG, "Ad was clicked.");
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            mInterstitialAd = null;
                            loadads();
                            WebView.HitTestResult result = view.getHitTestResult();
                            String data = result.getExtra();
                            Context context = view.getContext();
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                            context.startActivity(browserIntent);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when ad fails to show.
                            Log.e(TAG, "Ad failed to show fullscreen content.");
                            mInterstitialAd = null;
                        }
                    });
                }else {
                    WebView.HitTestResult result = view.getHitTestResult();
                    String data = result.getExtra();
                    Context context = view.getContext();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                    context.startActivity(browserIntent);
                }
                return false;
            }
        });



    }




    @Override
    public void onBackPressed() {
        loadads();
        if (webView.canGoBack()){
            webView.goBack();
        }else {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MainActivity2.this);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        // Called when a click is recorded for an ad.
                        Log.d(TAG, "Ad was clicked.");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        mInterstitialAd = null;
                        MainActivity2.super.onBackPressed();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        Log.e(TAG, "Ad failed to show fullscreen content.");
                        mInterstitialAd = null;
                    }
                });
            } else {
                super.onBackPressed();
            }
        }

    }

    private void loadads() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,getString(R.string.act2_inter), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;


                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
    }


}