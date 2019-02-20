package com.hel.guessword.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hel.guessword.R;

import java.util.List;
import java.util.Map;

/**
 * @author hel
 * @date 2018/2/22
 * 文件 GuessWord
 * 描述
 */

public class MyLevelAdapter extends RecyclerView.Adapter<MyLevelAdapter.MyViewHolder> {

    private List<Map<String, String>> mDatas;
    private Context mContext;
    private LayoutInflater inflater;
    private OnItemClickListener mOnItemClickListener;

    public MyLevelAdapter(Context context, List<Map<String, String>> datas) {
        this.mContext = context;
        this.mDatas = datas;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {

        return mDatas.size();
    }

    //填充onCreateViewHolder方法返回的holder中的控件
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tv.setText(mDatas.get(position).get("sub_level_no"));
        if (mDatas.get(position).get("ispass").equals("0")) {
            if (mDatas.get(position).get("level_no").equals("1") && mDatas.get(position).get("sub_level_no").equals("1")) {
                holder.tv.setTextColor(Color.parseColor("#FF6600"));
                holder.lock.setVisibility(View.GONE);
            } else {
                holder.tv.setTextColor(Color.TRANSPARENT);
                holder.lock.setVisibility(View.VISIBLE);

            }
        } else {
            holder.tv.setTextColor(Color.parseColor("#FF6600"));
            holder.lock.setVisibility(View.GONE);
        }
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onLongClick(position);
                    return false;
                }
            });
        }
    }

    //重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_home, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    public interface OnItemClickListener {
        void onClick(int position);

        void onLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;
        ImageView lock;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.tv_item);
            lock = view.findViewById(R.id.lock);
        }

    }
}
