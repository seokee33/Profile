package com.example.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private CircleImageView iv_profile;//이미지뷰

    ProfileEditDialog dialog;
    private final static int OPEN_GALLERY = 101;
    private final static int OPEN_CAMERA = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionSupport.getInstance(MainActivity.this, MainActivity.this).permissionCheck();

        //이미지뷰 클릭리스너
        iv_profile = findViewById(R.id.iv_profile);
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    dialog = new ProfileEditDialog(MainActivity.this);
                    dialog.setDialogListener(new ProfileEditDialog.ProfileEditDialogListener() {
                        @Override
                        public void onPositiveClicked(String result) {
                            switch (result) {
                                case "Camera":
                                    Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if (imageTakeIntent.resolveActivity(getPackageManager()) != null) {
                                        startActivityForResult(imageTakeIntent, OPEN_CAMERA);
                                    }
                                    break;
                                case "Gallery":
                                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(i, OPEN_GALLERY);
                                    break;
                            }
                        }

                        @Override
                        public void onNegativeClicked() {
                            Toast.makeText(getApplicationContext(), "취소하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.show();
                } else {
                    Toast.makeText(getApplicationContext(), "권한을 설정해주셔야 프로필 사진 변경이 가능합니다.\n애플리케이션 정보 -> 권한 -> 저장공간 허용", Toast.LENGTH_SHORT).show();
                    PermissionSupport.getInstance(MainActivity.this, MainActivity.this).permissionCheck();
                }
            }
        });

    }

    //겔러리로 부터 사진 주소 결과값을 받아와서 이미지뷰에 이미지 넣는 곳
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case OPEN_GALLERY://겔러리 결과값을 받아올때
                if (data != null && resultCode == RESULT_OK) {
                    bitmapToImage(ImageUtil.getInstance(MainActivity.this).getGallery(data), iv_profile);
                }
                break;
            case OPEN_CAMERA:
                if (data != null && resultCode == RESULT_OK) {
                    bitmapToImage(ImageUtil.getInstance(MainActivity.this).getCamera(data), iv_profile);
                }
                break;
        }
        dialog.dismiss();
    }

    private void bitmapToImage(Bitmap bitmap, ImageView imageView) {
        Glide.with(this).asBitmap().load(bitmap)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                    }
                });
    }


    // 권한 체크
    // Request Permission에 대한 결과 값을 받는다.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 리턴이 false일 경우 다시 권한 요청
        if (!PermissionSupport.getInstance(MainActivity.this, MainActivity.this).permissionResult(requestCode, permissions, grantResults)) {
            PermissionSupport.getInstance(MainActivity.this, MainActivity.this).requestPermission();
        }
    }

}