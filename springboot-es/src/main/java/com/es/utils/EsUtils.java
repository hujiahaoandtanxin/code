package com.es.utils;

import com.es.bean.EsResult;
import com.google.gson.*;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.*;

public class EsUtils {

    /**
     * 创建索引
     *
     * @throws Exception
     */
    public static void CreateIndex(RestClient restClient, String index) throws Exception {
        String method = "PUT";
        String endpoint = "/" + index;
        Response response = restClient.performRequest(method, endpoint);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    /**
     * 创建文档
     *
     * @throws Exception
     */
    public static void CreateDocument(RestClient restClient, String index, String type, String jsonParam) throws Exception {
        String uuid = String.valueOf(UUID.randomUUID());
        String method = "PUT";
        StringBuilder endpoint = new StringBuilder()
                .append('/')
                .append(index)
                .append('/')
                .append(type)
                .append('/')
                .append(uuid);
        HttpEntity entity = new NStringEntity(
                jsonParam, ContentType.APPLICATION_JSON);
        Response response = restClient.performRequest(method, endpoint.toString(), Collections.<String, String>emptyMap(), entity);
    }

    /**
     * 根据 _id 获取文档
     *
     * @throws Exception
     */
    public static void getDocument(RestClient restClient, String index, String type, String id) throws Exception {
        String method = "GET";
        StringBuilder endpoint = new StringBuilder()
                .append('/')
                .append(index)
                .append('/')
                .append(type)
                .append('/')
                .append(id);
        Response response = restClient.performRequest(method, endpoint.toString());
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    /**
     * 查询所有数据
     *
     * @throws Exception
     */
    public static List<EsResult> QueryAll(RestClient restClient, String index, String type, String jsonParam) throws Exception {
        Gson gson = new Gson();
        String method = "POST";
        StringBuilder endpoint = new StringBuilder()
                .append('/')
                .append(index)
                .append('/')
                .append(type)
                .append('/')
                .append("_search");
        HttpEntity entity = new NStringEntity(jsonParam, ContentType.APPLICATION_JSON);
        Response response = restClient.performRequest(method, endpoint.toString(), Collections.<String, String>emptyMap(), entity);
        JsonArray jsonArray = JsonParser.parseString(EntityUtils.toString(response.getEntity())).getAsJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray();
        Iterator<JsonElement> iterator = jsonArray.iterator();
        List<EsResult> list = new ArrayList<>();
        while (iterator.hasNext()){
            JsonElement next = iterator.next();
            JsonObject asJsonObject = next.getAsJsonObject().get("_source").getAsJsonObject();
            EsResult esResult = gson.fromJson(asJsonObject, EsResult.class);
            list.add(esResult);
        }
        return list;
    }
}
