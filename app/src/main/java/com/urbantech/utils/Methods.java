package com.urbantech.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.onesignal.OneSignal;
import com.urbantech.padmashali.BuildConfig;
import com.urbantech.padmashali.LiveChannelActivity;
import com.urbantech.padmashali.LoginActivity;
import com.urbantech.padmashali.MainActivity;
import com.urbantech.padmashali.R;
import com.urbantech.interfaces.InterAdListener;
import com.urbantech.item.ItemNews;
import com.urbantech.item.ItemUser;
import com.yakivmospan.scytale.Crypto;
import com.yakivmospan.scytale.Options;
import com.yakivmospan.scytale.Store;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.crypto.SecretKey;
import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class Methods {

    private Context context;
    private InterAdListener interAdListener;
    private ItemNews itemNews;
    private Typeface typeface;
    private SecretKey key;

    public Methods(Context context) {
        this.context = context;
        typeface = Typeface.createFromAsset(this.context.getAssets(), "fonts/pop_med.ttf");

        Store store = new Store(context);
        if (!store.hasKey(BuildConfig.ENC_KEY)) {
            key = store.generateSymmetricKey(BuildConfig.ENC_KEY, null);
        } else {
            key = store.getSymmetricKey(BuildConfig.ENC_KEY, null);
        }
    }

    public Methods(Context context, InterAdListener interAdListener) {
        this.context = context;
        this.interAdListener = interAdListener;

        Store store = new Store(context);
        if (!store.hasKey(BuildConfig.ENC_KEY)) {
            key = store.generateSymmetricKey(BuildConfig.ENC_KEY, null);
        } else {
            key = store.getSymmetricKey(BuildConfig.ENC_KEY, null);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfoMob = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo netInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return (netInfoMob != null && netInfoMob.isConnectedOrConnecting()) || (netInfoWifi != null && netInfoWifi.isConnectedOrConnecting());
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    public void openTV() {
        Intent intent = new Intent(context, LiveChannelActivity.class);
        context.startActivity(intent);
    }

    public void openLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("from", "app");
        context.startActivity(intent);
    }

    public void setLoginButton(MenuItem buttonLogin, MenuItem buttonProfile, Context context) {
        if (Constant.isLogged) {
            buttonLogin.setTitle(context.getResources().getString(R.string.logout));
            buttonLogin.setIcon(context.getResources().getDrawable(R.drawable.logout));
            buttonProfile.setVisible(true);
        } else {
            buttonLogin.setTitle(context.getResources().getString(R.string.login));
            buttonLogin.setIcon(context.getResources().getDrawable(R.drawable.login));
            buttonProfile.setVisible(false);
        }
    }

    public void logout(Activity activity) {
        SharedPref sharedPref = new SharedPref(context);
        sharedPref.setCat("");
        changeAutoLogin(false);
        Constant.isLogged = false;
        OneSignal.sendTag("user_id", "");
        Constant.itemUser = new ItemUser("", "", "", "", "null", "", "");
        Intent intent1 = new Intent(context, LoginActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.putExtra("from", "");
        context.startActivity(intent1);
        activity.finish();
    }

    public void changeAutoLogin(Boolean isAutoLogin) {
        SharedPref sharePref = new SharedPref(context);
        sharePref.setIsAutoLogin(isAutoLogin);
    }

    public boolean clickLogin() {
        if (Constant.isLogged) {
            logout((Activity) context);
            Toast.makeText(context, context.getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
        } else {
            openLogin();
        }
        return false;
    }

    public void setStatusColor(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public boolean isPackageInstalled(String packagename) {
        try {
            return context.getPackageManager().getApplicationInfo(packagename, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    //check dark mode or not
    public boolean isDarkMode() {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                return false;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                return true;
            default:
                return false;
        }
    }

    public String getDarkMode() {
        SharedPref sharedPref = new SharedPref(context);
        return sharedPref.getDarkMode();
    }

    public void startVideoPlay(String vid) {
        Intent intent;
        if (isPackageInstalled("com.google.android.youtube")) {
            intent = YouTubeStandalonePlayer.createVideoIntent((Activity) context, BuildConfig.API_KEY, vid, 0, true, false);
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + vid));
        }
        context.startActivity(intent);
    }

    public void setFavImage(Boolean isFav, ImageView imageView) {
        if (isFav) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.fav_hover));
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.fav));
        }
    }

    public void setLikeImage(Boolean isLiked, ImageView imageView) {
        if (isLiked) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fav_hover));
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fav));
        }
    }





    public void shareNews(ItemNews News) {
        itemNews = News;
        saveImage(itemNews.getImage(), FilenameUtils.getName(itemNews.getImage()));
    }

    private void saveImage(String img_url, String name) {

        String regex = "([0-9_-]+(\\.(?i)(jpe?g|png|gif|bmp))$)";

        if(name.matches(regex)){
            new LoadShare().execute(img_url, name);
        }else{
            showToast("You Cannot Share This News");
        }

    }




    public class LoadShare extends AsyncTask<String, String, String> {
        File file;

        @Override
        protected String doInBackground(String... strings) {

            String filepath = "";

            String name = strings[1];
            try {
                File SDCardRoot = context.getExternalCacheDir().getAbsoluteFile();
                file = new File(SDCardRoot, name);
                if (!file.exists()) {
                    URL url = new URL(strings[0]);

                    InputStream inputStream;
                    int totalSize ;

                    if (strings[0].contains("https://")) {
                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Accept", "*/*");
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                        inputStream = urlConnection.getInputStream();
                        totalSize = urlConnection.getContentLength();

                    } else {
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Accept", "*/*");
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                        inputStream = urlConnection.getInputStream();
                        totalSize = urlConnection.getContentLength();
                    }

                    if (file.createNewFile()) {
                        file.createNewFile();
                    }

                    FileOutputStream fileOutput = new FileOutputStream(file);

                    int downloadedSize = 0;
                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;
                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fileOutput.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                        Log.i("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize);
                    }
                    fileOutput.close();
                    if (downloadedSize == totalSize) {
                        filepath = file.getPath();
                    }
                } else {
                    filepath = file.getPath();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                filepath = null;
                e.printStackTrace();

            }
            Log.e("filepath:", " " + filepath);
            return filepath;
        }

        @Override
        protected void onPostExecute(String s) {

            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName().concat(".fileprovider"), file);

//            Spanned spanned = Html.fromHtml(itemNews.getDesc());
//            char[] chars = new char[spanned.length()];
//            TextUtils.getChars(spanned, 0, spanned.length(), chars, 0);
//            String plainText = new String(chars);

            Intent intent = new Intent(Intent.ACTION_SEND);
            
            intent.setDataAndType(contentUri, context.getContentResolver().getType(contentUri));

            if (!itemNews.getVideoUrl().trim().isEmpty()) {
                intent.putExtra(Intent.EXTRA_TEXT, itemNews.getHeading() + "\n\n" + itemNews.getVideoUrl() + "\n\n" + itemNews.getShareLink() + "\n\n" + context.getString(R.string.share_message) + "\n" + context.getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + context.getPackageName());
            } else {
                intent.putExtra(Intent.EXTRA_TEXT, itemNews.getHeading() + "\n\n" + itemNews.getShareLink() + "\n\n" + context.getString(R.string.share_message) + "\n" + context.getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + context.getPackageName());
            }
            intent.putExtra(Intent.EXTRA_STREAM, contentUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.share_news)));
            super.onPostExecute(s);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void forceRTLIfSupported(Window window) {
        if (context.getResources().getString(R.string.isRTL).equals("true")) {
            window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public GradientDrawable getRoundDrawable(int color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.mutate();
        gd.setCornerRadius(10);
        return gd;
    }

    public GradientDrawable getRoundDrawableRadis(int color, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.mutate();
        gd.setCornerRadius(radius);
        return gd;
    }

    public String encrypt(String value) {
        try {
            Crypto crypto = new Crypto(Options.TRANSFORMATION_SYMMETRIC);
            return crypto.encrypt(value, key);
        } catch (Exception e) {
            Crypto crypto = new Crypto(Options.TRANSFORMATION_SYMMETRIC);
            return crypto.encrypt("null", key);
        }
    }

    public String decrypt(String value) {
        try {
            Crypto crypto = new Crypto(Options.TRANSFORMATION_SYMMETRIC);
            return crypto.decrypt(value, key);
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

//    private void showPersonalizedAds(LinearLayout linearLayout) {
//        AdView adView = new AdView(context);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        adView.setAdUnitId(Constant.bannerAdID);
//        adView.setAdSize(AdSize.BANNER);
//        linearLayout.addView(adView);
//        adView.loadAd(adRequest);
//    }

//    private void showNonPersonalizedAds(LinearLayout linearLayout) {
//        Bundle extras = new Bundle();
//        extras.putString("npa", "1");
//        AdView adView = new AdView(context);
//        AdRequest adRequest = new AdRequest.Builder()
//                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
//                .build();
//        adView.setAdUnitId(Constant.bannerAdID);
//        adView.setAdSize(AdSize.BANNER);
//        linearLayout.addView(adView);
//        adView.loadAd(adRequest);
//    }

//    public void showBannerAd(LinearLayout linearLayout) {
//        if (isNetworkAvailable() && Constant.isBannerAd) {
//            if (ConsentInformation.getInstance(context).getConsentStatus() == ConsentStatus.NON_PERSONALIZED) {
//                showNonPersonalizedAds(linearLayout);
//            } else {
//                showPersonalizedAds(linearLayout);
//            }
//        }
//    }

//    public void showInterAd(final int pos, final String type) {
//        if (Constant.isInterstitialAd) {
//            Constant.adCount = Constant.adCount + 1;
//            if (Constant.adCount % Constant.interstitialAdShow == 0) {
//                final AdManagerInter adManagerAdmobInter = new AdManagerInter(context);
//                if (adManagerAdmobInter.getAd() != null) {
//                    adManagerAdmobInter.getAd().setFullScreenContentCallback(new FullScreenContentCallback() {
//                        @Override
//                        public void onAdDismissedFullScreenContent() {
//                            AdManagerInter.setAd(null);
//                            adManagerAdmobInter.createAd();
//                            interAdListener.onClick(pos, type);
//                            super.onAdDismissedFullScreenContent();
//                        }
//
//                        @Override
//                        public void onAdFailedToShowFullScreenContent(@NonNull @NotNull com.google.android.gms.ads.AdError adError) {
//                            AdManagerInter.setAd(null);
//                            adManagerAdmobInter.createAd();
//                            interAdListener.onClick(pos, type);
//                            super.onAdFailedToShowFullScreenContent(adError);
//                        }
//                    });
//                    adManagerAdmobInter.getAd().show((Activity) context);
//                } else {
//                    AdManagerInter.setAd(null);
//                    adManagerAdmobInter.createAd();
//                    interAdListener.onClick(pos, type);
//                }
//            } else {
//                interAdListener.onClick(pos, type);
//            }
//        } else {
//            interAdListener.onClick(pos, type);
//        }
//    }

    public String getTempUploadPath(Uri uri) {
        File root = context.getExternalCacheDir().getAbsoluteFile();
        try {
            String filePath = root.getPath() + File.separator + System.currentTimeMillis() + ".jpg";

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bm = BitmapFactory.decodeStream(inputStream);

            if (saveBitMap(root, bm, filePath)) {
                return filePath;
            } else {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private boolean saveBitMap(File root, Bitmap Final_bitmap, String filePath) {
        if (!root.exists()) {
            boolean isDirectoryCreated = root.mkdirs();
            if (!isDirectoryCreated)
                Log.i("SANJAY ", "Can't create directory to save the image");
            return false;
        }
        String filename = filePath;
        File pictureFile = new File(filename);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            Final_bitmap.compress(Bitmap.CompressFormat.PNG, 18, oStream);
            oStream.flush();
            oStream.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Typeface getFontMedium() {
        return typeface;
    }

    public String getImageThumbSize(String imagePath, String type) {
        if (type.equals(context.getString(R.string.categories))) {
            imagePath = imagePath.replace("&size=300x300", "&size=400x300");
        } else if (type.equals("homecat")) {
            imagePath = imagePath.replace("&size=300x300", "&size=250x150");
        } else if (type.equals("banner")) {
            imagePath = imagePath.replace("&size=300x300", "&size=550x350");
        } else if (type.equals(context.getString(R.string.home))) {
            imagePath = imagePath.replace("&size=300x300", "&size=300x250");
        } else if (type.equals("header")) {
            imagePath = imagePath.replace("&size=300x300", "&size=500x250");
        } else if (type.equals("notheader")) {
            imagePath = imagePath.replace("&size=300x300", "&size=400x300");
        }

        return imagePath;
    }

    public void getInvalidUserDialog(String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        alertDialog.setTitle(context.getString(R.string.invalid_user));
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout((Activity) context);
            }
        });
        alertDialog.show();
    }

    public Boolean checkPer() {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            if ((ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ((Activity) context).requestPermissions(new String[]{READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE}, 22);
                return false;
            } else {
                return true;
            }
        } else {
            if ((ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ((Activity) context).requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 22);
                    return false;
                }
            }
            return true;
        }
    }

    public void showUpdateAlert(String message, boolean isSplash) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        alertDialog.setTitle(context.getString(R.string.update));
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(context.getString(R.string.update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = Constant.appUpdateURL;
                if (url.equals("")) {
                    url = "http://play.google.com/store/apps/details?id=" + context.getPackageName();
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);

                ((Activity) context).finish();
            }
        });
        if (Constant.appUpdateCancel) {
            alertDialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (isSplash) {
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                }
            });
        } else {
            alertDialog.setNegativeButton(context.getString(R.string.exit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((Activity) context).finish();
                }
            });
        }
        alertDialog.show();
    }

    public void getVerifyDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                finish();
            }
        });
        alertDialog.show();
    }
