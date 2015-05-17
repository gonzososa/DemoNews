package com.outlook.gonzasosa.demos.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Esta clase maneja la actividad correspondiente al detalle de la
 * noticia seleccionada en el ListView
 */
public class ActivityDetailedNews extends AppCompatActivity {
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_news_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle (Globals.itemSelected.Title);
            getSupportActionBar().setDisplayHomeAsUpEnabled (true);
        }

        // dado que toda la lógica de cómo cargar y mostrar el detalle de la noticia seleccionada
        // se realiza en el fragmento correspodiente, aquí solo procedemos a cargar
        // dicho fragmento
        android.support.v4.app.FragmentTransaction trans =
                getSupportFragmentManager().beginTransaction ();

        FragmentDetailedNews detailedNews = new FragmentDetailedNews ();

        trans.replace (R.id.replaceMe, detailedNews);
        trans.setTransition (FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        trans.commit ();
    }

    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId ()) {
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent (this);
                intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo (this, intent);
                return true;
            default:
                return super.onOptionsItemSelected (item);
        }
    }
}
