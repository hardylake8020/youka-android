package com.zzqs.app_kc.z_kc.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.activities.UserInfoActivity;
import com.zzqs.app_kc.app.ContentData;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.utils.CommonTools;
import com.zzqs.app_kc.utils.ImageUtil;
import com.zzqs.app_kc.utils.ScreenUtil;
import com.zzqs.app_kc.widgets.DialogView;
import com.zzqs.app_kc.z_kc.entitiy.ErrorInfo;
import com.zzqs.app_kc.z_kc.fragment.BankCardAuthFragment;
import com.zzqs.app_kc.z_kc.fragment.CarAuthFragment;
import com.zzqs.app_kc.z_kc.fragment.RealNameAuthFragment;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;
import com.zzqs.app_kc.z_kc.network.DriverApiImpl;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

/**
 * Created by ray on 2017/3/16.
 * Class name : PersonInfoActivity
 * Description :个人信息页面
 */
public class PersonInfoActivity extends BaseActivity implements PopupWindow.OnDismissListener {
    public static final int SELECT_PHOTO = 100;
    public static final int TAKE_A_PHOTO = 200;
    private final static int SAVE_FILE_FAILED = -1;
    private final static int SAVE_FILE_SUCCESS = 1;
    private final static int UPLOAD_FAIL = -2;
    private final static int UPLOAD_SUCCESS = 2;
    android.support.v4.app.FragmentManager mFragmentManager;
    ViewPager mPager;
    private ImageView cursor;
    private RealNameAuthFragment realNameAuth;
    private CarAuthFragment carAuth;
    private BankCardAuthFragment bankCardAuthFragment;
    private FragmentAdapter mFragmentAdapter;
    private List<Fragment> listFragments; // Tab页面列表
    public int currIndex = 0;// 当前页卡编号
    private int screenWidth;
    private TextView tvSave, head_back, head_title, tvRealNameAuth, tvCarAuth, tvBankCardAuth;
    public User user;
    private PopupWindow popupWindow;
    private int navigationHeight;
    private File tmpFile, headFile, idCardFile, drivingIdPhotoFile, travelIdPhotoFile, truckFile, carPlateFile, bankNumberFile;
    private String photoType, photoName;
    private Uri uri;
    private Bitmap bitmap = null;
    private BaseDao<User> userDao;
    private List<String> keyList = new ArrayList<>();
    private Map<String, File> fileList;
    private boolean uploadIsErr = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            safePd.dismiss();
            switch (msg.what) {
                case SAVE_FILE_SUCCESS:
                    switch (photoType) {
                        case "photo":
                            user.setPhoto(headFile.getAbsolutePath());
                            realNameAuth.refreshImagePath("photo");
                            break;
                        case "idCardPhoto":
                            user.setIdCardPhoto(idCardFile.getAbsolutePath());
                            realNameAuth.refreshImagePath("idCardPhoto");
                            break;
                        case "drivingIdPhoto":
                            user.setDrivingIdPhoto(drivingIdPhotoFile.getAbsolutePath());
                            realNameAuth.refreshImagePath("drivingIdPhoto");
                            break;
                        case "truck_photo":
                            user.setTruck_photo(truckFile.getAbsolutePath());
                            carAuth.refreshImagePath("truck_photo");
                            break;
                        case "car_plate_photo":
                            user.setCar_plate_photo(carPlateFile.getAbsolutePath());
                            carAuth.refreshImagePath("car_plate_photo");
                            break;
                        case "travelIdPhoto":
                            user.setTravelIdPhoto(travelIdPhotoFile.getAbsolutePath());
                            carAuth.refreshImagePath("travelIdPhoto");
                            break;
                        case "bank_number_photo":
                            user.setBank_number_photo(bankNumberFile.getAbsolutePath());
                            bankCardAuthFragment.refreshImagePath();
                            break;
                    }
                    break;
                case UPLOAD_SUCCESS:
                    updateDriverProfile();
                    break;
            }
        }
    };

    @Override
    public void initVariables() {
        screenWidth = ScreenUtil.getScreenWidth(getApplicationContext());// 获取分辨率宽度
        userDao = DaoManager.getUserDao(this);
        List<User> users = userDao.find();
        if (users != null && users.size() > 0) {
            user = users.get(0);
        }
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        navigationHeight = getResources().getDimensionPixelSize(resourceId);
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.z_kc_act_person_info);
        tvSave = (TextView) findViewById(R.id.tvSave);
        head_back = (TextView) findViewById(R.id.head_back);
        head_title = (TextView) findViewById(R.id.head_title);
        head_title.setText(getString(R.string.person_info));
        head_back.setText("");
        head_back.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                finish();
            }
        });
        tvSave.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                if (TextUtils.isEmpty(user.getNickname())) {
                    showToast("请填写姓名", Toast.LENGTH_SHORT);
                    return;
                }
                if (TextUtils.isEmpty(user.getId_card_number())) {
                    showToast("请填写身份证号", Toast.LENGTH_SHORT);
                    return;
                }
                if (TextUtils.isEmpty(user.getTruck_number())) {
                    showToast("请填写车牌号", Toast.LENGTH_SHORT);
                    return;
                }
                if (TextUtils.isEmpty(user.getBank_number())) {
                    showToast("请填写银行卡号", Toast.LENGTH_SHORT);
                    return;
                }
                if (TextUtils.isEmpty(user.getBank_username())) {
                    showToast("请填写开户名", Toast.LENGTH_SHORT);
                    return;
                }
                if (TextUtils.isEmpty(user.getBank_name())) {
                    showToast("请填写开户支行", Toast.LENGTH_SHORT);
                    return;
                }
                if (headFile == null && idCardFile == null && drivingIdPhotoFile == null
                        && travelIdPhotoFile == null && truckFile == null && carPlateFile == null
                        && bankNumberFile == null) {
                    updateDriverProfile();
                } else {
                    fileList = new HashMap<String, File>();
                    if (headFile != null && headFile.exists()) {
                        fileList.put("photo", headFile);
                    }
                    if (idCardFile != null && idCardFile.exists()) {
                        fileList.put("idCardPhoto", idCardFile);
                    }
                    if (drivingIdPhotoFile != null && drivingIdPhotoFile.exists()) {
                        fileList.put("drivingIdPhoto", drivingIdPhotoFile);
                    }
                    if (travelIdPhotoFile != null && travelIdPhotoFile.exists()) {
                        fileList.put("travelIdPhoto", travelIdPhotoFile);
                    }
                    if (truckFile != null && truckFile.exists()) {
                        fileList.put("truck_photo", truckFile);
                    }
                    if (carPlateFile != null && carPlateFile.exists()) {
                        fileList.put("plate_photo", carPlateFile);
                    }
                    if (bankNumberFile != null && bankNumberFile.exists()) {
                        fileList.put("bank_number_photo", bankNumberFile);
                    }

                    if (fileList.size() > 0) {
                        for (Map.Entry<String, File> entry : fileList.entrySet()) {
                            uploadPhoto(fileList.get(entry.getKey()));
                        }
                    } else {
                        updateDriverProfile();
                    }
                }
            }
        });

        tvRealNameAuth = (TextView) findViewById(R.id.tvRealNameAuth);
        tvRealNameAuth.setOnClickListener(new CursorClickListener(0));
        tvCarAuth = (TextView) findViewById(R.id.tvCarAuth);
        tvCarAuth.setOnClickListener(new CursorClickListener(1));
        tvBankCardAuth = (TextView) findViewById(R.id.tvBankCardAuth);
        tvBankCardAuth.setOnClickListener(new CursorClickListener(2));
        cursor = (ImageView) findViewById(R.id.cursor);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cursor.getLayoutParams();
        lp.width = screenWidth / 3;
        cursor.setLayoutParams(lp);
        mFragmentManager = getSupportFragmentManager();
        mPager = (ViewPager) findViewById(R.id.vPager);
        mPager.setOffscreenPageLimit(2);
        listFragments = new ArrayList<>();
        realNameAuth = new RealNameAuthFragment();
        carAuth = new CarAuthFragment();
        bankCardAuthFragment = new BankCardAuthFragment();
        listFragments.add(realNameAuth);
        listFragments.add(carAuth);
        listFragments.add(bankCardAuthFragment);
        mFragmentAdapter = new FragmentAdapter(mFragmentManager, listFragments);
        mPager.setAdapter(mFragmentAdapter);
        mPager.addOnPageChangeListener(new MyOnPageChangeListener());
        mPager.setCurrentItem(currIndex);
    }

    @Override
    public void loadData() {
    }

    @Override
    public void onDismiss() {
        setBackgroundAlpha(1);
    }

    /**
     * ViewPager适配器
     */
    public class FragmentAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList = new ArrayList<Fragment>();

        public FragmentAdapter(android.support.v4.app.FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }
    }

    /**
     * 头标点击监听
     */
    public class CursorClickListener implements View.OnClickListener {
        private int index = 0;

        public CursorClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }
    }

    private void resetTextView() {
        tvRealNameAuth.setTextColor(ContextCompat.getColor(this, R.color.gray));
        tvCarAuth.setTextColor(ContextCompat.getColor(this, R.color.gray));
        tvBankCardAuth.setTextColor(ContextCompat.getColor(this, R.color.gray));
    }

    /**
     * 页卡切换监听
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {


        @Override
        public void onPageSelected(int position) {
            resetTextView();
            switch (position) {
                case 0:
                    tvRealNameAuth.setTextColor(Color.WHITE);
                    break;
                case 1:
                    tvCarAuth.setTextColor(Color.WHITE);
                    break;
                case 2:
                    tvBankCardAuth.setTextColor(Color.WHITE);
                    break;
            }
            currIndex = position;
        }

        @Override
        public void onPageScrolled(int position, float offset, int offsetPixels) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cursor
                    .getLayoutParams();

            /**
             * 利用currIndex(当前所在页面)和position(下一个页面)以及offset来
             * 设置mTabLineIv的左边距 滑动场景：
             * 记3个页面,
             * 从左到右分别为0,1,2
             * 0->1; 1->2; 2->1; 1->0
             */

            if (currIndex == 0 && position == 0)// 0->1
            {
                lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currIndex * (screenWidth / 3));
            } else if (currIndex == 1 && position == 0) // 1->0
            {
                lp.leftMargin = (int) (-(1 - offset) * (screenWidth * 1.0 / 3) + currIndex * (screenWidth / 3));

            } else if (currIndex == 1 && position == 1) // 1->2
            {
                lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currIndex * (screenWidth / 3));
            } else if (currIndex == 2 && position == 1) // 2->1
            {
                lp.leftMargin = (int) (-(1 - offset) * (screenWidth * 1.0 / 3) + currIndex * (screenWidth / 3));
            }
            cursor.setLayoutParams(lp);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    public void showBigImage(String path, final ImageView imageView, final String photoType) {
        boolean isFromServer = true;
        if (path.contains(ContentData.BASE_PICS)) {
            isFromServer = false;
        }
        DialogView.showBigImageDialog(this, path, isFromServer, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == DialogView.DELETE) {
                    ImageUtil.showImage(null, imageView);
                    switch (photoType) {
                        case "photo":
                            user.setPhoto("");
                            if (headFile != null) {
                                if (headFile.exists()) {
                                    headFile.delete();
                                }
                            }
                            break;
                        case "idCardPhoto":
                            user.setIdCardPhoto("");
                            if (idCardFile != null) {
                                if (idCardFile.exists()) {
                                    idCardFile.delete();
                                }
                            }
                            break;
                        case "drivingIdPhoto":
                            user.setDrivingIdPhoto("");
                            if (drivingIdPhotoFile != null) {
                                if (drivingIdPhotoFile.exists()) {
                                    drivingIdPhotoFile.delete();
                                }
                            }
                            break;
                        case "truck_photo":
                            user.setTruck_photo("");
                            if (truckFile != null) {
                                if (truckFile.exists()) {
                                    truckFile.delete();
                                }
                            }
                            break;
                        case "car_plate_photo":
                            user.setCar_plate_photo("");
                            if (carPlateFile != null) {
                                if (carPlateFile.exists()) {
                                    carPlateFile.delete();
                                }
                            }
                            break;
                        case "travelIdPhoto":
                            user.setTravelIdPhoto("");
                            if (travelIdPhotoFile != null) {
                                if (travelIdPhotoFile.exists()) {
                                    travelIdPhotoFile.delete();
                                }
                            }
                            break;
                        case "bank_number_photo":
                            user.setBank_number_photo("");
                            if (bankNumberFile != null) {
                                if (bankNumberFile.exists()) {
                                    bankNumberFile.delete();
                                }
                            }
                            break;
                    }
                }
            }
        });
    }

    public void openPopupWindow(View v, String photo) {
        //防止重复按按钮
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        //设置PopupWindow的View
        View view = LayoutInflater.from(this).inflate(R.layout.view_popupwindow, null);
        popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
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
        photoType = photo;
    }

    //设置屏幕背景透明效果
    private void setBackgroundAlpha(float alpha) {
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
        TextView tv_take_photo, tv_pick_photo, tv_cancel;
        tv_take_photo = (TextView) view.findViewById(R.id.tv_take_photo);
        tv_pick_photo = (TextView) view.findViewById(R.id.tv_pick_photo);
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_take_photo.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                shootPhoto();
                popupWindow.dismiss();
            }
        });
        tv_pick_photo.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                selectPhoto();
                popupWindow.dismiss();
            }
        });
        tv_cancel.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                popupWindow.dismiss();
            }
        });
    }

    public void shootPhoto() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(this, R.string.prompt_no_sdcard, Toast.LENGTH_LONG).show();
            return;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = format.format(new Date());
        photoName = user.getPhone() + photoType + time + ".jpg";
        tmpFile = new File(ContentData.BASE_PICS + "/" + photoName);
        if (!tmpFile.exists()) {
            try {
                tmpFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        uri = Uri.fromFile(tmpFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, TAKE_A_PHOTO);
    }

    private void selectPhoto() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(this, R.string.prompt_no_sdcard, Toast.LENGTH_LONG).show();
            return;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = format.format(new Date());
        photoName = user.getPhone() + photoType + time + ".jpg";
        tmpFile = new File(ContentData.BASE_PICS + "/" + photoName);
        if (!tmpFile.exists()) {
            try {
                tmpFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, UserInfoActivity.SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            safePd.setMessage(getString(R.string.prompt_dl_compressed_image));
            safePd.show();
            try {
                final Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        if (requestCode == TAKE_A_PHOTO) {
                            if (null != tmpFile && tmpFile.exists()) {
                                bitmap = BitmapFactory.decodeFile(tmpFile.getAbsolutePath());
                                int degree = ImageUtil.readPictureDegree(tmpFile.getAbsolutePath());
                                bitmap = ImageUtil.rotaingImageView(degree, bitmap);
                                bitmap = ImageUtil.comp(bitmap, 100, 256, 256);
                            } else {
                                handler.sendEmptyMessage(SAVE_FILE_FAILED);
                            }
                        } else if (requestCode == SELECT_PHOTO) {
                            ContentResolver resolver = getContentResolver();
                            //照片的原始资源地址
                            Uri originalUri = data.getData();
                            try {
                                //使用ContentProvider通过URI获取原始图片
                                Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                                if (photo != null) {
                                    //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                                    bitmap = ImageUtil.comp(photo, 100, 256, 256);
                                    //释放原始图片占用的内存，防止out of memory异常发生
                                    photo.recycle();
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                return;
                            } catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                        Message msg = handler.obtainMessage();
                        boolean isSaveSuccess;
                        File file = null;
                        switch (photoType) {
                            case "photo":
                                if (headFile != null) {
                                    if (headFile.exists()) {
                                        headFile.delete();
                                    }
                                }
                                file = headFile = new File(ContentData.BASE_PICS + "/" + user.getUsername() + "photo" + System.currentTimeMillis() + ".jpg");
                                isSaveSuccess = ImageUtil.saveCompressBitmap(bitmap, headFile.getAbsolutePath(), 100);
                                msg.obj = headFile.getAbsolutePath();
                                break;
                            case "idCardPhoto":
                                if (idCardFile != null) {
                                    if (idCardFile.exists()) {
                                        idCardFile.delete();
                                    }
                                }
                                file = idCardFile = new File(ContentData.BASE_PICS + "/" + user.getUsername() + "idCardPhoto" + System.currentTimeMillis() + ".jpg");
                                isSaveSuccess = ImageUtil.saveCompressBitmap(bitmap, idCardFile.getAbsolutePath(), 100);
                                msg.obj = idCardFile.getAbsolutePath();
                                break;
                            case "drivingIdPhoto":
                                if (drivingIdPhotoFile != null) {
                                    if (drivingIdPhotoFile.exists()) {
                                        drivingIdPhotoFile.delete();
                                    }
                                }
                                file = drivingIdPhotoFile = new File(ContentData.BASE_PICS + "/" + user.getUsername() + "drivingIdPhoto" + System.currentTimeMillis() + ".jpg");
                                isSaveSuccess = ImageUtil.saveCompressBitmap(bitmap, drivingIdPhotoFile.getAbsolutePath(), 100);
                                msg.obj = drivingIdPhotoFile.getAbsolutePath();
                                break;
                            case "truck_photo":
                                if (truckFile != null) {
                                    if (truckFile.exists()) {
                                        truckFile.delete();
                                    }
                                }
                                file = truckFile = new File(ContentData.BASE_PICS + "/" + user.getUsername() + "truck_photo" + System.currentTimeMillis() + ".jpg");
                                isSaveSuccess = ImageUtil.saveCompressBitmap(bitmap, truckFile.getAbsolutePath(), 100);
                                msg.obj = truckFile.getAbsolutePath();
                                break;
                            case "car_plate_photo":
                                if (carPlateFile != null) {
                                    if (carPlateFile.exists()) {
                                        carPlateFile.delete();
                                    }
                                }
                                file = carPlateFile = new File(ContentData.BASE_PICS + "/" + user.getUsername() + "car_plate_photo" + System.currentTimeMillis() + ".jpg");
                                isSaveSuccess = ImageUtil.saveCompressBitmap(bitmap, carPlateFile.getAbsolutePath(), 100);
                                msg.obj = carPlateFile.getAbsolutePath();
                                break;
                            case "travelIdPhoto":
                                if (travelIdPhotoFile != null) {
                                    if (travelIdPhotoFile.exists()) {
                                        travelIdPhotoFile.delete();
                                    }
                                }
                                file = travelIdPhotoFile = new File(ContentData.BASE_PICS + "/" + user.getUsername() + "travelIdPhoto" + System.currentTimeMillis() + ".jpg");
                                isSaveSuccess = ImageUtil.saveCompressBitmap(bitmap, travelIdPhotoFile.getAbsolutePath(), 100);
                                msg.obj = travelIdPhotoFile.getAbsolutePath();
                                break;
                            case "bank_number_photo":
                                if (bankNumberFile != null) {
                                    if (bankNumberFile.exists()) {
                                        bankNumberFile.delete();
                                    }
                                }
                                file = bankNumberFile = new File(ContentData.BASE_PICS + "/" + user.getUsername() + "bank_number_photo" + System.currentTimeMillis() + ".jpg");
                                isSaveSuccess = ImageUtil.saveCompressBitmap(bitmap, bankNumberFile.getAbsolutePath(), 100);
                                msg.obj = bankNumberFile.getAbsolutePath();
                                break;
                            default:
                                isSaveSuccess = false;
                                break;
                        }
                        if (isSaveSuccess) {
                            msg.what = SAVE_FILE_SUCCESS;
                        } else {
                            msg.what = SAVE_FILE_FAILED;
                            if (file != null && file.exists()) {
                                file.delete();
                            }
                        }
                        handler.sendMessage(msg);

                    }
                };
                thread.start();
            } catch (Exception e) {
                safePd.dismiss();
                Toast.makeText(this, R.string.prompt_unexpected_error_take_photo, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void uploadPhoto(final File file) {
        safePd.setMessage("图片上传中，请稍后");
        safePd.show();
        RestAPI.getInstance(this).getQiniuImgToken(new RestAPI.RestResponse() {
            @Override
            public void onSuccess(Object object) {
                String token = (String) object;
                UploadManager uploadManager = new UploadManager();
                if (file != null && file.exists()) {
                    String key = file.getName();
                    uploadManager.put(file, key, token, new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo responseInfo, JSONObject jsonObject) {
                            if (responseInfo.isOK()) {
                                keyList.add(key);
                                if (keyList.size() == fileList.size()) {
                                    handler.sendEmptyMessage(UPLOAD_SUCCESS);
                                }
                            } else {
                                uploadIsErr = true;
                                keyList.clear();
                                Message msg = handler.obtainMessage();
                                msg.what = UPLOAD_FAIL;
                                msg.obj = getString(R.string.prompt_upload_img_failed);
                                handler.sendMessage(msg);
                                Log.d("qiniuUpload", responseInfo.toString());
                            }
                        }
                    }, null);
                }
            }

            @Override
            public void onFailure(Object object) {
                uploadIsErr = true;
                keyList.clear();
                Message msg = handler.obtainMessage();
                msg.what = UPLOAD_FAIL;
                msg.obj = object;
                handler.sendMessage(msg);
            }
        });
    }

    private void updateDriverProfile() {
        user.setTruck_type(carAuth.getTruckType());
        DriverApiImpl.getDriverApiImpl().updateDriverProfile(CommonTools.getToken(this), user, fileList, new Subscriber<ErrorInfo>() {
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
                    Toast.makeText(mContext, "修改成功", Toast.LENGTH_LONG).show();
                    User currentUser = ZZQSApplication.getInstance().getUser();
                    currentUser.setNickname(user.getNickname());
                    currentUser.setId_card_number(user.getId_card_number());
                    currentUser.setTruck_number(user.getTruck_number());
                    currentUser.setTruck_type(user.getTruck_type());
                    currentUser.setBank_number(user.getBank_number());
                    currentUser.setBank_name(user.getBank_name());
                    currentUser.setBank_username(user.getBank_username());
                    if (headFile != null && headFile.exists()) {
//                        currentUser.setPhoto(getFileNameNoEx(headFile.getName()));
                        currentUser.setPhoto(headFile.getName());
                    }
                    if (idCardFile != null && idCardFile.exists()) {
//                        currentUser.setIdCardPhoto(getFileNameNoEx(idCardFile.getName()));
                        currentUser.setIdCardPhoto(idCardFile.getName());
                    }
                    if (drivingIdPhotoFile != null && drivingIdPhotoFile.exists()) {
//                        currentUser.setDrivingIdPhoto(getFileNameNoEx(drivingIdPhotoFile.getName()));
                        currentUser.setDrivingIdPhoto(drivingIdPhotoFile.getName());
                    }
                    if (truckFile != null && truckFile.exists()) {
//                        currentUser.setTruck_photo(getFileNameNoEx(truckFile.getName()));
                        currentUser.setTruck_photo(truckFile.getName());
                    }
                    if (carPlateFile != null && carPlateFile.exists()) {
//                        currentUser.setCar_plate_photo(getFileNameNoEx(carPlateFile.getName()));
                        currentUser.setCar_plate_photo(carPlateFile.getName());
                    }
                    if (travelIdPhotoFile != null && travelIdPhotoFile.exists()) {
//                        currentUser.setTravelIdPhoto(getFileNameNoEx(travelIdPhotoFile.getName()));
                        currentUser.setTravelIdPhoto(travelIdPhotoFile.getName());
                    }
                    if (bankNumberFile != null && bankNumberFile.exists()) {
//                        currentUser.setBank_number_photo(getFileNameNoEx(bankNumberFile.getName()));
                        currentUser.setBank_number_photo(bankNumberFile.getName());
                    }
                    ZZQSApplication.getInstance().updateUser(currentUser);
                    finish();
                } else {
                    Toast.makeText(mContext, errorInfo.getType(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }
}
