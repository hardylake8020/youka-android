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
import android.widget.TextView;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ContentData;
import com.zzqs.app_kc.utils.ImageUtil;
import com.zzqs.app_kc.z_kc.activity.PersonInfoActivity;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;

/**
 * Created by ray on 2017/5/17.
 * Class name : RealNameAuthFragment
 * Description :
 */
public class RealNameAuthFragment extends Fragment {
    private View view;
    private TextView tvUserPhone;
    private EditText editUserName, editIdCard;
    private ImageView ivPortrait, ivIdPhoto, ivDrivePhoto;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.z_kc_fm_real_name_auth, container, false);
        initView();
        initData();
        setOnClickListener();
        return view;
    }

    private void initView() {
        tvUserPhone = (TextView) view.findViewById(R.id.tvUserPhone);
        editUserName = (EditText) view.findViewById(R.id.editUserName);
        editIdCard = (EditText) view.findViewById(R.id.editIdCard);
        ivPortrait = (ImageView) view.findViewById(R.id.ivPortrait);
        ivIdPhoto = (ImageView) view.findViewById(R.id.ivIdPhoto);
        ivDrivePhoto = (ImageView) view.findViewById(R.id.ivDrivePhoto);
    }

    private void initData() {
        tvUserPhone.setText(((PersonInfoActivity) getActivity()).user.getPhone());
        if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getNickname())) {
            editUserName.setText(((PersonInfoActivity) getActivity()).user.getNickname());
        }
        if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getId_card_number())) {
            editIdCard.setText(((PersonInfoActivity) getActivity()).user.getId_card_number());
        }
        if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getPhoto())) {
            if (((PersonInfoActivity) getActivity()).user.getPhoto().contains(ContentData.BASE_PICS)) {
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getPhoto(), ivPortrait);
            } else {
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getPhoto(), ivPortrait, true);
            }
        }
        if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getIdCardPhoto())) {
            if (((PersonInfoActivity) getActivity()).user.getIdCardPhoto().contains(ContentData.BASE_PICS)) {
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getIdCardPhoto(), ivIdPhoto);
            } else {
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getIdCardPhoto(), ivIdPhoto, true);
            }
        }
        if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getDrivingIdPhoto())) {
            if (((PersonInfoActivity) getActivity()).user.getDrivingIdPhoto().contains(ContentData.BASE_PICS)) {
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getDrivingIdPhoto(), ivDrivePhoto);
            } else {
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getDrivingIdPhoto(), ivDrivePhoto, true);
            }
        }
    }

    private void setOnClickListener() {

        ivPortrait.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getPhoto())) {
                    ((PersonInfoActivity) getActivity()).showBigImage(((PersonInfoActivity) getActivity()).user.getPhoto(), ivPortrait, "photo");
                } else {
                    ((PersonInfoActivity) getActivity()).openPopupWindow(view, "photo");
                }
            }
        });
        ivIdPhoto.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getIdCardPhoto())) {
                    ((PersonInfoActivity) getActivity()).showBigImage(((PersonInfoActivity) getActivity()).user.getIdCardPhoto(), ivIdPhoto, "idCardPhoto");
                } else {
                    ((PersonInfoActivity) getActivity()).openPopupWindow(view, "idCardPhoto");
                }

            }
        });
        ivDrivePhoto.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getDrivingIdPhoto())) {
                    ((PersonInfoActivity) getActivity()).showBigImage(((PersonInfoActivity) getActivity()).user.getDrivingIdPhoto(), ivDrivePhoto, "drivingIdPhoto");
                } else {
                    ((PersonInfoActivity) getActivity()).openPopupWindow(view, "drivingIdPhoto");
                }

            }
        });

        editUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ((PersonInfoActivity) getActivity()).user.setNickname(editUserName.getText().toString());
            }
        });

        editIdCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ((PersonInfoActivity) getActivity()).user.setId_card_number(editIdCard.getText().toString());
            }
        });

    }

    public void refreshImagePath(String type) {
        switch (type) {
            case "photo":
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getPhoto(), ivPortrait);
                break;
            case "idCardPhoto":
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getIdCardPhoto(), ivIdPhoto);
                break;
            case "drivingIdPhoto":
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getDrivingIdPhoto(), ivDrivePhoto);
                break;
        }
    }
}
