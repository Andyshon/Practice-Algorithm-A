package com.andyshon.practice;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.andyshon.practice.enums.Mode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Andy Shkatula andyshon.shkatula@gmail.com
 * @since 03.03.2021
 */
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private final List<Item> mData = new ArrayList<>();
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    int startPointPos=-1, endPointPos=-1;
    Set<Integer> blocksPos = new HashSet<>();

    Adapter(Context context) {
        initData();
        this.mInflater = LayoutInflater.from(context);
    }

    public void initData() {
        for (int i=1; i<=400; i++) {
            mData.add(new Item(i));
        }
        notifyDataSetChanged();
    }

    public void markAsBlock(int pos) {
        Item item = mData.get(pos);
        item.type = item.type == Mode.NONE ? Mode.BLOCK : Mode.NONE;
        notifyItemChanged(pos);

        if (item.type == Mode.BLOCK)
            blocksPos.add(pos);
        else
            blocksPos.remove(pos);
        printBlocks();
    }

    // debug purpose
    private void printBlocks() {
        for (Integer blocksPo : blocksPos) {
            Log.e("Adapter", String.valueOf(blocksPo));
        }
    }

    public void marAsStartPoint(int pos) {
        if (startPointPos != -1) {
            mData.get(startPointPos).type = Mode.NONE;
            notifyItemChanged(startPointPos);
        }

        startPointPos = pos;

        mData.get(startPointPos).type = Mode.START_POINT;
        notifyItemChanged(startPointPos);
    }

    public void marAsEndPoint(int pos) {
        if (endPointPos != -1) {
            mData.get(endPointPos).type = Mode.NONE;
            notifyItemChanged(endPointPos);
        }

        endPointPos = pos;

        mData.get(endPointPos).type = Mode.END_POINT;
        notifyItemChanged(endPointPos);
    }

    public void drawPath(Set<Integer> steps) {
        for (Integer step : steps) {
            if (step < getItemCount()) {
                mData.get(step).type = Mode.PATH;
                notifyItemChanged(step);
            }
        }
    }

    public void reset() {
        mData.clear();
        startPointPos = -1;
        endPointPos = -1;
        initData();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_table, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = mData.get(position);
        if (item.type == Mode.BLOCK)
            holder.mView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
        else if (item.type == Mode.START_POINT)
            holder.mView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.yellow));
        else if (item.type == Mode.END_POINT)
            holder.mView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.teal_200));
        else if (item.type == Mode.PATH)
            holder.mView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
        else
            holder.mView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.purple_200));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View mView;

        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView.findViewById(R.id.item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
