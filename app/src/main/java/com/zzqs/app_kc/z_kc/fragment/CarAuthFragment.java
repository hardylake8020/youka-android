package com.zzqs.app_kc.z_kc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ContentData;
import com.zzqs.app_kc.utils.ImageUtil;
import com.zzqs.app_kc.z_kc.activity.PersonInfoActivity;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ray on 2017/5/17.
 * Class name : CarAuthFragment
 * Description :
 */
public class CarAuthFragment extends Fragment {
    private View view;
    private EditText editCarNumber;
    private ImageView ivCarPhoto, ivCarNumberPhoto, ivDrivingPhoto;//车辆照片、车牌照片、行驶证照片
    private Spinner spinnerCardType;
    private List<String> truckTypeList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.z_kc_fm_car_auth, container, false);
        String[] truckTypes = getResources().getStringArray(R.array.truck_type);
        truckTypeList = new ArrayList<>();
        for (int i = 0; i < truckTypes.length; i++) {
            truckTypeList.add(truckTypes[i]);
        }
        initView();
        initData();
        setOnClickListener();
        return view;
    }

    private void initView() {
        editCarNumber = (EditText) view.findViewById(R.id.editCarNumber);
        ivCarPhoto = (ImageView) view.findViewById(R.id.ivCarPhoto);
        ivCarNumberPhoto = (ImageView) view.findViewById(R.id.ivCarNumberPhoto);
        ivDrivingPhoto = (ImageView) view.findViewById(R.id.ivDrivingPhoto);
        spinnerCardType = (Spinner) view.findViewById(R.id.spinnerTruckType);
        simpleArrayAdapter adapter = new simpleArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, truckTypeList);
        //设置样式
        adapter.setDropDownViewResource(R.layout.view_spinner_item);
        spinnerCardType.setAdapter(adapter);
        spinnerCardType.setSelection(truckTypeList.size() - 1, true);
    }

    private void initData() {
        if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getTruck_type())) {
            int position = 0;
            for (int i = 0; i < truckTypeList.size(); i++) {
                if (((PersonInfoActivity) getActivity()).user.getTruck_type().equals(truckTypeList.get(i))) {
                    position = i;
                }
            }
            spinnerCardType.setSelection(position, true);
        }
        if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getTruck_number())) {
            editCarNumber.setText(((PersonInfoActivity) getActivity()).user.getTruck_number());
        }
        if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getTruck_photo())) {
            if (((PersonInfoActivity) getActivity()).user.getTruck_photo().contains(ContentData.BASE_PICS)) {
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getTruck_photo(), ivCarPhoto);
            } else {
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getTruck_photo(), ivCarPhoto, true);
            }
        }
        if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getCar_plate_photo())) {
            if (((PersonInfoActivity) getActivity()).user.getCar_plate_photo().contains(ContentData.BASE_PICS)) {
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getCar_plate_photo(), ivCarNumberPhoto);
            } else {
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getCar_plate_photo(), ivCarNumberPhoto, true);
            }
        }
        if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getTravelIdPhoto())) {
            if (((PersonInfoActivity) getActivity()).user.getTravelIdPhoto().contains(ContentData.BASE_PICS)) {
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getTravelIdPhoto(), ivDrivingPhoto);
            } else {
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getTravelIdPhoto(), ivDrivingPhoto, true);
            }
        }
    }

    private void setOnClickListener() {

        ivCarPhoto.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getTruck_photo())) {
                    ((PersonInfoActivity) getActivity()).showBigImage(((PersonInfoActivity) getActivity()).user.getTruck_photo(), ivCarPhoto, "truck_photo");
                } else {
                    ((PersonInfoActivity) getActivity()).openPopupWindow(view, "truck_photo");
                }
            }
        });
        ivCarNumberPhoto.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getCar_plate_photo())) {
                    ((PersonInfoActivity) getActivity()).showBigImage(((PersonInfoActivity) getActivity()).user.getCar_plate_photo(), ivCarNumberPhoto, "car_plate_photo");
                } else {
                    ((PersonInfoActivity) getActivity()).openPopupWindow(view, "car_plate_photo");
                }

            }
        });
        ivDrivingPhoto.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                if (!TextUtils.isEmpty(((PersonInfoActivity) getActivity()).user.getTravelIdPhoto())) {
                    ((PersonInfoActivity) getActivity()).showBigImage(((PersonInfoActivity) getActivity()).user.getTravelIdPhoto(), ivDrivingPhoto, "travelIdPhoto");
                } else {
                    ((PersonInfoActivity) getActivity()).openPopupWindow(view, "travelIdPhoto");
                }

            }
        });

        editCarNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ((PersonInfoActivity) getActivity()).user.setTruck_number(editCarNumber.getText().toString());
            }
        });

    }

    public void refreshImagePath(String type) {
        switch (type) {
            case "truck_photo":
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getTruck_photo(), ivCarPhoto);
                break;
            case "car_plate_photo":
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getCar_plate_photo(), ivCarNumberPhoto);
                break;
            case "travelIdPhoto":
                ImageUtil.showImage(((PersonInfoActivity) getActivity()).user.getTravelIdPhoto(), ivDrivingPhoto);
                break;
        }
    }

    class simpleArrayAdapter<T> extends ArrayAdapter {

        public simpleArrayAdapter(Context context, int resource, List<T> objects) {
            super(context, resource, objects);
        }

        //复写这个方法，使返回的数据没有最后一项
        @Override
        public int getCount() {
            // don't display last item. It is used as hint.
            int count = super.getCount();
            return count > 0 ? count - 1 : count;
        }
    }

    public String getTruckType() {
        String truckType = spinnerCardType.getSelectedItem().toString();
        return truckType;
    }
}
