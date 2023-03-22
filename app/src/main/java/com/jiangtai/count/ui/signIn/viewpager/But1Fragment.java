package com.jiangtai.count.ui.signIn.viewpager;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jiangtai.count.R;
import com.jiangtai.count.ui.signIn.DetailsAdapter;
import com.jiangtai.count.ui.signIn.SignlnFragmentUtil;

public class But1Fragment extends BaseFragment {

    private RecyclerView xqrec;

    @Override
    protected void initData(View inflate) {
        xqrec = inflate.findViewById(R.id.xqrec);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xqrec.setLayoutManager(linearLayoutManager);
        if (SignlnFragmentUtil.ydkpersonList == null || SignlnFragmentUtil.ydkpersonList.size() == 0) {
            DetailsAdapter detailsAdapter = new DetailsAdapter(getContext(), SignlnFragmentUtil.find1, 1);
            xqrec.setAdapter(detailsAdapter);
        } else {
            DetailsywdAdapter detailsAdapter = new DetailsywdAdapter(getContext(), SignlnFragmentUtil.ydkpersonList, 1);
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
