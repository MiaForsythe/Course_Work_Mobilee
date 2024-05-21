package com.tochycomputerservices.orangeplayer.vx.LauncherActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tochycomputerservices.orangeplayer.vx.About;
import com.tochycomputerservices.orangeplayer.vx.Activities.DisplayMetricsHandler;
import com.tochycomputerservices.orangeplayer.vx.Activities.PrivacyPolicyScreen;
import com.tochycomputerservices.orangeplayer.vx.Activities.TracksSubFragment;
import com.tochycomputerservices.orangeplayer.vx.BuildConfig;
import com.tochycomputerservices.orangeplayer.vx.Common;
import com.tochycomputerservices.orangeplayer.vx.Equalizer.EqualizerActivity;
import com.tochycomputerservices.orangeplayer.vx.FileDirectory.FolderFragment;
import com.tochycomputerservices.orangeplayer.vx.Interfaces.OnScrolledListener;
import com.tochycomputerservices.orangeplayer.vx.R;
import com.tochycomputerservices.orangeplayer.vx.Search.SearchActivity;
import com.tochycomputerservices.orangeplayer.vx.Setting.SettingActivity;
import com.tochycomputerservices.orangeplayer.vx.Songs.SongsFragment;
import com.tochycomputerservices.orangeplayer.vx.SubGridViewFragment.TracksSubGridViewFragment;
import com.tochycomputerservices.orangeplayer.vx.Utils.Constants;
import com.tochycomputerservices.orangeplayer.vx.Utils.CursorHelper;
import com.tochycomputerservices.orangeplayer.vx.Utils.MusicUtils;
import com.tochycomputerservices.orangeplayer.vx.Utils.PreferencesHelper;
import com.tochycomputerservices.orangeplayer.vx.Utils.SortOrder;

import java.util.ArrayList;
import java.util.Random;



public class MainActivity extends AppCompatActivity implements OnScrolledListener {

    int row_index = 0;
    int posofItem = 0;

    private String[] title;
    private Drawable[] icon;

    RecyclerView.Adapter adapter;

