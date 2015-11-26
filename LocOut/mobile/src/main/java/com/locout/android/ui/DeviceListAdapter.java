package com.locout.android.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.locout.android.R;
import com.locout.android.api.Device;

import java.util.ArrayList;
import java.util.List;

public class DeviceListAdapter extends ArrayAdapter<Device>{

    private Activity context;
    private ArrayList<Device> devices;

    public DeviceListAdapter(Context context, int resource, List<Device> devices) {
        super(context, resource, devices);

        this.context = (Activity) context;
        this.devices = (ArrayList<Device>) devices;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        Device device = devices.get(position);

        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.device_list_item, null);
            rowView.setTag(device.getId());
        }

        // fill data
        ((TextView) rowView.findViewById(R.id.deviceName)).setText(device.getName());
        ((TextView) rowView.findViewById(R.id.deviceTrustLevel)).setText(String.valueOf(device.getTrustLevelPercentage()));


        return rowView;
    }
}
