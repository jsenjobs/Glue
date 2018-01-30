package app.chaosstudio.com.glue.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.view.View;

import app.chaosstudio.com.glue.R;
import app.chaosstudio.com.glue.ui.SimpleAlert;

/**
 * Created by jsen on 2018/1/21.
 */

public class PermissionHelp {

    public static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public static final int REQUEST_CODE_ASK_PERMISSIONS_1 = 1234;

    public static void grantPermissionsStorage(final Activity activity) {

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int hasWRITE_EXTERNAL_STORAGE = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    SimpleAlert.Build build = new SimpleAlert.Build(activity, R.style.SimpleAlert);
                    build.setShowTitle(true);
                    build.setTitle(activity.getString(R.string.permission_request_title));
                    build.setContent(activity.getString(R.string.permission_request));
                    build.setOnPos(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (android.os.Build.VERSION.SDK_INT >= 23)
                                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                        }
                    });
                    build.build().show();
                }
            }
        }
    }

    public static void grantPermissionsLoc(final Activity activity) {

        if (android.os.Build.VERSION.SDK_INT >= 23) {

            int hasACCESS_FINE_LOCATION = activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
                if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

                    SimpleAlert.Build build = new SimpleAlert.Build(activity, R.style.SimpleAlert);
                    build.setShowTitle(true);
                    build.setTitle(activity.getString(R.string.permission_request_title));
                    build.setContent(activity.getString(R.string.permission_request));
                    build.setOnPos(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (android.os.Build.VERSION.SDK_INT >= 23)
                                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_CODE_ASK_PERMISSIONS_1);
                        }
                    });
                    build.build().show();
                }
            }
        }
    }

    public interface OnResult {
        void execed(boolean exec);
    }
    public static void grantPermissionsLocForce(final Activity activity, final Fragment fragment, final OnResult onResult) {

        if (android.os.Build.VERSION.SDK_INT >= 23) {

            int hasACCESS_FINE_LOCATION = activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {

                SimpleAlert.Build build = new SimpleAlert.Build(activity, R.style.SimpleAlert);
                build.setShowTitle(true);
                build.setTitle(activity.getString(R.string.permission_request_title));
                build.setContent(activity.getString(R.string.permission_request));
                build.setOnPos(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (android.os.Build.VERSION.SDK_INT >= 23)
                            fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_CODE_ASK_PERMISSIONS_1);
                    }
                });
                SimpleAlert alert = build.build();
                alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (dialog instanceof SimpleAlert) {
                            SimpleAlert d = (SimpleAlert) dialog;
                            if (onResult != null) {
                                onResult.execed(d.getTag());
                            }
                        }
                    }
                });
                alert.show();
            }
        }
    }

}
