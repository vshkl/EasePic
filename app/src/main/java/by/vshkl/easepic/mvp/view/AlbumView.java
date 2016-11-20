package by.vshkl.easepic.mvp.view;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import by.vshkl.easepic.mvp.model.Picture;
import by.vshkl.easepic.ui.utils.ErrorUtils;

public interface AlbumView extends MvpView {

    void showPictures(List<Picture> pictureList);

    void showUpdatedAlbumName(String newAlbumName);

    void showMessage(String message);

    void showError(ErrorUtils.Error error);

    void showProgress();

    void hideProgress();
}
