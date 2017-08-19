package com.zzqs.app_kc.z_kc.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ContentData;
import com.zzqs.app_kc.utils.ImageUtil;
import com.zzqs.app_kc.z_kc.activity.PersonInfoActivity;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;

/**
 * Created by ray on 2017/5/17.
 * Class name : CarAuthFragment
 * Description :银行卡认证
 */
public class BankCardAuthFragment extends Fragment {
    private View view;
    private EditText editBankNumber, editBankUserName, editBankName;
    private ImageView ivBankPhoto;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.z_kc_fm_bank_card_auth, container, false);
        initView();
        initData();
        setOnClickListener();
        return view;
    }

    private void initView() {
        editBankNumber = (EditText) view.findViewById(R.id.editBankNumber);
        editBankUserName = (EditText) view.findViewById(R.id.editBankUserName);
        editBankName = (EditText) view.findViewById(R.id.editBankName);
        ivBankPhoto = (ImageView) view.findViewById(R.id.ivBankPhoto);
    }

    private void initData() {
        if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getBank_number())) {
            editBankNumber.setText(((PersonInfoActivity) getActivity()).user.getBank_number());
        }
        if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getBank_username())) {
            editBankUserName.setText(((PersonInfoActivity) getActivity()).user.getBank_username());
        }
        if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getBank_name())) {
            editBankName.setText(((PersonInfoActivity) getActivity()).user.getBank_name());
        }
        if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getBank_number_photo())) {
            if (((PersonInfoActivity) getActivity()).user.getBank_number_photo().contains(ContentData.BASE_PICS)) {
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getBank_number_photo(), ivBankPhoto);
            } else {
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getBank_number_photo(), ivBankPhoto, true);
            }
        }
    }


    private void setOnClickListener() {
        ivBankPhoto.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getBank_number_photo())) {
                    ((PersonInfoActivity) getActivity()).showBigImage(((PersonInfoActivity) getActivity()).user.getBank_number_photo(), ivBankPhoto, "bank_number_photo");
                } else {
                    ((PersonInfoActivity) getActivity()).openPopupWindow(view, "bank_number_photo");
                }
            }
        });
        editBankNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ((PersonInfoActivity) getActivity()).user.setBank_number(editBankNumber.getText().toString());
            }
        });

        editBankUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ((PersonInfoActivity) getActivity()).user.setBank_username(editBankUserName.getText().toString());
            }
        });

        editBankName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ((PersonInfoActivity) getActivity()).user.setBank_name(editBankName.getText().toString());
            }
        });
    }

    public void refreshImagePath() {
        ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getBank_number_photo(), ivBankPhoto);
    }
}
