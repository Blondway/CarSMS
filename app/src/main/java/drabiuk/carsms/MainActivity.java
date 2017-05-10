package drabiuk.carsms;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static String NameOfPackage;
    private static String NameOfDB;
    private static DatabaseHandler dbRef;

    Boolean StartFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NameOfPackage=getApplicationContext().getPackageName();
        NameOfDB="CarSMS_DB";

        ImageButton Button_Settings = (ImageButton) findViewById(R.id.btn_settings);
        final Button Button_Start = (Button) findViewById(R.id.btn_start);
        Button Button_DeleteDB = (Button) findViewById(R.id.button_deletedb);
        final DatabaseHandler db = new DatabaseHandler(this);

        Button_Start.setBackgroundColor(0xFFDDDDDD); // 0xAARRGGBB
        dbRef=db;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"Permission READ_CONTACTS not granted", Toast.LENGTH_LONG).show();finish();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Permission SEND_SMS not granted", Toast.LENGTH_LONG).show();finish();
        }

        File dbtest = new File("/data/data/"+getNameOfPackage()+"/databases/"+NameOfDB);
        if(dbtest.exists())
        {
            Log.d("", "Database already exists.");
        }
        else {
            Log.d("", "Creating a new db.");
            db.addGroup(new ObjectGroup("Grupa domyślna", "Prosze zadzwonic pozniej."));
            db.addGroup(new ObjectGroup("Praca", "Nie moge odebrac. Kieruje swoim najszybszym samochodem na swiecie. Oddzwonie pozniej."));
            db.addGroup(new ObjectGroup("Rodzina", "Nie moge odebrac. Jak cos waznego to wyslij SMSa albo zadzwon pozniej :)"));
            db.addGroup(new ObjectGroup("Przyjaciele", "Kieruje moim Hyundai'em. Jade zbyt szybko zeby odebrac. Zadzwon pozniej :)"));

            String LastPhoneNumber = "", LastName = "";
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));

                    if (cur.getInt(cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phoneNo = phoneNo.replace("+48", "");
                            phoneNo = phoneNo.replace("-", "");
                            phoneNo = phoneNo.replace(" ", "");
                            //&& !(LastName.equals(name))
                            if (!(LastPhoneNumber.equals(phoneNo)))
                                db.addContact(new Contact(name, phoneNo, 1 ));
                            LastPhoneNumber = phoneNo;
                            //LastName = name;
                        }
                        pCur.close();
                    }
                }
            }
        }

        final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("CarSMS App")
                .setContentText("Jak ktoś teraz zadzwoni to dostanie smsa!")
                .setAutoCancel(true);

        Button_Settings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Button_Start.setBackgroundColor(0xFFDDDDDD);
                mNotificationManager.cancel(0);
                PackageManager pm  = getApplicationContext().getPackageManager();
                ComponentName componentName = new ComponentName(MainActivity.this, ServiceReceiver.class);
                pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
                startActivity(new Intent(MainActivity.this, Groups.class));
            }
        });
        Button_Start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    if(StartFlag==true) {
                        StartFlag = false;
                        Button_Start.setBackgroundColor(0xFFDDDDDD);
                        mNotificationManager.cancel(0);

                        PackageManager pm  = getApplicationContext().getPackageManager();
                        ComponentName componentName = new ComponentName(MainActivity.this, ServiceReceiver.class);
                        pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
                        Toast.makeText(getApplicationContext(), "BroadcastReceiver cancelled", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        StartFlag=true;
                        Button_Start.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        mNotificationManager.notify(0, mBuilder.build());

                        PackageManager pm  = getApplicationContext().getPackageManager();
                        ComponentName componentName = new ComponentName(MainActivity.this, ServiceReceiver.class);
                        pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                        Toast.makeText(getApplicationContext(), "BroadcastReceiver activated", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        Button_DeleteDB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                db.deleteDB();
                finish();
            }
        });
    }
    public static String getNameOfPackage()
    {
        return NameOfPackage;
    }
    public static String getNameOfDB()
    {
        return NameOfDB;
    }
    public static DatabaseHandler getDB()
    {
        return dbRef;
    }

}
