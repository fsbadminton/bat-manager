package com.fsb.mapper;

import com.fsb.Mapper.OrderMapper;
import com.fsb.pojo.DTO.OrderPageQueryDTO;
import org.apache.ibatis.annotations.Select;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderMapperTest {

    @Test
    void userOrdersAreSortedNewestFirst() throws NoSuchMethodException {
        Method method = OrderMapper.class.getMethod(
                "listUserPageQuery", String.class, OrderPageQueryDTO.class);
        Select select = method.getAnnotation(Select.class);
        String sql = String.join(" ", Arrays.asList(select.value()))
                .replaceAll("\\s+", " ")
                .toLowerCase();

        assertTrue(sql.contains("order by create_time desc, id desc"));
    }
}
