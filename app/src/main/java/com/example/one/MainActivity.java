package com.example.one;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.one.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.transition.MaterialContainerTransform;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity { // MainActivity 类继承自 AppCompatActivity

    private AppBarConfiguration appBarConfiguration; // 声明 AppBarConfiguration 类型的变量 appBarConfiguration
    private ActivityMainBinding binding; // 声明 ActivityMainBinding 类型的变量 binding

    @Override
    protected void onCreate(Bundle savedInstanceState) { // 重写 onCreate() 方法，这是 Activity 的入口点
        super.onCreate(savedInstanceState); // 调用父类的 onCreate() 方法

        binding = ActivityMainBinding.inflate(getLayoutInflater()); // 使用布局文件生成 binding 实例
        setContentView(binding.getRoot()); // 设置 Activity 的内容视图

        setSupportActionBar(binding.toolbar); // 将布局文件中的 toolbar 设置为 ActionBar

        // 获取 NavHostFragment 实例
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = navHostFragment.getNavController(); // 获取 NavController 实例

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNavigationView, navController); // 将底部导航栏与 NavController 关联

        // 创建 AppBarConfiguration 实例，指定顶部级别的 Fragment
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.FirstFragment, R.id.SecondFragment, R.id.ThirdFragment)
                .build();

        // 将 ActionBar 与 NavController 关联，并使用 AppBarConfiguration 进行配置
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // 重写 onCreateOptionsMenu() 方法，创建选项菜单
        getMenuInflater().inflate(R.menu.menu_main, menu); // 加载菜单资源文件
        return true; // 返回 true 表示显示菜单
    }

    @Override
    public boolean onSupportNavigateUp() { // 重写 onSupportNavigateUp() 方法，处理向上导航
        // 获取 NavController 实例
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        // 使用 NavigationUI 处理向上导航，或者调用父类的 onSupportNavigateUp() 方法
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}