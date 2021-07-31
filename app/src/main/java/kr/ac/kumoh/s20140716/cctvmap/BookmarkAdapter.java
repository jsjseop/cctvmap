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

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {
    private List<Bookmark> bookmarkList = new ArrayList<>();


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookmark_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookmark_name = itemView.findViewById(R.id.bookmark_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                    {
                        Bookmark book = bookmarkList.get(pos);
                        mListener.onItemClick(view, book);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                    {
                        Bookmark book = bookmarkList.get(pos);
                        mLongListener.onItemLongClick(v, book);
                    }
                    return true;
                }
            });

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.bookmark_item, parent, false) ;
        ViewHolder vh = new ViewHolder(view) ;

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bookmark current_bookmark = bookmarkList.get(position);
        holder.bookmark_name.setText(current_bookmark.getBookmark());
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    public void setBookmarkList(List<Bookmark> bookmarks) {
        this.bookmarkList = bookmarks;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener
    {
        void onItemClick(View v, Bookmark his);
    }

    public interface OnItemLongClickListener
    {
        void onItemLongClick(View v, Bookmark book);
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;
    private OnItemLongClickListener mLongListener = null;


    // OnItemClickListener 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener)
    {
        this.mLongListener = listener;
    }

}