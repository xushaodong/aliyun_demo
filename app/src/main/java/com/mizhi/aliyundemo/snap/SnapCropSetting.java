/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.mizhi.aliyundemo.snap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.aliyun.common.utils.StorageUtils;
import com.aliyun.demo.crop.AliyunVideoCrop;
import com.aliyun.demo.crop.MediaActivity;
import com.aliyun.demo.recorder.AliyunVideoRecorder;
import com.aliyun.struct.common.CropKey;
import com.aliyun.struct.common.ScaleMode;
import com.aliyun.struct.common.VideoQuality;
import com.aliyun.struct.recorder.CameraType;
import com.aliyun.struct.recorder.FlashType;
import com.aliyun.struct.snap.AliyunSnapVideoParam;
import com.mizhi.aliyundemo.R;
import com.mizhi.aliyundemo.util.Utils;
import com.mizhi.aliyundemo.util.VodResult;

import java.io.File;

/**
 * Created by Administrator on 2017/1/13.
 */

public class SnapCropSetting extends Activity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    String[] eff_dirs;

    private static final int PROGRESS_1_1 = 33;
    private static final int PROGRESS_3_4 = 66;
    private static final int PROGRESS_9_16 = 100;

    private static final int PROGRESS_360P = 25;
    private static final int PROGRESS_480P = 50;
    private static final int PROGRESS_540P = 75;
    private static final int PROGRESS_720P = 100;

    private static final int PROGRESS_LOW = 25;
    private static final int PROGRESS_MEIDAN = 50;
    private static final int PROGRESS_HIGH = 75;
    private static final int PROGRESS_SUPER = 100;

    private static final int DEFAULT_FRAMR_RATE = 25;
    private static final int DEFAULT_GOP = 5;
    private static final int REQUEST_CROP = 2002;

    private TextView startImport;
    private SeekBar resolutionSeekbar,qualitySeekbar, ratioSeekbar;
    private TextView ratioTxt,qualityTxt, resolutionTxt,uploadTxt;
    private RadioButton radioFill,radioCrop;
    private EditText frameRateEdit, gopEdit;
    private ImageView back;
    private int resolutionMode ,ratioMode;
    private VideoQuality videoQulity;
    private ScaleMode cropMode = AliyunVideoCrop.SCALE_CROP;
    private VodResult vodResult;
    private ToggleButton mGpuSwitch = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aliyun_svideo_activity_snap_crop_setting);
        initView();
        initAssetPath();
        copyAssets();
    }

    private void copyAssets() {
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                Utils.copyAll(SnapCropSetting.this);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                startImport.setEnabled(true);
                initAssetPath();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initAssetPath(){
        String path = StorageUtils.getCacheDirectory(this).getAbsolutePath() + File.separator+ Utils.QU_NAME + File.separator;
        File filter = new File(new File(path), "filter");

        String[] list = filter.list();
        if(list == null || list.length == 0){
            return ;
        }
        eff_dirs = new String[list.length + 1];
        eff_dirs[0] = null;
        for(int i = 0; i < list.length; i++){
            eff_dirs[i + 1] = filter.getPath() + "/" + list[i];
        }
//        eff_dirs = new String[]{
//                null,
//                path + "filter/chihuang",
//                path + "filter/fentao",
//                path + "filter/hailan",
//                path + "filter/hongrun",
//                path + "filter/huibai",
//                path + "filter/jingdian",
//                path + "filter/maicha",
//                path + "filter/nonglie",
//                path + "filter/rourou",
//                path + "filter/shanyao",
//                path + "filter/xianguo",
//                path + "filter/xueli",
//                path + "filter/yangguang",
//                path + "filter/youya",
//                path + "filter/zhaoyang"
//        };
    }


    private void initView(){
        startImport = (TextView) findViewById(R.id.start_import);
        startImport.setOnClickListener(this);
        startImport.setEnabled(false);
        resolutionSeekbar = (SeekBar) findViewById(R.id.resolution_seekbar);
        qualitySeekbar = (SeekBar) findViewById(R.id.quality_seekbar);
        ratioSeekbar = (SeekBar) findViewById(R.id.ratio_seekbar);
        ratioTxt = (TextView) findViewById(R.id.ratio_txt);
        qualityTxt = (TextView) findViewById(R.id.quality_txt);
        resolutionTxt = (TextView) findViewById(R.id.resolution_txt);
        uploadTxt = (TextView) findViewById(R.id.upload_progress);
        uploadTxt.setVisibility(View.GONE);
        radioCrop = (RadioButton) findViewById(R.id.radio_crop);
        radioCrop.setOnCheckedChangeListener(this);
        radioFill = (RadioButton) findViewById(R.id.radio_fill);
        radioFill.setOnCheckedChangeListener(this);
        back = (ImageView) findViewById(R.id.aliyun_back);
        back.setOnClickListener(this);
        frameRateEdit = (EditText) findViewById(R.id.frame_rate_edit);
        gopEdit = (EditText) findViewById(R.id.gop_edit);
        mGpuSwitch = (ToggleButton) findViewById(R.id.tbtn_gpu);
        ratioSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress <= PROGRESS_1_1){
                    ratioMode = AliyunSnapVideoParam.RATIO_MODE_1_1;
                    ratioTxt.setText(R.string.aliyun_record_ratio_1_1);
                }else if(progress > PROGRESS_1_1 && progress <= PROGRESS_3_4){
                    ratioMode = AliyunSnapVideoParam.RATIO_MODE_3_4;
                    ratioTxt.setText(R.string.aliyun_record_ratio_3_4);
                }else if(progress > PROGRESS_3_4){
                    ratioMode = AliyunSnapVideoParam.RATIO_MODE_9_16;
                    ratioTxt.setText(R.string.aliyun_reocrd_ratio_9_16);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if(progress <= PROGRESS_1_1){
                    seekBar.setProgress(PROGRESS_1_1);
                }else if(progress > PROGRESS_1_1 && progress <= PROGRESS_3_4){
                    seekBar.setProgress(PROGRESS_3_4);
                }else if(progress > PROGRESS_3_4){
                    seekBar.setProgress(PROGRESS_9_16);
                }
            }
        });
        qualitySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress <= PROGRESS_LOW){
                    videoQulity = VideoQuality.LD;
                    qualityTxt.setText(R.string.aliyun_video_quality_low);
                }else if(progress > PROGRESS_LOW && progress <= PROGRESS_MEIDAN){
                    videoQulity = VideoQuality.SD;
                    qualityTxt.setText(R.string.aliyun_video_quality_meidan);
                }else if(progress > PROGRESS_MEIDAN && progress <= PROGRESS_HIGH){
                    videoQulity = VideoQuality.HD;
                    qualityTxt.setText(R.string.aliyun_video_quality_high);
                }else if(progress > PROGRESS_HIGH){
                    videoQulity = VideoQuality.SSD;
                    qualityTxt.setText(R.string.aliyun_video_quality_super);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if(progress <= PROGRESS_LOW){
                    seekBar.setProgress(PROGRESS_LOW);
                }else if(progress > PROGRESS_LOW && progress <= PROGRESS_MEIDAN){
                    seekBar.setProgress(PROGRESS_MEIDAN);
                }else if(progress > PROGRESS_MEIDAN && progress <= PROGRESS_HIGH){
                    seekBar.setProgress(PROGRESS_HIGH);
                }else if(progress > PROGRESS_HIGH){
                    seekBar.setProgress(PROGRESS_SUPER);
                }
            }
        });
        resolutionSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress <= PROGRESS_360P){
                    resolutionMode = AliyunSnapVideoParam.RESOLUTION_360P;
                    resolutionTxt.setText(R.string.aliyun_record_resolution_360p);
                }else if(progress > PROGRESS_360P && progress <= PROGRESS_480P){
                    resolutionMode = AliyunSnapVideoParam.RESOLUTION_480P;
                    resolutionTxt.setText(R.string.aliyun_record_resolution_480p);
                }else if(progress > PROGRESS_480P && progress <= PROGRESS_540P){
                    resolutionMode = AliyunSnapVideoParam.RESOLUTION_540P;
                    resolutionTxt.setText(R.string.aliyun_record_resolution_540p);
                }else if(progress > PROGRESS_540P){
                    resolutionMode = AliyunSnapVideoParam.RESOLUTION_720P;
                    resolutionTxt.setText(R.string.aliyun_record_resolution_720p);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if(progress < PROGRESS_360P){
                    seekBar.setProgress(PROGRESS_360P);
                } else if (progress > PROGRESS_360P && progress <= PROGRESS_480P) {
                    seekBar.setProgress(PROGRESS_480P);
                } else if(progress > PROGRESS_480P && progress <= PROGRESS_540P){
                    seekBar.setProgress(PROGRESS_540P);
                } else if(progress > PROGRESS_540P){
                    seekBar.setProgress(PROGRESS_720P);
                }
            }
        });
        ratioSeekbar.setProgress(PROGRESS_3_4);
        resolutionSeekbar.setProgress(PROGRESS_540P);
        qualitySeekbar.setProgress(PROGRESS_HIGH);

    }

    @Override
    public void onClick(View v) {
        if(v == startImport){
            String g = gopEdit.getText().toString();
            int gop ;
            if(g == null || g.isEmpty()){
                gop = DEFAULT_GOP;
            }else{
                gop = Integer.parseInt(gopEdit.getText().toString());
            }
            String f = frameRateEdit.getText().toString();
            int frameRate ;
            if(f == null || f.isEmpty()){
                frameRate = DEFAULT_FRAMR_RATE;
            }else{
                frameRate = Integer.parseInt(frameRateEdit.getText().toString());
            }
            AliyunSnapVideoParam mCropParam = new AliyunSnapVideoParam.Builder()
                    .setFrameRate(frameRate)
                    .setGop(gop)
                    .setCropMode(cropMode)
                    .setVideoQuality(videoQulity)
                    .setResolutionMode(resolutionMode)
                    .setRatioMode(ratioMode)
                    .setNeedRecord(true)
                    .setMinVideoDuration(2000)
                    .setMaxVideoDuration(60 * 1000 * 1000)
                    .setMinCropDuration(3000)
                    .setCropUseGPU(mGpuSwitch.isChecked())
                    /**
                     * 录制参数
                     */
                    .setRecordMode(AliyunSnapVideoParam.RECORD_MODE_AUTO)
                    .setFilterList(eff_dirs)
                    .setBeautyLevel(80)
                    .setBeautyStatus(false)
                    .setCameraType(CameraType.BACK)
                    .setFlashType(FlashType.ON)
                    .setMaxDuration(30000)
                    .setMinDuration(2000)
                    .setNeedClip(true)
                    .build();
            AliyunVideoCrop.startCropForResult(this,REQUEST_CROP,mCropParam);
        }else if(v ==  back){
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CROP){
            if(resultCode == Activity.RESULT_OK && data!= null){
                int type = data.getIntExtra(MediaActivity.RESULT_TYPE,0);
                if(type ==  MediaActivity.RESULT_TYPE_CROP){
                    String path = data.getStringExtra(CropKey.RESULT_KEY_CROP_PATH);
                    Toast.makeText(this,"文件路径为 "+ path + " 时长为 " + data.getLongExtra(CropKey.RESULT_KEY_DURATION,0), Toast.LENGTH_SHORT).show();

                }else if(type ==  MediaActivity.RESULT_TYPE_RECORD){
                    Toast.makeText(this,"文件路径为 "+ data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH), Toast.LENGTH_SHORT).show();
                }
            }else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this,"用户取消裁剪", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            if(buttonView == radioCrop){
                cropMode = AliyunVideoCrop.SCALE_CROP;
                if(radioFill != null){
                    radioFill.setChecked(false);
                }
            }else if(buttonView ==  radioFill){
                cropMode = AliyunVideoCrop.SCALE_FILL;
                if(radioCrop != null){
                    radioCrop.setChecked(false);
                }
            }
        }
    }
}
