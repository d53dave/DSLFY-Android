package net.d53dev.dslfy.android.ui.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.d53dev.dslfy.android.R;
import net.d53dev.dslfy.android.core.ImageUtils;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by davidsere on 14/11/15.
 */
public class ImageProcessingAdaptor extends RecyclerView.Adapter {

    private Context mContext;
    private ImageView bigImageView;
    private Bitmap bigImage;
    private Bitmap smallImage;
    private GPUImage gpuImage;
    private PhotoViewAttacher mAttacher;

    public ImageProcessingAdaptor(PhotoViewAttacher mAttacher, ImageView bigImageView, Bitmap image, Context context){

        this.mContext = context;
        this.bigImageView = bigImageView;
        this.bigImage = image;
        this.smallImage = ImageUtils.scaleBitmapAndKeepRatio(image, 250, 250);
        this.mAttacher = mAttacher;
        gpuImage = new GPUImage(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.grid_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        final ImageView imageView = viewHolder.imageView;
        final TextView textView = viewHolder.textView;

        final Filter filter = Filter.filterAt(position);
        textView.setText(filter.name());

        if(filter.imageFilter != null) {
            gpuImage.setFilter(filter.imageFilter);
            gpuImage.setImage(smallImage);

            imageView.setImageBitmap(gpuImage.getBitmapWithFilterApplied());
        } else {
            imageView.setImageBitmap(smallImage);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filter.imageFilter != null){
                    gpuImage.setImage(bigImage);
                    gpuImage.setFilter(filter.imageFilter);
                    bigImageView.setImageBitmap(gpuImage.getBitmapWithFilterApplied());
                } else {
                    bigImageView.setImageBitmap(bigImage);
                }
                mAttacher.update();

                Toast.makeText(mContext, filter.name() + " applied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return Filter.values().length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView imageView;
        final TextView textView;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.grid_element_image);
            textView = (TextView) v.findViewById(R.id.grid_element_text);
        }
    }


}
