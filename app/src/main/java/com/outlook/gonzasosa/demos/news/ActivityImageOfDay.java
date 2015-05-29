package com.outlook.gonzasosa.demos.news;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.outlook.gonzasosa.demos.news.util.TouchImageView;
import com.shirwa.simplistic_rss.RssItem;
import com.shirwa.simplistic_rss.RssReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Actividad para mostrar la imagen del día obtenida a partir de
 * la direccion web especificada en la variable estática.
 *
 * Se hace uso de la clase TouchImageView debido a que esta implementa los
 * métodos necesarios para hacer zoom-out y zoom-in sobre la imagen mostrada
 *
 * Para no reinventar la rueda se utiliza la clase RssReader para realizar la lectura
 * del archivo RSS
 */
public class ActivityImageOfDay extends AppCompatActivity {
    final String URL_IMAGE_OF_DAY = "http://www.nasa.gov/rss/dyn/image_of_the_day.rss";
    TouchImageView tvImageOfDay;
    ProgressBar progressBar1;

    int deviceOrientation;
    int screenWidth;
    int screenHeight;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.image_of_day_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle ("Imagen del día");
            getSupportActionBar().setDisplayHomeAsUpEnabled (true);
        }

        // mostramos la barra de progreso
        progressBar1 = (ProgressBar) findViewById (R.id.progressBar1);
        progressBar1.setVisibility (View.VISIBLE);

        // obtenemos las dimensiones y la orientación de la pantalla
        // del dispositivo
        deviceOrientation = getResources().getConfiguration().orientation;
        DisplayMetrics metrics = new DisplayMetrics ();
        getWindowManager().getDefaultDisplay().getMetrics (metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        tvImageOfDay = (TouchImageView) findViewById (R.id.iv_imageOfDay);
        new DownloadImageOfDayTask().execute ();
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
     * Clase auxiliar para la descarga del archivo RSS que contiene la imagen del día.
     *
     * Dado que no se permiten las operaciones de entrada/salida desde el hilo de la
     * interfaz principal es necesario hacerlo en segundo plano.
     *
     * Una vez descargados los bytes que componen la imagen y dado que esta tiende a ser
     * muy grande, es necesario escalarla para evitar que se consuma el maximo de RAM
     * que se tiene disponible por el sistema.
     */
    class DownloadImageOfDayTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground (Void...params) {
            return processRSSAndDowloadImage ();
        }

        /**
         * Descarga el archivo RSS que contiene las entradas con la imágenes
         * deseadas.
         *
         * @return Objecto bitmap con la imagen descargada y escalada.
         */
        Bitmap processRSSAndDowloadImage () {
                Bitmap image = null;

                try {
                    // Utilizamos la clase RssReader para obtener la lista
                    // de elmentos del archivo XML y seleccionamos la entrada
                    // más reciente.
                    RssReader reader = new RssReader (URL_IMAGE_OF_DAY);
                    List<RssItem> items = reader.getItems ();
                    RssItem item = items.get (0);
                    String imageUri = item.getImageUrl ();

                    image = downloadAndProcessImage (imageUri);
                } catch (Exception ex) {
                    Log.e ("IMAGEOFDAY", ex.getMessage ());
                }

                return image;
            }

            @Override
            protected void onPostExecute (Bitmap image) {
                progressBar1.setVisibility (View.GONE);

            if (!isCancelled () && image != null) {
                tvImageOfDay.setVisibility (View.VISIBLE);
                tvImageOfDay.setImageBitmap (image);
            } else if (!isCancelled ()) {
                Toast.makeText (ActivityImageOfDay.this,
                                "No se pudo obtener la imagen",
                                Toast.LENGTH_LONG).show ();
            }
        }

        /**
         * Realiza la descarga de la imagen seleccionada
         * @param imageUri Dirección URL de la imagen
         * @return mapa de bits con la imagen procesada.
         */
        Bitmap downloadAndProcessImage (String imageUri) {
            byte [] buffer;
            BitmapFactory.Options options;

            try {
                URL url = new URL(imageUri);
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
                // obtenemos una referencia al flujo de bytes correspondiente a la imagen
                InputStream inputStream = client.getInputStream ();

                // mientras existan bytes no leídos en el flujo
                // se escriben usando un buffer y asignándolos a un objeto
                // ByteArrayOutputStream
                int i;
                while ((i = inputStream.read (bitmapBytes)) != -1) {
                    if (isCancelled ()) return null;
                    bos.write (bitmapBytes, 0, i);
                }

                buffer = bos.toByteArray (); // obtenemos el arreglo de bytes

                inputStream.close ();
                bos.close ();
            } catch (MalformedURLException mex) {
                Log.e ("IMAGEOFDAY", mex.getMessage ());
                return null;
            } catch (IOException ioex) {
                Log.e ("IMAGEOFDAY", ioex.getMessage ());
                return null;
            }

            // una vez obtenidos los bytes de la imagen se puede utilizar la
            // clase BitmapFactory para generar el mapa de bits correspondiente
            // sin embargo, tomando en cuenta que las imágenes son bastante grandes
            // es necesario realizar un escalado de la imagen antes de asignar a la
            // vista que la prestará al usuario ya que de otra forma es posible obtener un
            // error del tipo MemoryOutRangeException
            options = new BitmapFactory.Options ();

            // primero obtenemos el mejor nivel de escalado a partir de un ancho y alto determinado
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray (buffer, 0, buffer.length, options);
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = calculateInSampleSize (options, 600, 600);
            options.inJustDecodeBounds = false;

            // una vez que se conoce el mejor nivel de escalado, generamos la imagen
            Bitmap bmp = BitmapFactory.decodeByteArray (buffer, 0, buffer.length, options);
            Bitmap scaledBitmap = scaleImage (bmp, 600, 600);

            // reciclamos el mapa de bits
            bmp.recycle ();
            buffer = null;

            return scaledBitmap;
        }

        /**
         * Calcula el mejor nivel a escalar la imagen a partir de las dimensiones dadas
         * @param options objeto Bitmap.Options
         * @param reqWidth ancho deseado para la imagen resultante
         * @param reqHeight alto deseado para la imagen resultante
         * @return valor entero con el mejor nivel de escalado
         */
        int calculateInSampleSize (BitmapFactory.Options options, int reqWidth, int reqHeight) {
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                while (((halfHeight / inSampleSize) > reqHeight) && ((halfWidth / inSampleSize) > reqWidth)) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }

        /**
         * Obtiene una imagen escalada a partir de las dimensiones dadas
         * @param bitmap Objeto bitmap con la imagen original
         * @param width ancho deseado para la imagen escalada
         * @param height alto deseado para la imagen escalada
         * @return mapa de bits con la imagen escalada según los parámetros dados
         */
        Bitmap scaleImage (Bitmap bitmap, int width, int height) {
            int oWIdth = bitmap.getWidth ();
            int oHeight = bitmap.getHeight ();

            if (oWIdth > oHeight) {
                height = (int) (((double) oHeight / (double) oWIdth) * height);
            } else if (oHeight > oWIdth) {
                width = (int) (((double) oWIdth / (double) oHeight) * width);
            }

            return Bitmap.createScaledBitmap (bitmap, width, height, true);
        }
    }
}
