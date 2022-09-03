package com.itheima;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Stream;

@SpringBootTest
class ReggieApplicationTests {

    @Test
    void contextLoads() {
        // 获取Stream流
        Stream<String> stream = Stream.of("1", "2", "3", "4", "5");
        // 使用map方法，把字符串类型的整数，转换（映射）为Integer类型的整数
        stream.map(str -> {
            return Integer.parseInt(str);
        }).forEach(i->{
            System.out.println(i);
        });

    }

}
