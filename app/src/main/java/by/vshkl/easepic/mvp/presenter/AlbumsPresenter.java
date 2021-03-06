package by.vshkl.easepic.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import by.vshkl.easepic.mvp.model.Album;
import by.vshkl.easepic.mvp.view.AlbumsView;
import by.vshkl.easepic.repository.LocalRepository;
import by.vshkl.easepic.repository.Repository;
import by.vshkl.easepic.ui.utils.ErrorUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class AlbumsPresenter extends MvpPresenter<AlbumsView> {

    private Disposable disposable;
    private Repository repository;

    public void onStart(Context context) {
        repository = new LocalRepository(context);
        getViewState().showProgress();
    }

    public void onStop() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public void getLibrary() {
        disposable = repository.getAlbums()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Function<Throwable, List<Album>>() {
                    @Override
                    public List<Album> apply(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        getViewState().hideProgress();
                        getViewState().showError(ErrorUtils.Error.ERROR_GET_ALBUMS);
                        return null;
                    }
                })
                .subscribe(new Consumer<List<Album>>() {
                    @Override
                    public void accept(List<Album> albumList) throws Exception {
                        getViewState().hideProgress();
                        getViewState().showAlbums(albumList);
                    }
                });
    }
}
