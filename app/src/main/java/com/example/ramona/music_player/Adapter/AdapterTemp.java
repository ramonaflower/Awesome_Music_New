package com.example.ramona.music_player.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ramona.music_player.R;

import java.util.List;

/**
 * Created by Ramona on 10/24/2017.
 */

public class AdapterTemp extends RecyclerView.Adapter<AdapterTemp.MyViewHolder>{
    private List<Integer> mList;

    public AdapterTemp(List<Integer> mList) {
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.lt_item_temp, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mText1.setText(position+1+"");
        holder.mText2.setText(mList.get(position)+"");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView mText1, mText2;

        public MyViewHolder(View itemView) {
            super(itemView);
            mText1 = itemView.findViewById(R.id.stt);
            mText2= itemView.findViewById(R.id.id_stt);
        }
    }
}
