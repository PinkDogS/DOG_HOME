package com.ailen.mapper;

import com.ailen.model.Goods;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchTypeMapper;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

public interface GoodsMapper extends ElasticsearchRepository<Goods,String> {



}
