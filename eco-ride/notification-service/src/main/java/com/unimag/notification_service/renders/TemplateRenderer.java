package com.unimag.notification_service.renders;


import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateRenderer {
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\{([a-zA-Z0-9_]+)\\}");

    /**
     * Renderiza templateBody reemplazando {key} por vars.get(key).toString().
     * Si el valor es null o no existe, se reemplaza por "" (vacío).
     */
    public String render(String templateBody, Map<String, Object> vars) {
        if (templateBody == null) return "";

        Matcher m = TOKEN_PATTERN.matcher(templateBody);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String key = m.group(1);
            Object value = vars != null ? vars.get(key) : null;
            String replacement = value != null ? escapeForPlainText(value.toString()) : "";
            // escape backslashes/dollars for Matcher.appendReplacement
            replacement = Matcher.quoteReplacement(replacement);
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * Evitar caracteres problemáticos en el body final.
     * Si vas a enviar HTML por email, reemplaza/escapa apropiadamente.
     */
    private String escapeForPlainText(String s) {
        // por ahora solo trim, puedes añadir sanitización si necesario
        return s == null ? "" : s.trim();
    }
}
