package watchhome.play.yale.phonecamera.watchhome.play.yale.phonecamera.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import watchhome.play.yale.phonecamera.R;
import watchhome.play.yale.phonecamera.watchhome.play.yale.phonecamera.util.LogUtils;

public class CameraShowActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback, View.OnClickListener{


    private static Context context = null;
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceholder;
    private Camera camera = null;
    private Button switchButton;

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
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //获取camera对象
        camera = Camera.open();
        try {
            //设置预览监听
            camera.setPreviewDisplay(holder);
            Camera.Parameters parameters = camera.getParameters();

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
            camera.stopPreview();
            camera.release();
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        LogUtils.i("recive msg ,data: "+data);
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
