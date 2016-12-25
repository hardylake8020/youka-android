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
import com.zzqs.app_kc.z_kc.adapter.OilCardAdapter;
import com.zzqs.app_kc.z_kc.entitiy.OilCard;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ray on 2016/12/25.
 * Class name : MyOilCardFragment
 * Description :
 */
public class MyOilCardFragment extends Fragment implements XListView.IXListViewListener {
    private String type;
    private View view;
    private XListView lvOilCards;
    private List<OilCard> oilCards;
    private OilCardAdapter oilCardAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        type = getArguments().getString(OilCard.OILCARD_TYPE);
        if (type == null) {
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
        view = inflater.inflate(R.layout.z_kc_fm_my_oilcards, container, false);
        lvOilCards = (XListView) view.findViewById(R.id.lvOilCards);
        lvOilCards.setPullRefreshEnable(true);
        lvOilCards.setPullLoadEnable(false);
        lvOilCards.setXListViewListener(this);
        oilCards = new ArrayList<>();
        oilCardAdapter = new OilCardAdapter(getContext(), oilCards, false);
        lvOilCards.setAdapter(oilCardAdapter);
        return view;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }
}
