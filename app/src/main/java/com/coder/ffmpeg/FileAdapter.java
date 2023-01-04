package com.coder.ffmpeg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileAdapter extends BaseAdapter {
    private Map<Integer,Boolean> map=new HashMap<>();// 存放已被选中的CheckBox
    public static List<ViewHolder> all_holder=new ArrayList();
    public static int buju_code=0;//判断是哪个界面
    List<fileItem> mlist;
    ViewHolder holder;
    private Context mContent;
    public static List<String> use_choose=new ArrayList();//存放的是用户选择合并文件夹的全路径的字符串
    public FileAdapter(List<fileItem> mlist,Context context){
        this.mlist=mlist;
        this.mContent=context;
    }
    @Override
    public int getCount() {
        return mlist!=null?mlist.size():0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Log.e("getView被调用索引",position+"");

        if(convertView==null){
            holder=new FileAdapter.ViewHolder();//获取holder

            if (buju_code==0) {
                convertView= LayoutInflater.from(mContent).inflate(R.layout.file_item,null);//获取item视图
                holder.file_Image=convertView.findViewById(R.id.file_image);
                holder.file_checkBox = convertView.findViewById(R.id.file_checkBox);
                holder.tvText=convertView.findViewById(R.id.file_text);
            }else {
                convertView= LayoutInflater.from(mContent).inflate(R.layout.splitmerge_file_item,null);
                holder.file_Image=convertView.findViewById(R.id.splitmerge_file_image);
                holder.tvText=convertView.findViewById(R.id.splitMerge_file_text);
            }
            convertView.setTag(holder);//设置
            all_holder.add(holder);//供其他类处理item
        }else {
            holder=(FileAdapter.ViewHolder) convertView.getTag();
        }



        boolean icon=mlist.get(position).geticon();
        String pname=mlist.get(position).getLJ();
        if (!icon){//不是文件夹则更换图标
            holder.file_Image.setImageResource(R.drawable.file_tp);//文件图标
        }

        if (pname.equals(splitMerge_Main.backupdir)){//如果是返回上一级目录则用文件夹图标
            holder.file_Image.setImageResource(R.drawable.dir_tp);//文件夹图标
        }


        String LJ=mlist.get(position).getLJ();//获取item全路径
        int first = LJ.indexOf("/"); //单引号第一次出现的位置
        int last = LJ.lastIndexOf("/"); //单引号最后一次出现的位置
        String aa = LJ.substring(first, last+1);//截取后变成新的字符串
        String newLJ=LJ.replace(aa,"");
        holder.tvText.setText(newLJ);//设置item名称


        if (buju_code==0) {

//            holder.file_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    Log.e("onCheckedChanged被调用索引",position+"");
//                    //设置checkBox的点击事件
//                    if (isChecked) {//选中状态
//                        if (!use_choose.contains(LJ))
//                            use_choose.add(LJ);
//                        //Log.d("起飞：",use_choose+"----------------------");
//                    } else {//未选中状态
//                        if (use_choose.contains(LJ))//如果集合中包含了该元素则移除
//                            use_choose.remove(LJ);
//                        //Log.d("起飞：",use_choose+"----------------------");
//                    }
//                }
//            });


//排除checkbox错乱的方法

            holder.file_checkBox.setOnCheckedChangeListener(null);

            if(map!=null&&map.containsKey(position)){
                holder.file_checkBox.setChecked(true);

            }else {
                holder.file_checkBox.setChecked(false);

            }
//CheckBox监听
            holder.file_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked==true){
                        map.put(position,true);
                        use_choose.add(LJ);
                    }else {
                        map.remove(position);
                        use_choose.remove(LJ);
                    }
                }
            });








        }





        if(!mlist.get(position).geticon()) {//判断是否是文件如果是则设置选项不可见
            if (buju_code==0) {
                holder.file_checkBox.setVisibility(View.INVISIBLE);
            }
        }else {
            if (buju_code==0) {
                holder.file_checkBox.setVisibility(View.VISIBLE);
            }
        }







        return convertView;
    }
    class ViewHolder{
        ImageView file_Image;
        TextView tvText;
        CheckBox file_checkBox;
    }
}
