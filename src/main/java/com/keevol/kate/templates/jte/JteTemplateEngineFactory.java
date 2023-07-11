package com.keevol.kate.templates.jte;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

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
public class JteTemplateEngineFactory {
    protected static final Logger logger = LoggerFactory.getLogger(JteTemplateEngineFactory.class);
    protected static AtomicReference<TemplateEngine> templateEngineHolder = new AtomicReference<>();

    public static TemplateEngine apply() {
        if (templateEngineHolder.get() == null) {
            String profile = System.getProperty("profile");
            if (profile != null && (profile.equalsIgnoreCase("production") || profile.equalsIgnoreCase("prod"))) {
                logger.info("profile=production, create Precompiled TemplateEngine for Jte.");
                templateEngineHolder.set(TemplateEngine.createPrecompiled(Path.of("jte-classes"), ContentType.Html, JteTemplateEngineFactory.class.getClassLoader()));
            } else {
                logger.info("profile is not production, so create hot-reloadable TemplateEngine for Jte in Development phase.");
                DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(Path.of("src", "main", "jte"));
                templateEngineHolder.set(TemplateEngine.create(codeResolver, ContentType.Html));
            }
        }
        return templateEngineHolder.get();
    }

    public static TemplateEngine create() {
        return apply();
    }
}
