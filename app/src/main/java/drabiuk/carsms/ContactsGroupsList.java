package drabiuk.carsms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ListView;

import java.util.List;

public class ContactsGroupsList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_groups_list);

        List<Contact> contacts = drabiuk.carsms.MainActivity.getDB().getAllContacts();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ListView lv = (ListView) findViewById(R.id.listanumerow);
        ContactsListAdapter adapter = new ContactsListAdapter(getApplicationContext(), contacts);
        lv.setAdapter(adapter);
    }
}
