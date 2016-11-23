package by.vshkl.easepic.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import by.vshkl.easepic.mvp.model.Picture;
import by.vshkl.easepic.mvp.model.PictureInfo;
import by.vshkl.easepic.mvp.view.PicturesPagerView;
import by.vshkl.easepic.repository.LocalRepository;
import by.vshkl.easepic.repository.Repository;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class PicturesPagerPresenter extends MvpPresenter<PicturesPagerView> {

    private Disposable disposable;
    private Repository repository;
    private String picturesRootPath;
    private String pictureFullPath;
    private String pictureId;

    public void onStart(Context context) {
        repository = new LocalRepository(context);
    }

    public void onStop() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public void getPictures() {
        disposable = repository.getPictures(picturesRootPath)
                .subscribeOn(Schedulers.newThread())
                .onErrorReturn(new Function<Throwable, List<Picture>>() {
                    @Override
                    public List<Picture> apply(Throwable throwable) throws Exception {
                        return null;
                    }
                })
                .subscribe(new Consumer<List<Picture>>() {
                    @Override
                    public void accept(List<Picture> pictureList) throws Exception {
                        System.out.println(pictureList);

                        if (pictureList != null) {
                            for (int i = 0; i < pictureList.size(); i++) {
                                if (pictureList.get(i).getPath().equals(pictureFullPath)) {
                                    getViewState().showPictures(pictureList, i);
                                }
                            }
                        }
                    }
                });
    }

    public void getPictureInfo() {
        disposable = repository.getPictureInfo(pictureId, pictureFullPath)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Function<Throwable, PictureInfo>() {
                    @Override
                    public PictureInfo apply(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        return null;
                    }
                })
                .subscribe(new Consumer<PictureInfo>() {
                    @Override
                    public void accept(PictureInfo pictureInfo) throws Exception {
                        if (pictureInfo != null) {
                            getViewState().showPictureInfo(pictureInfo);
                        }
                    }
                });
    }

    public void deletePicture() {
        disposable = repository.deletePicture(pictureId, pictureFullPath)
                .subscribeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Function<Throwable, Boolean>() {
                    @Override
                    public Boolean apply(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        return null;
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            getViewState().onDeleted(pictureId);
                        } else {
                            //TODO: add message that picture delete failed
                        }
                    }
                });
    }

    //------------------------------------------------------------------------------------------------------------------

    public void setPicturesRootPath(String picturesRootPath) {
        this.picturesRootPath = picturesRootPath;
    }

    public void setPictureFullPath(String pictureFullPath) {
        this.pictureFullPath = pictureFullPath;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }
}
