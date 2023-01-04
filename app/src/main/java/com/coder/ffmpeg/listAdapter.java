package com.coder.ffmpeg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class listAdapter extends BaseAdapter  {
    private Map<Integer,Boolean> map=new HashMap<>();// 存放已被选中的CheckBox
    public static List<ViewHolder> more_holder=new ArrayList();
    static List<list_Item> mlist;
    public static List<String> more_choose=new ArrayList();
    private Context mContent;
    public listAdapter(List<list_Item> mlist,Context context){
        more_holder.clear();//清除上一个页面残留
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
    public boolean isEnabled(int position) {//重写isEnabled(int position)方法
        return true;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=LayoutInflater.from(mContent).inflate(R.layout.layout_list_item,null,false);
            holder.more_checkBox=convertView.findViewById(R.id.more_checkBox);
            holder.tvText=convertView.findViewById(R.id.item);
            holder.more_checkBox.setVisibility(View.GONE);

            //安卓7以上
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                holder.item_Relative=convertView.findViewById(R.id.item_Relative);
//                holder.item_Relative.setBackgroundResource(R.drawable.bg_buttonmain);
//            }

            convertView.setTag(holder);
            more_holder.add(holder);
        }else {
            holder=(ViewHolder) convertView.getTag();
        }
        String MC=mlist.get(position).getMC();//文字标题：缓存集合的某一个章节的路径）
        String LJ=mlist.get(position).getLJ();
        holder.tvText.setText(MC);

//        holder.more_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                //设置checkBox的点击事件
//                if (isChecked) {//选中状态
//                    if (!more_choose.contains(LJ))
//                        more_choose.add(LJ);
//                    //Log.d("起飞：",use_choose+"----------------------");
//                } else {//未选中状态
//                    if (more_choose.contains(LJ))//如果集合中包含了该元素则移除
//                        more_choose.remove(LJ);
//                    //Log.d("起飞：",use_choose+"----------------------");
//                }
//            }
//        });
/////////////////////////必须这样大坑//  赋值之前取消CheckBox监听
        holder.more_checkBox.setOnCheckedChangeListener(null);


        if(map!=null&&map.containsKey(position)){
            holder.more_checkBox.setChecked(true);

        }else {
            holder.more_checkBox.setChecked(false);
        }
        //排除checkbox错乱的方法
        holder.more_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    map.put(position,true);
                    more_choose.add(LJ);
                }else {
                    map.remove(position);
                    more_choose.remove(LJ);
                }
            }
        });
/////////////////////////



        return convertView;
    }



    class ViewHolder{
        TextView tvText;
        CheckBox more_checkBox;
        RelativeLayout item_Relative;
    }
}
