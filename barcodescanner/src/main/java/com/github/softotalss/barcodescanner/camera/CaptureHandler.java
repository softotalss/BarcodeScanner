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

package com.github.softotalss.barcodescanner.camera;

import android.os.Handler;
import android.os.Message;

import com.github.softotalss.barcodescanner.R;
import com.github.softotalss.barcodescanner.utils.Constants;
import com.github.softotalss.barcodescanner.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultPointCallback;

import java.util.Collection;
import java.util.Map;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureHandler extends Handler {

    private final DecodeThread decodeThread;
    private State state;
    private final ViewCallback mViewCallback;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    public CaptureHandler(ViewCallback viewCallback,
                          Collection<BarcodeFormat> decodeFormats,
                          Map<DecodeHintType,?> baseHints, ViewfinderView viewfinderView) {
        this.mViewCallback = viewCallback;
        decodeThread = new DecodeThread(viewCallback, decodeFormats,
                baseHints, new ViewfinderResultPointCallback(viewfinderView));
        decodeThread.start();
        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        mViewCallback.getCameraManager().startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case Constants.RESTART_PREVIEW:
                restartPreviewAndDecode();
                break;
            case Constants.DECODE_SUCCEEDED:
                state = State.SUCCESS;
                mViewCallback.onResult((Result) message.obj);
                break;
            case Constants.DECODE_FAILED:
                // We're decoding as fast as possible, so when one decode fails, start another.
                state = State.PREVIEW;
                mViewCallback.getCameraManager().requestPreviewFrame(decodeThread.getHandler(), Constants.DECODE);
                break;
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        mViewCallback.getCameraManager().stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), Constants.QUIT);
        quit.sendToTarget();
        try {
            // Wait at most half a second; should be enough time, and onPause() will timeout quickly
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(Constants.DECODE_SUCCEEDED);
        removeMessages(Constants.DECODE_FAILED);
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            mViewCallback.getCameraManager().requestPreviewFrame(decodeThread.getHandler(), Constants.DECODE);
            mViewCallback.drawViewfinder();
        }
    }
}

