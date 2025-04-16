package com.example.likealion_mini_project;

import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.likealion_mini_project.databinding.ActivityChartBinding;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {

    ActivityChartBinding binding;

    String name;
    ArrayList<Integer> scoreArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        binding = ActivityChartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = getIntent().getStringExtra("name");
        scoreArrayList = getIntent().getIntegerArrayListExtra("scoreArrayList");

        binding.tvName.setText(name);

        WebSettings settings = binding.wvChart.getSettings();
        settings.setJavaScriptEnabled(true);
        binding.wvChart.loadUrl("file:///android_asset/test.html");
        binding.wvChart.loadUrl("javascript:lineChart()");

        binding.wvChart.addJavascriptInterface(new JavaScriptChart(), "android");

    }

    class JavaScriptChart {

        @JavascriptInterface
        public String getChartData() {
            if (scoreArrayList == null || scoreArrayList.isEmpty()) {
                return "[]";
            }

            StringBuffer buffer = new StringBuffer();
            buffer.append("[");

            for (int i = 0; i < scoreArrayList.size(); i++) {
                buffer.append("[" + i + "," + scoreArrayList.get(i) + "]");
                if (i < scoreArrayList.size() - 1)
                    buffer.append(",");
            }

            buffer.append("]");
            return buffer.toString();

        }
    }
}