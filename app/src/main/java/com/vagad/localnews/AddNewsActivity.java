package com.vagad.localnews;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.model.NewsPostModel;
import com.vagad.utils.Constants;
import com.vagad.utils.DialogUtils;
import com.vagad.utils.camera.BitmapHelper;
import com.vagad.utils.camera.CameraIntentHelper;
import com.vagad.utils.camera.CameraIntentHelperCallback;
import com.vagad.utils.camera.ImageFilePath;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Admin on 15-Feb-17.
 */

public class AddNewsActivity extends BaseActivity {

    private ImageView imgNews;
    private static final String TAG = "AddNewsActivity";
    private EditText edtNewsTitle, edtYourName, edtDesc;
    private CameraIntentHelper mCameraIntentHelper;
    private String imagePath = "";
    private RelativeLayout mRelParent;
    private boolean isGalleryOpen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setupExplodeWindowAnimations(Gravity.BOTTOM);
        setContentView(R.layout.activity_add_news);
        checkPermission();
        setupCameraIntentHelper();
        initView();
        findViewById(R.id.fab_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder.from(AddNewsActivity.this)
                        .setType("message/rfc822")
                        .addEmailTo(getString(R.string.my_email))
                        .setSubject("Your Subject")
                        .setText("")
                        //.setHtmlText(body) //If you are using HTML in your body text
                        .setChooserTitle("Send Suggestions & Requirement")
                        .startChooser();
            }
        });
    }

    private void initView() {
        imgNews = (ImageView) findViewById(R.id.imgNews);
        edtNewsTitle = (EditText) findViewById(R.id.edtNewsTitle);
        edtYourName = (EditText) findViewById(R.id.edtYourName);
        edtDesc = (EditText) findViewById(R.id.edtDesc);
        mRelParent = (RelativeLayout) findViewById(R.id.rel_parent);
    }

    private void fullScreen() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void onClickNewsPhoto(View view){
        final Dialog dialog = new DialogUtils(this).setupCustomeDialogFromBottom(R.layout.dialog_gallery);
        ImageView imgCamera = (ImageView) dialog.findViewById(R.id.imgCamera);
        ImageView imgGallery = (ImageView) dialog.findViewById(R.id.imgGallery);
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openCamera();
            }
        });
        imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openGallery();
            }
        });
        dialog.show();
    }

    public void onClickSubmit(View view){
        if(isValid()){
            addValToFirebase();
        }
    }

    private boolean isValid() {
        if(!isOnline(this)){
            showSnackbar(mRelParent, getString(R.string.no_internet));
            return false;
        }else if(TextUtils.isEmpty(edtNewsTitle.getText().toString().trim())){
            showSnackbar(mRelParent, "Please Enter News Title");
            return false;
        }else if(TextUtils.isEmpty(edtYourName.getText().toString().trim())){
            showSnackbar(mRelParent, "Please Enter Your Name");
            return false;
        }
        return true;
    }

    private void addValToFirebase() {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_NEWS);
        String key = mDatabase.push().getKey();
        NewsPostModel user = new NewsPostModel(edtYourName.getText().toString(), edtDesc.getText().toString(), edtNewsTitle.getText().toString());
        mDatabase.child(key).setValue(user);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: "+dataSnapshot.getKey()+"   "+dataSnapshot.getRef()+""+dataSnapshot.getChildren()+"   "+dataSnapshot.getChildrenCount());
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    NewsPostModel changedPost = messageSnapshot.getValue(NewsPostModel.class);
                    Log.e(TAG, "for : "+changedPost.nameReporter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void checkPermission() {
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this) &&
                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, this) && checkPermission(Manifest.permission.CAMERA, this)) {
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, Constants.REQUEST_PERMISSION_WRITE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Constants.REQUEST_PERMISSION_WRITE_STORAGE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(isGalleryOpen)
                    openGallery();
                else
                    openCamera();
            }
        }
    }

    void openGallery() {
        isGalleryOpen = true;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.REQUEST_OPEN_GALLERY);
    }

    void openCamera(){
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,this) &&
                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, this) && checkPermission(Manifest.permission.CAMERA, this)) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                File photoFile = createImageFile();
                imagePath = photoFile.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mCameraIntentHelper != null) {
                mCameraIntentHelper.startCameraIntent();
            }
        }else{
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, Constants.REQUEST_PERMISSION_WRITE_STORAGE);
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Vagad_" + timeStamp + "_";
        File sdCard = new File(Environment.getExternalStorageDirectory()+"/VagadNews/Images");
        if(!sdCard.exists())
            sdCard.mkdirs();
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    sdCard      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     *
     * @param savedInstanceState Start Camera Intent handler
     */

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mCameraIntentHelper.onSaveInstanceState(savedInstanceState);
    }

    private void setupCameraIntentHelper() {
        mCameraIntentHelper = new CameraIntentHelper(this, new CameraIntentHelperCallback() {
            @Override
            public void onPhotoUriFound(Date dateCameraIntentStarted, Uri photoUri, int rotateXDegrees) {
                if (photoUri != null) {
                    displayImage(photoUri);
                    imagePath = ImageFilePath.getPath(AddNewsActivity.this, photoUri);
                    Log.e(TAG, "Image Camera" + imagePath);
                }
            }

            @Override
            public void deletePhotoWithUri(Uri photoUri) {
                BitmapHelper.deleteImageWithUriIfExists(photoUri, AddNewsActivity.this);
            }

            @Override
            public void onSdCardNotMounted() {
            }

            @Override
            public void onCanceled() {
            }

            @Override
            public void onCouldNotTakePhoto() {
            }

            @Override
            public void onPhotoUriNotFound() {
            }

            @Override
            public void logException(Exception e) {
                Log.e(getClass().getName(), e.getMessage());
            }

            @Override
            public void onActivityResult(Intent intent, int requestCode) {
                startActivityForResult(intent, requestCode);
            }
        });
    }

    /**
     *
     * End  Camera Intent handler
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_OPEN_CAMERA) {
                mCameraIntentHelper.onActivityResult(requestCode, resultCode, data);
            } else if (requestCode == Constants.REQUEST_OPEN_GALLERY) {
                getGalleryImageUri(data);
            }
        }
    }

    private Uri getGalleryImageUri(Intent data) {
        Uri uri = null;
        try {
            Uri imageUri = data.getData();
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = getContentResolver().query(imageUri, projection, null, null,
                    null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            String selectedImagePath = cursor.getString(column_index);
            imagePath = selectedImagePath;
            uri= Uri.fromFile(new File(imagePath));
            Log.e(TAG, "Image Gallery" + imagePath);
            //bitmap = galleryCameraDialog.decodeUri(imageUri);
            displayImage(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    private void displayImage(Uri photoUri) {
        Glide.with(this).load(photoUri).into(imgNews);
    }


}
