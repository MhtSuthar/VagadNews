package com.vagad.utils.camera;

import android.content.Intent;
import android.net.Uri;

import java.util.Date;

/**
 * Created  by Android Developer on 12/5/2015.
 */
public interface CameraIntentHelperCallback {

    void onPhotoUriFound(Date dateCameraIntentStarted, Uri photoUri, int rotateXDegrees);

    void deletePhotoWithUri(Uri photoUri);

    void onSdCardNotMounted();

    void onCanceled();

    void onCouldNotTakePhoto();

    void onPhotoUriNotFound();

    void logException(Exception e);

    void onActivityResult(Intent intent, int requestCode);
}
