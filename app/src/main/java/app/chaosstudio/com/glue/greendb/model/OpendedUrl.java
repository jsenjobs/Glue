package app.chaosstudio.com.glue.greendb.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by jsen on 2018/1/27.
 */
@Entity
public class OpendedUrl {
    @Id
    @Generated
    private Long id;
    private String domain;
    private String uuid;
    @Generated(hash = 1276485000)
    public OpendedUrl(Long id, String domain, String uuid) {
        this.id = id;
        this.domain = domain;
        this.uuid = uuid;
    }
    @Generated(hash = 280289142)
    public OpendedUrl() {
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
    public String getUuid() {
        return this.uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
