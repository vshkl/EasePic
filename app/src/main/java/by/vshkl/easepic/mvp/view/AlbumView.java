package by.vshkl.easepic.mvp.view;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import by.vshkl.easepic.mvp.model.Picture;

public interface AlbumView extends MvpView {

    void showPictures(List<Picture> pictureList);

    void showMessage(String message);

    void showError(String error);

    void showProgress();

    void hideProgress();
}
