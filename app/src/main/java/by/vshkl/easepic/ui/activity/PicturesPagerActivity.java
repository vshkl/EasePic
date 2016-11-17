package by.vshkl.easepic.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.arellomobile.mvp.MvpAppCompatActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.vshkl.easepic.R;
import by.vshkl.easepic.mvp.model.Picture;
import by.vshkl.easepic.ui.adapter.PicturesPagerAdapter;
import by.vshkl.easepic.ui.common.DepthPageTransformer;

public class PicturesPagerActivity extends MvpAppCompatActivity {

    public static final String EXTRA_POSITION = "EXTRA_POSITION";
    public static final String EXTRA_PICTURE_LIST = "EXTRA_PICTURE_LIST";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.vp_pictures)
    ViewPager vpPictures;

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
            int position = intent.getIntExtra(EXTRA_POSITION, -1);
            List<Picture> pictureList = intent.getParcelableArrayListExtra(EXTRA_PICTURE_LIST);

            PicturesPagerAdapter adapter = new PicturesPagerAdapter(getSupportFragmentManager());
            adapter.setPictureList(pictureList);

            vpPictures.setAdapter(adapter);
            vpPictures.setCurrentItem(position);
            vpPictures.setPageTransformer(true, new DepthPageTransformer());
        }
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
}
