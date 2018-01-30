package app.chaosstudio.com.glue.greendb.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by jsen on 2018/1/25.
 */
@Entity
public class BlackUrl {
    @Id
    @Generated
    private Long id;
    private String domain;
    private String tag;
    @Generated(hash = 1798781370)
    public BlackUrl(Long id, String domain, String tag) {
        this.id = id;
        this.domain = domain;
        this.tag = tag;
    }
    @Generated(hash = 1429734378)
    public BlackUrl() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDomain() {
        return this.domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }
    public String getTag() {
        return this.tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
}
