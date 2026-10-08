package com.cuit.service;

import com.cuit.enums.SSEMsgType;
import com.cuit.utils.CustomerTextSpliter;
import com.cuit.utils.SSEServer;
import com.cuit.vo.ChatVo;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final RedisVectorStore redisVectorStore;

    @Override
    public List<Document> loadText(Resource resource, String fileName) {
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("fileName", fileName);
        List<Document> documentList = textReader.read();
//        System.out.println("documentList: " + documentList);

//        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
//        List<Document> list = tokenTextSplitter.apply(documentList);
        CustomerTextSpliter tokenTextSplitter = new CustomerTextSpliter();
        List<Document> list = tokenTextSplitter.apply(documentList);
//        System.out.println("list="+list);
//        System.out.println("list.size()="+list.size());
        //向量存储
        redisVectorStore.add(list);
        return documentList;
    }

    @Override
    public List<Document> doSearch(String question) {
        return redisVectorStore.similaritySearch(question);
    }




}
