package com.fsb.Service;

import com.fsb.pojo.DTO.OrderPageQueryDTO;
import com.fsb.pojo.DTO.UserOrderCreateDTO;
import com.fsb.pojo.DTO.UserOrderUpdateDTO;
import com.fsb.pojo.VO.OrderVO;
import com.fsb.result.PageResult;

public interface UserOrderService {
    Long createOrder(UserOrderCreateDTO dto, String username);

    PageResult listUserOrders(String username, OrderPageQueryDTO dto);

    OrderVO getUserOrderDetail(Long orderId, String username);

    void updateUserOrder(UserOrderUpdateDTO dto, String username);

    void confirmReceive(Long orderId, String username);
}
