package com.jiangtai.count.ui.signIn;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiangtai.count.R;
import com.jiangtai.count.bean.Project;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DetailsewAdapter extends RecyclerView.Adapter<DetailsewAdapter.ViewHolder>   {
    private Context context;
    private List<Project> data;

    public DetailsewAdapter(Context context,List<Project> data) {
        this.context = context;
        this.data = data;
    }



    @NonNull
    @Override
    public DetailsewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_sign_in_kq, parent, false);

        return new DetailsewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DetailsewAdapter.ViewHolder holder, int position) {

        holder.project_name.setText(data.get(position).getProjectName());
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView project_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            project_name = itemView.findViewById(R.id.project_name);

        }
    }

}
