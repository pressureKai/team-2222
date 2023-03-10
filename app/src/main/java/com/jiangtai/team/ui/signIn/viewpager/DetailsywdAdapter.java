package com.jiangtai.team.ui.signIn.viewpager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiangtai.team.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DetailsywdAdapter extends RecyclerView.Adapter<DetailsywdAdapter.ViewHolder>   {
    private Context context;
    private ArrayList<String>  data;
    private  int type;

    public DetailsywdAdapter(Context context, ArrayList<String> data,int type) {
        this.context = context;
        this.data = data;
        this.type = type;
    }



    @NonNull
    @Override
    public DetailsywdAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.detailsadapter, parent, false);

        return new DetailsywdAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DetailsywdAdapter.ViewHolder holder, int position) {

if (type==1){
    holder.is_qd.setText("已签到");
    holder.is_name.setText(data.get(position));
}else{
    holder.is_qd.setText("未签到");
    holder.is_name.setText(data.get(position));
}




    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView is_qd,is_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            is_qd = itemView.findViewById(R.id.is_qd);
            is_name = itemView.findViewById(R.id.is_name);

        }
    }

}
