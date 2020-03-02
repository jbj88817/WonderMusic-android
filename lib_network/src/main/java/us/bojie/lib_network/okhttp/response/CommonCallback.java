package us.bojie.lib_network.okhttp.response;

import okhttp3.Callback;

public abstract class CommonCallback implements Callback {

    /**
     * the java layer exception, do not same to the logic error
     */
    protected final int NETWORK_ERROR = -1; // the network relative error
    protected final int JSON_ERROR = -2; // the JSON relative error
    protected final int IO_ERROR = -3; // the JSON relative error
    protected final String EMPTY_MSG = "";

}
