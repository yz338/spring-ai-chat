package com.cuit.service;

import com.cuit.vo.SearchResult;

import java.util.List;

public interface SearXngService {
    public List<SearchResult> search(String query);
}
