package cn.dmego.camel.common.dto;

/**
 * ProductDTO
 *
 * @author dmego
 * @date 2022/6/9 14:29
 */
public class ProductDTO extends BaseDTO {

    /**
     * 商品编号
     */
    private Long productId;

    /**
     * 扣减数量
     */
    private Integer count;

    public ProductDTO() {
    }

    public ProductDTO(Long productId, Integer count) {
        this.productId = productId;
        this.count = count;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "ProductDTO {" +
                "productId=" + productId +
                ", count=" + count +
                '}';
    }
}
