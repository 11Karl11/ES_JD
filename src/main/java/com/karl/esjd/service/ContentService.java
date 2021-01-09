package com.karl.esjd.service;

import com.alibaba.fastjson.JSON;
import com.karl.esjd.pojo.Content;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.karl.esjd.utils.HtmlParseUtil.parseJD;

/**
 * @author karl xie
 * Created on 2021-01-09 14:51
 */
@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    //解析数据放入ES
    public Boolean parseContent(String keywords) throws Exception {

        List<Content> contents = parseJD(keywords);

        //把查询到的数据放入es中

        BulkRequest bulkRequest = new BulkRequest();

        bulkRequest.timeout("2m");
        for (int i = 0; i < contents.size(); i++) {
            bulkRequest.add(new IndexRequest("jd_goods")
                    .source(JSON.toJSONString(contents.get(i)), XContentType.JSON));
        }

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }


    //获取这些数据实现搜索功能
    public List<Map<String, Object>> searchPage(String keywords, int pageNo, int pageSize) throws IOException {
        if (pageNo <= 1) {
            pageNo = 1;
        }

        //条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();


        //分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);

        //精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keywords);
        sourceBuilder.query(termQueryBuilder);

        //执行搜索
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);


        ArrayList<Map<String, Object>> list = new ArrayList<>();
        //解析结果
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            list.add(hit.getSourceAsMap());
        }

        return list;
    }




    //获取这些数据实现搜索功能,涵盖高亮
    public List<Map<String, Object>> searchPageHigh(String keywords, int pageNo, int pageSize) throws IOException {
        if (pageNo <= 1) {
            pageNo = 1;
        }

        //条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();


        //分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);

        //精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keywords);
        sourceBuilder.query(termQueryBuilder);

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.requireFieldMatch(false);//多个显示高亮
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        //执行搜索
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);


        ArrayList<Map<String, Object>> list = new ArrayList<>();
        //解析结果
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            //解析高亮的字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//原来的结果

            //解析高亮字段,将原来的字段换成高亮的字段
            if (title!=null){
                Text[] fragments = title.fragments();
                String newTitle ="";
                for (Text fragment : fragments) {
                    newTitle+=fragment;
                }
                sourceAsMap.put("title",newTitle);
            }
            list.add(sourceAsMap);
        }

        return list;
    }
}