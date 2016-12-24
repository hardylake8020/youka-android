package com.zzqs.app_kc.z_kc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.widgets.xlistView.XListView;
import com.zzqs.app_kc.z_kc.adapter.TenderAdapter;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.entitiy.Tender;
import com.zzqs.app_kc.z_kc.network.TenderApiImpl;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

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

  private void getTenderByStatus(final boolean isRefresh) {
    int currentCount = isRefresh ? 0 : tenderList.size();
    TenderApiImpl.getUserApiImpl().getStartedListByDriver(CommonTools.getToken(getContext()), currentCount, 10, status, new Subscriber<ErrorInfo>() {
      @Override
      public void onCompleted() {
      }

      @Override
      public void onError(Throwable e) {
        e.printStackTrace();
        onLoad();
      }

      @Override
      public void onNext(ErrorInfo errorInfo) {
        onLoad();
        if (errorInfo.getType().equals(ErrorInfo.SUCCESS)) {
          List<Tender> list = (List<Tender>) errorInfo.object;
          if (isRefresh) {
            tenderList.clear();
          }
          if (list.size() < 10) {
            lvTenders.setPullLoadEnable(false);
          } else {
            lvTenders.setPullLoadEnable(true);
          }
          tenderList.addAll(list);
          adapter.notifyDataSetChanged();
        } else {
          Toast.makeText(getContext(), errorInfo.getMessage(), Toast.LENGTH_LONG).show();
        }
      }
    });
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
