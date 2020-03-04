package us.bojie.wondermusic.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.Nullable;
import us.bojie.lib_common_ui.base.BaseActivity;
import us.bojie.lib_network.okhttp.response.listener.DisposeDataListener;
import us.bojie.wondermusic.api.RequestCenter;
import us.bojie.wondermusic.databinding.ActivityLoginLayoutBinding;
import us.bojie.wondermusic.login.manager.UserManager;
import us.bojie.wondermusic.login.user.LoginEvent;
import us.bojie.wondermusic.login.user.User;

/**
 * 登录页面
 */
public class LoginActivity extends BaseActivity implements DisposeDataListener {

    private ActivityLoginLayoutBinding mBinding;

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityLoginLayoutBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.loginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestCenter.login(LoginActivity.this);
            }
        });
    }

    @Override
    public void onSuccess(Object responseObj) {
        User user = (User) responseObj;
        UserManager.getInstance().saveUser(user);
        EventBus.getDefault().post(new LoginEvent());
        finish();
    }

    @Override
    public void onFailure(Object reasonObj) {
        Toast.makeText(this, "Failed login!", Toast.LENGTH_SHORT).show();
    }
}
