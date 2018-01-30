package app.chaosstudio.com.glue.greendb.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by jsen on 2018/1/23.
 */

@Entity
public class History {
    @Id
    @Generated
    private Long id;

    private String name;
    private String url;
    private long date;
    @Generated(hash = 1934541277)
    public History(Long id, String name, String url, long date) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.date = date;
    }
    @Generated(hash = 869423138)
    public History() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
}
