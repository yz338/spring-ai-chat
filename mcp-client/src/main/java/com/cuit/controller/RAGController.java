package com.cuit.controller;

import com.cuit.service.ChatService;
import com.cuit.service.DocumentService;
import com.cuit.utils.LeeResult;
import com.cuit.vo.ChatVo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("rag")
public class RAGController {
    @Autowired
    private DocumentService documentService;

    @Autowired
    private ChatService chatService;

    @PostMapping("uploadRagDoc")
    public LeeResult uploadRagDoc(@RequestParam("file") MultipartFile file){
        List<Document> documentList = documentService.loadText(file.getResource(), file.getOriginalFilename());
        return LeeResult.ok(documentList);
    }

    @GetMapping("dosearch")
    public LeeResult doSearch(String question){
        return LeeResult.ok(documentService.doSearch(question));
    }


    @PostMapping("search")
    public void search(@RequestBody ChatVo chatVo, HttpServletResponse response){
        List<Document> documentList = documentService.doSearch(chatVo.getMessage());
        response.setCharacterEncoding("UTF-8");
        chatService.doChatRagSearch(chatVo,documentList);
    }
}
