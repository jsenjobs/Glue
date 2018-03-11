package app.chaosstudio.com.glue.greendb.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by jsen on 2018/2/1.
 */

@Entity
public class DownloadMode {
    @Id
    @Generated
    private Long id;
    private String url;
    private long time;
    private long size;
    private boolean isFinished = false;

    private String path;
    @Generated(hash = 1777550836)
    public DownloadMode(Long id, String url, long time, long size,
            boolean isFinished, String path) {
        this.id = id;
        this.url = url;
        this.time = time;
        this.size = size;
        this.isFinished = isFinished;
        this.path = path;
    }
    @Generated(hash = 48096091)
    public DownloadMode() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public long getSize() {
        return this.size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public boolean getIsFinished() {
        return this.isFinished;
    }
    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
}
