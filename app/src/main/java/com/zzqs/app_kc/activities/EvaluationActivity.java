package com.zzqs.app_kc.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.adapter.EvaluationAdapter;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.entity.Evaluation;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.CircleImageView;
import com.zzqs.app_kc.widgets.SafeProgressDialog;
import com.zzqs.app_kc.widgets.xlistView.XListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lance on 15/11/2.
 */
public class EvaluationActivity extends BaseActivity implements View.OnClickListener, XListView.IXListViewListener {
    CircleImageView headPortrait;
    TextView username, allEvaluation, goodEvaluation, midEvaluation, badEvaluation,back;
    XListView xListView;
    private EvaluationAdapter evaluationAdapter;
    private List<Evaluation> checkedTagList = null;
    private User user;
    private List<TextView> tags = null;
    SafeProgressDialog progressDialog;
    private int allOrdersNum, allEvaluationNum, goodEvaluationNum, midEvaluationNum, badEvaluationNum;
    private int checkType = 0;
    private int checkNum = 0;
    private boolean isCheckType = true;
    private boolean isFirstRefresh = true;
    private static final int REFRESH_COUNT = 1;
    private static final int REFRESH_EVALUATION = 2;
    private static final int LOAD_MORE_EVALUATION = 3;
    private ImageView iv_evaluation_grade;
    private TextView tv_rate,tv_evaluation_count;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == REFRESH_COUNT) {
                tv_evaluation_count.setText(allOrdersNum + "");
                allEvaluationNum = goodEvaluationNum + midEvaluationNum + badEvaluationNum;
                int rate = (int) (((double) goodEvaluationNum / (double) allEvaluationNum) * 100);
                if(allEvaluationNum<10){
                    tv_rate.setText("0%");
                }else {
                    tv_rate.setText(rate + "%");
                }
                allEvaluation.setText(getString(R.string.view_tv_all_evaluates) + allEvaluationNum);
                goodEvaluation.setText(getString(R.string.view_tv_good_evaluates) + goodEvaluationNum);
                midEvaluation.setText(getString(R.string.view_tv_mid_evaluates) + midEvaluationNum);
                badEvaluation.setText(getString(R.string.view_tv_bad_evaluates) + badEvaluationNum);
                setRate(rate);
            } else if (msg.what == REFRESH_EVALUATION) {
                isCheckType = true;
                onLoad();
                evaluationAdapter.notifyDataSetChanged();
            } else if (msg.what == LOAD_MORE_EVALUATION) {
                isCheckType = true;
                xListView.stopLoadMore();
                evaluationAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_comment);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    private void initView() {
        progressDialog = new SafeProgressDialog(this);
        progressDialog.setTitle("");
        progressDialog.setMessage(getString(R.string.prompt_dl_loading_evaluation));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(this);
        headPortrait = (CircleImageView) findViewById(R.id.head_portrait);
        username = (TextView) findViewById(R.id.username);
        allEvaluation = (TextView) findViewById(R.id.all_evaluation_num);
        allEvaluation.setOnClickListener(this);
        goodEvaluation = (TextView) findViewById(R.id.good_evaluation_num);
        goodEvaluation.setOnClickListener(this);
        midEvaluation = (TextView) findViewById(R.id.middle_evaluation_num);
        midEvaluation.setOnClickListener(this);
        badEvaluation = (TextView) findViewById(R.id.bad_evaluation_num);
        badEvaluation.setOnClickListener(this);
        xListView = (XListView) findViewById(R.id.xListView);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(false);
        xListView.setXListViewListener(this);
        checkedTagList = new ArrayList<>();
        evaluationAdapter = new EvaluationAdapter(checkedTagList, this);
        xListView.setAdapter(evaluationAdapter);

        iv_evaluation_grade= (ImageView) findViewById(R.id.iv_evaluation_grade);
        tv_rate= (TextView) findViewById(R.id.tv_rate);
        tv_evaluation_count= (TextView) findViewById(R.id.tv_evaluation_count);
    }

    private void initData() {
        user = ZZQSApplication.getInstance().getUser();
            if (!StringTools.isEmp(user.getLocalPhoto())) {
                final ImageLoader imageLoader = ImageLoader.getInstance();
                final File file = new File(user.getLocalPhoto());
                if (file.exists()) {
                    imageLoader.displayImage("file://" + file.getAbsolutePath(), headPortrait);
                }
            }
            if (!StringTools.isEmp(user.getNickname())) {
                username.setText(user.getNickname() + "");
            } else if (!StringTools.isEmp(user.getUsername())) {
                username.setText(user.getUsername() + "");
            }
        tags = new ArrayList<>();
        tags.add(allEvaluation);
        tags.add(goodEvaluation);
        tags.add(midEvaluation);
        tags.add(badEvaluation);
        allEvaluation.setSelected(true);
        refreshEvaluation();
    }

    private void setNumber(Object object) {
        JSONObject obj = (JSONObject) object;
        try {
            if (obj.has("orderCount")) {
                allOrdersNum = obj.getInt("orderCount");
            }
            if (obj.has("evaluationCount")) {
                JSONObject evaluationObj = obj.getJSONObject("evaluationCount");
                goodEvaluationNum = evaluationObj.getInt("good");
                midEvaluationNum = evaluationObj.getInt("general");
                badEvaluationNum = evaluationObj.getInt("bad");
                allEvaluationNum = goodEvaluationNum + midEvaluationNum + badEvaluationNum;
            }
            if (isFirstRefresh) {
                isFirstRefresh = false;
                checkNum = allEvaluationNum;
            }
            handler.sendEmptyMessage(REFRESH_COUNT);
        } catch (JSONException e) {
            e.printStackTrace();
            ZZQSApplication.getInstance().CrashToLogin();
        }
    }

    private void selectTag(int viewId, int checkType) {
        for (TextView v : tags) {
            if (v.getId() == viewId) {
                v.setSelected(true);
            } else {
                v.setSelected(false);
            }
        }
        this.checkType = checkType;
        getEvaluationOfType();
    }

    private void setRate(int rate) {
        if (allOrdersNum <= 10) {
            iv_evaluation_grade.setBackgroundResource(R.drawable.tiro);
            ZZQSApplication.getInstance().getUser().setRate(-1);
            ZZQSApplication.getInstance().updateUser(ZZQSApplication.getInstance().getUser());
        } else {
            ZZQSApplication.getInstance().getUser().setRate(rate);
            DaoManager.getUserDao(this).update(ZZQSApplication.getInstance().getUser());
            if (rate < 60) {
                iv_evaluation_grade.setBackgroundResource(R.drawable.medal_bronze);
            } else if (rate < 80 && rate >= 60) {
                iv_evaluation_grade.setBackgroundResource(R.drawable.medal_silver);

            } else if (rate >= 80) {
                iv_evaluation_grade.setBackgroundResource(R.drawable.medal_gold);

            }
        }
    }

    @Override
    public void onClick(View view) {
        int check = checkType;
        switch (view.getId()) {
            case R.id.all_evaluation_num:
                check = Evaluation.ALL;
                checkNum = allEvaluationNum;
                break;
            case R.id.good_evaluation_num:
                check = Evaluation.GOOD;
                checkNum = goodEvaluationNum;
                break;
            case R.id.middle_evaluation_num:
                check = Evaluation.MID;
                checkNum = midEvaluationNum;
                break;
            case R.id.bad_evaluation_num:
                check = Evaluation.BAD;
                checkNum = badEvaluationNum;
                break;
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
        if (isCheckType) {
            if (check != checkType) {
                selectTag(view.getId(), check);
            }
        }
    }

    private void getEvaluationOfType() {
        RestAPI.getInstance(getApplicationContext()).getEvaluations(10, null, checkType, new RestAPI.RestResponse() {
            @Override
            public void onSuccess(Object object) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (object != null) {
                    List<Evaluation> list = (List<Evaluation>) object;
                    checkedTagList.clear();
                    checkedTagList.addAll(list);
                }
                if (checkNum > checkedTagList.size()) {
                    xListView.setPullLoadEnable(true);
                } else {
                    xListView.setPullLoadEnable(false);
                }
                handler.sendEmptyMessage(REFRESH_EVALUATION);
            }

            @Override
            public void onFailure(Object object) {
                onLoad();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void refreshEvaluation() {
        isCheckType = false;
        RestAPI.getInstance(getApplicationContext()).getEvaluationNumber(new RestAPI.RestResponse() {
            @Override
            public void onSuccess(Object object) {
                if (object != null) {
                    setNumber(object);
                    getEvaluationOfType();
                }
            }

            @Override
            public void onFailure(Object object) {
                onLoad();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        refreshEvaluation();
    }

    @Override
    public void onLoadMore() {
        if (checkNum > checkedTagList.size()) {
            isCheckType = false;
            RestAPI.getInstance(getApplicationContext()).getEvaluationNumber(new RestAPI.RestResponse() {

                @Override
                public void onSuccess(Object object) {
                    if (object != null) {
                        setNumber(object);
                    }
                    RestAPI.getInstance(getApplicationContext()).getEvaluations(10, checkedTagList.get(checkedTagList.size() - 1).getCreateTime(), checkType, new RestAPI.RestResponse() {
                        @Override
                        public void onSuccess(Object object) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            List<Evaluation> list = (List<Evaluation>) object;
                            checkedTagList.addAll(list);
                            handler.sendEmptyMessage(LOAD_MORE_EVALUATION);
                        }

                        @Override
                        public void onFailure(Object object) {
                            isCheckType = true;
                            xListView.stopLoadMore();
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });
                }

                @Override
                public void onFailure(Object object) {
                    isCheckType = true;
                    xListView.stopLoadMore();
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            });
        } else {
            xListView.stopLoadMore();
            xListView.setPullLoadEnable(false);
            Toast.makeText(this, R.string.prompt_no_more_evaluation, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    /**
     * 停止刷新，
     */
    private void onLoad() {
        xListView.stopRefresh();
        xListView.stopLoadMore();
        xListView.setRefreshTime(getString(R.string.xilstview_refreshed));
    }
}
