package net.d53dev.dslfy.android.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import net.d53dev.dslfy.android.R;

/**
 * Created by davidsere on 11/11/15.
 */
public class GalleryAdapter extends BaseAdapter {
    private Context mContext;

    public GalleryAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return 10;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setAdjustViewBounds(true);
//            imageView.setLayoutParams(new GridView.LayoutParams();
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            int padding_in_dp = 2;  // 2 dps
            final float scale = mContext.getResources().getDisplayMetrics().density;
            int padpx = (int) (padding_in_dp * scale + 0.5f);
            imageView.setPadding(padpx, padpx, padpx, padpx);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(R.drawable.dog_selfie);
        return imageView;
    }

}
