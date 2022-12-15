package com.keevol.kate;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

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
public class Kate {
    protected Logger logger = LoggerFactory.getLogger(Kate.class);

    protected KateHandler[] handlers;
    protected Vertx vertx;
    protected HttpServer httpServer;

    protected Boolean implicitVertxCreated = false;
    protected Boolean clearPreflightRoutes = false;
    protected AtomicBoolean running = new AtomicBoolean(false);
    protected AtomicLong virtualThreadCounter = new AtomicLong(0);

    public Kate(KateHandler[] handlers) {
        this.vertx = Vertx.vertx();
        this.implicitVertxCreated = true;
        this.handlers = handlers;
    }

    public Kate(Vertx vertx, KateHandler[] handlers) {
        this.vertx = vertx;
        this.handlers = handlers;
    }

    public Future<HttpServer> start(String host, int port) {
        if (running.compareAndSet(false, true)) {
            return doStart(host, port);
        } else {
            logger.info("Kate server has been running...");
            return Future.succeededFuture();
        }
    }

    protected Future<HttpServer> doStart(String host, int port) {
        Router router = Router.router(vertx);
        if (!clearPreflightRoutes) {
            preparePreflightRoutes(router);
        }
        for (KateHandler handler : this.handlers) {
            router.route(handler.route()).handler(ctx -> {
                Thread.ofVirtual().name("kate handler thread: " + virtualThreadCounter.getAndIncrement()).uncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        logger.warn("uncaught exception in thread: " + t.getName() + ":\n" + e.getMessage());
                    }
                }).start(() -> {
                    handler.handle(ctx);
                });
            });
        }
        this.httpServer = vertx.createHttpServer();
        return httpServer.requestHandler(router).listen(port, host);
    }


    public void stop() {
        if (running.compareAndSet(true, false)) {
            doStop();
            if (implicitVertxCreated) {
                this.vertx.close();
            }
        } else {
            logger.info("http server had been shutdown.");
        }
    }

    protected Future<Void> doStop() {
        return httpServer.close();
    }

    protected void preparePreflightRoutes(Router router) {
        // handle CORS issue
        router.route().handler(CorsHandler.create("*").
                allowedMethod(HttpMethod.POST).
                allowedMethod(HttpMethod.GET).
                allowedMethod(HttpMethod.OPTIONS).
                allowedHeaders(new java.util.HashSet<String>(java.util.Arrays.asList("Token", "Authorization", "Content-Type", "User-Agent", "If-Modified-Since", "Cache-Control", "Range"))).
                exposedHeaders(new java.util.HashSet<String>(java.util.Arrays.asList("Content-Length", "Content-Range"))));

        router.route().handler(ctx -> {
            if (ctx.request().method().equals(HttpMethod.OPTIONS)) {
                // CORS preflight request
                ctx.response().end();
            } else {
                ctx.next();
            }
        });
        // handle body parse or file upload
        router.route().handler(BodyHandler.create(true).setBodyLimit(5 * 1024 * 1024L));
    }


    public Boolean getClearPreflightRoutes() {
        return clearPreflightRoutes;
    }

    public void setClearPreflightRoutes(Boolean clearPreflightRoutes) {
        this.clearPreflightRoutes = clearPreflightRoutes;
    }

    public static void main(String[] args) throws Throwable {
        KateHandler sampleHandler = new KateHandler() {

            @Override
            public void handle(RoutingContext ctx) {
                ctx.response().end("DONE!");
            }

            @Override
            String route() {
                return "/";
            }
        };
        Kate kate = new Kate(new KateHandler[]{sampleHandler});
        kate.start("localhost", 9999);
        System.in.read();
        kate.stop();

    }
}
