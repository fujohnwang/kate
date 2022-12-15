package com.keevol.kate.templates.jte;

import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;

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
public class JteTemplateUtils {
    public static String merge(TemplateEngine templateEngine, String jteTemplateFileName, Map<String, Object> model) {
        StringOutput output = new StringOutput();
        templateEngine.render(jteTemplateFileName, model, output);
        return output.toString();
    }

}