    public ViewPager mViewPager;
    public SwipeAdapter mAdapter;
    private Context mContext;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private Menu mMenu;
    private AppBarLayout mAppBarLayout;
    private ArrayList<Fragment> mFragments;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        initViews();

    }

    private void initViews() {

        mContext = this;

        mFragments = new ArrayList<>();
        mTabLayout = findViewById(R.id.id_tabs);
        mViewPager = findViewById(R.id.view_pager);

        String[] tabs = getTabs();
        mAdapter = new SwipeAdapter(getSupportFragmentManager(), tabs);

        mViewPager.setAdapter(mAdapter);

        setDefaultTab(tabs);

        mViewPager.setOffscreenPageLimit(1);

        mTabLayout.setupWithViewPager(mViewPager);

        MusicUtils.changeTabsFont(mContext, mTabLayout);
        MusicUtils.applyFontForToolbarTitle(this);
        mToolbar = findViewById(R.id.toolbar);
        mAppBarLayout = findViewById(R.id.id_toolbar_container);


        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        params.topMargin = Common.getStatusBarHeight(this);
        // mAppBarLayout.setLayoutParams(params);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationOnClickListener(v -> {
            onPrepareOptionsMenu(mMenu);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        icon = loadScreenIcons();
        title = loadScreenTitles();
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(MainActivity.this, title, icon, posofItem);
        recyclerView.setAdapter(adapter);

    }

    private String[] loadScreenTitles() {

        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {

        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {

            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    private void setDefaultTab(String[] tabs) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Common.getInstance());
        String startup_screen = sharedPreferences.getString("preference_key_startup_screen", "SONGS");
        for (int i = 0; i < tabs.length; i++) {
            if (tabs[i].equalsIgnoreCase(startup_screen)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_search:
                Intent intent = new Intent((Context) MainActivity.this, SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.album_sort_default:
                PreferencesHelper.getInstance().put(PreferencesHelper.Key.ALBUM_SORT_ORDER, SortOrder.AlbumSortOrder.ALBUM_DEFAULT);
                (mAdapter.getFragment(0)).onResume();
                invalidateOptionsMenu();
                break;
            case R.id.album_sort_name:
                PreferencesHelper.getInstance().put(PreferencesHelper.Key.ALBUM_SORT_ORDER, SortOrder.AlbumSortOrder.ALBUM_NAME);
                (mAdapter.getFragment(0)).onResume();
                invalidateOptionsMenu();
                break;
            case R.id.album_sort_year:
                PreferencesHelper.getInstance().put(PreferencesHelper.Key.ALBUM_SORT_ORDER, SortOrder.AlbumSortOrder.ALBUM_YEAR);
                (mAdapter.getFragment(0)).onResume();
                invalidateOptionsMenu();
                break;
            case R.id.album_sort_artist_name:
                PreferencesHelper.getInstance().put(PreferencesHelper.Key.ALBUM_SORT_ORDER, SortOrder.AlbumSortOrder.ALBUM_ARTIST);
                (mAdapter.getFragment(0)).onResume();
                invalidateOptionsMenu();
                break;
            case R.id.album_sort_no_of_songs:
                PreferencesHelper.getInstance().put(PreferencesHelper.Key.ALBUM_SORT_ORDER, SortOrder.AlbumSortOrder.ALBUM_NUMBER_OF_SONGS);
                (mAdapter.getFragment(0)).onResume();
                invalidateOptionsMenu();
                break;
            case R.id.album_sort_type:
                if (PreferencesHelper.getInstance().getString(PreferencesHelper.Key.ALBUM_SORT_TYPE, Constants.ASCENDING).equalsIgnoreCase(Constants.ASCENDING)) {
                    PreferencesHelper.getInstance().put(PreferencesHelper.Key.ALBUM_SORT_TYPE, Constants.DESCENDING);
                } else {
                    PreferencesHelper.getInstance().put(PreferencesHelper.Key.ALBUM_SORT_TYPE, Constants.ASCENDING);
                }

                (mAdapter.getFragment(0)).onResume();
                invalidateOptionsMenu();
                break;

            case R.id.artist_sort_name:
                PreferencesHelper.getInstance().put(PreferencesHelper.Key.ARTIST_SORT_ORDER, SortOrder.ArtistSortOrder.ARTIST_NAME);
                (mAdapter.getFragment(1)).onResume();
                invalidateOptionsMenu();
                break;

            case R.id.artist_sort_no_of_albums:

                PreferencesHelper.getInstance().put(PreferencesHelper.Key.ARTIST_SORT_ORDER, SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_ALBUMS);
                (mAdapter.getFragment(1)).onResume();
                invalidateOptionsMenu();
                break;

            case R.id.artist_sort_no_of_songs:

                PreferencesHelper.getInstance().put(PreferencesHelper.Key.ARTIST_SORT_ORDER, SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_SONGS);
                (mAdapter.getFragment(1)).onResume();
                invalidateOptionsMenu();
                break;

            case R.id.artist_sort_type:

                if (PreferencesHelper.getInstance().getString(PreferencesHelper.Key.ARTIST_SORT_TYPE, Constants.ASCENDING).equalsIgnoreCase(Constants.ASCENDING)) {
                    PreferencesHelper.getInstance().put(PreferencesHelper.Key.ARTIST_SORT_TYPE, Constants.DESCENDING);
                } else {
                    PreferencesHelper.getInstance().put(PreferencesHelper.Key.ARTIST_SORT_TYPE, Constants.ASCENDING);
                }
                (mAdapter.getFragment(1)).onResume();
                invalidateOptionsMenu();
                break;




            case R.id.item_shuffle:

                ((SongsFragment) mAdapter.getFragment(2)).shuffleSongs();
                return true;
                /* case R.id.item_settings:
                startActivity(new Intent(mContext, SettingActivity.class));
                break;*/

        }
        return super.onOptionsItemSelected(item);
    }

    private String[] getTabs() {
        String titles = PreferencesHelper.getInstance().getString(PreferencesHelper.Key.TITLES);
        if (titles == null) {
            String[] tabTitles = new String[]{"ALBUMS", "ARTISTS", "SONGS",  "PLAYLISTS", "DIRECTORY"};
            CursorHelper.saveTabTitles(tabTitles);
            return tabTitles;
        } else {
            Gson gson = new Gson();
            return gson.fromJson(titles, String[].class);
        }
    }

    @Override
    public void onBackPressed() {
        if (mFragments.size() > 0) {

            Fragment fragment = mFragments.get(mFragments.size() - 1);
            if (fragment instanceof TracksSubFragment) {

                ((TracksSubFragment) fragment).removeFragment();
            }
            if (fragment instanceof TracksSubGridViewFragment) {

                ((TracksSubGridViewFragment) fragment).removeFragment();
            }

            mFragments.remove(fragment);
            return;
        }

        if (mAdapter.getFragment(mViewPager.getCurrentItem()) instanceof FolderFragment) {
            FolderFragment folderFragment = (FolderFragment) mAdapter.getFragment(mViewPager.getCurrentItem());
            if (folderFragment.getCurrentDir().equals("/")) {
                goHomeScreen();
            } else {

                folderFragment.getParentDir();
            }
        } else {


            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.super.onBackPressed();
                            quit();
                        }
                    }).create().show();
        }
    }

    public void quit() {
        Intent start = new Intent(Intent.ACTION_MAIN);
        start.addCategory(Intent.CATEGORY_HOME);
        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(start);
    }

    private void goHomeScreen() {


        try {

            Log.e("TAG", "onBackPressed: ");

            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(null);
            dialog.getWindow().setLayout((int) (DisplayMetricsHandler.getScreenWidth() - 50), android.widget.Toolbar.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            LinearLayout rateus, shareapp, nothnks;

            rateus = dialog.findViewById(R.id.rateus);
            shareapp = dialog.findViewById(R.id.shareapp);
            nothnks = dialog.findViewById(R.id.nothnks);

            rateus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));

                    } catch (android.content.ActivityNotFoundException anfe) {

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                    }

                }
            });

            shareapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {

                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                        String shareMessage = "Music Player" + "" + "\n\nLet me recommend you this application\n\n";
                        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                        startActivity(Intent.createChooser(shareIntent, "choose one"));
                        dialog.dismiss();

                    } catch (Exception e) {

                    }

                }
            });

            nothnks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.cancel();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                    finish();

                }
            });

        } catch (Exception e) {
            e.getMessage();
        }




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /*@Override
    public void onScrolledUp() {

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) findViewById(R.id.bottom_bar).getLayoutParams();
        int fabBottomMargin = lp.bottomMargin + 150;

        (findViewById(R.id.bottom_bar))
                .animate()
                .translationY(findViewById(R.id.bottom_bar).getHeight() + fabBottomMargin)
                .setInterpolator(new AccelerateInterpolator(2))
                .start();
    }

    @Override
    public void onScrolledDown() {
        (findViewById(R.id.bottom_bar))
                .animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(2));
    }*/

    public Menu getMenu() {
        return mMenu;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void addFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_parent, fragment);
        fragmentTransaction.commitAllowingStateLoss();

        mFragments.add(fragment);
    }


    @Override
    public void onScrolledUp() {

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) findViewById(R.id.bottom_bar).getLayoutParams();
        int fabBottomMargin = lp.bottomMargin + 150;

        (findViewById(R.id.bottom_bar))
                .animate()
                .translationY(findViewById(R.id.bottom_bar).getHeight() + fabBottomMargin)
                .setInterpolator(new AccelerateInterpolator(2))
                .start();
    }

    @Override
    public void onScrolledDown() {
        (findViewById(R.id.bottom_bar))
                .animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(2));
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private String[] screenTitles;
        private Drawable[] screenIcons;
        Context context1;

        public RecyclerViewAdapter(Context context2, String[] screenTitles, Drawable[] screenIcons, int posofItem) {

            this.screenTitles = screenTitles;
            this.screenIcons = screenIcons;
            context1 = context2;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView icon;
            public TextView title, txtCoin;
            public LinearLayout layoutItem;

            public ViewHolder(View v) {
                super(v);
                icon = (ImageView) v.findViewById(R.id.icon);
                title = (TextView) v.findViewById(R.id.title);
                layoutItem = (LinearLayout) v.findViewById(R.id.layoutItem);
            }
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view1 = LayoutInflater.from(context1).inflate(R.layout.item_option, parent, false);
            ViewHolder viewHolder1 = new ViewHolder(view1);
            return viewHolder1;
        }

