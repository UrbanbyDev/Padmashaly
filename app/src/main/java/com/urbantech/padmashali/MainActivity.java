package com.urbantech.padmashali;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;
import com.urbantech.AsyncTask.LoadAbout;
import com.urbantech.AsyncTask.LoadProfile;
import com.urbantech.fragments.FragmentHome;
import com.urbantech.fragments.FragmentLatest;
import com.urbantech.fragments.FragmentSettings;
import com.urbantech.fragments.FragmentVideo;
import com.urbantech.interfaces.AboutListener;
import com.urbantech.interfaces.AdConsentListener;
import com.urbantech.interfaces.SuccessListener;
import com.urbantech.utils.AdConsent;
import com.urbantech.utils.Constant;
import com.urbantech.utils.DBHelper;
import com.urbantech.utils.Methods;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity   {

    Toolbar toolbar;
    DBHelper dbHelper;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
//    NavigationView navigationView;
    FragmentManager fm;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    Methods methods;
//    AdConsent adConsent;
    MenuItem menuItemLogin, menuItemProfile, menuItemUpload,menuItemadduser;
//    LinearLayout ll_adView;
    RelativeLayout lay_toolbar_profile;
    SpaceNavigationView spaceNavigationView;
    private FragmentTransaction fragmentTransaction;
    private String applogo;
    RoundedImageView toolbar_icon;
    TextView toolbar_profile;
    ImageView toolbar_share;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OneSignal.sendTag("user_id", Constant.itemUser.getId());

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("aaa", "printHashKey()", e);
//        } catch (Exception e) {
//            Log.e("aaa", "printHashKey()", e);
//        }
//        requestPermission();
        checkPermission();


//        ll_adView = findViewById(R.id.ll_adView);
        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        dbHelper = new DBHelper(this);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        lay_toolbar_profile=findViewById(R.id.lay_toolbar_profile);

        toolbar_icon=findViewById(R.id.toolbar_icon);
        toolbar_profile=findViewById(R.id.toolbar_profile);
        toolbar_share=findViewById(R.id.shareapp_icon);

        fm = getSupportFragmentManager();

        toolbar_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ishare = new Intent(Intent.ACTION_SEND);
                ishare.setType("text/plain");
                ishare.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName());
                startActivity(ishare);
            }
        });

        if (Constant.isLogged){
//            if (!Constant.itemUser.getImage().equals("")) {
//                Picasso.get().load(Constant.itemUser.getImage()).placeholder(R.drawable.placeholder_prof).into(toolbar_icon);
//            }
//            toolbar_profile.setText(Constant.itemUser.getName());
//            setData();
            loadUserProfile();
        }else{
            Picasso.get().load(R.drawable.logo).into(toolbar_icon);
            toolbar_profile.setText(R.string.app_name);
        }


        lay_toolbar_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
//                    startActivity(intent);
                if (Constant.isLogged != false) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


//        drawer = findViewById(R.id.drawer_layout);
//        toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//
//        menuItemLogin = navigationView.getMenu().findItem(R.id.nav_login);
//        menuItemProfile = navigationView.getMenu().findItem(R.id.nav_profile);
//        menuItemUpload = navigationView.getMenu().findItem(R.id.nav_upload);
//        menuItemadduser=navigationView.getMenu().findItem(R.id.nav_add_user);
//        methods.setLoginButton(menuItemLogin, menuItemProfile, MainActivity.this);
        

        FragmentHome homeFragment = new FragmentHome();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_nav, homeFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

//        navigationView.setCheckedItem(R.id.nav_home);

