package by.vshkl.easepic.repository;

import java.util.List;

import by.vshkl.easepic.mvp.model.Album;
import io.reactivex.Observable;

public interface Repository {

    Observable<List<Album>> getAlbums();
}
