package app.chaosstudio.com.glue

import org.junit.Test
import java.net.URI
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Created by jsen on 2018/2/3.
 */
class TestUri {
    @Test
    fun testUri00() {
        val base = URI("http://456.e.now.cn/hh/test/")
        val abs = base.resolve("//share/style.css")
        System.out.println(abs.toString())
    }
    @Test
    fun testUri0() {
        val base = URI("http://456.e.now.cn/hh/test/")
        val abs = base.resolve("/share/style.css")
        System.out.println(abs.toString())
    }
    @Test
    fun testUri1() {
        val base = URI("http://456.e.now.cn/hh/test/")
        val abs = base.resolve("../share/style.css")
        System.out.println(abs.toString())
    }
    @Test
    fun testUri2() {
        val base = URI("http://456.e.now.cn/hh/test")
        val abs = base.resolve("../share/style.css")
        System.out.println(abs.toString())
    }
    @Test
    fun testUri3() {
        val base = URI("http://456.e.now.cn/hh/test/?name=jsen#hold")
        val abs = base.resolve("../share/style.css")
        System.out.println(abs.toString())
    }
    @Test
    fun testUri4() {
        val base = URI("http://456.e.now.cn/hh/test?name=jsen#hold")
        val abs = base.resolve("../share/style.css")
        System.out.println(abs.toString())
    }

