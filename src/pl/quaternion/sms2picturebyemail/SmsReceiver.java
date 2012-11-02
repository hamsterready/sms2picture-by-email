package pl.quaternion.sms2picturebyemail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

  private static final String TAG = "SmsReceiver";

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "Got SMS: " + intent + ".");

    final String originatingAddress = SmsMessage.createFromPdu((byte[]) ((Object[]) intent.getExtras().get("pdus"))[0]).getOriginatingAddress();
    final String acceptedMobileNumber = context.getSharedPreferences(InfoActivity.PREFERENCES, 0).getString(InfoActivity.PREFERENCES_MOBILE_NUMBER, "");

    if (acceptedMobileNumber.equals(originatingAddress)) {
      context.startService(new Intent(context, TakePictureService.class));
    }
  }
}
