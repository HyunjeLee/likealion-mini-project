package com.example.likealion_mini_project;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.likealion_mini_project.databinding.ActivityAddScoreBinding;
import com.example.likealion_mini_project.db.DBHelper;
import com.example.likealion_mini_project.util.DialogUtil;

public class AddScoreActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityAddScoreBinding binding;

    Intent intent;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        binding = ActivityAddScoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        intent = getIntent();
        id = intent.getIntExtra("id", -1);

        binding.btnKey0.setOnClickListener(this);
        binding.btnKey1.setOnClickListener(this);
        binding.btnKey2.setOnClickListener(this);
        binding.btnKey3.setOnClickListener(this);
        binding.btnKey4.setOnClickListener(this);
        binding.btnKey5.setOnClickListener(this);
        binding.btnKey6.setOnClickListener(this);
        binding.btnKey7.setOnClickListener(this);
        binding.btnKey8.setOnClickListener(this);
        binding.btnKey9.setOnClickListener(this);
        binding.btnKeyBack.setOnClickListener(this);
        binding.btnKeyAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == binding.btnKeyAdd) {
            int nowScore = Integer.parseInt(binding.tvKeyEdit.getText().toString());
            if (nowScore > 100) {  // 입력값이 점수의 최댓값인 100을 넘어가면
                DialogUtil.showToast(AddScoreActivity.this, "최고점수는 100점 💯💯💯");
                return;
            }

            long date = System.currentTimeMillis();

            String score = binding.tvKeyEdit.getText().toString();

            DBHelper helper = new DBHelper(this);
            SQLiteDatabase database = helper.getWritableDatabase();

            database.execSQL("insert into tb_score (student_id, date, score) values (?, ?, ?)",
                    new String[]{String.valueOf(id), String.valueOf(date), score});

            database.close();

            // 화면은 ??  // 이 화면은 특정 화면에서 호출된 화면 (CRUD의 create 화면)  // 즉, create 끝나면 parent화면으로 돌아가야함
            intent.putExtra("score", score);
            intent.putExtra("date", date);
            setResult(RESULT_OK, intent);  // parent 돌아가는 방식은 result launcher를 통할테니..
            finish();

        }
        else if (v == binding.btnKeyBack){  // 넘버패드의 지우기 버튼 클릭 시

            String score = binding.tvKeyEdit.getText().toString();

            if (score.length() == 1) {
                binding.tvKeyEdit.setText("0");
            } else {
                String newScore = score.substring(0, score.length() - 1); // 입력값의 마지막 숫자값 제거  // 125 -> 12로
                binding.tvKeyEdit.setText(newScore);
            }
        }
        else {  // 숫자키 클릭 시

            Button btn = (Button) v;  // 클릭 시 매개변수인 v는 버튼이지만 View타입으로 받아와짐  // 다시 버튼으로 만들기

            String key = btn.getText().toString();
            String score = binding.tvKeyEdit.getText().toString();

            if (score.equals("0")) {
                binding.tvKeyEdit.setText(key);
            } else {
                binding.tvKeyEdit.setText(score+key);
            }
        }

    }
}