package com.example.one;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.one.databinding.FragmentFirstBinding;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONObject;

import java.io.IOException;

import android.util.Log;

import java.nio.charset.StandardCharsets;

import android.os.CountDownTimer;

public class FirstFragment extends Fragment implements GestureDetector.OnGestureListener { // 实现 OnGestureListener 接口

    private FragmentFirstBinding binding;
    private OkHttpClient client = new OkHttpClient();
    private TextView hitokotoText;
    private TextView hitokotoType;
    private TextView hitokotoFrom;
    private TextView hitokotoFromWho;

//    private ProgressBar progressBarCountdown;
//    private Button buttonToggle;
//    private boolean isAutoFetchEnabled = false;
//    private CountDownTimer countDownTimer;


    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;
    private GestureDetector gestureDetector;

    private static final int SWIPE_THRESHOLD = 50;
    private static final int SWIPE_VELOCITY_THRESHOLD = 50;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        // 获取 SwipeRefreshLayout 实例
        swipeRefreshLayout = binding.getRoot().findViewById(R.id.swipeRefreshLayout1); // Assuming you added SwipeRefreshLayout in your XML
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // This method is called when the user pulls down to refresh
                // Perform the refresh operation here
                fetchHitokoto();
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hitokotoText = binding.hitokotoText;
        hitokotoType = binding.hitokotoType;
        hitokotoFrom = binding.hitokotoFrom;
        hitokotoFromWho = binding.hitokotoFromWho;


        gestureDetector = new GestureDetector(getContext(), (GestureDetector.OnGestureListener) this);
        view.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });


        fetchHitokoto();

//        progressBarCountdown = view.findViewById(R.id.progressBar_auto_1);
//        buttonToggle = view.findViewById(R.id.button_auto_1);

//
//        binding.buttonSecond1.setOnClickListener(v ->
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_to_SecondFragment)
//        );
//
//        binding.buttonThird1.setOnClickListener(v ->
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_to_ThirdFragment)
//        );
//
//        binding.buttonClose1.setOnClickListener(v ->{
//            finishAffinity(getActivity());
//            System.exit(0);
//        });
//
//        binding.buttonFresh1.setOnClickListener(v ->
//                fetchHitokoto()
//        );

//        buttonToggle.setOnClickListener(v -> {
//            isAutoFetchEnabled = !isAutoFetchEnabled;
//            if (isAutoFetchEnabled) {
//                buttonToggle.setText(R.string.stop);
//                startAutoFetch();
//            } else {
//                buttonToggle.setText(R.string.auto);
//                stopAutoFetch();
//            }
//        });
    }

//    private void startAutoFetch() {
//        countDownTimer = new CountDownTimer(5000, 50) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                int progress = (int) (100 - millisUntilFinished / 50);
//                progressBarCountdown.setProgress(progress);
//            }
//
//            @Override
//            public void onFinish() {
//                fetchHitokoto();
//                if (isAutoFetchEnabled) {
//                    startAutoFetch();
//                }
//            }
//        }.start();
//    }
//
//    private void stopAutoFetch() {
//        if (countDownTimer != null) {
//            countDownTimer.cancel();
//        }
//    }


    private void fetchHitokoto() {
        Request request = new Request.Builder()
                .url("https://v1.hitokoto.cn?c=d&c=i&c=j&c=k")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    getActivity().runOnUiThread(() -> {
                        try {
                            JSONObject json = new JSONObject(responseData);
                            Log.d("HitokotoResponse", new String(json.toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
                            String hitokoto = json.getString("hitokoto");
                            String uuid = json.getString("uuid");
                            hitokotoText.setText(hitokoto);

                            String description = getDescriptionByType(json.getString("type"));
                            hitokotoType.setText(description != null ? description : "");

                            hitokotoFrom.setText(json.has("from") && !json.isNull("from") ? json.getString("from") : "");
                            hitokotoFromWho.setText(json.has("from_who") && !json.isNull("from_who") ? json.getString("from_who") : "");
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                            hitokotoText.setText("Error: " + e.getMessage());
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }

    private String getDescriptionByType(String type) {
        switch (type) {
            case "a":
                return "动画";
            case "b":
                return "漫画";
            case "c":
                return "游戏";
            case "d":
                return "文学";
            case "e":
                return "原创";
            case "f":
                return "来自网络";
            case "g":
                return "其他";
            case "h":
                return "影视";
            case "i":
                return "诗词";
            case "j":
                return "网易云";
            case "k":
                return "哲学";
            case "l":
                return "抖机灵";
            default:
                return "未知类型";
        }
    }

    private void renderTextViewByType(TextView textView, String type) {
        String description = getDescriptionByType(type);
        textView.setText(description);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public boolean onDown(@NonNull MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // Check if SwipeRefreshLayout is not refreshing
        if (!swipeRefreshLayout.isRefreshing()) {
            // Check if the scroll is primarily vertical
            if (Math.abs(distanceY) > Math.abs(distanceX)) {
                // Check if the scroll distance is significant enough to be considered a pull-down gesture
                if (distanceY < -SWIPE_THRESHOLD) {
                    // Handle pull-down gesture for refreshing
                    swipeRefreshLayout.setRefreshing(true);
                    fetchHitokoto();
                    return true;
                }
            }
        }
        // Handle regular scroll gesture
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX() - e2.getX() > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            // 向左滑动
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_to_SecondFragment, null, new NavOptions.Builder().setPopUpTo(R.id.FirstFragment, true).build());
        } else if (e2.getX() - e1.getX() > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            // 向右滑动
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_to_ThirdFragment, null, new NavOptions.Builder().setPopUpTo(R.id.FirstFragment, true).build());
        }
        return true; // 返回 true 表示事件已处理
    }
}