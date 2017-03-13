
package com.example.android.httpnetworking.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.httpnetworking.R;
import com.example.android.httpnetworking.model.Movie;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class MoviesAdapter extends ArrayAdapter{

    public MoviesAdapter(Context context, List objects) {
        super(context,0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Obteniendo una instancia del inflater
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Salvando la referencia del View de la fila
        View v = convertView;

        //Comprobando si el View no existe
        if (null == convertView) {
            //Si no existe, entonces inflarlo
            v = inflater.inflate(
                    R.layout.item_lista,
                    parent,
                    false);
        }

        //Obteniendo instancias de los elementos
        TextView mPositionTextView = (TextView) v.findViewById(R.id.tv_position);
        TextView mOriginalTitleTextView = (TextView) v.findViewById(R.id.tv_original_title);
        TextView mOverviewTextView = (TextView) v.findViewById(R.id.tv_overview);

        //Obteniendo instancia de la Movie en la posición actual
        Movie item = (Movie) getItem(position);
        mPositionTextView.setText(String.valueOf(position+1));
        mOriginalTitleTextView.setText(item.original_title);
        mOverviewTextView.setText(item.overview);

        return v;
    }

}