//        adConsent = new AdConsent(this, new AdConsentListener() {
//            @Override
//            public void onConsentUpdate() {
//                methods.showBannerAd(ll_adView);
//            }
//        });

        if (methods.isNetworkAvailable()) {
            loadAboutData();
        } else {
//            adConsent.checkForConsent();
            dbHelper.getAbout();
            methods.showToast(getString(R.string.err_internet_not_conn));
        }


        spaceNavigationView = findViewById(R.id.space);
        spaceNavigationView.showIconOnly();
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem(getString(R.string.home), R.drawable.home));
        spaceNavigationView.addSpaceItem(new SpaceItem(getString(R.string.latest_news), R.drawable.latest));
        spaceNavigationView.addSpaceItem(new SpaceItem(getString(R.string.video_news), R.drawable.video));
        spaceNavigationView.addSpaceItem(new SpaceItem(getString(R.string.setting), R.drawable.settings));

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {

                if (Constant.isLogged != false) {
                    Intent intent = new Intent(MainActivity.this, UploadsNewsActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex) {
                    case 0:
                        FragmentHome homeFragment = new FragmentHome();
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_nav, homeFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    case 1:

                        FragmentLatest fragmentLatest = new FragmentLatest();
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_nav, fragmentLatest);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    case 2:

                        FragmentVideo fragmentVideo = new FragmentVideo();
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_nav, fragmentVideo);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    case 3:
                        FragmentSettings fragmentSettings = new FragmentSettings();
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_nav, fragmentSettings);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

            }
        });

    }
    private void loadUserProfile() {
        LoadProfile loadProfile = new LoadProfile(new SuccessListener() {
            @Override
            public void onStart() {


            }

            @Override
            public void onEnd(String success, String registerSuccess, String message) {
//                progressDialog.dismiss();
                if (success.equals("1")) {
                    setData();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }

            }
        }, methods.getAPIRequest(Constant.METHOD_PROFILE, 0, "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "",  "","","","","","","","","",null, null));
        loadProfile.execute();
    }


    private void setData() {
        if (!Constant.itemUser.getImage().equals("")) {
                Picasso.get().load(Constant.itemUser.getImage()).placeholder(R.drawable.placeholder_prof).into(toolbar_icon);
        }
            toolbar_profile.setText(Constant.itemUser.getName());
    }

    private void checkPermission() {

        if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager();
            Toast.makeText(MainActivity.this,"permissions granted ",Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
//        if (fm.getBackStackEntryCount() != 0) {
//            String title = fm.getFragments().get(fm.getBackStackEntryCount() - 1).getTag();
//            if (title.equals(getString(R.string.home))) {
////                navigationView.setCheckedItem(R.id.nav_home);
//            }
//            getSupportActionBar().setTitle(title);
            super.onBackPressed();
//        } else {
            exitDialog();
//        }

    }


//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//
//        switch (item.getItemId()) {
//            case R.id.nav_home:
//                FragmentHome f1 = new FragmentHome();
//                loadFrag(f1, getString(R.string.home), fm);
//                break;
//            case R.id.nav_latest:
//                FragmentLatest flatest = new FragmentLatest();
//                loadFrag(flatest, getString(R.string.latest), fm);
//                break;
//            case R.id.nav_login:
//                methods.clickLogin();
//                break;
//            case R.id.nav_video:
//                FragmentVideo fvideo = new FragmentVideo();
//                loadFrag(fvideo, getString(R.string.video_news), fm);
//                toolbar.setTitle(getString(R.string.video_news));
//                break;
//            case R.id.nav_cat:
//                FragmentCat fcat = new FragmentCat();
//                loadFrag(fcat, getString(R.string.categories), fm);
//                toolbar.setTitle(getString(R.string.categories));
//                break;
//            case R.id.nav_fav:
//                FragmentFav ffav = new FragmentFav();
//                loadFrag(ffav, getString(R.string.favourite), fm);
//                toolbar.setTitle(getString(R.string.favourite));
//                break;
//            case R.id.nav_upload:
//                Intent intent_upload = new Intent(MainActivity.this, UploadsNewsActivity.class);
//                startActivity(intent_upload);
//                break;
//            case R.id.nav_add_user:
//                Intent intent_add=new Intent(MainActivity.this,AddUserActivity.class);
//                startActivity(intent_add);
//                break;
//            case R.id.nav_profile:
//                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.nav_shareapp:
//                Intent ishare = new Intent(Intent.ACTION_SEND);
//                ishare.setType("text/plain");
//                ishare.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + getPackageName());
//                startActivity(ishare);
//                break;
//            case R.id.nav_settings:
//                Intent intent_set = new Intent(MainActivity.this, SettingActivity.class);
//                startActivity(intent_set);
//                break;
//        }
//
//        navigationView.setCheckedItem(item.getItemId());
//        drawer.closeDrawer(GravityCompat.START);
//
//        return true;
//    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        FragmentTransaction ft = fm.beginTransaction();
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        if (!name.equals(getString(R.string.home))) {
            ft.hide(fm.getFragments().get(fm.getFragments().size() - 1));
            ft.add(R.id.frame_nav, f1, name);
            ft.addToBackStack(name);
        } else {
            ft.replace(R.id.frame_nav, f1, name);
        }
        ft.commit();

        getSupportActionBar().setTitle(name);
    }

    public void loadAboutData() {
        LoadAbout loadAbout = new LoadAbout(MainActivity.this, new AboutListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message) {
                if (!verifyStatus.equals("-1")) {
                    String version = "";
                    try {
                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        version = String.valueOf(pInfo.versionCode);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (Constant.showUpdateDialog && !Constant.appVersion.equals(version)) {
                        methods.showUpdateAlert(Constant.appUpdateMsg, false);
                    } else {
//                        adConsent.checkForConsent();
                        dbHelper.addtoAbout();
                    }
                } else {
                    methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                }
            }
        }, methods.getAPIRequest(Constant.METHOD_APP_DETAILS, 0, "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "",  "","","","","","","","","",null, null));
        loadAbout.execute();
    }

    private void exitDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        alert.setTitle(getString(R.string.exit));
        alert.setMessage(getString(R.string.sure_exit));
        alert.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FragmentHome homeFragment = new FragmentHome();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_nav, homeFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        alert.show();
    }

    @Override
    protected void onResume() {
        if (menuItemLogin != null) {
            methods.setLoginButton(menuItemLogin, menuItemProfile, MainActivity.this);
            if (Constant.itemUser.getIsReporter()) {
                menuItemUpload.setVisible(true);
                menuItemadduser.setVisible(true);
            } else {
                menuItemUpload.setVisible(false);
                menuItemadduser.setVisible(false);
            }
        }

        if (Constant.isLogged) {
            loadUserProfile();
        }
        super.onResume();
    }


}