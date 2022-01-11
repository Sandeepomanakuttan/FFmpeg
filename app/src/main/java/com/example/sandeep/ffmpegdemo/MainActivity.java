package com.example.sandeep.ffmpegdemo;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.VideoView;

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

    FrameLayout frmout;
    Uri uri;
    VideoView videoView;
    String TAG = this.getClass().getSimpleName();
    FFmpeg ffmpeg;
    String newFileName= "/sdcard/Movies/inshot/v";
    String newFileAudio= "/sdcard/Movies/inshot/i";
    String newFilehorizantal= "/sdcard/Movies/inshot/h";
    String newFilevertical= "/sdcard/Movies/inshot/ver";
    String watermark="/sdcard/Movies/inshot/wtr";
    String fadeIn_and_fadeOut ="/sdcard/Movies/inshot/fad";
    File file,file1,file2;
    String s;


    // Used to load the 'ffmpegdemo' library on application startup.
    static {
        System.loadLibrary("ffmpegdemo");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //file = new File("/sdcard/Movies/Instagram/VID_20321116_050009_343.mp4");
        file1 = new File("/sdcard/Movies/Instagram/VID_19850220_014350_854.mp4");
        file2 = new File("/sdcard/Movies/Instagram/watermark.png");
        //long size = file.length();
        //long filezize = size / 1024;
        videoView = findViewById(R.id.videoView);
        frmout = findViewById(R.id.frmout);
        frmout.setVisibility(View.GONE);

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 getimage();
            }
        });




    }

    private void getimage() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent,100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100 && resultCode==RESULT_OK){
            uri =data.getData();
            if (uri!=null){
                videoView.setVideoURI(uri);
                videoView.start();
                s=getPath(uri);
            }else{
                Toast.makeText(this, "exit", Toast.LENGTH_SHORT).show();
            }

        }
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
                    executeFFmpegCmd(cmd);
                }


                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

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
                    frmout.setVisibility(View.VISIBLE);
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
                    frmout.setVisibility(View.GONE);
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
        if (s!=null) {
        final String[] cmd=new String[]{"-y","-i",s,"-r","20","-s","352*288","-vb","400k","-acodec","aac","-strict","experimental","-ac","1","-ar","8000","-ab","24k",newFileName+".3gp"};
        permission(cmd);
        }else{
            Toast.makeText(this, "Please select video", Toast.LENGTH_SHORT).show();
        }
    }

//convert Mp4 to Mp3

    public void convertMp3(View view) {
            if (s!=null) {
        final String[] cmd =new String[]{"-y","-i",s,"-b:a","192","-vn",newFileAudio+".mp3"};
        permission(cmd);
            }else{
                Toast.makeText(this, "Please select video", Toast.LENGTH_SHORT).show();
            }
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
        if (s!=null) {
            final String[] cmd = new String[]{"-y", "-i", s, "-i", file1.getPath(), "-filter_complex", "hstack=inputs=2", newFilehorizantal + ".mp4"};
            permission(cmd);
        }else{
            Toast.makeText(this, "Please select video", Toast.LENGTH_SHORT).show();
        }
    }

    public void vertical(View view) {
        if (s!=null) {
        final String[] cmd = new String[]{"-y","-i", s, "-i", s,"-filter_complex","vstack=inputs=2",newFilevertical+".mp4"};
        permission(cmd);
        }else{
            Toast.makeText(this, "Please select video", Toast.LENGTH_SHORT).show();
        }

    }

    public void watermark(View view) {
        if (s!=null) {
        final String[] cmd = new String[]{"-y","-i", s, "-i", file2.getPath(),"-filter_complex","overlay=x=main_w-overlay_w-10:y=main_h-overlay_h-10",watermark+".mp4"};
        permission(cmd);
    }else{
        Toast.makeText(this, "Please select video", Toast.LENGTH_SHORT).show();
    }
    }

    public void fade(View view) {
        if (s!=null) {
        final String[] command = new String[]{"-y","-i", s, "-acodec", "copy", "-vf", "fade=t=in:st=0:d=3, fade=t=out:st=12:d=3",fadeIn_and_fadeOut+".mp4"};
        permission(command);
    }else{
        Toast.makeText(this, "Please select video", Toast.LENGTH_SHORT).show();
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
}

