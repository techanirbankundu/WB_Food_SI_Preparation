package com.faststudy.foodsipre;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
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
import com.google.android.material.snackbar.Snackbar;
import com.onesignal.OneSignal;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private AdView mAdView;
    Button bt1,bt2,bt3,bt4,bt5,bt6,conwp;
    private StringRequest mStringRequest;
    CardView adcard;
    ImageView imageView;
    TextView tv;
    private InterstitialAd mInterstitialAd;
    YouTubePlayerView youTubePlayerView;
    private static final String ONESIGNAL_APP_ID = "46665f45-683c-4606-82a4-cabdbc7e00db";
    String app_ver = "1.0.4";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        adcard = findViewById(R.id.ad_card);
        adcard.setVisibility(View.GONE);
        String url = "https://faststudy.xyz/food_si/";
        imageView = findViewById(R.id.introimage);

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        tv = findViewById(R.id.pp);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://faststudy.xyz/food_si/pp.html");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        conwp = findViewById(R.id.conwp);
        conwp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://wa.me/919123981296?text=Hi");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        //Notfication
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OneSignal.promptForPushNotifications();


        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            String img_link = response.getString("img_link");
                            String link = response.getString("link");
                            String s_ver = response.getString("version");
                            if (!app_ver.equals(s_ver)){
                                Dialog dialog = new Dialog(MainActivity.this);
                                dialog.setContentView(R.layout.update_dialog);
                                Button okbtn = dialog.findViewById(R.id.u_button);
                                dialog.setCancelable(false);
                                okbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.faststudy.foodsipre&hl=en");
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }
                                });
                                dialog.show();

                            }
                            if (status == "true" && img_link != " " && link != " "){
                                if (img_link.startsWith("https://")){
                                    Glide.with(MainActivity.this).load(img_link).into(imageView);
                                    adcard.setVisibility(View.VISIBLE);
                                    if (!link.equals("NOLINK")){
                                        if (link.startsWith("BROWSER@")){
                                            String[] arrOfStr = link.split("R@", 2);
                                            imageView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Uri uri = Uri.parse(arrOfStr[1]);
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                    startActivity(intent);
                                                }
                                            });
                                        }else {
                                            imageView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                                                    intent.putExtra("num", "ad_link@" + link);
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    }
                                }else{
                                    adcard.setVisibility(View.VISIBLE);
                                    imageView.setVisibility(View.GONE);
                                    youTubePlayerView.setVisibility(View.VISIBLE);
                                    youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                                        @Override
                                        public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                                            String videoId = img_link;
                                            youTubePlayer.loadVideo(videoId, 0);
                                        }
                                    });
                                }

                                if(img_link == " " && link == " "){
                                    adcard.setVisibility(View.GONE);
                                }




                            }else{
                                adcard.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        requestQueue.add(req);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        bt1 = findViewById(R.id.button2);
        bt2 = findViewById(R.id.button4);
        bt3 = findViewById(R.id.button6);
        bt4 = findViewById(R.id.button7);
        bt5 = findViewById(R.id.button8);
        bt6 = findViewById(R.id.but_cur);
        loadads();
        checkinternet();
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadads();
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                        @Override
                        public void onAdClicked() {
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            mInterstitialAd = null;
                            Intent intent =new Intent(MainActivity.this, MainActivity2.class);
                            intent.putExtra("num","0");
                            startActivity(intent);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when ad fails to show.
                            Log.e(TAG, "Ad failed to show fullscreen content.");
                            mInterstitialAd = null;
                        }
                    });
                } else {
                    Intent intent =new Intent(MainActivity.this, MainActivity2.class);
                    intent.putExtra("num","0");
                    startActivity(intent);
                }

            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadads();
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                        @Override
                        public void onAdClicked() {
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            mInterstitialAd = null;
                            Intent intent =new Intent(MainActivity.this, MainActivity2.class);
                            intent.putExtra("num","1");
                            startActivity(intent);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when ad fails to show.
                            Log.e(TAG, "Ad failed to show fullscreen content.");
                            mInterstitialAd = null;
                        }
                    });
                } else {
                    Intent intent =new Intent(MainActivity.this, MainActivity2.class);
                    intent.putExtra("num","1");
                    startActivity(intent);
                }
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadads();
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                        @Override
                        public void onAdClicked() {
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            mInterstitialAd = null;
                            Intent intent =new Intent(MainActivity.this, MainActivity2.class);
                            intent.putExtra("num","2");
                            startActivity(intent);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when ad fails to show.
                            Log.e(TAG, "Ad failed to show fullscreen content.");
                            mInterstitialAd = null;
                        }
                    });
                } else {
                    Intent intent =new Intent(MainActivity.this, MainActivity2.class);
                    intent.putExtra("num","2");
                    startActivity(intent);
                }
            }
        });
        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadads();
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                        @Override
                        public void onAdClicked() {
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            mInterstitialAd = null;
                            Intent intent =new Intent(MainActivity.this, MainActivity2.class);
                            intent.putExtra("num","3");
                            startActivity(intent);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when ad fails to show.
                            Log.e(TAG, "Ad failed to show fullscreen content.");
                            mInterstitialAd = null;
                        }
                    });
                } else {
                    Intent intent =new Intent(MainActivity.this, MainActivity2.class);
                    intent.putExtra("num","3");
                    startActivity(intent);
                }
            }
        });
        bt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadads();
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                        @Override
                        public void onAdClicked() {
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            mInterstitialAd = null;
                            Intent intent =new Intent(MainActivity.this, MainActivity2.class);
                            intent.putExtra("num","4");
                            startActivity(intent);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when ad fails to show.
                            Log.e(TAG, "Ad failed to show fullscreen content.");
                            mInterstitialAd = null;
                        }
                    });
                } else {
                    Intent intent =new Intent(MainActivity.this, MainActivity2.class);
                    intent.putExtra("num","4");
                    startActivity(intent);
                }
            }
        });

        bt6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadads();
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                        @Override
                        public void onAdClicked() {
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            mInterstitialAd = null;
                            Intent intent =new Intent(MainActivity.this, MainActivity2.class);
                            intent.putExtra("num","5");
                            startActivity(intent);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when ad fails to show.
                            Log.e(TAG, "Ad failed to show fullscreen content.");
                            mInterstitialAd = null;
                        }
                    });
                } else {
                    Intent intent =new Intent(MainActivity.this, MainActivity2.class);
                    intent.putExtra("num","5");
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure. You want to exit?")
                .setCancelable(false)
                .setTitle("EXIT")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void checkinternet(){
        //        check internet conection
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (isConnected) {
                // Connected to the internet
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // Connected to Wi-Fi
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // Connected to mobile data
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You have no internet. Please check your conection")
                        .setCancelable(false)
                        .setTitle("Sorry...")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }

    }

    private void loadads() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,getString(R.string.act1_inter), adRequest,
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