package com.example.zaki.projet;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.LocalDateTime;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;

import static java.util.logging.Logger.global;

public class MainActivity extends AppCompatActivity {
    static DownloadManager dm;
    static long id;
    TextView file;
    String fileName;
    Cursor cur;
    String path="";
    AccesDonnees accesDonnees;
    String[] downloadedFiles;
    EditText saisie;
    Intent intent;
   ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dm=(DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        saisie=(EditText)findViewById(R.id.url);
        file=(TextView) findViewById(R.id.file);
        accesDonnees=new AccesDonnees(this);
        accesDonnees.supression_moins_de_deux_jours();

        IntentFilter filter =           new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver( new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(                   DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (reference==id ){
                    try {
                        Afficher_file();
                        progressDialog.dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }},filter);
    }
    public void chargementDownlodmanager(View view) throws IOException {
        Uri uri = Uri.parse(saisie.getText().toString());
      progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("loading");
        progressDialog.setTitle("telechargement");
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             dm.remove(id);
             progressDialog.dismiss();
            }
        });

        DownloadManager.Request req = new DownloadManager.Request(uri);
        Toast.makeText(this,uri.getPath(),Toast.LENGTH_LONG).show();
        req.setDestinationInExternalFilesDir(this,/*context*/ Environment.DIRECTORY_DOWNLOADS,"animals");
        id = dm.enqueue(req);
        //fileName = URLUtil.guessFileName("https://www.thesundaily.my/rss/style-life/reviews", null, MimeTypeMap.getFileExtensionFromUrl("https://www.lemonde.fr/rss/une.xml"));

       //progressDialog.dismiss();
            //Toast.makeText(this, fileName, Toast.LENGTH_LONG).show();
    }

    public void Afficher_liste_des_fichier(View view)
    {
        Intent intent=new Intent(this,AfficheActivity.class);
        startActivity(intent);
    }

    public  void Afficher_file() throws IOException {

        DownloadManager.Query question = new DownloadManager.Query();
        question.setFilterById(id).setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor cur= dm.query(question);

        if(cur==null){
            Toast.makeText(this,"euroor",Toast.LENGTH_LONG).show();
        }
        else {
            ParcelFileDescriptor pDesc = null;
            try {
                pDesc = dm.openDownloadedFile(id);
                FileDescriptor desc=pDesc.getFileDescriptor();

                if (cur.moveToFirst()) {
                    path = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        Toast.makeText(this,path,Toast.LENGTH_LONG).show();
                        file.setText(path);
                        Uri uri=Uri.parse(path);
                        File file=new File(uri.getPath());
                        if (file.exists())
                            Toast.makeText(this,uri.getPath()+ "file existe",Toast.LENGTH_LONG).show();
                        //dm.remove(id);
                        //file.delete();
                    try {
                        SAXParserFactory factory = SAXParserFactory.newInstance();
                        SAXParser parser = factory.newSAXParser();
                        parser.parse(path, new MyXMLHandler(accesDonnees));

                        dm.remove(id);
                        file.delete();

                    } catch (DOMException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    } catch (TransformerFactoryConfigurationError e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
