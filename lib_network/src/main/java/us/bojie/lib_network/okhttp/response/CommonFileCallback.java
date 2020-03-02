package us.bojie.lib_network.okhttp.response;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Response;
import us.bojie.lib_network.okhttp.exception.OkHttpException;
import us.bojie.lib_network.okhttp.response.listener.DisposeDataHandle;
import us.bojie.lib_network.okhttp.response.listener.DisposeDownloadListener;

/**
 * 处理文件类型的响应
 */
public class CommonFileCallback extends CommonCallback {
    /**
     * 将其它线程的数据转发到UI线程
     */
    private static final int PROGRESS_MESSAGE = 0x01;
    private Handler mDeliveryHandler;
    private DisposeDownloadListener mListener;
    private String mFilePath;
    private Integer mProgress;


    public CommonFileCallback(DisposeDataHandle handle) {
        this.mListener = (DisposeDownloadListener) handle.mListener;
        this.mFilePath = handle.mSource;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case PROGRESS_MESSAGE:
                    default:
                        mListener.onProgress((Integer) msg.obj);
                        break;
                }
            }
        };
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkHttpException(NETWORK_ERROR, e));
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final File file = handleResponse(response);
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                if (file != null) {
                    mListener.onSuccess(file);
                } else {
                    mListener.onFailure(new OkHttpException(IO_ERROR, EMPTY_MSG));
                }
            }
        });
    }


    private File handleResponse(Response response) {
        if (response == null) {
            return null;
        }
        InputStream is = null;
        File file = null;
        FileOutputStream fos = null;
        byte[] buffer = new byte[2048];
        int length;
        double curLng = 0;
        double sumLength;

        try {
            checkLocalFilePath(mFilePath);
            file = new File(mFilePath);
            fos = new FileOutputStream(file);
            is = response.body().byteStream();
            sumLength = response.body().contentLength();
            while ((length = is.read(buffer)) != -1) {
                fos.write(buffer, 0, buffer.length);
                curLng += length;
                mProgress = (int) (curLng / sumLength * 100);
                mDeliveryHandler.obtainMessage(PROGRESS_MESSAGE, mProgress).sendToTarget();
            }

            fos.flush();
        } catch (Exception e) {
            file = null;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                file = null;
            }
        }
        return file;
    }

    private void checkLocalFilePath(String localFilePath) {
        File path = new File(localFilePath.substring(0,
                localFilePath.lastIndexOf("/") + 1));
        File file = new File(localFilePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
