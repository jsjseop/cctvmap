package kr.ac.kumoh.s20140716.cctvmap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.List;

public class BookmarkActivity extends AppCompatActivity {

    private BookmarkViewModel bookmarkViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);


        RecyclerView recyclerView = findViewById(R.id.bookmark_recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        // 리스트를 역순으로 출력하기 위해 LayoutManager 설정
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        BookmarkAdapter adapter = new BookmarkAdapter();
        recyclerView.setAdapter(adapter);

        bookmarkViewModel = ViewModelProviders.of(this).get(BookmarkViewModel.class);
        bookmarkViewModel.getBookmarkList().observe(this, new Observer<List<Bookmark>>() {
            @Override
            public void onChanged(List<Bookmark> bookmarks) {
                adapter.setBookmarkList(bookmarks);
            }
        });

        adapter.setOnItemClickListener(new BookmarkAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Bookmark book) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("search_bookmark", book);
                setResult(2,intent);
                finish();
            }
        });

        adapter.setOnItemLongClickListener(new BookmarkAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View v, Bookmark book) {
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(BookmarkActivity.this);
                alert_confirm.setMessage("삭제 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BookmarkDatabase db = BookmarkDatabase.getINSTANCE(getApplication());
                                db.bookmarkDao().delete(book);
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });

    }
}
