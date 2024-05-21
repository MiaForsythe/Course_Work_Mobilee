package com.tochycomputerservices.orangeplayer.vx.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tochycomputerservices.orangeplayer.vx.Interfaces.OnProgressUpdate;
import com.tochycomputerservices.orangeplayer.vx.R;
import com.tochycomputerservices.orangeplayer.vx.Utils.Constants;
import com.tochycomputerservices.orangeplayer.vx.Utils.CursorHelper;
import com.tochycomputerservices.orangeplayer.vx.Utils.Logger;
import com.tochycomputerservices.orangeplayer.vx.Utils.MusicUtils;
import com.tochycomputerservices.orangeplayer.vx.Utils.PreferencesHelper;
import com.tochycomputerservices.orangeplayer.vx.Utils.TypefaceHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class SplashActivity extends AppCompatActivity implements OnProgressUpdate {

    private CompositeDisposable mCompositeDisposable;
    private ProgressBar mProgressBar;
    private RelativeLayout mProgressBarHolder;
    private TextView mTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mTitle = findViewById(R.id.title);
        mTitle.setTypeface(TypefaceHelper.getTypeface(getApplicationContext(), TypefaceHelper.FUTURA_BOLD));
        ((TextView) findViewById(R.id.building_library_task)).setTypeface(TypefaceHelper.getTypeface(getApplicationContext(), TypefaceHelper.FUTURA_CONDENSED));

        mProgressBar = findViewById(R.id.building_library_progress);
        mProgressBarHolder = findViewById(R.id.progress_elements_container);
        mCompositeDisposable = new CompositeDisposable();

        //Keeping the track of the launchcount to scan the library.

        int launchCount = PreferencesHelper.getInstance().getInt(PreferencesHelper.Key.LAUNCH_COUNT, 0);
        launchCount++;
        PreferencesHelper.getInstance().put(PreferencesHelper.Key.LAUNCH_COUNT, launchCount);

        launchMainActivity();
    }

    public void launchMainActivity() {
        if (!MusicUtils.isKitkat()) {
            if (checkAndRequestPermissions()) {
                buildLibrary();
            }
        } else {
            buildLibrary();
        }
    }

    private boolean checkAndRequestPermissions() {
        int modifyAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (modifyAudioPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Constants.REQUEST_PERMISSIONS);
            return false;
        }
        return true;
    }


    /**
     * Permission check if version is >= 6 (Marshmallow.)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (Constants.REQUEST_PERMISSIONS == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                buildLibrary();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
                builder.setTitle(R.string.grant_permission);
                builder.setMessage(R.string.grant_permission_message);
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SplashActivity.this.finish();
                    }
                });


                builder.setPositiveButton(R.string.open_settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", SplashActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        SplashActivity.this.startActivity(intent);

                    }
                });
                builder.create().show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }

    /**
     * Fade the title and show the building library text,
     * have just started with RxJava so might not be that good for background tasks.
     */

    private void buildLibrary() {
        fadeInFadeOut();
        mCompositeDisposable.add(Observable.fromCallable(() -> CursorHelper.buildMusicLibrary(this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        //Intent intent = new Intent((Context) SplashActivity.this, MainActivity.class);
                        Intent intent = new Intent((Context) SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.exp("" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );
    }

    /**
     * Fade in and out animations.
     */
    private void fadeInFadeOut() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation((Context) this, R.anim.fade_in);
        Animation fadeOutAnimation = (Animation) AnimationUtils.loadAnimation((Context) this, R.anim.fade_out);
        mTitle.startAnimation(fadeOutAnimation);
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTitle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mProgressBarHolder.startAnimation(fadeInAnimation);
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mProgressBarHolder.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    public void onProgressed(int progress) {
        mProgressBar.setProgress(progress);
    }

    @Override
    public void maxProgress(int max) {
        mProgressBar.setMax(max);
    }
}
