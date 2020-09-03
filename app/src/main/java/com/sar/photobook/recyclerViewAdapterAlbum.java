package com.sar.photobook;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sar.photobook.models.album;

import java.util.List;

public class recyclerViewAdapterAlbum extends RecyclerView.Adapter<recyclerViewAdapterAlbum.ViewHolder>  {

    private static final String TAG = "recycViewAdaptAlbum";

    private List<album> list;
    private Context mContext;

    public recyclerViewAdapterAlbum(Context mContext, List<album> list) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_items_album, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder : called.");

        album post = list.get(i);

            Glide.with(mContext)
                    .asBitmap()
                    .load(post.getPhotoUrl())
                    .into(viewHolder.image_item);

            viewHolder.title.setText(post.getTitle());

    }

    @Override
    public int getItemCount() {
                int arr = 0;
        try{
            if(list.size()==0){
                arr = 0;
            }
            else{
                arr=list.size();
            }
        }
        catch (Exception e){
        }

        return arr;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        AppCompatImageView image_item;
        AppCompatTextView title;
        CardView cardViewHome;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_item = itemView.findViewById(R.id.image_item);
            title = itemView.findViewById(R.id.title);
            cardViewHome = itemView.findViewById(R.id.cardview_home);
        }
    }
}
