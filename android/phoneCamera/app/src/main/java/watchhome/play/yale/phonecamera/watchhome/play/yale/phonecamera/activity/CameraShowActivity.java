package watchhome.play.yale.phonecamera.watchhome.play.yale.phonecamera.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.IOException;

import watchhome.play.yale.phonecamera.R;
import watchhome.play.yale.phonecamera.watchhome.play.yale.phonecamera.codec.AvcEncoder;
import watchhome.play.yale.phonecamera.watchhome.play.yale.phonecamera.util.LogUtils;

public class CameraShowActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback, View.OnClickListener{


    private static Context context = null;
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceholder;
    private Camera camera = null;
    private Button switchButton;
    AvcEncoder avcCodec;
    int width = 720;
    int height = 1280;
    int framerate = 20;
    int bitrate = 2500000;

    byte[] h264 = new byte[width*height*3/2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_show);
        initCamera();
    }

    private void initCamera(){

        context = this;
        surfaceview = (SurfaceView)findViewById(R.id.camera_show);
        surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceholder.addCallback(CameraShowActivity.this);

        switchButton = (Button)findViewById(R.id.switch_camera);
        switchButton.setOnClickListener(this);

        // 方法1 Android获得屏幕的宽和高
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = screenWidth = display.getWidth();
        int screenHeight = screenHeight = display.getHeight();

        avcCodec = new AvcEncoder(screenWidth,screenHeight,framerate,bitrate);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //获取camera对象
        camera = Camera.open();
        try {
            //设置预览监听
            camera.setPreviewDisplay(holder);
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(width, height);
            parameters.setPictureSize(width, height);
            parameters.setPreviewFormat(ImageFormat.YV12);

            if (this.getResources().getConfiguration().orientation
                    != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                camera.setDisplayOrientation(90);
                parameters.setRotation(90);
            } else {
                parameters.set("orientation", "landscape");
                camera.setDisplayOrientation(0);
                parameters.setRotation(0);
            }
            camera.setParameters(parameters);
            //启动摄像头预览
            camera.startPreview();
            camera.setPreviewCallback(this);

        } catch (IOException e) {
            e.printStackTrace();
            camera.release();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.setPreviewCallback(null) ;
            camera.stopPreview();
            camera.release();
        }
        avcCodec.close();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //编码视频数据
        LogUtils.i("video encode", "data length : "+data.length);
        if(avcCodec != null){
            int ret = avcCodec.offerEncoder(data, h264);
            LogUtils.d("video encode", "Video data encode ret: "+ret);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_camera:{
                switchCamera();
                break;
            }
        }
    }

    private void switchCamera(){

    }
}
