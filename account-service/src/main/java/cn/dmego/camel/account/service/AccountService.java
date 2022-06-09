package cn.dmego.camel.account.service;

import cn.dmego.camel.common.dto.AccountDTO;

/**
 * AccountService
 *
 * @author dmego
 * @date 2021/3/31 10:46
 */
public interface AccountService {

    Boolean reduceBalance(AccountDTO accountDTO) throws Exception;

    Boolean compensateBalance(AccountDTO accountDTO) throws Exception;

    void clearHolder(String holderId);
}
