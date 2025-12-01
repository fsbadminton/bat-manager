package com.fsb.Controller.user;


import com.fsb.Service.UserOrderService;
import com.fsb.pojo.DTO.OrderPageQueryDTO;
import com.fsb.pojo.DTO.UserOrderCreateDTO;
import com.fsb.pojo.DTO.UserOrderUpdateDTO;
import com.fsb.result.PageResult;
import com.fsb.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/order")
@Slf4j
public class UserOrderController {

    @Autowired
    private UserOrderService userOrderService;

    /**
     * 创建订单
     * @return
     */
    @PostMapping("/create")
    public Result<Long> createOrder(@RequestBody UserOrderCreateDTO dto) {
        Long orderId = userOrderService.createOrder(dto);
        return Result.success(orderId);
    }

    @GetMapping("/list")
    public Result<PageResult> list(OrderPageQueryDTO  dto){
        // 假设你从 token 中取的用户名，这里先写死
        String username = "zhangsan";

        PageResult pageResult = userOrderService.listUserOrders(username, dto);
        return Result.success(pageResult);

    }

    /**
     * 修改订单
     * @return
     */
    @PostMapping("/update")
    public Result updateOrder(@RequestBody UserOrderUpdateDTO dto){
        log.info("用户更新订单请求：{}", dto);
        userOrderService.updateUserOrder(dto);
        return Result.success();

    }

}
