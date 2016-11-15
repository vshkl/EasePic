package by.vshkl.easepic.ui.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.vshkl.easepic.R;
import by.vshkl.easepic.mvp.model.Album;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.LibraryViewHolder> {

    private List<Album> albumList = new ArrayList<>();

    @Override
    public LibraryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new LibraryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LibraryViewHolder holder, int position) {
        Album album = albumList.get(position);

        holder.tvName.setText(album.getBucketName());

        Picasso.with(holder.itemView.getContext())
                .load(Uri.fromFile(new File(album.getBucketThumbnail())))
                .resize(320, 320)
                .onlyScaleDown()
                .centerCrop()
                .into(holder.ivThumbnail);
    }

    @Override
    public int getItemCount() {
        return (albumList != null) ? albumList.size() : 0;
    }

    public void setAlbumList(List<Album> albumList) {
        this.albumList = albumList;
    }

    class LibraryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_thumbnail)
        ImageView ivThumbnail;
        @BindView(R.id.tv_name)
        TextView tvName;

        LibraryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(LibraryViewHolder.this, itemView);
        }
    }
}
