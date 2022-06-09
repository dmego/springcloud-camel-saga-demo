package cn.dmego.camel.common.dto;

import cn.dmego.camel.common.constant.ExceptionType;

import java.util.Set;

/**
 * BusinessDTO
 *
 * @author dmego
 * @date 2022/6/9 14:28
 */
public class BusinessDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 购买商品数量
     */
    private Integer count;

    /**
     * 异常Set
     */
    private Set<ExceptionType> exception;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Set<ExceptionType> getException() {
        return exception;
    }

    public void setException(Set<ExceptionType> exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "BusinessDTO{" +
                "userId=" + userId +
                ", productId=" + productId +
                ", count=" + count +
                '}';
    }
}