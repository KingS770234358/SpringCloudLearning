package com.wq.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain=true) // 使这个类的set方法支持链式写法
public class Dept implements Serializable {
    // 数据库bigint 要用Long接收
    private Long deptno;
    private String dname;
    // 看下这个数据是存在哪个数据库的字段  微服务---一个服务对应一个数据库
    // 同一个信息可能存在不同的数据库
    private String dbSource;

    // 单参构造函数
    public Dept(String dname){
        this.dname = dname;
    }
}
