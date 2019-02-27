/*
 * Copyright 2018 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.learzhu.androidserver;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import androidx.annotation.NonNull;
import com.learzhu.androidserver.util.FileUtils;

import java.io.File;

/**
 * Created by YanZhenjie on 2018/6/9.
 */
public class AndroidServerApp extends Application {

    private static AndroidServerApp mInstance;

    private File mRootDir;

    @Override
    public void onCreate() {
        super.onCreate();

        if (mInstance == null) {
            mInstance = this;
            initRootPath(this);
        }
    }

    @NonNull
    public static AndroidServerApp getInstance() {
        return mInstance;
    }

    @NonNull
    public File getRootDir() {
        return mRootDir;
    }

    private void initRootPath(Context context) {
        if (mRootDir != null) return;

        if (FileUtils.storageAvailable()) {
            mRootDir = Environment.getExternalStorageDirectory();
        } else {
            mRootDir = context.getFilesDir();
        }
        mRootDir = new File(mRootDir, "AndroidServer");
//        IOUtils.createFolder(mRootDir);
    }
}