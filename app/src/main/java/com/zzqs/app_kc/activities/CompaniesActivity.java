package com.zzqs.app_kc.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.adapter.CompaniesAdapter;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.Company;
import com.zzqs.app_kc.entity.Events;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.utils.CommonFiled;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.widgets.DialogView;
import com.zzqs.app_kc.widgets.xlistView.XListView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by lance on 15/3/23.
 */
public class CompaniesActivity extends BaseActivity implements XListView.IXListViewListener, View.OnClickListener {
    XListView xListView;
    TextView head_message,head_back;
    private CompaniesAdapter companiesAdapter;
    private BaseDao<Company> companyDao;
    private ArrayList<Company> companyList;
    private User user;
    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CommonFiled.NEW_INVITE_COMPANY)) {
                Company company = intent.getParcelableExtra(Company.COMPANY);
                companyList.add(0, company);
                companiesAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_companies);
        Events.OtherEvent disMissRedHind = new Events.OtherEvent("true", Events.INVITE_TIP);
        EventBus.getDefault().post(disMissRedHind);
        init();
    }



    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void init() {
        if (CommonTools.getNewMessage(CommonFiled.COMPANY_MSG, getApplicationContext())) {
            CommonTools.setNewMessage(CommonFiled.COMPANY_MSG, false, getApplicationContext());
        }
        user = ZZQSApplication.getInstance().getUser();
        companyDao = DaoManager.getCompanyDao(getApplicationContext());
        companyList = new ArrayList<Company>();
        companyList.addAll(companyDao.find(null, "username=?", new String[]{user.getUsername()}, null, null, "_id desc", null));
        xListView = (XListView) findViewById(R.id.XListView);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(false);
        xListView.setXListViewListener(this);
        xListView.setLongClickable(true);
        head_back = (TextView) findViewById(R.id.head_back);
        head_message = (TextView) findViewById(R.id.head_title);
        head_message.setText(R.string.view_tv_cooperation_company);
        companiesAdapter = new CompaniesAdapter(companyList, companyDao, this);
        xListView.setAdapter(companiesAdapter);
        head_back.setOnClickListener(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(CommonFiled.NEW_INVITE_COMPANY);
    }


    private boolean update = false;

    @Override
    public void onRefresh() {
        RestAPI.getInstance(getApplicationContext()).getCompanies(new RestAPI.RestResponse() {
            @Override
            public void onSuccess(Object object) {
                final List<Company> list = (List<Company>) object;
                if (list.size() > 0 && null != companiesAdapter) {
                    for (Company company : list) {
                        company.setUsername(user.getUsername());
                        List<Company> localList = companyDao.find(null, "company_id=?", new String[]{company.getCompany_id() + ""}, null, null, null, null);
                        if (null == localList || localList.size() == 0) {
                            companyDao.insert(company);
                            companyList.add(0, company);
                        } else {
                            company.set_id(localList.get(0).get_id());
                            companyDao.update(company);
                            update = true;
                        }
                    }
                }
                if (update) {
                    companyList.clear();
                    companyList.addAll(companyDao.find(null, "username=?", new String[]{user.getUsername()}, null, null, "_id desc", null));
                }
                companiesAdapter.notifyDataSetChanged();
                onLoad();
            }

            @Override
            public void onFailure(Object object) {
                if (object.toString().equals("disconnected")) {
                    DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, getString(R.string.prompt_dl_other_equipment_login_title), getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            ZZQSApplication.getInstance().clearUser(CompaniesActivity.this);
                            ZZQSApplication.getInstance().cleanAllActivity();
                            finish();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                    });
                } else {
                    Toast.makeText(CompaniesActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                    onLoad();
                }
            }
        });

    }

    @Override
    public void onLoadMore() {

    }

    /**
     * 停止刷新，
     */
    private void onLoad() {
        xListView.stopRefresh();
        xListView.stopLoadMore();
        xListView.setRefreshTime(getString(R.string.xilstview_refreshed));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_back:
                finish();
                break;
        }
    }
}
