package net.d53dev.dslfy.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import net.d53dev.dslfy.android.DSLFY;
import net.d53dev.dslfy.android.R;
import net.d53dev.dslfy.android.util.Strings;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.commonsware.cwac.cam2.AbstractCameraActivity;
import com.commonsware.cwac.cam2.CameraActivity;


/**
 * Created by davidsere on 11/11/15.
 */
public class GalleryFragment extends Fragment {

    protected static final String IMAGE_PATH_IDENTIFIER = "dslfyCapturedImagePath";

    interface Contract {
        void takePicture(Intent i);
    }

    private static View view;

    private List<Integer> selectedViews = new ArrayList<>();

    @Bind(R.id.gallery_gridview) protected GridView imageGrid;
    @Bind(R.id.fab_gallery) protected FloatingActionButton floatingActionButton;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.gallery, null);
        }
        ButterKnife.bind(this, view);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        imageGrid.setAdapter(new GalleryAdapter(this.getActivity()));

        imageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(GalleryFragment.this.getActivity(), "tap " + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        imageGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
//                view.setBackgroundColor(Color.CYAN);
                if (selectedViews.contains(position)) {
                    view.setBackgroundColor(Color.WHITE);
//                    view.setAlpha(1f);
                    ((ImageView) view).clearColorFilter();
                    selectedViews.remove(Integer.valueOf(position));
                } else {
                    view.setBackgroundColor(Color.GRAY);
//                    view.setAlpha(0.7f);
                    ((ImageView) view).setColorFilter(Color.CYAN, PorterDuff.Mode.DARKEN);
                    selectedViews.add(position);
                }

                Toast.makeText(GalleryFragment.this.getActivity(), "long pos " + position+ " id "+id,
                        Toast.LENGTH_SHORT).show();

                return true;
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Contract)) {
            throw new IllegalStateException("Hosting activity does not implement Contract interface!");
        }
    }

    private void takePicture() {
        SharedPreferences prefs = this.getActivity()
                .getSharedPreferences(DSLFY.PREFNAME, Context.MODE_PRIVATE);
        CameraActivity.IntentBuilder b=new CameraActivity.IntentBuilder(getActivity());

        if (!prefs.getBoolean("confirm", false)) {
            b.skipConfirm();
        }

        if (prefs.getBoolean("ffc", true)) {
            b.facing(AbstractCameraActivity.Facing.FRONT);
        }
        else {
            b.facing(AbstractCameraActivity.Facing.BACK);
        }

        if (prefs.getBoolean("exact_match", false)) {
            b.facingExactMatch();
        }

        if (prefs.getBoolean("debug", false)) {
            b.debug();
        }

        if (prefs.getBoolean("updateMediaStore", false)) {
            b.updateMediaStore();
        }

        if (prefs.getBoolean("forceClassic", false)) {
            b.forceClassic();
        }

        if (prefs.getBoolean("file", true)) {

            Calendar c = GregorianCalendar.getInstance();
            String filename = "dslfy_"+c.getTimeInMillis()+".jpeg";
            b.to(new File(getActivity().getExternalFilesDir(null), filename));
        }

        if (prefs.getBoolean("mirrorPreview", false)) {
            b.mirrorPreview();
        }

        int rawFocusMode=Integer.valueOf(
                prefs.getString("focusMode", "0"));

        switch (rawFocusMode) {
            case 0:
                b.focusMode(AbstractCameraActivity.FocusMode.CONTINUOUS);
                break;
            case 1:
                b.focusMode(AbstractCameraActivity.FocusMode.OFF);
                break;
            case 2:
                b.focusMode(AbstractCameraActivity.FocusMode.EDOF);
                break;
        }

        ((Contract)getActivity()).takePicture(b.build());
    }
}
