package com.example.zaki.projet;

import android.content.CursorLoader;
import android.content.Intent;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AfficheActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private LinearLayoutManager linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affiche);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        configuraSwipe();
        recyclerView.setHasFixedSize(true);
        linearLayout= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayout);
        adapter = new MyAdapter(new MyAdapter.WhenCliqueOnItem() {
            @Override
            public void ItemHasBeenClicked(Cursor cursor) {
                String str=cursor.getString(cursor.getColumnIndex("link"));
                Intent iii=new Intent(AfficheActivity.this,Affiche_itemActivity.class);
                iii.putExtra("MESSAGE",str);
                startActivity(iii);
            }
        });
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        getLoaderManager().initLoader(0,null,this);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {Uri uri;
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority("com.example.zaki.basededonneprojet")
                .appendPath("parserpage_table")
                .build();
        String projection1 []={"*","rowid as _id"};

        return new CursorLoader(this, uri, projection1,
                null, null, null);

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
                        final String lksub = cursor.getString(
                                cursor.getColumnIndex("link_submited"));
                        Uri.Builder builder = new Uri.Builder();
                        Uri uri = builder.scheme("content")
                                .authority("com.example.zaki.basededonneprojet")
                                .appendPath("parserpage_table")
                                .build();
                        String[] args={""+lksub};

                        Uri.Builder builder1 = new Uri.Builder();
                        Uri uri1 = builder1.scheme("content")
                                .authority("com.example.zaki.basededonneprojet")
                                .appendPath("items_table")
                                .build();
                        getContentResolver().delete(uri,"link_submited=?",args);
                        getContentResolver().delete(uri1,"link_submited=?",args);
                        getLoaderManager().restartLoader(0,null,AfficheActivity.this);
                    }
                };
        ItemTouchHelper itemTouchHelper =
                new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        adapter.setCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.setCursor(null);
    }
}
