package com.outlook.gonzasosa.demos.news.interfaces;

import com.outlook.gonzasosa.demos.news.util.NASANewsItem;

/**
 * Interfaz para manejar el evento onItemClick de la lista de noticias
 * Breaking News
 */
public interface OnNewsSelected {
    public void showDetailsNews (NASANewsItem item);
}
