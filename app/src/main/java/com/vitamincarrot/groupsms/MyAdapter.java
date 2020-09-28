package com.vitamincarrot.groupsms;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<ItemList> mDataSet;
    public ArrayList<ItemList> checkPos = new ArrayList<>();



    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView listName, listPhoneNumber, listText1, listText2, listText3, listText4, listText5;
        public CheckBox checkBox;

        public MyViewHolder(View v) {
            super(v);
            listName = v.findViewById(R.id.listName);
            listPhoneNumber = v.findViewById(R.id.listNumber);
            listText1 = v.findViewById(R.id.listText1);
            listText2 = v.findViewById(R.id.listText2);
            listText3 = v.findViewById(R.id.listText3);
            listText4 = v.findViewById(R.id.listText4);
            listText5 = v.findViewById(R.id.listText5);
            checkBox = v.findViewById(R.id.checkBox);


        }
    }

    public MyAdapter(ArrayList<ItemList> myDataset) {
        mDataSet = myDataset;
    }


    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.phonenumber_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if (checkPos.size() == 0) {
            holder.checkBox.setChecked(false);

        }
        else {
            holder.checkBox.setChecked(true);
        }
        String name = mDataSet.get(position).getName();
        String phoneNumber = mDataSet.get(position).getPhoneNumber();
        String text1 = mDataSet.get(position).getText1();
        String text2 = mDataSet.get(position).getText2();
        String text3 = mDataSet.get(position).getText3();
        String text4 = mDataSet.get(position).getText4();
        String text5 = mDataSet.get(position).getText5();


        holder.listName.setText(name);
        holder.listPhoneNumber.setText(phoneNumber);
        holder.listText1.setText(text1);
        holder.listText2.setText(text2);
        holder.listText3.setText(text3);
        holder.listText4.setText(text4);
        holder.listText5.setText(text5);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (mDataSet.size() > 0) {

                    if (isChecked) {
                        checkPos.add(mDataSet.get(position));
                        Log.d("MyAdapter_Log", "(onBindViewHolder) Add delete data position : " + mDataSet.get(position) + ":" + position);
                    } else if (checkPos.size() > 0){
                        checkPos.remove(mDataSet.get(position));
                        Log.d("MyAdapter_Log", "(onBindViewHolder) Remove delete data position : " + mDataSet.get(position) + ":" + position);
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

