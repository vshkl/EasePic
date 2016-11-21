package by.vshkl.easepic.mvp.view;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import by.vshkl.easepic.mvp.model.Picture;

public interface PicturesPagerView extends MvpView {

    void showPictures(List<Picture> pictureList, int position);
}
