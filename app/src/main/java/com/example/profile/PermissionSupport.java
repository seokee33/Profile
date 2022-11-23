package com.example.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionSupport {
    private static PermissionSupport permissionSupport = null;
    private Context context;
    private Activity activity;

    private PermissionSupport(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }


    public static PermissionSupport getInstance(Context context, Activity activity) {
        if (permissionSupport == null) {
            permissionSupport = new PermissionSupport(context, activity);
        }
        return permissionSupport;
    }


    // Manifset에 권한을 작성 후
    // 요청할 권한을 배열로 저장
    private String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    // 권한 요청을 할 때 발생하는 창에 대한 결과값
    private List<Object> permissionList;

    private final int MULTIPLE_PERMISSIONS = 1023;

    // 허용할 권한 요청이 남았는지 체크
    public boolean checkPermission() {
        int result;
        permissionList = new ArrayList<>();

        // 배열로 저장한 권한 중 허용되지 않은 권한이 있는지 체크
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(context, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }

        return permissionList.isEmpty();
    }

    // 권한 허용 요청
    public void requestPermission() {
        ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]),
                MULTIPLE_PERMISSIONS);
    }

    // 권한 요청에 대한 결과 처리
    public boolean permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSIONS && (grantResults.length > 0)) {
            for (int i = 0; i < grantResults.length; i++) {
                // grantResults == 0 사용자가 허용한 것
                // grantResults == -1 사용자가 거부한 것
                if (grantResults[i] == -1) {
                    return false;
                }
            }
        }
        return true;
    }


    // 권한 체크
    public void permissionCheck() {
        // sdk 23버전 이하 버전에서는 permission이 필요하지 않음
        if (Build.VERSION.SDK_INT >= 23) {
            // 권한 체크한 후에 리턴이 false일 경우 권한 요청을 해준다.
            if (!checkPermission()) {
                requestPermission();
            }
        }
    }


}
