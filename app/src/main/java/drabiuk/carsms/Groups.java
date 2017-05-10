package drabiuk.carsms;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

public class Groups extends AppCompatActivity {
    static List<ObjectGroup> groups;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        groups = MainActivity.getDB().getAllGroups();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ListView lv = (ListView) findViewById(R.id.listview1);
        GroupListAdapter adapter = new GroupListAdapter(getApplicationContext(), groups);
        lv.setAdapter(adapter);

        FloatingActionButton myFab = (FloatingActionButton)findViewById(R.id.fab_addgroup);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Groups.this, AddGroup.class));
           }
        });

        Button listanumerow = (Button)findViewById(R.id.przycisk_listanumerow);
        listanumerow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Groups.this, ContactsGroupsList.class));
            }
        });

    }
}