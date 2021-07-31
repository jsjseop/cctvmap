package kr.ac.kumoh.s20140716.cctvmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<History> historyList = new ArrayList<>();


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView Recent_routes;
        TextView search_date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Recent_routes = itemView.findViewById(R.id.recent_routes);
            search_date = itemView.findViewById(R.id.search_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                    {
                        History his = historyList.get(pos);
                        mListener.onItemClick(view, his);
                    }
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.history_item, parent, false) ;
        HistoryAdapter.ViewHolder vh = new HistoryAdapter.ViewHolder(view) ;

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        History current_history = historyList.get(position);
        holder.Recent_routes.setText(current_history.getStartAddress() + "->" + current_history.getEndAddress());
        holder.search_date.setText(current_history.getDate());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void setHistoryList(List<History> histories) {
        this.historyList = histories;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener
    {
        void onItemClick(View v, History his);
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;

    // OnItemClickListener 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mListener = listener;
    }
}
