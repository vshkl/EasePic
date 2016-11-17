package by.vshkl.easepic.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.piasy.biv.view.BigImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import by.vshkl.easepic.R;
import by.vshkl.easepic.mvp.model.Picture;

public class PictureFragment extends Fragment {

    private static final String ARG_PICTURE = "ARG_POSITION";

    @BindView(R.id.iv_picture)
    BigImageView ivPicture;

    private Unbinder unbinder;

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
}
