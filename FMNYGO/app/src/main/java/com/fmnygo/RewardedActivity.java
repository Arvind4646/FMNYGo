package com.fmnygo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.fmnygo.databinding.ActivityRewardedBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class RewardedActivity extends AppCompatActivity {

    ActivityRewardedBinding  binding;

    private RewardedAd rewardedAd;

    FirebaseDatabase database;
    String currentuid;
    int coins = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRewardedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        currentuid = FirebaseAuth.getInstance().getUid();
        loadAd();

        database.getReference().child("profiles")
                        .child(currentuid)
                                .child("coins")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                coins = snapshot.getValue(Integer.class);
                                                binding.coins.setText(String.valueOf(coins));
                                            }

                                            @Override
                                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                            }
                                        });

        binding.video1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rewardedAd != null) {
                    Activity activityContext = RewardedActivity.this;
                    rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            coins = coins + 25;
                            database.getReference().child("profiles")
                                    .child(currentuid)
                                    .child("coins")
                                    .setValue(coins);
                            binding.video1icon.setImageResource(R.drawable.check);
                        }
                    });
                } else {
//                    Log.d(TAG, "The rewarded ad wasn't ready yet.");
                }
            }
        });

        binding.video2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rewardedAd != null) {
                    Activity activityContext = RewardedActivity.this;
                    rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            coins = coins + 50;
                            database.getReference().child("profiles")
                                    .child(currentuid)
                                    .child("coins")
                                    .setValue(coins);
                            binding.video2icon.setImageResource(R.drawable.check);
                        }
                    });
                } else {
//                    Log.d(TAG, "The rewarded ad wasn't ready yet.");
                }
            }
        });

        binding.video3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rewardedAd != null) {
                    Activity activityContext = RewardedActivity.this;
                    rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            coins = coins + 100;
                            database.getReference().child("profiles")
                                    .child(currentuid)
                                    .child("coins")
                                    .setValue(coins);
                            binding.video3icon.setImageResource(R.drawable.check);
                        }
                    });
                } else {
//                    Log.d(TAG, "The rewarded ad wasn't ready yet.");
                }
            }
        });

        binding.video4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rewardedAd != null) {
                    Activity activityContext = RewardedActivity.this;
                    rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            coins = coins + 150;
                            database.getReference().child("profiles")
                                    .child(currentuid)
                                    .child("coins")
                                    .setValue(coins);
                            binding.video4icon.setImageResource(R.drawable.check);
                        }
                    });
                } else {
//                    Log.d(TAG, "The rewarded ad wasn't ready yet.");
                }
            }
        });

        binding.video5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rewardedAd != null) {
                    Activity activityContext = RewardedActivity.this;
                    rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            loadAd();
                            coins = coins + 200;
                            database.getReference().child("profiles")
                                    .child(currentuid)
                                    .child("coins")
                                    .setValue(coins);
                            binding.video5icon.setImageResource(R.drawable.check);
                        }
                    });
                } else {
//                    Log.d(TAG, "The rewarded ad wasn't ready yet.");
                }
            }
        });
    }

    void loadAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                    }
                });
    }
}