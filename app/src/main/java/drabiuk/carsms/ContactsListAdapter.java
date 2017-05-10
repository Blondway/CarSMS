package drabiuk.carsms;

/**
 * Created by Magdalena on 2017-04-19.
 */

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ContactsListAdapter extends ArrayAdapter<Contact> {

    List<Contact> ContactsItems = null;
    Context context;
    int DBGroupCount = MainActivity.getDB().getGroupCount();
    int[] id_tab=new int [DBGroupCount];

    public ContactsListAdapter(Context context, List<Contact> resource) {
        super(context,R.layout.element_group, resource);
        this.context = context;
        this.ContactsItems = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        List<ObjectGroup> grupy = MainActivity.getDB().getAllGroups();
        for(int i=0;i<DBGroupCount;i++)
        {
            id_tab[i]=grupy.get(i).getID();
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.element_contact, parent, false);

        final TextView textview_name = (TextView)convertView.findViewById(R.id.tw_name);
        final TextView textview_number = (TextView)convertView.findViewById(R.id.tw_number);
        textview_name.setText(ContactsItems.get(position).getName());
        textview_number.setText(ContactsItems.get(position).getPhoneNumber());

        List<ObjectGroup> groups = MainActivity.getDB().getAllGroups();
        String[] SpinnerGrupy = new String [MainActivity.getDB().getGroupCount()];

        for(int i=0;i<DBGroupCount;i++)
        {
            SpinnerGrupy[i]=groups.get(i).getName();
        }

        final Spinner s = (Spinner)convertView.findViewById(R.id.spinner_wybierzgrupe);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.element_spinner, SpinnerGrupy);
        s.setAdapter(adapter);

        int[] GroupIDsList=GetAllGroupsIDs();
        int SpinnerListPosition=0;

        for(int i=0;i<DBGroupCount;i++)
        {
            if( GroupIDsList[i]==(ContactsItems.get(position).getGroupID()))SpinnerListPosition=i;
        }
        s.setSelection(SpinnerListPosition);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ContactsItems.get(position).setGroupID(GetDBIDbyGroupName(s.getSelectedItem().toString()));
                MainActivity.getDB().updateContact(ContactsItems.get(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        return convertView;
    }

    int GetDBIDbyGroupName(String name)
    {
        for(int i=0;i<DBGroupCount;i++)
        {
            if(MainActivity.getDB().getGroup(id_tab[i]).getName().equals(name))
            {
                return MainActivity.getDB().getGroup(id_tab[i]).getID();
            }
        }
        return 0;
    }

    int[] GetAllGroupsIDs()
    {
        int[] out=new int[DBGroupCount];

        for(int i=0;i<DBGroupCount;i++)
        {
            out[i]=MainActivity.getDB().getGroup(id_tab[i]).getID();
        }
        return out;
    }
}