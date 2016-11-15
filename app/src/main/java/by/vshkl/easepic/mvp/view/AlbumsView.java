package by.vshkl.easepic.mvp.view;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import by.vshkl.easepic.mvp.model.Album;

public interface AlbumsView extends MvpView {

    void showAlbums(List<Album> albumList);

    void showMessage(String message);

    void showError(String error);

    void showProgress();

    void hideProgress();
}
