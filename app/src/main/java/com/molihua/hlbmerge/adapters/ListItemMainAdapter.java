package com.molihua.hlbmerge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.entities.ListItemMain;
import com.molihua.hlbmerge.interfaces.IRecyclerViewOnItenClickListener;

import java.util.List;

/**
 * 准备合并页面适配器
 */
public class ListItemMainAdapter extends RecyclerView.Adapter<ListItemMainAdapter.ViewHolder> {

    private List<ListItemMain> listItemMains;//ListItemMain实体类集合
    private Context context;
    private IRecyclerViewOnItenClickListener recyclerOnItenClickListener;

    public ListItemMainAdapter(List<ListItemMain> listItemMains, Context context,IRecyclerViewOnItenClickListener recyclerOnItenClickListener) {
        this.listItemMains = listItemMains;
        this.context = context;
        this.recyclerOnItenClickListener = recyclerOnItenClickListener;

    }


    @NonNull
    @Override
    public ListItemMainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list_item_main, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemMainAdapter.ViewHolder holder, int position) {
        ListItemMain listItemMain = listItemMains.get(position);

        String name = listItemMain.getName();//从ListItemMain实体中获取name
        holder.item_textView.setText(name);

        int checkBoxVisibility = listItemMain.getCheckBoxVisibility();//从ListItemMain实体中获取CheckBoxVisibility
        holder.item_checkBox.setVisibility(checkBoxVisibility);
        /**
         * 不对checkBoxCheck设置监听通过listviewitem监听来实现(避免listview和checkbox一起用出现混乱的情况)
         * checkBox的checked状态通过从item的属性中读取
         */

        boolean checkBoxCheck = listItemMain.isCheckBoxCheck();
        holder.item_checkBox.setChecked(checkBoxCheck);

        holder.item_Relative.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return recyclerOnItenClickListener.LongClick(position);
            }
        });

        holder.item_Relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerOnItenClickListener.onClick(position);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return listItemMains==null ? 0 : listItemMains.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_textView;
        CheckBox item_checkBox;
        RelativeLayout item_Relative;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_textView = itemView.findViewById(R.id.item_textView);
            item_checkBox = itemView.findViewById(R.id.item_checkBox);
            item_Relative = itemView.findViewById(R.id.item_Relative);
        }
    }
}
