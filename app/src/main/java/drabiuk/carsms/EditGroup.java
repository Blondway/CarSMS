package drabiuk.carsms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EditGroup extends AppCompatActivity {
    int id;
    EditText edytowana_nazwa;
    EditText edytowana_wiadomosc;
    Boolean GroupNameExist=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);


        Bundle extras = getIntent().getExtras();
        id = Integer.valueOf((Integer)extras.get("Group_ID"));

        List<Contact> contacts =new ArrayList<>();

        for(int i=1;i<MainActivity.getDB().getContactsCount();i++)
        {
            if(MainActivity.getDB().getContact(i).getGroupID()==id)contacts.add(MainActivity.getDB().getContact(i));
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ListView lv = (ListView) findViewById(R.id.lw_uzytkownicygrupy);
        GroupsUserAdapter adapter = new GroupsUserAdapter(getApplicationContext(), contacts);
        lv.setAdapter(adapter);

        edytowana_nazwa = (EditText)findViewById(R.id.edit_group_name);
        edytowana_wiadomosc = (EditText)findViewById(R.id.edit_group_message);

        ObjectGroup CurrentGroup = MainActivity.getDB().getGroup(id);

        edytowana_nazwa.setText(CurrentGroup.getName());
        edytowana_wiadomosc.setText(CurrentGroup.getMsg());

        if(CurrentGroup.getName().equals("Grupa domyślna"))
        {
            edytowana_nazwa.setFocusable(false);

        }
        Button clickButtonsave = (Button) findViewById(R.id.save_changes);
        clickButtonsave.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
        GroupNameExist = false;
                String nazwa = edytowana_nazwa.getText().toString();
                String wiadomosc =  edytowana_wiadomosc.getText().toString();

                List<ObjectGroup> grupy =new ArrayList<>();
                grupy=MainActivity.getDB().getAllGroups();
                int liczbagrup = MainActivity.getDB().getGroupCount();



            if(nazwa.equals("") || wiadomosc.equals("") || (nazwa.trim().length() == 0) || (wiadomosc.trim().length() == 0))
            {
                Toast.makeText(getApplicationContext(),"Uzupełnij wszystkie pola.", Toast.LENGTH_LONG).show();
            }
                else
            {
                for (int i=0;i<liczbagrup;i++) {
                    String nazwabezspacji = nazwa.replace(" ", "");
                    String nazwagrupybezspacji = grupy.get(i).getName().replace(" ", "");
                    if (nazwabezspacji.equals(nazwagrupybezspacji)){
                        Toast.makeText(getApplicationContext(),"Taka grupa już istnieje!", Toast.LENGTH_LONG).show();
                        GroupNameExist = true;
                        break;
                    }
                }
                if (GroupNameExist == false) {
                MainActivity.getDB().updateGroup(new ObjectGroup(id,edytowana_nazwa.getText().toString(),edytowana_wiadomosc.getText().toString()));
                Intent i = new Intent(getBaseContext(), Groups.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }}
            }
        });

        Button clickButtondelete = (Button) findViewById(R.id.button_deletegroup);

        if(CurrentGroup.getName().equals("Grupa domyślna"))
        {
            clickButtondelete.setEnabled(false);
        }

        clickButtondelete.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                for(int i=1;i<=MainActivity.getDB().getContactsCount();i++)
                {
                    if(MainActivity.getDB().getContact(i).getGroupID()==id)MainActivity.getDB().getContact(i).setGroupID(1);
                }

                MainActivity.getDB().deleteGroup(id);
                Intent i = new Intent(getBaseContext(), Groups.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

    }
}
