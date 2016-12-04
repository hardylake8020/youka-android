package com.zzqs.app_kc.activities;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ContentData;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.utils.CommonFiled;
import com.zzqs.app_kc.utils.ImageUtil;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.CircleImageView;
import com.zzqs.app_kc.widgets.DialogView;
import com.zzqs.app_kc.widgets.RoundImageView;
import com.zzqs.app_kc.widgets.SafeProgressDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by lance on 15/4/17.
 */
public class UserInfoActivity extends BaseActivity implements View.OnClickListener {
    public static final int SELECT_PHOTO = 100;
    public static final int TAKE_A_PHOTO = 200;
    public static final int SHOOT_PLATE_PHOTO = 99;
    private File headFile, idCardFile, drivingIdPhotoFile, travelIdPhotoFile, tradingCardFile, newFile;
    CircleImageView headPortrait;
    TextView title, name, drivingDate, mobilePhone, plate;
    RoundImageView idCardImg, drivingIdImg, travelIdImg, tradingIdImg;
    private User user;
    SafeProgressDialog pd;
    private final static int UPLOAD_FAIL = 0;
    private final static int UPLOAD_SUCCESS = 1;
    private final static int UPLOAD = 5;
    private final static int GET_HEAD_IMG = 6;
    private final static int GET_ID_CARD_IMG = 7;
    private final static int GET_DRIVING_ID_PHOTO_IMG = 8;
    private final static int GET_TRAVEL_ID_PHOTO_IMG = 9;
    private final static int GET_TRADING_CARD_IMG = 10;
    private final static int SAVE_FILE_FAILED = 11;
    private int getPictureMold;
    private ImageLoader imageLoader;
    private String numbers;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPLOAD:
                    String path = (String) msg.obj;
                    File file = new File(path);
                    if (file.exists()) {
                        uploadPhoto(file);
                    } else {
                        Toast.makeText(UserInfoActivity.this, R.string.prompt_unexpected_error_take_photo, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case UPLOAD_FAIL:
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    String err = (String) msg.obj;
                    Toast.makeText(UserInfoActivity.this, err, Toast.LENGTH_SHORT).show();
                    break;
                case UPLOAD_SUCCESS:
                    switch (getPictureMold) {
                        case GET_HEAD_IMG:
                            user.setPhoto(headFile.getAbsolutePath());
                            imageLoader.displayImage("file://" + headFile.getAbsolutePath(), headPortrait);
                            break;
                        case GET_ID_CARD_IMG:
                            user.setIdCardPhoto(idCardFile.getAbsolutePath());
                            imageLoader.displayImage("file://" + idCardFile.getAbsolutePath(), idCardImg);
                            break;
                        case GET_DRIVING_ID_PHOTO_IMG:
                            user.setDrivingIdPhoto(drivingIdPhotoFile.getAbsolutePath());
                            imageLoader.displayImage("file://" + drivingIdPhotoFile.getAbsolutePath(), drivingIdImg);
                            break;
                        case GET_TRAVEL_ID_PHOTO_IMG:
                            user.setTravelIdPhoto(travelIdPhotoFile.getAbsolutePath());
                            imageLoader.displayImage("file://" + travelIdPhotoFile.getAbsolutePath(), travelIdImg);
                            break;
                        case GET_TRADING_CARD_IMG:
                            user.setTradingIdPhoto(tradingCardFile.getAbsolutePath());
                            imageLoader.displayImage("file://" + tradingCardFile.getAbsolutePath(), tradingIdImg);
                            break;
                    }
                    RestAPI.getInstance(getApplicationContext()).updateProfile(user, new RestAPI.RestResponse() {
                        @Override
                        public void onSuccess(Object object) {
                            if (pd.isShowing()) {
                                pd.dismiss();
                            }
                            ZZQSApplication.getInstance().updateUser(user);
                            Toast.makeText(UserInfoActivity.this, R.string.prompt_save_success, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Object object) {
                            if (pd.isShowing()) {
                                pd.dismiss();
                            }
                            if (object.toString().equals("disconnected")) {
                                DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, getString(R.string.prompt_dl_other_equipment_login_title), getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        ZZQSApplication.getInstance().clearUser(UserInfoActivity.this);
                                        ZZQSApplication.getInstance().cleanAllActivity();
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    }
                                });
                            } else {
                                Toast.makeText(UserInfoActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    break;
                case SAVE_FILE_FAILED:
                    Toast.makeText(UserInfoActivity.this, getString(R.string.prompt_save_file_failed), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public interface Response {
        public void onSuccess(Object object);

        public void onFailure(Object object);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_user_info);
        user = ZZQSApplication.getInstance().getUser();
        initView();
        initPhoto();
        initText();
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = ZZQSApplication.getInstance().getUser();
        initText();
    }

    private void initView() {
        pd = new SafeProgressDialog(this);
        pd.setCancelable(false);
        findViewById(R.id.head_back).setOnClickListener(this);
        title = (TextView) findViewById(R.id.head_title);
        name = (TextView) findViewById(R.id.name);
        plate = (TextView) findViewById(R.id.plate);
        drivingDate = (TextView) findViewById(R.id.driving_date);
        mobilePhone = (TextView) findViewById(R.id.mobile_phone);
        headPortrait = (CircleImageView) findViewById(R.id.head_portrait);
        idCardImg = (RoundImageView) findViewById(R.id.id_card_img);
        drivingIdImg = (RoundImageView) findViewById(R.id.driving_id_img);
        travelIdImg = (RoundImageView) findViewById(R.id.travel_id_img);
        tradingIdImg = (RoundImageView) findViewById(R.id.trading_id_img);
        findViewById(R.id.rl_head).setOnClickListener(this);
        findViewById(R.id.rl_name).setOnClickListener(this);
        findViewById(R.id.rl_driving_date).setOnClickListener(this);
        findViewById(R.id.rl_mobile_phone).setOnClickListener(this);
        findViewById(R.id.rl_Id_card).setOnClickListener(this);
        findViewById(R.id.rl_driver_license).setOnClickListener(this);
        findViewById(R.id.rl_driving_license).setOnClickListener(this);
        findViewById(R.id.rl_license_plates).setOnClickListener(this);
        findViewById(R.id.rl_trading_card).setOnClickListener(this);
    }

    private void initPhoto() {
        newFile = new File(ContentData.BASE_DIR + "/newImg.jpg");
        imageLoader = ImageLoader.getInstance();
        if (!StringTools.isEmp(user.getLocalPhoto())) {
            headFile = new File(user.getLocalPhoto());
            if (headFile.exists()) {
                imageLoader.displayImage("file://" + headFile.getAbsolutePath(), headPortrait);
            }
        }

        loadPhoto(user.getLocalIdCardPhoto(), user.getIdCardPhoto(), idCardFile, idCardImg, new Response() {
            @Override
            public void onSuccess(Object object) {
                if (object instanceof String) {
                    user.setLocalIdCardPhoto((String) object);
                    ZZQSApplication.getInstance().updateUser(user);
                }
            }

            @Override
            public void onFailure(Object object) {

            }
        });

        loadPhoto(user.getLocalDrivingIdPhoto(), user.getDrivingIdPhoto(), drivingIdPhotoFile, drivingIdImg, new Response() {
            @Override
            public void onSuccess(Object object) {
                if (object instanceof String) {
                    user.setLocalDrivingIdPhoto((String) object);
                    ZZQSApplication.getInstance().updateUser(user);
                }
            }

            @Override
            public void onFailure(Object object) {

            }
        });

        loadPhoto(user.getLocalTradingIdPhoto(), user.getTradingIdPhoto(), tradingCardFile, tradingIdImg, new Response() {
            @Override
            public void onSuccess(Object object) {
                if (object instanceof String) {
                    user.setLocalTradingIdPhoto((String) object);
                    ZZQSApplication.getInstance().updateUser(user);
                }
            }

            @Override
            public void onFailure(Object object) {

            }
        });

        loadPhoto(user.getLocalTravelIdPhoto(), user.getTravelIdPhoto(), travelIdPhotoFile, travelIdImg, new Response() {
            @Override
            public void onSuccess(Object object) {
                if (object instanceof String) {
                    user.setLocalTravelIdPhoto((String) object);
                    ZZQSApplication.getInstance().updateUser(user);
                }
            }

            @Override
            public void onFailure(Object object) {

            }
        });
    }

    private void initText() {
        title.setText(R.string.view_tv_user_info);

        if (!StringTools.isEmp(user.getNickname())) {
            name.setText(user.getNickname());
        }
        if (!StringTools.isEmp(user.getPhone())) {
            mobilePhone.setText(user.getPhone() + "");
        }
        if (!StringTools.isEmp(user.getDrivingDate())) {
            drivingDate.setText(user.getDrivingDate());
        }

        numbers = user.getPlateNumbers();
        if (!StringTools.isEmp(numbers)) {//车牌
            numbers = numbers.toUpperCase();
            if (numbers.contains(";")) {
                String[] str = numbers.split(";");
                for (int i = 0; i < str.length; i++) {
                    plate.setText(str[0] + "");
                }
            } else {
                plate.setText(numbers + "");
            }
        }
    }


    private void loadPhoto(String localUrl, final String downloadUrl, File file, RoundImageView view, Response response) {
        if (!StringTools.isEmp(localUrl)) {
            file = new File(localUrl);
            if (file.exists()) {
                imageLoader.displayImage("file://" + file.getAbsolutePath(), view);
                response.onSuccess(true);
            } else if (!StringTools.isEmp(downloadUrl)) {
                String[] names = downloadUrl.split("/");
                localUrl = ContentData.BASE_DIR + "/" + names[names.length - 1];
                downloadPhoto(localUrl, downloadUrl, view, response);
            }
        } else if (!StringTools.isEmp(downloadUrl)) {
            String[] names = downloadUrl.split("/");
            localUrl = ContentData.BASE_DIR + "/" + names[names.length - 1];
            downloadPhoto(localUrl, downloadUrl, view, response);
        }

    }

    private void downloadPhoto(final String localUrl, final String downloadUrl, final RoundImageView view, final Response response) {
        ImageUtil.downloadBitmap(getApplicationContext(), CommonFiled.QINIU_ZOOM + downloadUrl, localUrl, new ImageUtil.ImageUtilCallback() {
            @Override
            public void onSuccess(Object object) {
                final File file = new File(localUrl);
                imageLoader.displayImage("file://" + file.getAbsolutePath(), view);
                response.onSuccess(localUrl);
            }

            @Override
            public void onFailure(Object object) {
                response.onFailure(true);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        pd.dismiss();
        if (newFile.exists()) {
            newFile.delete();
        }
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.head_back:
                finish();
                break;
            case R.id.rl_name:
                intent = new Intent(this, InputActivity.class);
                intent.putExtra(InputActivity.INFO_TYPE, InputActivity.USER_NAME);
                startActivity(intent);
                break;
            case R.id.rl_driving_date:
                intent = new Intent(this, InputActivity.class);
                intent.putExtra(InputActivity.INFO_TYPE, InputActivity.DRIVER_YEAR);
                startActivity(intent);
                break;
            case R.id.rl_mobile_phone:
                intent = new Intent(this, InputActivity.class);
                intent.putExtra(InputActivity.INFO_TYPE, InputActivity.MOBLIE_PHONE);
                startActivity(intent);
                break;
            case R.id.rl_head:
                getPictureMold = GET_HEAD_IMG;
                getPhoto(user.getPhoto(), "头像");
                break;
            case R.id.rl_Id_card:
                getPictureMold = GET_ID_CARD_IMG;
                getPhoto(user.getIdCardPhoto(), "身份证");
                break;
            case R.id.rl_driver_license:
                getPictureMold = GET_DRIVING_ID_PHOTO_IMG;
                getPhoto(user.getDrivingIdPhoto(), "驾驶证");
                break;
            case R.id.rl_driving_license:
                getPictureMold = GET_TRAVEL_ID_PHOTO_IMG;
                getPhoto(user.getTravelIdPhoto(), "行驶证");
                break;
            case R.id.rl_trading_card:
                getPictureMold = GET_TRADING_CARD_IMG;
                getPhoto(user.getTradingIdPhoto(), "营运证");
                break;
            case R.id.rl_license_plates:
                intent = new Intent(getApplicationContext(), PlatePhotosActivity.class);
                intent.putExtra(User.USER, user);
                startActivityForResult(intent, SHOOT_PLATE_PHOTO);
                break;
            case R.id.head_right:
                user.setDrivingDate(drivingDate.getText().toString());
                pd.setMessage(getResources().getString(R.string.prompt_dl_submitting));
                pd.show();
                RestAPI.getInstance(getApplicationContext()).updateProfile(user, new RestAPI.RestResponse() {
                    @Override
                    public void onSuccess(Object object) {
                        pd.dismiss();
                        ZZQSApplication.getInstance().updateUser(user);
                        Toast.makeText(UserInfoActivity.this, R.string.prompt_save_success, Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Object object) {
                        pd.dismiss();
                        if (object.toString().equals("disconnected")) {
                            DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, getString(R.string.prompt_dl_other_equipment_login_title), getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    ZZQSApplication.getInstance().clearUser(UserInfoActivity.this);
                                    ZZQSApplication.getInstance().cleanAllActivity();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                }
                            });
                        } else {
                            Toast.makeText(UserInfoActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }

    private Bitmap bitmap = null;

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            pd.setMessage(getString(R.string.prompt_dl_submitting));
            pd.show();
            try {
                final Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        if (requestCode == TAKE_A_PHOTO) {
                            bitmap = BitmapFactory.decodeFile(newFile.getAbsolutePath());
                            int degree = ImageUtil.readPictureDegree(newFile.getAbsolutePath());
                            bitmap = ImageUtil.rotaingImageView(degree, bitmap);
                            bitmap = ImageUtil.comp(bitmap, 100, 256, 256);
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
                        switch (getPictureMold) {
                            case GET_HEAD_IMG:
                                if (headFile != null) {
                                    if (headFile.exists()) {
                                        headFile.delete();
                                    }
                                }
                                file = headFile = new File(ContentData.BASE_DIR + "/" + user.getUsername() + "photo" + System.currentTimeMillis() + ".jpg");
                                isSaveSuccess = ImageUtil.saveCompressBitmap(bitmap, headFile.getAbsolutePath(), 100);
                                msg.obj = headFile.getAbsolutePath();
                                break;
                            case GET_ID_CARD_IMG:
                                if (idCardFile != null) {
                                    if (idCardFile.exists()) {
                                        idCardFile.delete();
                                    }
                                }
                                file = idCardFile = new File(ContentData.BASE_DIR + "/" + user.getUsername() + "id_card_photo" + System.currentTimeMillis() + ".jpg");
                                isSaveSuccess = ImageUtil.saveCompressBitmap(bitmap, idCardFile.getAbsolutePath(), 100);
                                msg.obj = idCardFile.getAbsolutePath();
                                break;
                            case GET_DRIVING_ID_PHOTO_IMG:
                                if (drivingIdPhotoFile != null) {
                                    if (drivingIdPhotoFile.exists()) {
                                        drivingIdPhotoFile.delete();
                                    }
                                }
                                file = drivingIdPhotoFile = new File(ContentData.BASE_DIR + "/" + user.getUsername() + "driving_id_photo" + System.currentTimeMillis() + ".jpg");
                                isSaveSuccess = ImageUtil.saveCompressBitmap(bitmap, drivingIdPhotoFile.getAbsolutePath(), 100);
                                msg.obj = drivingIdPhotoFile.getAbsolutePath();
                                break;
                            case GET_TRAVEL_ID_PHOTO_IMG:
                                if (travelIdPhotoFile != null) {
                                    if (travelIdPhotoFile.exists()) {
                                        travelIdPhotoFile.delete();
                                    }
                                }
                                file = travelIdPhotoFile = new File(ContentData.BASE_DIR + "/" + user.getUsername() + "travel_id_photo" + System.currentTimeMillis() + ".jpg");
                                isSaveSuccess = ImageUtil.saveCompressBitmap(bitmap, travelIdPhotoFile.getAbsolutePath(), 100);
                                msg.obj = travelIdPhotoFile.getAbsolutePath();
                                break;
                            case GET_TRADING_CARD_IMG:
                                if (tradingCardFile != null) {
                                    if (tradingCardFile.exists()) {
                                        tradingCardFile.delete();
                                    }
                                }
                                file = tradingCardFile = new File(ContentData.BASE_DIR + "/" + user.getUsername() + "trading_id_photo" + System.currentTimeMillis() + ".jpg");
                                isSaveSuccess = ImageUtil.saveCompressBitmap(bitmap, tradingCardFile.getAbsolutePath(), 100);
                                msg.obj = tradingCardFile.getAbsolutePath();
                                break;
                            default:
                                isSaveSuccess = false;
                                break;
                        }
                        if (isSaveSuccess) {
                            msg.what = UPLOAD;
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
                pd.dismiss();
                Toast.makeText(this, R.string.prompt_unexpected_error_take_photo, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        user = ZZQSApplication.getInstance().getUser();
        if (!StringTools.isEmp(numbers)) {
            if (numbers.contains(";")) {
                String[] str = numbers.split(";");
                for (int i = 0; i < str.length; i++) {
                    plate.setText(str[0]);
                }
            } else {
                plate.setText(numbers);
            }
        }
    }

    private void getPhoto(final String path, final String photoType) {
        final CharSequence[] items;
        if (!StringTools.isEmp(path)) {
            items = new CharSequence[]{"查看", "相机拍照", "相册选取", "关闭"};
        } else {
            items = new CharSequence[]{"相机拍照", "相册选取", "关闭"};
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);
        builder.setTitle(R.string.prompt_dl_choice_img_source);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int item) {
                if (items[item].equals("查看")) {
                    DialogView.showBigImageDialog(UserInfoActivity.this, path, null, photoType, null);
                } else if (items[item].equals("相机拍照")) {
                    if (!newFile.exists()) {
                        try {
                            newFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
                    startActivityForResult(intent, TAKE_A_PHOTO);
                } else if (items[item].equals("相册选取")) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_PHOTO);
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void uploadPhoto(final File file) {
        RestAPI.getInstance(this).getQiniuImgToken(new RestAPI.RestResponse() {
            @Override
            public void onSuccess(Object object) {
                String token = (String) object;
                UploadManager uploadManager = new UploadManager();
                if (file.exists()) {
                    String key = file.getAbsolutePath();
                    uploadManager.put(file, key, token, new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo responseInfo, JSONObject jsonObject) {
                            if (responseInfo.isOK()) {
                                handler.sendEmptyMessage(UPLOAD_SUCCESS);
                            } else {
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
                Message msg = handler.obtainMessage();
                msg.what = UPLOAD_FAIL;
                msg.obj = object;
                handler.sendMessage(msg);
            }
        });
    }
}
