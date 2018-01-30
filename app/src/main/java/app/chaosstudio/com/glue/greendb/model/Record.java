package app.chaosstudio.com.glue.greendb.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by jsen on 2018/1/23.
 */
@Entity
public class Record {
    @Id
    @Generated
    private Long id;
    private String title;
    private String url;
    private long time;
    @Generated(hash = 1367524247)
    public Record(Long id, String title, String url, long time) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.time = time;
    }
    @Generated(hash = 477726293)
    public Record() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
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
