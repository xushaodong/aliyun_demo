package com.mizhi.aliyundemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aliyun.common.utils.StorageUtils;
import com.aliyun.common.utils.ToastUtil;
import com.aliyun.demo.recorder.AliyunVideoRecorder;
import com.aliyun.struct.common.CropKey;
import com.aliyun.struct.common.ScaleMode;
import com.aliyun.struct.common.VideoQuality;
import com.aliyun.struct.recorder.CameraType;
import com.aliyun.struct.recorder.FlashType;
import com.aliyun.struct.snap.AliyunSnapVideoParam;
import com.mizhi.aliyundemo.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 阿里云短视频基础版demo
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private static final int PERMISSION_CODES = 1001;
    private static final int REQUEST_RECORD = 2001;
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    int min = 2000;
    int max = 30000;
    int gop = 5;
    private boolean permissionGranted = true;
    private Button mStartRecordBT;
    private int resolutionMode, ratioMode;
    private String[] eff_dirs;
    private AliyunSnapVideoParam recordParam;
    private VideoQuality videoQuality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        }
        setContentView(R.layout.activity_main);
        mStartRecordBT = (Button) findViewById(R.id.bt_record);
        initParam();
        mStartRecordBT.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission() {
        List<String> p = new ArrayList<>();
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                p.add(permission);
            }
        }
        if (p.size() > 0) {
            requestPermissions(p.toArray(new String[p.size()]), PERMISSION_CODES);
        }
    }

    private void initParam() {
        String path = StorageUtils.getCacheDirectory(this).getAbsolutePath() + File.separator + Utils.QU_NAME + File.separator;
        File filter = new File(new File(path), "filter");
        String[] list = filter.list();
        if (list == null) {
            list = new String[]{""};
        }
        videoQuality = VideoQuality.SD;
        resolutionMode = AliyunSnapVideoParam.RESOLUTION_720P;
        eff_dirs = new String[list.length + 1];
        eff_dirs[0] = null;
        for (int i = 0; i < list.length; i++) {
            eff_dirs[i + 1] = filter.getPath() + "/" + list[i];
        }
        recordParam = new AliyunSnapVideoParam.Builder()
                .setResolutionMode(resolutionMode)
                .setRatioMode(ratioMode)
                .setRecordMode(AliyunSnapVideoParam.RECORD_MODE_AUTO)
                .setFilterList(eff_dirs)
                .setBeautyLevel(80)
                .setBeautyStatus(true)
                .setCameraType(CameraType.FRONT)
                .setFlashType(FlashType.ON)
                .setNeedClip(true)
                .setMaxDuration(max)
                .setMinDuration(min)
                .setVideoQuality(videoQuality)
                .setGop(gop)

                /**
                 * 裁剪参数
                 */
                .setMinVideoDuration(4000)
                .setMaxVideoDuration(29 * 1000)
                .setMinCropDuration(3000)
                .setFrameRate(25)
                .setCropMode(ScaleMode.PS)
                .setSortMode(AliyunSnapVideoParam.SORT_MODE_PHOTO)
                .build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODES:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    ToastUtil.showToast(this, getString(R.string.need_permission));
                    permissionGranted = false;
                } else {
                    permissionGranted = true;
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RECORD) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                int type = data.getIntExtra(AliyunVideoRecorder.RESULT_TYPE, 0);
                if (type == AliyunVideoRecorder.RESULT_TYPE_CROP) {
                    String path = data.getStringExtra(CropKey.RESULT_KEY_CROP_PATH);
                    Toast.makeText(this, "文件路径为 " + path + " 时长为 " + data.getLongExtra(CropKey.RESULT_KEY_DURATION, 0), Toast.LENGTH_SHORT).show();
                } else if (type == AliyunVideoRecorder.RESULT_TYPE_RECORD) {
                    Toast.makeText(this, "文件路径为 " + data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH), Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "用户取消录制", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        //隐士调用
//        Intent recorder = new Intent("com.aliyun.action.snaprecorder");
//        startActivity(recorder);
        //直接开始录制
        AliyunVideoRecorder.startRecordForResult(this, REQUEST_RECORD, recordParam);
    }


}
