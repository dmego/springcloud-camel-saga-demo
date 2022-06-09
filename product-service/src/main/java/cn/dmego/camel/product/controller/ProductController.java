package cn.dmego.camel.product.controller;

import cn.dmego.camel.common.dto.ProductDTO;
import cn.dmego.camel.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ProductController
 *
 * @author dmego
 * @date 2021/3/31 10:52
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/reduceStock")
    Boolean reduceStock(@RequestBody ProductDTO productDTO) throws Exception {
        return productService.reduceStock(productDTO);
    }

    @PostMapping("/compensateStock")
    Boolean compensateStock(@RequestBody ProductDTO productDTO) throws Exception {
        return productService.compensateStock(productDTO);
    }

    @GetMapping("/getPrice/{productId}")
    Integer getPrice(@PathVariable("productId") Long productId) {
        return productService.getPriceById(productId);
    }


    @DeleteMapping("/clearHolder/{holderId}")
    void clearHolder(@PathVariable("holderId") String holderId) {
        productService.clearHolder(holderId);
    }
}
