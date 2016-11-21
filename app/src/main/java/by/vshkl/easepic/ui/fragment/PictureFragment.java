package by.vshkl.easepic.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.github.piasy.biv.view.BigImageView;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import by.vshkl.easepic.R;
import by.vshkl.easepic.mvp.model.Picture;
import by.vshkl.easepic.ui.activity.PicturesPagerActivity;

public class PictureFragment extends Fragment {

    private static final String ARG_PICTURE = "ARG_PICTURE";

    @BindView(R.id.iv_picture)
    BigImageView ivPicture;

    private WeakReference<PicturesPagerActivity> activityRef;
    private Unbinder unbinder;
    private boolean isShown = true;

    public static PictureFragment newInstance(Picture picture) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_PICTURE, picture);

        PictureFragment fragment = new PictureFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PicturesPagerActivity) {
            activityRef = new WeakReference<>((PicturesPagerActivity) context);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        unbinder = ButterKnife.bind(PictureFragment.this, view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Picture picture = bundle.getParcelable(ARG_PICTURE);
            ivPicture.showImage(new Uri.Builder().scheme("file").path(picture.getPath()).build());
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (activityRef != null) {
            activityRef.clear();
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @OnClick(R.id.iv_picture)
    void onClick() {
        if (isShown) {
            hideUi();
            isShown = false;
        } else {
            showUi();
            isShown = true;
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private void hideUi() {
        activityRef.get().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        activityRef.get().getToolbar()
                .animate()
                .translationY(-dpToPx(80))
                .setInterpolator(new AccelerateInterpolator());
    }

    private void showUi() {
        activityRef.get().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        activityRef.get().getToolbar()
                .animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator());
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
