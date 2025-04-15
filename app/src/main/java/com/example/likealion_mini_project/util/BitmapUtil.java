package com.example.likealion_mini_project.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.InputStream;

public class BitmapUtil {

    // 안드로이드 이미지의 원천 타입은 Bitmap  // Drawable은 좀 더 편리한 사용을 위한 타입일 뿐 내부는 Bitmap
    // 내부 갤러리 앱에서 선택된 이미지의 Uri를 받아 Bitmap으로 return

    // URI 유효성을 이유로 2가지 방식으로 구현  // URI를 뽑아온 당시 상황이 아니면 해당 URI가 제대로 동작하지 않을 가능성 높음
    // 학습을 위해 2가지 전부 사용
    public static Bitmap getGalleryImageBitmapFromStream(Context context, Uri uri) {
        try {
            // 데이터 이미지는 사이즈가 큼  // 그냥 로딩 시 OUT OF MEMORY 발생 가능
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;  // 10분의 1 사이즈로 로딩

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // String 절대경로를 받아 Bitmap으로 return
    public static Bitmap getGalleryImageFromFile(Context context, String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 10;

        return BitmapFactory.decodeFile(filePath, options);
    }
}
