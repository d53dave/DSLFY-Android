package net.d53dev.dslfy.android.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.d53dev.dslfy.android.R;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by davidsere on 11/11/15.
 */
public class GalleryAdapter extends BaseAdapter {
    private Context mContext;
    private ImageLoader mImageLoader;

    public GalleryAdapter(Context c) {
        mContext = c;
        mImageLoader = ImageLoader.getInstance();
    }

    protected File[] sortedFiles;
    protected int oldLength;

    public int getCount() {
        File externalStorage = mContext.getExternalFilesDir(null);
        File[] jpegs =  externalStorage.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file != null && file.getName().endsWith(".jpeg");
            }
        });

        if ( oldLength != jpegs.length ){
            sortedFiles = null;
        }
        oldLength = jpegs.length;
        return jpegs.length;
    }

    public Object getItem(int position) {
        try {
            return getUriForPostion(position);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setAdjustViewBounds(true);

            int padding_in_dp = 1;  // 2 dps
            final float scale = mContext.getResources().getDisplayMetrics().density;
            int padpx = (int) (padding_in_dp * scale + 0.5f);
            imageView.setPadding(padpx, padpx, padpx, padpx);
        } else {
            imageView = (ImageView) convertView;
        }

        try{
            String path = this.getUriForPostion(position);
//            mImageLoader.displayImage(path, imageView);
            mImageLoader.loadImage(path, new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage){
                    imageView.setImageBitmap(loadedImage);
                }
            });
        } catch (IOException ioe){
            imageView.setImageDrawable(mContext.getResources().getDrawable(
                    R.drawable.com_facebook_profile_picture_blank_portrait));
        }

        return imageView;
    }

    private String getUriForPostion(int position) throws IOException{
        if(sortedFiles != null){
            return Uri.fromFile(sortedFiles[position].getAbsoluteFile()).toString();
        }

        File externalStorage = mContext.getExternalFilesDir(null);
        File[] files = externalStorage.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file != null && file.getName().endsWith(".jpeg");
            }
        });

        Arrays.sort(files, new Comparator<File>()
        {
            public int compare(File o1, File o2) {
               return Long.valueOf(o2.lastModified()).compareTo(Long.valueOf(o1.lastModified()));
            }
        });

        sortedFiles = files;

        File selectedFile = files[position];
        return Uri.fromFile(selectedFile.getAbsoluteFile()).toString();
    }

}
