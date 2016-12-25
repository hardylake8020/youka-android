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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.app.ContentData;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.utils.CommonFiled;
import com.zzqs.app_kc.utils.ImageUtil;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.DialogView;
import com.zzqs.app_kc.widgets.SafeProgressDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lance on 15/4/23.
 */
public class PlatePhotosActivity extends BaseActivity implements View.OnClickListener {
    GridView gridView;
    TextView title, headRight;
    private List<String> localPaths = null;
    private List<String> paths = null;
    private List<String> numbers = null;
    private User user;
    private GridViewAdapter adapter;
    private Bitmap bitmap;
    private File tmpFile;
    SafeProgressDialog pd;
    private String photoName;
    private ImageLoader imageLoader;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    adapter.notifyDataSetChanged();
                    saveData();
                    break;
                case 1:
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    if (msg.obj != null) {
                        Toast.makeText(PlatePhotosActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlatePhotosActivity.this, R.string.prompt_upload_img_failed, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    uploadPhoto(tmpFile);
                    break;
                case 3:
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    Toast.makeText(PlatePhotosActivity.this, R.string.prompt_unexpected_error_take_photo, Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    adapter.notifyDataSetChanged();
                    break;
                case 5:
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    StringBuilder stringBuilder = (StringBuilder) msg.obj;
                    String[] localFilePaths = stringBuilder.toString().split(";");
                    String[] urlPaths = user.getPlatePhotos().split(";");
                    for (int i = 0; i < localFilePaths.length; i++) {
                        localPaths.add(localFilePaths[i]);
                    }
                    for (int i = 0; i < urlPaths.length; i++) {
                        paths.add(urlPaths[i]);
                    }
                    user.setLocalPlatePhotos(stringBuilder.toString());
                    ZZQSApplication.getInstance().updateUser(user);
                    adapter.notifyDataSetChanged();
                    break;
                case 6:
                    Toast.makeText(PlatePhotosActivity.this, getString(R.string.prompt_save_file_failed), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imageFilePath", photoName);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            photoName = savedInstanceState.getString("imageFilePath");
            if (null != photoName) {
                tmpFile = new File(ContentData.BASE_PICS + "/" + photoName);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_user_plate);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pd.dismiss();
    }

    private int downloadNum = 0;

    private void downloadPhotos() {
        pd.setMessage(getString(R.string.prompt_dl_syncing_img));
        pd.setCancelable(false);
        pd.show();
        user.setLocalPlatePhotos("");
        final StringBuilder StringBuilder = new StringBuilder();
        final String[] photoKeys = user.getPlatePhotos().split(";");

        for (int i = 0; i < photoKeys.length; i++) {
            String[] key = photoKeys[i].split("/");
            String fileName = ContentData.BASE_DIR + "/" + key[key.length - 1];
            StringBuilder.append(fileName);
            if (i < photoKeys.length - 1) {
                StringBuilder.append(";");
            }
            ImageUtil.downloadBitmap(getApplicationContext(), CommonFiled.QINIU_ZOOM + photoKeys[i], fileName, new ImageUtil.ImageUtilCallback() {
                @Override
                public void onSuccess(Object object) {
                    ++downloadNum;
                    if (downloadNum == photoKeys.length) {
                        Message msg = handler.obtainMessage();
                        msg.what = 5;
                        msg.obj = StringBuilder;
                        handler.sendMessage(msg);
                    }
                }

                @Override
                public void onFailure(Object object) {
                }
            });
        }
    }

    private void loadPlatePhotos() {
        String plateNumbers = user.getPlateNumbers();
        if (!StringTools.isEmp(plateNumbers)) {
            if (plateNumbers.contains(";")) {
                String[] texts = plateNumbers.split(";");
                for (int i = 0; i < texts.length; i++) {
                    numbers.add(texts[i]);
                }
            } else {
                numbers.add(plateNumbers);
            }
        }
        boolean existLocalPhotos = !StringTools.isEmp(user.getLocalPlatePhotos());
        boolean existPlatePhotos = !StringTools.isEmp(user.getPlatePhotos());
        if (existLocalPhotos && existPlatePhotos) {
            String[] localPlatePhotos = user.getLocalPlatePhotos().split(";");
            String[] platePhotos = user.getPlatePhotos().split(";");
            if (localPlatePhotos.length == platePhotos.length) {
                for (int i = 0; i < platePhotos.length; i++) {
                    File file = new File(localPlatePhotos[i]);
                    if (file.exists()) {
                        localPaths.add(localPlatePhotos[i]);
                        paths.add(platePhotos[i]);
                    } else {
                        downloadPhotos();
                        return;
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                downloadPhotos();
            }
        } else if (existLocalPhotos && !existPlatePhotos) {
            user.setLocalPlatePhotos("");
            DaoManager.getUserDao(getApplicationContext()).update(user);
        } else if (!existLocalPhotos && existPlatePhotos) {
            downloadPhotos();
        }
    }

    private void init() {
        localPaths = new ArrayList<>();
        paths = new ArrayList<>();
        numbers = new ArrayList<>();
        user = getIntent().getParcelableExtra(User.USER);
        pd = new SafeProgressDialog(this);
        imageLoader = ImageLoader.getInstance();
        gridView = (GridView) findViewById(R.id.plate_photos);
        title = (TextView) findViewById(R.id.head_title);
        title.setText(R.string.view_tv_plate_img);
        headRight = (TextView) findViewById(R.id.head_right);
        headRight.setText(R.string.view_tv_add_plate);
        headRight.setVisibility(View.VISIBLE);
        findViewById(R.id.head_back).setOnClickListener(this);
        headRight.setOnClickListener(this);
        adapter = new GridViewAdapter();
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                DialogView.showBigImageDialog(PlatePhotosActivity.this, localPaths.get(i), numbers.get(i), null, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        switch (msg.what) {
                            case DialogView.DELETE:
                                paths.remove(i);
                                numbers.remove(i);
                                localPaths.remove(i);
                                saveData();
                                adapter.notifyDataSetChanged();
                                break;
                            case DialogView.DEFAULT:
                                if (StringTools.isEmp(paths.get(i)) || StringTools.isEmp(numbers.get(i))) {
                                    Toast.makeText(PlatePhotosActivity.this, R.string.prompt_system_err, Toast.LENGTH_SHORT).show();
                                } else {
                                    String defaultPlateNumbers = numbers.get(i);
                                    String defaultPlatePhoto = paths.get(i);
                                    String defaultLocalPlatePhoto = localPaths.get(i);
                                    numbers.remove(i);
                                    paths.remove(i);
                                    localPaths.remove(i);
                                    numbers.add(0, defaultPlateNumbers);
                                    paths.add(0, defaultPlatePhoto);
                                    localPaths.add(0, defaultLocalPlatePhoto);
                                    StringBuilder stringBuilder = new StringBuilder();
                                    for (String number : numbers) {
                                        stringBuilder.append(number + ";");
                                    }
                                    if (stringBuilder.length() > 1) {
                                        stringBuilder.substring(0, stringBuilder.length() - 1);
                                    }
                                    user.setPlateNumbers(stringBuilder.toString());
                                    stringBuilder.setLength(0);
                                    for (String path : paths) {
                                        stringBuilder.append(path + ";");
                                    }
                                    if (stringBuilder.length() > 1) {
                                        stringBuilder.substring(0, stringBuilder.length() - 1);
                                    }
                                    user.setPlatePhotos(stringBuilder.toString());
                                    stringBuilder.setLength(0);
                                    for (String path : localPaths) {
                                        stringBuilder.append(path + ";");
                                    }
                                    if (stringBuilder.length() > 1) {
                                        stringBuilder.substring(0, stringBuilder.length() - 1);
                                    }
                                    user.setLocalPlatePhotos(stringBuilder.toString());

                                    RestAPI.getInstance(PlatePhotosActivity.this).updateProfile(user, new RestAPI.RestResponse() {
                                        @Override
                                        public void onSuccess(Object object) {
                                            ZZQSApplication.getInstance().updateUser(user);
                                            Toast.makeText(PlatePhotosActivity.this, R.string.prompt_set_up_success, Toast.LENGTH_SHORT).show();
                                            Message msg = handler.obtainMessage();
                                            msg.what = 4;
                                            handler.sendMessage(msg);
                                        }

                                        @Override
                                        public void onFailure(Object object) {
                                            if (object.toString().equals("disconnected")) {
                                                DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, getString(R.string.prompt_dl_other_equipment_login_title), getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
                                                    @Override
                                                    public void handleMessage(Message msg) {
                                                        ZZQSApplication.getInstance().clearUser(PlatePhotosActivity.this);
                                                        ZZQSApplication.getInstance().cleanAllActivity();
                                                        startActivity(new Intent(PlatePhotosActivity.this, LoginActivity.class));
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(PlatePhotosActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                break;
                        }
                    }
                });
            }
        });
        loadPlatePhotos();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_back:
                finish();
                break;
            case R.id.head_right:
                addPlate();
                break;
        }
    }

    private String plateNumber = null;

    private void addPlate() {
        DialogView.inputInfo(this, DialogView.INPUT_PLATE_NUMBER, getResources().getString(R.string.prompt_dl_input_plate_number), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == DialogView.ACCEPT) {
                    plateNumber = (String) msg.obj;
                    final CharSequence[] items;
                    items = new CharSequence[]{"相机拍照", "相册选取", "关闭"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(PlatePhotosActivity.this);
                    builder.setTitle(R.string.prompt_dl_choice_img_source);
                    builder.setItems(items, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(final DialogInterface dialog, int item) {
                            if (items[item].equals("相机拍照")) {
                                photoName = user.getUsername() + "plate_id_photo" + System.currentTimeMillis() + ".jpg";
                                tmpFile = new File(ContentData.BASE_DIR + "/" + photoName);
                                if (!tmpFile.exists()) {
                                    try {
                                        tmpFile.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        return;
                                    }
                                }
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmpFile));
                                startActivityForResult(intent, UserInfoActivity.TAKE_A_PHOTO);
                            } else if (items[item].equals("相册选取")) {
                                photoName = user.getUsername() + "plate_id_photo" + System.currentTimeMillis() + ".jpg";
                                tmpFile = new File(ContentData.BASE_DIR + "/" + photoName);
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
                            } else {
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    /**
     * 保存
     */
    private void saveData() {
        StringBuilder stringBuilder1 = new StringBuilder();
        StringBuilder stringBuilder2 = new StringBuilder();
        StringBuilder stringBuilder3 = new StringBuilder();
        if (localPaths.size() == paths.size() && paths.size() == numbers.size()) {
            for (int i = 0; i < paths.size(); i++) {
                stringBuilder1.append(paths.get(i));
                stringBuilder2.append(numbers.get(i));
                stringBuilder3.append(localPaths.get(i));
                if (i < paths.size() - 1) {
                    stringBuilder1.append(";");
                    stringBuilder2.append(";");
                    stringBuilder3.append(";");
                }
            }
        } else {
            localPaths.clear();
            paths.clear();
            numbers.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(PlatePhotosActivity.this, R.string.view_tv_system_setting, Toast.LENGTH_SHORT).show();
        }
        user.setPlatePhotos(stringBuilder1.toString());
        user.setPlateNumbers(stringBuilder2.toString());
        user.setLocalPlatePhotos(stringBuilder3.toString());
        RestAPI.getInstance(this).updateProfile(user, new RestAPI.RestResponse() {
            @Override
            public void onSuccess(Object object) {
                if (pd != null) {
                    pd.dismiss();
                }
                ZZQSApplication.getInstance().updateUser(user);
                Toast.makeText(PlatePhotosActivity.this, R.string.prompt_save_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Object object) {
                if (pd != null) {
                    pd.dismiss();
                }
                if (object.toString().equals("disconnected")) {
                    DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, getString(R.string.prompt_dl_other_equipment_login_title), getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            ZZQSApplication.getInstance().clearUser(PlatePhotosActivity.this);
                            ZZQSApplication.getInstance().cleanAllActivity();
                            startActivity(new Intent(PlatePhotosActivity.this, LoginActivity.class));
                        }
                    });
                } else {
                    Toast.makeText(PlatePhotosActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            pd.setMessage(getString(R.string.prompt_dl_compressed_and_upload_img));
            pd.show();
            final Thread thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    if (requestCode == UserInfoActivity.TAKE_A_PHOTO) {
                        if (null != tmpFile && tmpFile.exists()) {
                            bitmap = BitmapFactory.decodeFile(tmpFile.getAbsolutePath());
                            int degree = ImageUtil.readPictureDegree(tmpFile.getAbsolutePath());
                            bitmap = ImageUtil.rotaingImageView(degree, bitmap);
                            bitmap = ImageUtil.comp(bitmap, 100, 256, 256);
                        } else {
                            handler.sendEmptyMessage(3);
                        }
                    } else if (requestCode == UserInfoActivity.SELECT_PHOTO) {
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
                    boolean isSuccess = ImageUtil.saveCompressBitmap(bitmap, tmpFile.getAbsolutePath(), 100);
                    if (isSuccess) {
                        handler.sendEmptyMessage(2);
                    }else{
                        if (tmpFile != null && tmpFile.exists()) {
                            tmpFile.delete();
                        }
                        handler.sendEmptyMessage(6);
                    }
                }
            };
            thread.start();
        }
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
                                localPaths.add(file.getAbsolutePath());
                                paths.add(file.getAbsolutePath());
                                numbers.add(plateNumber);
                                handler.sendEmptyMessage(0);
                            } else {
                                handler.sendEmptyMessage(1);
                                Log.d("qiniuUpload", responseInfo.toString());
                            }
                        }
                    }, null);
                }
            }

            @Override
            public void onFailure(Object object) {
                Message msg = handler.obtainMessage();
                msg.obj = object;
                msg.what = 1;
                handler.sendMessage(msg);
            }
        });
    }

    private class GridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return localPaths != null ? localPaths.size() : 0;
        }

        @Override
        public Object getItem(int i) {
            return localPaths.get(i);
        }

        @Override
        public long getItemId(int i) {
            return localPaths != null ? localPaths.size() : 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            final ViewHolder holder;
            String path = localPaths.get(i);
            String number = numbers.get(i);
            if (convertView == null) {
                convertView = LayoutInflater.from(PlatePhotosActivity.this).inflate(R.layout.item_gridview, null);
                holder = new ViewHolder();
                holder.img = (ImageView) convertView.findViewById(R.id.ivPhoto);
                holder.tv = (TextView) convertView.findViewById(R.id.textView);
                holder.mold = (TextView) convertView.findViewById(R.id.mold);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Uri uri = Uri.fromFile(new File(path));
            File f = new File(path);
            if (f.exists()) {
                imageLoader.displayImage(uri.toString(), holder.img);
            }
            holder.tv.setVisibility(View.VISIBLE);
            holder.tv.setText(number.toUpperCase() + "");
            if (i == 0) {
                holder.mold.setText(R.string.view_tv_default);
                holder.mold.setBackgroundColor(convertView.getResources().getColor(R.color.red));
            } else {
                holder.mold.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
    }

    private class ViewHolder {
        ImageView img;
        TextView tv, mold;
    }
}
