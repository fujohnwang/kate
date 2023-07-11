package com.keevol.kate;

import com.keevol.kate.templates.jte.JteTemplateEngineFactory;
import com.keevol.kate.templates.jte.JteTemplateUtils;
import com.keevol.kate.utils.ResponseUtils;
import gg.jte.TemplateEngine;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
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

    protected RouteRegister[] routeRegisters;
    protected Vertx vertx;
    protected HttpServer httpServer;

    protected Boolean implicitVertxCreated = false;
    protected AtomicBoolean running = new AtomicBoolean(false);
    protected AtomicLong virtualThreadCounter = new AtomicLong(0);

    private String fileLocation = "kate-uploads";


    public Kate(RouteRegister[] routeRegisters) {
        this(Vertx.vertx(), routeRegisters);
        this.implicitVertxCreated = true;
    }

    public Kate(Vertx vertx, RouteRegister[] routeRegisters) {
        this.vertx = vertx;
        this.routeRegisters = routeRegisters;
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
        preparePreflightRoutes(router);
        for (RouteRegister routeRegister : this.routeRegisters) {
            routeRegister.apply(router);
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
        router.route().handler(CorsHandler.create());
        router.route().handler(ctx -> {
            if (ctx.request().method().equals(HttpMethod.OPTIONS)) {
                // CORS preflight request
                ctx.response().end();
            } else {
                ctx.next();
            }
        });

        // handle body parse or file upload
        File uploadDir = new File(fileLocation);
        if (!uploadDir.exists()) {
            if (!uploadDir.mkdirs()) {
                throw new RuntimeException("fails to create file uploads directory at: " + uploadDir.getAbsolutePath());
            }
        }
        router.route().handler(BodyHandler.create(true).setBodyLimit(5 * 1024 * 1024L).setUploadsDirectory(fileLocation));
    }

    public static void main(String[] args) throws Throwable {

        TemplateEngine te = JteTemplateEngineFactory.apply();

        RouteRegister rr = new RouteRegister() {
            @Override
            public void apply(Router router) {
                router.get("/").handler(ctx -> ctx.response().end("DONE!"));
                router.get("/page").handler(ctx -> {
                    Map<String, Object> templateContext = new HashMap<>();
                    templateContext.put("message", "Hello, Kate.");
                    ResponseUtils.html(ctx, JteTemplateUtils.merge(te, "sample.jte", templateContext), 200);
                });
            }

        };

        Kate kate = new Kate(new RouteRegister[]{
                rr
        });
        kate.start("localhost", 9999);
        System.in.read();
        kate.stop();

    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }
}
