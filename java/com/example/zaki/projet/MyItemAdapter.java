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

public class MyItemAdapter extends RecyclerView.Adapter <MyItemAdapter.ViewHolderItem>{
    Cursor MonCursor;
    WhenCliqueOnItem_to_url ecouteur;
    int visible=0;

    public static class ViewHolderItem extends RecyclerView.ViewHolder
    {
        public ImageView mImageView;
        public TextView titre;
        public TextView description ;
        public TextView date;

        public ViewHolderItem(@NonNull View itemView) {
            super(itemView);
            mImageView=itemView.findViewById(R.id.imageView);
            titre=itemView.findViewById(R.id.titreItem);
            description=itemView.findViewById(R.id.descriptionItem);
            date=itemView.findViewById(R.id.dateItem);
        }

    }

    public interface WhenCliqueOnItem_to_url{
        void ItemHasBeenClicked (Cursor cursor);
        void ajouterFavoris (Cursor cursor,int fav);
    }

    public MyItemAdapter(WhenCliqueOnItem_to_url ecouteur)
    {
        this.ecouteur=ecouteur;
    }
    @NonNull
    @Override
    public ViewHolderItem onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itemview,viewGroup,false);
        final ViewHolderItem vh=new ViewHolderItem(v);
        vh.description.setVisibility(View.GONE);
        vh.titre.setOnClickListener(new View.OnClickListener (){

                                        @Override
                                        public void onClick(View v) {
                                            if (visible==0 ){
                                                vh.description.setVisibility(View.VISIBLE);
                                                visible++;
                                            }
                                            else
                                                {
                                                    visible=0;
                                                    vh.description.setVisibility(View.GONE);
                                                }
                                        }
                                    }
        );
        vh.description.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int enplacement = vh.getAdapterPosition();
                MonCursor.moveToPosition(enplacement);
                if(ecouteur!=null){
                    ecouteur.ItemHasBeenClicked(MonCursor);
                }
            }
        });

        vh.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int enplacement = vh.getAdapterPosition();
                MonCursor.moveToPosition(enplacement);
                if(MonCursor.getInt(MonCursor.getColumnIndex("favoris"))==0){
                    vh.mImageView.setColorFilter(Color.rgb(218,165,32));
                   if(ecouteur!=null) {
                       ecouteur.ajouterFavoris(MonCursor,1);
                   }
                }else{
                    vh.mImageView.setColorFilter(Color.rgb(192,192,192));
                    if(ecouteur!=null) {
                        ecouteur.ajouterFavoris(MonCursor,0);
                    }
                }

            }
        });

        return vh;

    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolderItem VH, int i)
    {
        MonCursor.moveToPosition(i);
        VH.mImageView.setImageResource(R.drawable.ic_star_black_24dp);
        VH.mImageView.setColorFilter(Color.rgb(192,192,192));
        if(MonCursor.getInt(MonCursor.getColumnIndex("favoris"))==1)
            VH.mImageView.setColorFilter(Color.rgb(218,165,32));
        VH.titre.setText(MonCursor.getString(MonCursor.getColumnIndex("title")));
        VH.description.setText(MonCursor.getString(MonCursor.getColumnIndex("discription")));
        VH.date.setText(MonCursor.getString(MonCursor.getColumnIndex("date")));
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
                int idx_id = MonCursor.getColumnIndex("id");
                return MonCursor.getLong(idx_id);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
    public void notifyItemRemoved()
    {
    }

}
