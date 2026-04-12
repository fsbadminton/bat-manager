package com.fsb.Service;

import com.fsb.pojo.DTO.OrderPageQueryDTO;
import com.fsb.pojo.DTO.UserOrderCreateDTO;
import com.fsb.result.PageResult;

public interface UserOrderService {
    Long createOrder(UserOrderCreateDTO dto, String username);

    PageResult listUserOrders(String username, OrderPageQueryDTO dto);
}
