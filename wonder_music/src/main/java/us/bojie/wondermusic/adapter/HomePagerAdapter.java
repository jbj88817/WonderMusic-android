package us.bojie.wondermusic.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import us.bojie.wondermusic.discovery.DiscoveryFragment;
import us.bojie.wondermusic.friend.FriendFragment;
import us.bojie.wondermusic.mine.MineFragment;
import us.bojie.wondermusic.model.CHANNEL;

/**
 * 首页ViewPager的Adapter
 */
public class HomePagerAdapter extends FragmentPagerAdapter {
    private CHANNEL[] mList;

    public HomePagerAdapter(FragmentManager fm, CHANNEL[] datas) {
        super(fm);
        mList = datas;
    }

    //初始化对应的fragment
    @Override
    public Fragment getItem(int position) {
        int type = mList[position].getValue();
        switch (type) {
            case CHANNEL.MINE_ID:
                return MineFragment.newInstance();
            case CHANNEL.DISCORY_ID:
                return DiscoveryFragment.newInstance();
            case CHANNEL.FRIEND_ID:
                return FriendFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.length;
    }
}
