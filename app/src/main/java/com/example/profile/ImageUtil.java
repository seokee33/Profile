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

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class ImageUtil {
    private static ImageUtil imageUtil = null;
    private Context context;

    private String filePath;

    private ImageUtil(Context context) {
        this.context = context;
    }

    public static ImageUtil getInstance(Context context) {
        if (imageUtil == null) {
            imageUtil = new ImageUtil(context);
        }
        return imageUtil;
    }

    public boolean makeFile() {
        // 촬영한 사진을 저장할 파일 생성
        File photoFile = null;

        try {
            //임시로 사용할 파일이므로 경로는 캐시폴더로
            File tempDir = context.getCacheDir();

            //프로파일 세팅
            String imageFileName = "Profile_";

            File tempImage = File.createTempFile(
                    imageFileName,  /* 파일이름 */
                    ".jpg",         /* 파일형식 */
                    tempDir      /* 경로 */
            );

            // ACTION_VIEW 인텐트를 사용할 경로 (임시파일의 경로)
            filePath = tempImage.getAbsolutePath();

            photoFile = tempImage;

        } catch (IOException e) {
            //에러 로그는 이렇게 관리하는 편이 좋다.
            Log.w("FilePathError", "파일 생성 에러!", e);
        }

        //파일이 정상적으로 생성되었다면 계속 진행
        if (photoFile != null) {
            return true;

        }
        return false;
    }

    public Bitmap getGallery(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);

        if (cursor == null || cursor.getCount() < 1) {
            return null; // no cursor or no record. DO YOUR ERROR HANDLING
        }
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

        if (columnIndex < 0) // no column index
            return null; // DO YOUR ERROR HANDLING

        //선택한 파일 경로
        String picturePath = cursor.getString(columnIndex);
        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
        cursor.close();
        return bitmap;
    }

    public Bitmap getCamera(Intent data) {
        Bundle extras = data.getExtras();
        Bitmap bitmap = (Bitmap) extras.get("data");
        return bitmap;
    }
}
