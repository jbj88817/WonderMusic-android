package us.bojie.wondermusic.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import us.bojie.lib_common_ui.base.BaseActivity;
import us.bojie.lib_common_ui.pager_indictor.ScaleTransitionPagerTitleView;
import us.bojie.lib_image_loader.app.ImageLoaderManager;
import us.bojie.wondermusic.R;
import us.bojie.wondermusic.adapter.HomePagerAdapter;
import us.bojie.wondermusic.databinding.ActivityHomeBinding;
import us.bojie.wondermusic.login.LoginActivity;
import us.bojie.wondermusic.login.manager.UserManager;
import us.bojie.wondermusic.login.user.LoginEvent;
import us.bojie.wondermusic.model.CHANNEL;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private static final CHANNEL[] CHANNELS =
            new CHANNEL[]{CHANNEL.MY, CHANNEL.DISCORY, CHANNEL.FRIEND};
    private ActivityHomeBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initMagicIndicator();
        mBinding.logoutLayout.setOnClickListener(this);
        HomePagerAdapter adapter = new HomePagerAdapter(getSupportFragmentManager(), CHANNELS);
        mBinding.viewPager.setAdapter(adapter);
        mBinding.toggleView.setOnClickListener(this);
    }

    private void initMagicIndicator() {
        MagicIndicator magicIndicator = mBinding.magicIndicator;
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return CHANNELS == null ? 0 : CHANNELS.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setText(CHANNELS[index].getKey());
                simplePagerTitleView.setTextSize(19);
                simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(HomeActivity.this, R.color.color_999999));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(HomeActivity.this, R.color.color_333333));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBinding.viewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }

            @Override
            public float getTitleWeight(Context context, int index) {
                return 1.0f;
            }
        });

        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mBinding.viewPager);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        DrawerLayout drawerLayout = mBinding.drawerLayout;
        switch (v.getId()) {
            case R.id.logout_layout:
                if (!UserManager.getInstance().hasLogin()) {
                    LoginActivity.start(this);
                } else {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
                break;
            case R.id.toggle_view:
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
                break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
        mBinding.logoutLayout.setVisibility(View.GONE);
        ImageView photoView = mBinding.avatarView;
        photoView.setVisibility(View.VISIBLE);
        ImageLoaderManager.getInstance().displayImageForCircle(photoView,
                UserManager.getInstance().getUser().data.photoUrl);
    }
}
