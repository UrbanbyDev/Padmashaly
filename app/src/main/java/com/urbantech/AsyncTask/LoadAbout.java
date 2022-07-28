package com.urbantech.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.urbantech.interfaces.AboutListener;
import com.urbantech.item.ItemAbout;
import com.urbantech.utils.Constant;
import com.urbantech.utils.JsonUtils;
import com.urbantech.utils.SharedPref;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadAbout extends AsyncTask<String, String, String> {

    private AboutListener aboutListener;
    private RequestBody requestBody;
    private String message = "", verifyStatus = "0";
    private Context context;

    public LoadAbout(Context context, AboutListener aboutListener, RequestBody requestBody) {
        this.aboutListener = aboutListener;
        this.requestBody = requestBody;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        aboutListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            if (mainJson.has(Constant.TAG_ROOT)) {
                JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);

                    if (!c.has(Constant.TAG_SUCCESS)) {
                        String appname = c.getString("app_name");
                        String applogo = c.getString("app_logo");
                        String desc = c.getString("app_description");
                        String appversion = c.getString("app_version");
                        String appauthor = c.getString("app_author");
                        String appcontact = c.getString("app_contact");
                        String email = c.getString("app_email");
                        String website = c.getString("app_website");
                        String privacy = c.getString("app_privacy_policy");
                        String developedby = c.getString("app_developed_by");

                        Constant.termsOfUse = c.getString("app_terms_conditions");
                        Constant.publisherAdID = c.getString("publisher_id");

                        Constant.isBannerAd = Boolean.parseBoolean(c.getString("banner_ad"));
                        Constant.isInterstitialAd = Boolean.parseBoolean(c.getString("interstital_ad"));
                        Constant.isNativeAd = Boolean.parseBoolean(c.getString("native_ad"));

                        Constant.bannerAdType = c.getString("banner_ad_type");
                        Constant.interstitialAdType = c.getString("interstital_ad_type");
                        Constant.natveAdType = c.getString("native_ad_type");

                        Constant.bannerAdID = c.getString("banner_ad_id");
                        Constant.interstitialAdID = c.getString("interstital_ad_id");
                        Constant.nativeAdID = c.getString("native_ad_id");

                        Constant.interstitialAdShow = Integer.parseInt(c.getString("interstital_ad_click"));
                        Constant.nativeAdShow = Integer.parseInt(c.getString("native_position"));

                        Constant.channelStatus = Boolean.parseBoolean(c.getString("channel_status"));
                        String user_category = c.getString("user_category");
                        Constant.packageName = c.getString("package_name");

                        Constant.showUpdateDialog = c.getBoolean("app_update_status");
                        Constant.appVersion = c.getString("app_new_version");
                        Constant.appUpdateMsg = c.getString("app_update_desc");
                        Constant.appUpdateURL = c.getString("app_redirect_url");
                        Constant.appUpdateCancel = c.getBoolean("cancel_update_status");

                        if (Constant.isLogged) {
                            SharedPref sharedPref = new SharedPref(context);
                            if(user_category.equals("null")) {
                                sharedPref.setCat("");
                            } else {
                                sharedPref.setCat(user_category);
                            }
                        }

                        Constant.itemAbout = new ItemAbout(appname, applogo, desc, appversion, appauthor, appcontact, email, website, privacy, developedby);

                    } else {
                        verifyStatus = c.getString(Constant.TAG_SUCCESS);
                        message = c.getString(Constant.TAG_MSG);
                    }
                }
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        aboutListener.onEnd(s, verifyStatus, message);
        super.onPostExecute(s);
    }
}