package by.vshkl.easepic.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.vshkl.easepic.R;
import by.vshkl.easepic.mvp.model.Picture;
import by.vshkl.easepic.mvp.model.PictureInfo;
import by.vshkl.easepic.mvp.presenter.PicturesPagerPresenter;
import by.vshkl.easepic.mvp.view.PicturesPagerView;
import by.vshkl.easepic.ui.adapter.PicturesPagerAdapter;
import by.vshkl.easepic.ui.common.DepthPageTransformer;
import by.vshkl.easepic.ui.listener.OnDeleteConfirmedListener;
import by.vshkl.easepic.ui.utils.DialogUtils;
import by.vshkl.easepic.ui.utils.ErrorUtils;
import by.vshkl.easepic.ui.view.MarqueeTextView;
import by.vshkl.easepic.ui.view.SwipeBackLayout;

public class PicturesPagerActivity extends MvpSwipeBackActivity implements PicturesPagerView, OnPageChangeListener,
        OnDeleteConfirmedListener {

    public static final String EXTRA_POSITION = "EXTRA_POSITION";
    public static final String EXTRA_PICTURE_LIST = "EXTRA_PICTURE_LIST";

    public static final int REQUEST_CODE = 43;

    @BindView(R.id.root)
    View rootView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_toolbar)
    MarqueeTextView tvToolbar;
    @BindView(R.id.vp_pictures)
    ViewPager vpPictures;
    @BindView(R.id.swipe_back_layout)
    SwipeBackLayout swipeBackLayout;

    @InjectPresenter
    PicturesPagerPresenter picturesPagerPresenter;

    private PicturesPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_pager);
        ButterKnife.bind(PicturesPagerActivity.this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();
        if (intent != null) {
            if (Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getType().startsWith("image/")) {
                handleOpenImage(intent.getData());
            } else {
                int position = intent.getIntExtra(EXTRA_POSITION, -1);
                List<Picture> pictureList = intent.getParcelableArrayListExtra(EXTRA_PICTURE_LIST);

                adapter = new PicturesPagerAdapter(getSupportFragmentManager());
                adapter.setPictureList(pictureList);

                vpPictures.setAdapter(adapter);
                vpPictures.setCurrentItem(position);
                vpPictures.setPageTransformer(true, new DepthPageTransformer());
            }
        }

        setDragEdge(SwipeBackLayout.DragEdge.TOP);
        swipeBackLayout.setScrollChild(vpPictures);
    }

    @Override
    protected void onStart() {
        super.onStart();
        picturesPagerPresenter.onStart(PicturesPagerActivity.this);
        vpPictures.addOnPageChangeListener(PicturesPagerActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        vpPictures.removeOnPageChangeListener(PicturesPagerActivity.this);
        picturesPagerPresenter.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_share:
                handleShareAction();
                return true;
            case R.id.action_details:
                handleViewDetailsAction();
                return true;
            case R.id.action_delete:
                DialogUtils.showDeleteConfirmationDialog(PicturesPagerActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void showMessage(final String message) {
        Toast.makeText(PicturesPagerActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(ErrorUtils.Error error) {
        Toast.makeText(this, ErrorUtils.getMessageString(PicturesPagerActivity.this, error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showPictures(final List<Picture> pictureList, final int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new PicturesPagerAdapter(getSupportFragmentManager());
                adapter.setPictureList(pictureList);

                vpPictures.setAdapter(adapter);
                vpPictures.setCurrentItem(position);
                vpPictures.setPageTransformer(true, new DepthPageTransformer());
            }
        });
    }

    @Override
    public void showPictureInfo(PictureInfo pictureInfo) {
        DialogUtils.showPictureDetailsDialog(PicturesPagerActivity.this, pictureInfo);
    }

    @Override
    public void onDeleted(final String pictureId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
            }
        });
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        tvToolbar.setText(adapter.getPictureName(position));
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onDeleteConfirmed() {
        handleDeleteAction();
    }

    //------------------------------------------------------------------------------------------------------------------

    public Toolbar getToolbar() {
        return toolbar;
    }

    private void handleOpenImage(Uri pictureUri) {
        String path = pictureUri.getPath();
        picturesPagerPresenter.onStart(PicturesPagerActivity.this);
        picturesPagerPresenter.setPicturesRootPath(path.substring(0, path.lastIndexOf("/")));
        picturesPagerPresenter.setPictureFullPath(path);
        picturesPagerPresenter.getPictures();
    }

    private void handleShareAction() {
        File file = new File(adapter.getPicturePath(vpPictures.getCurrentItem()));
        Uri uri = FileProvider.getUriForFile(PicturesPagerActivity.this, "by.vshkl.fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(intent, getString(R.string.chooser_share_title)));
    }

    private void handleViewDetailsAction() {
        picturesPagerPresenter.setPictureId(adapter.getPictureId(vpPictures.getCurrentItem()));
        picturesPagerPresenter.setPictureFullPath(adapter.getPicturePath(vpPictures.getCurrentItem()));
        picturesPagerPresenter.getPictureInfo();
    }

    private void handleDeleteAction() {
        picturesPagerPresenter.setPictureId(adapter.getPictureId(vpPictures.getCurrentItem()));
        picturesPagerPresenter.setPictureFullPath(adapter.getPicturePath(vpPictures.getCurrentItem()));
        picturesPagerPresenter.deletePicture();
    }
}
