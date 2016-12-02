package by.vshkl.easepic.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.vshkl.easepic.R;
import by.vshkl.easepic.mvp.model.Album;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumsViewHolder> {

    public interface OnAlbumClickListener {
        void onAlbumClicked(Album.StorageType storageType, String albumId, String albumName);
    }

    private List<Album> albumList;
    private WeakReference<OnAlbumClickListener> onAlbumClickListener;
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
                .placeholder(R.color.colorSemiTransparent)
                .override(itemDimension, itemDimension)
                .centerCrop()
                .crossFade()
                .into(holder.ivThumbnail);

        holder.ivThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onAlbumClickListener.get() != null) {
                    onAlbumClickListener.get().onAlbumClicked(
                            album.getBucketStorageType(), album.getBucketId(), album.getBucketName());
                }
            }
        });
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (onAlbumClickListener != null) {
            onAlbumClickListener.clear();
            onAlbumClickListener = null;
        }
    }

    @Override
    public int getItemCount() {
        return (albumList != null) ? albumList.size() : 0;
    }

    public void setAlbumList(List<Album> albumList) {
        this.albumList = new ArrayList<>(albumList.size());
        this.albumList = albumList;
    }

    public List<Album> getAlbumList() {
        return albumList;
    }

    public void setOnAlbumClickListener(OnAlbumClickListener onAlbumClickListener) {
        this.onAlbumClickListener = new WeakReference<>(onAlbumClickListener);
    }

    public void removeOnAlbumClickListener() {
        this.onAlbumClickListener = null;
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
