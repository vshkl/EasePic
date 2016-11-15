package by.vshkl.easepic.ui.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.common.util.UriUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.vshkl.easepic.R;
import by.vshkl.easepic.mvp.model.Picture;
import by.vshkl.easepic.ui.view.SquareImageView;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.PictureViewHolder> {

    public interface OnPictureClickListener {
        void onPictureClicked(int position);
    }

    private List<Picture> pictureList = new ArrayList<>();
    private OnPictureClickListener onPictureClickListener;

    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture, parent, false);
        return new PictureViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PictureViewHolder holder, int position) {
        final int currentPosition = position;

        Picasso.with(holder.itemView.getContext())
                .load(Uri.fromFile(new File(pictureList.get(position).getPath())))
                .resize(240, 240)
                .onlyScaleDown()
                .centerCrop()
                .into(holder.ivThumbnail);
        holder.ivThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPictureClickListener != null) {
                    onPictureClickListener.onPictureClicked(currentPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (pictureList != null) ? pictureList.size() : 0;
    }

    public void setPictureList(List<Picture> pictureList) {
        this.pictureList = pictureList;
    }

    public void setOnPictureClickListener(OnPictureClickListener onPictureClickListener) {
        this.onPictureClickListener = onPictureClickListener;
    }

    public List<String> getPicturesPaths() {
        List<String> picturesPaths = new ArrayList<>();
        for (Picture picture : pictureList) {
            Uri uri = new Uri.Builder()
                    .scheme(UriUtil.LOCAL_FILE_SCHEME)
                    .path(picture.getPath())
                    .build();
            picturesPaths.add(uri.toString());
        }
        return picturesPaths;
    }

    class PictureViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_thumbnail)
        SquareImageView ivThumbnail;

        PictureViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(PictureViewHolder.this, itemView);
        }
    }
}
