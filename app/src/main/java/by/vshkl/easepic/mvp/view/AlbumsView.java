package by.vshkl.easepic.mvp.view;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import by.vshkl.easepic.mvp.model.Album;
import by.vshkl.easepic.ui.utils.ErrorUtils;

public interface AlbumsView extends MvpView {

    void showAlbums(List<Album> albumList);

    void showMessage(String message);

    void showError(ErrorUtils.Error error);

    void showProgress();

    void hideProgress();
}
