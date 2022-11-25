package com.example.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
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

        //권한 요청
        PermissionSupport.getInstance(MainActivity.this, MainActivity.this).permissionCheck();

        //이미지뷰 클릭시
        iv_profile = findViewById(R.id.iv_profile);

        //인앱 저장소에 프로필 사진이 있으면 불러와서 이미지 뷰에 갱신
        if(ImageUtil.getInstance(MainActivity.this).getImageDir() != null)
            bitmapToImage(ImageUtil.getInstance(MainActivity.this).getImageDir(),iv_profile);

        //이미지뷰 클릭리스너
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    //다이얼로그 띄우기(겔러리, 카메라 둘중 고르는 것)
                    dialog = new ProfileEditDialog(MainActivity.this);
                    dialog.setDialogListener(new ProfileEditDialog.ProfileEditDialogListener() {
                        @Override
                        public void onPositiveClicked(String result) {
                            switch (result) {
                                case "Camera": //반환값이 카메라 이면 카메라 실행
                                    Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if (imageTakeIntent.resolveActivity(getPackageManager()) != null) {
                                        startActivityForResult(imageTakeIntent, OPEN_CAMERA);
                                    }
                                    break;
                                case "Gallery": //반환값이 겔러리 이면 겔러리 실행
                                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(i, OPEN_GALLERY);
                                    break;
                            }
                        }

                        @Override
                        public void onNegativeClicked() { //취소버튼 누를때
                            Toast.makeText(getApplicationContext(), "취소하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.show();
                } else {//권한이 안되어 있으면 알림
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
        //가져온 사진을 내부저장소에 저장
        switch (requestCode) {
            case OPEN_GALLERY://겔러리 결과값을 받아올때
                if (data != null && resultCode == RESULT_OK) {
                    bitmapToImage(ImageUtil.getInstance(MainActivity.this).getGallery(data), iv_profile);
                    ImageUtil.getInstance(MainActivity.this).saveBitmapToJpeg(ImageUtil.getInstance(MainActivity.this).getGallery(data),"Profile");
                }
                break;
            case OPEN_CAMERA://카메라 결과값을 받아올때
                if (data != null && resultCode == RESULT_OK) {
                    bitmapToImage(ImageUtil.getInstance(MainActivity.this).getCamera(data), iv_profile);
                    ImageUtil.getInstance(MainActivity.this).saveBitmapToJpeg(ImageUtil.getInstance(MainActivity.this).getCamera(data),"Profile");
                }
                break;
        }
    }
    //Bitmap을 ImageView에 갱신시켜주는 함수
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 리턴이 false일 경우 다시 권한 요청
        if (!PermissionSupport.getInstance(MainActivity.this, MainActivity.this).permissionResult(requestCode, permissions, grantResults)) {
            PermissionSupport.getInstance(MainActivity.this, MainActivity.this).requestPermission();
        }
    }

}