package com.example.profile;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class ProfileEditDialog extends Dialog implements View.OnClickListener {

    private ProfileEditDialogListener profileEditDialogListener;

    private Button btn_Camera;
    private Button btn_Gallery;
    private ImageView iv_Cancel;

    public ProfileEditDialog(@NonNull Context context) {
        super(context);
    }


    //인터페이스 설정
    interface ProfileEditDialogListener {
        void onPositiveClicked(String result);

        void onNegativeClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(ProfileEditDialogListener profileEditDialogListener) {
        this.profileEditDialogListener = profileEditDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_profile);

        btn_Camera = findViewById(R.id.btn_Camera);
        btn_Gallery = findViewById(R.id.btn_Gallery);
        iv_Cancel = findViewById(R.id.iv_Cancel);

        //버튼 클릭 리스너 등록
        btn_Camera.setOnClickListener(this);
        btn_Gallery.setOnClickListener(this);
        iv_Cancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Camera:   //인터페이스 함수 호출하여 결과값을 MainActivity에 반환
                profileEditDialogListener.onPositiveClicked("Camera");
                dismiss();
                break;
            case R.id.btn_Gallery:  //인터페이스 함수 호출하여 결과값을 MainActivity에 반환
                profileEditDialogListener.onPositiveClicked("Gallery");
                dismiss();
                break;
            case R.id.iv_Cancel:    //종료
                profileEditDialogListener.onNegativeClicked();
                dismiss();
                break;
        }
    }


}