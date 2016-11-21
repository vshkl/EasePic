package by.vshkl.easepic.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import by.vshkl.easepic.mvp.model.Picture;
import by.vshkl.easepic.mvp.view.PicturesPagerView;
import by.vshkl.easepic.repository.LocalRepository;
import by.vshkl.easepic.repository.Repository;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class PicturesPagerPresenter extends MvpPresenter<PicturesPagerView> {

    private String picturesRootPath;
    private String pictureFullPath;

    public void getPictures(Context context) {
        Repository repository = new LocalRepository(context);

        repository.getPictures(picturesRootPath)
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

    public void setPicturesRootPath(String picturesRootPath) {
        this.picturesRootPath = picturesRootPath;
    }

    public void setPictureFullPath(String pictureFullPath) {
        this.pictureFullPath = pictureFullPath;
    }
}
