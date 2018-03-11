package app.chaosstudio.com.glue.greendb.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by jsen on 2018/2/3.
 */

@Entity
public class PlayList {
    @Id
    @Generated
    private Long id;
    private String url;
    private long time;
    @Generated(hash = 1739366930)
    public PlayList(Long id, String url, long time) {
        this.id = id;
        this.url = url;
        this.time = time;
    }
    @Generated(hash = 438209239)
    public PlayList() {
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
}
