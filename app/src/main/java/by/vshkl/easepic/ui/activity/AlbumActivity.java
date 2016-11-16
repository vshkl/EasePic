package by.vshkl.easepic.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.vshkl.easepic.R;
import by.vshkl.easepic.mvp.model.Album;
import by.vshkl.easepic.mvp.model.Picture;
import by.vshkl.easepic.mvp.presenter.AlbumPresenter;
import by.vshkl.easepic.mvp.view.AlbumView;
import by.vshkl.easepic.ui.adapter.AlbumAdapter;
import by.vshkl.easepic.ui.adapter.AlbumAdapter.OnPictureClickListener;
import by.vshkl.easepic.ui.view.ImageViewer;

public class AlbumActivity extends MvpAppCompatActivity implements AlbumView, OnPictureClickListener {

    public static final String EXTRA_STORAGE_TYPE = "EXTRA_STORAGE_TYPE";
    public static final String EXTRA_ALBUM_ID = "EXTRA_ALBUM_ID";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;
    @BindView(R.id.rv_gallery)
    RecyclerView rvGallery;

    @InjectPresenter
    AlbumPresenter albumPresenter;

    private AlbumAdapter albumAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(AlbumActivity.this);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        initializePresenter(
                (Album.StorageType) intent.getSerializableExtra(EXTRA_STORAGE_TYPE),
                intent.getStringExtra(EXTRA_ALBUM_ID));

        initializeRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setListeners();
        albumPresenter.onStart(AlbumActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeListeners();
        albumPresenter.onStop();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void showPictures(List<Picture> pictureList) {
        albumAdapter.setPictureList(pictureList);
        albumAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
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
    public void onPictureClicked(int position) {
        new ImageViewer.Builder(this, albumAdapter.getUriList())
                .setStartPosition(position)
                .show();
    }

    //------------------------------------------------------------------------------------------------------------------

    private void initializePresenter(Album.StorageType storageType, String albumId) {
        albumPresenter.setStorageType(storageType);
        albumPresenter.setAlbumId(albumId);
    }

    private void initializeRecyclerView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int itemDimension;

        GridLayoutManager gridLayoutManager;
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                gridLayoutManager = new GridLayoutManager(AlbumActivity.this, 3);
                itemDimension = displayMetrics.widthPixels / 3;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                gridLayoutManager = new GridLayoutManager(AlbumActivity.this, 6);
                itemDimension = displayMetrics.widthPixels / 6;
                break;
            default:
                gridLayoutManager = new GridLayoutManager(AlbumActivity.this, 3);
                itemDimension = displayMetrics.widthPixels / 3;
                break;
        }
        rvGallery.setLayoutManager(gridLayoutManager);

        albumAdapter = new AlbumAdapter(itemDimension);
        rvGallery.setAdapter(albumAdapter);
    }

    private void setListeners() {
        albumAdapter.setOnPictureClickListener(AlbumActivity.this);
    }

    private void removeListeners() {
        albumAdapter.removeOnPictureClickListener();
    }
}
