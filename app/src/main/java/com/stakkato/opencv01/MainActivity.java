package com.stakkato.opencv01;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static org.opencv.android.CameraBridgeViewBase.CAMERA_ID_FRONT;

public class MainActivity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;
    private static final int    MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private TextView txtInfo;
    private Button btnNormal, btnCanny, btnBW;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        txtInfo     = findViewById(R.id.txtInfo);
        btnNormal   = findViewById(R.id.btnNormal);
        btnCanny    = findViewById(R.id.btnCanny);
        btnBW    = findViewById(R.id.btnBW);

        btnNormal.setOnClickListener(buttonListener);
        btnCanny.setOnClickListener(buttonListener);
        btnBW.setOnClickListener(buttonListener);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.imgCanvas);

        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)) {
            txtInfo.setVisibility(View.VISIBLE);
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            txtInfo.setVisibility(View.INVISIBLE);
            openCameraWithOpenCV();
        }
    }

    public void openCameraWithOpenCV() {
        txtInfo.setVisibility(View.INVISIBLE);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCameraIndex(CAMERA_ID_FRONT);

        mOpenCvCameraView.setCvCameraViewListener(this); // <-- implements CvCameraViewListener2
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        boolean canUseCamera = false;

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canUseCamera = true;
                }

                if (!canUseCamera) {
                    Toast.makeText(this, "Cannot use this feature without requested permission", Toast.LENGTH_SHORT).show();
                } else {
                    openCameraWithOpenCV();
                }
            }
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    /**
     * onClick
     */
    private Mat mFilter = null;
    private int filterIndex = 0;

    private View.OnClickListener buttonListener = new View.OnClickListener() {
        public void onClick(View v) {
            Log.i("CLICKED", "Button pressed with ID " + v.getId());
            switch(v.getId()) {
                case R.id.btnNormal:
                    mFilter = null;
                    filterIndex = 0;
                    break;
                case R.id.btnCanny:
                    filterIndex = 1;
                    mFilter = new Mat(mOpenCvCameraView.getWidth(), mOpenCvCameraView.getHeight(), CvType.CV_8UC2);
                    break;
                case R.id.btnBW:
                    filterIndex = 2;
                    mFilter = null;
                    break;
            }
        }
    };


    // What to do when camera opens...
    public void onCameraViewStarted(int width, int height) {
//        if(mFilter == null) {
//            mFilter = new Mat(width, height, CvType.CV_8UC2);
//        }
    }

    // What to do with EVERY received frame
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        switch(filterIndex) {
            case 0:
                return inputFrame.rgba();
            case 1:
                Imgproc.Canny(inputFrame.gray(), mFilter, 120, 200);
                break;
            case 2:
                mFilter = inputFrame.gray();
                break;
            default:
                return inputFrame.rgba();
        }
        return mFilter;
    }

    public void onCameraViewStopped() {
        if (mFilter != null) {
            mFilter.release();
        }
    }

}