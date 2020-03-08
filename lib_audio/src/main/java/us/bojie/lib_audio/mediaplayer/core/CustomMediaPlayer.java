package us.bojie.lib_audio.mediaplayer.core;

import android.media.MediaPlayer;

import java.io.IOException;

public class CustomMediaPlayer extends MediaPlayer implements MediaPlayer.OnCompletionListener {

    private OnCompletionListener mListener;

    public enum Status {
        IDEL, INITALIZED, STARTED, PAUSED, STOPPED, COMPLETED
    }

    private Status mStatus = Status.IDEL;

    public CustomMediaPlayer() {
        super();
        super.setOnCompletionListener(this);
    }

    @Override
    public void reset() {
        super.reset();
        mStatus = Status.IDEL;
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(path);
        mStatus = Status.INITALIZED;
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        mStatus = Status.STARTED;
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        mStatus = Status.PAUSED;
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        mStatus = Status.STOPPED;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mStatus = Status.COMPLETED;
    }

    public Status getStatus() {
        return mStatus;
    }

    public boolean isComplete() {
        return mStatus == Status.COMPLETED;
    }


    public void setCompletedListener(OnCompletionListener listener) {
        mListener = listener;
    }
}
