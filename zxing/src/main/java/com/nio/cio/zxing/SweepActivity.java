package com.nio.cio.zxing;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Vector;


/**
 * @author Administrator
 */
public class SweepActivity extends Activity implements Callback {

    private SweepActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;// surface
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.90f;
    private boolean vibrate;//
    private SweepView sweepView;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sweepView = new SweepView(this);
        setContentView(sweepView);

        CameraManager.init(getApplication());
        viewfinderView = sweepView.getViewfinderView();
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        sweepView.getButtonAlbums().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZxingUtils.goAlbums(SweepActivity.this);
            }
        });
        sweepView.getBackIB().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
//		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceView surfaceView = sweepView.getSurfaceView();
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
//		initSysRingSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new SweepActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    public void handleDecode(Result obj, Bitmap barcode) {
        decodeResult(obj);
    }

    private void decodeResult(Result obj) {
        if (obj == null) {
            Toast.makeText(this, "解码失败，请重试", Toast.LENGTH_SHORT).show();
            return;
        }
        inactivityTimer.onActivity();
        // viewfinderView.drawResultBitmap(barcode);
        playBeepSoundAndVibrate();
        String str = obj.getText();
        try {
            Intent intent = new Intent();
            intent.putExtra(StaticField.QR_RESULT, str);
            setResult(StaticField.SWEEP_RESULT, intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.qzxing_s);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }


    private Uri getSystemDefultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(this,
                RingtoneManager.TYPE_NOTIFICATION);
    }

    private void initSysRingSound() {
        mediaPlayer = MediaPlayer.create(this, getSystemDefultRingtoneUri());

        mediaPlayer.setLooping(false);
        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || data.getData() == null) {
            return;
        }
        if (requestCode == StaticField.ALBUMS_REQUEST && resultCode == RESULT_OK) {
            paserUriImg(data.getData());
//            ZxingUtils.goCropAlbums(SweepActivity.this, data.getData());
        } else if (requestCode == StaticField.ALBUMS_CROP_REQUEST && resultCode == RESULT_OK) {
//            paserUriImg(ZxingUtils.tempUri);
        }
    }

    private void paserUriImg(Uri tempUri) {
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), tempUri);
            Result result = decode(bmp);
            if (result == null) {
                Toast.makeText(this, "解析图片失败", Toast.LENGTH_SHORT)
                        .show();
            } else {
                decodeResult(result);
//                String RealPath = getRealFilePath(this, tempUri);
//                if (RealPath != null) {
//                    File file = new File(RealPath);
//                    if (file.exists()) {
//                        file.delete();
//                    }
//                }

            }

        } catch (Exception e) {
            Log.e("TAG-->Error", e.toString());
        }
    }

    private void getBitmapp(Intent data) throws IOException {
        Uri originalUri = data.getData();  //获得图片的uri
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(originalUri, proj, null, null, null);
        String img_path = null;
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            img_path = cursor.getString(column_index);
            Log.i("TAG-->path", img_path);
        }
        cursor.close();
        Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), originalUri);
    }

    public static Result decode(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);

        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        Reader reader = new MultiFormatReader();
        try {
            return reader.decode(binaryBitmap);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String recode(String str) {
        String formart = "";

        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder()
                    .canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
                Log.i("1234      ISO8859-1", formart);
            } else {
                formart = str;
                Log.i("1234      stringExtra", str);
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return formart;
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}