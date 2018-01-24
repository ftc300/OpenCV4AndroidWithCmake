package com.martin.ads.testopencv.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.kongqw.permissionslibrary.PermissionsManager;
import com.martin.ads.testopencv.R;
import com.martin.ads.testopencv.activities.object.ObjectDetectingActivity;
import com.martin.ads.testopencv.activities.object.ObjectMainActivity;
import com.martin.ads.testopencv.activities.object.ObjectTrackingActivity;


public class SamplesActivity extends AppCompatActivity {

    Intent sampleIntent;
    private PermissionsManager mPermissionsManager;
    private final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private final int REQUEST_CODE1 = 1;
    private final int REQUEST_CODE2 = 2;
    private final int REQUEST_CODE3 = 3;
    private final int REQUEST_CODE4 = 4;
    private final int REQUEST_CODE5 = 5;
    private final int REQUEST_CODE6 = 6;
    private final int REQUEST_CODE7 = 7;
    private final int REQUEST_CODE8 = 8;
    private final int REQUEST_CODE9 = 9;
    private final int REQUEST_CODE10 = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_samples);
        // 动态权限校验
        mPermissionsManager = new PermissionsManager(this) {

            @Override
            public void authorized(int requestCode) {
                // 权限通过
                switch (requestCode) {
                    case REQUEST_CODE1:
                        startActivity(new Intent(SamplesActivity.this, Tutorial1Activity.class));
                        break;
                    case REQUEST_CODE2:
                        startActivity(new Intent(SamplesActivity.this, Tutorial2Activity.class));
                        break;
                    case REQUEST_CODE3:
                        startActivity(new Intent(SamplesActivity.this, Tutorial3Activity.class));
                        break;
                    case REQUEST_CODE4:
                        startActivity(new Intent(SamplesActivity.this, ImageManipulationsActivity.class));
                        break;
                    case REQUEST_CODE5:
                        startActivity(new Intent(SamplesActivity.this, FaceDetectionActivity.class));
                        break;
                    case REQUEST_CODE6:
                        startActivity(new Intent(SamplesActivity.this, ColorBlobDetectionActivity.class));
                        break;
                    case REQUEST_CODE7:
                        startActivity(new Intent(SamplesActivity.this, CameraCalibrationActivity.class));
                        break;
                    case REQUEST_CODE8:
                        startActivity(new Intent(SamplesActivity.this, Puzzle15Activity.class));
                        break;
                    case REQUEST_CODE9:
                        startActivity(new Intent(SamplesActivity.this, ObjectMainActivity.class));
                        break;
                    case REQUEST_CODE10:
                        startActivity(new Intent(SamplesActivity.this, ScanWatchActivity.class));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void noAuthorization(int requestCode, String[] lacksPermissions) {
                // 缺少必要权限
                showPermissionDialog();
            }

            @Override
            public void ignore(int requestCode) {
                // Android 6.0 以下系统不校验
                authorized(requestCode);
            }
        };
    }

    /**
     * 显示缺少权限的对话框
     */
    protected void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请求权限");
        builder.setMessage("Android 6.0+ 动态请求相机权限");
        builder.setPositiveButton("去设置权限", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                PermissionsManager.startAppSettings(getApplicationContext());
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 复查权限
     *
     * @param requestCode  requestCode
     * @param permissions  permissions
     * @param grantResults grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 用户做出选择以后复查权限，判断是否通过了权限申请
        mPermissionsManager.recheckPermissions(requestCode, permissions, grantResults);
    }


    public void tutorial1(View v) {
        mPermissionsManager.checkPermissions(REQUEST_CODE1, PERMISSIONS);
    }

    public void tutorial2(View v) {
        mPermissionsManager.checkPermissions(REQUEST_CODE2, PERMISSIONS);
    }

    public void tutorial3(View v) {
        mPermissionsManager.checkPermissions(REQUEST_CODE3, PERMISSIONS);
    }

    public void imageManipulations(View v) {
        mPermissionsManager.checkPermissions(REQUEST_CODE4, PERMISSIONS);
    }

    public void faceDetection(View v) {
//        mPermissionsManager.checkPermissions(REQUEST_CODE5, PERMISSIONS);
    }

    public void colorBlobDetection(View v) {
        mPermissionsManager.checkPermissions(REQUEST_CODE6, PERMISSIONS);
    }

    public void cameraCalibration(View v) {
        mPermissionsManager.checkPermissions(REQUEST_CODE7, PERMISSIONS);
    }

    public void puzzle15(View v) {
        mPermissionsManager.checkPermissions(REQUEST_CODE8, PERMISSIONS);
    }

    public void objectAnalysis(View v) {
        mPermissionsManager.checkPermissions(REQUEST_CODE9, PERMISSIONS);
    }
    public void scanWatch(View v) {
        mPermissionsManager.checkPermissions(REQUEST_CODE10, PERMISSIONS);
    }

}
