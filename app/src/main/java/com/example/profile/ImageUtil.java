package com.example.profile;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {
    private static ImageUtil imageUtil = null;
    private Context context;

    private ImageUtil(Context context) {
        this.context = context;
    }

    //싱글톤 패턴으로 구현
    public static ImageUtil getInstance(Context context) {
        if (imageUtil == null) {
            imageUtil = new ImageUtil(context);
        }
        return imageUtil;
    }

    //인앱 저장소에 프로필 사진이 있는지..(있으면 Bitmap 반환, 없으면 null 반환)
    public Bitmap getImageDir() {
        File fileFile = new File(context.getFilesDir() + "/Profile.jpg");

        if (fileFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(fileFile.getAbsolutePath());
            return bitmap;
        }
        return null;
    }

    //Bitmap과 파일이름을 받아서 인앱 저장소에 저장하는 함수
    public void saveBitmapToJpeg(Bitmap bitmap, String name) {
        //내부저장소 캐시 경로를 받아옵니다.
        File storage = context.getFilesDir();

        //저장할 파일 이름
        String fileName = name + ".jpg";

        //storage 에 파일 인스턴스를 생성합니다.
        File tempFile = new File(storage, fileName);

        try {
            // 자동으로 빈 파일을 생성합니다.
            tempFile.createNewFile();
            // 파일을 쓸 수 있는 스트림을 준비합니다.
            FileOutputStream out = new FileOutputStream(tempFile);
            // compress 함수를 사용해 스트림에 비트맵을 저장합니다.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();//Stream 종료
        } catch (FileNotFoundException e) {
            Log.e("ImageUtil_SaveBitmapToJpeg", "FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("ImageUtil_SaveBitmapToJpeg", "IOException : " + e.getMessage());
        }
    }

    //Intent data : 겔러리로 부터 받은 경로값을 받아와서 Bitmap으로 변환하여 반환
    public Bitmap getGallery(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);

        if (cursor == null || cursor.getCount() < 1) {
            return null;
        }
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

        if (columnIndex < 0)
            return null; //

        //선택한 파일 경로
        String picturePath = cursor.getString(columnIndex);
        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
        cursor.close();
        return bitmap;
    }

    //카메라로 부터 값을 받아와서 Bitmap으로 변환
    public Bitmap getCamera(Intent data) {
        Bundle extras = data.getExtras();
        Bitmap bitmap = (Bitmap) extras.get("data");
        return bitmap;
    }
}
