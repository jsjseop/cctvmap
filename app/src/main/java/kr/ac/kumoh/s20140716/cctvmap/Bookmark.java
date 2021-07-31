package kr.ac.kumoh.s20140716.cctvmap;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Bookmark implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int BookmarkID;
    private String bookmark;

    public Bookmark(String bookmark) {
        this.bookmark = bookmark;
    }

    public int getBookmarkID() {
        return BookmarkID;
    }

    public void setBookmarkID(int bookmarkID) {
        BookmarkID = bookmarkID;
    }

    public String getBookmark() {
        return bookmark;
    }

    public void setBookmark(String bookmark) {
        this.bookmark = bookmark;
    }
}
