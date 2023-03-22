package com.jiangtai.count.ui.signIn.viewpager;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jiangtai.count.R;
import com.jiangtai.count.ui.signIn.DetailsAdapter;
import com.jiangtai.count.ui.signIn.SignlnFragmentUtil;

public class ButFragment extends BaseFragment {

    private RecyclerView xqrec;

    @Override
    protected void initData(View inflate) {
        xqrec = inflate.findViewById(R.id.xqrec);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xqrec.setLayoutManager(linearLayoutManager);
        if (SignlnFragmentUtil.personList1 == null || SignlnFragmentUtil.personList1.size() == 0) {
            DetailsAdapter detailsAdapter = new DetailsAdapter(getContext(), SignlnFragmentUtil.find1, 0);
            xqrec.setAdapter(detailsAdapter);
        } else {
            DetailsAdapter detailsAdapter = new DetailsAdapter(getContext(), SignlnFragmentUtil.personList1, 0);
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
