package by.vshkl.easepic.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.vshkl.easepic.R;
import by.vshkl.easepic.mvp.model.Album;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumsViewHolder> {

    public interface OnAlbumClickListener {
        void onAlbumClicked(Album.StorageType storageType, String albumId);
    }

    private List<Album> albumList = new ArrayList<>();
    private OnAlbumClickListener onAlbumClickListener;
    private int itemDimension;

    public AlbumsAdapter(int itemDimension) {
        this.itemDimension = itemDimension;
    }

    @Override
    public AlbumsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new AlbumsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AlbumsViewHolder holder, int position) {
        final Album album = albumList.get(position);

        holder.tvName.setText(album.getBucketName());

        Glide.with(holder.itemView.getContext())
                .load(album.getBucketThumbnail())
                .placeholder(R.drawable.ic_image_placeholder)
                .override(itemDimension, itemDimension)
                .centerCrop()
                .crossFade()
                .into(holder.ivThumbnail);

        holder.ivThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onAlbumClickListener != null) {
                    onAlbumClickListener.onAlbumClicked(album.getBucketStorageType(), album.getBucketId());
                }
            }
        });
    }

    @Override
    public void onViewRecycled(AlbumsViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return (albumList != null) ? albumList.size() : 0;
    }

    public void setAlbumList(List<Album> albumList) {
        this.albumList = albumList;
    }

    public void setOnAlbumClickListener(OnAlbumClickListener onAlbumClickListener) {
        this.onAlbumClickListener = onAlbumClickListener;
    }

    private static Bitmap decode(String path, int rW, int rH) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);

        final int iH = options.outHeight;
        final int iW = options.outWidth;
        int inSampleSize = 1;

        if (iH > rH || iW > rW) {
            final int hH = iH / 2;
            final int hW = iW / 2;

            while ((hH / inSampleSize) >= rH && (hW / inSampleSize) >= rW) {
                inSampleSize *= 2;
            }
        }

        options.inSampleSize = inSampleSize;

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    class AlbumsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_thumbnail)
        ImageView ivThumbnail;
        @BindView(R.id.tv_name)
        TextView tvName;

        AlbumsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(AlbumsViewHolder.this, itemView);
        }
    }
}
