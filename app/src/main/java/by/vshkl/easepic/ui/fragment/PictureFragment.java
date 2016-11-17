package by.vshkl.easepic.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private static final String ARG_PICTURE = "ARG_POSITION";

    @BindView(R.id.iv_picture)
    BigImageView ivPicture;

    private Unbinder unbinder;
    private boolean isActionBarHidden = false;
    private boolean isToolbarHidden = false;

    public static PictureFragment newInstance(Picture picture) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_PICTURE, picture);

        PictureFragment fragment = new PictureFragment();
        fragment.setArguments(arguments);

        return fragment;
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

    @OnClick(R.id.iv_picture)
    void onClick() {
        WeakReference<PicturesPagerActivity> activity = new WeakReference<>((PicturesPagerActivity) getActivity());
        if (activity.get().getSupportActionBar().isShowing()) {
            activity.get().getSupportActionBar().hide();
        } else {
            activity.get().getSupportActionBar().show();
        }
        activity.clear();
    }
}
