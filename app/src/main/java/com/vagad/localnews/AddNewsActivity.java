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
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.dashboard.NewsListActivity;
import com.vagad.localnews.adapter.ReportNewsRecyclerAdapter;
import com.vagad.model.NewsPostModel;
import com.vagad.storage.SharedPreferenceUtil;
import com.vagad.utils.AnimationUtils;
import com.vagad.utils.AppUtils;
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
import java.util.HashMap;

/**
 * Created by Admin on 15-Feb-17.
 */

public class AddNewsActivity extends BaseActivity {

    private ImageView imgNews;
    private static final String TAG = "AddNewsActivity";
    private EditText edtNewsTitle, edtYourName, edtDesc, edtMobile;
    private CameraIntentHelper mCameraIntentHelper;
    private String imagePath = "";
    private RelativeLayout mRelParent;
    private int isGalleryOpen = -1;
    private AppCompatButton btnSubmit;
    private ProgressBar progressBar;
    private LinearLayout linAdd;

    private NewsPostModel newsPostModel;
    private boolean isEditMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setupExplodeWindowAnimations(Gravity.BOTTOM);
        setContentView(R.layout.activity_add_news);
        checkPermission();
        setupCameraIntentHelper();
        initView();

        /**
         * For Checking where it comes for edit or add
         */
        if(getIntent().hasExtra(Constants.EXTRA_NEWS)){
            newsPostModel = (NewsPostModel) getIntent().getExtras().getSerializable(Constants.EXTRA_NEWS);
            if(newsPostModel != null){
                isEditMode = true;
                setAllVal();
            }
        }
    }

    private void setAllVal() {
        edtYourName.setText(newsPostModel.nameReporter);
        edtDesc.setText(newsPostModel.newsDesc);
        edtMobile.setText(newsPostModel.mobileNo);
        edtNewsTitle.setText(newsPostModel.newsTitle);
        linAdd.setVisibility(View.GONE);
        btnSubmit.setText("Update");
        try {
            Glide.with(this).load(decodeFromFirebaseBase64(newsPostModel.image)).asBitmap().
                    placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder).into(imgNews);
        } catch (IOException e) {
            linAdd.setVisibility(View.VISIBLE);
            e.printStackTrace();
        }
    }

    public static  byte[]  decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = Base64.decode(image, Base64.DEFAULT);
        //return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
        return decodedByteArray;
    }

    private void initView() {
        imgNews = (ImageView) findViewById(R.id.imgNews);
        edtNewsTitle = (EditText) findViewById(R.id.edtNewsTitle);
        edtYourName = (EditText) findViewById(R.id.edtYourName);
        edtDesc = (EditText) findViewById(R.id.edtDesc);
        mRelParent = (RelativeLayout) findViewById(R.id.rel_parent);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSubmit = (AppCompatButton) findViewById(R.id.btnSubmit);
        linAdd = (LinearLayout) findViewById(R.id.linAdd);
        edtMobile = (EditText) findViewById(R.id.edtMobileNo);
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
            AnimationUtils.animateScaleIn(btnSubmit);
            progressBar.setVisibility(View.VISIBLE);

            SharedPreferenceUtil.putValue(Constants.LOCALE_NEWS_TITLE_ADD, edtNewsTitle.getText().toString());
            SharedPreferenceUtil.putValue(Constants.LOCALE_NEWS_DESC_ADD, edtDesc.getText().toString());
            SharedPreferenceUtil.save();

            if(isEditMode){
                editValue();
            }else {
                addValToFirebase();
            }
        }
    }

    private boolean isValid() {
        if(!isOnline(this)){
            showSnackbar(mRelParent, getString(R.string.no_internet));
            return false;
        }else if(TextUtils.isEmpty(edtNewsTitle.getText().toString().trim())){
            showSnackbar(mRelParent, "Please Enter News Title");
            edtNewsTitle.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }else if(TextUtils.isEmpty(edtYourName.getText().toString().trim())){
            showSnackbar(mRelParent, "Please Enter Your Name");
            edtYourName.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }else if(edtMobile.getText().length() > 0 && edtMobile.getText().length() < 9){
            showSnackbar(mRelParent, "Please Enter Correct Mobile No");
            edtMobile.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }
        return true;
    }

    private void addValToFirebase() {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_NEWS);
        String key = mDatabase.push().getKey();
        NewsPostModel user = new NewsPostModel(edtYourName.getText().toString(),
                edtDesc.getText().toString(), edtNewsTitle.getText().toString(),
                AppUtils.getBase64Image(imagePath), true,
                AppUtils.getUniqueId(AddNewsActivity.this), edtMobile.getText().toString(), key);
        mDatabase.child(key).setValue(user);

        /*mDatabase.addValueEventListener(new ValueEventListener() {
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
        });*/

        showSnackbar(mRelParent, "News Add Successfully!");
        //showToast("News Add Successfully!");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setResult(RESULT_OK);
                finish();
                //moveActivity(new Intent(AddNewsActivity.this, ReporterNewsListActivity.class), AddNewsActivity.this, true);
            }
        }, 1000);
    }

    private void editValue(){
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_NEWS);

        HashMap<String, Object> result = new HashMap<>();
        result.put("nameReporter", edtYourName.getText().toString());
        result.put("newsDesc", edtDesc.getText().toString());
        result.put("newsTitle", edtNewsTitle.getText().toString());
        result.put("image", imagePath.equals("") ? newsPostModel.image : AppUtils.getBase64Image(imagePath));
        result.put("mobileNo", edtMobile.getText().toString());
        mDatabase.child(newsPostModel.key).updateChildren(result);

        /*mDatabase.addValueEventListener(new ValueEventListener() {
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
        });*/

        showSnackbar(mRelParent, "News Update Successfully!");
        //showToast("News Add Successfully!");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setResult(RESULT_OK);
                finish();
            }
        }, 1000);
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
                if(isGalleryOpen == 0)
                    openGallery();
                else if(isGalleryOpen == 1)
                    openCamera();
            }
        }
    }

    void openGallery() {
        isGalleryOpen = 0;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.REQUEST_OPEN_GALLERY);
    }

    void openCamera(){
        isGalleryOpen = 1;
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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCameraIntentHelper.onRestoreInstanceState(savedInstanceState);
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
        Log.e(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
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
        linAdd.setVisibility(View.GONE);
        Glide.with(this).load(photoUri).into(imgNews);
    }

  String getFileNameByUri(Context context, Uri uri) {
        String fileName = "";
        Uri filePathUri = uri;
        if (uri.getScheme().toString().compareTo("content") == 0) {
            Cursor cursor = null;
            try {
                String[] proj = { MediaStore.Images.Media.DATA };
                cursor = context.getContentResolver().query(filePathUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if (uri.getScheme().compareTo("file") == 0) {
            fileName = new File(uri.getPath()).getAbsolutePath();
        }
        return fileName;
    }
}
