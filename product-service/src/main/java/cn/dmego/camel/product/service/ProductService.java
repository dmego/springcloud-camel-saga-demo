package cn.dmego.camel.product.service;


import cn.dmego.camel.common.dto.ProductDTO;

/**
 * ProductService
 *
 * @author dmego
 * @date 2021/3/31 10:53
 */
public interface ProductService {

    Boolean reduceStock(ProductDTO productDTO) throws Exception;

    Boolean compensateStock(ProductDTO productDTO) throws Exception;

    Integer getPriceById(Long productId);

    void clearHolder(String holderId);
}