//    String method, int page, String title, String newsID, String searchText, String type, String catID, String date, String email, String password, String name, String phone, String userID, String reportMessage_url, File file, ArrayList<File> fileArray
    public RequestBody getAPIRequest(String method, int page, String title, String newsID, String searchText, String type, String catID, String date, String email, String password, String name, String phone, String userID, String reportMessage_url,String surname,String fathername,String gender,String housenumber,String wardnumber,String city,String mandal,String district,String qualification,String profession,String religion,String caste,String marital_status,String spouse_name,String spouse_date,String spouse_gender,String assembly,String parliament,String state,String country,String amount,File file, ArrayList<File> fileArray) {
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", method);
        jsObj.addProperty("package_name", context.getPackageName());

        switch (method) {
            case Constant.METHOD_HOME:

                jsObj.addProperty("cat_id", catID);
                jsObj.addProperty("user_id", userID);

                break;
            case Constant.METHOD_APP_DETAILS:

                jsObj.addProperty("user_id", userID);

                break;
            case Constant.METHOD_LOGIN:

                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("auth_id", userID);
                jsObj.addProperty("type", type);

                break;
            case Constant.METHOD_REGISTER:

                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);
                jsObj.addProperty("type", type);
                jsObj.addProperty("auth_id", userID);

                break;
            case Constant.METHOD_FORGOT_PASSWORD:

                jsObj.addProperty("email", email);

                break;
            case Constant.METHOD_PROFILE:

                jsObj.addProperty("user_id", userID);

                break;
            case Constant.METHOD_PROFILE_EDIT:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);
                jsObj.addProperty("caste",catID);

                break;
            case Constant.METHOD_LATEST:

                jsObj.addProperty("page", page);
                jsObj.addProperty("user_id", userID);

                break;
            case Constant.METHOD_VIDEO_NEWS:

                jsObj.addProperty("page", page);
                jsObj.addProperty("cat_id", catID);
                jsObj.addProperty("user_id", userID);

                break;
            case Constant.METHOD_LATEST_USER:

                jsObj.addProperty("page", page);
                jsObj.addProperty("cat_id", catID);
                jsObj.addProperty("user_id", userID);

                break;
            case Constant.METHOD_NEWS_BY_CAT:

                jsObj.addProperty("page", page);
                jsObj.addProperty("cat_id", catID);
                jsObj.addProperty("user_id", userID);

                break;
            case Constant.METHOD_SEARCH:

                jsObj.addProperty("page", page);
                jsObj.addProperty("search_text", searchText);

                break;
            case Constant.METHOD_COMMENTS:

                jsObj.addProperty("page", page);
                jsObj.addProperty("news_id", newsID);

                break;
            case Constant.METHOD_POST_COMMENTS:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("news_id", newsID);
                jsObj.addProperty("comment_text", searchText);

                break;
            case Constant.METHOD_SINGLE_NEWS:

                jsObj.addProperty("news_id", newsID);

                break;
            case Constant.METHOD_SAVE_CATEGORY:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("cat_id", catID);

                break;
            case Constant.METHOD_REPORT:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("news_id", newsID);
                jsObj.addProperty("report", reportMessage_url);

                break;
            case Constant.METHOD_DELETE_COMMENTS:

                jsObj.addProperty("comment_id", catID);
                jsObj.addProperty("news_id", newsID);

                break;
            case Constant.METHOD_APPLY_REPORTER:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);

                break;
            case Constant.METHOD_FAV:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("news_id", newsID);

                break;
            case Constant.METHOD_FAV_LIST:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);

                break;
            case Constant.METHOD_RECENT_NEWS:

                jsObj.addProperty("news_ids", newsID);
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);

                break;
            case Constant.METHOD_RELATED_NEWS:

                jsObj.addProperty("news_ids", newsID);
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("cat_id", catID);
                jsObj.addProperty("page", page);

                break;
            case Constant.METHOD_DELETE_NEWS:

                jsObj.addProperty("news_id", newsID);
                jsObj.addProperty("user_id", userID);

                break;
            case Constant.METHOD_USER_UPLOAD_LIST:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);

                break;
            case Constant.METHOD_UPLOAD_NEWS:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("news_type", type);
                jsObj.addProperty("cat_id", catID);
                jsObj.addProperty("news_heading", title);
                jsObj.addProperty("news_description", searchText);
                jsObj.addProperty("news_date", date);
                jsObj.addProperty("video_url", reportMessage_url);

                break;
            case Constant.METHOD_EDIT_UPLOAD_NEWS:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("news_id", newsID);
                jsObj.addProperty("news_type", type);
                jsObj.addProperty("cat_id", catID);
                jsObj.addProperty("video_url", reportMessage_url);
                jsObj.addProperty("news_heading", title);
                jsObj.addProperty("news_description", searchText);
                jsObj.addProperty("news_date", date);

                break;
            case Constant.METHOD_REMOVE_GALLERY_IMAGE:

                jsObj.addProperty("image_id", catID);
                jsObj.addProperty("news_id", newsID);

                break;
                //adduser
            case Constant.METHOD_ADDUSER:

                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);
                jsObj.addProperty("type", type);
                jsObj.addProperty("auth_id", userID);

                break;

                //survey

            case Constant.METHOD_SURVEY:

                jsObj.addProperty("user_id",userID);
                jsObj.addProperty("survey_name",name);
                jsObj.addProperty("survey_surname",surname);
                jsObj.addProperty("survey_fathername",fathername);
                jsObj.addProperty("survey_dateofbirth",date);
                jsObj.addProperty("survey_gender",gender);
                jsObj.addProperty("survey_housenumber",housenumber);
                jsObj.addProperty("survey_wardnumber",wardnumber);
                jsObj.addProperty("survey_city",city);
                jsObj.addProperty("survey_mandal",mandal);
                jsObj.addProperty("survey_district",district);
                jsObj.addProperty("survey_qualification",qualification);
                jsObj.addProperty("survey_occupation",type);

                break;

                //survey details
            case Constant.METHOD_SURVEY_DETAILS:

                jsObj.addProperty("user_id", userID);

                break;
            case Constant.METHOD_UPCOMING_SERVICES:
