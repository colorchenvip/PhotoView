package com.colorchen.photoview.sample.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.colorchen.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by color on 16/6/2 16:49.
 */
public class SynthesizeDemo extends AppCompatActivity implements Handler.Callback {
    private ViewPager mViewPager;
    private SamplePagerAdapter adapter;

    public final static int SAVE_IMAGE = 2;
    private Bitmap mBitmap;
    private Handler mHandler = null;
    private static final File STORAGE_DIRECTORY = Environment.getExternalStorageDirectory();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        mViewPager = (com.colorchen.photoview.sample.HackyViewPager) findViewById(R.id.view_pager);
        setContentView(mViewPager);
        adapter = new SamplePagerAdapter();
        mViewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, Menu.NONE, "保存图片");
        menu.add(Menu.NONE, 1, Menu.NONE, "Rotate 10° Left");
        menu.add(Menu.NONE, 2, Menu.NONE, "Toggle automatic rotation");
        menu.add(Menu.NONE, 3, Menu.NONE, "Reset to 0");
        menu.add(Menu.NONE, 4, Menu.NONE, "Reset to 90");
        menu.add(Menu.NONE, 5, Menu.NONE, "Reset to 180");
        menu.add(Menu.NONE, 6, Menu.NONE, "Reset to 270");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                mHandler = new Handler(this);
                WeakReference<Thread> loadThread = new WeakReference<>(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mBitmap = null;
                        mBitmap = adapter.getImagePath();
                        if (mBitmap != null) {
                            mHandler.sendEmptyMessage(SAVE_IMAGE);
                        }
                    }
                }));
                loadThread.get().start();
                return true;
            case 1:
                adapter.setRotationBy(-10);
                return true;
            case 2:
                return true;
            case 3:
                adapter.setRotationTo(0);
                return true;
            case 4:
                adapter.setRotationTo(90);
                return true;
            case 5:
                adapter.setRotationTo(180);
                return true;
            case 6:
                adapter.setRotationTo(270);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SAVE_IMAGE:
                if (mBitmap != null && !mBitmap.isRecycled()) {
                    String imagePath = saveImageFile(mBitmap);
                    fileScan(imagePath);
                    if (!TextUtils.isEmpty(imagePath)) {
                        Toast.makeText(getApplicationContext(), "保存成功:" + imagePath, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 保存完后重新扫描目标文件
     *
     * @param filePath 文件路径
     */
    public void fileScan(String filePath) {
        Uri data = Uri.parse("file:" + filePath);
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
    }

    /**
     * 保存文件
     *
     * @param bitmap
     * @return
     */
    public static String saveImageFile(Bitmap bitmap) {
        Calendar c = Calendar.getInstance();
        long ms = c.getTimeInMillis();
        int m = (int) (ms / 1000);
        String imageName = String.valueOf(m + ".jpg");
        StringBuilder filePath = new StringBuilder();
        filePath.append((new File(STORAGE_DIRECTORY, "001")).getAbsolutePath());
        filePath.append("/");
        filePath.append(imageName);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath.toString(), false);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        deleteAllBlankImage();
        return filePath.toString();
    }

    public static void deleteAllBlankImage() {
        List<File> blankFiles = getBlankFiles(new File(STORAGE_DIRECTORY, "001"), ".jpg");
        if (blankFiles == null){
            return;
        }
        for (File file : blankFiles) {
            if (file.exists()) {
                file.delete();
            }
        }

        blankFiles.clear();
    }

    //搜索目录，扩展名，是否进入子文件夹
    public static List<File> getBlankFiles(File dir, String Extension) {
        List<File> result = new ArrayList<>();
        if (!dir.exists()){
            return null;
        }
        File[] files = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isFile()) {
                if (file.getPath().substring(file.getPath().length() - Extension.length()).equals(Extension)) {
                    //判断扩展名
                    if (file.length() == 0) {
                        result.add(file);
                    }
                }
            }
        }

        return result;
    }

    @Override
    protected void onDestroy() {
        mBitmap = null;
        super.onDestroy();
    }

}
