package cn.dmego.camel.common.constant;

/**
 * @author dmego
 * @date 2022/06/09 14:09
 */
public enum ExceptionType {

    /**
     * 不执行异常
     */
    OFF(0),

    /**
     * 创建订单异常
     */
    CREATE_ORDER(1),

    /**
     * 补偿订单异常
     */
    ROLLBACK_ORDER(2),

    /**
     * 扣减余额异常
     */
    REDUCE_BALANCE(3),

    /**
     * 补偿余额异常
     */
    ROLLBACK_BALANCE(4),

    /**
     * 扣减库存异常
     */
    REDUCE_STOCK(5),

    /**
     * 补偿库存异常
     */
    ROLLBACK_STOCK(6)

    ;

    private final Integer code;

    public Integer getCode() {
        return code;
    }

    ExceptionType(Integer code) {
        this.code = code;
    }

    public static ExceptionType get(int code) {
        ExceptionType value = null;
        try {
            value = ExceptionType.values()[code];
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown ExceptionType[" + code + "]");
        }
        return value;
    }


}