//                jsObj.addProperty("sid",newsID);
//                jsObj.addProperty("user_id",userID);
                break;
            case Constant.METHOD_FEEDBACK:
                jsObj.addProperty("user_id",userID);
                jsObj.addProperty("feedback",title);
                break;
            case Constant.METHOD_LIKE:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("news_id", newsID);

                break;
            case Constant.METHOD_MEMBERSHIP:

                jsObj.addProperty("user_id",userID);
                jsObj.addProperty("member_id",newsID);
                jsObj.addProperty("member_religion",religion);
                jsObj.addProperty("member_caste",caste);
                jsObj.addProperty("member_name",name);
                jsObj.addProperty("member_surname",surname);
                jsObj.addProperty("member_dob",date);
                jsObj.addProperty("member_gender",gender);
                jsObj.addProperty("member_fathername",fathername);
                jsObj.addProperty("member_marital_status",marital_status);
                jsObj.addProperty("spouse_name",spouse_name);
                jsObj.addProperty("spouse_dob",spouse_date);
                jsObj.addProperty("spouse_gender",spouse_gender);
                jsObj.addProperty("member_qualification",qualification);
                jsObj.addProperty("member_profession",profession);
                jsObj.addProperty("member_hno",housenumber);
                jsObj.addProperty("member_wardno",wardnumber);
                jsObj.addProperty("member_city",city);
                jsObj.addProperty("member_mandal",mandal);
                jsObj.addProperty("member_assembly",assembly);
                jsObj.addProperty("member_parliament",parliament);
                jsObj.addProperty("member_district",district);
                jsObj.addProperty("member_state",state);
                jsObj.addProperty("member_country",country);
                jsObj.addProperty("membership_type",type);
                jsObj.addProperty("member_amount",amount);
                jsObj.addProperty("member_mobile",phone);

                break;

            case Constant.METHOD_MEMBERSHIP_DETAILS:

                jsObj.addProperty("user_id", userID);

                break;
            case Constant.METHOD_SINGLE_MEMBERSHIP:

                jsObj.addProperty("member_id", newsID);

                break;

            case Constant.METHOD_OTP:

                jsObj.addProperty("member_mobile",phone);
                jsObj.addProperty("otp",catID);
                jsObj.addProperty("type",type);

                break;

        }

