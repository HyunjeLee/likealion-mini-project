package com.example.likealion_mini_project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.likealion_mini_project.adapter.MainAdapter;
import com.example.likealion_mini_project.callback.DialogCallback;
import com.example.likealion_mini_project.callback.PermissionCallback;
import com.example.likealion_mini_project.databinding.ActivityMainBinding;
import com.example.likealion_mini_project.db.DBHelper;
import com.example.likealion_mini_project.model.Student;
import com.example.likealion_mini_project.util.DialogUtil;
import com.example.likealion_mini_project.util.PermissionUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ActivityResultLauncher<Intent> launcher;

    MainAdapter mainAdapter;

    ArrayList<Student> studentArrayList = new ArrayList<>();
    ArrayList<Student> studentBackupArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Toolbar
        setSupportActionBar(binding.toolbar1);

        launcher = registerForActivityResult(  // 학생정보 추가 이후 복귀 시
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        int id = intent.getIntExtra("id", -1);
                        String name = intent.getStringExtra("name");
                        String email = intent.getStringExtra("email");
                        String phone = intent.getStringExtra("phone");
                        String memo = intent.getStringExtra("memo");

                        Student student = new Student(id, name, email, phone, memo, null);

                        //새로운 Student 객체가 만들어졌다.. 이 데이터로 항목이 하나 추가되게 하면 된다..
                        //adapter에 넘긴 항목 구성 데이터에 추가한 후에..
                        //변경사항 반영해.. 명령내리면 된다..
                        studentArrayList.add(student);
                        studentBackupArrayList.add(student);
                        mainAdapter.notifyDataSetChanged();
                    }
                }
        );

        PermissionUtil.checkAllPermission(MainActivity.this, new PermissionCallback() {
            @Override
            public void onPermissionResult(boolean isAllGranted) {
                if (isAllGranted) {
                    makeRecyclerView();
                } else {
                    showPermissionRequestDialog();
                }
            }
        });

    }

    // db data select
    private void getListData() {
        DBHelper helper = new DBHelper(MainActivity.this);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from tb_student order by name", null);  // 이름순 정렬  // 기본은 _id

        while (cursor.moveToNext()) {
            Student student = new Student(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(5),
                    cursor.getString(4)  // fixme: 모델과 db간 순서 맞출 것
            );

            studentArrayList.add(student);
        }
        studentBackupArrayList = new ArrayList<>(studentArrayList);
    }

    private void makeRecyclerView() {
        getListData();

        mainAdapter = new MainAdapter(studentArrayList, MainActivity.this);
        binding.rv1.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        binding.rv1.setAdapter(mainAdapter);
        binding.rv1.addItemDecoration(new DividerItemDecoration(MainActivity.this,
                DividerItemDecoration.VERTICAL));
    }

    private void showPermissionRequestDialog() {
        DialogUtil.showMessageDialog(MainActivity.this,
                getString(R.string.permission_denied),
                "화긴",
                null,
                new DialogCallback() {
                    @Override
                    public void onPositiveCall() {

                    }

                    @Override
                    public void onNegativeCall() {

                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 메뉴 출력
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem menuItemSearch = menu.findItem(R.id.menu_main_search);
        SearchView searchView = (SearchView) menuItemSearch.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.main_search_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // newText에 따라 recyclerview 업데이트
                // 지금 있는 studentArrayList 사용
                if (newText.isEmpty()) {
                    studentArrayList.clear();
                    studentArrayList.addAll(studentBackupArrayList);

                    mainAdapter.notifyDataSetChanged();
                } else {
                    studentArrayList.clear();
                    // 그때그때 db에서 조건에 맞는 객체 뽑아오기
                    DBHelper helper = new DBHelper(MainActivity.this);
                    SQLiteDatabase db = helper.getReadableDatabase();

                    Cursor cursor = db.rawQuery(
                            "SELECT * FROM tb_student WHERE name LIKE ? ORDER BY name",
                            new String[]{ "%" + newText + "%" }
                    );  // newText를 포함하는 name select

                    while (cursor.moveToNext()) {
                        Student student = new Student(
                                cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3),
                                cursor.getString(5),
                                cursor.getString(4)  // fixme: 모델과 db간 순서 맞출 것
                        );
                        studentArrayList.add(student);
                    }
                    mainAdapter.notifyDataSetChanged();
                }

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_main_add) {
            // toolbar의 add 메뉴 클릭 시
            //화면전환.. 되돌아 올때.. 사후처리..
            Intent intent = new Intent(MainActivity.this, AddStudentActivity.class);
            launcher.launch(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}