package com.cuit.service;

import cn.hutool.json.JSONUtil;
import com.cuit.vo.SearXNGResponse;
import com.cuit.vo.SearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearXngServiceImpl implements SearXngService {
    @Value("${internet.websearch.searxng.url}")
    private String SEARXNG_URL;
    @Value("${internet.websearch.searxng.counts}")
    private Integer COUNTS;
    private final OkHttpClient okHttpClient;
    @Override
    public List<SearchResult> search(String query) {
        HttpUrl url=HttpUrl.get(SEARXNG_URL)
                        .newBuilder()
                        .addQueryParameter("q",query)
                        .addQueryParameter("format","json")
                        .build();
        log.info("搜索的url地址为:{}",url.url());
        Request request=new Request.Builder()
                .url(url).build();
        try(Response response = okHttpClient.newCall(request).execute()) {
            if(!response.isSuccessful())throw new RuntimeException("请求失败:HTTP"+response.code());
            if(response.body()!=null){
                String responseBody = response.body().string();
                SearXNGResponse searXNGResponse = JSONUtil.toBean(responseBody, SearXNGResponse.class);
                return  dealResults(searXNGResponse.getResults());
            }
            log.error("搜索失败：{}",response.message());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return List.of();
    }

    //处理结果集合，截取限制的个数
    private List<SearchResult> dealResults(List<SearchResult> results){
        return results.subList(0,Math.min(COUNTS,results.size()))
                .parallelStream()
                .sorted(Comparator.comparingDouble(SearchResult::getScore).reversed())
                .limit(COUNTS).toList();
    }
}
