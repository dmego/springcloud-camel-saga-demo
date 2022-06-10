-- ----------------------------
-- Table structure for account
-- ----------------------------
CREATE TABLE `account` (
   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户id',
   `balance` int(11) DEFAULT NULL COMMENT '总余额',
   `frozen` int(11) DEFAULT NULL COMMENT '冻结余额',
   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
INSERT INTO `saga_account`.`account` (`id`, `balance`, `frozen`) VALUES ('1', '100', '0');