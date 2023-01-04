package com.molihua.hlbmerge.interfaces;
/**
 * RecyclerView的item点击长按接口
 */
public interface IRecyclerViewOnItenClickListener {
    void onClick( int position);
    boolean LongClick( int position );
}
