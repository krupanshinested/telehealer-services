package com.thealer.telehealer.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.thealer.telehealer.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static com.thealer.telehealer.common.PermissionConstants.CAMERA_REQUEST_CODE;
import static com.thealer.telehealer.common.PermissionConstants.GALLERY_REQUEST_CODE;

/**
 * Created by Aswin on 06,November,2018
 */
public class CameraUtil {
    private static String capturedImagePath = null;

    public static void showImageSelectionAlert(Context context) {
        Utils.hideKeyboard((Activity)context);
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
                    openCamera(context);
                }
            });

            galleryTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    openGallery(context);
                }
            });
        }
    }


    public static void openGallery(Context context) {
        Utils.hideKeyboard((Activity)context);
        if (PermissionChecker.with(context).checkPermission(PermissionConstants.PERMISSION_GALLERY)) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                ((FragmentActivity) context).startActivityForResult(intent, GALLERY_REQUEST_CODE);
            } else {
                Toast.makeText(context, context.getString(R.string.gallery_not_supported), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void openCamera(Context context) {
        Utils.hideKeyboard((Activity)context);
        if (PermissionChecker.with(context).checkPermission(PermissionConstants.PERMISSION_CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile(context);
                    capturedImagePath = photoFile.getAbsolutePath();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    Log.e("aswin", "openCamera: " + Uri.fromFile(photoFile).toString());
                    Uri photoUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    ((Activity) context).startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }
            } else {
                capturedImagePath = null;
                Toast.makeText(context, context.getString(R.string.camera_not_supported), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String getImagePath(Context context, int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
//                if (data != null && data.getExtras() != null && data.getExtras().get("data") != null) {
//                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//
//                    return getBitmapFilePath(context, bitmap);
//                }
                return capturedImagePath;
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = context.getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    return picturePath;
                }
            }
        }
        return null;
    }

    private static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    public static String getBitmapFilePath(Context context, Bitmap bitmap) {

        Uri uri = getImageUri(context, bitmap);
        File finalFile = new File(getRealPathFromUri(context, uri));
        return finalFile.getAbsolutePath();
    }

    private static Uri getImageUri(Context context, Bitmap bitmap) {

        String relativeLocation = Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name);

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,  SystemClock.currentThreadTimeMillis());
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //this one
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation);
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1);
        }


        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);


        try {
            OutputStream stream = resolver.openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (Exception e) {
            if (uri != null) {
                resolver.delete(uri, null, null);
            }
        } finally {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0);
        }

         return uri;
    }

    public String getRealPathFromURI(Context context, Uri uri) {
        String path = "";
        try {

            if (context.getContentResolver() != null) {
                Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    path = cursor.getString(idx);
                    cursor.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public static String getRealPathFromUri(Context context, final Uri uri) {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            if (isGooglePhotosUri(uri)) {
                return getImagePathFromInputStreamUri(context, uri);
            }

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = MediaStore.Images.Media.DATA;
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }

    public boolean isTypeImage(String path) {

        String mimeType = MimeTypeMap.getFileExtensionFromUrl(path);
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeType);
        return type != null && type.contains("image/");
    }

    public static boolean isTypeImage(Context context, Uri uri) {

        String mimeType = context.getContentResolver().getType(uri);
        return mimeType != null && mimeType.contains("image/");
    }


    public static String getImagePathFromInputStreamUri(Context context, Uri uri) {
        InputStream inputStream = null;
        String filePath = null;

        if (uri.getAuthority() != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(uri); // context needed
                File photoFile = createTemporalFileFrom(context, inputStream, uri.getLastPathSegment());

                if (photoFile.exists())
                    filePath = photoFile.getPath();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return filePath;
    }

    private static File createTemporalFileFrom(Context context, InputStream inputStream, String lastPathSegment) throws IOException {
        File targetFile = null;

        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[8 * 1024];

            targetFile = createTemporalFile(context, lastPathSegment);
            OutputStream outputStream = new FileOutputStream(targetFile);

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();

            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return targetFile;
    }

    private static File createTemporalFile(Context context, String lastPathSegment) {
        return new File(context.getExternalCacheDir(), lastPathSegment + ".jpg"); // context needed
    }
}
