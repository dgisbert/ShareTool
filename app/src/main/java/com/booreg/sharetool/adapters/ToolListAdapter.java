package com.booreg.sharetool.adapters;

import de.greenrobot.event.EventBus;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.booreg.sharetool.R;
import com.booreg.sharetool.activities.ToolActivity;
import com.booreg.sharetool.model.Tool;

import java.util.List;
import java.util.Locale;

/**
 * Adapter class to show contact list on screen.
 */

public class ToolListAdapter extends ArrayAdapter<Tool> implements AdapterView.OnItemClickListener
{
    private List<Tool> toolList;

    //*****************************************************************************************************************
    // Private inner classes
    //*****************************************************************************************************************

    private static class ViewHolder
    {
        protected TextView toolNameItem;
        protected TextView toolDistItem;
        protected TextView toolPricItem;
        protected TextView toolCityItem;
    }

    //*****************************************************************************************************************
    // Getters
    //*****************************************************************************************************************

    /** Returns the tools list of the adapter */ public List<Tool> getToolList() { return toolList; }

    //*****************************************************************************************************************
    // Public section
    //*****************************************************************************************************************

    @Override public int  getCount ()             { return toolList.size(); }
    @Override public Tool getItem  (int position) { return toolList.get(position); }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Tool tool = toolList.get(position);

        Context context = getContext();

        Intent intent = new Intent(context, ToolActivity.class);

        EventBus.getDefault().postSticky(tool);

        context.startActivity(intent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view;

        final ViewHolder viewHolder;

        if (convertView == null || convertView.getTag() == null)
        {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(R.layout.tool_item, parent, false);

            viewHolder.toolNameItem = (TextView) view.findViewById(R.id.toolNameItem);
            viewHolder.toolDistItem = (TextView) view.findViewById(R.id.toolDistItem);
            viewHolder.toolPricItem = (TextView) view.findViewById(R.id.toolPricItem);
            viewHolder.toolCityItem = (TextView) view.findViewById(R.id.toolCityItem);

            view.setTag(viewHolder);

        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
            view = convertView;
        }

        // Set text with the item name

        Locale locale = Locale.getDefault();

        Tool tool = toolList.get(position);

        viewHolder.toolNameItem.setText(tool.getName());

        String city = tool.getCity();
        Number dist = tool.getDist();
        Number pric = tool.getPric();

        if (dist != null) viewHolder.toolDistItem.setText(String.format(locale, "%.2f", dist.doubleValue()));
        if (pric != null) viewHolder.toolPricItem.setText(String.format(locale, "%.2f", pric.doubleValue()));
        if (city != null) viewHolder.toolCityItem.setText(city);

        return view;
    }

    //*****************************************************************************************************************
    // Constructor
    //*****************************************************************************************************************

    public ToolListAdapter(Context context, List<Tool> toolList)
    {
        super(context, R.layout.tool_item);

        this.toolList = toolList;
    }
}
