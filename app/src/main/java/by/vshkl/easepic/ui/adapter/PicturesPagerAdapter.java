package by.vshkl.easepic.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import by.vshkl.easepic.mvp.model.Picture;
import by.vshkl.easepic.ui.fragment.PictureFragment;

public class PicturesPagerAdapter extends FragmentStatePagerAdapter {

    private List<Picture> pictureList;

    public PicturesPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return PictureFragment.newInstance(pictureList.get(position));
    }

    @Override
    public int getCount() {
        return (pictureList != null) ? pictureList.size() : 0;
    }

    public void setPictureList(List<Picture> pictureList) {
        this.pictureList = pictureList;
    }

    public String getPictureName(int position) {
        return pictureList.get(position).getName();
    }

    public String getPictureId(int position) {
        return pictureList.get(position).getId();
    }

    public String getPicturePath(int position) {
        return pictureList.get(position).getPath();
    }
}
