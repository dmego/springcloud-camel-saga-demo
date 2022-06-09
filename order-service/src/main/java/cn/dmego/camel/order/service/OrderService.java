package cn.dmego.camel.order.service;


import cn.dmego.camel.common.dto.OrderDTO;

/**
 * OrderService
 *
 * @author dmego
 * @date 2021/3/31 10:52
 */
public interface OrderService {

    Boolean createOrder(OrderDTO orderDTO) throws Exception;

    Boolean revokeOrder(OrderDTO order) throws Exception;

    void clearHolder(String holderId);

}
