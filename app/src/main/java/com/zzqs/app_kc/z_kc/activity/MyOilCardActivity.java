package com.zzqs.app_kc.z_kc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.widgets.xlistView.XListView;
import com.zzqs.app_kc.z_kc.adapter.OilCardAdapter;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.entitiy.OilCard;
import com.zzqs.app_kc.z_kc.entitiy.Truck;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;
import com.zzqs.app_kc.z_kc.network.OilCardApiImpl;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by ray on 16/12/14.
 * Class name : MyOilCardActivity
 * Description :我的油卡
 */
public class MyOilCardActivity extends BaseActivity implements XListView.IXListViewListener {
  private static final int ADD_CARD = 100;
  private TextView tvLeft, tvTitle, tvRight;
  private XListView lvOilCards;
  private OilCardAdapter oilCardAdapter;
  private List<OilCard> oilCards;

  @Override

  public void initVariables() {
    oilCards = new ArrayList<>();

  }

  @Override
  public void initViews(Bundle savedInstanceState) {
    setContentView(R.layout.z_kc_act_oil_card);
    tvTitle = (TextView) findViewById(R.id.head_title);
    tvTitle.setText(R.string.my_cards);
    tvLeft = (TextView) findViewById(R.id.head_back);
    tvLeft.setText("");
    tvLeft.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        finish();
      }
    });
    tvRight = (TextView) findViewById(R.id.head_right);
    tvRight.setText(getString(R.string.add_oil_card));
    tvRight.setVisibility(View.VISIBLE);
    tvRight.setOnClickListener(new MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        startActivityForResult(new Intent(mContext, AddOilCardActivity.class), ADD_CARD);
      }
    });
    lvOilCards = (XListView) findViewById(R.id.lvOilCards);
    lvOilCards.setPullRefreshEnable(true);
    lvOilCards.setPullLoadEnable(false);
    lvOilCards.setXListViewListener(this);
    oilCardAdapter = new OilCardAdapter(this, oilCards, false);
    lvOilCards.setAdapter(oilCardAdapter);
    lvOilCards.stopRefresh();
    lvOilCards.stopLoadMore();
    lvOilCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0) {
          String truckId = oilCards.get(position - 1).getTruck_id();
          if (!TextUtils.isEmpty(truckId)) {
            Intent intent = new Intent(mContext, TruckDetailActivity.class);
            intent.putExtra(Truck.TRUCK_ID, truckId);
            startActivity(intent);
          }
        }
      }
    });
  }

  @Override
  public void loadData() {
    getOilCardListByDriver();
  }

  @Override
  public void onRefresh() {
    getOilCardListByDriver();

  }

  @Override
  public void onLoadMore() {

  }

  private void getOilCardListByDriver() {
    safePd.show();
    OilCardApiImpl.getOilCardApiImpl().getOilCardListByDriver(CommonTools.getToken(this), new Subscriber<ErrorInfo>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        e.printStackTrace();
        safePd.dismiss();
        onLoad();
      }

      @Override
      public void onNext(ErrorInfo errorInfo) {
        safePd.dismiss();
        if (errorInfo.getType().equals(ErrorInfo.SUCCESS)) {

          List<OilCard> list = (List<OilCard>) errorInfo.object;
          oilCards.clear();
          oilCards.addAll(list);
          oilCardAdapter.notifyDataSetChanged();
        } else {
          Toast.makeText(mContext, errorInfo.getMessage(), Toast.LENGTH_LONG).show();
        }
        onLoad();
      }
    });
  }

  /**
   * 停止刷新，
   */
  private void onLoad() {
    lvOilCards.stopRefresh();
    lvOilCards.stopLoadMore();
    lvOilCards.setRefreshTime(getString(R.string.xilstview_refreshed));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == ADD_CARD) {
      if (resultCode == RESULT_OK) {
        OilCard oilCard = data.getParcelableExtra(OilCard.OILCARD);
        if (oilCard == null) {
          return;
        }
        System.out.println(oilCard.toString());
        oilCards.add(0, oilCard);
        oilCardAdapter.notifyDataSetChanged();
      }
    }
  }

}
