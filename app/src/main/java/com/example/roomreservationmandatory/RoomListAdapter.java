package com.example.roomreservationmandatory;

import androidx.annotation.NonNull;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class RoomListAdapter extends ArrayAdapter<JsonRoomModel> {
    private final int resource;

    public RoomListAdapter(Context context, int resource, JsonRoomModel[] objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    public RoomListAdapter(Context context, int resource, List<JsonRoomModel> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        JsonRoomModel Room = getItem(position);
        int id = Room.getId();
        String name = Room.getName();
        String description = Room.getDescription();
        int capacity = Room.getCapacity();
        String remarks = Room.getRemarks();
        LinearLayout RoomView;

        if (convertView == null) {
            RoomView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(inflater);
            li.inflate(resource, RoomView, true);
        }
        else {
            RoomView = (LinearLayout) convertView;
        }
        TextView room_name_view = RoomView.findViewById(R.id.room_name_view);
        TextView room_descr_view = RoomView.findViewById(R.id.room_descr_view);
        TextView room_cap_view = RoomView.findViewById(R.id.room_cap_view);
        TextView room_remark_view = RoomView.findViewById(R.id.room_remark_view);

        room_name_view.setText("Værelse: " + name);
        room_descr_view.setText("Type: " + description);
        room_cap_view.setText("Størrelse: " + capacity);
        room_remark_view.setText("Andre anmærkninger: " + remarks);

        return RoomView;
    }
}
