package com.example.likealion_mini_project.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.likealion_mini_project.R;
import com.example.likealion_mini_project.callback.DialogCallback;
import com.example.likealion_mini_project.databinding.DialogImageBinding;

public class DialogUtil {
    //이 클래스 자체가.. 객체를 반복적으로 생성해서 각자의 메모리에 데이터를 유지하기 위한 목적이 아니고..
    //여러곳에서 사용하는 코드의 중복을 피하기 위해서 이곳에 담아놓고.. 필요한 곳에서 호출해서 쓰겠다는 의도..
    //그 함수를 object member 로 만들 필요가 있을까???? 보통의 경우 static member 로...
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // custom dialog가 필요한 순간 호출하는 메서드
    public static void showCustomDialog(Context context, Bitmap bitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        DialogImageBinding binding = DialogImageBinding.inflate(LayoutInflater.from(context));
        binding.ivDialog.setImageBitmap(bitmap);
        builder.setView(binding.ivDialog);

        AlertDialog dialog = builder.create();
        dialog.show();

        // dialog 사이즈를 이미지 사이즈와 동일하게 ! // 밑의 코드가 없다면 dialog 기본 크기로 출력 되어 여백이 많다
        // show 이후 변경해야한다.
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(bitmap.getWidth(), bitmap.getHeight());
        }
    }

    // 메세지를 뿌리고 밑에 ok, cancel 버튼을 가진 다이얼로그
    // ok, cancel에 대한 클릭 이벤트 처리는 호출하는 곳에서 처리함  // 각각 이벤트 처리가 다르므로
    // 이 함수가 하는 일은.. dialog를 생성해서 출력한다. 하지만 ! ok,cancel 버튼에 대한 이벤트 처리만 위임함 !
    public static void showMessageDialog(Context context,
                                         String message,
                                         String positiveText,
                                         String negativeText,
                                         DialogCallback dialogCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("Message");
        builder.setMessage(message);

        if (positiveText != null) {
            builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogCallback.onPositiveCall();
                }
            });
        }
        if (negativeText != null) {
            builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogCallback.onNegativeCall();
                }
            });
        }

        builder.show();

    }

}
