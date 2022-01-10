package com.example.sandeep.ffmpegdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Button button;
    String TAG = this.getClass().getSimpleName();
    FFmpeg ffmpeg;
    String newFileName= "/sdcard/Movies/Instagram/output";
    String newFileAudio= "/sdcard/Movies/Instagram/mp3";
    String newFilehorizantal= "/sdcard/Movies/Instagram/horizontal";
    String newFilevertical= "/sdcard/Movies/Instagram/vertical";
    String watermark="/sdcard/Movies/Instagram/Addwatermark";
    String fadeIn_and_fadeOut ="/sdcard/Movies/Instagram/fadeIn_and_fadeOut";
    File file,file1,file2;


    // Used to load the 'ffmpegdemo' library on application startup.
    static {
        System.loadLibrary("ffmpegdemo");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        file = new File("/sdcard/Movies/Instagram/VID_20321116_050009_343.mp4");
        file1 = new File("/sdcard/Movies/Instagram/VID_19850220_014350_854.mp4");
        file2 = new File("/sdcard/Movies/Instagram/watermark.png");
        long size = file.length();
        long filezize = size / 1024;



    }

    /**
     * A native method that is implemented by the 'ffmpegdemo' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public void compressed(String[] cmd) {

        ffmpeg=FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    Toast.makeText(getApplicationContext(), "failure", Toast.LENGTH_SHORT).show();


                }

                @Override
                public void onSuccess() {
                    Toast.makeText(getApplicationContext(), "onsucess", Toast.LENGTH_SHORT).show();
                    executeFFmpegCmd(cmd);
                }

                @Override
                public void onStart() {
                    Toast.makeText(getApplicationContext(), "onstrt", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFinish() {
                    Toast.makeText(getApplicationContext(), "finish", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }

    }


    private void executeFFmpegCmd(String[] cmd) {
        try {
            Log.e(TAG, "executeFFmpegCmd: " + cmd + " " + Arrays.toString(cmd));
//            Log.e(TAG, "executeFFmpegCmd: "+ cmd );

            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.e(TAG, "onStart: ");
                }

                @Override
                public void onProgress(String message) {
                    Log.e(TAG, "onProgress: " + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.e(TAG, "onFailure: " + message);
                }

                @Override
                public void onSuccess(String message) {
                    Log.e(TAG, "onSuccess: " + message);
                }

                @Override
                public void onFinish() {
                    Log.e(TAG, "onFinish:");
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    public void initialize(Context context) {
        ffmpeg = FFmpeg.getInstance(context);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onSuccess() {
                    // FFmpeg is supported by device
                    Log.e(TAG, "onSuccess: Supported");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }
    }

//convert Mp4 to 3gp

    public void compressedmp4(View view) {
        final String[] cmd=new String[]{"-y","-i",file.getPath(),"-r","20","-s","352*288","-vb","400k","-acodec","aac","-strict","experimental","-ac","1","-ar","8000","-ab","24k",newFileName+".3gp"};
        permission(cmd);
    }

//convert Mp4 to Mp3

    public void convertMp3(View view) {
        final String[] cmd =new String[]{"-y","-i",file.getPath(),"-b:a","192","-vn",newFileAudio+".mp3"};
        permission(cmd);
    }


    public void permission(String[] cmd){
        String[] permission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Permissions.check(this, permission, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
              compressed(cmd);
            }
        });
    }


    public void horizontal(View view) {
        final String[] cmd = new String[]{"-y","-i", file.getPath(), "-i", file1.getPath(),"-filter_complex","hstack=inputs=2",newFilehorizantal+".mp4"};
        permission(cmd);
    }

    public void vertical(View view) {

        final String[] cmd = new String[]{"-y","-i", file.getPath(), "-i", file1.getPath(),"-filter_complex","vstack=inputs=2",newFilevertical+".mp4"};
        permission(cmd);
    }

    public void watermark(View view) {
        final String[] cmd = new String[]{"-y","-i", file.getPath(), "-i", file2.getPath(),"-filter_complex","overlay=x=main_w-overlay_w-10:y=main_h-overlay_h-10",watermark+".mp4"};
        permission(cmd);
    }

    public void fade(View view) {
        final String[] command = new String[]{"-y","-i", file1.getPath(), "-acodec", "copy", "-vf", "fade=t=in:st=0:d=3, fade=t=out:st=12:d=3",fadeIn_and_fadeOut+".mp4"};
        permission(command);
    }
}

