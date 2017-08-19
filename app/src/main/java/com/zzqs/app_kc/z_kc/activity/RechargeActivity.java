package com.zzqs.app_kc.z_kc.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;
import com.switfpass.pay.handle.PayHandlerManager;
import com.switfpass.pay.utils.MD5;
import com.switfpass.pay.utils.SignUtils;
import com.switfpass.pay.utils.Util;
import com.switfpass.pay.utils.XmlUtils;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Date;

import cz.msebera.android.httpclient.NameValuePair;


/**
 * Created by ray on 2017/6/21.
 * Class name : RechargeActivity
 * Description :充值页面
 */
public class RechargeActivity extends BaseActivity {
    String TAG = "RechargeActivity";
    public static final String RECHARGE = "recharge";
    public static final String WITHDRAW = "withdraw";
    private EditText editMoney;
    private TextView tvSubmit, tvLeft, tvTitle;
    RelativeLayout rlHead;
    private String tag;
    public Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case PayHandlerManager.PAY_H5_FAILED: //失败，原因如有（商户未开通[pay.weixin.wappay]支付类型）等
                    Log.i("RechargeActivity", "" + msg.obj);
                    break;
                case PayHandlerManager.PAY_H5_SUCCES: //成功
                    Log.i("RechargeActivity", "" + msg.obj);
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void initVariables() {
        tag = getIntent().getStringExtra("Tag");
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.z_kc_act_cash_operation);
        editMoney = (EditText) findViewById(R.id.editMoney);
        tvSubmit = (TextView) findViewById(R.id.tvSubmit);
        rlHead = (RelativeLayout) findViewById(R.id.rlDefaultHeadParent);
        tvLeft = (TextView) findViewById(R.id.head_back);
        tvLeft.setText("");
        tvLeft.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                finish();
            }
        });
        tvTitle = (TextView) findViewById(R.id.head_title);
        if (tag.equals(RECHARGE)) {//充值
            tvTitle.setText(R.string.deposit_recharge);
            rlHead.setBackgroundResource(R.color.green);
            tvSubmit.setBackgroundResource(R.drawable.radius_5dp_col_green);
            tvSubmit.setText(R.string.recharge_now);
        } else {//提现
            tvTitle.setText(R.string.deposit_withdraw);
            rlHead.setBackgroundResource(R.color.orange);
            tvSubmit.setText(R.string.withdraw_now);
            tvSubmit.setBackgroundResource(R.drawable.radius_5dp_col_orange);

        }
        tvSubmit.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                if (TextUtils.isEmpty(editMoney.getText().toString())) {
                    showToast("请输入具体金额", Toast.LENGTH_SHORT);
                    return;
                }
                switch (tag) {
                    case RECHARGE:
                        new GetPrepayIdTask().execute();
                        break;
                    case WITHDRAW:
                        break;
                }
            }
        });
    }

    private String genSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        int i = 0;
        for (; i < params.size() - 1; i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append(params.get(i).getName());
        sb.append('=');
        sb.append(params.get(i).getValue());

        String sha1 = Util.sha1(sb.toString());
        Log.d(TAG, "genSign, sha1 = " + sha1);
        return sha1;
    }

    private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {

        private ProgressDialog dialog;

        private String accessToken;

        public GetPrepayIdTask(String accessToken) {
            this.accessToken = accessToken;
        }

        public GetPrepayIdTask() {
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(RechargeActivity.this,
                    getString(R.string.app_tip),
                    getString(R.string.getting_prepayid));
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {
            if (dialog != null) {
                dialog.dismiss();
            }
            if (result == null) {
                Toast.makeText(RechargeActivity.this, getString(R.string.get_prepayid_fail), Toast.LENGTH_LONG).show();
            } else {
                if (result.get("status").equalsIgnoreCase("0")) // 成功
                {

                    Toast.makeText(RechargeActivity.this, R.string.get_prepayid_succ, Toast.LENGTH_LONG).show();
                    RequestMsg msg = new RequestMsg();
                    msg.setTokenId(result.get("token_id"));
                    //微信
                    msg.setTradeType(MainApplication.WX_APP_TYPE);
                    msg.setAppId("wx2a5538052969956e");//wxd3a1cdf74d0c41b3
                    PayPlugin.unifiedAppPay(RechargeActivity.this, msg);
                } else {
                    Toast.makeText(RechargeActivity.this, getString(R.string.get_prepayid_fail), Toast.LENGTH_LONG)
                            .show();
                }

            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Map<String, String> doInBackground(Void... params) {
            // 统一预下单接口
            //            String url = String.format("https://api.weixin.qq.com/pay/genprepay?access_token=%s", accessToken);
            String url = "https://pay.swiftpass.cn/pay/gateway";
            //            String entity = getParams();

            String entity = getParams();

            Log.d(TAG, "doInBackground, url = " + url);
            Log.d(TAG, "doInBackground, entity = " + entity);

//            GetPrepayIdResult result = new GetPrepayIdResult();

            byte[] buf = Util.httpPost(url, entity);
            if (buf == null || buf.length == 0) {
                return null;
            }
            String content = new String(buf);
            Log.d(TAG, "doInBackground, content = " + content);
//            result.parseFrom(content);
            try {
                return XmlUtils.parse(content);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ssddd");

    /**
     * 组装参数
     * <功能详细描述>
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    private String getParams() {

        Map<String, String> params = new HashMap<String, String>();
        params.put("body", "保证金"); // 商品名称
        params.put("service", "unified.trade.pay"); // 支付类型
        params.put("version", "2.0"); // 版本
        params.put("mch_id", "755437000006"); // 威富通商户号
        //        params.put("mch_id", mchId.getText().toString()); // 威富通商户号
        params.put("notify_url", " "); // 后台通知url
        params.put("nonce_str", genNonceStr()); // 随机数money, body, mchId, notifyUrl, orderNo, signKey, appId, seller_id, credit_pay;
//        String out_trade_no = genOutTradNo();
        String out_trade_no = dateFormat.format(new Date()).toString();
        params.put("out_trade_no", out_trade_no); //订单号
        Log.i("hehui", "out_trade_no-->" + out_trade_no);
        params.put("mch_create_ip", "127.0.0.1"); // 机器ip地址
        params.put("sub_appid", "wx2a5538052969956e"); //
        params.put("total_fee", editMoney.getText().toString()); // 总金额
        params.put("device_info", "android_sdk"); // 手Q反扫这个设备号必须要传1ff9fe53f66189a6a3f91796beae39fe
        params.put("limit_credit_pay", "1"); // 是否禁用信用卡支付， 0：不禁用（默认），1：禁用
        String sign = createSign("7daa4babae15ae17eee90c9e", params); // 9d101c97133837e13dde2d32a5054abb 威富通密钥

        params.put("sign", sign); // sign签名

        return XmlUtils.toXml(params);
    }

    public static String GetappId() {
        String appid = "wx2a5538052969956e";
        return appid;
    }

    public String createSign(String signKey, Map<String, String> params) {
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        SignUtils.buildPayParams(buf, params, false);
        buf.append("&key=").append(signKey);
        String preStr = buf.toString();
        String sign = "";
        // 获得签名验证结果
        try {
            sign = MD5.md5s(preStr).toUpperCase();
        } catch (Exception e) {
            sign = MD5.md5s(preStr).toUpperCase();
        }
        return sign;
    }

    private String genOutTradNo() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private String createSign(Map<String, String> params) {
        Log.i("hehui", "params-->" + params.toString());
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        buildPayParams(buf, params, false);
        buf.append("&key=" + "7daa4babae15ae17eee90c9e");
        String preStr = buf.toString();
        return MD5.md5s(preStr).toUpperCase();
    }

    public void buildPayParams(StringBuilder sb, Map<String, String> payParams, boolean encoding) {
        List<String> keys = new ArrayList<String>(payParams.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            sb.append(key).append("=");
            if (encoding) {
                sb.append(urlEncode(payParams.get(key)));
            } else {
                sb.append(payParams.get(key));
            }
            sb.append("&");
        }
        sb.setLength(sb.length() - 1);
    }

    public String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (Throwable e) {
            return str;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        //        Toast.makeText(getApplicationContext(), "pay_result-->" + data.getStringExtra("pay_result"), 0).show();
    }

    @Override
    public void loadData() {

    }
}
