package com.fsb.Service;

import com.fsb.pojo.DTO.OrderPageQueryDTO;
import com.fsb.pojo.DTO.UserOrderCreateDTO;
import com.fsb.pojo.DTO.UserOrderUpdateDTO;
import com.fsb.result.PageResult;

public interface UserOrderService {
    Long createOrder(UserOrderCreateDTO dto);

    PageResult listUserOrders(String username, OrderPageQueryDTO dto);

    void updateUserOrder(UserOrderUpdateDTO dto);
}
