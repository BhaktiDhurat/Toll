package collection.toll.online.com.onlinetollcollection.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import collection.toll.online.com.onlinetollcollection.NotificationActivity;
import collection.toll.online.com.onlinetollcollection.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static String previousTollId = "0";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.e("########", "remoteMessage: " + remoteMessage);
        Log.e("########", "remoteMessage data : " + remoteMessage.getData());   //e data : {message={"notification":{"body":"Hi","title":"App"}}}
        String message = remoteMessage.getData().toString();
        System.out.println("Before Substring : -----------" + message);
        JSONObject message1 = null;
        try {
            JSONObject msgObject = new JSONObject(message);
            message1 = msgObject.getJSONObject("message");
            System.out.println("After ************" + message1.toString());
            message = message1.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        message=message.substring(message.lastIndexOf("{"),message.indexOf("}")+1);

        Log.e("########", "message : " + message);   //e data : {message={"notification":{"body":"Hi","title":"App"}}}


        //Calling method to generate notification
        try {
            sendNotification(message1);
        } catch (JSONException e) {
            System.out.println("********************************** Error sending notification;;;;");
            e.printStackTrace();
        }
    }

    private void sendNotification(JSONObject message) throws JSONException {
        String tollName, tollId;
        JSONArray jarr = message.getJSONArray("toll_location");
        for (int i = 0; i < jarr.length(); i++) {
            try {
                JSONObject jsonObject = jarr.getJSONObject(0);
                tollName = jsonObject.getString("TollName");
                tollId = jsonObject.getInt("toll id") + "";

                if (!tollId.equals(previousTollId)) {
                    previousTollId = tollId;
                    notification(tollName, tollId);
                }

                notification(tollName, tollId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void notification(String tollName, String tollId) {
        Intent intent = new Intent(MyFirebaseMessagingService.this, NotificationActivity.class);
        intent.putExtra("tollName", tollName);
        intent.putExtra("tollId", tollId);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Tolls Notification")
                .setContentText(tollName + " Toll")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

}