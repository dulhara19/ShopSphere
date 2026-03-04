package com.shopsphere.notification.service;

import com.shopsphere.notification.model.EmailTemplate;
import com.shopsphere.notification.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateService {

    private final EmailTemplateRepository templateRepository;

    /**
     * Render a template by replacing {{variable}} placeholders with actual data
     * (Epic 1.2.3)
     */
    public String renderTemplate(String templateId, Map<String, Object> data) {
        EmailTemplate template = templateRepository.findByName(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found: " + templateId));
        return renderContent(template.getHtmlContent(), data);
    }

    /**
     * Get the subject from a template with variable substitution
     */
    public String renderSubject(String templateId, Map<String, Object> data) {
        EmailTemplate template = templateRepository.findByName(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found: " + templateId));
        return renderContent(template.getSubject(), data);
    }

    /**
     * Preview a template with sample data (Epic 1.2.4)
     */
    public String previewTemplate(String templateId, Map<String, Object> sampleData) {
        return renderTemplate(templateId, sampleData);
    }

    /**
     * Replace {{variable}} placeholders and handle {{#if variable}}...{{/if}}
     * conditionals (Epic 1.2.3)
     */
    private String renderContent(String content, Map<String, Object> data) {
        if (content == null || data == null)
            return content;

        // Handle conditionals: {{#if variableName}}...content...{{/if}}
        Pattern conditionalPattern = Pattern.compile("\\{\\{#if\\s+(\\w+)\\}\\}(.*?)\\{\\{/if\\}\\}", Pattern.DOTALL);
        Matcher conditionalMatcher = conditionalPattern.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (conditionalMatcher.find()) {
            String varName = conditionalMatcher.group(1);
            String innerContent = conditionalMatcher.group(2);
            Object value = data.get(varName);
            boolean truthy = value != null && !"false".equals(value.toString()) && !"".equals(value.toString());
            conditionalMatcher.appendReplacement(sb, truthy ? Matcher.quoteReplacement(innerContent) : "");
        }
        conditionalMatcher.appendTail(sb);
        content = sb.toString();

        // Replace {{variable}} placeholders
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            content = content.replace("{{" + entry.getKey() + "}}",
                    entry.getValue() != null ? entry.getValue().toString() : "");
        }

        return content;
    }
}
