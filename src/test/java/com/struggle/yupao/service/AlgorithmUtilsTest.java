package com.struggle.yupao.service;

import com.struggle.yupao.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 算法工具类测试
 * @author Mr.Chen
 */
public class AlgorithmUtilsTest {
    @Test
    void test(){
        String str1 = "奋斗是1";
        String str2 = "奋斗不是1";
        String str3 = "奋斗是2不是1";
        //1
        int score1 = AlgorithmUtils.minDistance(str1, str2);
        //3
        int score2 = AlgorithmUtils.minDistance(str1, str3);
        System.out.println(score1);
        System.out.println(score2);
    }
    @Test
    void testCompareTags(){
        List<String> tagList1 = Arrays.asList("Java", "大一", "男");
        List<String> tagList2 = Arrays.asList("Java","大一","女");
        List<String> tagList3 = Arrays.asList("Python","大二","女");
        //1
        int score1 = AlgorithmUtils.minDistance(tagList1, tagList2);
        //3
        int score2 = AlgorithmUtils.minDistance(tagList1, tagList3);
        System.out.println(score1);
        System.out.println(score2);
    }
}
