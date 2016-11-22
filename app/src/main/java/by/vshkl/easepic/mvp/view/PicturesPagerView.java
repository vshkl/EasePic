package by.vshkl.easepic.mvp.view;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import by.vshkl.easepic.mvp.model.Picture;
import by.vshkl.easepic.mvp.model.PictureInfo;

public interface PicturesPagerView extends MvpView {

    void showPictures(List<Picture> pictureList, int position);

    void showPictureInfo(PictureInfo pictureInfo);
}
