package com.outlook.gonzasosa.demos.news;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityMain extends AppCompatActivity {
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled (false);
        }

    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId ()) {
            case R.id.menu_picoftheday:
                return true;
            case R.id.menu_breakingnews:
                return true;
            case R.id.menu_about:
                loadAboutWindow ();
                return true;
            default:
                return super.onOptionsItemSelected (item);
        }
    }

    private void loadAboutWindow () {
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FragmentAbout about = new FragmentAbout ();
        transaction.add (R.id.dummy, about);
        transaction.setTransition (FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit ();
    }

    private void loadBreakingNew () {
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FragmentBreakingNews news = new FragmentBreakingNews ();
        transaction.add (R.id.dummy, news);
        transaction.setTransition (FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit ();
    }
}