//
//        @Override
//        public void onBindViewHolder(ViewHolder holder, final int position) {
//
//            holder.icon.setImageDrawable(screenIcons[position]);
//
//            holder.title.setText(screenTitles[position]);
//
//            holder.layoutItem.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    row_index = position;
//
//                    notifyDataSetChanged();
//
//                    if (position == 0) {
//
//                        Random rand = new Random();
//                        int randomNum = 0 + rand.nextInt(5);
//
//                        if (randomNum == 0) {
//                            if (!Common.getInstance().requestNewInterstitial()) {
//
//                                startActivity(new Intent(mContext, About.class));
//
//                            } else {
//
//                                Common.getInstance().mInterstitialAd.setAdListener(new AdListener() {
//                                    @Override
//                                    public void onAdClosed() {
//                                        super.onAdClosed();
//
//                                        Common.getInstance().mInterstitialAd.setAdListener(null);
//                                        Common.getInstance().mInterstitialAd = null;
//                                        Common.getInstance().ins_adRequest = null;
//                                        Common.getInstance().LoadAds();
//
//                                        startActivity(new Intent(mContext, About.class));
//
//                                    }
//
//                                    @Override
//                                    public void onAdFailedToLoad(int i) {
//                                        super.onAdFailedToLoad(i);
//                                    }
//
//                                    @Override
//                                    public void onAdLoaded() {
//                                        super.onAdLoaded();
//                                    }
//                                });
//                            }
//                        } else {
//
//                            startActivity(new Intent(mContext, About.class));
//                        }
//
//
//                    } else if (position == 1) {
//
//                        Random rand = new Random();
//                        int randomNum = 0 + rand.nextInt(5);
//
//                        if (randomNum == 0) {
//                            if (!Common.getInstance().requestNewInterstitial()) {
//
//                                mContext.startActivity(new Intent(mContext, EqualizerActivity.class));
//
//                            } else {
//
//                                Common.getInstance().mInterstitialAd.setAdListener(new AdListener() {
//                                    @Override
//                                    public void onAdClosed() {
//                                        super.onAdClosed();
//
//                                        Common.getInstance().mInterstitialAd.setAdListener(null);
//                                        Common.getInstance().mInterstitialAd = null;
//                                        Common.getInstance().ins_adRequest = null;
//                                        Common.getInstance().LoadAds();
//
//                                        mContext.startActivity(new Intent(mContext, EqualizerActivity.class));
//
//                                    }
//
//                                    @Override
//                                    public void onAdFailedToLoad(int i) {
//                                        super.onAdFailedToLoad(i);
//                                    }
//
//                                    @Override
//                                    public void onAdLoaded() {
//                                        super.onAdLoaded();
//                                    }
//                                });
//                            }
//                        } else {
//
//                            mContext.startActivity(new Intent(mContext, EqualizerActivity.class));
//
//                        }
//
//
//                    } else if (position == 2) {
//
//
//                        Random rand = new Random();
//                        int randomNum = 0 + rand.nextInt(5);
//
//                        if (randomNum == 0) {
//                            if (!Common.getInstance().requestNewInterstitial()) {
//
//                                mContext.startActivity(new Intent(mContext, PrivacyPolicyScreen.class));
//
//                            } else {
//
//                                Common.getInstance().mInterstitialAd.setAdListener(new AdListener() {
//                                    @Override
//                                    public void onAdClosed() {
//                                        super.onAdClosed();
//
//                                        Common.getInstance().mInterstitialAd.setAdListener(null);
//                                        Common.getInstance().mInterstitialAd = null;
//                                        Common.getInstance().ins_adRequest = null;
//                                        Common.getInstance().LoadAds();
//
//                                        mContext.startActivity(new Intent(mContext, PrivacyPolicyScreen.class));
//
//                                    }
//
//                                    @Override
//                                    public void onAdFailedToLoad(int i) {
//                                        super.onAdFailedToLoad(i);
//                                    }
//
//                                    @Override
//                                    public void onAdLoaded() {
//                                        super.onAdLoaded();
//                                    }
//                                });
//                            }
//                        } else {
//
//                            mContext.startActivity(new Intent(mContext, PrivacyPolicyScreen.class));
//
//                        }
//
//
//                    } else if (position == 3) {
//
//                        try {
//
//                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                            shareIntent.setType("text/plain");
//                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
//                            String shareMessage = "Music Player" + "\n\nLet me recommend you this application\n\n";
//                            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
//                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
//                            startActivity(Intent.createChooser(shareIntent, "choose one"));
//
//                        } catch (Exception e) {
//
//                        }
//
//
//                    } else if (position == 4) {
//
//                        try {
//                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
//                        } catch (android.content.ActivityNotFoundException anfe) {
//                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
//                        }
//
//                    }
//                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                    drawer.closeDrawer(GravityCompat.START);
//
//                }
//            });
//        }


        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.icon.setImageDrawable(screenIcons[position]);
            holder.title.setText(screenTitles[position]);

            holder.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = holder.getAdapterPosition();

                    if (clickedPosition == 0) {
                        Random rand = new Random();
                        int randomNum = rand.nextInt(5);

                        if (randomNum == 0) {
                            if (!Common.getInstance().requestNewInterstitial()) {
                                mContext.startActivity(new Intent(mContext, About.class));
                            } else {
                                Common.getInstance().mInterstitialAd.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdClosed() {
                                        super.onAdClosed();
                                        resetAdComponents();
                                        mContext.startActivity(new Intent(mContext, About.class));
                                    }

                                    @Override
                                    public void onAdFailedToLoad(int i) {
                                        super.onAdFailedToLoad(i);
                                    }

                                    @Override
                                    public void onAdLoaded() {
                                        super.onAdLoaded();
                                    }
                                });
                            }
                        } else {
                            mContext.startActivity(new Intent(mContext, About.class));
                        }
                    } else if (clickedPosition == 1) {
                        Random rand = new Random();
                        int randomNum = rand.nextInt(5);

                        if (randomNum == 0) {
                            if (!Common.getInstance().requestNewInterstitial()) {
                                mContext.startActivity(new Intent(mContext, EqualizerActivity.class));
                            } else {
                                Common.getInstance().mInterstitialAd.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdClosed() {
                                        super.onAdClosed();
                                        resetAdComponents();
                                        mContext.startActivity(new Intent(mContext, EqualizerActivity.class));
                                    }

                                    @Override
                                    public void onAdFailedToLoad(int i) {
                                        super.onAdFailedToLoad(i);
                                    }

                                    @Override
                                    public void onAdLoaded() {
                                        super.onAdLoaded();
                                    }
                                });
                            }
                        } else {
                            mContext.startActivity(new Intent(mContext, EqualizerActivity.class));
                        }
                    } else if (clickedPosition == 2) {
                        Random rand = new Random();
                        int randomNum = rand.nextInt(5);

                        if (randomNum == 0) {
                            if (!Common.getInstance().requestNewInterstitial()) {
                                mContext.startActivity(new Intent(mContext, PrivacyPolicyScreen.class));
                            } else {
                                Common.getInstance().mInterstitialAd.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdClosed() {
                                        super.onAdClosed();
                                        resetAdComponents();
                                        mContext.startActivity(new Intent(mContext, PrivacyPolicyScreen.class));
                                    }

                                    @Override
                                    public void onAdFailedToLoad(int i) {
                                        super.onAdFailedToLoad(i);
                                    }

                                    @Override
                                    public void onAdLoaded() {
                                        super.onAdLoaded();
                                    }
                                });
                            }
                        } else {
                            mContext.startActivity(new Intent(mContext, PrivacyPolicyScreen.class));
                        }
                    } else if (clickedPosition == 3) {
                        try {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                            String shareMessage = "Music Player" + "\n\nLet me recommend you this application\n\n";
                            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                            mContext.startActivity(Intent.createChooser(shareIntent, "choose one"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (clickedPosition == 4) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mContext.getPackageName())));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
                        }
                    }

                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                }
            });
        }

        private void resetAdComponents() {
            Common.getInstance().mInterstitialAd.setAdListener(null);
            Common.getInstance().mInterstitialAd = null;
            Common.getInstance().ins_adRequest = null;
            Common.getInstance().LoadAds();
        }


        @Override
        public int getItemCount() {

            return screenTitles.length;
        }
    }

}
