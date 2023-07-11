package com.keevol.kate;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {{{
 * _                                    _
 * | |                                  | |
 * | | __   ___    ___  __   __   ___   | |
 * | |/ /  / _ \  / _ \ \ \ / /  / _ \  | |
 * |   <  |  __/ |  __/  \ V /  | (_) | | |
 * |_|\_\  \___|  \___|   \_/    \___/  |_|
 * }}}
 * <p>
 * KEEp eVOLution!
 *
 * @author fq@keevol.com
 * @since 2017.5.12
 * <p>
 * Copyright 2017 © 杭州福强科技有限公司版权所有
 * [[https://www.keevol.com]]
 */
public class AsyncHandler implements Handler<RoutingContext> {

    protected Logger logger = LoggerFactory.getLogger(AsyncHandler.class);

    private Handler<RoutingContext> handler;

    public AsyncHandler(Handler<RoutingContext> handler) {
        this.handler = handler;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        Thread.ofVirtual()
                .name("AsyncHandler virtual thread")
                .uncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        logger.warn("uncaughtException in virtual thread:{} from kate web handler: {}", t.getName(), ExceptionUtils.getStackTrace(e));
                    }
                })
                .start(() -> handler.handle(routingContext));
    }


    public static void main(String[] args) {
        new AsyncHandler(ctx -> {
            ctx.response().end("demo");
        });
    }
}

