package net.d53dev.dslfy.android.ui.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.d53dev.dslfy.android.R;
import net.d53dev.dslfy.android.core.ImageUtils;
import net.d53dev.dslfy.android.ui.DSLFYActivity;
import net.d53dev.dslfy.android.ui.GalleryAdapter;
import net.d53dev.dslfy.android.ui.GalleryFragment;
import net.d53dev.dslfy.android.util.Strings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by davidsere on 14/11/15.
 */
public class CameraResultActivity extends DSLFYActivity {

    @Bind(R.id.image_processing_imgageview)
    protected ImageView imageView;
    @Bind(R.id.image_processing_scrollview_items)
    protected HorizontalGridView effectPreviewList;

    private ImageLoader mImageLoader;
    private String imagePath;
    private PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.image_processing);

        getSupportActionBar().setTitle(R.string.camera_result_bar_title);
//        getSupportActionBar().setc
//        getSupportActionBar().set


        mImageLoader = ImageLoader.getInstance();
        Bundle extras = getIntent().getExtras();
        imagePath = extras.getString(GalleryFragment.IMAGE_PATH_IDENTIFIER);

        if (Strings.notEmpty(imagePath)) {

            mImageLoader.loadImage(imagePath, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imageView.setImageBitmap(loadedImage);
                    mAttacher = new PhotoViewAttacher(imageView);
                    effectPreviewList.setAdapter(new ImageProcessingAdaptor(mAttacher, imageView, loadedImage, CameraResultActivity.this));
                }
            });

        }

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {

        return super.onCreateView(name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.camera_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_image:
                Toast.makeText(this, "Photo saved.", Toast.LENGTH_SHORT).show();

//                GridView g = ((GridView) findViewById(R.id.gallery_gridview));
//                ((BaseAdapter) g.getAdapter()).notifyDataSetChanged();
//                g.invalidateViews();

                try{
                    Uri fileUri = Uri.parse(imagePath);
                    File f = new File(fileUri.getPath());

                    boolean deleted = f.delete();
                    if(deleted){
                        f.createNewFile();
                    }

                    FileOutputStream fos = new FileOutputStream(f);
                    Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                    ImageUtils.scaleBitmapAndKeepRatio(bitmap, 1000, 1000);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (IOException ioe){
                    Toast.makeText(this, "Error: "+ioe.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                GalleryFragment.forceUpdate();

                finish();
                return true;

            case android.R.id.home:
            case R.id.action_discard_image:

                boolean deleted = new File(imagePath).delete();
                Toast.makeText(this, "Photo "
                        +(deleted? "was":"could not be")
                        +" discarded.", Toast.LENGTH_SHORT).show();
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
