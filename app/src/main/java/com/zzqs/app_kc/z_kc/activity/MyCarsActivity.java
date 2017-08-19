package com.zzqs.app_kc.z_kc.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.z_kc.adapter.CarAdapter;
import com.zzqs.app_kc.z_kc.adapter.DriverAdapter;
import com.zzqs.app_kc.z_kc.entitiy.Driver;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.entitiy.Tender;
import com.zzqs.app_kc.z_kc.entitiy.Truck;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;
import com.zzqs.app_kc.z_kc.network.DriverApiImpl;
import com.zzqs.app_kc.z_kc.network.TruckApiImpl;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by lance on 2016/12/4.
 */

public class MyCarsActivity extends BaseActivity implements PopupWindow.OnDismissListener {
    TextView tvLeft, tvTitle, tvRight, tvBottom, tvCancel;
    ListView lvCars, lvDrives;
    CarAdapter carAdapter;
    List<Truck> truckList;
    public static final String IS_SELECT_CAR = "isSelectCar";
    private boolean isSelectCar;
    public static final int TO_ADD_CAR = 100;
    private Tender tender;
    private RelativeLayout rlSearch;
    private PopupWindow popupWindow;
    private int navigationHeight;
    private EditText editSearchDriver;
    private ImageView ivDelete;
    private DriverAdapter driverAdapter;
    private List<Driver> driverList;
    private boolean popupWindowIsShow = false;

