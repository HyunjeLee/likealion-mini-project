package com.example.likealion_mini_project.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context){
        super(context, "studentdb", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table tb_student (" +
                "_id integer primary key autoincrement," +
                "name text not null," +
                "email text," +
                "phone text," +
                "photo text," +
                "memo text)");

        // 🚀🚀🚀 추가적인 테이블 추가
        // 🤔🤔🤔 어떻게 적용시키지?  // onCreate는 설치 후 단 한 번
        db.execSQL("create table tb_score (" +
                "_id integer primary key autoincrement," +
                "student_id integer not null," +
                "date," +
                "score)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 서비스 중에는 table 삭제 후, onCreate() 호출하는 방식으로 하면 안됨 !  // 미니 프로젝트니까 그냥 진행
//        if (newVersion == 2) {  // todo: 이 조건 필요함?
            db.execSQL("drop table tb_student");
            onCreate(db);
//        }
    }
}
