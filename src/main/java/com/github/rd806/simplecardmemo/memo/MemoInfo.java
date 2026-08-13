package com.github.rd806.simplecardmemo.memo;

public class MemoInfo {
    private String name;
    private String path;
    private String author;
    private boolean isExternal;
    private long lastModified;

    public MemoInfo(String name, String path, String author, boolean isExternal, long lastModified) {
        this.name = name;
        this.path = path;
        this.author = author;
        this.isExternal = isExternal;
        this.lastModified = lastModified;
    }

    public MemoInfo() {}

    public String getMemoName() { return name; }
    public void setMemoName(String name) { this.name = name; }

    public String getMemoPath() { return path; }
    public void setMemoPath(String path) { this.path = path; }

    public String getMemoAuthor() { return author; }
    public void setMemoAuthor(String author) { this.author = author; }

    public boolean isExternal() { return isExternal; }
    public void setExternal(boolean external) { this.isExternal = external; }

    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
}
