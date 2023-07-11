package moneyswagx.defaultkakao;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;


import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.kakao.talk.theme.defaultkakao.R;

public class MainActivity extends Activity {
    private static final String KAKAOTALK_SETTINGS_THEME_URI = "kakaotalk://settings/theme/";
    private static final String MARKET_URI = "market://details?id=";
    private static final String KAKAO_TALK_PACKAGE_NAME = "com.kakao.talk";
     private final String TAG = "MainActivity";
    private RewardedAd rewardedAd;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, getString(R.string.admobreward),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                    }
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Ad was loaded.");
                    }
                });




        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        mAdView.loadAd(adRequest);













        final MediaPlayer music;
        music = MediaPlayer.create(MainActivity.this,R.raw.spawn);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.statusBarColor));
            } catch (Throwable ignored) {
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } catch (Throwable ignored) {
                //거절

            }
        }



        final Button applyButton = (Button) findViewById(R.id.apply); //버튼 클릭시 SetonCliskListner 작동.
        applyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {



                if (rewardedAd != null) {
                    Activity activityContext = MainActivity.this;
                    rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d(TAG, "The user earned the reward.");

                            final Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(KAKAOTALK_SETTINGS_THEME_URI + getPackageName()));
                            startActivity(intent);


                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();
                        }
                    });
                } else {
                    Log.d(TAG, "The rewarded ad wasn't ready yet.");
                }



                //  finish();
            }
        });



        Button SHAREFRI = findViewById(R.id.share);
        SHAREFRI.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Sharing_intent = new Intent(Intent.ACTION_SEND);
                Sharing_intent.setType("text/plain");

                String Test_Message = getString(R.string.message);

                Sharing_intent.putExtra(Intent.EXTRA_TEXT, Test_Message);

                Intent Sharing = Intent.createChooser(Sharing_intent, "❣️공유하기❣️");
                startActivity(Sharing);
            }
        });


        final Button installButton = (Button) findViewById(R.id.market);
        installButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URI + KAKAO_TALK_PACKAGE_NAME));
                startActivity(intent);
                finish();
            }
        });

        try {
            getPackageManager().getPackageInfo(KAKAO_TALK_PACKAGE_NAME, 0);
            applyButton.setVisibility(View.VISIBLE);
            installButton.setVisibility(View.GONE);
        } catch (NameNotFoundException e) {
            applyButton.setVisibility(View.GONE);
            installButton.setVisibility(View.VISIBLE);
        }
    }




}
