package com.example.likealion_mini_project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.likealion_mini_project.adapter.DetailAdapter;
import com.example.likealion_mini_project.databinding.ActivityDetailBinding;
import com.example.likealion_mini_project.db.DBHelper;
import com.example.likealion_mini_project.model.Student;
import com.example.likealion_mini_project.util.BitmapUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;

    Student student;

    ArrayList<Map<String, String>> scoreList;
    DetailAdapter adapter;

    ActivityResultLauncher<Intent> requestGalleryLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // actionbar -> toolbar
        setSupportActionBar(binding.toolbar);

        int id = getIntent().getIntExtra("id", -1);

        // db에서 출력데이터값 bind
        bindInitialStudentData(id);
        bindInitialScoreData(id);

        // 커스텀뷰에 score set
        if (!scoreList.isEmpty()) {
            int firstScore = Integer.parseInt(scoreList.get(0).get("score"));
            binding.cvDonut.setScore(firstScore);
        }

        ActivityResultLauncher<Intent> addScoreLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        //AddScoreActivity 에서 전달한 데이터 획득..
                        Intent intent = result.getData();
                        String score = intent.getStringExtra("score");
                        long date = intent.getLongExtra("date", 0);

                        HashMap<String, String> map = new HashMap<>();
                        map.put("score", score);
                        //데이터를 유저에게 뿌릴 문자열 포멧으로 변형해서..
                        Date d = new Date(date);
                        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                        map.put("date", sd.format(d));

                        scoreList.add(0, map);
                        //새로운 항목 데이터가 추가된 것이다..
                        //adapter 가 이미 항목을 만들어 놓았을 것이다.. 변경사항이 있다고 알려준다..
                        adapter.notifyDataSetChanged();

                        binding.cvDonut.setScore(Integer.parseInt(score));
                    }
                    if (result.getResultCode() == RESULT_CANCELED) {  // 시스템 백버튼 등

                    }


                }
        );
        binding.btnAddScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DetailActivity.this, AddScoreActivity.class);
                intent.putExtra("id", id);
                addScoreLauncher.launch(intent);
            }
        });

        // callback end
        requestGalleryLauncher = registerForActivityResult(  // todo: 코드 흐름 다시 보기
                new ActivityResultContracts.StartActivityForResult(),
                activityResult -> {  // 내부 갤러리 앱에서 사진 선택 후 복귀 한 경우
                    try {
                        if (activityResult.getResultCode() == RESULT_OK) {
                            Uri uri = activityResult.getData().getData();  // getData 1번은 Intent  // 2번해야댐

                            String[] proj = new String[]{MediaStore.Images.Media.DATA};  // Android OS DB의 column명  // 이미지 관련 column
                            Cursor galleryCursor = getContentResolver().query(uri, proj, null, null, null);  // proj명의 column에서 uri에 해당하는 커서 생성

                            if (galleryCursor != null) {
                                if (galleryCursor.moveToFirst()) {
                                    String filePath = galleryCursor.getString(0);  // 해당 uri값의 이미지의 절대경로

                                    // tb_student의 테이블에 데이터 수정  // 기존에 null
                                    DBHelper helper = new DBHelper(DetailActivity.this);
                                    SQLiteDatabase database = helper.getWritableDatabase();

                                    database.execSQL("update tb_student set photo=? where _id=?",
                                            new String[]{filePath, String.valueOf(id)}
                                    );
                                    database.close();
                                }
                            }

                            Bitmap bitmap = BitmapUtil.getGalleryImageBitmapFromStream(DetailActivity.this, uri);
                            if (bitmap != null) {
                                binding.ivProfile.setImageBitmap(bitmap);
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );


    }

    private void bindInitialStudentData(int id) {
        DBHelper helper = new DBHelper(DetailActivity.this);
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = database.rawQuery("select * from tb_student where _id=?",
                new String[]{String.valueOf(id)});

        String photoFilePath = null;

        if (cursor.moveToFirst()) {
            String name = cursor.getString(1);
            String email = cursor.getString(2);
            String phone = cursor.getString(3);

            // 화면 바인딩
            binding.tvName.setText(name);
            binding.tvEmail.setText(email);
            binding.tvPhone.setText(phone);

            photoFilePath = cursor.getString(4);

            student = new Student(
                    cursor.getInt(0),
                    name,
                    email,
                    phone,
                    cursor.getString(5),
                    photoFilePath
            );  // todo: db랑 model간 인덱스 순서 맞추기
        }

        database.close();

        // db에 저장된 photo filepath로 출력
        Bitmap bitmap = BitmapUtil.getGalleryImageFromFile(this, photoFilePath);
        if (bitmap != null) {
            binding.ivProfile.setImageBitmap(bitmap);
        }

        binding.ivProfile.setOnClickListener(v -> {
            // intent를 통해 gallery 앱 접근
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  // Uri.parse("content://media/external/images/media") 이것과 같다
            intent.setType("image/*");
            requestGalleryLauncher.launch(intent);
        });

    }

    private void bindInitialScoreData(int id) {
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select score, date from tb_score where student_id = ? order by date desc",
                new String[]{String.valueOf(id)});

        scoreList = new ArrayList<>();

        while (cursor.moveToNext()) {

            HashMap<String, String> map = new HashMap<>();
            map.put("score", cursor.getString(0));

            Date date = new Date(Long.parseLong(cursor.getString(1)));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            map.put("date", simpleDateFormat.format(date));

            scoreList.add(map);
        }
        database.close();


        binding.rvDetail.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DetailAdapter(this, scoreList);
        binding.rvDetail.setAdapter(adapter);
        binding.rvDetail.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }
}