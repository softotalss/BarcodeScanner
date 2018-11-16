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
 *
 *  Part of this code is based on project: https://github.com/zxing/zxing
 */

package com.github.softotalss.barcodescanner.view;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.github.softotalss.barcodescanner.camera.CameraManager;
import com.github.softotalss.barcodescanner.camera.CaptureHandler;
import com.github.softotalss.barcodescanner.camera.ViewCallback;
import com.github.softotalss.barcodescanner.utils.Constants;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class BarcodeScannerView extends FrameLayout implements SurfaceHolder.Callback,
        ViewCallback {

    private static final String TAG = BarcodeScannerView.class.getSimpleName();

    private ViewfinderView mViewfinderView;
    private Map<DecodeHintType,Object> mHints;
    public static final List<BarcodeFormat> ALL_FORMATS = new ArrayList<>();
    private List<BarcodeFormat> mFormats;
    private ActivityCallback mCallback;
    private CameraManager mCameraManager;
    private SurfaceView mPreview;
    private CaptureHandler mHandler;
    private boolean mHasSurface;

    static {
        ALL_FORMATS.add(BarcodeFormat.AZTEC);
        ALL_FORMATS.add(BarcodeFormat.CODABAR);
        ALL_FORMATS.add(BarcodeFormat.CODE_39);
        ALL_FORMATS.add(BarcodeFormat.CODE_93);
        ALL_FORMATS.add(BarcodeFormat.CODE_128);
        ALL_FORMATS.add(BarcodeFormat.DATA_MATRIX);
        ALL_FORMATS.add(BarcodeFormat.EAN_8);
        ALL_FORMATS.add(BarcodeFormat.EAN_13);
        ALL_FORMATS.add(BarcodeFormat.ITF);
        ALL_FORMATS.add(BarcodeFormat.MAXICODE);
        ALL_FORMATS.add(BarcodeFormat.PDF_417);
        ALL_FORMATS.add(BarcodeFormat.QR_CODE);
        ALL_FORMATS.add(BarcodeFormat.RSS_14);
        ALL_FORMATS.add(BarcodeFormat.RSS_EXPANDED);
        ALL_FORMATS.add(BarcodeFormat.UPC_A);
        ALL_FORMATS.add(BarcodeFormat.UPC_E);
        ALL_FORMATS.add(BarcodeFormat.UPC_EAN_EXTENSION);
    }

    public BarcodeScannerView(Context context) {
        super(context);
        init();
    }

    public void setFormats(List<BarcodeFormat> formats) {
        mFormats = formats;
        initHints();
    }

    public void setResultHandler(ActivityCallback resultHandler) {
        mCallback = resultHandler;
    }

    public Collection<BarcodeFormat> getFormats() {
        if(mFormats == null) {
            return ALL_FORMATS;
        }
        return mFormats;
    }

    public void startCamera() {
        mCameraManager = new CameraManager(getContext());
        mViewfinderView.setCameraManager(mCameraManager);

        if (mHasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(mPreview.getHolder());
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            mPreview.getHolder().addCallback(this);
        }
    }

    public void stopCamera() {
        if (mHandler != null) {
            mHandler.quitSynchronously();
            mHandler = null;
        }

        mCameraManager.closeDriver();

        if (!mHasSurface) {
            mPreview.getHolder().removeCallback(this);
        }
    }

    public void restartCamera() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(Constants.RESTART_PREVIEW);
        }
    }

    public void setFlash(boolean flag) {
        mCameraManager.setTorch(flag);
    }

    public boolean isFlashOn() {
        return mCameraManager.getTorchState();
    }

    public void toggleFlash() {
        setFlash(!isFlashOn());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }

        if (!mHasSurface) {
            mHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // do nothing
    }

    @Override
    public CameraManager getCameraManager() {
        return mCameraManager;
    }

    @Override
    public Handler getHandler() {
        return mHandler;
    }

    @Override
    public void onResult(Result result) {
        mCallback.onResult(result);
    }

    @Override
    public void drawViewfinder() {
        mViewfinderView.drawViewfinder();
    }

    // Private

    private void init() {
        initPreview();
        initViewfinder();
        initHints();
    }

    private void initPreview() {
        mPreview = new SurfaceView(getContext());
        addView(mPreview);
    }

    private void initViewfinder() {
        mViewfinderView = new ViewfinderView(getContext());
        addView(mViewfinderView);
    }

    private void initHints() {
        mHints = new EnumMap<>(DecodeHintType.class);
        mHints.put(DecodeHintType.POSSIBLE_FORMATS, getFormats());
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }

        if (mCameraManager.isOpen()) {
            return;
        }

        try {
            mCameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (mHandler == null) {
                mHandler = new CaptureHandler(this, getFormats(), mHints, mViewfinderView);
            }
        } catch (IOException|RuntimeException e) {
            mCallback.onErrorExit(e);
        }
    }

    // Callback

    public interface ActivityCallback {
        void onResult(Result result);
        void onErrorExit(Exception e);
    }
}
