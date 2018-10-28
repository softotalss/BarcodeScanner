/*
 * Copyright (c) 2018. softotalss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.softotalss.barcodescanner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;

@SuppressWarnings("unused")
public class ActivityUtil {

    public static boolean comprobarPermiso(Context context, @NonNull String permiso) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ActivityCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressWarnings("SameParameterValue")
    public static boolean solicitarPermisos(final Activity activity, @NonNull String permiso,
                                            @StringRes int titulo, @StringRes int mensaje,
                                            final int idPermiso) {
        if (comprobarPermiso(activity, permiso)) {
            return true;
        } else {
            final String[] permisos = new String[]{permiso};

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permiso)) {
                DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(activity, permisos, idPermiso);
                    }
                };

                DialogFactory.showNoCancelable(DialogFactory.createSimpleOkDialog(activity,
                        titulo, mensaje, ok));
            } else {
                ActivityCompat.requestPermissions(activity, permisos, idPermiso);
            }

            return false;
        }
    }
}
