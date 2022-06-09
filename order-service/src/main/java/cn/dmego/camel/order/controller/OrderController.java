package cn.dmego.camel.order.controller;

import cn.dmego.camel.common.dto.OrderDTO;
import cn.dmego.camel.order.service.OrderService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * OrderController
 * 
 * @author dmego
 * @date 2022/6/7 20:14
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping("/createOrder")
    public Boolean createOrder(@RequestBody OrderDTO orderDTO) throws Exception {
        return orderService.createOrder(orderDTO);
    }

    @PostMapping("/revokeOrder")
    public Boolean revokeOrder(@RequestBody OrderDTO orderDTO) throws Exception {
        return orderService.revokeOrder(orderDTO);
    }

    @DeleteMapping("/clearHolder/{holderId}")
    void clearHolder(@PathVariable("holderId") String holderId) {
        orderService.clearHolder(holderId);
    }

}
