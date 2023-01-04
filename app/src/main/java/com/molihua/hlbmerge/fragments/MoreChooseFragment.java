package com.molihua.hlbmerge.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.LogUtils;
import com.molihua.hlbmerge.MainActivity;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.dialogs.JudgeMoreMergeDialog;
import com.molihua.hlbmerge.dialogs.MoreProgressDialog;
import com.molihua.hlbmerge.entities.ListItemMain;
import com.molihua.hlbmerge.interfaces.IActivityAndFragment;
import com.molihua.hlbmerge.utils.PathTools;

import java.util.ArrayList;
import java.util.List;
/**
 * 多选按钮Fragment
 */
public class MoreChooseFragment extends Fragment implements View.OnClickListener  {
    private View view;
    private Button btn_both_noboth;
    private Button btn_more_merge;
    private IActivityAndFragment IMainActivity;//定义activity与fragment通信接口
    private MainActivity mainActivity;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view==null){
            view = inflater.inflate(R.layout.fragment_show_hidden_morechoose, container, false);
            getComponents();//获取组件
            setListeners();//设置监听
            initData();//初始化数据
        }
        return view;
    }

    private void initData() {

    }

    private void setListeners() {
        btn_both_noboth.setOnClickListener(this);
        btn_more_merge.setOnClickListener(this);
    }

    private void getComponents() {
        btn_both_noboth=view.findViewById(R.id.btn_both_noboth);
        btn_more_merge=view.findViewById(R.id.btn_more_merge);
    }

    @Override
    public void onClick(View v) {
        MainShowListFragment mainShowListFragment = mainActivity.getMainShowListFragment();
        List<ListItemMain> listItemMains = mainShowListFragment.getListItemMains();

        switch (v.getId()){

            case R.id.btn_both_noboth ://全选按钮

                String text = btn_both_noboth.getText().toString();
                switch (text){
                    case "全选":
                        for (int i = 0; i < listItemMains.size(); i++) {
                            if (!listItemMains.get(i).getName().equals("返回上一级")){
                                listItemMains.get(i).setCheckBoxCheck(true);
                            }
                        }
                        btn_both_noboth.setText("取消全选");
                        break;
                    case "取消全选":
                        for (int i = 0; i < listItemMains.size(); i++) {
                            listItemMains.get(i).setCheckBoxCheck(false);
                        }
                        btn_both_noboth.setText("全选");
                        break;
                }
                mainShowListFragment.notifyDataAdapter();
                break;
            case R.id.btn_more_merge://合并按钮

                switch (mainShowListFragment.FLAG_PAGE){
                    case 0://页面层次是合集listview
                        //临时存放所有合并的item
                        List<ListItemMain> allCheckedListItemMains=new ArrayList<>();
                        if (allCheckedListItemMains.size()!=0){//如果大小不等与0就清理
                            allCheckedListItemMains.clear();
                        }
                        //获取所有勾选的合集item
                        for (int i = 0; i < listItemMains.size(); i++) {
                            if (listItemMains.get(i).isCheckBoxCheck()){//判断是否勾选
                                //临时存放一个合集下所有p item的集合
                                List<ListItemMain> morelistItemMains=new ArrayList<ListItemMain>();
                                //遍历一个合集获取它所有的p item
                                PathTools.getChapterPaths(morelistItemMains,listItemMains.get(i).getPath());
                                //把一个合集下的所有p item添加到总集合中
                                allCheckedListItemMains.addAll(morelistItemMains);
                            }
                        }



                        JudgeMoreMergeDialog.showJudgeMergeDialog(allCheckedListItemMains,mainActivity,1);

                        //MoreProgressDialog.showMoreProgressDialog(allCheckedListItemMains,mainActivity,MoreProgressDialog.TYPE_ROUGH);//开始合并进度粗略


                        //RxFfmpeg.execStatement(allCheckedListItemMains,mainActivity);//开始合并进度详细



                        break;
                    case 1://页面层次是p listview

                        List<ListItemMain> checkedListItemMains=new ArrayList<>();
                        if (checkedListItemMains.size()!=0){//如果大小不等与0就清理
                            checkedListItemMains.clear();
                        }
                        for (int i = 0; i < listItemMains.size(); i++) {
                            if (listItemMains.get(i).isCheckBoxCheck()){//判断是否勾选
                                checkedListItemMains.add(listItemMains.get(i));
                            }
                        }

                        //RxFfmpeg.execStatement(checkedListItemMains,mainActivity);//开始合并进度详细

                        JudgeMoreMergeDialog.showJudgeMergeDialog(checkedListItemMains,mainActivity,MoreProgressDialog.TYPE_ROUGH);//开始合并进度粗略
                        //MoreProgressDialog.showMoreProgressDialog(checkedListItemMains,mainActivity,MoreProgressDialog.TYPE_ROUGH);

                        //RxFfmpeg.execStatement(checkedListItemMains,mainActivity);//开始合并进度详细


                        break;

                }




                break;
        }
    }
    /**
     * 当Activity和Fragment产生关系时调用
     * context可以强转为Activity
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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
