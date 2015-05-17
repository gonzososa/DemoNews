package com.outlook.gonzasosa.demos.news;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Este fragmento se encarga de mostrar los detalles del elemento
 * seleccionado en la lista de Breaking News.
 *
 * Dado que uno de los elementos que se presentarán al usuario es una imagen en miniatura,
 * es necesario realizar las operaciones de descarga de forma asíncrona
 *
 * Para no reinventar la rueda se utiliza la clase RssReader para realizar la lectura
 * del archivo RSS
 */
public class FragmentDetailedNews extends Fragment {
    ImageView imageView;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate (R.layout.fragment_detailed_news, container, false);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);
        if (savedInstanceState != null) return;

        // "inflamos" los elementos de la interfaz
        TextView pubDate    = (TextView) getActivity().findViewById (R.id.detailedNews_datePub);
        TextView title      = (TextView) getActivity().findViewById (R.id.detailedNews_title);
        TextView content    = (TextView) getActivity().findViewById (R.id.detailedNews_content);
        imageView           = (ImageView) getActivity().findViewById (R.id.detailedNews_thumb);

        // asignamos los valores correspodientes
        pubDate.setText (Globals.itemSelected.PubDate);
        title.setText   (Globals.itemSelected.Title);
        content.setText (Globals.itemSelected.Description);

        // la descarga de la imagen thumbnail debe realizarse asíncronamente
        new DownloadThumbnailTask ().execute (Globals.itemSelected.imageURL);
    }

    /**
     * Realiza la tarea la descarga asíncrona
     */
    class DownloadThumbnailTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground (String...params) {
            return downloadThumbnail (params [0]);
        }

        /**
         * Realiza la descarga de los bytes que corresponden a la imagen deseada.
         * Una vez descargados se utiliza la clase BitmapFactory para generar el thumbnail
         * correpondiente y asignarlo al ImageView especificado
         * @param uri dirección url de la imagen
         * @return mapa de bits con la imagen obtenida
         */
        Bitmap downloadThumbnail (String uri) {
            byte [] buffer;

            try {
                URL url = new URL (uri);
                HttpURLConnection client = (HttpURLConnection) url.openConnection ();
                final int statusCode = client.getResponseCode ();

                if (statusCode != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                // debido a que en algunas ocasiones al utilizar HttpURLConnection
                // la lectura directa de las imágenes puede presentar fallas es necesario
                // realizar la lectura como un flujo de bytes y luego procesarlos para obtener la imagen
                ByteArrayOutputStream bos = new ByteArrayOutputStream ();
                byte [] bitmapBytes = new byte [1024 * 4];
                InputStream inputStream = client.getInputStream ();

                // mientras existan bytes no leídos en el flujo
                // se escriben usando un buffer y asignándolos a un objeto
                // ByteArrayOutputStream
                int i;
                while ((i = inputStream.read (bitmapBytes)) != -1) {
                    if (isCancelled ()) return null;
                    bos.write (bitmapBytes, 0, i);
                }

                buffer = bos.toByteArray ();  // obtenemos el arreglo de bytes

                inputStream.close ();
                bos.close ();
            } catch (MalformedURLException mex) {
                Log.e("BREAKINGNEWS", mex.getMessage());
                return null;
            } catch (IOException ioex) {
                Log.e ("BREAKINGNEWS", ioex.getMessage ());
                return null;
            }

            // una vez obtenidos los bytes de la imagen se puede utilizar la
            // clase BitmapFactory para generar el mapa de bits correspondiente
            return BitmapFactory.decodeByteArray(buffer, 0, buffer.length, null);
        }

        @Override
        protected void onPostExecute (Bitmap bitmap) {
            if (!isCancelled () && bitmap != null) {
                imageView.setImageBitmap (bitmap);
            } else if (!isCancelled ()) {
                Toast.makeText ( getActivity(),
                                "¡No se pudo obtener la imagen thumbnail!",
                                Toast.LENGTH_LONG).show ();
            }
        }
    }
}
