package drabiuk.carsms;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class GroupsUserAdapter extends ArrayAdapter<Contact> {

    List<Contact> ContactsItems = null;
    Context context;

    public GroupsUserAdapter(Context context, List<Contact> resource) {
        super(context, R.layout.element_user, resource);
        this.context = context;
        this.ContactsItems = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.element_user, parent, false);

        final TextView textview_name = (TextView) convertView.findViewById(R.id.tw2_name);
        final TextView textview_number = (TextView) convertView.findViewById(R.id.tw2_number);
        textview_name.setText(ContactsItems.get(position).getName());
        textview_number.setText(ContactsItems.get(position).getPhoneNumber());

        return convertView;
    }
}