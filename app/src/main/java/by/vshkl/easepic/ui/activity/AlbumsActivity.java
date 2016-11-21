package by.vshkl.easepic.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.vshkl.easepic.R;
import by.vshkl.easepic.mvp.model.Album;
import by.vshkl.easepic.mvp.presenter.AlbumsPresenter;
import by.vshkl.easepic.mvp.view.AlbumsView;
import by.vshkl.easepic.ui.adapter.AlbumsAdapter;
import by.vshkl.easepic.ui.adapter.AlbumsAdapter.OnAlbumClickListener;
import by.vshkl.easepic.ui.utils.ErrorUtils;
import by.vshkl.easepic.ui.utils.PreferenceUtils;
import by.vshkl.easepic.ui.view.MarqueeToolbar;

public class AlbumsActivity extends MvpAppCompatActivity implements AlbumsView, OnAlbumClickListener {

    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 42;
    private static final int REQUEST_CAMERA = 43;

    @BindView(R.id.root)
    View rootVIew;
    @BindView(R.id.toolbar)
    MarqueeToolbar toolbar;
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;
    @BindView(R.id.rv_gallery)
    RecyclerView rvGallery;

    @InjectPresenter
    AlbumsPresenter albumsPresenter;

    private AlbumsAdapter albumsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(AlbumsActivity.this);
        setSupportActionBar(toolbar);

        initializeRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setListeners();
        albumsPresenter.onStart(AlbumsActivity.this);
        checkExternalStoragePermission();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeListeners();
        albumsPresenter.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_albums, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemSortByDate = menu.findItem(R.id.action_sort_by_date);
        MenuItem itemSortByName = menu.findItem(R.id.action_sort_by_name);

        String sortOrder = PreferenceUtils.getSortOrder(AlbumsActivity.this,
                getString(R.string.key_sort_order_albums),
                getString(R.string.value_sort_order_by_date_albums));
        if (sortOrder.equals(getString(R.string.value_sort_order_by_date_albums))) {
            itemSortByDate.setIcon(R.drawable.ic_radio_button_checked);
            itemSortByName.setIcon(R.drawable.ic_radio_button_unchecked);
        } else if (sortOrder.equals(getString(R.string.value_sort_order_by_name_albums))) {
            itemSortByDate.setIcon(R.drawable.ic_radio_button_unchecked);
            itemSortByName.setIcon(R.drawable.ic_radio_button_checked);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_date:
                PreferenceUtils.storeSortOrder(AlbumsActivity.this,
                        getString(R.string.key_sort_order_albums),
                        getString(R.string.value_sort_order_by_date_albums));
                invalidateOptionsMenu();
                sortAndSetAlbumsList(albumsAdapter.getAlbumList());
                return true;
            case R.id.action_sort_by_name:
                PreferenceUtils.storeSortOrder(AlbumsActivity.this,
                        getString(R.string.key_sort_order_albums),
                        getString(R.string.value_sort_order_by_name_albums));
                invalidateOptionsMenu();
                sortAndSetAlbumsList(albumsAdapter.getAlbumList());
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(rootVIew, R.string.permission_read_external_storage_granted, Snackbar.LENGTH_SHORT)
                            .show();
                    albumsPresenter.getLibrary();
                } else {
                    Snackbar.make(rootVIew, R.string.permission_read_external_storage_not_granted, Snackbar.LENGTH_SHORT)
                            .show();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void showAlbums(List<Album> albumList) {
        sortAndSetAlbumsList(albumList);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(ErrorUtils.Error error) {
        Toast.makeText(this, ErrorUtils.getMessageString(AlbumsActivity.this, error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {
        rvGallery.setVisibility(View.GONE);
        pbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        pbLoading.setVisibility(View.GONE);
        rvGallery.setVisibility(View.VISIBLE);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void onAlbumClicked(Album.StorageType storageType, String albumId, String albumName) {
        Intent intent = new Intent(AlbumsActivity.this, AlbumActivity.class);
        intent.putExtra(AlbumActivity.EXTRA_STORAGE_TYPE, storageType);
        intent.putExtra(AlbumActivity.EXTRA_ALBUM_ID, albumId);
        intent.putExtra(AlbumActivity.EXTRA_ALBUM_NAME, albumName);
        startActivity(intent);
    }

    //------------------------------------------------------------------------------------------------------------------

    private void initializeRecyclerView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int itemDimension;

        GridLayoutManager gridLayoutManager;
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                gridLayoutManager = new GridLayoutManager(AlbumsActivity.this, 2);
                itemDimension = displayMetrics.widthPixels / 2;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                gridLayoutManager = new GridLayoutManager(AlbumsActivity.this, 3);
                itemDimension = displayMetrics.widthPixels / 3;
                break;
            default:
                gridLayoutManager = new GridLayoutManager(AlbumsActivity.this, 2);
                itemDimension = displayMetrics.widthPixels / 2;
                break;
        }
        rvGallery.setLayoutManager(gridLayoutManager);

        albumsAdapter = new AlbumsAdapter(itemDimension);
        rvGallery.setAdapter(albumsAdapter);
    }

    private void checkExternalStoragePermission() {
        if (ActivityCompat.checkSelfPermission(AlbumsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            checkRationaleRequiredAndRequestExternalStoragePermission();
        } else {
            albumsPresenter.getLibrary();
        }
    }

    private void checkRationaleRequiredAndRequestExternalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                AlbumsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Snackbar.make(rootVIew, R.string.permission_read_external_storage_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestExternalStoragePermission();
                        }
                    })
                    .show();
        } else {
            requestExternalStoragePermission();
        }
    }

    private void requestExternalStoragePermission() {
        ActivityCompat.requestPermissions(AlbumsActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
    }

    private void sortAndSetAlbumsList(List<Album> albumList) {
        String sortOrder = PreferenceUtils.getSortOrder(AlbumsActivity.this,
                getString(R.string.key_sort_order_albums),
                getString(R.string.value_sort_order_by_date_albums));
        if (sortOrder.equals(getString(R.string.value_sort_order_by_date_albums))) {
            if (albumList.size() > 0) {
                Collections.sort(albumList, new Comparator<Album>() {
                    @Override
                    public int compare(Album object1, Album object2) {
                        return object2.getBucketDate().toUpperCase().compareTo(object1.getBucketDate().toUpperCase());
                    }
                });
            }
        } else if (sortOrder.equals(getString(R.string.value_sort_order_by_name_albums))) {
            if (albumList.size() > 0) {
                Collections.sort(albumList, new Comparator<Album>() {
                    @Override
                    public int compare(Album object1, Album object2) {
                        return object1.getBucketName().toUpperCase().compareTo(object2.getBucketName().toUpperCase());
                    }
                });
            }
        }
        albumsAdapter.setAlbumList(albumList);
        albumsAdapter.notifyDataSetChanged();
    }

    private void setListeners() {
        albumsAdapter.setOnAlbumClickListener(AlbumsActivity.this);
    }

    private void removeListeners() {
        albumsAdapter.removeOnAlbumClickListener();
    }
}
