package org.apache.cordova.videoplayer;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.VideoView;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi;
import org.json.JSONException;
import org.json.JSONObject;

public class VideoPlayer extends CordovaPlugin implements OnCompletionListener, OnPreparedListener, OnErrorListener {

    protected static final String LOG_TAG = "VideoPlayer";

    protected static final String ASSETS = "/android_asset/";

    private Dialog dialog;

    private VideoView videoView;

    private MediaPlayer player;
    /**
     * Executes the request and returns PluginResult.
     *
     * @param action        The action to execute.
     * @param args          JSONArray of arguments for the plugin.
     * @param callbackId    The callback id used when calling back into JavaScript.
     * @return              A PluginResult object with a status and message.
     */
    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("play")) {
            CordovaResourceApi resourceApi = webView.getResourceApi();
            String target = args.getString(0);
            final JSONObject options = args.getJSONObject(1);

            String fileUriStr;
            try {
                Uri targetUri = resourceApi.remapUri(Uri.parse(target));
                fileUriStr = targetUri.toString();
            } catch (IllegalArgumentException e) {
                fileUriStr = target;
            }

            Log.v(LOG_TAG, fileUriStr);

            final String path = stripFileProtocol(fileUriStr);

            // Create dialog in new thread
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    openVideoDialog(path, options, callbackContext);
                }
            });

            callbackContext.success();

            return true;
        }
        return false;
    }

    /**
     * Removes the "file://" prefix from the given URI string, if applicable.
     * If the given URI string doesn't have a "file://" prefix, it is returned unchanged.
     *
     * @param uriString the URI string to operate on
     * @return a path without the "file://" prefix
     */
    public static String stripFileProtocol(String uriString) {
        if (uriString.startsWith("file://")) {
            return Uri.parse(uriString).getPath();
        }
        return uriString;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void openVideoDialog(String path, JSONObject options, final CallbackContext callbackContext) {
        // Let's create the main dialog
        dialog = new Dialog(cordova.getActivity(), android.R.style.Theme_NoTitleBar);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        // Main container layout
        LinearLayout main = new LinearLayout(cordova.getActivity());
        main.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        main.setOrientation(LinearLayout.VERTICAL);
        main.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        main.setVerticalGravity(Gravity.CENTER_VERTICAL);

        videoView = new VideoView(cordova.getActivity());
        videoView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        // videoView.setVideoURI(uri);
        // videoView.setVideoPath(path);
        main.addView(videoView);

        player = new MediaPlayer();
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

        if (path.startsWith(ASSETS)) {
            String f = path.substring(15);
            AssetFileDescriptor fd = null;
            try {
                fd = cordova.getActivity().getAssets().openFd(f);
                player.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            } catch (Exception e) {
                callbackContext.error(e.getLocalizedMessage());
            }
        }

        try {
            float volume = Float.valueOf(options.getString("volume"));
            player.setVolume(volume, volume);
        } catch (Exception e) {
            callbackContext.error(e.getLocalizedMessage());
        }

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            try {
                int scalingMode = options.getInt("scalingMode");
                switch (scalingMode) {
                    case MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING:
                        player.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                        break;
                    default:
                        player.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                }
            } catch (Exception e) {
                callbackContext.error(e.getLocalizedMessage());
            }
        }

        final SurfaceHolder mHolder = videoView.getHolder();
        mHolder.setKeepScreenOn(true);
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                player.setDisplay(holder);
                try {
                    player.prepare();
                } catch (Exception e) {
                    callbackContext.error(e.getLocalizedMessage());
                }
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                player.release();
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.setContentView(main);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(LOG_TAG, "AudioPlayer.onError(" + what + ", " + extra + ")");
        mp.stop();
        mp.release();
        dialog.dismiss();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.release();
        dialog.dismiss();
    }
}