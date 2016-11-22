package by.vshkl.easepic.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;

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
import by.vshkl.easepic.ui.utils.DialogUtils;
import by.vshkl.easepic.ui.view.MarqueeTextView;
import by.vshkl.easepic.ui.view.SwipeBackLayout;

public class PicturesPagerActivity extends MvpSwipeBackActivity implements PicturesPagerView, OnPageChangeListener {

    public static final String EXTRA_POSITION = "EXTRA_POSITION";
    public static final String EXTRA_PICTURE_LIST = "EXTRA_PICTURE_LIST";

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
        vpPictures.addOnPageChangeListener(PicturesPagerActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        vpPictures.removeOnPageChangeListener(PicturesPagerActivity.this);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

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

    //------------------------------------------------------------------------------------------------------------------

    public Toolbar getToolbar() {
        return toolbar;
    }

    private void handleOpenImage(Uri pictureUri) {
        String path = pictureUri.getPath();
        picturesPagerPresenter.setPicturesRootPath(path.substring(0, path.lastIndexOf("/")));
        picturesPagerPresenter.setPictureFullPath(path);
        picturesPagerPresenter.getPictures(PicturesPagerActivity.this);
    }

    private void handleShareAction() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, new Uri.Builder().scheme("file").appendPath(
                adapter.getPicturePath(vpPictures.getCurrentItem())).build());
        startActivity(Intent.createChooser(intent, getString(R.string.chooser_share_title)));
    }

    private void handleViewDetailsAction() {
        picturesPagerPresenter.setPictureId(adapter.getPictureId(vpPictures.getCurrentItem()));
        picturesPagerPresenter.setPictureFullPath(adapter.getPicturePath(vpPictures.getCurrentItem()));
        picturesPagerPresenter.getPictureInfo(PicturesPagerActivity.this);
    }
}
