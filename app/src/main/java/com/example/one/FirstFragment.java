package com.example.one;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

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

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private OkHttpClient client = new OkHttpClient();
    private TextView hitokotoText;
    private TextView hitokotoType;
    private TextView hitokotoFrom;
    private TextView hitokotoFromWho;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hitokotoText = binding.hitokotoText;
        hitokotoType = binding.hitokotoType;
        hitokotoFrom = binding.hitokotoFrom;
        hitokotoFromWho = binding.hitokotoFromWho;

        binding.buttonFirst.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );

        binding.buttonFresh.setOnClickListener(v ->
                fetchHitokoto()
        );

        fetchHitokoto();
    }

    private void fetchHitokoto() {
        Request request = new Request.Builder()
                .url("https://v1.hitokoto.cn?c=d&c=e&c=f&c=g&c=h&c=i&c=j&c=k")
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

                        } catch (Exception e) {
                            e.printStackTrace();
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
}