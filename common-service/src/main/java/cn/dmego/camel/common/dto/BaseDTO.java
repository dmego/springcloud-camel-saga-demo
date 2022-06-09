package cn.dmego.camel.common.dto;

import cn.dmego.camel.common.constant.ExceptionType;

import java.util.Set;

/**
 * @author dmego
 * @date 2022/06/09 14:05
 */
public class BaseDTO {
    /**
     * 订单号（幂等控制）
     */
    private String holderId;

    /**
     * 触发异常类型
     */
    private Set<ExceptionType> exceptionTypeSet;

    public String getHolderId() {
        return holderId;
    }

    public void setHolderId(String holderId) {
        this.holderId = holderId;
    }

    public Set<ExceptionType> getExceptionTypeSet() {
        return exceptionTypeSet;
    }

    public void setExceptionTypeSet(Set<ExceptionType> exceptionTypeSet) {
        this.exceptionTypeSet = exceptionTypeSet;
    }
}
