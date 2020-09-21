package com.thealer.telehealer.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thealer.telehealer.R;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static android.app.Activity.RESULT_OK;
import static com.thealer.telehealer.common.PermissionConstants.CAMERA_REQUEST_CODE;
import static com.thealer.telehealer.common.PermissionConstants.GALLERY_REQUEST_CODE;

/**
 * Created by Aswin on 06,November,2018
 */
public class CameraUtil {

    private static CameraUtil cameraUtil;
    private static Context context;

    public static CameraUtil with(Context con) {
        if (cameraUtil == null) {
            cameraUtil = new CameraUtil();
        }
        context = con;
        return cameraUtil;
    }

    public CameraUtil showImageSelectionAlert() {
        if (PermissionChecker.with(context).checkPermission(PermissionConstants.PERMISSION_CAM_PHOTOS)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.image_selection_view, null);
            alertDialog.setView(view);
            Dialog dialog = alertDialog.create();
            dialog.show();

            TextView cameraTv, galleryTv;

            cameraTv = (TextView) view.findViewById(R.id.camera_tv);
            galleryTv = (TextView) view.findViewById(R.id.gallery_tv);

            cameraTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    openCamera();
                }
            });

            galleryTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    openGallery();
                }
            });
        }

        return cameraUtil;
    }


    public void openGallery() {
        if (PermissionChecker.with(context).checkPermission(PermissionConstants.PERMISSION_GALLERY)) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                ((FragmentActivity) context).startActivityForResult(intent, GALLERY_REQUEST_CODE);
            } else {
                Toast.makeText(context, context.getString(R.string.gallery_not_supported), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openCamera() {
        if (PermissionChecker.with(context).checkPermission(PermissionConstants.PERMISSION_CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(context.getPackageManager()) != null)
                ((Activity) context).startActivityForResult(intent, CAMERA_REQUEST_CODE);
            else
                Toast.makeText(context, context.getString(R.string.camera_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    public String getImagePath(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                if (data != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                    return getBitmapFilePath(bitmap);
                }
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = context.getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                return picturePath;
            }
        }
        return null;
    }

    public String getBitmapFilePath(Bitmap bitmap) {

        Uri uri = getImageUri(bitmap);
        File finalFile = new File(getRealPathFromURI(uri));
        return finalFile.getAbsolutePath();
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title " + SystemClock.currentThreadTimeMillis(), null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (context.getContentResolver() != null) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }
}
