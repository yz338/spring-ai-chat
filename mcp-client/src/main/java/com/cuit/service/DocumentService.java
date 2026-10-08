package com.cuit.service;

import com.cuit.vo.ChatVo;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.util.List;

public interface DocumentService {
    //加载文档并且读取数据进行保存到知识库
    public List<Document> loadText(Resource resource, String fileName);

    //根据用户的问题从知识库中查询数据返回给用户(相似度)
    public List<Document> doSearch(String question);


}
