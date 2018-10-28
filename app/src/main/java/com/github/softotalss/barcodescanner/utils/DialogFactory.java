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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class DialogFactory {

    public static Dialog createSimpleOkDialog(Context context, String title, String message, DialogInterface.OnClickListener ok) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, ok);
        return alertDialog.create();
    }

    public static Dialog createSimpleOkDialog(Context context, String title, String message) {
        return createSimpleOkDialog(context, title, message, null);
    }

    public static Dialog createSimpleOkDialog(Context context, @StringRes int titleResource, @StringRes int messageResource, DialogInterface.OnClickListener ok) {
        return createSimpleOkDialog(context, context.getString(titleResource), context.getString(messageResource), ok);
    }

    public static void showNoCancelable(Dialog dialogo) {
        dialogo.setCancelable(false);
        dialogo.show();
    }
}
