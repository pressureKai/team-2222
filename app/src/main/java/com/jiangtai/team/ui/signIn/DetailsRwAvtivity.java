package com.jiangtai.team.ui.signIn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jiangtai.team.R;
import com.jiangtai.team.bean.Project;

import org.litepal.LitePal;

import java.util.List;

public class DetailsRwAvtivity extends AppCompatActivity {

    private Intent intent;
    private String taskId;
    private List<Project> projects;
    private RecyclerView xzjhqk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_rw_avtivity);
        intent = getIntent();
        taskId = intent.getStringExtra("taskId");
        projects = LitePal.where("taskId = ?", taskId).find(Project.class);
        xzjhqk = findViewById(R.id.xzjhqk);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xzjhqk.setLayoutManager(linearLayoutManager);
        DetailsewAdapter  detailsewAdapter = new DetailsewAdapter(this, projects);
        xzjhqk.setAdapter(detailsewAdapter);
    }
}