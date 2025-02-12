package org.jxy.spring.web.view;

import freemarker.cache.TemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.*;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jxy.spring.exception.ServerErrorException;

import java.io.*;
import java.util.Map;
import java.util.Objects;

/**
 * @date 2025/2/11
 * @package org.jxy.spring.web.mvc
 */
@Slf4j
@RequiredArgsConstructor
public class FreeMarkerViewResolver implements ViewResolver {
	private final String templatePath;
	
	private final String templateEncoding;
	
	private final ServletContext servletContext;
	
	private Configuration configuration;
	
	@Override
	public void init() {
		log.info("init {}, set template path: {}", getClass().getSimpleName(), this.templatePath);
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
		cfg.setOutputFormat(HTMLOutputFormat.INSTANCE);
		cfg.setDefaultEncoding(this.templateEncoding);
		cfg.setTemplateLoader(new ServletTemplateLoader(this.servletContext, this.templatePath));
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
		cfg.setAutoEscapingPolicy(Configuration.ENABLE_IF_SUPPORTED_AUTO_ESCAPING_POLICY);
		cfg.setLocalizedLookup(false);
		
		var ow = new DefaultObjectWrapper(Configuration.VERSION_2_3_32);
		ow.setExposeFields(true);
		cfg.setObjectWrapper(ow);
		this.configuration = cfg;
	}
	
	@Override
	public void render(String viewName, Map<String, Object> model, HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		Template template;
		PrintWriter pw = resp.getWriter();;
		
		try {
			template = configuration.getTemplate(viewName);
		} catch (Exception e) {
			throw new ServerErrorException("View not found: " + viewName);
		}
		
		try {
			template.process(model, pw);
		} catch (TemplateException e) {
			throw new ServerErrorException(e);
		}
		
		pw.flush();
	}
}

@Slf4j
class ServletTemplateLoader implements TemplateLoader {
	private final ServletContext servletContext;
	
	private final String subdirPath;
	
	public ServletTemplateLoader(ServletContext servletContext, String subdirPath) {
		Objects.requireNonNull(servletContext);
		Objects.requireNonNull(subdirPath);
		
		subdirPath = subdirPath.replace('\\', '/');
		if (!subdirPath.endsWith("/")) {
			subdirPath += "/";
		}
		if (!subdirPath.startsWith("/")) {
			subdirPath = "/" + subdirPath;
		}
		this.subdirPath = subdirPath;
		this.servletContext = servletContext;
	}
	
	@Override
	public Object findTemplateSource(String name) throws IOException {
		String fullPath = subdirPath + name;
		
		try {
			String realPath = servletContext.getRealPath(fullPath);
			log.info("load template {}: real path: {}", name, realPath);
			if (realPath != null) {
				File file = new File(realPath);
				if (file.canRead() && file.isFile()) {
					return file;
				}
			}
		} catch (SecurityException ignored) {
		}
		
		return null;
	}
	
	@Override
	public long getLastModified(Object templateSource) {
		if (templateSource instanceof File) {
			return ((File) templateSource).lastModified();
		}
		return 0;
	}
	
	@Override
	public Reader getReader(Object templateSource, String encoding) throws IOException {
		if (templateSource instanceof File) {
			return new InputStreamReader(new FileInputStream((File) templateSource), encoding);
		}
		throw new IOException("File not found.");
	}
	
	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
	}
}
