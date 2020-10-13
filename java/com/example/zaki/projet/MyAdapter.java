package com.example.zaki.projet;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
public class MyAdapter extends RecyclerView.Adapter <MyAdapter.ViewHolder>{
    Cursor MonCursor;
    WhenCliqueOnItem ecouteur;

    public static class ViewHolder extends RecyclerView.ViewHolder
    {

        public TextView titre;
        public TextView description ;
        //public TextView lien;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titre=itemView.findViewById(R.id.titre);
            description=itemView.findViewById(R.id.description);
          //  lien=itemView.findViewById(R.id.lien);
        }
    }

    public interface WhenCliqueOnItem{
        void ItemHasBeenClicked (Cursor cursor);
    }

    public MyAdapter(WhenCliqueOnItem ecouteur)
    {
        this.ecouteur=ecouteur;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.exemple_item,viewGroup,false);
        final ViewHolder vh=new ViewHolder(v);

        v.setOnClickListener(new  View.OnClickListener(){

            @Override
            public void onClick(View v) {
                int enplacement = vh.getAdapterPosition();
                MonCursor.moveToPosition(enplacement);
                if(ecouteur!=null){
                    ecouteur.ItemHasBeenClicked(MonCursor);
                }
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder VH, int i)
    {
        MonCursor.moveToPosition(i);
        //VH.mImageView.setImageResource(R.drawable.ic_star_black_24dp);
        //VH.mImageView.setColorFilter(Color.rgb(218,165,32));
        VH.titre.setText(MonCursor.getString(MonCursor.getColumnIndex("title")));
        VH.description.setText(MonCursor.getString(MonCursor.getColumnIndex("discription")));
        //VH.lien.setText(MonCursor.getString(MonCursor.getColumnIndex("date")));

    }

    @Override
    public int getItemCount()
    {
        return (MonCursor != null) ? MonCursor.getCount() : 0;
    }

    public void setCursor(Cursor newCursor){
        MonCursor = newCursor;
        notifyDataSetChanged();
    }
    public Cursor getCursor(){
        return MonCursor;
    }
    @Override
    public long getItemId(int position) {
        if (MonCursor != null) {
            if (MonCursor.moveToPosition(position)) {
                int idx_id = MonCursor.getColumnIndex("_id");
                return MonCursor.getLong(idx_id);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }


}
