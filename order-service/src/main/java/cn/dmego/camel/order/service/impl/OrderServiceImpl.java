package cn.dmego.camel.order.service.impl;

import cn.dmego.camel.common.constant.ExceptionType;
import cn.dmego.camel.common.constant.HolderType;
import cn.dmego.camel.common.dto.OrderDTO;
import cn.dmego.camel.common.util.ResultHolder;
import cn.dmego.camel.order.dao.OrderDao;
import cn.dmego.camel.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OrderServiceImpl
 *
 * @author dmego
 * @date 2021/3/31 10:51
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    OrderDao orderDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean createOrder(OrderDTO order) throws Exception {
        logger.info("[createOrder] 开始创建订单: {}", order.toString());
        // 幂等控制
        if (HolderType.CREATE_ORDER == ResultHolder.getResult(getClass(), order.getHolderId())) {
            logger.warn("[createOrder] 创建订单触发幂等控制");
            return true;
        }

        // 创建订单
        int result = orderDao.createOrder(order);
        if(result == 0){
            logger.warn("[createOrder] 创建订单 {} 失败", order);
            return false;
        }
        //保存一个幂等标识
        ResultHolder.setResult(getClass(), order.getHolderId(), HolderType.CREATE_ORDER);

        // 触发异常
        if(order.getExceptionTypeSet() != null && order.getExceptionTypeSet().contains(ExceptionType.CREATE_ORDER)){
            throw new Exception("[createOrder] 创建订单发生异常!");
        }

        logger.info("[createOrder] 保存订单成功: {}", order.getId());
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean revokeOrder(OrderDTO order) throws Exception {
        logger.info("[revokeOrder] 开始撤销订单, orderId: {}", order.getId());
        // 幂等控制
        if (HolderType.ROLLBACK_ORDER == ResultHolder.getResult(getClass(), order.getHolderId())) {
            logger.warn("[revokeOrder] 撤销订单触发幂等控制");
            return true;
        }

        // 撤销订单
        int result = orderDao.revokeOrder(order.getId());
        if(result == 0){
            logger.warn("[revokeOrder] 撤销订单 orderId: {} 失败", order.getId());
            return false;
        }

        //保存一个幂等标识
        ResultHolder.setResult(getClass(), order.getHolderId(), HolderType.ROLLBACK_ORDER);

        // 触发异常
        if(order.getExceptionTypeSet() != null && order.getExceptionTypeSet().contains(ExceptionType.ROLLBACK_ORDER)){
            throw new Exception("[revokeOrder] 撤销订单发生异常!");
        }
        logger.info("[revokeOrder] 撤销订单成功 orderId: {}", order.getId());
        return true;
    }

    @Override
    public void clearHolder(String holderId) {
        ResultHolder.removeResult(getClass(), holderId);
    }
}
