package by.vshkl.easepic.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.github.piasy.biv.view.BigImageView;

import java.lang.ref.WeakReference;
import java.util.List;

import by.vshkl.easepic.mvp.model.Picture;
import by.vshkl.easepic.ui.view.MarqueeToolbar;

public class ImageViewerAdapter extends PagerAdapter {

    private WeakReference<Context> context;
    private List<Picture> pictureList;
    private WeakReference<BigImageView> imageView;
    private WeakReference<MarqueeToolbar> toolbar;

    public ImageViewerAdapter(Context context, List<Picture> pictureList) {
        this.context = new WeakReference<>(context);
        this.pictureList = pictureList;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public int getCount() {
        return (pictureList != null) ? pictureList.size() : 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        imageView = getImage(new Uri.Builder().scheme("file").path(pictureList.get(position).getPath()).build());
        toolbar = getToolbar(pictureList.get(position).getName());

        try {
            container.addView(imageView.get(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageView.get();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((BigImageView) object);
    }

    //------------------------------------------------------------------------------------------------------------------

    public boolean isScaled() {
        return (imageView.get().getScaleX() == 0.0F && imageView.get().getScaleY() == 0.0F);
    }

    public void resetScale() {
        imageView.get().setScaleX(0.0F);
        imageView.get().setScaleY(0.0F);
    }

    public Picture getPicture(int position) {
        return pictureList.get(position);
    }

    private WeakReference<BigImageView> getImage(Uri uri) {
        WeakReference<BigImageView> bigImageVIew = new WeakReference<>(new BigImageView(context.get()));
        bigImageVIew.get().showImage(uri);
        return bigImageVIew;
    }

    private WeakReference<MarqueeToolbar> getToolbar(String title) {
        WeakReference<MarqueeToolbar> toolbar = new WeakReference<>(new MarqueeToolbar(context.get()));
        toolbar.get().setTitle(title);
        return toolbar;
    }
}
