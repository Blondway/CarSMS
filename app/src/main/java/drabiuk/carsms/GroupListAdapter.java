package drabiuk.carsms;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Magdalena on 2017-04-19.
 */import java.util.List;

public class GroupListAdapter extends ArrayAdapter<ObjectGroup> {

    List<ObjectGroup> GroupItems = null;
    Context context;

    public GroupListAdapter(Context context, List<ObjectGroup> resource) {
        super(context,R.layout.element_group, resource);
        this.context = context;
        this.GroupItems = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.element_group, parent, false);

        final TextView textviewGroupName = (TextView)convertView.findViewById(R.id.groupname);
        final TextView textviewGroupMSG = (TextView)convertView.findViewById(R.id.groupmessage);
        final ImageButton settings = (ImageButton)convertView.findViewById(R.id.group_settings);

        textviewGroupName.setText(GroupItems.get(position).getName());
        textviewGroupMSG.setText(GroupItems.get(position).getMsg());

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),EditGroup.class);
                i.putExtra("Group_ID", GroupItems.get(position).getID());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(i);
            }
        });
        return convertView;
    }
}