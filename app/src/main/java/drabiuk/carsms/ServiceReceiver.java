package drabiuk.carsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;

public class ServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING))
        {
            incomingNumber = incomingNumber.replace("+48", "");
            String msg = MainActivity.getDB().GetMessageForPhoneNumber(incomingNumber);
            //Log.i("TAKA WIADOMOSC", msg);
            ArrayList<String> arrSMS = SmsManager.getDefault().divideMessage(msg);
            if(!msg.equals("NUMBER_NOT_IN_CONTACT_LIST"))SmsManager.getDefault().sendMultipartTextMessage(incomingNumber, null, arrSMS, null, null);
            //Log.d("NUMER DZWONI:",incomingNumber);
        }
    }
}