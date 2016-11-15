package by.vshkl.easepic.repository;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import by.vshkl.easepic.mvp.model.Album;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class LocalRepository implements Repository {

    private Context context;

    public LocalRepository(Context context) {
        this.context = context;
    }

    @Override
    public Observable<List<Album>> getAlbums() {
        final String[] projection = {
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA};
        final String sortOrder = MediaStore.Images.Media.DATE_MODIFIED;

        return Observable.create(new ObservableOnSubscribe<List<Album>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Album>> emitter) throws Exception {
                List<Album> albumList = new ArrayList<>();
                albumList.addAll(getFromInternalStorage(context, projection, sortOrder));
                albumList.addAll(getFromExternalStorage(context, projection, sortOrder));
                emitter.onNext(albumList);
            }
        });
    }

    //------------------------------------------------------------------------------------------------------------------

    private List<Album> getFromInternalStorage(Context contexts, String[] projection, String sortOrder) {
        return getFromStorage(contexts, MediaStore.Images.Media.INTERNAL_CONTENT_URI, projection, sortOrder);
    }

    private List<Album> getFromExternalStorage(Context contexts, String[] projection, String selectionClause) {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return getFromStorage(contexts, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selectionClause);
        }
        return new ArrayList<>();
    }

    private List<Album> getFromStorage(Context contexts, Uri storageUri, String[] projection, String sortOrder) {
        Cursor cursor = contexts.getContentResolver().query(
                storageUri,
                projection,
                null,
                null,
                sortOrder
        );

        List<Album> albumList = new ArrayList<>();

        if (cursor != null) {
            int indexBucketId = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
            int indexBucketName = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                Album album = new Album();
                album.setBucketId(cursor.getString(indexBucketId));
                album.setBucketName(cursor.getString(indexBucketName));
                if (!albumList.contains(album)) {
                    albumList.add(album);
                }
                cursor.moveToPosition(i);
            }

            cursor.close();
        }

        Collections.reverse(albumList);
        return albumList;
    }
}
