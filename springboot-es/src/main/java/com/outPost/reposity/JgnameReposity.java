package com.outPost.reposity;

import com.outPost.entity.Jgname;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JgnameReposity extends CrudRepository<Jgname, Long> {

    //通过名字相等查询
    @Query(value="select name from t_mdm_jgname where name like %:name% LIMIT 0,10",nativeQuery = true)
    List<String> findByName(String name);
}
