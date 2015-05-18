package com.outlook.gonzasosa.demos.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Esta clase se encarga de mostrar el listado con las entradas RSS y maneja el evento
 * onItemClick del ListView
 *
 */
public class ActivityBreakingNews extends AppCompatActivity implements OnNewsSelected {
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.breaking_news_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle ("Breaking News");
            getSupportActionBar().setDisplayHomeAsUpEnabled (true);
        }

        // dado que toda la lógica para descargar y procesar las entradas del
        // archivo RSS descargado se realiza en el fragmento correspondiente, aquí
        // únicamente procedemos a cargar dicho fragmento
        FragmentBreakingNews frgBreakingNews =
                (FragmentBreakingNews) getSupportFragmentManager().findFragmentById (R.id.frgBreakingNews);

        // y establece el manejador del evento onItemClick
        if (frgBreakingNews != null) {
            frgBreakingNews.setNewsSelectedListener (this);
        }
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


    /**
     * Este método se invoca cuando el usuario hace "tap" en un elemento de la lista de
     * entradas del ListView
     *
     * El detalle del elemento seleccionado se muestra en otro fragmento adyacente a la lista o
     * bien en un actividad completamente aparte.
     *
     * @param item Información del elemento seleccionado
     */
    public void showDetailsNews (NASANewsItem item) {
        Globals.itemSelected = item;

        // si existe el layout "dummyContent" entonces el sistema dispone de un pantalla
        // grande en modo landscape por lo que procedemos a mostrar el detalle del
        // elemento seleccionado en el fragmento de detalles
        if (findViewById (R.id.dummyContent) != null) {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            FragmentDetailedNews detailedNews = new FragmentDetailedNews ();
            trans.replace (R.id.dummyContent, detailedNews);
            trans.setTransition (FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            trans.commit ();
        } else { // de lo contrario, simplemente se llama a la actividad correspondiente
            Intent intent = new Intent (this, ActivityDetailedNews.class);
            startActivity (intent);
        }
    }
}
