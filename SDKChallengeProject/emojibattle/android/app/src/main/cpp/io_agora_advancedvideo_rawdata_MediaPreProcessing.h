/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class io_agora_advancedvideo_rawdata_MediaPreProcessing */

#ifndef _Included_io_agora_advancedvideo_rawdata_MediaPreProcessing
#define _Included_io_agora_advancedvideo_rawdata_MediaPreProcessing
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     io_agora_advancedvideo_rawdata_MediaPreProcessing
 * Method:    setCallback
 * Signature: (Lio/agora/advancedvideo/rawdata/MediaPreProcessing/ProgressCallback;)V
 */
JNIEXPORT void JNICALL Java_io_agora_advancedvideo_rawdata_MediaPreProcessing_setCallback
(JNIEnv *, jclass, jobject);

/*
 * Class:     io_agora_advancedvideo_rawdata_MediaPreProcessing
 * Method:    setVideoCaptureByteBuffer
 * Signature: (Ljava/nio/ByteBuffer;)V
 */
JNIEXPORT void JNICALL Java_io_agora_advancedvideo_rawdata_MediaPreProcessing_setVideoCaptureByteBuffer
(JNIEnv *, jclass, jobject);

/*
 * Class:     io_agora_advancedvideo_rawdata_MediaPreProcessing
 * Method:    setAudioRecordByteBuffer
 * Signature: (Ljava/nio/ByteBuffer;)V
 */
JNIEXPORT void JNICALL Java_io_agora_advancedvideo_rawdata_MediaPreProcessing_setAudioRecordByteBuffer
(JNIEnv *, jclass, jobject);

/*
 * Class:     io_agora_advancedvideo_rawdata_MediaPreProcessing
 * Method:    setAudioPlayByteBuffer
 * Signature: (Ljava/nio/ByteBuffer;)V
 */
JNIEXPORT void JNICALL Java_io_agora_advancedvideo_rawdata_MediaPreProcessing_setAudioPlayByteBuffer
(JNIEnv *, jclass, jobject);

/*
 * Class:     io_agora_advancedvideo_rawdata_MediaPreProcessing
 * Method:    setBeforeAudioMixByteBuffer
 * Signature: (Ljava/nio/ByteBuffer;)V
 */
JNIEXPORT void JNICALL Java_io_agora_advancedvideo_rawdata_MediaPreProcessing_setBeforeAudioMixByteBuffer
(JNIEnv *, jclass, jobject);

/*
 * Class:     io_agora_advancedvideo_rawdata_MediaPreProcessing
 * Method:    setAudioMixByteBuffer
 * Signature: (Ljava/nio/ByteBuffer;)V
 */
JNIEXPORT void JNICALL Java_io_agora_advancedvideo_rawdata_MediaPreProcessing_setAudioMixByteBuffer
(JNIEnv *, jclass, jobject);

/*
 * Class:     io_agora_advancedvideo_rawdata_MediaPreProcessing
 * Method:    setVideoDecodeByteBuffer
 * Signature: (ILjava/nio/ByteBuffer;)V
 */
JNIEXPORT void JNICALL Java_io_agora_advancedvideo_rawdata_MediaPreProcessing_setVideoDecodeByteBuffer
(JNIEnv *, jclass, jint, jobject);

/*
 * Class:     io_agora_advancedvideo_rawdata_MediaPreProcessing
 * Method:    releasePoint
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_io_agora_advancedvideo_rawdata_MediaPreProcessing_releasePoint
(JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif