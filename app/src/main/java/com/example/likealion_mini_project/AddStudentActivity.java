package com.example.likealion_mini_project;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.likealion_mini_project.databinding.ActivityAddStudentBinding;
import com.example.likealion_mini_project.db.DBHelper;
import com.example.likealion_mini_project.util.DialogUtil;

public class AddStudentActivity extends AppCompatActivity {

    ActivityAddStudentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        binding = ActivityAddStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scroll_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(binding.toolbar1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add_save) {
            save();
        }

        return super.onOptionsItemSelected(item);
    }

    private void save() {
        // 유저가 입력한 데이터 획득
        String name = binding.etName.getText().toString();
        String email = binding.etEmail.getText().toString();
        String phone = binding.etPhone.getText().toString();
        String memo = binding.etMemo.getText().toString();

        // 유효성 검사
        if(name == null || name.equals("")){
            DialogUtil.showToast(this, getString(R.string.add_name_null));
        }else {
            //db 저장..
            DBHelper helper = new DBHelper(this);
            SQLiteDatabase db = helper.getWritableDatabase();

            //저장 데이터..
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("email", email);
            values.put("phone", phone);
            values.put("memo", memo);

            //execSql() 함수로도 insert 가능하다.. 하지만.. execSql 이 insert 전문이 아니라.. 다른 select
            //제외하고.. 나머지 sql 실행이다 보니.. return 데이터가 없다..
            //insert 후에.. insert 된 그 row 의 식별자값을 얻고 싶다...
            int newRowId = (int) db.insert("tb_student", null, values);
            db.close();

            //화면은????
            //MainActivity 로 화면 전환.. 데이터 포함시켜서..
            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            intent.putExtra("id", newRowId);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("phone", phone);
            intent.putExtra("memo", memo);
            finish();

        }
    }
}