    @Override
    public void initVariables() {
        isSelectCar = getIntent().getBooleanExtra(IS_SELECT_CAR, false);
        tender = getIntent().getParcelableExtra("tender");
        truckList = new ArrayList<>();
        //PopupWindow相关
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        navigationHeight = getResources().getDimensionPixelSize(resourceId);

    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.z_kc_act_my_cars);
        tvLeft = (TextView) findViewById(R.id.head_back);
        tvLeft.setText("");
        tvLeft.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                finish();
            }
        });
        tvTitle = (TextView) findViewById(R.id.head_title);
        tvRight = (TextView) findViewById(R.id.head_right);
        tvBottom = (TextView) findViewById(R.id.tvBottom);
        rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
        if (isSelectCar) {
            tvTitle.setText(R.string.distribution_car);
            tvBottom.setText(R.string.distribution_car);
            rlSearch.setVisibility(View.GONE);
        } else {
            tvRight.setText(R.string.add);
            tvTitle.setText(R.string.my_cars);
            tvBottom.setVisibility(View.GONE);
            tvRight.setVisibility(View.VISIBLE);
        }
        tvRight.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                startActivity(new Intent(MyCarsActivity.this, AddTruckActivity.class));
            }
        });

        tvBottom.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                if (isSelectCar) {//分配车辆
                    if (!hasCarChoise()) {
                        Toast.makeText(MyCarsActivity.this, getString(R.string.need_choice_car), Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent intent = new Intent(mContext, ChoiceOilCardActivity.class);
                    Truck truck = new Truck();
                    for (Truck truck1 : truckList) {
                        if (truck1.isSelect()) {
                            truck = truck1;
                        }
                    }
                    if (!truck.getTruck_type().equals(tender.getTruck_type())) {
                        showToast(getString(R.string.mismatched),Toast.LENGTH_LONG);
                        return;
                    }
                    intent.putExtra("truck", truck);
                    intent.putExtra("tender", tender);
                    startActivity(intent);
                }
            }
        });
        lvCars = (ListView) findViewById(R.id.lvCars);
        carAdapter = new CarAdapter(this, truckList, isSelectCar);
        lvCars.setAdapter(carAdapter);
        lvCars.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Truck truck = truckList.get(position);
                if (truck.isSelect()) {
                    return;
                }
                if (isSelectCar) {
                    for (Truck truck1 : truckList) {
                        truck1.setSelect(false);
                    }
                    truck.setSelect(true);
                    carAdapter.notifyDataSetChanged();
                } else {
                    Intent intent = new Intent(mContext, TruckDetailActivity.class);
                    Driver driver = truck.getDriver();
                    driver.setType(Driver.OWN);
                    intent.putExtra(Driver.DRIVER, driver);
                    startActivity(intent);
                }
            }
        });
        rlSearch.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                openPopupWindow(view);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkConnected()) {
            getTrucks();
            if (popupWindowIsShow) {
                String keyWord = editSearchDriver.getText().toString();
                searcheDriver(keyWord, true);
            }
        }
    }

    @Override
    public void loadData() {

    }

    private void getTrucks() {
        safePd.setMessage(getString(R.string.loading));
        safePd.show();
        TruckApiImpl.getTruckApiImpl().getListByDriver(CommonTools.getToken(this), new Subscriber<ErrorInfo>() {
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
                    List<Truck> list = (List<Truck>) errorInfo.object;
//                    if (isSelectCar) {
//                        Iterator<Truck> it = list.iterator();
//                        while (it.hasNext()) {
//                            Truck truck = it.next();
//                            if (!TextUtils.isEmpty(truck.getCard_id())) {
//                                it.remove();
//                            }
//                        }
//                    }
                    truckList.clear();
                    truckList.addAll(list);
                    carAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, errorInfo.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == TO_ADD_CAR) {
                Truck truck = data.getParcelableExtra(Truck.TRUCK);
                if (truck == null) {
                    return;
                }
                truckList.add(0, truck);
                carAdapter.notifyDataSetChanged();
            }
        }
    }

    private boolean hasCarChoise() {
        boolean result = false;
        for (Truck truck : truckList) {
            if (truck.isSelect()) {
                result = true;
            }
        }
        return result;
    }

    private void openPopupWindow(View v) {
        //防止重复按按钮
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        //设置PopupWindow的View
        View view = LayoutInflater.from(this).inflate(R.layout.view_popupwindow_search, null);
        popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        //设置背景,这个没什么效果，不添加会报错
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //设置点击弹窗外隐藏自身
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        //设置动画
        popupWindow.setAnimationStyle(R.style.PopupWindow);
        //设置位置
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, navigationHeight);
        //设置消失监听
        popupWindow.setOnDismissListener(this);
        //设置PopupWindow的View点击事件
        setOnPopupViewClick(view);
        //设置背景色
        setBackgroundAlpha(0.5f);
        popupWindowIsShow = true;
    }

    //设置屏幕背景透明效果
    public void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        if (alpha == 1) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        getWindow().setAttributes(lp);
    }

    private void setOnPopupViewClick(View view) {
        driverList = new ArrayList<>();
        driverAdapter = new DriverAdapter(this, driverList);
        tvCancel = (TextView) view.findViewById(R.id.tvCancel);
        editSearchDriver = (EditText) view.findViewById(R.id.editSearchDriver);
        ivDelete = (ImageView) view.findViewById(R.id.ivDelete);
        lvDrives = (ListView) view.findViewById(R.id.lvDrives);
        lvDrives.setAdapter(driverAdapter);
        lvDrives.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Driver driver = driverList.get(position);
                driver.setType(Driver.SEARCHE);
                Intent intent = new Intent(mContext, TruckDetailActivity.class);
                intent.putExtra(Driver.DRIVER, driver);
                startActivity(intent);
            }
        });
        tvCancel.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                popupWindow.dismiss();
            }
        });
        ivDelete.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                editSearchDriver.setText("");
                driverList.clear();
                driverAdapter.notifyDataSetChanged();
            }
        });
        editSearchDriver.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!checkConnected()) {
                    return false;
                }
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED && event != null) {
                    safePd.setMessage(getString(R.string.searching));
                    safePd.show();
                    String keyWord = editSearchDriver.getText().toString();
                    searcheDriver(keyWord, false);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDismiss() {
        popupWindowIsShow = false;
        setBackgroundAlpha(1);
    }

    private void searcheDriver(String keyWord, final boolean isOnResume) {
        DriverApiImpl.getDriverApiImpl().searchDrivers(CommonTools.getToken(MyCarsActivity.this), keyWord, new Subscriber<ErrorInfo>() {
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
                    List<Driver> list = (List<Driver>) errorInfo.object;
                    if (list.size() > 0) {
                        driverList.clear();
                        driverList.addAll(list);
                        driverAdapter.notifyDataSetChanged();
                        if (!isOnResume) {
                            Toast.makeText(mContext, "搜索到" + list.size() + "条结果。", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(mContext, getString(R.string.no_result), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(mContext, errorInfo.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
