-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '产品id',
   `price` int(11) DEFAULT NULL COMMENT '产品单价',
   `stock` int(11) DEFAULT '0' COMMENT '总库存',
   `frozen` int(11) DEFAULT '0' COMMENT '冻结库存',
   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
INSERT INTO `saga_storage`.`product` (`id`, `price`, `stock`, `frozen`) VALUES ('1', '5', '100', '0');