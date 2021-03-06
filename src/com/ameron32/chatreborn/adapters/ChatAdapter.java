package com.ameron32.chatreborn.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;

import com.ameron32.chatreborn.activities.MainActivity;
import com.ameron32.chatreborn.chat.MessageTemplates.*;
import com.ameron32.knbasic.core.chat.R;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter {
  
  private final Context          context;
  ViewHolder                     holder;
  
  private Map<Long, MessageBase> mData;
  
  public ChatAdapter(Context context, Map<Long, MessageBase> data) {
    super();
    this.context = context;
    mData = data;
  }
  
  public int getCount() {
    return mData.size();
  }
  
  public MessageBase getItem(int position) {
    return mData.get(getKeyAt(position));
  }
  
  public long getItemId(int arg0) {
    return arg0;
  }
  
  private long getKeyAt(int position) {
    int counter = 0;
    for (Map.Entry<Long, MessageBase> entry : mData.entrySet()) {
      if (position == counter) return entry.getKey();
      counter++;
    }
    return -1l;
  }
  
  @Override
  public View getView(final int position, View convertView, final ViewGroup parent) {
    final MessageBase item = getItem(position);
    if (item instanceof SystemMessage) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.chat_sysmsg_ui, parent, false);
      holder = new ViewHolder();
      holder.tvTime = (TextView) convertView.findViewById(R.id.tvTimeStamp);
      holder.tvUsr = (TextView) convertView.findViewById(R.id.tvUsr);
      holder.tvMsg = (TextView) convertView.findViewById(R.id.tvMsg);
      
      convertView.setTag(holder);
    }
    if (item instanceof ChatMessage) {
      // check for continued message
      boolean useDefaultInflater = true;
      if (position != 0) {
        final MessageBase previousItem = getItem(position - 1);
        if (previousItem instanceof ChatMessage && previousItem.name.equals(item.name)) {
          
          LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          convertView = inflater.inflate(R.layout.chat_bubble_continue, parent, false);
          holder = new ViewHolder();
          holder.tvTime = (TextView) convertView.findViewById(R.id.tvTimeStamp);
          // holder.tvUsr = (TextView)
          // convertView.findViewById(R.id.tvUsr);
          holder.tvMsg = (TextView) convertView.findViewById(R.id.tvMsg);
          
          convertView.setTag(holder);
          useDefaultInflater = false;
        }
      }
      
      if (useDefaultInflater) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.chat_bubble_ui, parent, false);
        holder = new ViewHolder();
        holder.tvTime = (TextView) convertView.findViewById(R.id.tvTimeStamp);
        holder.tvUsr = (TextView) convertView.findViewById(R.id.tvUsr);
        holder.tvMsg = (TextView) convertView.findViewById(R.id.tvMsg);
        
        convertView.setTag(holder);
      }
    }
    
    // both share slide rear
    holder.bEdit = (Button) convertView.findViewById(R.id.bEditChat);
    holder.bHide = (Button) convertView.findViewById(R.id.bHideChat);
    holder.bDelete = (Button) convertView.findViewById(R.id.bDeleteChat);
    
    // tmp OnClickListener
    final View.OnClickListener tmp = new View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        MainActivity a = (MainActivity) context;
        a.showMessage("Not yet implemented.");
      }
    };
    
    // cheating to allow SystemMessage to avoid these buttons
    if (item instanceof ChatMessage) {
      holder.bEdit.setOnClickListener(tmp);
      holder.bHide.setOnClickListener(tmp);
      holder.bDelete.setOnClickListener(tmp);
    }
    
    Long timeStamp = item.getTimeStamp();
    holder.tvTime.setText(new SimpleDateFormat("h:mma", Locale.US).format(timeStamp));
    if (holder.tvUsr != null) holder.tvUsr.setText(item.name);
    holder.tvMsg.setText(item.getText());
    
    return convertView;
  }
  
  public static class ViewHolder {
    
    TextView tvTime, tvUsr, tvMsg;
    Button   bEdit, bHide, bDelete;
  }
  
  public void clear() {
    mData.clear();
  }
  
  public void addAll(TreeMap<Long, MessageBase> history) {
    mData.putAll(history);
  }
  
  public void remove(int position) {
    mData.remove(getKeyAt(position));
  }
  
}
