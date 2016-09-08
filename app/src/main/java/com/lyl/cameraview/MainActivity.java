package com.lyl.cameraview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    public static final String TAG = MainActivity.class.getName();
    private SurfaceView mSurfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.frame_containter);
        surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        try {
            camera.setPreviewDisplay(holder);
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();

            //每个类型的取得的支持的尺寸顺序不一样，有的最大的在最前，有的最小的在最前
            //取第一个判断一个，小于1000，就说明最小的在最前，则将其倒序排列一下。
            Camera.Size s = pictureSizes.get(0);
            if (s.width < 1000) {
                Collections.reverse(pictureSizes);
            }

            Camera.Size size = pictureSizes.get(2);
            parameters.setPictureSize(size.width, size.height);


            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            parameters.setRotation(90);
            // 设置像素值大小,默认为85，区间是1-100
            parameters.setJpegQuality(100);
            camera.setParameters(parameters);

            camera.startPreview();
        } catch (IOException exception) {
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                play();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                play();
                return true;
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                play();
                return true;
        }


        return super.onKeyDown(keyCode, event);
    }

    private void play() {
        camera.takePicture(//
                new Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {

                    }
                },//
                null,//
                new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        take(data, camera);
                        Toast.makeText(getApplication(), "拍照成功", Toast.LENGTH_SHORT).show();
                        camera.startPreview();
                    }
                });
    }

    public void take(byte[] data, Camera camera) {//拍摄完成后保存照片
        try {
            String path = Environment.getExternalStorageDirectory() + "/q" + System.currentTimeMillis() + ".jpg";
            data2file(data, path);
        } catch (Exception e) {
        }
        camera.startPreview();
    }

    private void data2file(byte[] w, String fileName) throws Exception {//将二进制数据转换为文件的函数
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            // 支持压缩
            // 拿到原图
            Bitmap bitmap = BitmapFactory.decodeByteArray(w, 0, w.length);
            // 压缩 格式 ， 质量 ， 图片流
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.write(w);
            out.close();
        } catch (Exception e) {
            if (out != null) out.close();
            throw e;
        }
    }
}

