package com.martin.ads.testopencv.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;

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
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ScanWatchActivity extends AppCompatActivity implements CvCameraViewListener2 {
    private static final String TAG = "ScanWatchActivity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean mIsJavaCamera = true;
    private MenuItem mItemSwitchCamera = null;
    private TextView tvCurrent;
    private Mat srcImgMat;
    private Mat midImgMat;
    private Mat dstImgMat;
    private Mat circlesMat;
    private Mat linesMat;
    double eps = 0.0000000001;
    double PI = Math.acos(-1.0);
    Point center;
    List<MyLine> list_MyLine = new ArrayList<>();


    static {
        System.loadLibrary("native-lib");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");

                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    public ScanWatchActivity() {
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
        setContentView(R.layout.activity_scanwatch);
        tvCurrent = findViewById(R.id.currentTime);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setMaxFrameSize(360, 360);
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
        midImgMat = new Mat(height, width, CvType.CV_8UC4);
        srcImgMat = new Mat(height, width, CvType.CV_8UC4);
        dstImgMat = new Mat(height, width, CvType.CV_8UC4);
        linesMat = new Mat(height, width, CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
        midImgMat.release();
        srcImgMat.release();
        dstImgMat.release();
        circlesMat.release();
        linesMat.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        srcImgMat = inputFrame.rgba();
        midImgMat = inputFrame.gray();
//        Imgproc.cvtColor(srcImgMat, midImgMat, Imgproc.COLOR_BGR2GRAY, 4);
        Imgproc.GaussianBlur(midImgMat, dstImgMat, new Size(9, 9


        ), 0);
        Imgproc.Canny(dstImgMat, dstImgMat, 20, 50);
        circlesMat = new MatOfRect();
//        (image, circlesMat, method, dp, minDist, param1, param2, minRadius, maxRadius)
        Imgproc.HoughCircles(dstImgMat, circlesMat, Imgproc.CV_HOUGH_GRADIENT, 1, 50);
        if (circlesMat.cols() > 0) {
            Log.d(TAG, "circlesMat.cols:" + circlesMat.cols());
            for (int i = 0; i < circlesMat.cols(); i++) {
                double vCircle[] = circlesMat.get(0, i);
                center = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
                int radius = (int) Math.round(vCircle[2]);
                // draw the circle center
                Imgproc.circle(dstImgMat, center, radius, new Scalar(255, 0, 0), 1);
                // draw the circle outline
                Imgproc.circle(dstImgMat, center, 3, new Scalar(0, 0, 255), -1);
            }
        }

        Imgproc.HoughLinesP(dstImgMat, linesMat, 1, Math.PI / 180, 4, 2, 0);
        if (linesMat.cols() > 0 && center != null) {
            Log.d(TAG, "linesMat.cols:" + linesMat.cols());
            for (int i = 0; i < linesMat.cols(); i++) {
                double vl[] = linesMat.get(0, i);
                Point A = new Point(vl[0], vl[1]);
                Point B = new Point(vl[2], vl[3]);
                if (distance2Segment(center, A, B) < 40)//根据圆心到指针的距离阈值滤掉其他线段
                {
                    boolean down = (A.y + B.y - 2 * center.y > 0);//判断长的在过圆心的水平线上部还是下部
                    if (A.x == B.x) {//斜率为无穷的情况
                        list_MyLine.add(new MyLine(i, 90 + (down ? 180 : 0), getLength(A, B)));
                    } else if (A.y == B.y) {//水平的情况
                        list_MyLine.add(new MyLine(i, (A.x + B.x - 2 * center.x > 0) ? 0 : 180, getLength(A, B)));
                    } else {
                        if (down) {
                            if (A.y > center.y)
                                list_MyLine.add(new MyLine(i, 360 - getSlope(A, B), getLength(A, B)));
                            else
                                list_MyLine.add(new MyLine(i, 360 - getSlope(A, B), getLength(A, B)));
                        } else {
                            if (A.y < center.y)
                                list_MyLine.add(new MyLine(i, getSlope(A, B), getLength(A, B)));
                            else
                                list_MyLine.add(new MyLine(i, getSlope(A, B), getLength(A, B)));
                        }
                    }
//                    Imgproc.line(srcImgMat, A, B, new Scalar(0, 0, i * 20 + 40));
                }
                Imgproc.line(srcImgMat, A, B, new Scalar(0, 0, 255),2);
            }

        }

        return srcImgMat;
    }

    double distance2Segment(Point P, Point A, Point B) {//点到线段的距离
        Point v1 = Minus(B, A);
        Point v2 = Minus(P, A);
        Point v3 = Minus(P, B);
        if (A == B) return Length(v2);
        if (dcmp(Dot(v1, v2)) < 0) return Length(v2);
        else if (dcmp(Dot(v1, v3)) > 0) return Length(v3);
        else return Math.abs(Cross(v1, v2)) / Length(v1);
    }

    int getSlope(Point A, Point B) { //斜率
        return (int) Math.abs(Math.atan2(A.y - B.y, A.x - B.x) * 180 / PI);
    }

    int getLength(Point A, Point B) { //线段长度
        return (int) Length(Minus(A, B));
    }


    int dcmp(double x) {
        if (Math.abs(x) < eps) return 0;
        else return x < 0 ? -1 : 1;
    }

    double Dot(Point A, Point B) {
        return A.x * B.x + A.y * B.y;
    }//向量点积

    double Length(Point A) {
        return Math.sqrt(Dot(A, A));
    }//向量模长

    double Cross(Point A, Point B) {
        return A.x * B.y - A.y * B.x;
    }//向量叉积

    Point Minus(Point A, Point B) {
        return new Point(A.x - B.x, A.y - B.y);
    }//向量差


    class MyLine {
        public int id;//编号
        public int k;//倾斜角[0-360)
        public int l;//长度

        public MyLine(int ID, int K, int L) {
            id = ID;
            k = K;
            l = L;
        }

        boolean operatorSmallerThan(MyLine A) {
            return k < A.k;
        }//重定义小于号

        void print() {
            Log.d(TAG, String.format("id: %3d  k: %3d°  l: %3d\n", id, k, l));
        }//输出函数
    }

}
