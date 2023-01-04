package com.molihua.hlbmerge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.entities.VideoListItem;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import java.util.List;

/**
 * 合并完成页面适配器
 */
public class VideoListItemAdapter extends BaseAdapter {
    private List<VideoListItem> videoListItems;//ListItemMain实体类集合
    private Context context;
    public VideoListItemAdapter(List<VideoListItem> videoListItems, Context context) {
        this.videoListItems = videoListItems;
        this.context = context;
    }
    @Override
    public int getCount() {
        return videoListItems==null ? 0 : videoListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return videoListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView==null){
            holder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_complete_video_list, null);
            //convertView = View.inflate(context, R.layout.item_listitemmain, null);//获取item布局文件

            holder.video_thumbnail = convertView.findViewById(R.id.video_thumbnail);
            holder.video_name = convertView.findViewById(R.id.video_name);
            holder.video_checkBox = convertView.findViewById(R.id.video_checkBox);
            convertView.setTag(holder);//实体内部类绑定控件
        }else {
            holder=(ViewHolder) convertView.getTag();
        }

        VideoListItem videoListItem = videoListItems.get(position);

        String videoName = videoListItem.getVideoName();//从ListItemMain实体中获取name
        holder.video_name.setText(videoName);

        int checkBoxVisibility = videoListItem.getCheckBoxVisibility();//从ListItemMain实体中获取CheckBoxVisibility
        holder.video_checkBox.setVisibility(checkBoxVisibility);

        boolean checkBoxCheck = videoListItem.isCheckBoxCheck();
        holder.video_checkBox.setChecked(checkBoxCheck);

        //Bitmap localVideoBitmap = FileTools.getLocalVideoBitmap(videoListItem.getVideoPath());

        //Bitmap videoThumbnail = FileTools.createVideoThumbnail(videoListItem.getVideoPath(), 1);
        //holder.video_thumbnail.setImageBitmap(videoThumbnail);




        return convertView;


    }
    class ViewHolder{
        RadiusImageView video_thumbnail;

        TextView video_name;
        CheckBox video_checkBox;

    }
}
