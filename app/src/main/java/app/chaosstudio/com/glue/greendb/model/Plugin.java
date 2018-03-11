package app.chaosstudio.com.glue.greendb.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by jsen on 2018/1/31.
 */

@Entity
public class Plugin {
    @Id
    @Generated
    private Long id;
    private String tag;
    private String filter;
    private long time;
    private String js;
    @Generated(hash = 767589366)
    public Plugin(Long id, String tag, String filter, long time, String js) {
        this.id = id;
        this.tag = tag;
        this.filter = filter;
        this.time = time;
        this.js = js;
    }
    @Generated(hash = 1719252137)
    public Plugin() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFilter() {
        return this.filter;
    }
    public void setFilter(String filter) {
        this.filter = filter;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public String getJs() {
        return this.js;
    }
    public void setJs(String js) {
        this.js = js;
    }
    public String getTag() {
        return this.tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
}
