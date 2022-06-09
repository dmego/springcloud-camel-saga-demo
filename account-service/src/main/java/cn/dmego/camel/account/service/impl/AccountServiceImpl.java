package cn.dmego.camel.account.service.impl;

import cn.dmego.camel.account.dao.AccountDao;
import cn.dmego.camel.account.service.AccountService;
import cn.dmego.camel.common.constant.ExceptionType;
import cn.dmego.camel.common.constant.HolderType;
import cn.dmego.camel.common.dto.AccountDTO;
import cn.dmego.camel.common.util.ResultHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AccountServiceImpl
 *
 * @author dmego
 * @date 2021/3/31 10:46
 */
@Service
public class AccountServiceImpl implements AccountService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    AccountDao accountDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean reduceBalance(AccountDTO account) throws Exception {
        Long userId = account.getUserId();
        Integer amount = account.getAmount();
        logger.info("[reduceBalance] 开始扣减余额, userId:{}, amount: {}", userId, amount);

        // 防悬挂
        if (HolderType.ROLLBACK_BALANCE == ResultHolder.getResult(getClass(), account.getHolderId())) {
            logger.warn("[reduceBalance] 扣减余额触发防悬挂");
            return false;
        }

        // 幂等控制
        if (HolderType.REDUCE_BALANCE == ResultHolder.getResult(getClass(), account.getHolderId())) {
            logger.warn("[reduceBalance] 扣减余额触发幂等控制");
            return true;
        }

        // 检查余额
        checkBalance(userId, amount);

        // 扣减余额
        int result = accountDao.reduceBalance(userId, amount);
        if(result == 0){
            logger.warn("[reduceBalance] 扣减余额失败, userId:{}, amount: {} ", userId, amount);
            return false;
        }

        //保存一个幂等标识
        ResultHolder.setResult(getClass(), account.getHolderId(), HolderType.REDUCE_BALANCE);

        // 触发异常
        if(account.getExceptionTypeSet() != null && account.getExceptionTypeSet().contains(ExceptionType.REDUCE_BALANCE)){
            throw new Exception("[reduceBalance] 扣减余额发生异常!");
        }

        logger.info("[reduceBalance] 扣减余额成功, userId:{}, amount: {}", userId, amount);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean compensateBalance(AccountDTO account) throws Exception {
        Long userId = account.getUserId();
        Integer amount = account.getAmount();
        logger.info("[compensateBalance] 开始回滚余额, userId:{}, amount: {}", userId, amount);

        // 空回滚控制
        if (ResultHolder.getResult(getClass(), account.getHolderId()) == null) {
            logger.warn("[compensateBalance] 回滚余额触发空回滚");
            return true;
        }

        // 幂等控制
        if (HolderType.ROLLBACK_BALANCE == ResultHolder.getResult(getClass(), account.getHolderId())) {
            logger.warn("[compensateBalance] 回滚余额触发幂等控制");
            return true;
        }

        // 补偿余额
        int result = accountDao.compensateBalance(userId, amount);
        if(result == 0){
            logger.warn("[compensateBalance] 回滚余额失败, userId:{}, amount: {}", userId, amount);
            return false;
        }

        //保存一个幂等标识
        ResultHolder.setResult(getClass(), account.getHolderId(), HolderType.ROLLBACK_BALANCE);

        // 触发异常
        if(account.getExceptionTypeSet() != null && account.getExceptionTypeSet().contains(ExceptionType.ROLLBACK_BALANCE)){
            throw new Exception("[compensateBalance] 补偿余额发生异常!");
        }

        logger.info("[compensateBalance] 回滚余额成功, userId:{}, amount: {}", userId, amount);
        return true;
    }

    @Override
    public void clearHolder(String holderId) {
        ResultHolder.removeResult(getClass(), holderId);
    }

    private void checkBalance(Long userId, Integer price) throws Exception {
        logger.info("[checkBalance] 检查用户 {} 余额", userId);
        Integer balance = accountDao.getBalance(userId);
        if (balance < price) {
            logger.warn("[checkBalance] 用户 {} 余额不足，当前余额:{}", userId, balance);
            throw new Exception("余额不足");
        }
    }
}
