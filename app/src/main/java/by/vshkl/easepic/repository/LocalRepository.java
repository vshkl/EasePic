package by.vshkl.easepic.repository;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import by.vshkl.easepic.mvp.model.Album;
import by.vshkl.easepic.mvp.model.Picture;
import by.vshkl.easepic.mvp.model.PictureInfo;
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
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.DATE_MODIFIED,
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
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.DISPLAY_NAME,
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

    @Override
    public Observable<List<Picture>> getPictures(final String picturesRootPath) {
        final String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA};
        final String selectionClause = MediaStore.Images.Media.DATA + " LIKE ?";
        final String[] selectionArgs = {"%" + picturesRootPath + "%"};
        final String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC";

        return Observable.create(new ObservableOnSubscribe<List<Picture>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Picture>> emitter) throws Exception {
                List<Picture> pictureList;

                if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)
                        && picturesRootPath.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                    pictureList = getPicturesFromStorage(context.get(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            projection, selectionClause, selectionArgs, sortOrder);
                } else {
                    pictureList = getPicturesFromStorage(context.get(), MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                            projection, selectionClause, selectionArgs, sortOrder);
                }

                emitter.onNext(pictureList);
            }
        });
    }

    @Override
    public Observable<PictureInfo> getPictureInfo(String pictureId, final String picturePath) {
        final String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.DATE_TAKEN};
        final String selectionClause = MediaStore.Images.Media._ID + " = ?";
        final String[] selectionArgs = {pictureId};

        return Observable.create(new ObservableOnSubscribe<PictureInfo>() {
            @Override
            public void subscribe(ObservableEmitter<PictureInfo> emitter) throws Exception {
                if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)
                        && picturePath.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                    emitter.onNext(getPictureInfo(context.get(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            projection, selectionClause, selectionArgs));
                } else {
                    emitter.onNext(getPictureInfo(context.get(), MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                            projection, selectionClause, selectionArgs));
                }
            }
        });
    }

    @Override
    public Observable<Boolean> deletePicture(String pictureId, final String picturePath) {
        final String selectionClause = MediaStore.Images.Media._ID + " = ?";
        final String[] selectionArgs = {pictureId};

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                int deletedPictures = 0;

                if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)
                        && picturePath.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                    deletedPictures = context.get().getContentResolver()
                            .delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selectionClause, selectionArgs);
                } else {
                    deletedPictures = context.get().getContentResolver()
                            .delete(MediaStore.Images.Media.INTERNAL_CONTENT_URI, selectionClause, selectionArgs);
                }

                emitter.onNext(deletedPictures > 0);
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
                sortOrder);

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

        return getPicturesFromStorage(context, storageUri, projection, selectionClause, selectionArgs, sortOrder);
    }

    private List<Picture> getPicturesFromStorage(Context context, Uri storageUri, String[] projection,
                                                 String selectionClause, String[] selectionArgs, String sortOrder) {
        Cursor cursor = context.getContentResolver().query(
                storageUri,
                projection,
                selectionClause,
                selectionArgs,
                sortOrder);

        List<Picture> pictureList = null;

        if (cursor != null) {
            pictureList = new ArrayList<>(cursor.getCount());

            int indexPictureId = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            int indexPictureDate = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);
            int indexPictureName = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
            int indexPictureData = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            while (cursor.moveToNext()) {
                Picture picture = new Picture();
                picture.setId(cursor.getString(indexPictureId));
                picture.setDate(cursor.getString(indexPictureDate));
                picture.setName(cursor.getString(indexPictureName));
                picture.setPath(cursor.getString(indexPictureData));
                pictureList.add(picture);
            }

            cursor.close();
        }

        return pictureList;
    }

    private PictureInfo getPictureInfo(Context context, Uri storageUri,
                                       String[] projection, String selectionClause, String[] selectionArgs) {
        Cursor cursor = context.getContentResolver().query(
                storageUri,
                projection,
                selectionClause,
                selectionArgs,
                null);

        PictureInfo pictureInfo = new PictureInfo();
        if (cursor != null) {
            int indexPicturePath = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int indexPictureMimeType = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE);
            int indexPictureSize = cursor.getColumnIndex(MediaStore.Images.Media.SIZE);
            int indexPictureWidth = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH);
            int indexPictureHeight = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT);
            int indexPictureDate = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);

            while (cursor.moveToNext()) {
                pictureInfo.setPath(cursor.getString(indexPicturePath));
                pictureInfo.setMimeType(cursor.getString(indexPictureMimeType));
                pictureInfo.setSize(cursor.getString(indexPictureSize));
                pictureInfo.setWidth(cursor.getString(indexPictureWidth));
                pictureInfo.setHeight(cursor.getString(indexPictureHeight));
                pictureInfo.setDate(cursor.getString(indexPictureDate));
            }

            cursor.close();
        }

        return pictureInfo;
    }
}
