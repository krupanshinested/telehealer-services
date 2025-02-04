package com.thealer.telehealer.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogCapability;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogger;
import com.thealer.telehealer.views.proposer.ProposerActivity;

import flavor.GoogleFit.GoogleFitManager;


/**
 * Created by Aswin on 08,November,2018
 */
public class PermissionChecker {
    private static PermissionChecker permissionChecker;
    private static Context context;

    public static PermissionChecker with(Context con) {
        if (permissionChecker == null) {
            permissionChecker = new PermissionChecker();
        }
        context = con;
        return permissionChecker;
    }

    public boolean checkPermission(int permissionFor) {
        if (isGranted(permissionFor)) {
            return true;
        } else {
            showProposer(permissionFor);
            return false;
        }
    }

    @Nullable
    public Intent checkAndReturn(int permissionFor) {
        if (isGranted(permissionFor)) {
            return null;
        } else {
            return getIntent(permissionFor);
        }
    }

    public boolean checkPermissionForFragment(int permissionFor, Fragment fragment) {
        if (isGranted(permissionFor)) {
            return true;
        } else {
            showProposerForFragment(permissionFor, fragment);
            return false;
        }
    }

    public boolean isGranted(int permissionFor) {
        switch (permissionFor) {
            case PermissionConstants.PERMISSION_CAM_PHOTOS:
            case PermissionConstants.PERMISSION_CAMERA:
                if (isCamerPermissionGranted() && isGalleryPermissionGranted()) {
                    return true;
                }
                break;
            case PermissionConstants.PERMISSION_STORAGE:
            case PermissionConstants.PERMISSION_GALLERY:
                if (isGalleryPermissionGranted()) {
                    return true;
                }
                break;
            case PermissionConstants.PERMISSION_CAM_MIC_NOTIFICATION:
                if (isCamerPermissionGranted() && isMicPermissionGranted() && isNotiPermissionGranted()) {
                    return true;
                }
                break;
            case PermissionConstants.PERMISSION_CAM_MIC:
                if (isCamerPermissionGranted() && isMicPermissionGranted()) {
                    return true;
                }
                break;
            case PermissionConstants.PERMISSION_LOCATION:
                if (isLocationPermissionGranted()) {
                    return true;
                }
                break;
            case PermissionConstants.PERMISSION_LOCATION_STORAGE_VITALS:
                if (isLocationPermissionGranted() && isWriteStoragePermissionGranted()) {
                    return true;
                }
                break;
            case PermissionConstants.PERMISSION_MICROPHONE:
                if (isMicPermissionGranted()) {
                    return true;
                }
                break;
            case PermissionConstants.PERMISSION_CONTACTS:
                return isContactsPermissionGranted();
            case PermissionConstants.PERMISSION_GOOGLE_FIT:
                return (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION)
                        == PackageManager.PERMISSION_GRANTED);
        }

