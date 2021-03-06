package by.vshkl.easepic.repository;

import java.util.List;

import by.vshkl.easepic.mvp.model.Album;
import by.vshkl.easepic.mvp.model.Picture;
import by.vshkl.easepic.mvp.model.PictureInfo;
import io.reactivex.Observable;

public interface Repository {

    Observable<List<Album>> getAlbums();

    Observable<List<Picture>> getPictures(Album.StorageType storageType, String albumId);

    Observable<List<Picture>> getPictures(String picturesRootPath);

    Observable<PictureInfo> getPictureInfo(String pictureId, String picturePath);

    Observable<Boolean> deletePicture(String pictureId, String picturePath);
}
