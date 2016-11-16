package by.vshkl.easepic.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import by.vshkl.easepic.mvp.model.Album;
import by.vshkl.easepic.mvp.model.Picture;
import by.vshkl.easepic.mvp.view.AlbumView;
import by.vshkl.easepic.repository.LocalRepository;
import by.vshkl.easepic.repository.Repository;
import by.vshkl.easepic.ui.utils.ErrorUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class AlbumPresenter extends MvpPresenter<AlbumView> {

    private Disposable disposable;
    private Repository repository;
    private Album.StorageType storageType;
    private String albumId;

    public void onStart(Context context) {
        repository = new LocalRepository(context);
        getViewState().showProgress();
        getPictures();
    }

    public void onStop() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public void getPictures() {
        System.out.println(storageType);
        System.out.println(albumId);
        disposable = repository.getPictures(storageType, albumId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Function<Throwable, List<Picture>>() {
                    @Override
                    public List<Picture> apply(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        getViewState().hideProgress();
                        getViewState().showError(ErrorUtils.Error.ERROR_NO_PICTURES);
                        return null;
                    }
                })
                .subscribe(new Consumer<List<Picture>>() {
                    @Override
                    public void accept(List<Picture> pictureList) throws Exception {
                        if (pictureList != null) {
                            getViewState().hideProgress();
                            getViewState().showPictures(pictureList);
                        } else {
                            getViewState().showError(ErrorUtils.Error.ERROR_NO_PICTURES);
                        }
                    }
                });
    }

    public void setStorageType(Album.StorageType storageType) {
        this.storageType = storageType;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }
}
