package net.d53dev.dslfy.android.ui;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by davidsere on 14/11/15.
 */
public class ImageProcessingAdaptor extends RecyclerView.Adapter {

    private String imagePath;

    public ImageProcessingAdaptor(String imagePath){
        this.imagePath = imagePath;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        return new ViewHolder(parent.getContext(), imageView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        ImageView imageView = (ImageView) holder.itemView;
        Picasso.with(viewHolder.mContext).load(imagePath).into(imageView);
    }

    @Override
    public int getItemCount() {
        return 15;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Context mContext;
        public ViewHolder(Context c, View v) {
            super(v);
            mContext = c;
        }
    }
}
