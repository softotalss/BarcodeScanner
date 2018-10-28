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
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * This thread does all the heavy lifting of decoding the images.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
final class DecodeThread extends Thread {

    private final Map<DecodeHintType,Object> hints;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;
    private final ViewCallback mViewCallback;

    DecodeThread(ViewCallback viewCallback, Collection<BarcodeFormat> decodeFormats,
                 Map<DecodeHintType,?> baseHints, ResultPointCallback resultPointCallback) {
        this.mViewCallback = viewCallback;
        handlerInitLatch = new CountDownLatch(1);
        hints = new EnumMap<>(DecodeHintType.class);
        hints.putAll(baseHints);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
    }

    Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new DecodeHandler(mViewCallback, hints);
        handlerInitLatch.countDown();
        Looper.loop();
    }

}

