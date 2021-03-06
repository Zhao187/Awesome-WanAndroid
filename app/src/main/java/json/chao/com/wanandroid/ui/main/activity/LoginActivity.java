package json.chao.com.wanandroid.ui.main.activity;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import json.chao.com.wanandroid.component.RxBus;
import json.chao.com.wanandroid.core.DataManager;
import json.chao.com.wanandroid.core.bean.LoginData;
import json.chao.com.wanandroid.core.bean.LoginResponse;
import json.chao.com.wanandroid.R;
import json.chao.com.wanandroid.base.BaseActivity;
import json.chao.com.wanandroid.contract.LoginContract;
import json.chao.com.wanandroid.core.event.LoginEvent;
import json.chao.com.wanandroid.presenter.LoginPresenter;
import json.chao.com.wanandroid.utils.CommonUtils;
import json.chao.com.wanandroid.widget.RegisterPopupWindow;

/**
 * @author quchao
 * @date 2018/2/26
 */

public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.View, View.OnClickListener {

    @BindView(R.id.login_group)
    RelativeLayout mLoginGroup;
    @BindView(R.id.login_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.login_iv)
    ImageView mThemeIv;
    @BindView(R.id.login_account_edit)
    EditText mAccountEdit;
    @BindView(R.id.login_password_edit)
    EditText mPasswordEdit;
    @BindView(R.id.login_btn)
    Button mLoginBtn;
    @BindView(R.id.login_register_btn)
    Button mRegisterBtn;

    @Inject
    DataManager mDataManager;
    private RegisterPopupWindow mPopupWindow;

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initEventAndData() {
        mRegisterBtn.setOnClickListener(this);
        mPopupWindow = new RegisterPopupWindow(this, this);
        mPopupWindow.setAnimationStyle(R.style.popup_window_animation);
        mPopupWindow.setOnDismissListener(() -> {
            setBackgroundAlpha(1.0f);
            mRegisterBtn.setOnClickListener(this);
        });
        mToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
    }

    @Override
    public void showLoginData(LoginResponse loginResponse) {
        if (loginResponse == null || loginResponse.getData() == null) {
            showLoginFail();
            return;
        }
        LoginData loginData = loginResponse.getData();
        mDataManager.setLoginAccount(loginData.getUsername());
        mDataManager.setLoginPassword(loginData.getPassword());
        mDataManager.setLoginStatus(true);
        RxBus.getDefault().post(new LoginEvent());
        CommonUtils.showMessage(this, getString(R.string.login_success));
        onBackPressedSupport();
    }

    @Override
    public void showRegisterData(LoginResponse loginResponse) {
        if (loginResponse == null || loginResponse.getData() == null) {
            showRegisterFail();
            return;
        }
        mPresenter.getLoginData(loginResponse.getData().getUsername(),
                loginResponse.getData().getPassword());
    }

    @Override
    public void showLoginFail() {
        CommonUtils.showMessage(this, getString(R.string.login_fail));
    }

    @Override
    public void showRegisterFail() {
        CommonUtils.showMessage(this, getString(R.string.register_fail));
    }

    @OnClick({R.id.login_btn})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                String account = mAccountEdit.getText().toString().trim();
                String password = mPasswordEdit.getText().toString().trim();
                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                    CommonUtils.showMessage(this, getString(R.string.account_password_null_tint));
                    return;
                }
                mPresenter.getLoginData(account, password);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_register_btn:
                //设置背景窗口Alpha
                setBackgroundAlpha(0.5f);
                mPopupWindow.showAtLocation(mLoginGroup, Gravity.CENTER, 0, 0);
                mRegisterBtn.setOnClickListener(null);
                break;
            case R.id.register_btn:
                register();
            default:
                break;
        }
    }

    private void register() {
        if (mPopupWindow == null) {
            return;
        }

        String account = mPopupWindow.mUserNameEdit.getText().toString().trim();
        String password = mPopupWindow.mPasswordEdit.getText().toString().trim();
        String rePassword = mPopupWindow.mRePasswordEdit.getText().toString().trim();

        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password) || TextUtils.isEmpty(rePassword)) {
            CommonUtils.showMessage(this, getString(R.string.account_password_null_tint));
            return;
        }

        if (!password.equals(rePassword)) {
            CommonUtils.showMessage(this, getString(R.string.password_not_same));
            return;
        }

        mPresenter.getRegisterData(account, password, rePassword);
    }

    /**
     * 设置屏幕透明度
     *
     * @param bgAlpha background alpha
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // 0.0~1.0
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

}
