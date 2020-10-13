package com.example.zaki.projet;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SearchView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Affiche_itemActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView recyclerView;
    private MyItemAdapter adapter;
    private LinearLayoutManager linearLayout;
    private String link;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affiche_item);
        Intent iii=getIntent();
        link=iii.getStringExtra("MESSAGE");
        Toast.makeText(this,link,Toast.LENGTH_LONG).show();

        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewItem);
        linearLayout= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayout);
        configuraSwipe();
        adapter = new MyItemAdapter(new MyItemAdapter.WhenCliqueOnItem_to_url() {
            @Override
            public void ItemHasBeenClicked(Cursor cursor) {
                String link=cursor.getString(cursor.getColumnIndex("link"));
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link));
                startActivity(intent);
            }

            @Override
            public void ajouterFavoris(Cursor cursor, int fav) {

                int id = cursor.getInt(cursor.getColumnIndex("id"));
                ContentValues values = new ContentValues();
                values.put("favoris", fav);
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("content").authority("com.example.zaki.basededonneprojet").appendPath("items_table");
                Uri uri = builder.build();
                String[] args ={""+id};
                getContentResolver().update(uri,values,"id = ?",args);

                getLoaderManager().restartLoader(0,null,Affiche_itemActivity.this);

            }
        }
        );
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        getLoaderManager().restartLoader(0,null,this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));

        return true;

    }
    private void configuraSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(
                        0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder
                                                 viewHolder,
                                         int swipeDir) {
                        final int x = viewHolder.getLayoutPosition();
                        Cursor cursor = adapter.getCursor();
                        cursor.moveToPosition(x);
                        final int id = cursor.getInt(cursor.getColumnIndex("id"));
                       int fav = cursor.getInt(cursor.getColumnIndex("favoris"));

                       if(fav==0) {
                           Uri.Builder builder = new Uri.Builder();
                           Uri uri = builder.scheme("content")
                                   .authority("com.example.zaki.basededonneprojet")
                                   .appendPath("items_table")
                                   .build();
                           String[] args = {"" + id};
                           getContentResolver().delete(uri, "id=?", args);
                       }
                        getLoaderManager().restartLoader(0,null,Affiche_itemActivity.this);
                    }
                };
        ItemTouchHelper itemTouchHelper =
                new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {Uri uri;
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority("com.example.zaki.basededonneprojet")
                .appendPath("items_table")
                .build();
        String [] linktab= {link};
        return new CursorLoader(this, uri, null,
                "link_submited=?", linktab,null);
    }
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        adapter.setCursor(cursor);
        adapter.getCursor();
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.setCursor(null);
        adapter.notifyDataSetChanged();
    }
}
