package com.example.goputapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static com.example.goputapplication.MatchInfoActivity.actList;

public class MainActivity extends AppCompatActivity {

    int REQUEST_IMAGE_CODE = 1001;
    int REQUEST_EXTERNAL_STORAGE_PERMISSION = 1002;

    de.hdodenhof.circleimageview.CircleImageView imgUser;
    Uri uri;
    Button intoClubBtn;
    Button CreateClubBtn;

    //    public void onClickBtnStyle1(View v){
//        intoClubBtn.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.btnstyle1blue));
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actList.add(this);


        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE_PERMISSION);
            }
        } else {

        }
        imgUser = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.imgUser);
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, REQUEST_IMAGE_CODE);
//                CropImage.startPickImageActivity(MainActivity.this);
//                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
        intoClubBtn = (Button) findViewById(R.id.intoClubBtn);
        intoClubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intoClubBtn.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.btnstylelogout));
                intoClubBtn.setText("클럽 참가");
                intoClubBtn.setTextColor(0xFFFFFFFF);
                Intent intent = new Intent(v.getContext(), InfoAcitivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        CreateClubBtn = (Button) findViewById(R.id.CreateClubBtn);
        CreateClubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateClubBtn.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.btnstylelogout));
                CreateClubBtn.setText("클럽 생성");
                CreateClubBtn.setTextColor(0xFFFFFFFF);
                Intent intent = new Intent(v.getContext(), MakeClubActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgUser.setImageURI(result.getUri());
                imgUser.setBorderWidth(10);
                imgUser.setBorderColor(Color.parseColor("#8FAADC"));
                Toast.makeText(this, "Image Update 성공", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == REQUEST_IMAGE_CODE) {
            Uri image = data.getData();
            startCrop(image);
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);\
//                imgUser.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                Toast.makeText(this, "Oops! 로딩에 오류가 있습니다.", Toast.LENGTH_LONG).show();
//                e.printStackTrace();
//            }
        }else{
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
        }

        //        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            Uri imageuri = CropImage.getPickImageResultUri(this, data);
//            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageuri)) {
//                uri = imageuri;
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
//                        , 0);
//            } else {
////                startCrop(imageuri);
//            }
//        }
    }

    private void startCrop(Uri imageuri) {
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }
    private Bitmap getRoundedCroppedBitmap(Bitmap bitmap) {
        int widthLight = bitmap.getWidth();
        int heightLight = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paintColor = new Paint();
        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);

        RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));

        canvas.drawRoundRect(rectF, widthLight / 2, heightLight / 2, paintColor);

        Paint paintImage = new Paint();
        paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, 0, 0, paintImage);

        return output;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}