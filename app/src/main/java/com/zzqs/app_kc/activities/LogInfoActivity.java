package com.zzqs.app_kc.activities;

import android.os.Bundle;
import android.view.View;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.adapter.LogInfoAdapter;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.LogInfo;
import com.zzqs.app_kc.widgets.xlistView.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lance on 15/4/17.
 */
public class LogInfoActivity extends BaseActivity implements XListView.IXListViewListener {
    private XListView listView;
    private List<LogInfo> logList = null;
    private LogInfoAdapter adapter;
    private BaseDao<LogInfo> logInfoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_location_info);
        logInfoDao = DaoManager.getLogInfoDao(this);
        logList = new ArrayList<LogInfo>();
        logList.addAll(logInfoDao.find(null, null, null, null, null, null, 20 + ""));
        listView = (XListView) findViewById(R.id.listView);
        listView.setPullRefreshEnable(false);
        listView.setPullLoadEnable(true);
        listView.setXListViewListener(this);
        adapter = new LogInfoAdapter((ArrayList<LogInfo>) logList, this);
        listView.setAdapter(adapter);
        findViewById(R.id.deleteLog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logInfoDao.execSql("DELETE FROM log_info where 1=1", null);
                logList.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        String[] maxLogId;
        if (logList.size() > 0) {
            maxLogId = new String[]{logList.get(logList.size() - 1).get_id() + ""};
        } else {
            maxLogId = new String[]{0 + ""};
        }
        logList.addAll(logInfoDao.find(null, "_id>?", maxLogId, null, null, null, 100 + ""));
        onLoad();
    }

    private void onLoad() {
        listView.stopRefresh();
        listView.stopLoadMore();
        adapter.notifyDataSetChanged();
    }
}