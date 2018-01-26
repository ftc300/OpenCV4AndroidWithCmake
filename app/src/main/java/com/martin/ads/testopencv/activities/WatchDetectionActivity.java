package com.martin.ads.testopencv.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.martin.ads.testopencv.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class WatchDetectionActivity extends AppCompatActivity implements CvCameraViewListener2 {
    private static final String TAG = "Tutorial1Activity";
    private Mat grayscaleImage;
    private Mat rgbMat;
    private CameraBridgeViewBase mOpenCvCameraView;
    private CascadeClassifier cascadeClassifier;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    initializeOpenCVDependencies();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    private void initializeOpenCVDependencies() {
        try {
            // Copy the resource into a temp file so OpenCV can load it
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            // Load the cascade classifier
            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            cascadeClassifier.load(mCascadeFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e("OpenCVActivity", "Error loading cascade", e);
        }
        // And we are ready to go
        mOpenCvCameraView.enableView();
    }

    public WatchDetectionActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_tutorial1);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
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

    public void onCameraViewStarted(int width, int height) {
        grayscaleImage = new Mat(height, width, CvType.CV_8UC4);
        rgbMat = new Mat(height, width, CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
        grayscaleImage.release();
        rgbMat.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        rgbMat = inputFrame.rgba();
        Imgproc.cvtColor(rgbMat, grayscaleImage, Imgproc.COLOR_RGBA2GRAY);
        MatOfRect matOfRect = new MatOfRect();
        if (cascadeClassifier != null) {
            cascadeClassifier.detectMultiScale(grayscaleImage, matOfRect, 1.1, 3,  Objdetect.CASCADE_SCALE_IMAGE, new Size(200,200),grayscaleImage.size());
        }
        // If there are any stickers found, draw a rectangle around it
        Rect[] rectArray = matOfRect.toArray();
        for (int i = 0; i < rectArray.length; i++)
            Imgproc.rectangle(rgbMat, rectArray[i].tl(), rectArray[i].br(), new Scalar(0, 255, 0, 255), 3);

        return rgbMat;
    }

    private Size getSize(Mat gray, float relativeObjectWidth, float relativeObjectHeight) {
        Size size = gray.size();
        int cameraWidth = gray.cols();
        int cameraHeight = gray.rows();
        int width = Math.round(cameraWidth * relativeObjectWidth);
        int height = Math.round(cameraHeight * relativeObjectHeight);
        size.width = 0 >= width ? 0 : (cameraWidth < width ? cameraWidth : width); // width [0, cameraWidth]
        size.height = 0 >= height ? 0 : (cameraHeight < height ? cameraHeight : height); // height [0, cameraHeight]
        return size;
    }
}
