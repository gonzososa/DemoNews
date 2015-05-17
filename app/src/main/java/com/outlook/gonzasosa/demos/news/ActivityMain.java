package com.outlook.gonzasosa.demos.news;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Punto de entrada de la aplicación
 */
public class ActivityMain extends AppCompatActivity {
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.main_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled (false);
            getSupportActionBar().setDisplayHomeAsUpEnabled (false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate (R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId ()) {
            case R.id.menu_picoftheday:
                loadImageOfDay ();
                return true;
            case R.id.menu_breakingnews:
                loadBreakingNews ();
                return true;
            case R.id.menu_about:
                loadAboutWindow ();
                return true;
            default:
                return super.onOptionsItemSelected (item);
        }
    }

    private void loadImageOfDay () {
        Intent intent = new Intent (getBaseContext(), ActivityImageOfDay.class);
        startActivity (intent);
    }

    private void loadAboutWindow () {
        Intent intent = new Intent (getBaseContext(), ActivityAbout.class);
        startActivity(intent);
    }

    private void loadBreakingNews () {
        Intent intent = new Intent (getBaseContext(), ActivityBreakingNews.class);
        startActivity (intent);
    }
}
