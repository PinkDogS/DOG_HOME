package com.ailen.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 创建索引库  goods
 * 字段以及类型
 */
@Data
@Accessors(chain = true)
@Document(indexName = "goods")
public class Goods {

    @Field(type = FieldType.Keyword)
    private String id;
    @Field(type = FieldType.Text)
    private String goodsName;
    @Field(type = FieldType.Integer)
    private String store;
    @Field(type = FieldType.Double)
    private String price;


}
