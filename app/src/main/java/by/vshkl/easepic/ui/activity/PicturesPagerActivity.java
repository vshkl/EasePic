package by.vshkl.easepic.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MenuItem;
import android.view.View;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.vshkl.easepic.R;
import by.vshkl.easepic.mvp.model.Picture;
import by.vshkl.easepic.ui.adapter.PicturesPagerAdapter;
import by.vshkl.easepic.ui.common.DepthPageTransformer;
import by.vshkl.easepic.ui.view.MarqueeToolbar;
import by.vshkl.easepic.ui.view.SwipeBackLayout;

public class PicturesPagerActivity extends MvpSwipeBackActivity implements OnPageChangeListener {

    public static final String EXTRA_POSITION = "EXTRA_POSITION";
    public static final String EXTRA_PICTURE_LIST = "EXTRA_PICTURE_LIST";

    @BindView(R.id.root)
    View rootView;
    @BindView(R.id.toolbar)
    MarqueeToolbar toolbar;
    @BindView(R.id.vp_pictures)
    ViewPager vpPictures;
    @BindView(R.id.swipe_back_layout)
    SwipeBackLayout swipeBackLayout;

    PicturesPagerAdapter adapter;

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        toolbar.setTitle(adapter.getPictureName(position));
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //------------------------------------------------------------------------------------------------------------------

    public MarqueeToolbar getToolbar() {
        return toolbar;
    }

    private void handleOpenImage(Uri pictureUri) {
        Picture picture = new Picture();
        picture.setPath(pictureUri.getPath());
        picture.setName(pictureUri.toString().substring(pictureUri.toString().lastIndexOf("/") + 1));

        adapter = new PicturesPagerAdapter(getSupportFragmentManager());
        adapter.setPictureList(Collections.singletonList(picture));

        vpPictures.setAdapter(adapter);
        vpPictures.setCurrentItem(0);
    }
}
