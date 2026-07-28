package com.github.rd806.simplecardmemo.memo;

public class MemoInfo {
    private String name;
    private String path;
    private String author;
    private boolean isLocalFile;
    private long lastModified;

    public MemoInfo(String name, String path, String author, boolean isLocalFile, long lastModified) {
        this.name = name;
        this.path = path;
        this.author = author;
        this.isLocalFile = isLocalFile;
        this.lastModified = lastModified;
    }

    public MemoInfo() {}

    public String getMemoName() { return name; }
    public void setMemoName(String name) { this.name = name; }

    public String getMemoPath() { return path; }
    public void setMemoPath(String path) { this.path = path; }

    public String getMemoAuthor() { return author; }
    public void setMemoAuthor(String author) { this.author = author; }

    public boolean isLocalFile() { return isLocalFile; }
    public void setLocalFile(boolean localFile) { this.isLocalFile = localFile; }

    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
}

