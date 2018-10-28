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

import com.google.zxing.Result;

public interface ViewCallback {

    CameraManager getCameraManager();
    Handler getHandler();
    void onResult(Result result);
    void drawViewfinder();

}
