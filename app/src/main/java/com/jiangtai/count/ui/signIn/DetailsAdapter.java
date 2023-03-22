package com.jiangtai.count.ui.signIn;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiangtai.count.R;
import com.jiangtai.count.bean.Person;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder>   {
    private Context context;
    private  List<Person> data;
    private  int type;

    public DetailsAdapter(Context context,List<Person> data,int type) {
        this.context = context;
        this.data = data;
        this.type = type;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.detailsadapter, parent, false);

        return new DetailsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        if (type==0){
            if (data.get(position).getSex().equals("1")){
                holder.is_qd.setText("已签到");
            }else{
                holder.is_qd.setText("未签到");
            }

            holder.is_name.setText(data.get(position).getName());
        }else if (type==1){
            if (data.get(position).getSex().equals("1")){
                holder.is_qd.setText("已签到");
                holder.is_name.setText(data.get(position).getName());
            }
        }else{
            if (data.get(position).getSex()!="1"){
                holder.is_qd.setText("未签到");
                holder.is_name.setText(data.get(position).getName());
            }
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
