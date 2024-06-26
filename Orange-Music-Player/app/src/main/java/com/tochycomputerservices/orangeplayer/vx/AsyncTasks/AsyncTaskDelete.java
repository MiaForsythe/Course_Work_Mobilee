package com.tochycomputerservices.orangeplayer.vx.AsyncTasks;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import com.tochycomputerservices.orangeplayer.vx.Common;
import com.tochycomputerservices.orangeplayer.vx.Interfaces.OnTaskCompleted;
import com.tochycomputerservices.orangeplayer.vx.Models.Song;
import com.tochycomputerservices.orangeplayer.vx.R;
import com.tochycomputerservices.orangeplayer.vx.Utils.CursorHelper;
import com.tochycomputerservices.orangeplayer.vx.Utils.FileUtils;
import com.tochycomputerservices.orangeplayer.vx.Utils.Logger;
import com.tochycomputerservices.orangeplayer.vx.Utils.MusicUtils;

import java.io.File;
import java.util.ArrayList;


public class AsyncTaskDelete extends AsyncTask<ArrayList<Song>, String, Boolean> implements MusicUtils.Defs {

    private ProgressDialog mProgressUpdateDialog;
    private Activity mActivity;
    private OnTaskCompleted listener;
    private ArrayList<Song> mPaths;
    private Common mApp;

    public AsyncTaskDelete(Activity context) {
        mActivity = context;
        mApp = (Common) context.getApplicationContext();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressUpdateDialog = new ProgressDialog(mActivity);
        mProgressUpdateDialog.setCancelable(false);
        mProgressUpdateDialog.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        mProgressUpdateDialog.setMessage(values[0]);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected Boolean doInBackground(ArrayList<Song>... params) {
        mPaths = params[0];
        try {
            for (int i = 0; i < mPaths.size(); i++) {
                File sourceFile = new File(mPaths.get(i)._path);
                publishProgress(Common.getInstance().getString(R.string.deleting) + " " + sourceFile.getName());

                long artistId = mPaths.get(i)._artistId;
                FileUtils.deleteRecursive(sourceFile);
                mApp.getDBAccessHelper().updateArtist(artistId);

            }
            return true;
        } catch (Exception e) {
            Logger.log(e.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        mProgressUpdateDialog.dismiss();
        if (result) {
            try {
                String message;
                if (mPaths.size() > 1) {
                    message = MusicUtils.makeLabel(mActivity, R.plurals.Nsongs, mPaths.size()) + " " + mActivity.getString(R.string.deleted);
                } else {
                    message = new File(mPaths.get(0)._path).getName() + " " + mActivity.getString(R.string.deleted);
                }

                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onSongDeleted();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(mActivity, R.string.song_failed_to_delete, Toast.LENGTH_SHORT).show();
        }
    }

    public void setListener(OnTaskCompleted listener) {
        this.listener = listener;
    }

}