//        Log.e("aaa - url", API.toBase64(jsObj.toString()));

        if (method.equals(Constant
                .METHOD_UPLOAD_NEWS) || method.equals(Constant.METHOD_EDIT_UPLOAD_NEWS) || method.equals(Constant.METHOD_REGISTER) || method.equals(Constant.METHOD_PROFILE_EDIT) || method.equals(Constant.METHOD_MEMBERSHIP)) {
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");

            if (method.equals(Constant.METHOD_PROFILE_EDIT) || method.equals(Constant.METHOD_REGISTER)) {
                if (file != null) {
                    return new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("profile_img", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file))
                            .addFormDataPart("data", API.toBase64(jsObj.toString()))
                            .build();
                } else {
                    return new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("data", API.toBase64(jsObj.toString()))
                            .build();
                }
            }
            else if(method.equals(Constant.METHOD_MEMBERSHIP)){
                if (file != null) {
                    return new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("member_profile", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file))
                            .addFormDataPart("data", API.toBase64(jsObj.toString()))
                            .build();
                } else {
                    return new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("data", API.toBase64(jsObj.toString()))
                            .build();
                }
            }
            else {
                MultipartBody.Builder buildernew = new MultipartBody.Builder().setType(MultipartBody.FORM);

                if (file != null) {
                    buildernew.addFormDataPart("news_featured_image", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
                }

                for (int i = 0; i < fileArray.size(); i++) {
                    buildernew.addFormDataPart("news_gallery_image[]", fileArray.get(i).getName(), RequestBody.create(MEDIA_TYPE_PNG, fileArray.get(i)));
                }

                return buildernew
                        .addFormDataPart("data", API.toBase64(jsObj.toString()))
                        .build();
            }



        } else {
            return new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("data", API.toBase64(jsObj.toString()))
                    .build();
        }

    }

}
