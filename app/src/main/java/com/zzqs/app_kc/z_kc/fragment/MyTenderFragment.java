package com.zzqs.app_kc.z_kc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zzqs.app_kc.widgets.xlistView.XListView;

/**
 * Created by lance on 2016/12/23.
 */

public class MyTenderFragment extends Fragment implements XListView.IXListViewListener  {
  private String status;
  public static final String TENDER_STATUS = "tenderStatus";
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    status = getArguments().getString(TENDER_STATUS);
    if (status == null){
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
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override
  public void onRefresh() {

  }

  @Override
  public void onLoadMore() {

  }
}
