package com.example.zaki.projet;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class RechercheActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    String query1;
    String query2;

    SimpleCursorAdapter adapter;
    LoaderManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent =getIntent();
        //  query= intent.getStringExtra(SearchManager.QUERY);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query1 = intent.getStringExtra(SearchManager.QUERY);



            adapter = new SimpleCursorAdapter(
                    this, // Context.
                    android.R.layout.simple_list_item_1,
                    null,
                    new String[]{"title"},
                    new int[]{android.R.id.text1});

            setListAdapter(adapter);

            manager=getLoaderManager();
            manager.initLoader(0, null,this);
        }
        else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            query2= intent.getData().getLastPathSegment();

            doserch2(query2);
        }
    }
    protected void onListItemClick(ListView l,
                                   View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        Cursor c = (Cursor) getListAdapter().getItem(position);
        //Log.i("bd", c.getString(0));
        String lien = c.getString(c.getColumnIndex("link"));

        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(lien));
        startActivity(intent);

        //  manager.restartLoader(0,null, this);



    }
    public void  doserch2(String qurey){
        adapter = new SimpleCursorAdapter(
                this, // Context.
                android.R.layout.simple_list_item_2,
                null,
                new String[]{"title","discription"},
                new int[]{android.R.id.text1,android.R.id.text2});

        setListAdapter(adapter);

        manager=getLoaderManager();
        manager.initLoader(1, null,this);

    }
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String []serchId={query2} ;

        Uri u1;
        if (id == 0) {
            Uri.Builder builder = (new Uri.Builder().scheme("content")).authority("com.example.zaki.basededonneprojet")
                    .appendPath("search").appendPath("%" + query1 + "%");
            u1 = builder.build();

            return new CursorLoader(this, u1, new String[]
                    {"rowid as _id","title"},null,null,null);
        }
        else
        { Uri.Builder builder = (new Uri.Builder().scheme("content")).authority("com.example.zaki.basededonneprojet")
                .appendPath("items_table");
            u1 = builder.build();

            return new CursorLoader(this, u1, new String[]
                    {"rowid as _id","title","discription","link"},"_id = ?",serchId,null);
        }
    }
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //data.moveToFirst();
        adapter.swapCursor(data); // les données prêtes, associer le Cursor
        adapter.notifyDataSetChanged(); // avec adapter pour faire afficher� }�
    }
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
        adapter.notifyDataSetChanged();
    }
}
