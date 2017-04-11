package com.visionvera.bmob.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.visionvera.bmob.R;
import com.visionvera.bmob.base.BaseActivity;
import com.visionvera.bmob.dialog.DrawerDialog;
import com.visionvera.bmob.event.RxEvent;
import com.visionvera.bmob.global.Constants;
import com.visionvera.bmob.global.UserHelper;
import com.visionvera.bmob.model.FileBean;
import com.visionvera.bmob.model.UserBean;
import com.visionvera.bmob.net.NetworkRequest;
import com.visionvera.bmob.net.ResponseSubscriber;
import com.visionvera.bmob.utils.FrescoUtil;
import com.visionvera.bmob.utils.IntentUtil;
import com.visionvera.bmob.utils.PermissionUtil;
import com.visionvera.bmob.utils.TextUtil;
import com.visionvera.bmob.utils.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    public static final int REQUEST_CAMERA_PERMISSION = 1001;
    public static final int REQUEST_CAMERA_ACTION = 101;
    public static final int REQUEST_GALLERY_ACTION = 102;
    public static final int REQUEST_CROP_ACTION = 103;

    private SimpleDraweeView register_avatar_sdv;
    private TextInputLayout register_account_ll;
    private TextInputEditText register_account_et;
    private TextInputLayout register_password_ll;
    private TextInputEditText register_password_et;
    private TextInputLayout register_phone_ll;
    private TextInputEditText register_phone_et;
    private TextInputLayout register_signature_ll;
    private TextInputEditText register_signature_et;
    private RadioGroup register_gender_rg;
    private Button register_confirm_bt;
    private String mAvatar;
    private Uri mAvatarUri;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        register_avatar_sdv = (SimpleDraweeView) findViewById(R.id.register_avatar_sdv);
        register_account_ll = (TextInputLayout) findViewById(R.id.register_account_ll);
        register_account_et = (TextInputEditText) findViewById(R.id.register_account_et);
        register_password_ll = (TextInputLayout) findViewById(R.id.register_password_ll);
        register_password_et = (TextInputEditText) findViewById(R.id.register_password_et);
        register_phone_ll = (TextInputLayout) findViewById(R.id.register_phone_ll);
        register_phone_et = (TextInputEditText) findViewById(R.id.register_phone_et);
        register_signature_ll = (TextInputLayout) findViewById(R.id.register_signature_ll);
        register_signature_et = (TextInputEditText) findViewById(R.id.register_signature_et);
        register_gender_rg = (RadioGroup) findViewById(R.id.register_gender_rg);
        register_confirm_bt = (Button) findViewById(R.id.register_confirm_bt);

        register_account_ll.setCounterEnabled(true);
        register_password_ll.setCounterEnabled(true);
        register_phone_ll.setCounterEnabled(true);
        register_signature_ll.setCounterEnabled(true);

        register_password_ll.setPasswordVisibilityToggleEnabled(true);

        register_confirm_bt.setOnClickListener(this);
        register_avatar_sdv.setOnClickListener(this);
    }

    @Override
    protected void onEventMainThread(RxEvent rxEvent) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register_confirm_bt) {
            String account = register_account_et.getText().toString();
            String password = register_password_et.getText().toString();
            String phone = register_phone_et.getText().toString();
            String signature = register_signature_et.getText().toString();

            if (TextUtil.isEmpty(account)) {
                register_account_ll.setError("empty account.");
                return;
            }
            if (TextUtil.isEmpty(password)) {
                register_password_ll.setError("empty password.");
                return;
            }
            if (!TextUtil.isValidateChars(password, 6, 12)) {
                register_password_ll.setError("invalidate password (6-12 characters).");
                return;
            }
            if (TextUtil.isEmpty(phone)) {
                register_phone_ll.setError("empty phone.");
                return;
            }
            if (!TextUtil.isMobileNumber(phone)) {
                register_phone_ll.setError("invalidate phone.");
                return;
            }
            if (TextUtil.isEmpty(signature)) {
                register_signature_ll.setError("empty signature.");
                return;
            }

            NetworkRequest.getInstance().postRegister(this, account, password, phone, signature,
                    register_gender_rg.getCheckedRadioButtonId() == R.id.register_gender_female_rb ? Constants.GENDER_FEMALE : Constants.GENDER_MALE,
                    mAvatar, new ResponseSubscriber<UserBean>() {
                        @Override
                        public void onSuccess(UserBean userBean) {
                            ToastUtil.showToast("register success.");
                            UserHelper.saveIsLogin(true);
                            UserHelper.saveUser(userBean);
                            IntentUtil.toMainActivity(RegisterActivity.this);
                            finish();
                        }

                        @Override
                        public void onFailure(int code, String error) {
                            register_account_ll.setError("code:" + code + " error:" + error);
                        }
                    });

        } else if (v.getId() == R.id.register_avatar_sdv) {
            if (!PermissionUtil.hasCameraPermission()) {
                PermissionUtil.requestCameraPermission(RegisterActivity.this, REQUEST_CAMERA_PERMISSION);
                return;
            }
            new DrawerDialog(this)
                    .addDrawer("相机")
                    .addDrawer("相册")
                    .setOnItemClickedListener(new DrawerDialog.OnItemClickedListener() {
                        @Override
                        public void onItemClick(int position) {
                            if (position == 1) {
                                toCamera();
                            } else if (position == 2) {
                                toGallery();
                            }
                        }
                    })
                    .show();
        }
    }

    private void toCamera() {
        File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        mAvatarUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), file);//通过FileProvider创建一个content类型的Uri
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mAvatarUri);
        startActivityForResult(intent, REQUEST_CAMERA_ACTION);
    }

    private void toGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        startActivityForResult(intent, REQUEST_GALLERY_ACTION);
    }

    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", false);// 人脸识别
        intent.putExtra("circleCrop", "true");
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_CROP_ACTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_ACTION) {
                crop(mAvatarUri);
            } else if (requestCode == REQUEST_GALLERY_ACTION) {
                if (data != null) {
                    crop(data.getData());
                }
            } else if (requestCode == REQUEST_CROP_ACTION) {
                final Bitmap bitmap = data.getParcelableExtra("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                NetworkRequest.getInstance().postFile(this, System.currentTimeMillis() + ".jpg", stream.toByteArray(), new ResponseSubscriber<FileBean>() {
                    @Override
                    public void onSuccess(FileBean fileBean) {
                        if (fileBean != null) {
                            FrescoUtil.display(register_avatar_sdv, fileBean.url);
                            mAvatar = fileBean.url;
                        }
                        bitmap.recycle();
                    }

                    @Override
                    public void onFailure(int code, String error) {
                        ToastUtil.networkFailure(error);
                        bitmap.recycle();
                    }
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                onClick(register_avatar_sdv);
            } else {
                ToastUtil.showToast("permission declined");
            }
        }
    }
}
