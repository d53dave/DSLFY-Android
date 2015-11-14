package net.d53dev.dslfy.android.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.squareup.picasso.Picasso;

import net.d53dev.dslfy.android.R;
import net.d53dev.dslfy.android.util.Strings;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by davidsere on 14/11/15.
 */
public class CameraResultActivity extends DSLFYActivity {

    @Bind(R.id.image_processing_imgageview) protected ImageView imageView;
    @Bind(R.id.image_processing_scrollview_items) protected HorizontalGridView effectPreviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.image_processing);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Bundle extras = getIntent().getExtras();
        String imagePath = extras.getString(GalleryFragment.IMAGE_PATH_IDENTIFIER);


        if(Strings.notEmpty(imagePath)){
            this.effectPreviewList.setAdapter(new ImageProcessingAdaptor(imagePath));
            Picasso.with(this).load(imagePath).into(this.imageView);
        }




    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {

        return super.onCreateView(name, context, attrs);
    }

    //    private static final String ARG_BITMAP="bitmap";
//    private static final String ARG_URI="uri";
//
//    static CameraResultActivity newInstance() {
//        CameraResultActivity fragment = new CameraResultActivity();
//
//        return(fragment);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(String name, Context context, AttributeSet attrs) {
//        return(new SubsamplingScaleImageView(this));
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        getSSSIV().setOrientation(SubsamplingScaleImageView.ORIENTATION_0);
//
//        Bitmap bitmap=getArguments().getParcelable(ARG_BITMAP);
//
//        if (bitmap == null) {
//            Uri uri=getArguments().getParcelable(ARG_URI);
//
//            if (uri != null) {
//                setImage(uri);
//            }
//        }
//        else {
//            setImage(bitmap);
//        }
//    }
//
//    void setImage(Bitmap bitmap) {
//        getArguments().putParcelable(ARG_BITMAP, bitmap);
//        getArguments().remove(ARG_URI);
//
//        if (getView()!=null) {
//            getSSSIV().setImage(ImageSource.bitmap(bitmap));
//        }
//    }
//
//    void setImage(Uri uri) {
//        getArguments().putParcelable(ARG_URI, uri);
//        getArguments().remove(ARG_BITMAP);
//
//        if (getView()!=null) {
//            getSSSIV().setImage(ImageSource.uri(uri));
//        }
//    }
//
//    private SubsamplingScaleImageView getSSSIV() {
//        return((SubsamplingScaleImageView)getView());
//    }
}
