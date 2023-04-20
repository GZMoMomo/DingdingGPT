package com.example.dingding.utils;

import com.example.dingding.pojo.user_send;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.SearchResults;
import io.milvus.param.ConnectParam;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.collection.ReleaseCollectionParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.SearchResultsWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.naming.directory.SearchResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.example.dingding.server.getMsg;

@Configuration
public class milvus {

    //milvus地址
    public static final String host="10.10.11.145";
    //端口
    public static final int port=19530;
    //返回数据条数
    public static final Integer SEARCH_K = 2;                       // TopK
    //查询时探测的候选向量数
    public static final String SEARCH_PARAM = "{\"nprobe\":10}";   // Params
    //集合
    public static final String collectionName="product";
    //分区
    public static final String partitionName="product";
    //返回的字段
    public static final List<String> search_output_fields = Arrays.asList("id");
    //创建milvus客户端
    public static final MilvusServiceClient milvusClient = new MilvusServiceClient(
            ConnectParam.newBuilder()
                    .withHost(host)
                    .withPort(port)
                    .build()
    );


    /**
     * 方法起点
     * @param user
     * @return 返回两个文本的索引id
     */
    public static List<Integer> returnResultId(user_send user){
        //序列化向量
        Float[] embedding=milvus.stringToDoubleArray(user.getAnswer());
        List<List<Float>> vector=new ArrayList<>();
        List<Float> floatList = new ArrayList<>(embedding.length);
        for (float floatValue : embedding) {
            floatList.add(floatValue);
        }
        vector.add(floatList);
        //查询milvus中最相似的文本，最终返回它的id。
        List<Integer> ids=milvus.searchMilvus(vector);
        return ids;
    }

    /**
     * 查询milvus
     * @param search_vectors
     * @return 返回文本的索引id
     */
    public static List<Integer> searchMilvus(List<List<Float>> search_vectors){
        //加载milvus集合
        milvusClient.loadCollection(
                LoadCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build()
        );

        //设置milvus搜索参数
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(collectionName)
                .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                .withMetricType(MetricType.L2)
                .withOutFields(search_output_fields)
                .withTopK(SEARCH_K)
                .withVectors(search_vectors)
                .withVectorFieldName("ada002")
                .withParams(SEARCH_PARAM)
                .build();
        //返回结果
        R<SearchResults> respSearch = milvusClient.search(searchParam);
        //解析结果
        SearchResultsWrapper wrapperSearch = new SearchResultsWrapper(respSearch.getData().getResults());
        //获取id
        List<?> id=wrapperSearch.getFieldData("id", 0);
        List<Integer> ids=new ArrayList<>();
        ids.add(Integer.valueOf(id.get(0).toString()));
        ids.add(Integer.valueOf(id.get(1).toString()));

        //释放集合
        milvusClient.releaseCollection(
                ReleaseCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build());
        return ids;
    }

    /**
     * 字符串转向量
     * @param s
     * @return
     */
    public static Float[] stringToDoubleArray(String s) {
        String[] parts = s.replaceAll("[\\[\\]]", "").split(", ");
        Float[] result = new Float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Float.parseFloat(parts[i]);
        }
        return result;
    }

}
