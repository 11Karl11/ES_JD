package com.karl.esjd.utils;

import com.karl.esjd.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author karl xie
 * Created on 2021-01-09 14:31
 */
public class HtmlParseUtil {


    public static List<Content> parseJD(String keywords) throws Exception {

        //获取请求  https://search.jd.com/Search?keyword=java
        //前提需要联网，ajax用不了
        String url = "https://search.jd.com/Search?keyword=" + keywords;

        //解析网页(Jsoup 返回的doc就是浏览器的document对象)
        Document document = Jsoup.parse(new URL(url), 30000);
        //所有你在js中可以使用的方法这里都可以用
        Element element = document.getElementById("J_goodsList");
        //获取所有的li元素
        Elements elements = element.getElementsByTag("li");
        ArrayList<Content> contents = new ArrayList<>();
        for (Element el : elements) {
            //关于这种图片，特别多的网址图片是延迟加载的
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            contents.add(Content.builder().img(img).price(price).title(title).build());
        }

        return contents;
    }
}