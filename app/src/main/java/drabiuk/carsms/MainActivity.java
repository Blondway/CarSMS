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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static android.Manifest.permission_group.SMS;
import static android.content.pm.PackageManager.DONT_KILL_APP;

public class MainActivity extends AppCompatActivity {

    private static String NameOfPackage;
    private static String NameOfDB;
    private static DatabaseHandler dbRef;

    PackageManager pm;
    ComponentName componentName;
    NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission READ_CONTACTS not granted", Toast.LENGTH_LONG).show();
            finish();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission READ_PHONE_STATE not granted", Toast.LENGTH_LONG).show();
            finish();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission SEND_SMS not granted", Toast.LENGTH_LONG).show();
            finish();
        }


        pm = getApplicationContext().getPackageManager();
        componentName = new ComponentName(MainActivity.this, ServiceReceiver.class);

        NameOfPackage = getApplicationContext().getPackageName();
        NameOfDB = "CarSMS_DB";

        /*ImageButton Button_Settings = (ImageButton) findViewById(R.id.btn_settings);
        //final Button Button_Start = (Button) findViewById(R.id.btn_start);
        Button Button_DeleteDB = (Button) findViewById(R.id.button_deletedb);
        */
        final DatabaseHandler db = new DatabaseHandler(this);

        //Button_Start.setBackgroundColor(0xFFDDDDDD); // 0xAARRGGBB
        dbRef = db;


        File dbtest = new File("/data/data/" + getNameOfPackage() + "/databases/" + NameOfDB);
        if (dbtest.exists()) {
            Log.d("", "Database already exists.");
        } else {
            Log.d("", "Creating a new db.");
            db.addGroup(new ObjectGroup("Grupa domyślna", "Proszę zadzwonić później."));
            db.addGroup(new ObjectGroup("Praca", "Nie mogę odebrać. Skontaktuję się później."));
            db.addGroup(new ObjectGroup("Rodzina", "Jadę samochodem i nie mogę odebrać. Napisz SMS'a lub zadzwoń później :)"));
            db.addGroup(new ObjectGroup("Przyjaciele", "Nie mogę teraz rozmawiać. Zadzwon pozniej :)"));

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
                                db.addContact(new Contact(name, phoneNo, 1));
                            LastPhoneNumber = phoneNo;
                            //LastName = name;
                        }
                        pCur.close();
                    }
                }
            }
        }

       mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_notification)
                .setContentTitle("CarSMS")
                .setContentText("Oczekiwanie na połączenie włączone.")
                .setAutoCancel(true);

        /*Button_Settings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Button_Start.setBackgroundColor(0xFFDDDDDD);
                mNotificationManager.cancel(0);
                PackageManager pm = getApplicationContext().getPackageManager();
                ComponentName componentName = new ComponentName(MainActivity.this, ServiceReceiver.class);
                pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                startActivity(new Intent(MainActivity.this, Groups.class));
            }
        }); */

        Switch switchButton;
        final TextView textView;
        final String switchOn = "Oczekiwanie na połączenie włączone";
        final String switchOff = "Oczekiwanie na połączenie wyłączone";


        // For switch button
        switchButton = (Switch) findViewById(R.id.switch1);
        textView = (TextView) findViewById(R.id.textSwitch);

        switchButton.setChecked(false);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, DONT_KILL_APP);

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {

                if (bChecked) {
                    textView.setText(switchOn);
                    mNotificationManager.notify(0, mBuilder.build());
                    pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, DONT_KILL_APP);
                    //Toast.makeText(getApplicationContext(), "Oczekiwanie na połączenie włączone", Toast.LENGTH_SHORT).show();
                } else {
                    textView.setText(switchOff);
                    mNotificationManager.cancel(0);
                    pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, DONT_KILL_APP);
                    //Toast.makeText(getApplicationContext(), "Oczekiwanie na połączenie wyłączone", Toast.LENGTH_SHORT).show();
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case R.id.menu_grupy:
                // Single menu item is selected do something
                // Ex: launching new activity/screen or show alert message
                pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, DONT_KILL_APP);
                startActivity(new Intent(MainActivity.this, Groups.class));
                return true;

            case R.id.menu_numery:
                startActivity(new Intent(MainActivity.this, ContactsGroupsList.class));
                return true;

            case R.id.menu_delete_db:
                dbRef.deleteDB();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy()
    {
        mNotificationManager.cancel(0);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, DONT_KILL_APP);
        finish();
        super.onDestroy();
    }
}