    @Test
    fun testHtml() {
        val data = "\n" +
                "<html lang=\"zh-cn\">\n" +
                "<head>\n" +
                "<meta charset=\"utf-8\"/>\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n" +
                "<title>java正则表达式获得html字符串中&lt;img src&gt;的src中的url地址 - 园芳宝贝 - 博客园</title>\n" +
                "<link type=\"text/css\" rel=\"stylesheet\" href=\"/bundles/blog-common.css?v=ON3Mxdo4-HlSMqbNDBZXhFIcGLon3eZDvU8zBESgwkk1\"/>\n" +
                "<link id=\"MainCss\" type=\"text/css\" rel=\"stylesheet\" href=\"/skins/ThinkInside/bundle-ThinkInside.css?v=RRjf6pEarGnbXZ86qxNycPfQivwSKWRa4heYLB15rVE1\"/>\n" +
                "<link id=\"mobile-style\" media=\"only screen and (max-width: 767px)\" type=\"text/css\" rel=\"stylesheet\" href=\"/skins/ThinkInside/bundle-ThinkInside-mobile.css?v=7KLBb8V0rc2yA8fbmGaHDyGb3rUQhG3DSX2DksEt9PU1\"/>\n" +
                "<link title=\"RSS\" type=\"application/rss+xml\" rel=\"alternate\" href=\"http://www.cnblogs.com/gmq-sh/rss\"/>\n" +
                "<link title=\"RSD\" type=\"application/rsd+xml\" rel=\"EditURI\" href=\"http://www.cnblogs.com/gmq-sh/rsd.xml\"/>\n" +
                "<link type=\"application/wlwmanifest+xml\" rel=\"wlwmanifest\" href=\"http://www.cnblogs.com/gmq-sh/wlwmanifest.xml\"/>\n" +
                "<script src=\"//common.cnblogs.com/scripts/jquery-2.2.0.min.js\"></script>\n" +
                "<script type=\"text/javascript\">var currentBlogApp = 'gmq-sh', cb_enable_mathjax=false;var isLogined=false;</script>\n" +
                "<script src=\"/bundles/blog-common.js?v=O-NTEmnhjbG7lSYLc3yeqkrVxfMJyY9iXf4xyjlKikw1\" type=\"text/javascript\"></script>\n" +
                "</head>\n" +
                "<body>\n" +
                "<a name=\"top\"></a>\n" +
                "<!--PageBeginHtml Block Begin-->\n" +
                "<script>\n" +
                "window.tctipConfig = {\n" +
                "staticPrefix: \"http://static.tctip.com\",\n" +
                "cssPrefix: \"http://static.tctip.com\",\n" +
                "buttonImageId: 5,\n" +
                "buttonTip: \"dashang\",\n" +
                "list:{\n" +
                "alipay: {qrimg: \"https://files.cnblogs.com/files/gmq-sh/zhifubao1.bmp\"},\n" +
                "weixin:{qrimg: \"https://files.cnblogs.com/files/gmq-sh/weixin1.bmp\"},\n" +
                "}\n" +
                "};\n" +
                "</script>\n" +
                "<script src=\"http://static.tctip.com/js/tctip.min.js\"></script>\n" +
                "<!--PageBeginHtml Block End-->\n" +
                "\n" +
                "<!--done-->\n" +
                "<div id=\"home\">\n" +
                "<div id=\"header\">\n" +
                "\t<div id=\"blogTitle\">\n" +
                "\t<a id=\"lnkBlogLogo\" href=\"http://www.cnblogs.com/gmq-sh/\"><img id=\"blogLogo\" src=\"/Skins/custom/images/logo.gif\" alt=\"返回主页\" /></a>\t\t\t\n" +
                "\t\t\n" +
                "<!--done-->\n" +
                "<h1><a id=\"Header1_HeaderTitle\" class=\"headermaintitle\" href=\"http://www.cnblogs.com/gmq-sh/\">园芳宝贝</a></h1>\n" +
                "<h2></h2>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\t\t\n" +
                "\t</div><!--end: blogTitle 博客的标题和副标题 -->\n" +
                "\t<div id=\"navigator\">\n" +
                "\t\t\n" +
                "<ul id=\"navList\">\n" +
                "<li><a id=\"blog_nav_sitehome\" class=\"menu\" href=\"http://www.cnblogs.com/\">博客园</a></li>\n" +
                "<li><a id=\"blog_nav_myhome\" class=\"menu\" href=\"http://www.cnblogs.com/gmq-sh/\">首页</a></li>\n" +
                "<li><a id=\"blog_nav_newpost\" class=\"menu\" rel=\"nofollow\" href=\"https://i.cnblogs.com/EditPosts.aspx?opt=1\">新随笔</a></li>\n" +
                "<li><a id=\"blog_nav_contact\" class=\"menu\" rel=\"nofollow\" href=\"https://msg.cnblogs.com/send/%E5%9B%AD%E8%8A%B3%E5%AE%9D%E8%B4%9D\">联系</a></li>\n" +
                "<li><a id=\"blog_nav_rss\" class=\"menu\" href=\"http://www.cnblogs.com/gmq-sh/rss\">订阅</a>\n" +
                "<!--<a id=\"blog_nav_rss_image\" class=\"aHeaderXML\" href=\"http://www.cnblogs.com/gmq-sh/rss\"><img src=\"//www.cnblogs.com/images/xml.gif\" alt=\"订阅\" /></a>--></li>\n" +
                "<li><a id=\"blog_nav_admin\" class=\"menu\" rel=\"nofollow\" href=\"https://i.cnblogs.com/\">管理</a></li>\n" +
                "</ul>\n" +
                "\t\t<div class=\"blogStats\">\n" +
                "\t\t\t\n" +
                "\t\t\t<div id=\"blog_stats\">\n" +
                "<span id=\"stats_post_count\">随笔 - 267&nbsp; </span>\n" +
                "<span id=\"stats_article_count\">文章 - 15&nbsp; </span>\n" +
                "<span id=\"stats-comment_count\">评论 - 31</span>\n" +
                "</div>\n" +
                "\t\t\t\n" +
                "\t\t</div><!--end: blogStats -->\n" +
                "\t</div><!--end: navigator 博客导航栏 -->\n" +
                "</div><!--end: header 头部 -->\n" +
                "\n" +
                "<div id=\"main\">\n" +
                "\t<div id=\"mainContent\">\n" +
                "\t<div class=\"forFlow\">\n" +
                "\t\t\n" +
                "<div id=\"post_detail\">\n" +
                "<!--done-->\n" +
                "<div id=\"topics\">\n" +
                "\t<div class = \"post\">\n" +
                "\t\t<h1 class = \"postTitle\">\n" +
                "\t\t\t<a id=\"cb_post_title_url\" class=\"postTitle2\" href=\"http://www.cnblogs.com/gmq-sh/p/5820937.html\">java正则表达式获得html字符串中&lt;img src&gt;的src中的url地址</a>\n" +
                "\t\t</h1>\n" +
                "\t\t<div class=\"clear\"></div>\n" +
                "\t\t<div class=\"postBody\">\n" +
                "\t\t\t<div id=\"cnblogs_post_body\" class=\"blogpost-body\"><div class=\"cnblogs_code\">\n" +
                "<pre><span style=\"color: #008000;\">/**</span><span style=\"color: #008000;\">\n" +
                "     * 得到网页中图片的地址\n" +
                "     </span><span style=\"color: #008000;\">*/</span>\n" +
                "    <span style=\"color: #0000ff;\">public</span> <span style=\"color: #0000ff;\">static</span> Set&lt;String&gt;<span style=\"color: #000000;\"> getImgStr(String htmlStr) {\n" +
                "        Set</span>&lt;String&gt; pics = <span style=\"color: #0000ff;\">new</span> HashSet&lt;&gt;<span style=\"color: #000000;\">();\n" +
                "        String img </span>= \"\"<span style=\"color: #000000;\">;\n" +
                "        Pattern p_image;\n" +
                "        Matcher m_image;\n" +
                "        </span><span style=\"color: #008000;\">//</span><span style=\"color: #008000;\">     String regEx_img = \"&lt;img.*src=(.*?)[^&gt;]*?&gt;\"; </span><span style=\"color: #008000;\">//</span><span style=\"color: #008000;\">图片链接地址</span>\n" +
                "        String regEx_img = \"&lt;img.*src\\\\s*=\\\\s*(.*?)[^&gt;]*?&gt;\"<span style=\"color: #000000;\">;\n" +
                "        p_image </span>=<span style=\"color: #000000;\"> Pattern.compile\n" +
                "                (regEx_img, Pattern.CASE_INSENSITIVE);\n" +
                "        m_image </span>=<span style=\"color: #000000;\"> p_image.matcher(htmlStr);\n" +
                "        </span><span style=\"color: #0000ff;\">while</span><span style=\"color: #000000;\"> (m_image.find()) {\n" +
                "            </span><span style=\"color: #008000;\">//</span><span style=\"color: #008000;\"> 得到&lt;img /&gt;数据</span>\n" +
                "            img =<span style=\"color: #000000;\"> m_image.group();\n" +
                "            </span><span style=\"color: #008000;\">//</span><span style=\"color: #008000;\"> 匹配&lt;img&gt;中的src数据</span>\n" +
                "            Matcher m = Pattern.compile(\"src\\\\s*=\\\\s*\\\"?(.*?)(\\\"|&gt;|\\\\s+)\"<span style=\"color: #000000;\">).matcher(img);\n" +
                "            </span><span style=\"color: #0000ff;\">while</span><span style=\"color: #000000;\"> (m.find()) {\n" +
                "                pics.add(m.group(</span>1<span style=\"color: #000000;\">));\n" +
                "            }\n" +
                "        }\n" +
                "        </span><span style=\"color: #0000ff;\">return</span><span style=\"color: #000000;\"> pics;\n" +
                "    }</span></pre>\n" +
                "</div>\n" +
                "<p>&nbsp;</p></div><div id=\"MySignature\"></div>\n" +
                "<div class=\"clear\"></div>\n" +
                "<div id=\"blog_post_info_block\">\n" +
                "<div id=\"BlogPostCategory\"></div>\n" +
                "<div id=\"EntryTag\"></div>\n" +
                "<div id=\"blog_post_info\">\n" +
                "</div>\n" +
                "<div class=\"clear\"></div>\n" +
                "<div id=\"post_next_prev\"></div>\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "\t\t</div>\n" +
                "\t\t<div class = \"postDesc\">posted @ <span id=\"post-date\">2016-08-30 10:29</span> <a href='http://www.cnblogs.com/gmq-sh/'>园芳宝贝</a> 阅读(<span id=\"post_view_count\">...</span>) 评论(<span id=\"post_comment_count\">...</span>)  <a href =\"https://i.cnblogs.com/EditPosts.aspx?postid=5820937\" rel=\"nofollow\">编辑</a> <a href=\"#\" onclick=\"AddToWz(5820937);return false;\">收藏</a></div>\n" +
                "\t</div>\n" +
                "\t<script type=\"text/javascript\">var allowComments=true,cb_blogId=214469,cb_entryId=5820937,cb_blogApp=currentBlogApp,cb_blogUserGuid='809d5e1d-6c79-e411-b908-9dcfd8948a71',cb_entryCreatedDate='2016/8/30 10:29:00';loadViewCount(cb_entryId);var cb_postType=1;</script>\n" +
                "\t\n" +
                "</div><!--end: topics 文章、评论容器-->\n" +
                "</div><a name=\"!comments\"></a><div id=\"blog-comments-placeholder\"></div><script type=\"text/javascript\">var commentManager = new blogCommentManager();commentManager.renderComments(0);</script>\n" +
                "<div id='comment_form' class='commentform'>\n" +
                "<a name='commentform'></a>\n" +
                "<div id='divCommentShow'></div>\n" +
                "<div id='comment_nav'><span id='span_refresh_tips'></span><a href='javascript:void(0);' onclick='return RefreshCommentList();' id='lnk_RefreshComments' runat='server' clientidmode='Static'>刷新评论</a><a href='#' onclick='return RefreshPage();'>刷新页面</a><a href='#top'>返回顶部</a></div>\n" +
                "<div id='comment_form_container'></div>\n" +
                "<div class='ad_text_commentbox' id='ad_text_under_commentbox'></div>\n" +
                "<div id='ad_t2'></div>\n" +
                "<div id='opt_under_post'></div>\n" +
                "<div id='cnblogs_c1' class='c_ad_block'></div>\n" +
                "<div id='under_post_news'></div>\n" +
                "<div id='cnblogs_c2' class='c_ad_block'></div>\n" +
                "<div id='under_post_kb'></div>\n" +
                "<div id='HistoryToday' class='c_ad_block'></div>\n" +
                "<script type='text/javascript'>\n" +
                "    fixPostBody();\n" +
                "    setTimeout(function () { incrementViewCount(cb_entryId); }, 50);\n" +
                "    deliverAdT2();\n" +
                "    deliverAdC1();\n" +
                "    deliverAdC2();    \n" +
                "    loadNewsAndKb();\n" +
                "    loadBlogSignature();\n" +
                "    LoadPostInfoBlock(cb_blogId, cb_entryId, cb_blogApp, cb_blogUserGuid);\n" +
                "    GetPrevNextPost(cb_entryId, cb_blogId, cb_entryCreatedDate, cb_postType);\n" +
                "    loadOptUnderPost();\n" +
                "    GetHistoryToday(cb_blogId, cb_blogApp, cb_entryCreatedDate);   \n" +
                "</script>\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "\t</div><!--end: forFlow -->\n" +
                "\t</div><!--end: mainContent 主体内容容器-->\n" +
                "\n" +
                "\t<div id=\"sideBar\">\n" +
                "\t\t<div id=\"sideBarMain\">\n" +
                "\t\t\t\n" +
                "<!--done-->\n" +
                "<div class=\"newsItem\">\n" +
                "<h3 class=\"catListTitle\">公告</h3>\n" +
                "\t<div id=\"blog-news\"></div><script type=\"text/javascript\">loadBlogNews();</script>\n" +
                "</div>\n" +
                "\n" +
                "\t\t\t<div id=\"blog-calendar\" style=\"display:none\"></div><script type=\"text/javascript\">loadBlogDefaultCalendar();</script>\n" +
                "\t\t\t\n" +
                "\t\t\t<div id=\"leftcontentcontainer\">\n" +
                "\t\t\t\t<div id=\"blog-sidecolumn\"></div><script type=\"text/javascript\">loadBlogSideColumn();</script>\n" +
                "\t\t\t</div>\n" +
                "\t\t\t\n" +
                "\t\t</div><!--end: sideBarMain -->\n" +
                "\t</div><!--end: sideBar 侧边栏容器 -->\n" +
                "\t<div class=\"clear\"></div>\n" +
                "\t</div><!--end: main -->\n" +
                "\t<div class=\"clear\"></div>\n" +
                "\t<div id=\"footer\">\n" +
                "\t\t\n" +
                "<!--done-->\n" +
                "Copyright &copy;2018 园芳宝贝\n" +
                "\t</div><!--end: footer -->\n" +
                "</div><!--end: home 自定义的最大容器 -->\n" +
                "</body>\n" +
                "</html>\n" +
                "\n"

        val result = getImgStr(data)
        for (item in result) {
            System.out.println(item)
        }
    }

    /**
     * 得到网页中图片的地址
     */
    fun getImgStr(htmlStr: String): Set<String> {
        val pics = HashSet<String>()
        var img = ""
        val p_image: Pattern
        val m_image: Matcher
        //     String regEx_img = "<img.*src=(.*?)[^>]*?>"; //图片链接地址
        val regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>"
        p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE)
        m_image = p_image.matcher(htmlStr)
        while (m_image.find()) {
            // 得到<img />数据
            img = m_image.group()
            // 匹配<img>中的src数据
            val m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img)
            while (m.find()) {
                pics.add(m.group(1))
            }
        }
        return pics
    }
}