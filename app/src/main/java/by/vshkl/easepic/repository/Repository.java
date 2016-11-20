package by.vshkl.easepic.repository;

import java.util.List;

import by.vshkl.easepic.mvp.model.Album;
import by.vshkl.easepic.mvp.model.Picture;
import io.reactivex.Observable;

public interface Repository {

    Observable<List<Album>> getAlbums();

    Observable<List<Picture>> getPictures(Album.StorageType storageType, String albumId);

    Observable<Boolean> updateAlbumName(Album.StorageType storageType, String albumId, String albumNewName);
}
