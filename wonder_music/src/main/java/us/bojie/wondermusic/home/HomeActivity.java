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

import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import us.bojie.lib_audio.mediaplayer.core.AudioController;
import us.bojie.lib_audio.mediaplayer.model.AudioBean;
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
    private ArrayList<AudioBean> mLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
        initData();
    }

    private void initData() {
        mLists.add(new AudioBean("100001", "http://sp-sycdn.kuwo.cn/resource/n2/85/58/433900159.mp3",
                "以你的名字喊我", "周杰伦", "七里香", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698076304&di=e6e99aa943b72ef57b97f0be3e0d2446&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fblog%2F201401%2F04%2F20140104170315_XdG38.jpeg",
                "4:30"));
        mLists.add(
                new AudioBean("100002", "http://sq-sycdn.kuwo.cn/resource/n1/98/51/3777061809.mp3", "勇气",
                        "梁静茹", "勇气", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698193627&di=711751f16fefddbf4cbf71da7d8e6d66&imgtype=jpg&src=http%3A%2F%2Fimg0.imgtn.bdimg.com%2Fit%2Fu%3D213168965%2C1040740194%26fm%3D214%26gp%3D0.jpg",
                        "4:40"));
        mLists.add(
                new AudioBean("100003", "http://sp-sycdn.kuwo.cn/resource/n2/52/80/2933081485.mp3", "灿烂如你",
                        "汪峰", "春天里", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698239736&di=3433a1d95c589e31a36dd7b4c176d13a&imgtype=0&src=http%3A%2F%2Fpic.zdface.com%2Fupload%2F201051814737725.jpg",
                        "3:20"));
        mLists.add(
                new AudioBean("100004", "http://sr-sycdn.kuwo.cn/resource/n2/33/25/2629654819.mp3", "小情歌",
                        "五月天", "小幸运", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698289780&di=5146d48002250bf38acfb4c9b4bb6e4e&imgtype=0&src=http%3A%2F%2Fpic.baike.soso.com%2Fp%2F20131220%2Fbki-20131220170401-1254350944.jpg",
                        "2:45"));

        AudioController.getInstance().setQueue(mLists);
    }

    private void initView() {
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
                return CHANNELS.length;
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
