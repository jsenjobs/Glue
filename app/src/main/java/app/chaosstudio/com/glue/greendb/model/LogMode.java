package app.chaosstudio.com.glue.greendb.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by jsen on 2018/1/28.
 */

@Entity
public class LogMode {
    @Id
    @Generated
    private Long id;
    private String url;
    private long timestamp;
    private boolean loaded = true;
    private String uuid;
    @Generated(hash = 1978356519)
    public LogMode(Long id, String url, long timestamp, boolean loaded,
            String uuid) {
        this.id = id;
        this.url = url;
        this.timestamp = timestamp;
        this.loaded = loaded;
        this.uuid = uuid;
    }
    @Generated(hash = 446357150)
    public LogMode() {
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
    public long getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getUuid() {
        return this.uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public boolean getLoaded() {
        return this.loaded;
    }
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
