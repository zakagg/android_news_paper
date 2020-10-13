package com.example.zaki.projet;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AccesDonnees {
    private static String authority="com.example.zaki.basededonneprojet";
    private ContentResolver contentResolver;
    Context c;
    public AccesDonnees (Context context )
    {
        contentResolver = context.getContentResolver();
        c= context;
    }

    public void ajoutlien(String link_submited,String link,String title,String discription) {
        ContentValues values = new ContentValues();
        values.put("link_submited", link_submited);
        values.put("link", link);
        values.put("title", title);
        values.put("discription", discription);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("parserpage_table");
        Uri uri = builder.build();
        Toast.makeText(c,""+existedeja(link),Toast.LENGTH_LONG).show();
        if (!existedeja(link))
            uri = contentResolver.insert(uri, values);

        Log.d("AccesDonnees", "l'ajjout marche" + link_submited);
        Log.d("AccesDonnees", "l'uri est " + uri.toString());

    }

    public void ajoutitems(String link_submited,String link,String title,String discription) {
        ContentValues values = new ContentValues();
        values.put("link_submited", link_submited);
        values.put("link", link);
        values.put("title", title);
        values.put("favoris", 0);
        values.put("discription", discription);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ");
        Calendar cal = Calendar.getInstance();
        values.put("date", dateFormat.format(cal.getTime()));
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("items_table");
        Uri uri = builder.build();

        try {
            uri = contentResolver.insert(uri, values);
            Log.d("AccesDonnees", "l'ajjout marche de l'itemes" + link_submited);
        }
        catch (SQLException e)
        {
            Toast.makeText(c,"il existe item de meme lien",Toast.LENGTH_LONG).show();
        }
        Log.d("AccesDonnees", "l'uri est " + uri.toString());
    }

    public void nb_item_ajjouter(String url)
    {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("items_table");
        Uri uri = builder.build();
        Cursor cursor=contentResolver.query(uri,null,null,null,null);
        if(cursor==null){
            Toast.makeText(c,"lollll",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(c,""+cursor.getCount(),Toast.LENGTH_LONG).show();
        }


    }
    public boolean existedeja(String lien)
    {

        String []tab= {lien};
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("parserpage_table");
        Uri uri = builder.build();
        Cursor cursor=contentResolver.query(uri,null,"link_submited = ? ",tab,null);

        if (cursor==null)
            return false;
        else
            return cursor.getCount()>0;
    }
    public void supression_moins_de_deux_jours ()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE,-2);
        String date =dateFormat.format(cal.getTime());
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("items_table");
        Uri uri = builder.build();
        String[] tab = { date,""+0};
        int i=contentResolver.delete(uri,"date <= ? AND favoris = ? ", tab);

        Toast.makeText(c,""+i,Toast.LENGTH_LONG).show();
    }
}