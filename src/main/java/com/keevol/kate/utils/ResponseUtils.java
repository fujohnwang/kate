package com.keevol.kate.utils;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

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
public class ResponseUtils {

    public static int DEFAULT_OK_STATUS_CODE = 200;
    public static int DEFAULT_REDIRECT_STATUS_CODE = 302;

    public static void redirect(RoutingContext ctx, String toLocation) {
        redirect(ctx, toLocation, DEFAULT_REDIRECT_STATUS_CODE);
    }

    public static void redirect(RoutingContext ctx, String toLocation, int statusCode) {
        ctx.response().setStatusCode(statusCode);
        ctx.response().putHeader(HttpHeaders.LOCATION.toString(), toLocation);
        ctx.response().end();
    }

    public static void html(RoutingContext ctx, String htmlContent, int statusCode) {
        ctx.response().setStatusCode(statusCode);
        ctx.response().putHeader("Content-Type", "text/html; charset=utf-8");
        ctx.response().end(htmlContent);
    }

    public static void json(RoutingContext ctx, JsonObject json, int statusCode) {
        ctx.response().setStatusCode(statusCode);
        ctx.response().putHeader(HttpHeaders.CONTENT_TYPE.toString(), "application/json");
        ctx.response().end(json.encode());
    }

    public static void error(RoutingContext ctx, String message, int statusCode) {
        JsonObject json = new JsonObject();
        json.put("message", message);
        json(ctx, json, statusCode);
    }

    public static void fail(RoutingContext ctx, String message) {
        error(ctx, message, 500);
    }

    public static void fail(RoutingContext ctx) {
        ctx.response().setStatusCode(500);
        ctx.response().end();
    }

}
