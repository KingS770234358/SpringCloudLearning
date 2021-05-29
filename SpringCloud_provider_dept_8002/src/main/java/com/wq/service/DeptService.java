package com.wq.service;

import com.wq.pojo.Dept;

import java.util.List;

/**
 * 服务层接口
 */
public interface DeptService {
    // 增加部门
    public boolean addDept(Dept dept);

    // 查询部门byId
    public Dept queryById(Long id);

    // 查询所有部门
    public List<Dept> queryAllDept();
}
