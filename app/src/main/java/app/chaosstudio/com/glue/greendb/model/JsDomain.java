package app.chaosstudio.com.glue.greendb.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by jsen on 2018/1/22.
 */
@Entity
public class JsDomain {
    @Id
    @Generated
    private Long id;
    private String domain;
    @Generated(hash = 1280260287)
    public JsDomain(Long id, String domain) {
        this.id = id;
        this.domain = domain;
    }
    @Generated(hash = 1425296150)
    public JsDomain() {
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
}
