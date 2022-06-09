package cn.dmego.camel.common.constant;

/**
 * @author dmego
 * @date 2022/06/09 14:42
 */
public enum HolderType {

    /**
     * 创建订单
     */
    CREATE_ORDER("create-order"),

    /**
     * 补偿订单
     */
    ROLLBACK_ORDER("rollback-order"),

    /**
     * 扣减余额
     */
    REDUCE_BALANCE("reduce-balance"),

    /**
     * 补偿余额
     */
    ROLLBACK_BALANCE("rollback-balance"),

    /**
     * 扣减库存
     */
    REDUCE_STOCK("reduce-stock"),

    /**
     * 补偿库存
     */
    ROLLBACK_STOCK("rollback-stock")

    ;

    private final String name;

    public String getName() {
        return name;
    }

    HolderType(String name) {
        this.name = name;
    }

    public static HolderType get(String name) {
        HolderType value = null;
        try {
            for (HolderType holder : HolderType.values()) {
                if (holder.getName().equals(name)) {
                    value = holder;
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown HolderType[" + name + "]");
        }
        return value;
    }
}
