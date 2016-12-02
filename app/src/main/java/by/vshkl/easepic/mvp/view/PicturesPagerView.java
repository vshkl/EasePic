package by.vshkl.easepic.mvp.view;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import by.vshkl.easepic.mvp.model.Picture;
import by.vshkl.easepic.mvp.model.PictureInfo;
import by.vshkl.easepic.ui.utils.ErrorUtils;

public interface PicturesPagerView extends MvpView {

    void showPictures(List<Picture> pictureList, int position);

    void showMessage(String message);

    void showError(ErrorUtils.Error error);

    void showPictureInfo(PictureInfo pictureInfo);

    void onDeleted(String pictureId);
}
