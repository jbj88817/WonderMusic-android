package us.bojie.lib_audio.mediaplayer.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import us.bojie.lib_audio.mediaplayer.app.AudioHelper;
import us.bojie.lib_audio.mediaplayer.events.AudioCompleteEvent;
import us.bojie.lib_audio.mediaplayer.events.AudioErrorEvent;
import us.bojie.lib_audio.mediaplayer.events.AudioLoadEvent;
import us.bojie.lib_audio.mediaplayer.events.AudioPauseEvent;
import us.bojie.lib_audio.mediaplayer.events.AudioReleaseEvent;
import us.bojie.lib_audio.mediaplayer.events.AudioStartEvent;
import us.bojie.lib_audio.mediaplayer.model.AudioBean;

public class AudioPlayer implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        AudioFocusManager.AudioFocusListener {

    private static final String TAG = "AudioPlayer";
    private static final int TIME_MSG = 0x01;
    private static final int TIME_INTERVAL = 100;

    private CustomMediaPlayer mMediaPlayer;
    private WifiManager.WifiLock mWifiLock;
    private AudioFocusManager mAudioFocusManager;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case TIME_MSG:
                    break;
            }
        }
    };
    private boolean mIsPauseByFocusLossTransient;

    public AudioPlayer() {
        init();
    }

    private void init() {
        Context context = AudioHelper.getContext();
        mMediaPlayer = new CustomMediaPlayer();
        mMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnErrorListener(this);

        mWifiLock = ((WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);

        mAudioFocusManager = new AudioFocusManager(context, this);
    }

    public void load(AudioBean audioBean) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(audioBean.mUrl);
            mMediaPlayer.prepareAsync();
            EventBus.getDefault().post(new AudioLoadEvent(audioBean));
        } catch (Exception e) {
            EventBus.getDefault().post(new AudioErrorEvent());
        }
    }

    public void pause() {
        if (getStatus() == CustomMediaPlayer.Status.STARTED) {
            mMediaPlayer.pause();
            releaseAudioFocusAndWifiLock();
            EventBus.getDefault().post(new AudioPauseEvent());
        }
    }

    public void resume() {
        if (getStatus() == CustomMediaPlayer.Status.PAUSED) {
            start();
        }
    }

    public void release() {
        if (mMediaPlayer == null) {
            return;
        }

        mMediaPlayer.release();
        mMediaPlayer = null;
        releaseAudioFocusAndWifiLock();
        mWifiLock = null;
        mAudioFocusManager = null;
        EventBus.getDefault().post(new AudioReleaseEvent());
    }

    private void start() {
        if (!mAudioFocusManager.requestAudioFocus()) {
            Log.e(TAG, "request audio failed");
            return;
        }

        mMediaPlayer.start();
        mWifiLock.acquire();
        EventBus.getDefault().post(new AudioStartEvent());
    }

    CustomMediaPlayer.Status getStatus() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getStatus();
        }
        return CustomMediaPlayer.Status.STOPPED;
    }

    private void releaseAudioFocusAndWifiLock() {
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
        if (mAudioFocusManager != null) {
            mAudioFocusManager.abandonAudioFocus();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        EventBus.getDefault().post(new AudioCompleteEvent());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        EventBus.getDefault().post(new AudioErrorEvent());
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        start();
    }

    @Override
    public void audioFocusGrant() {
        setVolume(1.0f, 1.0f);
        if (mIsPauseByFocusLossTransient) {
            resume();
        }
        mIsPauseByFocusLossTransient = false;
    }

    @Override
    public void audioFocusLoss() {
        pause();
    }

    @Override
    public void audioFocusLossTransient() {
        pause();
        mIsPauseByFocusLossTransient = true;
    }

    @Override
    public void audioFocusLossDuck() {
        // 瞬间失去焦点
        setVolume(0.5f, 0.5f);
    }

    private void setVolume(float leftVol, float rightVol) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(leftVol, rightVol);
        }
    }
}
