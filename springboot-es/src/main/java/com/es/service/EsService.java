package com.es.service;

import com.es.bean.EsResult;
import com.es.utils.EsUtils;
import com.google.gson.Gson;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EsService {

    @Autowired
    private RestClient restClient;

    public String testQueryAll(){
        System.out.println("restClient内存对象打印："+restClient);
        String result = "";
        String jsonParam = "{\n" +
                "  \"query\": {\n" +
                "    \"match_all\": {}\n" +
                "  }\n" +
                "}";
        try {
            List<EsResult> esResults = EsUtils.QueryAll(restClient, "hu_index", "hu_type", jsonParam);
            Gson gson = new Gson();
            result = gson.toJson(esResults);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
