package by.vshkl.easepic.repository;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import by.vshkl.easepic.mvp.model.Album;
import by.vshkl.easepic.mvp.model.Picture;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class LocalRepository implements Repository {

    private WeakReference<Context> context;

    public LocalRepository(Context context) {
        this.context = new WeakReference<>(context);
    }

    @Override
    public Observable<List<Album>> getAlbums() {
        final String[] projection = {
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA};
        final String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC";

        return Observable.create(new ObservableOnSubscribe<List<Album>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Album>> emitter) throws Exception {
                List<Album> albumList = new ArrayList<>();
                albumList.addAll(getAlbumsFromInternalStorage(context.get(), projection, sortOrder));
                albumList.addAll(getAlbumsFromExternalStorage(context.get(), projection, sortOrder));
                emitter.onNext(albumList);
            }
        });
    }

    @Override
    public Observable<List<Picture>> getPictures(final Album.StorageType storageType, String albumId) {
        final String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.DATA};
        final String selectionClause = MediaStore.Images.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = {albumId};
        final String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC";

        return Observable.create(new ObservableOnSubscribe<List<Picture>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Picture>> emitter) throws Exception {
                List<Picture> pictureList = new ArrayList<>();
                pictureList.addAll(getPicturesFromStorage(
                        context.get(), storageType, projection, selectionClause, selectionArgs, sortOrder));
                emitter.onNext(pictureList);
            }
        });
    }

    //------------------------------------------------------------------------------------------------------------------

    private List<Album> getAlbumsFromInternalStorage(Context context, String[] projection, String sortOrder) {
        return getAlbumsFromStorage(context, MediaStore.Images.Media.INTERNAL_CONTENT_URI, projection, sortOrder);
    }

    private List<Album> getAlbumsFromExternalStorage(Context context, String[] projection, String selectionClause) {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return getAlbumsFromStorage(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selectionClause);
        }
        return new ArrayList<>();
    }

    private List<Album> getAlbumsFromStorage(Context context, Uri storageUri, String[] projection, String sortOrder) {
        Cursor cursor = context.getContentResolver().query(
                storageUri,
                projection,
                null,
                null,
                sortOrder
        );

        LinkedHashSet<Album> albumLinkedHashSet = null;

        if (cursor != null) {
            albumLinkedHashSet = new LinkedHashSet<>(cursor.getCount());

            int indexBucketId = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
            int indexBucketDate = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);
            int indexBucketName = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int indexBucketData = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            cursor.moveToFirst();

            while (cursor.moveToNext()) {
                Album album = new Album();
                album.setBucketId(cursor.getString(indexBucketId));
                album.setBucketDate(cursor.getString(indexBucketDate));
                album.setBucketName(cursor.getString(indexBucketName));
                if (storageUri.equals(MediaStore.Images.Media.INTERNAL_CONTENT_URI)) {
                    album.setBucketStorageType(Album.StorageType.INTERNAL);
                } else {
                    album.setBucketStorageType(Album.StorageType.EXTERNAL);
                }
                album.setBucketThumbnail(cursor.getString(indexBucketData));
                albumLinkedHashSet.add(album);
            }

            cursor.close();
        }

        return new ArrayList<>(albumLinkedHashSet);
    }

    private List<Picture> getPicturesFromStorage(Context context, Album.StorageType storageType, String[] projection,
                                                 String selectionClause, String[] selectionArgs, String sortOrder) {
        Uri storageUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        if (storageType.equals(Album.StorageType.EXTERNAL)) {
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                storageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            }
        }

        Cursor cursor = context.getContentResolver().query(
                storageUri,
                projection,
                selectionClause,
                selectionArgs,
                sortOrder
        );

        List<Picture> pictureList = null;

        if (cursor != null) {
            pictureList = new ArrayList<>(cursor.getCount());

            int indexPictureId = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            int indexPictureName = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
            int indexPictureData = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            while (cursor.moveToNext()) {
                Picture picture = new Picture();
                picture.setId(cursor.getString(indexPictureId));
                picture.setName(cursor.getString(indexPictureName));
                picture.setPath(cursor.getString(indexPictureData));
                pictureList.add(picture);
            }

            cursor.close();
        }

        return pictureList;
    }
}
