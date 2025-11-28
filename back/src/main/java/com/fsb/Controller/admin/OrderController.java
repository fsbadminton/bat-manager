package com.fsb.Controller.admin;


import com.fsb.Service.OrderService;
import com.fsb.pojo.DTO.DeliveryOrderDTO;
import com.fsb.pojo.DTO.OrderDTO;
import com.fsb.pojo.DTO.OrderItemDTO;
import com.fsb.pojo.DTO.OrderPageQueryDTO;
import com.fsb.pojo.VO.OrderVO;
import com.fsb.pojo.entity.Order;
import com.fsb.result.PageResult;
import com.fsb.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/order")
public class OrderController {


    @Autowired
    private OrderService orderService;


    /**
     * 分页查询
     * @param orderPageQueryDTO
     * @return
     */
    @GetMapping("/list")
    public Result<PageResult> page(OrderPageQueryDTO orderPageQueryDTO){
        PageResult pageResult = orderService.pageQuery(orderPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    public Result<OrderVO> getById(@PathVariable Long id){
        log.info("查询id为{}的订单信息", id);
        OrderVO orderVO=orderService.getById(id);
        return Result.success(orderVO);
    }

    /*
    * 修改订单收货人信息
     */
    @PostMapping("/update/receiverInfo")
    public Result updateReceiverInfo(@RequestBody OrderDTO dto) {
        orderService.updateReceiverInfo(dto);
        return Result.success();
    }


    /**
     * 删除订单
     * @param id
     * @return
     */
    @DeleteMapping("/delete")
    public Result deleteOrder(@RequestParam Long id) {
        orderService.deleteOrder(id);
        return Result.success();
    }

    /**
     * 修改订单备注
     * @param dto
     * @return
     */
    @PostMapping("/update/note")
    public Result updateAdminNote(@RequestBody OrderDTO dto){
        orderService.updateAdminNote(dto);
        return Result.success();
    }

    /**
     * 关闭订单
     * @param dto
     * @return
     */
    @PostMapping("/update/close")
    public Result closeOrder(@RequestBody OrderDTO dto) {
        orderService.closeOrder(dto);
        return Result.success();
    }


    /**
     * 订单发货
     */
    @PostMapping("/update/delivery")
    public Result deliveryOrder(@RequestBody DeliveryOrderDTO dto) {
        orderService.deliveryOrder(dto.getOrderId(), dto.getDeliveryCompany(), dto.getDeliverySn());
        return Result.success();
    }
}
