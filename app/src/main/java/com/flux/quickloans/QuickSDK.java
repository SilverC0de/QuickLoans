package com.flux.quickloans;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.NotificationCompat;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

class QuickSDK {

    static void sendNotification(Context cx, String head, String body, Intent intent){
        NotificationManager notificationManager = (NotificationManager) cx.getSystemService(Context.NOTIFICATION_SERVICE);

        int requestCode = 0;
        int requestID = 1;
        String channelId = cx.getPackageName();
        String channelName = cx.getResources().getString(R.string.app_name);
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT > 23) importance = NotificationManager.IMPORTANCE_HIGH;


        if (Build.VERSION.SDK_INT > 25) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(cx, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(head)
                .setContentText(body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(cx);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(requestCode, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        notificationManager.notify(requestID, mBuilder.build()); //notificationID insttead of reuestID
    }

    static String MD5(String md5) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ignored) { }
        return null;
    }

    static String tripleDES (String card, String key){
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] digestOfPassword = md.digest(key.getBytes(StandardCharsets.UTF_16LE));

            byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

            for (int j = 0, k = 16; j < 8;) {
                keyBytes[k++] = keyBytes[j++];
            }

            SecretKey secretKey = new SecretKeySpec(keyBytes, 0, 24, "DESede");

            IvParameterSpec iv = new IvParameterSpec(new byte[8]);
            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            byte[] plainTextBytes = card.getBytes(StandardCharsets.UTF_16LE);
            byte[] cipherText = cipher.doFinal(plainTextBytes);

            return new String(Base64.encodeBase64(cipherText));
        } catch ( NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException
                | IllegalBlockSizeException
                | InvalidAlgorithmParameterException
                | BadPaddingException ignored)
        { return null; }
    }

    static String relativeTime(String stamp, boolean inMillis){
        long time = Long.parseLong(stamp);
        int milliSeconds = 1000;
        int minute = 60 * milliSeconds;
        int hour = 60 * minute;
        int day = 24 * hour;

        if (!inMillis) time *= 1000; //convert this time to java millis if time is not in php timestamp
        long now = new Date().getTime();
        final long diff = now - time;
        if (diff < minute) {
            return "Just now";
        } else if (diff < 2 * minute) {
            return "A minute ago";
        } else if (diff < 50 * minute) {
            return diff / minute + " minutes ago";
        } else if (diff < 90 * minute) {
            return "An hour ago";
        } else if (diff < 24 * hour) {
            return diff / hour + " hours ago";
        } else if (diff < 48 * hour) {
            return "Yesterday";
        } else {
            return diff / day + " days ago";
        }
    }

    static void hideSoftKeys(Activity face) {
        InputMethodManager im = (InputMethodManager) face.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = face.getCurrentFocus();
        if (view == null) view = new View(face);
        if (im != null) im.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}