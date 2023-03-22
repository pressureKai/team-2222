package com.jiangtai.count.ui.signIn.viewpager;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jiangtai.count.R;
import com.jiangtai.count.ui.signIn.DetailsAdapter;
import com.jiangtai.count.ui.signIn.SignlnFragmentUtil;

import java.util.ArrayList;

public class But2Fragment extends BaseFragment {

    private RecyclerView xqrec;

    @Override
    protected void initData(View inflate) {
        xqrec = inflate.findViewById(R.id.xqrec);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xqrec.setLayoutManager(linearLayoutManager);
        if (SignlnFragmentUtil.wdkpersonList == null || SignlnFragmentUtil.wdkpersonList.size() == 0) {
            DetailsAdapter detailsAdapter = new DetailsAdapter(getContext(), SignlnFragmentUtil.find1, 2);
            xqrec.setAdapter(detailsAdapter);
        } else {
            ArrayList<String> a = new ArrayList<>();

            for (int i = 0; i < SignlnFragmentUtil.find1.size(); i++) {
                if (SignlnFragmentUtil.find1.get(i).getSex() != "1") {
                    a.add(SignlnFragmentUtil.find1.get(i).getName());
                }
            }
            DetailsywdAdapter detailsAdapter = new DetailsywdAdapter(getContext(), a, 2);
            xqrec.setAdapter(detailsAdapter);
        }
    }

    @Override
    protected void initView(View inflate) {

    }

    @Override
    protected int initLayout() {
        return R.layout.but1fragment;
    }
}
