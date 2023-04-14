/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.induxapp.notificationfcm.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.induxapp.notificationfcm.MainActivity;
import com.induxapp.notificationfcm.R;


import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.FileNotFoundException;
import java.util.Random;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    String notificationType = "0";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());


        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getNotification() == null) {
            String channelUrl = null;
            JSONObject sendBird = new JSONObject(remoteMessage.getData());
            try {

                handleNotification(this, sendBird.optString("title"), sendBird.optString("body"), sendBird.optString("body"), sendBird, sendBird);


            } catch (Exception e) {
                String aa = "aa";

            }

        } else {

            sendNoti(this,
                    remoteMessage.getNotification().getImageUrl(),
                    remoteMessage.getNotification().getBody(),
                    remoteMessage.getNotification().getTitle()
            );
        }
    }

    public void sendNoti(Context context, Uri iconUrl, String body, String title) {
        //RemoteViews - create a Custom Notification Layout in android?
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_push);
        contentView.setTextViewText(R.id.title, title);
        contentView.setTextViewText(R.id.text, body);

        if (iconUrl == null){
            contentView.setImageViewResource(R.id.imageLogo, R.mipmap.ic_launcher);

        }else {
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(iconUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            //largeIcon
                            contentView.setImageViewBitmap(R.id.imageLogo, resource);
//                            notificationBuilder.setLargeIcon(resource);
                            //Big Picture
//                            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(resource));
//                            Notification notification = notificationBuilder.build();
//                            notificationManager.notify(NotificationID.getID(), notification);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }




        Intent intent;

        intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_MUTABLE);
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.df_message_ring);
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // create channel, it may be multiple according the uses
        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(Utils.CHANNEL_SIREN_ID, Utils.CHANNEL_SIREN_NAME, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setLightColor(Color.GRAY);
            mChannel.enableLights(true);
            mChannel.setDescription(Utils.CHANNEL_SIREN_DESCRIPTION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            mChannel.setSound(soundUri, audioAttributes);

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, Utils.CHANNEL_SIREN_ID)
                //.setSmallIcon(R.mipmap.ic_launcher)
                .setSmallIcon(R.drawable.ic_location)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logo))
             //   .setTicker(message)
                .setContent(contentView)
//                .setContentTitle(message)
//                .setContentText(body)
                .setAutoCancel(true)
                .setLights(0xff0000ff, 300, 1000) // blue color
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mBuilder.setSound(soundUri);
        }

        int NOTIFICATION_ID = 1; // Causes to update the same notification over and over again.
        /**
         * //use for random notification
         *  int min = 200;
         *  int max = 400;
         *  int NOTIFICATION_ID = (int)(Math.random()*(max-min+1)+min);
         *
         */
        if (mNotificationManager != null) {
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }


    private void handleNotification(Context context, String message, String body, String imageIcon, JSONObject jsonObject, JSONObject sendBird) {
//        if (!NotificationS.isAppIsInBackground(getApplicationContext())) {
//            // app is in foreground, broadcast the push message
//
////            if(sendBird.optString("notificationType").equalsIgnoreCase("endservice")) {
//            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//            pushNotification.putExtra("message", message);
//            pushNotification.putExtra("body", body);
//            pushNotification.putExtra("statusService", sendBird.optString("notificationType"));
//
//            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
////            }
//
//            sendNoti(this, message, body, imageIcon);
//        } else {
           // sendNoti(this, message, body, imageIcon);

            // If the app is in background, firebase itself handles the notification
       // }


    }



}