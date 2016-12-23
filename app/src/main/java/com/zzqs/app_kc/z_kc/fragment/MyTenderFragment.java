package com.zzqs.app_kc.z_kc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.widgets.xlistView.XListView;
import com.zzqs.app_kc.z_kc.adapter.TenderAdapter;
import com.zzqs.app_kc.z_kc.entitiy.Tender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lance on 2016/12/23.
 */

public class MyTenderFragment extends Fragment implements XListView.IXListViewListener {
  private String status;

  private View view;
  XListView lvTenders;
  TenderAdapter adapter;
  private List<Tender> tenderList;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    status = getArguments().getString(Tender.TENDER_STATUS);
    if (status == null) {
      getActivity().finish();
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.z_kc_fm_my_tenders, container, false);
    lvTenders = (XListView) view.findViewById(R.id.lvTenders);
    lvTenders.setPullRefreshEnable(true);
    lvTenders.setPullLoadEnable(false);
    lvTenders.setXListViewListener(this);
    tenderList = new ArrayList<>();
    adapter = new TenderAdapter(getContext(), tenderList, true);
    lvTenders.setAdapter(adapter);
    getTenderByStatus(true);
    return view;
  }

  @Override
  public void onRefresh() {
    getTenderByStatus(true);
  }

  @Override
  public void onLoadMore() {
    getTenderByStatus(false);
  }

  private void getTenderByStatus(boolean isRefresh) {
    //根据状态获取相应标书
  }

  private void onLoad() {
    lvTenders.stopRefresh();
    lvTenders.stopLoadMore();
    lvTenders.setRefreshTime(getString(R.string.xilstview_refreshed));
    if (tenderList.size() >= 10) {
      lvTenders.setPullLoadEnable(true);
    }
  }
}
