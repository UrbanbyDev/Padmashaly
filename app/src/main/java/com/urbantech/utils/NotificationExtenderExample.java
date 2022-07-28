package com.urbantech.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;
import com.urbantech.padmashali.R;
import com.urbantech.padmashali.SplashActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import androidx.core.app.NotificationCompat;


public class NotificationExtenderExample extends NotificationExtenderService {

    String title, message, bigpicture, url, objectID = "", objectTitle = "", objectType = "";

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {

        title = receivedResult.payload.title;
        message = receivedResult.payload.body;
        bigpicture = receivedResult.payload.bigPicture;
        try {
            objectID = receivedResult.payload.additionalData.getString("post_id");
            objectTitle = receivedResult.payload.additionalData.getString("title");
            objectType = receivedResult.payload.additionalData.getString("type");
            url = receivedResult.payload.additionalData.getString("external_link");
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendNotification();

        return true;
    }

    private void sendNotification() {
        Random random = new Random();
        int noti_id = random.nextInt(100);

        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent;
        if (!title.equals("")) {
            intent = new Intent(getApplicationContext(), SplashActivity.class);
            intent.putExtra("ispushnoti", true);
            intent.putExtra("id", objectID);
            intent.putExtra("name", objectTitle);
            intent.putExtra("type", objectType);
        } else if (url != null && !url.equals("false") && !url.trim().isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
        } else {
            intent = new Intent(getApplicationContext(), SplashActivity.class);
        }

        NotificationChannel mChannel;
        String NOTIFICATION_CHANNEL_ID = "news_push";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "News Channel";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), noti_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(true)
                .setSound(uri)
                .setLights(Color.RED, 800, 800)
                .setContentText(message)
                .setChannelId(NOTIFICATION_CHANNEL_ID);

        mBuilder.setSmallIcon(getNotificationIcon(mBuilder));

        mBuilder.setContentTitle(title);
        mBuilder.setTicker(message);

        if (bigpicture != null) {
            mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(getBitmapFromURL(bigpicture)).setSummaryText(message));
        } else {
            mBuilder.setContentText(message);
        }

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(noti_id, mBuilder.build());

    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(getColour());
            return R.drawable.ic_notification;
        } else {
            return R.mipmap.ic_launcher;
        }
    }

    private int getColour() {
        return 0xee2c7a;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            InputStream input;
            URL url = new URL(src);
            if (src.contains("https://")) {
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
            } else {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
            }

            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}
