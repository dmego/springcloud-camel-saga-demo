package cn.dmego.camel.route;

import cn.dmego.camel.common.dto.AccountDTO;
import cn.dmego.camel.common.dto.BusinessDTO;
import cn.dmego.camel.common.dto.OrderDTO;
import cn.dmego.camel.common.dto.ProductDTO;
import cn.dmego.camel.common.util.IDUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpMethods;
import org.apache.camel.model.SagaCompletionMode;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.saga.InMemorySagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author dmego
 * @date 2022/06/07 18:22
 */
@Component
public class BusinessRoute extends RouteBuilder {

    @Autowired
    CamelContext camelContext;

    private static final String ACCOUNT_SERVICE = "account-service";
    private static final String ORDER_SERVICE = "order-service";
    private static final String PRODUCT_SERVICE = "product-service";

    @Override
    public void configure() throws Exception {
        camelContext.addService(new InMemorySagaService());
        camelContext.getExecutorServiceManager().getDefaultThreadPoolProfile().setMaxQueueSize(-1);

        rest().description("Camel Saga Test Api")
                .consumes("application/json").produces("application/json")
                .post("/buy").description("buy a product and create order")
                .outType(String.class).type(BusinessDTO.class)
                .route()
                .id("camel-rest")
                .to("direct:prepare-data")
                .to("direct:start-buy")
                .end();

        rest().description("Camel Saga compensate Api")
                .consumes("application/json").produces("application/json")
                .put("/compensate")
                .route()
                .id("lra-compensate")
                .removeHeaders("CamelHttp*")
                .to("direct:cancel-buy")
                .end();

        from("direct:start-buy")
                .id("start-buy")
                .log("Start of direct:start-buy with body: ${body}")
                .removeHeaders("CamelHttp*")
                .saga()
                .option("holderId", exchangeProperty("orderId"))
                .option("order", exchangeProperty("order"))
                .option("account", exchangeProperty("account"))
                .option("product", exchangeProperty("product"))
                .propagation(SagaPropagation.REQUIRED)
                .completionMode(SagaCompletionMode.AUTO)
                .compensation("direct:cancel-buy")
                .completion("direct:complete-buy")
                .timeout(30, TimeUnit.SECONDS)
                //.multicast()
                //.parallelProcessing()
                .to("direct:create-order")
                .to("direct:reduce-balance")
                .to("direct:reduce-stock")
                .log("End of direct:start-buy with body: ${body}");

        from("direct:create-order")
                .id("create-order")
                .log("Start of direct:create-order with body: ${body}")
                .removeHeaders("CamelHttp*")
                .setBody(exchangeProperty("order"))
                //.marshal().json(JsonLibrary.Jackson)
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json;charset=UTF-8"))
                .serviceCall(ORDER_SERVICE + "/order/createOrder")
                .convertBodyTo(String.class)
                .log("End of direct:create-order with body: ${body}");

        from("direct:reduce-balance")
                .id("reduce-balance")
                .log("Start of direct:reduce-balance with body: ${body}")
                .removeHeaders("CamelHttp*")
                .setBody(exchangeProperty("account"))
                //.marshal().json(JsonLibrary.Jackson)
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json;charset=UTF-8"))
                .serviceCall(ACCOUNT_SERVICE + "/account/reduceBalance")
                .convertBodyTo(String.class)
                .log("End of direct:reduce-balance with body: ${body}");

        from("direct:reduce-stock")
                .id("reduce-stock")
                .log("Start of direct:reduce-stock with body: ${body}")
                .removeHeaders("CamelHttp*")
                .setBody(exchangeProperty("product"))
                //.marshal().json(JsonLibrary.Jackson)
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json;charset=UTF-8"))
                .serviceCall(PRODUCT_SERVICE + "/product/reduceStock")
                .convertBodyTo(String.class)
                .log("End of direct:reduce-stock with body: ${body}");

        from("direct:complete-buy")
                .id("complete-buy")
                .log("Start of direct:complete-buy with body: ${body}")
                .wireTap("direct:remove-holder")
                .log("End of direct:complete-buy with body: ${body}");

        from("direct:cancel-buy")
                .id("cancel-buy")
                .log("Start of direct:cancel-buy with header: ${headers}")
                .removeHeaders("CamelHttp*")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.setProperty("holderId", exchange.getIn().getHeader("holderId"));
                        exchange.setProperty("order", exchange.getIn().getHeader("order"));
                        exchange.setProperty("account", exchange.getIn().getHeader("account"));
                        exchange.setProperty("product", exchange.getIn().getHeader("product"));
                    }
                })
                .to("direct:rollback-order")
                .to("direct:rollback-balance")
                .to("direct:rollback-stock")
                .choice()
                    .when(exchange -> exchange.getIn().getBody().equals("true"))
                    .wireTap("direct:remove-holder")
                    .end()
                .log("End of direct:cancel-buy with body: ${body}");

        from("direct:rollback-order")
                .id("rollback-order")
                .log("Start of direct:rollback-order with body: ${body}")
                .removeHeaders("CamelHttp*")
                .setBody(exchangeProperty("order"))
                //.marshal().json(JsonLibrary.Jackson)
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json;charset=UTF-8"))
                .serviceCall(ORDER_SERVICE + "/order/revokeOrder")
                .convertBodyTo(String.class)
                .log("End of direct:rollback-order with body: ${body}");


        from("direct:rollback-balance")
                .id("rollback-balance")
                .log("Start of direct:rollback-balance with body: ${body}")
                .removeHeaders("CamelHttp*")
                .setBody(exchangeProperty("account"))
                //.marshal().json(JsonLibrary.Jackson)
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json;charset=UTF-8"))
                .serviceCall(ACCOUNT_SERVICE + "/account/compensateBalance")
                .convertBodyTo(String.class)
                .log("End of direct:rollback-balance with body: ${body}");


        from("direct:rollback-stock")
                .id("rollback-stock")
                .log("Start of direct:rollback-stock with body: ${body}")
                .removeHeaders("CamelHttp*")
                .setBody(exchangeProperty("product"))
                //.marshal().json(JsonLibrary.Jackson)
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json;charset=UTF-8"))
                .serviceCall(PRODUCT_SERVICE + "/product/compensateStock")
                .convertBodyTo(String.class)
                .log("End of direct:rollback-stock with body: ${body}");


        from("direct:query-price")
                .id("query-price")
                .log("Start of direct:query-price with body: ${body}")
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.GET))
                .serviceCall(PRODUCT_SERVICE + "/product/getPrice/${body.productId}")
                .convertBodyTo(Integer.class)
                .log("End of direct:query-price with body: ${body}");


        from("direct:prepare-data")
                .id("prepare-data")
                .log("Start of direct:prepare-data with body: ${body}")
                .removeHeaders("CamelHttp*")
                .setProperty("business", body())
                .to("direct:query-price")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Integer price = (Integer) exchange.getIn().getBody();
                        BusinessDTO business = (BusinessDTO)exchange.getProperty("business");
                        Integer payAmount = price * business.getCount();

                        // prepare create order request data
                        OrderDTO order = new OrderDTO();
                        Long orderId = IDUtils.nextId();
                        order.setId(orderId);
                        order.setProductId(business.getProductId());
                        order.setUserId(business.getUserId());
                        order.setCount(business.getCount());
                        order.setPayAmount(payAmount);

                        // prepare reduce balance request data
                        AccountDTO account = new AccountDTO();
                        account.setUserId(business.getUserId());
                        account.setAmount(payAmount);

                        // prepare reduce stock request data
                        ProductDTO product = new ProductDTO();
                        product.setProductId(business.getProductId());
                        product.setCount(business.getCount());

                        // set holder id and exception type
                        order.setHolderId(String.valueOf(orderId));
                        order.setExceptionTypeSet(business.getException());
                        account.setHolderId(String.valueOf(orderId));
                        account.setExceptionTypeSet(business.getException());
                        product.setHolderId(String.valueOf(orderId));
                        product.setExceptionTypeSet(business.getException());

                        // set data into property
                        ObjectMapper mapper = new ObjectMapper();
                        exchange.setProperty("orderId", orderId);
                        exchange.setProperty("order", mapper.writeValueAsString(order));
                        exchange.setProperty("account", mapper.writeValueAsString(account));
                        exchange.setProperty("product", mapper.writeValueAsString(product));
                    }
                })
                .log("End of direct:prepare-data with body: ${body}");

        from("direct:remove-holder")
                .id("remove-holder")
                .setProperty("holderId", header("holderId"))
                .to("direct:clear-order-holder")
                .to("direct:clear-account-holder")
                .to("direct:clear-product-holder")
                .end();

        from("direct:clear-order-holder")
                .id("clear-order-holder")
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.DELETE))
                .serviceCall(ORDER_SERVICE + "/order/clearHolder/${exchangeProperty.holderId}");


        from("direct:clear-account-holder")
                .id("clear-account-holder")
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.DELETE))
                .serviceCall(ACCOUNT_SERVICE + "/account/clearHolder/${exchangeProperty.holderId}");

        from("direct:clear-product-holder")
                .id("clear-product-holder")
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.DELETE))
                .serviceCall(PRODUCT_SERVICE + "/product/clearHolder/${exchangeProperty.holderId}");

    }
}
