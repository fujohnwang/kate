package com.keevol.kate.handlers;

import com.keevol.kate.utils.ResponseUtils;
import gg.jte.TemplateEngine;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Map;

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
public class SamplePageHandler extends AbstractPageHandler {

    public SamplePageHandler(TemplateEngine te) {
        super(te);
    }


    @Override
    public String route() {
        return "/page";
    }

    @Override
    public void handle(RoutingContext ctx) {
        Map<String, Object> templateContext = new HashMap<>();
        templateContext.put("message", "Hello, Kate.");
        ResponseUtils.html(ctx, render("sample.jte", templateContext), 200);
    }
}
