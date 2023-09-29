package com.fmnygo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fmnygo.databinding.ActivityCallBinding;
import com.fmnygo.models.InterfaceJava;
import com.fmnygo.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CallActivity extends AppCompatActivity {

    ActivityCallBinding binding;

    String uniqueId = "";
    FirebaseAuth auth;
    String username = "";
    String friendUsername = "";

    boolean isPeerConnected = false;

    DatabaseReference firebaseRef;

    boolean isAudio = true;
    boolean isVideo = true;
    String createdBy;
    String status;

    boolean pageExit = false;

    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //---------------------------

        mRunnable = new Runnable() {
            @Override
            public void run() {
                // Call your function here
                myFunction();

                // Call this runnable again after 1 second
                mHandler.postDelayed(this, 1000);
            }
        };

        // Call the runnable initially to start the loop
        mHandler.postDelayed(mRunnable, 1000);

        //----------------------------



        auth = FirebaseAuth.getInstance();
        firebaseRef = FirebaseDatabase.getInstance().getReference().child("users");

        username = getIntent().getStringExtra("username");
        String incoming = getIntent().getStringExtra("incoming");
        createdBy = getIntent().getStringExtra("createdBy");
        status = getIntent().getStringExtra("status");


        friendUsername = incoming;

        setupWebView();

        binding.micBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAudio = !isAudio;
                callJavaScriptFunction("javascript:toggleAudio(\"" + isAudio + "\")");
                if(isAudio)
                {
                    binding.micBtn.setImageResource(R.drawable.btn_unmute_normal);
                }else{
                    binding.micBtn.setImageResource(R.drawable.btn_mute_normal);
                }
            }
        });

        binding.videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVideo = !isVideo;
                callJavaScriptFunction("javascript:toggleVideo(\"" + isVideo + "\")");
                if(isVideo)
                {
                    binding.videoBtn.setImageResource(R.drawable.btn_video_normal);
                }else{
                    binding.videoBtn.setImageResource(R.drawable.btn_video_muted);
                }
            }
        });

        binding.endCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String first = createdBy;

                firebaseRef.child(first).child("status").setValue(2);

            }

        });

    }

    void setupWebView()
    {
        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
//                super.onPermissionRequest(request);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
            }
        });

        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        binding.webView.addJavascriptInterface(new InterfaceJava(this), "Android");

        loadVideoCall();
    }
    public void loadVideoCall(){
        String filePath = "file:android_asset/call.html";
        binding.webView.loadUrl(filePath);

        binding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                 initializePeer();
            }
        });
    }

    void initializePeer(){

        uniqueId = getUniqueId();


        callJavaScriptFunction("javascript:init(\"" + uniqueId + "\")");

        if(createdBy.equalsIgnoreCase(username))
        {
//            if (pageExit){
//                return;
//            }
            firebaseRef.child(username).child("connId").setValue(uniqueId);
            firebaseRef.child(username).child("isAvailable").setValue(true);

            binding.controls.setVisibility(View.VISIBLE);

            FirebaseDatabase.getInstance().getReference()
                    .child("profiles")
                    .child(friendUsername)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            Glide.with(CallActivity.this).load(user.getProfile())
                                    .into(binding.profile);
                            binding.name.setText(user.getName());
                            binding.city.setText(user.getCity());
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });

        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    friendUsername = createdBy;

                    FirebaseDatabase.getInstance().getReference()
                            .child("profiles")
                            .child(friendUsername)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);
                                    Glide.with(CallActivity.this).load(user.getProfile())
                                            .into(binding.profile);
                                    binding.name.setText(user.getName());
                                    binding.city.setText(user.getCity());
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });


                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(friendUsername)
                            .child("connId")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    if(snapshot.getValue() != null)
                                    {
                                        sendCallRequest();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                }
            }, 2000);
        }

    }

    public void onPeerConnected(){
        isPeerConnected = true;
    }

    void sendCallRequest(){
        if(!isPeerConnected)
        {
            Toast.makeText(this, "You are not connected. PLease check your internet ", Toast.LENGTH_SHORT).show();
            return;
        }

        listenConnId();
    }

    void listenConnId(){
    firebaseRef.child(friendUsername).child("connId").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            if(snapshot.getValue() == null)
                return;

            binding.controls.setVisibility(View.VISIBLE);
            String connId = snapshot.getValue(String.class);
            callJavaScriptFunction("javascript:startCall(\""+connId+"\")");
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    });
    }
    void callJavaScriptFunction(String function)
    {
        binding.webView.post(new Runnable() {
            @Override
            public void run() {
                binding.webView.evaluateJavascript(function, null);
            }
        });
    }
    String getUniqueId(){
        return UUID.randomUUID().toString();

    }

    @Override
    public void onBackPressed() {
        String first = createdBy;
        firebaseRef.child(first).child("status").setValue(2);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pageExit = true;
        firebaseRef.child(createdBy).setValue(null);
        finish();
    }


    private void myFunction() {

        String first =  getIntent().getStringExtra("createdBy");

        firebaseRef.child(first).child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String sts = String.valueOf(snapshot.getValue());
                if (sts.equals("2")){

                    binding.webView.evaluateJavascript("javascript:endCall()", null);
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}


