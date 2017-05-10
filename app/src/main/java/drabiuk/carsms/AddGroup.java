package drabiuk.carsms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.List;

public class AddGroup extends AppCompatActivity {

        String nazwa;
        String wiadomosc;
        Boolean GroupNameExist=false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        Button Button_Add = (Button) findViewById(R.id.btn_addgroup);

        final EditText Nazwa_Grupy   = (EditText)findViewById(R.id.addgroup_name);
        final EditText Tresc_Wiadomosci   = (EditText)findViewById(R.id.addgroup_msg);

        Button_Add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
                    {
                        GroupNameExist = false;
                        nazwa =  Nazwa_Grupy.getText().toString();
                        wiadomosc = Tresc_Wiadomosci.getText().toString();
                        List<ObjectGroup> grupy = MainActivity.getDB().getAllGroups();
                        int liczbagrup = MainActivity.getDB().getGroupCount();
                        if(!nazwa.equals("") && !wiadomosc.equals("") &&  !(nazwa.trim().length() == 0) && !(wiadomosc.trim().length() == 0)) {
                            for (int i=0;i<liczbagrup;i++) {
                                String nazwabezspacji = nazwa.replace(" ", "");
                                String nazwagrupybezspacji = grupy.get(i).getName().replace(" ", "");
                                if (nazwabezspacji.equals(nazwagrupybezspacji)){
                                    Toast.makeText(getApplicationContext(),"Taka grupa już istnieje!", Toast.LENGTH_LONG).show();
                                    GroupNameExist = true;
                                    break;
                                }}
                                if (       GroupNameExist == false){
                                DatabaseHandler db = drabiuk.carsms.MainActivity.getDB();
                                db.addGroup(new ObjectGroup(nazwa, wiadomosc));

                                Toast.makeText(AddGroup.this, "Grupa dodana!", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(getBaseContext(), Groups.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);}
                        }
                        else  Toast.makeText(AddGroup.this, "Uzupełnij wszystkie pola.", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
