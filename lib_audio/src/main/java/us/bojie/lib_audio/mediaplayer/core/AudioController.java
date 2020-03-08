package us.bojie.lib_audio.mediaplayer.core;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;

import us.bojie.lib_audio.mediaplayer.exception.AudioQueueEmptyException;
import us.bojie.lib_audio.mediaplayer.model.AudioBean;

public class AudioController {

    private AudioPlayer mAudioPlayer;
    private ArrayList<AudioBean> mQueue;
    private PlayMode mPlayMode;
    private int mQueueIndex;

    /**
     * 播放方式
     */
    public enum PlayMode {
        /**
         * 列表循环
         */
        LOOP,
        /**
         * 随机
         */
        RANDOM,
        /**
         * 单曲循环
         */
        REPEAT
    }

    public static AudioController getInstance() {
        return AudioController.SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static AudioController instance = new AudioController();
    }

    private AudioController() {
        mAudioPlayer = new AudioPlayer();
        mQueue = new ArrayList<>();
        mQueueIndex = 0;
        mPlayMode = PlayMode.LOOP;
        EventBus.getDefault().register(this);
    }

    public ArrayList<AudioBean> getQueue() {
        return mQueue == null ? new ArrayList<AudioBean>() : mQueue;
    }

    public void setQueue(ArrayList<AudioBean> queue) {
        mQueue = queue;
    }

    public void setQueue(ArrayList<AudioBean> queue, int queueIndex) {
        mQueue.addAll(queue);
        mQueueIndex = queueIndex;
    }

    public PlayMode getPlayMode() {
        return mPlayMode;
    }

    public void setPlayMode(PlayMode playMode) {
        mPlayMode = playMode;
    }

    public int getPlayIndex() {
        return mQueueIndex;
    }

    public void setPlayIndex(int queueIndex) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("PlayList is empty");
        }
        mQueueIndex = queueIndex;
        play();
    }

    public void play() {
        AudioBean bean = getNowPlaying();
        mAudioPlayer.load(bean);
    }

    public void pause() {
        mAudioPlayer.pause();
    }

    public void resume() {
        mAudioPlayer.resume();
    }

    public void release() {
        mAudioPlayer.release();
        EventBus.getDefault().unregister(this);
    }

    public void next() {
        AudioBean bean = getNextPlaying();
        mAudioPlayer.load(bean);

    }

    public void previous() {
        AudioBean bean = getPreviousPlaying();
        mAudioPlayer.load(bean);
    }

    public void playOrPause() {
        if (isStartState()) {
            pause();
        } else if (isPauseState()) {
            resume();
        }
    }

    public void addAudio(AudioBean bean) {
        addAudio(0, bean);
    }

    public void addAudio(int index, AudioBean bean) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("PlayList is empty");
        }
        int query = queryAudio(bean);
        if (query <= -1) {
            //没添加过此id的歌曲，添加且直播番放
            addCustomAudio(index, bean);
            setPlayIndex(index);
        } else {
            AudioBean currentBean = getNowPlaying();
            if (!currentBean.id.equals(bean.id)) {
                //添加过且不是当前播放，播，否则什么也不干
                setPlayIndex(query);
            }
        }
    }

    private void addCustomAudio(int index, AudioBean bean) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("PlayList is empty.");
        }
        mQueue.add(index, bean);
    }

    private int queryAudio(AudioBean bean) {
        return mQueue.indexOf(bean);
    }

    private AudioBean getPreviousPlaying() {
        switch (mPlayMode) {
            case LOOP:
                mQueueIndex = (mQueueIndex + mQueue.size() - 1) % mQueue.size();
                return getPlaying(mQueueIndex);
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                return getPlaying(mQueueIndex);
            case REPEAT:
                return getPlaying(mQueueIndex);
        }
        return null;
    }

    private AudioBean getNowPlaying() {
        return getPlaying(mQueueIndex);
    }

    private AudioBean getNextPlaying() {
        switch (mPlayMode) {
            case LOOP:
                mQueueIndex = (mQueueIndex + 1) % mQueue.size();
                break;
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                break;
            case REPEAT:
                getPlaying(mQueueIndex);
                break;
        }
        return null;
    }

    private AudioBean getPlaying(int index) {
        if (mQueue != null && !mQueue.isEmpty() && index >= 0 && index < mQueue.size()) {
            return mQueue.get(index);
        } else {
            throw new AudioQueueEmptyException("PlayList is empty");
        }
    }

    public boolean isStartState() {
        return CustomMediaPlayer.Status.STARTED == getStatus();
    }

    public boolean isPauseState() {
        return CustomMediaPlayer.Status.PAUSED == getStatus();
    }

    private CustomMediaPlayer.Status getStatus() {
        return mAudioPlayer.getStatus();
    }
}
