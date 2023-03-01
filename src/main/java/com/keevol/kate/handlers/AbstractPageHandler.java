package com.keevol.kate.handlers;

import com.keevol.kate.KateHandler;
import com.keevol.kate.templates.jte.JteTemplateUtils;
import gg.jte.TemplateEngine;

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
public abstract class AbstractPageHandler extends KateHandler {

    protected TemplateEngine templateEngine;

    public AbstractPageHandler(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String render(String templateFile, Map<String, Object> model) {
        return JteTemplateUtils.merge(templateEngine, templateFile, model);
    }
}
