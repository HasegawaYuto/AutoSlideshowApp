package jp.techacademy.yuuto.hasegawa.autoslideshowapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Build;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.net.Uri;
import android.content.ContentUris;
import android.util.Log;
import android.widget.ImageView;
import android.view.View;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import android.widget.Button;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Timer mTimer;
    Integer no = 0;

    Handler mHandler = new Handler();

    Button startButton;
    Button backButton;
    Button nextButton;

    ImageView imageVIew;

    ArrayList<Uri> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.startButton);
        backButton = (Button) findViewById(R.id.backButton);
        nextButton = (Button) findViewById(R.id.nextButton);
        imageVIew = (ImageView) findViewById(R.id.imageView);

        Log.d("DEBUG_PRINT","hoge");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }

        if(no == 0){
            backButton.setVisibility(View.INVISIBLE);
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimer == null) {
                    mTimer = new Timer();
                    backButton.setVisibility(View.INVISIBLE);
                    nextButton.setVisibility(View.INVISIBLE);
                    startButton.setText("STOP");
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            no++;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    setImage(no);
                                }
                            });
                        }
                    }, 2000, 2000);
                } else {
                    mTimer.cancel();
                    mTimer = null;
                    backButton.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.VISIBLE);
                    startButton.setText("START");
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (no >= 1) {
                    no--;
                    setImage(no);
                }else{
                    backButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        no++;
                        setImage(no);
                        backButton.setVisibility(View.VISIBLE);
                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()) {
            do {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = cursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                imageList.add(imageUri);
                //ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                //imageVIew.setImageURI(imageUri);
            } while (cursor.moveToNext());
            imageVIew.setImageURI(imageList.get(0));
        }
        Log.d("DEBUG_PRINT",String.valueOf(imageList.size()));
        cursor.close();
    }

    private void setImage(int no){
        if (imageList.size() > 0 ) {
            Integer No = no % imageList.size();
            imageVIew.setImageURI(imageList.get(No));
        }
    }

}
