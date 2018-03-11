package app.chaosstudio.com.glue.greendb.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by jsen on 2018/2/4.
 */

@Entity
public class PageSource {
    @Id
    @Generated
    private Long id;
    private String source;
    private String url;
    @Generated(hash = 1962441962)
    public PageSource(Long id, String source, String url) {
        this.id = id;
        this.source = source;
        this.url = url;
    }
    @Generated(hash = 2062840492)
    public PageSource() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getSource() {
        return this.source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
