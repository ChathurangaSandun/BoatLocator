package com.bankfinder.chathurangasandun.boatlocator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Chathuranga Sandun on 6/3/2016.
 */
public class OwnerAdapter extends RecyclerView.Adapter<OwnerAdapter.BranchViewholder>{

    private List<String> fishermanList;

    public class BranchViewholder extends RecyclerView.ViewHolder {
        public TextView tvFishermanName;
        public ImageView imgFisherman;

        public BranchViewholder(View view) {
            super(view);


            tvFishermanName = (TextView) view.findViewById(R.id.tvfisherman);
            Bitmap bitmap = BitmapFactory.decodeResource(view.getResources(),R.drawable.background);
            Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 180);

            imgFisherman= (ImageView) view.findViewById(R.id.ivfishermanFace);
            imgFisherman.setImageBitmap(circularBitmap);

        }
    }


    public OwnerAdapter(List<String> fishermanList) {
        this.fishermanList = fishermanList;
    }

    @Override
    public BranchViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);

        return new BranchViewholder(itemView);
    }

    @Override
    public void onBindViewHolder(BranchViewholder holder, int position) {
        holder.tvFishermanName.setText(fishermanList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.fishermanList.size();
    }


    /*public void setFilter(List<Branches> bList) {
        this.branchesList = new ArrayList<>();
        this.branchesList.addAll(bList);
        notifyDataSetChanged();
    }*/
}
