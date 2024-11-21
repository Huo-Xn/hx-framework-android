package wendu.dsbridge.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * ┌───────────────────────────────────┐
 * │作者:jky
 * │时间:2019-07-24 8:17
 * └───────────────────────────────────┘
 * 说明:BaseRecyclerViewAdapter
 * 需要导入  implementation 'com.android.support:recyclerview-v7:28.0.0'
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ArrayList<T> mList;      //创建集合 泛型是 传进来的

    private int mLayout = 0;             //布局文件

    private View mView = null;

    public ItemClick<T> mItemClick;     //单点接口对象

    public ItemLongClick<T> mItemLongClick;     //单点接口对象

    /**
     * @param mLayout 传入布局
     */
    public BaseRecyclerViewAdapter(int mLayout) {
        this.mLayout = mLayout;
        mList = new ArrayList<>();
    }


    public BaseRecyclerViewAdapter(View mView) {
        this.mView = mView;
        mList = new ArrayList<>();
    }

    /**
     * @param mItemLongClick 注册setmItemLongClick
     */
    public void setItemLongClick(ItemLongClick<T> mItemLongClick) {
        this.mItemLongClick = mItemLongClick;
    }

    /**
     * @param mItemClick 注册setItemClick
     */
    public void setItemClick(ItemClick<T> mItemClick) {
        this.mItemClick = mItemClick;
    }


    /**
     * @return 获取集合
     */
    public ArrayList<T> getList() {
        return mList;
    }


    /**
     * @param position 指定位置
     * @return 获取条目内容
     */
    public T getItem(int position) {
        if (position > -1 && position <= mList.size() - 1) {
            return mList.get(position);
        }
        return null;
    }


    /**
     * @param list 添加数据 覆盖
     */
    public BaseRecyclerViewAdapter setList(List<T> list) {
        if (list != null) {
            if (this.mList.size() > 0 && this.mList != null) {
                this.mList.clear();
            }
            this.mList.addAll(list);
            notifyDataSetChanged();
        }
        return this;
    }


    /**
     * @param data 在最后增加一条数据
     */
    public void append(T data) {
        if (data != null) {
            addData(mList.size(), data);
        }
    }


    /**
     * @param data 在头部增加一条数据
     */
    public void insertHead(T data) {
        if (data != null) {
            addData(0, data);
        }
    }


    /**
     * @param list 在最后增加N条数据
     */
    public void addList(List list) {
        if (list != null && list.size() > 0) {
            mList.addAll(mList.size() - 1, list);
            notifyItemInserted(this.mList.size() - list.size() - 1);
        }
    }


    /**
     * @param position 指定位置
     * @param data     在指定位置增加一条数据
     */
    public void addData(int position, T data) {
        if (position > -1 && data != null) {
            mList.add(position, data);
            notifyItemRangeInserted(position, 1);
        }
    }


    /**
     * @param position 指定位置
     * @param list     在指定位置增加N条数据
     */
    public void addList(int position, List list) {
        if (position > -1 && position <= mList.size() - 1 && list.size() > 0 && list != null) {
            mList.addAll(position, list);
            notifyItemRangeInserted(position, mList.size());
        }

    }


    /**
     * @param list 删除N条数据
     */
    public void removeList(List list) {
        if (list.size() > 0 && list != null) {
            mList.removeAll(list);
            notifyItemRemoved(list.size());
        }
    }


    /**
     * @param data 删除某条数据
     */
    public void remove(T data) {
        if (data != null) {
            mList.remove(data);
        }
    }


    /**
     * @param position 删除指定位置数据
     */
    public void remove(int position) {
        if (position > -1 && position <= mList.size() - 1) {
            mList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mList.size());
        }
    }


    /**
     * @param position 指定位置
     * @param data     替换指定位置数据
     */
    public void replace(int position, T data) {
        if (position > -1 && position <= mList.size() - 1) {
            mList.set(position, data);
            notifyItemChanged(position);
        }
    }


    /**
     * 清空数据
     */
    public void clearList() {
        if (mList.size() > 0) {
            mList.clear();
        }
        notifyDataSetChanged();
    }


    /**
     * @param itemView 子布局
     * @param position 指定条目
     * @param mList    集合
     */
    public abstract void onBindChildViewHolder(View itemView, int position, ArrayList<T> mList);


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        if (mLayout != 0 && mView == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(mLayout, viewGroup, false);
        } else {
            view = mView;
        }

        return new Holder(view, i);
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        /*
        获取数据
         */
        T mItemData = getItem(position);
        Holder h = (Holder) viewHolder;
        h.setData(h.itemView, position, mList);

    }


    @Override
    public int getItemCount() {
        if (mList.size() > 0 && mList != null) {
            return mList.size();
        }
        return 0;
    }


    private class Holder extends RecyclerView.ViewHolder {
        public Holder(View view, final int i) {
            super(view);
        }

        public void setData(View itemView, final int position, final ArrayList<T> mList) {


            onBindChildViewHolder(itemView, position, mList);

            /**
             * 单点
             */
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClick != null) {
                        mItemClick.setOnItemClick(v, position, mList);
                    }
                }
            });


            /**
             * 长按
             */
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mItemLongClick != null) {
                        mItemLongClick.setOnItemLongClick(v, position, mList);
                    }
                    return true;
                }
            });
        }

    }


    //条目单点
    public interface ItemClick<T> {
        void setOnItemClick(View v, int position, ArrayList<T> mList);
    }


    //条目长按
    public interface ItemLongClick<T> {
        void setOnItemLongClick(View v, int position, ArrayList<T> mList);
    }

}





