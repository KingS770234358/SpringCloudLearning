package com.wq.mapper;

import com.wq.pojo.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper //使用mapper.xml作为实现类
@Repository //注入到容器中
public interface DeptMapper {

    // 增加部门
    public boolean addDept(Dept dept);

    // 查询部门byId
    public Dept queryById(@Param("deptId") Long id);

    // 查询所有部门
    public List<Dept> queryAllDept();
}
