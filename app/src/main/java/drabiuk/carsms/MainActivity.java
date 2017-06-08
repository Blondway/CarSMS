package drabiuk.carsms;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static android.content.pm.PackageManager.DONT_KILL_APP;

public class MainActivity extends AppCompatActivity {

    private static String NameOfPackage;
    private static String NameOfDB;
    private static DatabaseHandler dbRef;
    Toast toast;

    PackageManager pm;
    ComponentName componentName;
    Boolean activeFlag = false;


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

        final DatabaseHandler db = new DatabaseHandler(this);
        dbRef = db;

        File dbtest = new File("/data/data/" + getNameOfPackage() + "/databases/" + NameOfDB);
        if (dbtest.exists()) {
            Log.d("", "Database already exists.");
        } else {
            Log.d("", "Creating a new db.");
            db.addGroup(new ObjectGroup("Grupa domyślna", "Proszę zadzwonić później."));
            db.addGroup(new ObjectGroup("Praca", "Nie mogę odebrać. Skontaktuję się później."));
            db.addGroup(new ObjectGroup("Rodzina", "Jadę samochodem i nie mogę odebrać. Napisz SMS'a lub zadzwoń później :)"));
            db.addGroup(new ObjectGroup("Przyjaciele", "Nie mogę teraz rozmawiać. Zadzwoń później :)"));

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

                            if (!(LastPhoneNumber.equals(phoneNo)))
                                db.addContact(new Contact(name, phoneNo, 1));
                            LastPhoneNumber = phoneNo;

                        }
                        pCur.close();
                    }
                }
            }
        }


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
                    ShowNotification(1);
                    pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, DONT_KILL_APP);
                    activeFlag = true;

                } else {
                    textView.setText(switchOff);
                    cancelNotification(getApplicationContext(), 1);
                    pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, DONT_KILL_APP);
                    activeFlag = false;

                }
            }
        });
    }

    public static String getNameOfPackage() {
        return NameOfPackage;
    }

    public static String getNameOfDB() {
        return NameOfDB;
    }

    public static DatabaseHandler getDB() {
        return dbRef;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_grupy:
                if (activeFlag == false) {
                    startActivity(new Intent(MainActivity.this, Groups.class));
                } else {
                    toast = Toast.makeText(this, "Aby zmienić ustawienia wyłącz oczekiwanie na połączenie", Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                }
                return true;

            case R.id.menu_numery:
                if (activeFlag == false) {
                    startActivity(new Intent(MainActivity.this, ContactsGroupsList.class));
                } else {
                    toast = Toast.makeText(this, "Aby zmienić ustawienia wyłącz oczekiwanie na połączenie", Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
                return true;

            case R.id.menu_delete_db:
                deleteDBDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {

        cancelNotification(getApplicationContext(), 1);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, DONT_KILL_APP);
        finish();
        super.onDestroy();
    }

    private void cancelNotification(Context ctx, int notifyId) {
        String ns = ctx.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    private void ShowNotification(int notifyId) {
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this).setSmallIcon(R.drawable.logo_notification).setContentTitle("CarSMS").setContentText("Oczekiwanie na połączenie włączone.");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        mBuilder.setContentIntent(intent);
        notificationManager.notify(notifyId, mBuilder.build());
    }


    public void deleteDBDialog() {

        new AlertDialog.Builder(this).setTitle("CarSMS").setMessage("Czy chcesz usunąć bazę danych?").setNegativeButton("NIE", null)
                .setPositiveButton("TAK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        dbRef.deleteDB();
                        finish();
                    }
                }).create().show();
    }
}


