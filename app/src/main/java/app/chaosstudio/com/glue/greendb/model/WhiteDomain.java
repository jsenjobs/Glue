package app.chaosstudio.com.glue.greendb.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by jsen on 2018/1/21.
 */
@Entity
public class WhiteDomain {
    @Id
    @Generated
    private Long id;
    private String domain;
    private String tag;
    @Generated(hash = 1935696506)
    public WhiteDomain(Long id, String domain, String tag) {
        this.id = id;
        this.domain = domain;
        this.tag = tag;
    }
    @Generated(hash = 970798362)
    public WhiteDomain() {
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
