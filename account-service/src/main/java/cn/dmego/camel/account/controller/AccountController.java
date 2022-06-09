package cn.dmego.camel.account.controller;

import cn.dmego.camel.account.service.AccountService;
import cn.dmego.camel.common.dto.AccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AccountController
 * 
 * @author dmego
 * @date 2021/3/31 10:51
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    AccountService accountService;

    @PostMapping("/reduceBalance")
    Boolean reduceBalance(@RequestBody AccountDTO accountDTO) throws Exception {
        return accountService.reduceBalance(accountDTO);
    }

    @PostMapping("/compensateBalance")
    Boolean compensateBalance(@RequestBody AccountDTO accountDTO) throws Exception {
        return accountService.compensateBalance(accountDTO);
    }

    @DeleteMapping("/clearHolder/{holderId}")
    void clearHolder(@PathVariable("holderId") String holderId) {
        accountService.clearHolder(holderId);
    }
}
