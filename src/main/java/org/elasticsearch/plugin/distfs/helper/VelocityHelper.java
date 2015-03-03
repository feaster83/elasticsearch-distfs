package org.elasticsearch.plugin.distfs.helper;

import lombok.SneakyThrows;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;

public final class VelocityHelper {
    final static String TEMPLATES_DIR = "templates/velocity/";
    final static VelocityEngine velocityEngine;

    static {
        velocityEngine = new VelocityEngine();
        try {
            velocityEngine.init("config/velocity.properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public static String renderTemplate(String templateName, VelocityContext context) {
        Template template = velocityEngine.getTemplate(TEMPLATES_DIR + templateName);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

}
