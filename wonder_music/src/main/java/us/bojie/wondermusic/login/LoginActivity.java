package us.bojie.wondermusic.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import us.bojie.lib_common_ui.base.BaseActivity;
import us.bojie.lib_network.okhttp.response.listener.DisposeDataListener;

/**
 * 登录页面
 */
public class LoginActivity extends BaseActivity implements DisposeDataListener {

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onSuccess(Object responseObj) {

    }

    @Override
    public void onFailure(Object reasonObj) {

    }
}
