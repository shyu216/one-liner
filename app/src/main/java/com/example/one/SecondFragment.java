package com.example.one;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.content.ContentProviderOperation;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.one.databinding.FragmentSecondBinding;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;

import com.bumptech.glide.Glide;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private OkHttpClient client = new OkHttpClient();

    private TextView cibaEnglish;
    private TextView cibaChinese;
    private ImageView cibaImage;
    private TextView cibaDate;
    LocalDate currentDate;


    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);

        // 获取 SwipeRefreshLayout 实例
        swipeRefreshLayout = binding.getRoot().findViewById(R.id.swipeRefreshLayout2); // Assuming you added SwipeRefreshLayout in your XML
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // This method is called when the user pulls down to refresh
                // Perform the refresh operation here
                goBack1Day();
            }
        });


        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cibaEnglish = view.findViewById(R.id.ciba_eng_text);
        cibaChinese = view.findViewById(R.id.ciba_chi_text);
        cibaImage = view.findViewById(R.id.ciba_img);
        cibaDate = view.findViewById(R.id.ciba_date_text);

//        binding.buttonThird2.setOnClickListener(v ->
//                NavHostFragment.findNavController(SecondFragment.this)
//                        .navigate(R.id.action_to_ThirdFragment)
//        );
//
//        binding.buttonFirst2.setOnClickListener(v ->
//                NavHostFragment.findNavController(SecondFragment.this)
//                        .navigate(R.id.action_to_FirstFragment)
//        );
//
//        binding.buttonClose2.setOnClickListener(v ->{
//            finishAffinity(getActivity());
//            System.exit(0);
//                });

        resetDate();

//        binding.buttonBack2.setOnClickListener(v -> goBack1Day());
//        binding.buttonReset2.setOnClickListener(v -> resetDate());
    }

    private void goBack1Day() {
        currentDate = currentDate.minusDays(1);
        fetchCiba();
    }

    private void resetDate() {
        currentDate = LocalDate.now();
        fetchCiba();
    }

    private void fetchCiba() {
        String baseUrl = "https://open.iciba.com/dsapi/";
        String file = "json"; // default format
        String date = currentDate.toString(); // default to empty // date=2024-05-03
        Log.d("Ciba", new String(date.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));

        String type = ""; // default to empty

        // Build the URL with parameters
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        urlBuilder.append("?file=").append(file);
        if (!date.isEmpty()) {
            urlBuilder.append("&date=").append(date);
        }
        if (!type.isEmpty()) {
            urlBuilder.append("&type=").append(type);
        }

        String url = urlBuilder.toString();
        Log.d("Ciba", "Request URL: " + url);

        Request request = new Request.Builder()
                .url(url)
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
                    Log.d("Ciba", "Response data: " + responseData);
                    getActivity().runOnUiThread(() -> {
                        try {
                            JSONObject json = new JSONObject(responseData);
                            String content = json.getString("content");
                            String note = json.getString("note");
                            String picture2 = json.getString("picture2");
                            String dateline = json.getString("dateline");

                            Log.d("Ciba", "Content: " + content);
                            Log.d("Ciba", "Note: " + note);
                            Log.d("Ciba", "Picture URL: " + picture2);
                            Log.d("Ciba", "Dateline: " + dateline);

                            cibaEnglish.setText(content);
                            cibaChinese.setText(note);
                            cibaDate.setText(dateline);

                            Glide.with(getActivity())
                                    .load(picture2)
                                    .into(cibaImage);

                            swipeRefreshLayout.setRefreshing(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                            cibaEnglish.setText("Error: " + e.getMessage());
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }
}