package by.vshkl.easepic.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.github.piasy.biv.view.BigImageView;

import java.lang.ref.WeakReference;
import java.util.List;

public class ImageViewerAdapter extends PagerAdapter {

    private WeakReference<Context> context;
    private List<Uri> uriList;
    private WeakReference<BigImageView> imageView;

    public ImageViewerAdapter(Context context, List<Uri> uriList) {
        this.context = new WeakReference<>(context);
        this.uriList = uriList;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public int getCount() {
        return (uriList != null) ? uriList.size() : 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        imageView = getImage(uriList.get(position));

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

    public Uri getUri(int position) {
        return uriList.get(position);
    }

    private WeakReference<BigImageView> getImage(Uri uri) {
        WeakReference<BigImageView> bigImageVIew = new WeakReference<>(new BigImageView(context.get()));
        bigImageVIew.get().showImage(uri);
        return bigImageVIew;
    }
}
