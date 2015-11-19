package net.d53dev.dslfy.android.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.d53dev.dslfy.android.R;
import net.d53dev.dslfy.android.util.Strings;

import butterknife.Bind;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by davidsere on 19/11/15.
 */
public class ViewSelfieActivity extends DSLFYActivity {

    //
    @Bind(R.id.selfie_view_image_view)
    protected ImageView imageView;

    private ImageLoader mImageLoader;
    private String imagePath;
    private PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.selfie_view);

        mImageLoader = ImageLoader.getInstance();
        Bundle extras = getIntent().getExtras();
        imagePath = extras.getString(GalleryFragment.IMAGE_PATH_IDENTIFIER);

        if (Strings.notEmpty(imagePath)) {
            mImageLoader.loadImage(imagePath, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    imageView.setImageBitmap(loadedImage);
                    mAttacher = new PhotoViewAttacher(imageView);
                }
            });
        }
    }
}
