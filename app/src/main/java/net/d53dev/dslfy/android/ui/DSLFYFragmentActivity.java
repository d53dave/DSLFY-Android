package net.d53dev.dslfy.android.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import net.d53dev.dslfy.android.Injector;
import net.d53dev.dslfy.android.util.Ln;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;


/**
 * Base class for all Bootstrap Activities that need fragments.
 */
public class DSLFYFragmentActivity extends ActionBarActivity {

    @Inject
    protected Bus eventBus;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);
    }

    @Override
    public void setContentView(final int layoutResId) {
        super.setContentView(layoutResId);

        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }

}
