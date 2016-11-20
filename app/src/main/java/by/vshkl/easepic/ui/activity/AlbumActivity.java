package by.vshkl.easepic.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import by.vshkl.easepic.ui.listener.OnAlbumNameEditedListener;
import by.vshkl.easepic.ui.utils.DialogUtils;
import by.vshkl.easepic.ui.utils.ErrorUtils;
import by.vshkl.easepic.ui.utils.PreferenceUtils;
import by.vshkl.easepic.ui.view.MarqueeToolbar;

public class AlbumActivity extends MvpAppCompatActivity implements AlbumView, OnPictureClickListener,
        OnAlbumNameEditedListener {

    public static final String EXTRA_STORAGE_TYPE = "EXTRA_STORAGE_TYPE";
    public static final String EXTRA_ALBUM_ID = "EXTRA_ALBUM_ID";
    public static final String EXTRA_ALBUM_NAME = "EXTRA_ALBUM_NAME";

    @BindView(R.id.toolbar)
    MarqueeToolbar toolbar;
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

        Intent intent = getIntent();

        toolbar.setTitle(intent.getStringExtra(EXTRA_ALBUM_NAME));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemSortByDate = menu.findItem(R.id.action_sort_by_date);
        MenuItem itemSortByName = menu.findItem(R.id.action_sort_by_name);

        String sortOrder = PreferenceUtils.getSortOrder(AlbumActivity.this,
                getString(R.string.key_sort_order_album),
                getString(R.string.value_sort_order_by_date_album));
        if (sortOrder.equals(getString(R.string.value_sort_order_by_date_album))) {
            itemSortByDate.setIcon(R.drawable.ic_radio_button_checked);
            itemSortByName.setIcon(R.drawable.ic_radio_button_unchecked);
        } else if (sortOrder.equals(getString(R.string.value_sort_order_by_name_album))) {
            itemSortByDate.setIcon(R.drawable.ic_radio_button_unchecked);
            itemSortByName.setIcon(R.drawable.ic_radio_button_checked);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_sort_by_date:
                PreferenceUtils.storeSortOrder(AlbumActivity.this,
                        getString(R.string.key_sort_order_album),
                        getString(R.string.value_sort_order_by_date_album));
                invalidateOptionsMenu();
                sortAndSetPicturesList(albumAdapter.getPictureList());
                return true;
            case R.id.action_sort_by_name:
                PreferenceUtils.storeSortOrder(AlbumActivity.this,
                        getString(R.string.key_sort_order_album),
                        getString(R.string.value_sort_order_by_name_album));
                invalidateOptionsMenu();
                sortAndSetPicturesList(albumAdapter.getPictureList());
                return true;
            case R.id.action_rename:
                DialogUtils.showAlbumRenameDialog(AlbumActivity.this, getLayoutInflater(), AlbumActivity.this,
                        toolbar.getTitle().toString());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void showPictures(List<Picture> pictureList) {
        sortAndSetPicturesList(pictureList);
    }

    @Override
    public void showUpdatedAlbumName(String newAlbumName) {
        toolbar.setTitle(newAlbumName);
        setSupportActionBar(toolbar);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(ErrorUtils.Error error) {
        Toast.makeText(this, ErrorUtils.getMessageString(AlbumActivity.this, error), Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(AlbumActivity.this, PicturesPagerActivity.class);
        intent.putExtra(PicturesPagerActivity.EXTRA_POSITION, position);
        intent.putParcelableArrayListExtra(
                PicturesPagerActivity.EXTRA_PICTURE_LIST,
                (ArrayList<Picture>) albumAdapter.getPictureList());
        startActivity(intent);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void onAlbumNameEdited(String newName) {
        albumPresenter.setNewAlbumName(newName);
        albumPresenter.updateAlbumName();
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

    private void sortAndSetPicturesList(List<Picture> pictureList) {
        String sortOrder = PreferenceUtils.getSortOrder(AlbumActivity.this,
                getString(R.string.key_sort_order_album),
                getString(R.string.value_sort_order_by_date_album));
        if (sortOrder.equals(getString(R.string.value_sort_order_by_date_album))) {
            if (pictureList.size() > 0) {
                Collections.sort(pictureList, new Comparator<Picture>() {
                    @Override
                    public int compare(Picture object1, Picture object2) {
                        return object2.getDate().toUpperCase().compareTo(object1.getDate().toUpperCase());
                    }
                });
            }
        } else if (sortOrder.equals(getString(R.string.value_sort_order_by_name_album))) {
            if (pictureList.size() > 0) {
                Collections.sort(pictureList, new Comparator<Picture>() {
                    @Override
                    public int compare(Picture object1, Picture object2) {
                        return object1.getName().toUpperCase().compareTo(object2.getName().toUpperCase());
                    }
                });
            }
        }
        albumAdapter.setPictureList(pictureList);
        albumAdapter.notifyDataSetChanged();
    }

    private void setListeners() {
        albumAdapter.setOnPictureClickListener(AlbumActivity.this);
    }

    private void removeListeners() {
        albumAdapter.removeOnPictureClickListener();
    }
}
