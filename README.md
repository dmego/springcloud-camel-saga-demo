# springcloud-camel-saga-demo
Spring Cloud 集成 Apache Camel 并使用  Saga 分布式事务模式完成创建订单-->扣减余额-->扣减库存的 Demo 示例  


- 正向测试

```shell
curl --location --request POST 'http://127.0.0.1:8085/camel/buy' \
--header 'Content-Type: application/json' \
--data-raw '{
    "userId": "1",
    "productId": "1",
    "count": "2"
}'
```

- 异常补偿测试
异常类型见：cn.dmego.camel.common.constant.ExceptionType
例如在扣减库存时出现异常: "exception": [5]

```shell
curl --location --request POST 'http://127.0.0.1:8085/camel/buy' \
--header 'Content-Type: application/json' \
--data-raw '{
    "userId": "1",
    "productId": "1",
    "count": "2",
    "exception": [5]
}'
```