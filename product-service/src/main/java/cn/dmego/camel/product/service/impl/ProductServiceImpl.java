package cn.dmego.camel.product.service.impl;


import cn.dmego.camel.common.constant.ExceptionType;
import cn.dmego.camel.common.constant.HolderType;
import cn.dmego.camel.common.dto.ProductDTO;
import cn.dmego.camel.common.util.ResultHolder;
import cn.dmego.camel.product.dao.ProductDao;
import cn.dmego.camel.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ProductServiceImpl
 *
 * @author dmego
 * @date 2021/3/31 10:53
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProductDao productDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean reduceStock(ProductDTO product) throws Exception {
        Long productId = product.getProductId();
        Integer count = product.getCount();
        logger.info("[reduceStock] 开始扣减库存, userId:{}, count: {}", productId, count);
        // 幂等控制
        if (HolderType.REDUCE_STOCK == ResultHolder.getResult(getClass(), product.getHolderId())) {
            logger.warn("[reduceStock] 扣减库存触发幂等控制");
            return true;
        }
        // 检查库存
        checkStock(productId, count);

        // 扣减库存
        int result = productDao.reduceStock(productId, count);
        if(result == 0){
            logger.warn("[reduceStock] 扣减库存失败, productId:{}, count: {} ", productId, count);
            return false;
        }
        //保存一个幂等标识
        ResultHolder.setResult(getClass(), product.getHolderId(), HolderType.REDUCE_STOCK);

        // 触发异常
        if(product.getExceptionTypeSet() != null && product.getExceptionTypeSet().contains(ExceptionType.REDUCE_STOCK)){
            throw new Exception("[reduceStock] 扣减库存发生异常!");
        }
        logger.info("[reduceStock] 扣减库存成功, productId:{}, count: {}", productId, count);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean compensateStock(ProductDTO product) throws Exception {
        Long productId = product.getProductId();
        Integer count = product.getCount();
        logger.info("[compensateStock] 开始回滚库存, userId:{}, count: {}", productId, count);
        // 幂等控制
        if (HolderType.ROLLBACK_STOCK == ResultHolder.getResult(getClass(), product.getHolderId())) {
            logger.warn("[compensateStock] 回滚库存触发幂等控制");
            return true;
        }

        // 补偿库存
        int result = productDao.compensateStock(productId, count);
        if(result == 0){
            logger.warn("[compensateStock] 回滚库存失败, productId:{}, count: {} ", productId, count);
            return false;
        }

        //保存一个幂等标识
        ResultHolder.setResult(getClass(), product.getHolderId(), HolderType.ROLLBACK_STOCK);

        // 触发异常
        if(product.getExceptionTypeSet() != null && product.getExceptionTypeSet().contains(ExceptionType.ROLLBACK_STOCK)){
            throw new Exception("[compensateStock] 补偿库存发生异常!");
        }
        logger.info("[compensateStock] 回滚库存成功, productId:{}, count: {}", productId, count);
        return true;
    }

    @Override
    public Integer getPriceById(Long productId) {
        return productDao.selectPriceById(productId);
    }

    @Override
    public void clearHolder(String holderId) {
        ResultHolder.removeResult(getClass(), holderId);
    }

    private void checkStock(Long productId, Integer count) throws Exception {
        logger.info("[checkStock] 检查商品 {} 库存", productId);
        Integer stock = productDao.getStock(productId);
        if (stock < count) {
            logger.warn("[checkStock] 商品 {} 库存不足，当前库存: {}", productId, stock);
            throw new Exception("库存不足");
        }
    }
}
