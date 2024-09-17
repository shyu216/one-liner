package com.example.one;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.one.databinding.FragmentThirdBinding;

import com.jinrishici.sdk.android.JinrishiciClient;
import com.jinrishici.sdk.android.factory.JinrishiciFactory;
import com.jinrishici.sdk.android.listener.JinrishiciCallback;
import com.jinrishici.sdk.android.model.JinrishiciRuntimeException;
import com.jinrishici.sdk.android.model.PoetySentence;

import java.util.List;

public class ThirdFragment extends Fragment {

    private FragmentThirdBinding binding;
    private static final String TAG = "ThirdFragment";


    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentThirdBinding.inflate(inflater, container, false);

        // 获取 SwipeRefreshLayout 实例
        swipeRefreshLayout = binding.getRoot().findViewById(R.id.swipeRefreshLayout3); // Assuming you added SwipeRefreshLayout in your XML
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // This method is called when the user pulls down to refresh
                // Perform the refresh operation here
                fetchPoetrySentence();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化 Jinrishici 库
        JinrishiciFactory.init(getContext());
        // 或者使用另一种初始化方式
        // JinrishiciClient.getInstance().init(getContext());

        // 调用获取今日诗词的 API
        fetchPoetrySentence();

//        binding.buttonSecond3.setOnClickListener(v ->
//                NavHostFragment.findNavController(ThirdFragment.this)
//                        .navigate(R.id.action_to_SecondFragment)
//        );
//
//        binding.buttonFirst3.setOnClickListener(v ->
//                NavHostFragment.findNavController(ThirdFragment.this)
//                        .navigate(R.id.action_to_FirstFragment)
//        );
//
//        binding.buttonClose3.setOnClickListener(v ->{
//            finishAffinity(getActivity());
//            System.exit(0);
//        });
//
//        binding.buttonFresh3.setOnClickListener(v ->
//                fetchPoetrySentence()
//        );
    }

    private void fetchPoetrySentence() {
        JinrishiciClient client = JinrishiciClient.getInstance();
        client.getOneSentenceBackground(new JinrishiciCallback() {
            @Override
            public void done(PoetySentence poetySentence) {
                //TODO do something
                //在这里进行你的逻辑处理
                Log.d(TAG, "done: " + poetySentence.getData().getContent());
                binding.shiciText.setText(poetySentence.getData().getContent());

                String author = poetySentence.getData().getOrigin().getAuthor();
                String title = poetySentence.getData().getOrigin().getTitle();
                String dynasty = poetySentence.getData().getOrigin().getDynasty();
                List<String> matchedTags = poetySentence.getData().getMatchTags();

                Log.d(TAG, "done: " + author);
                Log.d(TAG, "done: " + title);
                Log.d(TAG, "done: " + dynasty);
                Log.d(TAG, "done: " + matchedTags);

                binding.shiciA.setText(author);
                binding.shiciT.setText(title);
                binding.shiciD.setText(dynasty);
                binding.shiciM.setText(String.join("、", matchedTags));

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void error(JinrishiciRuntimeException e) {
                Log.w(TAG, "error: code = " + e.getCode() + " message = " + e.getMessage());
                //TODO do something else
                binding.shiciText.setText("Error: code = " + e.getCode() + " message = " + e.getMessage());

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}