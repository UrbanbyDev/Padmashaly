package com.urbantech.AsyncTask;

import android.os.AsyncTask;

import com.urbantech.interfaces.SingleNewsListener;
import com.urbantech.item.ItemComment;
import com.urbantech.item.ItemGallery;
import com.urbantech.item.ItemNews;
import com.urbantech.utils.Constant;
import com.urbantech.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadSingleNews extends AsyncTask<String, String, String> {

    private SingleNewsListener singleNewsListener;
    private RequestBody requestBody;
    private ItemNews itemNews;
    private ArrayList<ItemComment> arrayListComments = new ArrayList<>();
    private ArrayList<ItemNews> arrayListRelated = new ArrayList<>();

    public LoadSingleNews(SingleNewsListener singleNewsListener, RequestBody requestBody) {
        this.singleNewsListener = singleNewsListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        singleNewsListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

            ArrayList<ItemGallery> array = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                JSONArray gallery = obj.getJSONArray(Constant.TAG_GALLERY);
                if (gallery.length() > 0) {
                    try {
                        for (int j = 0; j < gallery.length(); j++) {
                            JSONObject obj_gallery = gallery.getJSONObject(j);
                            String image_id = obj_gallery.getString("image_id");
                            String image = obj_gallery.getString("image_name");
                            array.add(new ItemGallery(image_id,image));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                JSONArray comment = obj.getJSONArray(Constant.TAG_COMMENT);
                if (comment.length() > 0) {
                    try {
                        for (int j = 0; j < comment.length(); j++) {
                            JSONObject obj_com = comment.getJSONObject(j);
                            String nid = obj_com.getString(Constant.TAG_COMMENT_ID);
                            String uid = obj_com.getString(Constant.TAG_USER_ID);
                            String user_name = obj_com.getString(Constant.TAG_USER_NAME);
                            String user_email = obj_com.getString(Constant.TAG_USER_EMAIL);
                            String user_profile = obj_com.getString(Constant.TAG_USER_DP);
                            String comment_text = obj_com.getString(Constant.TAG_COMMENT_TEXT);
                            String comment_date = obj_com.getString(Constant.TAG_COMMENT_ON);

                            ItemComment itemComment = new ItemComment(nid, uid, user_name, user_email, comment_text,user_profile, comment_date);
                            arrayListComments.add(itemComment);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                
                if(obj.has("relative_news")) {
                    JSONArray jArrRelated = obj.getJSONArray("relative_news");
                    try {
                        for (int j = 0; j < jArrRelated.length(); j++) {
                            JSONObject objRelative = jArrRelated.getJSONObject(j);
                            String id = objRelative.getString(Constant.TAG_ID);
                            String cat_id = objRelative.getString(Constant.TAG_CAT_ID);
                            String cat_name = objRelative.getString(Constant.TAG_CAT_NAME);
                            String type = objRelative.getString(Constant.TAG_NEWS_TYPE);
                            String heading = objRelative.getString(Constant.TAG_NEWS_HEADING);
                            String desc = objRelative.getString(Constant.TAG_NEWS_DESC);
                            String video_id = objRelative.getString(Constant.TAG_NEWS_VIDEO_ID);
                            String video_url = objRelative.getString(Constant.TAG_NEWS_VIDEO_URL);
                            String date = objRelative.getString(Constant.TAG_NEWS_DATE);
                            String image = objRelative.getString(Constant.TAG_NEWS_IMAGE);
                            String image_thumb = objRelative.getString(Constant.TAG_NEWS_IMAGE_THUMB);
                            String tot_views = objRelative.getString(Constant.TAG_TOTAL_VIEWS);
                            Boolean fav = objRelative.getBoolean(Constant.TAG_FAV);
                            Boolean like = objRelative.getBoolean(Constant.TAG_LIKE);
                            String share_link = objRelative.getString(Constant.TAG_SHARE_LINK);

                            ItemNews itemNews = new ItemNews(id, cat_id, cat_name, type, heading, desc, video_id, video_url, date, image, image_thumb, tot_views, share_link, fav,like, null);
                            arrayListRelated.add(itemNews);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            JSONObject obj = jsonArray.getJSONObject(0);
            String id = obj.getString(Constant.TAG_ID);
            String cat_id = obj.getString(Constant.TAG_CAT_ID);
            String cat_name = obj.getString(Constant.TAG_CAT_NAME);
            String type = obj.getString(Constant.TAG_NEWS_TYPE);
            String heading = obj.getString(Constant.TAG_NEWS_HEADING);
            String desc = obj.getString(Constant.TAG_NEWS_DESC);
            String video_id = obj.getString(Constant.TAG_NEWS_VIDEO_ID);
            String video_url = obj.getString(Constant.TAG_NEWS_VIDEO_URL);
            String date = obj.getString(Constant.TAG_NEWS_DATE);
            String image = obj.getString(Constant.TAG_NEWS_IMAGE);
            String image_thumb = obj.getString(Constant.TAG_NEWS_IMAGE_THUMB);
            String tot_views = obj.getString(Constant.TAG_TOTAL_VIEWS);
            Boolean fav = obj.getBoolean(Constant.TAG_FAV);
            Boolean like = obj.getBoolean(Constant.TAG_LIKE);
            String share_link = obj.getString(Constant.TAG_SHARE_LINK);

            itemNews = new ItemNews(id, cat_id, cat_name, type, heading, desc, video_id, video_url, date, image, image_thumb, tot_views, share_link, fav,like, array);
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        singleNewsListener.onEnd(s, itemNews, arrayListComments, arrayListRelated);
        super.onPostExecute(s);
    }
}