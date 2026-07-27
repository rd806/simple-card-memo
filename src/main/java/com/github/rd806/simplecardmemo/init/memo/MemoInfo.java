package com.github.rd806.simplecardmemo.init.memo;

public class MemoInfo {
    private String name;
    private String path;
    private long lastModified;

    public String getMemoName() { return name; }
    public void setMemoName(String name) { this.name = name; }

    public String getMemoPath() { return path; }
    public void setMemoPath(String path) { this.path = path; }

    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
}

