package com.outlook.gonzasosa.demos.news;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import java.util.ArrayList;
import java.util.List;

public class FragmentBreakingNews extends Fragment {
    final String URL_BREAKING_NEWS = "http://www.nasa.gov/rss/dyn/breaking_news.rss";

    ListView news;
    ArrayList<NASANewsItem> newsItems = new ArrayList<NASANewsItem> ();

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate (R.layout.breaking_news, container, false);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        downloadNews ();
        news = (ListView) getActivity().findViewById (R.id.lvNews);
    }

    private void downloadNews () {
        RSSReader reader = new RSSReader ();

        try {
            RSSFeed feeds = reader.load (URL_BREAKING_NEWS);
            List<RSSItem> items = feeds.getItems();

            for (RSSItem i: items) {
                NASANewsItem item = new NASANewsItem ();
                item.Title = i.getTitle ();
                item.Description = i.getDescription ();
                item.Link = i.getLink ();
                item.PubDate = i.getPubDate ();

                newsItems.add (item);
            }
        } catch (RSSReaderException rex) {
            Toast.makeText (getActivity().getBaseContext (), rex.getMessage (), Toast.LENGTH_LONG).show ();
        }
    }

}
