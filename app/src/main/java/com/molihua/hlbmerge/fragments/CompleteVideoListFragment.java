package com.molihua.hlbmerge.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.molihua.hlbmerge.MainActivity;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.activities.PlayVideoActivity;
import com.molihua.hlbmerge.adapters.VideoListItemAdapter;
import com.molihua.hlbmerge.entities.VideoListItem;
import com.molihua.hlbmerge.interfaces.IActivityAndFragment;
import com.molihua.hlbmerge.utils.PathTools;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 合并完成列表Fragment
 */
public class CompleteVideoListFragment extends Fragment implements View.OnClickListener , AdapterView.OnItemClickListener {
    private View view;
    private RadiusImageView video_thumbnail;
    private static List<VideoListItem> videoListItems;//ListItemMain实体类集合
    private ListView frag_showvideo_list;//listview
    private VideoListItemAdapter videoListItemAdapter;//Adapter
    private MainActivity mainActivity;
    private IActivityAndFragment IMainActivity;//定义activity与fragment通信接口


    public CompleteVideoListFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.e("onCreate");

        videoListItems=new ArrayList<>();
        listViewDataShow();

        super.onCreate(savedInstanceState);

    }

    public void listViewDataShow() {


        try {
            PathTools.traverseVideoFile(videoListItems,PathTools.getOutputPath());
            if (videoListItemAdapter!=null){
                videoListItemAdapter.notifyDataSetChanged();//刷新数据
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.e("onCreateView");
        if (view==null){
            view = inflater.inflate(R.layout.fragment_complete_video_list, container, false);
            getComponents();//获取组件
            setListeners();//设置监听
            initData();//初始化数据
        }
        return view;
    }

    //清理
    public void clearVideoListItems() {
        this.videoListItems.clear();
    }

    private void initData() {
        videoListItemAdapter = new VideoListItemAdapter(videoListItems,this.getContext());//实例化Adapter
        frag_showvideo_list.setAdapter(videoListItemAdapter);//listview设置Adapter

    }

    private void setListeners() {
        //video_thumbnail.setOnClickListener(this);
        frag_showvideo_list.setOnItemClickListener(this);
    }

    private void getComponents() {
        //video_thumbnail=view.findViewById(R.id.video_thumbnail);
        frag_showvideo_list=view.findViewById(R.id.frag_showvideo_list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_thumbnail :
                ToastUtils.make().show("点击了图片");
                break;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(mainActivity, PlayVideoActivity.class);
        intent.putExtra("videoPath",videoListItems.get(position).getVideoPath());
        startActivity(intent);
    }

    /**
     * 当Activity和Fragment产生关系时调用
     * context可以强转为Activity
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //注意可能会内存泄露
        if (mainActivity==null){
            mainActivity= (MainActivity) context;
        }

        try {
            //获取通信接口实例
            IMainActivity= (IActivityAndFragment) context;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("Activity必须实现IMainActivityAndMainShowAreaList接口");
        }
    }
    /**
     * 当Activity和Fragment脱离时调用
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }


}