        return false;
    }

    private boolean isContactsPermissionGranted() {
        boolean isGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        TeleLogger.shared.log(TeleLogCapability.contacts, isGranted);
        return isGranted;
    }

    private boolean isMicPermissionGranted() {
        Boolean isGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        TeleLogger.shared.log(TeleLogCapability.mic, isGranted);
        return isGranted;
    }

    private boolean isNotiPermissionGranted() {
        Boolean isGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
//        TeleLogger.shared.log(TeleLogCapability., isGranted);
        return isGranted;
    }


    private boolean isGalleryPermissionGranted() {
        Boolean isGranted;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            isGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        TeleLogger.shared.log(TeleLogCapability.photo, isGranted);
        return isGranted;
    }

    private boolean isCamerPermissionGranted() {
        Boolean isGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        TeleLogger.shared.log(TeleLogCapability.camera, isGranted);
        return isGranted;
    }

    private boolean isLocationPermissionGranted() {
        Boolean isGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        TeleLogger.shared.log(TeleLogCapability.location, isGranted);
        return isGranted;
    }

    private boolean isWriteStoragePermissionGranted() {
        Boolean isGranted;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            isGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        TeleLogger.shared.log(TeleLogCapability.photo, isGranted);
        return isGranted;
    }

    private void showProposer(int permissionFor) {
        ((FragmentActivity) context).startActivityForResult(getIntent(permissionFor), permissionFor, null);
    }

    private Intent getIntent(int permissionFor) {
        Bundle bundle = getBundle(permissionFor);
        Intent intent = new Intent(context, ProposerActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    private void showProposerForFragment(int permissionFor, Fragment fragment) {
        fragment.startActivityForResult(getIntent(permissionFor), permissionFor, null);
    }

    private Bundle getBundle(int permissionFor) {
        int subPermission = getSubPermission(permissionFor);
        int imageSrc = getImage(subPermission);
        String title = getTitle(subPermission);
        String message = getMessage(subPermission);

        Bundle bundle = new Bundle();
        bundle.putInt(PermissionConstants.PERMISSION_FOR, subPermission);
        bundle.putInt(PermissionConstants.PROPOSER_IMAGE, imageSrc);
        bundle.putString(PermissionConstants.PROPOSER_TITLE, title);
        bundle.putString(PermissionConstants.PROPOSER_MESSAGE, message);

        return bundle;
    }

    private int getSubPermission(int permissionFor) {
        switch (permissionFor) {
            case PermissionConstants.PERMISSION_CAM_PHOTOS:
                if (isCamerPermissionGranted() && !isGalleryPermissionGranted())
                    return PermissionConstants.PERMISSION_GALLERY;
                else if (!isCamerPermissionGranted() && isGalleryPermissionGranted())
                    return PermissionConstants.PERMISSION_CAMERA;
                break;
            case PermissionConstants.PERMISSION_CAM_MIC:
                if (isCamerPermissionGranted() && !isMicPermissionGranted())
                    return PermissionConstants.PERMISSION_MICROPHONE;
                if (!isCamerPermissionGranted() && isMicPermissionGranted())
                    return PermissionConstants.PERMISSION_CAMERA;
                break;
            case PermissionConstants.PERMISSION_CAM_MIC_NOTIFICATION:
                if (isCamerPermissionGranted() && !isMicPermissionGranted() && isNotiPermissionGranted())
                    return PermissionConstants.PERMISSION_MICROPHONE;
                if (!isCamerPermissionGranted() && isMicPermissionGranted() && isNotiPermissionGranted())
                    return PermissionConstants.PERMISSION_CAMERA;
                if (isCamerPermissionGranted() && isMicPermissionGranted() && !isNotiPermissionGranted())
                    return PermissionConstants.PERMISSION_NOTIFICATION;
                break;
            case PermissionConstants.PERMISSION_LOCATION_STORAGE_VITALS:
                if (!isLocationPermissionGranted() && !isWriteStoragePermissionGranted())
                    return PermissionConstants.PERMISSION_LOCATION_STORAGE_VITALS;
                else if (!isLocationPermissionGranted() && isWriteStoragePermissionGranted())
                    return PermissionConstants.PERMISSION_LOCATION_VITALS;
                else if (isLocationPermissionGranted() && !isWriteStoragePermissionGranted())
                    return PermissionConstants.PERMISSION_WRITE_STORAGE_VITALS;
        }
        return permissionFor;
    }

    private String getMessage(int permissionFor) {
        switch (permissionFor) {
//            case PermissionConstants.PERMISSION_CAMERA:
//                return String.format(context.getString(R.string.permission_camera_message), context.getString(R.string.app_name));
            case PermissionConstants.PERMISSION_GALLERY:
            case PermissionConstants.PERMISSION_CAMERA:
            case PermissionConstants.PERMISSION_MICROPHONE:
            case PermissionConstants.PERMISSION_NOTIFICATION:
            case PermissionConstants.PERMISSION_CAM_MIC_NOTIFICATION:
            case PermissionConstants.PERMISSION_CAM_PHOTOS:
            case PermissionConstants.PERMISSION_CAM_MIC:
            case PermissionConstants.PERMISSION_MIC_NITIFICATION:
            case PermissionConstants.PERMISSION_CAM_NOTIFICAITON:
                return context.getString(R.string.permission_photo_microphone_cam_noti_message);
            case PermissionConstants.PERMISSION_LOCATION:
                return String.format(context.getString(R.string.permission_location_message), context.getString(R.string.app_name));
            case PermissionConstants.PERMISSION_HEALTH:
                return String.format(context.getString(R.string.permission_health_message), context.getString(R.string.app_name));
            case PermissionConstants.PERMISSION_STORAGE:
                return String.format(context.getString(R.string.permission_storage_message), context.getString(R.string.app_name));
            case PermissionConstants.PERMISSION_LOCATION_STORAGE_VITALS:
                return String.format(context.getString(R.string.permission_loc_storage_vitals_message), context.getString(R.string.app_name));
            case PermissionConstants.PERMISSION_LOCATION_VITALS:
                return String.format(context.getString(R.string.permission_loc_vitals_message), context.getString(R.string.app_name));
            case PermissionConstants.PERMISSION_WRITE_STORAGE_VITALS:
                return String.format(context.getString(R.string.permission_storage_vitals_message), context.getString(R.string.app_name));
            case PermissionConstants.PERMISSION_CONTACTS:
                return String.format(context.getString(R.string.permission_contact_message), context.getString(R.string.app_name));
            case PermissionConstants.PERMISSION_GOOGLE_FIT:
                return String.format(context.getString(R.string.permission_google_fit), context.getString(R.string.app_name));
        }
        return null;
    }

    public boolean isCamPhoMicNoti (int permissionFor) {
        switch (permissionFor) {
            case PermissionConstants.PERMISSION_GALLERY:
            case PermissionConstants.PERMISSION_CAMERA:
            case PermissionConstants.PERMISSION_MICROPHONE:
            case PermissionConstants.PERMISSION_NOTIFICATION:
            case PermissionConstants.PERMISSION_CAM_MIC_NOTIFICATION:
            case PermissionConstants.PERMISSION_CAM_PHOTOS:
            case PermissionConstants.PERMISSION_CAM_MIC:
            case PermissionConstants.PERMISSION_MIC_NITIFICATION:
            case PermissionConstants.PERMISSION_CAM_NOTIFICAITON:
                return true;
            default:
                return false;
        }
    }

    private String getTitle(int permissionFor) {
        switch (permissionFor) {
//            case PermissionConstants.PERMISSION_CAMERA:
//                return context.getString(R.string.permission_camera_title);
            case PermissionConstants.PERMISSION_GALLERY:
            case PermissionConstants.PERMISSION_CAMERA:
            case PermissionConstants.PERMISSION_MICROPHONE:
            case PermissionConstants.PERMISSION_NOTIFICATION:
            case PermissionConstants.PERMISSION_CAM_MIC_NOTIFICATION:
            case PermissionConstants.PERMISSION_CAM_PHOTOS:
            case PermissionConstants.PERMISSION_CAM_MIC:
            case PermissionConstants.PERMISSION_MIC_NITIFICATION:
            case PermissionConstants.PERMISSION_CAM_NOTIFICAITON:
                return context.getString(R.string.permission_photo_microphone_cam_noti_tittle);
            case PermissionConstants.PERMISSION_LOCATION:
                return context.getString(R.string.permission_location_title);
            case PermissionConstants.PERMISSION_HEALTH:
                return context.getString(R.string.permission_health_title);
            case PermissionConstants.PERMISSION_STORAGE:
                return context.getString(R.string.permission_storage_title);
            case PermissionConstants.PERMISSION_LOCATION_STORAGE_VITALS:
                return context.getString(R.string.permission_loc_storage_title);
            case PermissionConstants.PERMISSION_LOCATION_VITALS:
                return context.getString(R.string.permission_location_title);
            case PermissionConstants.PERMISSION_WRITE_STORAGE_VITALS:
                return context.getString(R.string.permission_storage_title);
            case PermissionConstants.PERMISSION_CONTACTS:
                return context.getString(R.string.permission_contact_title);
            case PermissionConstants.PERMISSION_GOOGLE_FIT:
                return context.getString(R.string.permission_google_fit_title);
        }
        return null;
    }

    private int getImage(int permissionFor) {
        switch (permissionFor) {
//            case PermissionConstants.PERMISSION_CAMERA:
//                return R.drawable.permission_camera;
            case PermissionConstants.PERMISSION_STORAGE:
            case PermissionConstants.PERMISSION_GALLERY:
                return R.drawable.permission_gallery;
            case PermissionConstants.PERMISSION_MICROPHONE:
                return R.drawable.permission_audio;
            case PermissionConstants.PERMISSION_LOCATION:
                return R.drawable.permission_location;
            case PermissionConstants.PERMISSION_NOTIFICATION:
                return R.drawable.permission_notification;
            case PermissionConstants.PERMISSION_HEALTH:
                return R.drawable.permission_health;
            case PermissionConstants.PERMISSION_CAM_MIC_NOTIFICATION:
                return R.drawable.proposer_banner;
            case PermissionConstants.PERMISSION_CAMERA:
            case PermissionConstants.PERMISSION_CAM_PHOTOS:
                return R.drawable.permission_camera_gallery;
            case PermissionConstants.PERMISSION_CAM_MIC:
                return R.drawable.permission_camera_mic;
            case PermissionConstants.PERMISSION_CAM_NOTIFICAITON:
                return R.drawable.permission_camera_notification;
            case PermissionConstants.PERMISSION_MIC_NITIFICATION:
                return R.drawable.permission_audio_notification;
            case PermissionConstants.PERMISSION_LOCATION_STORAGE_VITALS:
                return R.drawable.permission_location;
            case PermissionConstants.PERMISSION_LOCATION_VITALS:
                return R.drawable.permission_location;
            case PermissionConstants.PERMISSION_WRITE_STORAGE_VITALS:
                return R.drawable.permission_location;
            case PermissionConstants.PERMISSION_CONTACTS:
                return R.drawable.permission_contacts;
            case PermissionConstants.PERMISSION_GOOGLE_FIT:
                return R.drawable.permission_health;
        }
        return 0;
    }

    public void requestPermission(int permissionFor) {
        String[] permissions = getPermissions(permissionFor);
        int requestCode = getRequestCode(permissionFor);
        ActivityCompat.requestPermissions(((Activity) context), permissions, requestCode);
    }

    private int getRequestCode(int permissionFor) {
        switch (permissionFor) {
            case PermissionConstants.PERMISSION_CAM_PHOTOS:
                return PermissionConstants.CAM_GALLERY_REQUEST_CODE;
            case PermissionConstants.PERMISSION_CAMERA:
                return PermissionConstants.CAMERA_REQUEST_CODE;
            case PermissionConstants.PERMISSION_GALLERY:
            case PermissionConstants.PERMISSION_STORAGE:
                return PermissionConstants.GALLERY_REQUEST_CODE;
            case PermissionConstants.PERMISSION_LOCATION_STORAGE_VITALS:
                return PermissionConstants.LOC_STORAGE_REQUEST_CODE;
            case PermissionConstants.PERMISSION_LOCATION_VITALS:
            case PermissionConstants.PERMISSION_LOCATION:
                return PermissionConstants.LOC_REQUEST_CODE;
            case PermissionConstants.PERMISSION_WRITE_STORAGE_VITALS:
                return PermissionConstants.STORAGE_REQUEST_CODE;
            case PermissionConstants.PERMISSION_CONTACTS:
                return PermissionConstants.CONTACTS_REQUEST_CODE;
            case PermissionConstants.PERMISSION_GOOGLE_FIT:
                return GoogleFitManager.REQUEST_OAUTH_REQUEST_CODE;
            case PermissionConstants.PERMISSION_CAM_MIC_NOTIFICATION:
                return PermissionConstants.PERMISSION_NOTIFICATION;
        }
        return 0;
    }

    public String[] getPermissions(int permissionFor) {
        switch (permissionFor) {

            case PermissionConstants.PERMISSION_CAMERAS:
                return new String[]{Manifest.permission.CAMERA};
            case PermissionConstants.PERMISSION_CAM_PHOTOS:
            case PermissionConstants.PERMISSION_CAMERA:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    return new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.CAMERA};
                } else {
                    return new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                }
            case PermissionConstants.PERMISSION_GALLERY:
            case PermissionConstants.PERMISSION_STORAGE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    return new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO};
                } else {
                    return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                }
            case PermissionConstants.PERMISSION_CAM_MIC:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    return new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.REQUEST_COMPANION_START_FOREGROUND_SERVICES_FROM_BACKGROUND, Manifest.permission.FOREGROUND_SERVICE, Manifest.permission.START_FOREGROUND_SERVICES_FROM_BACKGROUND, Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND, Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS};
                } else {
                    return new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
                }
            case PermissionConstants.PERMISSION_CAM_MIC_NOTIFICATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    return new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.REQUEST_COMPANION_START_FOREGROUND_SERVICES_FROM_BACKGROUND, Manifest.permission.FOREGROUND_SERVICE, Manifest.permission.START_FOREGROUND_SERVICES_FROM_BACKGROUND, Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND, Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Manifest.permission.POST_NOTIFICATIONS};
                } else {
                    return new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.POST_NOTIFICATIONS};
                }
            case PermissionConstants.PERMISSION_LOCATION_STORAGE_VITALS:
                return new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            case PermissionConstants.PERMISSION_LOCATION_VITALS:
            case PermissionConstants.PERMISSION_LOCATION:
                return new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            case PermissionConstants.PERMISSION_WRITE_STORAGE_VITALS:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    return new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO};
                } else {
                    return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                }
            case PermissionConstants.PERMISSION_MICROPHONE:
                return new String[]{Manifest.permission.RECORD_AUDIO};
            case PermissionConstants.PERMISSION_CONTACTS:
                return new String[]{Manifest.permission.READ_CONTACTS};
            case PermissionConstants.PERMISSION_GOOGLE_FIT:
                return new String[]{Manifest.permission.ACTIVITY_RECOGNITION};
            case PermissionConstants.PERMISSION_NOTIFICATION:
                return new String[]{Manifest.permission.POST_NOTIFICATIONS};

        }
        return new String[0];
    }

    public boolean isPermissionDenied(int permissionFor) {

        String[] permissions = getPermissions(permissionFor);
        for (String permission :
                permissions) {
            if (TeleHealerApplication.appPreference.getBoolean(permission)) {
                return true;
            }
        }
        return false;
    }
}
