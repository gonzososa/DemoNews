package com.outlook.gonzasosa.demos.news.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.outlook.gonzasosa.demos.news.R;
import com.outlook.gonzasosa.demos.news.interfaces.OnNewsSelected;
import com.outlook.gonzasosa.demos.news.util.NASANewsItem;
import com.shirwa.simplistic_rss.RssItem;
import com.shirwa.simplistic_rss.RssReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase se encarga de descargar el archivo RSS especificado y
 * llena el dataset correspodiente al ListView que que se presentará al usuario.
 *
 * Se utiliza un fragmento para presentar una interfaz más adecuada en caso que
 * el usuario utilice un dispositivo de pantalla grande.
 */
public class FragmentBreakingNews extends Fragment {
    final String URL_BREAKING_NEWS = "http://www.nasa.gov/rss/dyn/breaking_news.rss";
    OnNewsSelected listener;
    ListView listViewNews;
    ArrayList<NASANewsItem> newsItems;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate (R.layout.fragment_breaking_news, container, false);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);

        newsItems = new ArrayList<>();
        listViewNews = (ListView) getActivity().findViewById (R.id.lvNews);

        /**
         * Establece la acción para cuando el usuario seleccione un elemento de la lista
         */
        listViewNews.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick (AdapterView<?> adapterView, View view, int i, long l) {
                listener.showDetailsNews (newsItems.get (i));
            }
        });

        // la tarea de descarga y llenada del adaptador del ListView se realiza mediante
        // una tarea asíncrona
        new RSSHelperTask().execute ();
    }

    /**
     * Clase auxiliar para descargar las entradas del archivo RSS
     *
     * Dado que no se permiten las operaciones de entrada/salida desde el hilo de la interfaz
     * principal es neceario realizar las operaciones de red de forma asíncrona, de esa forma
     * la interfaz principal no se bloquea.
     */
    class RSSHelperTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            return downloadNews ();
        }

        private Boolean downloadNews () {
            try {
                // Utilizamos la clase RSSReader para descargar el archivo XML
                RssReader reader = new RssReader (URL_BREAKING_NEWS);
                List<RssItem> items = reader.getItems ();

                // Iteramos por el listado obtenido seleccionando la entrada
                // mas reciente.
                for (RssItem i : items) {
                    NASANewsItem item = new NASANewsItem ();
                    item.Title = i.getTitle ();
                    item.PubDate = i.getPubDate ();
                    item.Description = i.getDescription ();
                    item.Link = i.getLink ();
                    item.imageURL = i.getImageUrl ();
                    newsItems.add(item);
                }
            } catch (Exception ex) {
                Log.e ("BREAKINGNEWS", ex.getMessage ());
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute (Boolean value) {
            if (!isCancelled () && value) {
                populateAdapter ();
            } else if (!isCancelled ()) {
                Toast.makeText (getActivity(),
                                "Error al obtener las entradas RSS!",
                                Toast.LENGTH_LONG).show ();
            }
        }

        /**
         * Se establece el adaptador para el ListView que se presentará al usuario
         */
        private void populateAdapter () {
            final ArrayList<NASANewsItem> i = newsItems;
            listViewNews.setAdapter (new ArrayAdapter<NASANewsItem>(getActivity().getBaseContext(),
                                                                    R.layout.rss_entry_item,
                                                                    i) {
                /**
                 * Devuelve un objeto View iterando por cada uno de los elementos obtenidos del
                 * archivo RSS.
                 *
                 * Se hace uso del patrón ViewHolder para acelerar el dibujado mediante
                 * las vistas recicladas del ListView
                 *
                 * @param position
                 * @param convertView
                 * @param parent
                 * @return
                 */
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ViewHolder viewHolder;

                    if (convertView == null) {
                        convertView = getActivity().getLayoutInflater().inflate (R.layout.rss_entry_item, null);
                        viewHolder = new ViewHolder ();
                        viewHolder.tvDatePub = (TextView) convertView.findViewById (R.id.datePub);
                        viewHolder.tvTitle = (TextView) convertView.findViewById (R.id.rssEntry);
                        convertView.setTag (viewHolder);
                    } else {
                        viewHolder = (ViewHolder) convertView.getTag ();
                    }

                    viewHolder.tvDatePub.setText (i.get(position).PubDate);
                    viewHolder.tvTitle.setText (i.get(position).Title);

                    return convertView;
                }
                 }
            );
        }
    }

    /**
     * Establece el manejador del evento "tap" en el ListView
     * @param listener clase que implementa la interfaz OnNewsSelected
     */
    public void setNewsSelectedListener (OnNewsSelected listener) {
        this.listener = listener;
    }

    /**
     * Esta clase nos ayuda a almancear en memoria los elementos que se presentarán en el
     * ListView de modo que sólo sera necesario "inflarlos" una vez, acelerando el dibujado
     * de la interfaz
     */
    class ViewHolder {
        public TextView tvDatePub;
        public TextView tvTitle;
    }
}
