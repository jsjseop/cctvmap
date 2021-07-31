package kr.ac.kumoh.s20140716.cctvmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import kr.ac.kumoh.s20140716.cctvmap.AutoCompleteParse;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import kr.ac.kumoh.s20140716.cctvmap.R;
import kr.ac.kumoh.s20140716.cctvmap.RecyclerViewAdapterCallback;
import kr.ac.kumoh.s20140716.cctvmap.SearchEntity;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public interface onItemClickListener
    {
        void onItemClick(View v, int position);
    }

    private onItemClickListener mlistener = null;

    public void setOnItemClickListener(RecyclerViewAdapter.onItemClickListener listener)
    {
        this.mlistener = listener;
    }

    private ArrayList<SearchEntity> itemLists = new ArrayList<>();
    private RecyclerViewAdapterCallback callback;
    private double lat, longi;
    private boolean flag = true;

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView address;

        public CustomViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            address = (TextView) itemView.findViewById(R.id.tv_address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(mlistener != null)
                        {
                            mlistener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int ItemPosition = position;

        if( holder instanceof CustomViewHolder) {
            CustomViewHolder viewHolder = (CustomViewHolder)holder;

            SearchEntity searchEntity = itemLists.get(position);

            viewHolder.title.setText(searchEntity.getTitle());
            viewHolder.address.setText(searchEntity.getAddress());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.Transportedit(searchEntity.getAddress() + searchEntity.getTitle());
                    setClear();
                    flag = false;
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return itemLists.size();
    }

    public void setData(ArrayList<SearchEntity> itemLists) {
        this.itemLists = itemLists;
    }

    public void setCallback(RecyclerViewAdapterCallback callback) {
        this.callback = callback;
    }

    public void setLocation(double lat, double longi)
    {
        this.lat = lat;
        this.longi = longi;
    }

    public void filter(String keyword) {
        if (keyword.length() >= 2 && flag) {
            try {
                AutoCompleteParse parser = new AutoCompleteParse(this, lat, longi);
                itemLists.addAll(parser.execute(keyword).get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        else if(keyword.length() < 2 && !flag)
        {
            flag = true;
        }
        try {
            AutoCompleteParse parser = new AutoCompleteParse(this, lat, longi);
            itemLists.addAll(parser.execute(keyword).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void setClear(){
        itemLists.clear();
        notifyDataSetChanged();
    }
}