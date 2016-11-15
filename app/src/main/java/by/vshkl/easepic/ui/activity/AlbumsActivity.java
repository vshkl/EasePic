package by.vshkl.easepic.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

public class AlbumsActivity extends MvpAppCompatActivity implements AlbumsView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;
    @BindView(R.id.rv_gallery_folders)
    RecyclerView rvGalleryFolders;

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
        rvGalleryFolders.setVisibility(View.GONE);
        pbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        pbLoading.setVisibility(View.GONE);
        rvGalleryFolders.setVisibility(View.VISIBLE);
    }

    //------------------------------------------------------------------------------------------------------------------

    private void initializeRecyclerView() {
        GridLayoutManager gridLayoutManager;
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                gridLayoutManager = new GridLayoutManager(AlbumsActivity.this, 2);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                gridLayoutManager = new GridLayoutManager(AlbumsActivity.this, 3);
                break;
            default:
                gridLayoutManager = new GridLayoutManager(AlbumsActivity.this, 2);
                break;
        }
        rvGalleryFolders.setLayoutManager(gridLayoutManager);

        albumsAdapter = new AlbumsAdapter();
        rvGalleryFolders.setAdapter(albumsAdapter);
    }
}
