package com.zzqs.app_kc.z_kc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.entitiy.OilCard;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;
import com.zzqs.app_kc.z_kc.network.OilCardApiImpl;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by ray on 2016/12/24.
 * Class name : AddOilCardActivity
 * Description :
 */
public class AddOilCardActivity extends BaseActivity {
    private TextView tvAddCard, tvLeft, tvTitle;
    private EditText etCardNumber;
    private Spinner spinnerOilCardType;
    private List<String> cardTypeList;
    private ArrayAdapter<String> adapter;

    @Override
    public void initVariables() {
        String[] cardTypes = getResources().getStringArray(R.array.card_type);
        cardTypeList = new ArrayList<>();
        for (int i = 0; i < cardTypes.length; i++) {
            cardTypeList.add(cardTypes[i]);
        }
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.z_kc_act_add_oil_card);
        tvAddCard = (TextView) findViewById(R.id.tvAddCard);
        tvAddCard.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                if (checkConnected()) {
                    addOilCard();
                }
            }
        });
        tvLeft = (TextView) findViewById(R.id.head_back);
        tvLeft.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                finish();
            }
        });
        tvTitle = (TextView) findViewById(R.id.head_title);
        tvTitle.setText(getString(R.string.add_oil_card));
        etCardNumber = (EditText) findViewById(R.id.etCardNumber);
        spinnerOilCardType = (Spinner) findViewById(R.id.spinnerOilCardType);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cardTypeList);
        adapter.setDropDownViewResource(R.layout.view_spinner_item);
        spinnerOilCardType.setAdapter(adapter);
        spinnerOilCardType.setSelection(0);
    }

    @Override
    public void loadData() {

    }

    private void addOilCard() {
        String cardNumber = etCardNumber.getText().toString();
        String typeStr = spinnerOilCardType.getSelectedItem().toString();
        String cardType;
        if (typeStr.equals("油卡")) {
            cardType = "unEtc";
        } else {
            cardType = "etc";
        }
        if (TextUtils.isEmpty(cardNumber)) {
            Toast.makeText(this, "请输入卡券的卡号", Toast.LENGTH_SHORT).show();
            return;
        }
        safePd.show();
        OilCardApiImpl.getOilCardApiImpl().addOilCardByDriver(CommonTools.getToken(this), cardNumber, cardType, new Subscriber<ErrorInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                safePd.dismiss();
            }

            @Override
            public void onNext(ErrorInfo errorInfo) {
                safePd.dismiss();
                if (errorInfo.getType().equals(ErrorInfo.SUCCESS)) {
                    Toast.makeText(mContext, R.string.add_success, Toast.LENGTH_LONG).show();
                    OilCard oilCard = (OilCard) errorInfo.object;
                    Intent data = new Intent();
                    data.putExtra(OilCard.OILCARD, oilCard);
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    Toast.makeText(mContext, errorInfo.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
