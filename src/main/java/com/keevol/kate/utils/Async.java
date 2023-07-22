package com.keevol.kate.utils;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

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
public class Async {

    private static Logger logger = LoggerFactory.getLogger(Async.class);
    private static AtomicLong counter = new AtomicLong(0);

    public static void run(RoutingContext ctx, Consumer<RoutingContext> consumer) {
        run(() -> consumer.accept(ctx));
    }

    public static void run(Runnable r) {
        Thread.ofVirtual().name("Async runner virtual thread: " + counter.getAndIncrement()).uncaughtExceptionHandler((Thread t, Throwable e) -> {
            logger.warn("uncaughtException in virtual thread:{} from kate Async context: {}", t.getName(), ExceptionUtils.getStackTrace(e));
        }).start(r);
    }

    // syntax sugar of scala will make this easy: Async.apply(r:=>Unit)
    public static void main(String[] args) {
        Handler<RoutingContext> handler = (RoutingContext ctx) -> {
            Async.run(() -> System.out.println("do anything with the routing context which will be run on virtual thread automatically."));
            // with scala that may be:
            // Async {
            //  do anything with ctx.
            // }
        };
    }
}
