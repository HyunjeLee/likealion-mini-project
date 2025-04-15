package com.example.likealion_mini_project.util;

import android.os.Build;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.likealion_mini_project.callback.PermissionCallback;

import java.util.HashSet;
import java.util.Map;

public class PermissionUtil {
    public static void checkAllPermission(ComponentActivity activity, PermissionCallback permissionCallback) {
        HashSet<String> permissionSet = new HashSet<>();

        permissionSet.add("android.permission.CALL_PHONE");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  // 티라미수 대신 33 써도 됨
            permissionSet.add("android.permission.READ_MEDIA_IMAGES");  // 33 미만에서는 이 권한이 존재하지 않음  // 런타임에서는 바로 denied로 err  // 다른 미디어 타입도 필요하다면 전부 각각 추가해야됨
        } else {
            permissionSet.add("android.permission.READ_EXTERNAL_STORAGE");  // 33 미만에서는 각 타입에 대해 권한 요청 필요없이 전역적인 파일시스템 권한만 확보하면 된다
        }

        // 하나의 권한 요청은 RequestPermission
        // 여러 개의 권한 요청은 RequestMultiplePermissions
        ActivityResultLauncher<String[]> launcher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<Map<String, Boolean>>() {  // 인자는 권한 Map
                    @Override
                    public void onActivityResult(Map<String, Boolean> o) {
                        boolean allGranted = true;

                        for(Boolean isGranted: o.values()) {
                            if (!isGranted) {
                                allGranted = false;
                                break;
                            }
                        }

                        permissionCallback.onPermissionResult(allGranted);
                    }
                }
        );

        launcher.launch(permissionSet.toArray(new String[permissionSet.size()]));
    }
}
