package com.example.groupsms;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<String> mDataSet;
    public ArrayList<String> checkPos = new ArrayList<>();


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public CheckBox checkBox;

        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.adapterTextView);
            checkBox = v.findViewById(R.id.checkBox);


        }
    }

    public MyAdapter(ArrayList<String> myDataset) {
        mDataSet = myDataset;
    }


    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.phonenumber_view, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String text = mDataSet.get(position);
        if (checkPos.size() == 0) {
            holder.checkBox.setChecked(false);

        }

        holder.textView.setText(text);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (mDataSet.size() > 0) {
                    if (isChecked) {
                        checkPos.add(mDataSet.get(position));
                        Log.d("MyAdapter_Log", "(onBindViewHolder) Add delete data position : " + mDataSet.get(position));
                    } else {
                        checkPos.remove(mDataSet.get(position));
                        Log.d("MyAdapter_Log", "(onBindViewHolder) Remove delete data position : " + mDataSet.get(position));
                    }
                }

            }


        });

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}

