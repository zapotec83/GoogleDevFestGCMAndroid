package es.gdfcordoba.jorider;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * 
 * @author jorge
 *
 */
public class GcmIntentService extends IntentService {

    public final String TAG = GcmIntentService.class.getName();

    public GcmIntentService() {
        super(GcmIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) { // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                //                sendNotification("Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            	try {
					createSimpleNotification(extras);
				} catch (NullPointerException e) {
					Log.e(TAG, "Mensaje no recibido correctamente!!");
				} catch (Exception e) {
					Log.e(TAG, "Error!!!->" + (e!= null && e.getMessage()!=null?e.getMessage():"Exception"));
				}
            }
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    /**
     * 
     * @param extras
     * @throws NullPointerException
     * @throws Exception
     */
    public void createSimpleNotification(Bundle extras) throws NullPointerException, Exception{

    	String mensaje = extras.getString("message");
    	
    	Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
    	NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.icon)
        .setLargeIcon(bm)
        .setContentTitle(getString(R.string.app_name))
        .setContentText(mensaje);
    	// Creates an explicit intent for an Activity in your app
    	Intent resultIntent = new Intent(this, MainActivity.class);
    	resultIntent.putExtras(extras);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder.setAutoCancel(true);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());		
    }

}