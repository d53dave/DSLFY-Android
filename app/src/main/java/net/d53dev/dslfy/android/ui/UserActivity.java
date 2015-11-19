package net.d53dev.dslfy.android.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import net.d53dev.dslfy.android.R;
import net.d53dev.dslfy.android.model.User;
import com.squareup.picasso.Picasso;

import butterknife.Bind;


public class UserActivity extends DSLFYActivity {

    @Bind(R.id.iv_avatar) protected ImageView avatar;
    @Bind(R.id.tv_name) protected TextView name;

    private User user;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_view);

        if (getIntent() != null && getIntent().getExtras() != null) {
            user = (User) getIntent().getExtras().getSerializable("dslfyuser");
        }

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


}
