package cn.dmego.camel.common.dto;

/**
 * AccountDTO
 *
 * @author dmego
 * @date 2022/6/9 14:28
 */
public class AccountDTO extends BaseDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 扣减金额
     */
    private Integer amount;

    public AccountDTO() {
    }

    public AccountDTO(Long userId, Integer amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }


    @Override
    public String toString() {
        return "AccountDTO{" +
                "userId=" + userId +
                ", amount=" + amount +
                '}';
    }
}
