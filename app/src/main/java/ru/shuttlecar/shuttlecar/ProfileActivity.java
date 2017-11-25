package ru.shuttlecar.shuttlecar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(this);
        toolbar.inflateMenu(R.menu.toolbar_profile_menu);
        setTitle(R.string.title_profile);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame_profile, new ProfileFragment(), null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_profile_menu_changeprofile:
                toolbar.getMenu().clear();
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.content_frame_profile, new ChangeProfileFragment(), null)
                        .commit();
                break;
            case R.id.toolbar_profile_menu_exit:
                setResult(RESULT_OK);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        invalidateOptionsMenu();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }
}