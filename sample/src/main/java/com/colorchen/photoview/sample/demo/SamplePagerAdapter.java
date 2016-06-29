package com.colorchen.photoview.sample.demo;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.colorchen.photoview.sample.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by color on 16/6/2 16:52.
 */
public class SamplePagerAdapter extends PagerAdapter {
    private static final int[] sDrawables = { R.drawable.wallpaper, R.drawable.wallpaper, R.drawable.wallpaper,
            R.drawable.wallpaper, R.drawable.wallpaper, R.drawable.wallpaper };
    private PhotoView currentPager;

    @Override
    public int getCount() {
        return sDrawables.length;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(container.getContext());
        final PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);
        Picasso.with(container.getContext())
//                .load("http://img.xgo-img.com.cn/pics/383/720/450/382694.jpg")
                .load(sDrawables[position])
                .into(photoView, new Callback() {
                    @Override
                    public void onSuccess() {
                        attacher.update();
                    }

                    @Override
                    public void onError() {
                    }
                });
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        currentPager = (PhotoView) object;
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public PhotoView getCurrentPager(){
        return currentPager;
    }
    public void setRotationBy(int num){
        currentPager.setRotationBy(num);
    }
    public void setRotationTo(int num){
        currentPager.setRotationBy(num);
    }
    //获得图片访问地址
    public Bitmap getImagePath() {
        if (currentPager != null){
            return currentPager.getVisibleRectangleBitmap();
        }else{
            return null;
        }
    }

}
