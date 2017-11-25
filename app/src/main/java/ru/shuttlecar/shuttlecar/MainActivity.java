package ru.shuttlecar.shuttlecar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import me.majiajie.pagerbottomtabstrip.Controller;
import me.majiajie.pagerbottomtabstrip.PagerBottomTabLayout;
import me.majiajie.pagerbottomtabstrip.TabLayoutMode;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectListener;
import ru.shuttlecar.shuttlecar.models.Constants;
import ru.shuttlecar.shuttlecar.models.State_logged_in;

public class MainActivity extends AppCompatActivity implements OnTabItemSelectListener, State_logged_in {

    Toolbar toolbar;
    MenuItem menuItem_profile, menuItem_settings;
    PagerBottomTabLayout bottomTabLayout;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        bottomTabLayout = (PagerBottomTabLayout) findViewById(R.id.nav_bar);
        Controller controller = bottomTabLayout.builder()
                .setMode(TabLayoutMode.HIDE_TEXT)
                .addTabItem(R.drawable.ic_search, getString(R.string.nav_bar_find))
                .addTabItem(R.drawable.ic_add, getString(R.string.nav_bar_add))
                .addTabItem(R.drawable.ic_car, getString(R.string.nav_bar_my))
                .addTabItem(R.drawable.ic_reserve, getString(R.string.nav_bar_reserve))
                .addTabItem(R.drawable.ic_star, getString(R.string.nav_bar_rate))
                .build();
        controller.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
        controller.addTabItemClickListener(this);


        pref = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);


        if (pref.getBoolean(Constants.IS_LOGGED_IN, false)) {

            if (pref.getString(Constants.TELEPHONE, "").isEmpty()) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, new FindOrderFragment(), FindOrderFragment.class.toString())
                        .commit();
                controller.setSelect(0);
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, new AddOrderFragment(), AddOrderFragment.class.toString())
                        .commit();
                controller.setSelect(1);
            }

        } else {
            setState(false);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new LoginFragment(), LoginFragment.class.toString())
                    .commit();
        }
    }

    @Override
    public void onSelected(int index, Object tag) {
        Fragment fragment = null;
        switch (index) {
            case 0:
                fragment = new FindOrderFragment();
                break;
            case 1:
                fragment = new AddOrderFragment();
                break;
            case 2:
                fragment = new MyOrderFragment();
                break;
            case 3:
                fragment = new ReserveOrderFragment();
                break;
            case 4:
                fragment = new ChangeRatingFragment();
                break;
        }
        if (fragment != null) {
            FragmentManager fm = getSupportFragmentManager();

            Fragment currentFragment = fm.findFragmentById(R.id.content_frame);

            if (!fragment.getClass().toString().equals(currentFragment.getTag())) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.content_frame, fragment, fragment.getClass().toString());
                ft.commit();
            }
        }
    }

    @Override
    public void onRepeatClick(int index, Object tag) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (pref.getBoolean(Constants.IS_LOGGED_IN, false)) {
            getMenuInflater().inflate(R.menu.toolbar_main_menu, menu);
            menuItem_profile = menu.getItem(0);
            menuItem_settings = menu.getItem(1);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Handler handler = new Handler();
        Intent intent;
        switch (item.getItemId()) {
            case R.id.toolbar_main_menu_profile:
                menuItem_profile.setEnabled(false);
                menuItem_settings.setEnabled(false);

                intent = new Intent(this, ProfileActivity.class);
                startActivityForResult(intent, Constants.IS_PROCESS_EXIT_PROFILE);

                handler.postDelayed(new Runnable() {
                    public void run() {
                        menuItem_profile.setEnabled(true);
                        menuItem_settings.setEnabled(true);
                    }
                }, 100);

                break;
            case R.id.toolbar_main_menu_settings:
                menuItem_profile.setEnabled(false);
                menuItem_settings.setEnabled(false);

                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);

                handler.postDelayed(new Runnable() {
                    public void run() {
                        menuItem_profile.setEnabled(true);
                        menuItem_settings.setEnabled(true);
                    }
                }, 100);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IS_PROCESS_EXIT_PROFILE && resultCode == RESULT_OK) {
            pref.edit().clear().apply();
            finish();
        }
    }

    @Override
    public void setState(boolean state) {
        if (state) {
            invalidateOptionsMenu();
            setTitle(R.string.app_name);
            bottomTabLayout.setVisibility(View.VISIBLE);
        } else {
            bottomTabLayout.setVisibility(View.GONE);
            setTitle(R.string.empty);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment currentFragment = fm.findFragmentById(R.id.content_frame);
        if (FoundOrderFragment.class.toString().equals(currentFragment.getTag())) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new FindOrderFragment(), FindOrderFragment.class.toString())
                    .commit();
        } else {
            super.onBackPressed();
        }
    }
}
