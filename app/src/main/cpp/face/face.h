//
// Created by chendong on 2018/1/23.
//
#include <jni.h>

#ifndef OPENCV4ANDROIDWITHCMAKE_FACE_H
#define OPENCV4ANDROIDWITHCMAKE_FACE_H
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_opencv_samples_fd_DetectionBasedTracker
 * Method:    nativeCreateObject
 * Signature: (Ljava/lang/String;F)J
 */
JNIEXPORT jlong JNICALL
Java_com_martin_ads_testopencv_utils_DetectionBasedTracker_nativeCreateObject
        (JNIEnv *, jclass, jstring, jint);

/*
 * Class:     org_opencv_samples_fd_DetectionBasedTracker
 * Method:    nativeDestroyObject
 * Signature: (J)V
 */
JNIEXPORT void JNICALL
Java_com_martin_ads_testopencv_utils_DetectionBasedTracker_nativeDestroyObject(JNIEnv *, jclass,
                                                                               jlong);

/*
 * Class:     org_opencv_samples_fd_DetectionBasedTracker
 * Method:    nativeStart
 * Signature: (J)V
 */
JNIEXPORT void JNICALL
Java_com_martin_ads_testopencv_utils_DetectionBasedTracker_nativeStart
        (JNIEnv *, jclass, jlong);

/*
 * Class:     org_opencv_samples_fd_DetectionBasedTracker
 * Method:    nativeStop
 * Signature: (J)V
 */
JNIEXPORT void JNICALL
Java_com_martin_ads_testopencv_utils_DetectionBasedTracker_nativeStop
        (JNIEnv *, jclass, jlong);

/*
 * Class:     org_opencv_samples_fd_DetectionBasedTracker
 * Method:    nativeSetFaceSize
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL
Java_com_martin_ads_testopencv_utils_DetectionBasedTracker_nativeSetFaceSize
        (JNIEnv *, jclass, jlong, jint);

/*
 * Class:     org_opencv_samples_fd_DetectionBasedTracker
 * Method:    nativeDetect
 * Signature: (JJJ)V
 */
JNIEXPORT void JNICALL
Java_com_martin_ads_testopencv_utils_DetectionBasedTracker_nativeDetect
        (JNIEnv *, jclass, jlong, jlong, jlong);

#ifdef __cplusplus
}
#endif
#endif