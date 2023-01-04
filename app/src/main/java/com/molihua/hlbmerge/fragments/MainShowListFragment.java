package com.molihua.hlbmerge.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.molihua.hlbmerge.MainActivity;
import com.molihua.hlbmerge.R;
import com.molihua.hlbmerge.adapters.ListItemMainAdapter;
import com.molihua.hlbmerge.dialogs.JudgeSingleMergeDialog;
import com.molihua.hlbmerge.entities.ListItemMain;
import com.molihua.hlbmerge.interfaces.IActivityAndFragment;
import com.molihua.hlbmerge.interfaces.IRecyclerViewOnItenClickListener;
import com.molihua.hlbmerge.utils.PathTools;

import java.util.ArrayList;
import java.util.List;
/**
 * 准备合并列表  Fragment
 */
public class MainShowListFragment extends Fragment implements IRecyclerViewOnItenClickListener {
    private View view;//总布局

    private static List<ListItemMain> listItemMains;//ListItemMain实体类集合
    private RecyclerView frag_showarea_list;//listview
    private ListItemMainAdapter listItemMainAdapter;//Adapter
    public static int FLAG_PAGE=0;//页面层次--- 合集listview   or    p listview
    private IActivityAndFragment IMainActivity;//定义activity与fragment通信接口
    private MainActivity mainActivity;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        listItemMains=new ArrayList<ListItemMain>();

        try {
            CollectionlistViewShow();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
    }



    //P列表显示
    private void ChapterlistViewShow(int position) {
        listItemMains=PathTools.getChapterPaths(listItemMains,listItemMains.get(position).getPath());//向listItemMains中添加数据
        listItemMainAdapter.notifyDataSetChanged();//刷新数据
    }
    //P列表显示
    public List<ListItemMain> ChapterlistViewShow(String path) {
        listItemMains=PathTools.getChapterPaths(listItemMains,path);//向listItemMains中添加数据
        listItemMainAdapter.notifyDataSetChanged();//刷新数据
        return listItemMains;
    }

