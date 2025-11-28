package com.fsb.Service;

import com.fsb.pojo.DTO.OrderDTO;
import com.fsb.pojo.DTO.OrderItemDTO;
import com.fsb.pojo.DTO.OrderPageQueryDTO;
import com.fsb.pojo.VO.OrderVO;
import com.fsb.pojo.entity.Customer;
import com.fsb.pojo.entity.Order;
import com.fsb.result.PageResult;

import java.util.List;
import java.util.Map;

public interface OrderService {


    PageResult pageQuery(OrderPageQueryDTO orderPageQueryDTO);

    OrderVO getById(Long id);

    void updateReceiverInfo(OrderDTO dto);

    void deleteOrder(Long id);

    void updateAdminNote(OrderDTO dto);

    void closeOrder(OrderDTO dto);

    void deliveryOrder(Long orderId, String deliveryCompany, String deliverySn);

}
