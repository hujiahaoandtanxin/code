package com.es;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.es.bean.EsResult;
import com.es.service.EsService;
import com.es.utils.EsUtils;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class EsClientTest {

    @Autowired
    EsService esService;

    @Autowired
    RestClient restClient;

    @Test
    public void testCreateIndex(){
        try {
            EsUtils.CreateIndex(restClient,"test_index");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateDoc(){
        String jsonParam = "{\n" +
                "    \"clientId\":\"65742\",\n" +
                "    \"clientName\":\"胡小仙\",\n" +
                "    \"timestamp\":\"2020/11/3 20:22:35\",\n" +
                "    \"action\":\"进入登陆界面\"\n" +
                "}";
        try {
            EsUtils.CreateDocument(restClient,"hu_index","hu_type",jsonParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void search() throws IOException {
        Map<String, String> params = Collections.emptyMap();

        String queryString = "{\"query\":{\"match_all\":{}}}";

        HttpEntity entity = new NStringEntity(queryString, ContentType.APPLICATION_JSON);

        try {

            Response response = restClient.performRequest("GET", "/hu_index/_search", params, entity);
            String responseBody = null;

            responseBody = EntityUtils.toString(response.getEntity());

            JSONObject jsonObject = JSON.parseObject(responseBody);


            System.out.println(jsonObject.get("hits"));
        }catch (ResponseException e){
            e.printStackTrace();
        }

    }
}
