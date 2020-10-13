package com.example.zaki.projet;
import android.content.Context;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
public class MyXMLHandler extends DefaultHandler{

    private AccesDonnees accesDonnees;
    private Context context;
    private String node = null;
    private int ieme=0;
    private String parent="";
    int isiteme=0;
    int inode=0;
    int idesc=0;
    int ilink=0;
    int ititre=0;
    int i=0;
    int premier_titre=0;
    int premiere_desc=0;
    int premier_link=0;
    String lien1="",
      desc1="", titre1="";
    String item_titre="",item_description="",itemlink="";
    int pass_val_item_data_base=0;//quand ca egale a 4 on passe les valeur dans la base de donnée.

    //début du parsing
    public MyXMLHandler(AccesDonnees accesDonnees)
    {
        this.accesDonnees=accesDonnees;
    }
    public void startDocument() throws SAXException {
        System.out.println("Début du parsing");
    }
    //fin du parsing
    public void endDocument() throws SAXException {
        System.out.println("Fin du parsing");
    }

    /**
     * Redéfinition de la méthode pour intercepter les événements
     */
    public void startElement(String namespaceURI, String lname,
                             String qname, Attributes attrs) throws SAXException {
        if(node=="item")
        {

            isiteme=1;
           // Log.d("MyXMLHandler", " je vien de rentre dans le item "+node);
        }
        if(node=="link")
        {
            ilink=1;
        }
        if(node=="title")
        {
            ititre=1;
        }
        if(node=="description")
        {
            idesc=1;
        }
        if(ieme==2&& node=="link")
        {
           // Log.d("MyXMLHandler", " je suis le premier link "+node +"  "+ieme + premier_link);
            premier_link=1;
        }
        if(ieme==2&& node=="title")
        {
            //Log.d("MyXMLHandler", " je suis le premier link "+node +"  "+ieme + premier_link);
            premier_titre=1;
        }
        if(ieme==2&& node=="description")
        {
            //Log.d("MyXMLHandler", " je suis le premier link "+node +"  "+ieme + premier_link);
            premiere_desc=1;
        }

        node = qname;
        ieme++;
        //Cette dernière contient la liste des attributs du nœud
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); i++) {
                //nous récupérons le nom de l'attribut
                String aname = attrs.getLocalName(i);
                //Et nous affichons sa valeur
                // System.out.println("Attribut " + aname + " valeur : " + attrs.getValue(i)+ "la valeur de qName " + qname+ " la valeur de l name " + lname);
            }
        }
    }
    public void endElement(String uri, String localName, String qName)
            throws SAXException{
        ieme--;
        if (node=="item") {
            isiteme=0;
            //Log.d("MyXMLHandler"," je vien de sortir  " +node);
        }
        if(node=="link")
        {
            ilink=0;
        }
        if(node=="title")
        {
            ititre=0;
        }
        if(node=="description")
        {
            idesc=0;
        }
/*
        if(ieme==3&& node=="link")
        {
            Log.d("MyXMLHandler", " je suis le premier link passer par  la fin "+node +"  "+ieme + premier_link);
            premier_link=1;
        }
        //if (node=="link" && ieme==2)
       // {
       //     premier_link=0;
       // }
        //System.out.println("Fin de l'élément " + qName);
        // System.out.println("la valeur de ieme " + ieme);
  */
    }
    /**
     * permet de récupérer la valeur d'un nœud
     */
    public void characters(char[] data, int start, int end){
        String str = new String(data, start, end);
        if(isiteme==1)
        {
            //System.out.println( node + " je suis le fils " + str+ "le ieme est " );
            if (node=="link"&& ilink==1)
            {
                //Log.d("MyXMLHandler", "je suis le link qui apartient aux " + node);
                itemlink=str;
                Log.d("MyXMLHandler", str);
                pass_val_item_data_base++;

            }
            if(node=="title"&& ititre==1)
            {
                //Log.d("MyXMLHandler", "je suis le titre qui apartient aux " + node);
                pass_val_item_data_base++;
                item_titre=str;
                Log.d("MyXMLHandler", str);
            }
            if(node=="description"&& idesc==1)
            {// Log.d("MyXMLHandler", "je suis la description qui apartient aux " + node +ieme);
                pass_val_item_data_base++;
                item_description=str;
                Log.d("MyXMLHandler", str);

            }
            if(pass_val_item_data_base==3)
            {
                pass_val_item_data_base=0;
                Log.d("MyXMLHandler", "ajjout dans l'items"+ itemlink);
                Log.d("MyXMLHandler", "ajjout dans l'items"+ item_description);
                Log.d("MyXMLHandler", "ajjout dans l'items"+ item_titre);
                accesDonnees.ajoutitems(lien1,itemlink,item_titre,item_description);
                itemlink="";
                item_titre="";
                item_description="";
            }
        }
        if(node=="link" && ieme==3 && premier_link==0) {
            lien1=""+str;
            inode++;
        }
        if(node=="description" && ieme==3 && premiere_desc==0) {
            desc1=""+str;
            inode++;
        }
        if(node=="title" && ieme==3 && premier_titre==0) {
            titre1=""+str;
            inode++;
        }
        if(inode>=3)
        {
            accesDonnees.ajoutlien(lien1,lien1,titre1,desc1);
            inode=0;
        }
    }
}