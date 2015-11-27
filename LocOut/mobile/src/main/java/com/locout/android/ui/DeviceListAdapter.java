package com.locout.android.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

        // update data
        ((TextView) rowView.findViewById(R.id.deviceName)).setText(device.getName());
        ((TextView) rowView.findViewById(R.id.deviceTrustLevel)).setText(device.getReadableTrustLevel(context));

        if (device.getTrustLevelPercentage() > Device.TRUST_LEVEL_MEDIUM) {
            ((ImageView) rowView.findViewById(R.id.deviceIcon)).setImageResource(R.drawable.ic_lock_open_black_48dp);
        } else {
            ((ImageView) rowView.findViewById(R.id.deviceIcon)).setImageResource(R.drawable.ic_lock_outline_black_48dp);
        }

        return rowView;
    }

    public ArrayList<Device> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }
}
