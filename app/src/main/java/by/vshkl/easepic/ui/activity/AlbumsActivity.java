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
import by.vshkl.easepic.mvp.presenter.AlbumsPresenter;
import by.vshkl.easepic.mvp.view.AlbumsView;
import by.vshkl.easepic.ui.adapter.AlbumsAdapter;
import by.vshkl.easepic.ui.adapter.AlbumsAdapter.OnAlbumClickListener;

public class AlbumsActivity extends MvpAppCompatActivity implements AlbumsView, OnAlbumClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
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
        albumsPresenter.onStart(AlbumsActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        albumsPresenter.onStop();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void showAlbums(List<Album> albumList) {
        albumsAdapter.setAlbumList(albumList);
        albumsAdapter.notifyDataSetChanged();
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
    public void onAlbumClicked(Album.StorageType storageType, String albumId) {
        Intent intent = new Intent(AlbumsActivity.this, AlbumActivity.class);
        intent.putExtra(AlbumActivity.EXTRA_STORAGE_TYPE, storageType);
        intent.putExtra(AlbumActivity.EXTRA_ALBUM_ID, albumId);
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
        albumsAdapter.setOnAlbumClickListener(AlbumsActivity.this);
        rvGallery.setAdapter(albumsAdapter);
    }
}
