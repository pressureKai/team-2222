package com.jiangtai.count.ui.signIn;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiangtai.count.R;
import com.jiangtai.count.bean.TaskBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DetailsKqAdapter extends RecyclerView.Adapter<DetailsKqAdapter.ViewHolder>   {
    private Context context;
    private List<TaskBean> data;

    public DetailsKqAdapter(Context context,List<TaskBean> data) {
        this.context = context;
        this.data = data;
    }



    @NonNull
    @Override
    public DetailsKqAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.detailsakqdapter, parent, false);

        return new DetailsKqAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DetailsKqAdapter.ViewHolder holder, int position) {
        holder.is_kqname.setText(data.get(position).getTaskName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            private Intent intent;
            private View inflate;
            private String taskId;

            @Override
            public void onClick(View v) {
                taskId = data.get(position).getTaskId();
                intent = new Intent(context, DetailsRwAvtivity.class);
                intent.putExtra("taskId",taskId);
                context.startActivity(intent);


            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView is_kqname;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            is_kqname = itemView.findViewById(R.id.is_kqname);

        }
    }

}

