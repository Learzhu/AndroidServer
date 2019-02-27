package com.learzhu.androidserver.handler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import com.learzhu.androidserver.AndroidServerApp;
import com.learzhu.androidserver.R;
import com.yanzhenjie.andserver.RequestMethod;
import com.yanzhenjie.andserver.SimpleRequestHandler;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.view.View;
import org.apache.httpcore.HttpEntity;
import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.entity.ContentType;
import org.apache.httpcore.entity.FileEntity;

import java.io.*;
import java.nio.charset.Charset;

import static com.yanzhenjie.andserver.util.FileUtils.getMimeType;


/**
 * 作者：leavesC
 * 时间：2018/4/5 16:30
 * 描述：https://github.com/leavesC/AndroidServer
 * https://www.jianshu.com/u/9df45b87cfdf
 */
public class DownloadImageHandler extends SimpleRequestHandler {

    private File file = new File(Environment.getExternalStorageDirectory(), "leavesC.jpg");

    @RequestMapping(method = {RequestMethod.GET})
    @Override
    protected View handle(HttpRequest request) throws HttpException, IOException {
        writeToSdCard();
        HttpEntity httpEntity = new FileEntity(file, ContentType.create(getMimeType(file.getAbsolutePath()), Charset.defaultCharset()));
        return new View(200, httpEntity);
    }

    private void writeToSdCard() throws IOException {
        if (!file.exists()) {
            synchronized (DownloadImageHandler.class) {
                if (!file.exists()) {
                    boolean b = file.createNewFile();
                    if (!b) {
                        throw new IOException("What broken cell phone.");
                    }
                    Bitmap bitmap = BitmapFactory.decodeResource(AndroidServerApp.getInstance().getResources(), R.mipmap.ic_launcher_round);
                    OutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (outputStream != null) {
                            outputStream.flush();
                            outputStream.close();
                        }
                    }
                }
            }
        }
    }

}