    //合集列表显示------从文件中读取数据---最全
    public List<ListItemMain> CollectionlistViewShow() {
        //从文件中读取数据---最全
        //默认路径向listItemMains中添加数据
        listItemMains=PathTools.getCollectionPaths(listItemMains,0);//自定义路径向listItemMains中添加数据


        if (listItemMainAdapter!=null){
            listItemMainAdapter.notifyDataSetChanged();//刷新数据
        }
        return listItemMains;

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null){
            view = inflater.inflate(R.layout.fragment_main_show_area_list,container,false);//加载布局文件
            getComponents();//获取组件
            setListeners();//设置监听
            initData();//初始化数据
        }
        return view;
    }


    private void initData() {
        listItemMainAdapter = new ListItemMainAdapter(listItemMains,mainActivity,this);//实例化Adapter
        frag_showarea_list.setLayoutManager(new LinearLayoutManager(mainActivity));
        frag_showarea_list.setAdapter(listItemMainAdapter);//listview设置Adapter
    }

    private void setListeners() {
        //listItem点击监听
//        frag_showarea_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                //IMainActivity.invokeFuncAiF(2);//搜索框的关闭
//
//                ListItemMain listItemMain = listItemMains.get(position);
//
//                //LogUtils.e(listItemMain.toString());
//
//                int checkBoxVisibility = listItemMain.getCheckBoxVisibility();//获取CheckBoxVisibility
//
//                switch (FLAG_PAGE){
//
//                    case 0 ://对合集进行单击监听
//
//                        switch (checkBoxVisibility){
//                            case View.VISIBLE://如果checkBox是显示的
//                                boolean checkBoxCheck = listItemMain.isCheckBoxCheck();//获取状态
//                                listItemMain.setCheckBoxCheck(!checkBoxCheck);//取反
//
//                                break;
//                            default:
//
//                                ChapterlistViewShow(position);//合集中P列表显示
//                                FLAG_PAGE=1;
//
//                                break;
//                        }
//
//                        listItemMainAdapter.notifyDataSetChanged();//刷新数据
//
//
//                        break;
//                    case 1://对p item进行单击监听
//
//                        if (listItemMain.getName().equals("返回上一级")){//返回上一级
//                            mainActivity.onBackPressed();
//                            break;
//                        }
//
//                        switch (checkBoxVisibility){
//                            case View.VISIBLE://如果checkBox是显示的
//                                boolean checkBoxCheck = listItemMain.isCheckBoxCheck();//获取状态
//                                listItemMain.setCheckBoxCheck(!checkBoxCheck);//取反
//                                listItemMainAdapter.notifyDataSetChanged();//刷新数据
//                                break;
//                            default://合并
//                                //RxFfmpeg.execStatement(listItemMain,mainActivity, RxFfmpeg.TYPE_SINGLE);
//
//                                JudgeSingleMergeDialog.showJudgeMergeDialog(listItemMain,mainActivity);
//
//                                //ProgressDialogSingle.setListItemMain(listItemMain);
//                                //ProgressDialogSingle.showHorizontalLoadingProgressDialog(mainActivity);
//
//                                break;
//                        }
//
//
//
//                        break;
//                }
//
//
//            }
//        });
//        //listItem长按监听
//        frag_showarea_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//                //MainActivity activity = (MainActivity) getActivity();//获取activity
//                //activity.showHiddenMoreChooseBtn();//显示或者隐藏多选按钮
//
//                CheckBox item_checkBox=view.findViewById(R.id.item_checkBox);
//                if (item_checkBox.getVisibility()!=View.VISIBLE){//如果是隐藏的
//                    showCheckBoxMoreChoose(true);
//                }else {
//                    showCheckBoxMoreChoose(false);
//                }
//
//                return true;
//            }
//        });
    }

    //显示或者隐藏CheckBox和MoreChoose按钮
    public void showCheckBoxMoreChoose(boolean state){
        if (state){//设置显示
            for (int i = 0; i < listItemMains.size(); i++) {
                if (!listItemMains.get(i).getName().equals("返回上一级")) {//返回上一级不设置可见
                    listItemMains.get(i).setCheckBoxVisibility(View.VISIBLE);//设置所有CheckBox可见
                    IMainActivity.invokeFuncAiF(0);//调用activity的showHiddenMoreChooseBtn(true)方法
                }
            }
        }else {
            for (int i = 0; i < listItemMains.size(); i++) {
                listItemMains.get(i).setCheckBoxVisibility(View.INVISIBLE);//设置不可见
                listItemMains.get(i).setCheckBoxCheck(false);//取消勾选
                IMainActivity.invokeFuncAiF(1);//调用activity的showHiddenMoreChooseBtn(fasle)方法
            }
        }
        listItemMainAdapter.notifyDataSetChanged();//刷新数据
    }

    private void getComponents() {
        frag_showarea_list = view.findViewById(R.id.frag_showarea_list);
    }


    //刷新Adapter数据
    public void notifyDataAdapter(){
        for (int i = 0; i < listItemMains.size(); i++) {
            LogUtils.e(listItemMains.get(i).getPath());
        }
        if (listItemMainAdapter!=null){
            listItemMainAdapter.notifyDataSetChanged();//刷新数据
        }

    }

    //获取ListItemMain实体类集合
    public List<ListItemMain> getListItemMains() {
        return listItemMains;
    }
    //设置ListItemMain实体类集合
    public void setListItemMains(List<ListItemMain> listItemMains) {
        this.listItemMains = listItemMains;
    }

    //设置ListItemMain实体类集合
    public void clearListItemMains() {
        this.listItemMains.clear();
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


    @Override
    public void onClick(int position) {
        //IMainActivity.invokeFuncAiF(2);//搜索框的关闭

        ListItemMain listItemMain = listItemMains.get(position);

        //LogUtils.e(listItemMain.toString());

        int checkBoxVisibility = listItemMain.getCheckBoxVisibility();//获取CheckBoxVisibility

        switch (FLAG_PAGE){

            case 0 ://对合集进行单击监听

                switch (checkBoxVisibility){
                    case View.VISIBLE://如果checkBox是显示的
                        boolean checkBoxCheck = listItemMain.isCheckBoxCheck();//获取状态
                        listItemMain.setCheckBoxCheck(!checkBoxCheck);//取反

                        break;
                    default:

                        ChapterlistViewShow(position);//合集中P列表显示
                        FLAG_PAGE=1;

                        break;
                }

                listItemMainAdapter.notifyDataSetChanged();//刷新数据


                break;
            case 1://对p item进行单击监听

                if (listItemMain.getName().equals("返回上一级")){//返回上一级
                    mainActivity.onBackPressed();
                    break;
                }

                switch (checkBoxVisibility){
                    case View.VISIBLE://如果checkBox是显示的
                        boolean checkBoxCheck = listItemMain.isCheckBoxCheck();//获取状态
                        listItemMain.setCheckBoxCheck(!checkBoxCheck);//取反
                        listItemMainAdapter.notifyDataSetChanged();//刷新数据
                        break;
                    default://合并
                        //RxFfmpeg.execStatement(listItemMain,mainActivity, RxFfmpeg.TYPE_SINGLE);

                        JudgeSingleMergeDialog.showJudgeMergeDialog(listItemMain,mainActivity);

                        //ProgressDialogSingle.setListItemMain(listItemMain);
                        //ProgressDialogSingle.showHorizontalLoadingProgressDialog(mainActivity);

                        break;
                }



                break;
        }
    }

    @Override
    public boolean LongClick(int position) {
        //MainActivity activity = (MainActivity) getActivity();//获取activity
        //activity.showHiddenMoreChooseBtn();//显示或者隐藏多选按钮

        CheckBox item_checkBox=view.findViewById(R.id.item_checkBox);
        if (item_checkBox.getVisibility()!=View.VISIBLE){//如果是隐藏的
            showCheckBoxMoreChoose(true);
        }else {
            showCheckBoxMoreChoose(false);
        }

        return true;
    }
}
