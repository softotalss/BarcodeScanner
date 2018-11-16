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

package com.github.softotalss.barcodescanner.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.github.softotalss.barcodescanner.R;
import com.github.softotalss.barcodescanner.utils.ActivityUtil;
import com.github.softotalss.barcodescanner.utils.Constants;
import com.github.softotalss.barcodescanner.utils.DialogFactory;
import com.google.zxing.Result;

public class ScannerActivity extends AppCompatActivity implements BarcodeScannerView.ActivityCallback {

    private static final String TAG = ScannerActivity.class.getSimpleName();

    private ViewGroup mContentFrame;
    private BarcodeScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_scanner);

        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        startCamera();
    }

    @Override
    public void onPause() {
        stopCamera();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scanner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.btn_flash:
                if(!mScannerView.isFlashOn()) {
                    item.setIcon(R.drawable.ic_flash_on);
                    item.setTitle(R.string.text_flash_on);
                } else {
                    item.setIcon(R.drawable.ic_flash_off);
                    item.setTitle(R.string.text_flash_off);
                }
                mScannerView.toggleFlash();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initCamera();
                } else {
                    DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    };

                    DialogFactory.showNoCancelable(DialogFactory.createSimpleOkDialog(this, R.string.text_alert,
                            R.string.msg_camera_permission_denied, ok));
                }
                break;
        }
    }

    private void initView() {
        mContentFrame = findViewById(R.id.layout_content);
        if (ActivityUtil.solicitarPermisos(this, Manifest.permission.CAMERA,
                R.string.text_alert, R.string.msg_camera_permission,
                Constants.PERMISSION_CAMERA)) {
            initCamera();
        }
    }

    private void initCamera() {
        mScannerView = new BarcodeScannerView(this);
        mContentFrame.addView(mScannerView);
    }

    private void startCamera() {
        if (mScannerView != null) {
            mScannerView.setResultHandler(this);
            mScannerView.startCamera();
        }
    }

    private void stopCamera() {
        if (mScannerView != null) {
            mScannerView.stopCamera();
        }
    }

    @Override
    public void onResult(Result result) {
        Log.d(TAG, result.getText());
        DialogFactory.showNoCancelable(DialogFactory.createSimpleOkDialog(this,
                getString(R.string.app_name), result.getText(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mScannerView.restartCamera(); // Use it for read another barcode without close camera
                    }
                }));
    }

    @Override
    public void onErrorExit(Exception e) {
        Log.d(TAG, "onErrorExit");
        DialogFactory.showNoCancelable(DialogFactory.createSimpleOkDialog(this,
                R.string.app_name, R.string.msg_camera_framework_bug,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }));
    }
}
