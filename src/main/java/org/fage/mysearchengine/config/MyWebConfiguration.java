package org.fage.mysearchengine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 
 * @author Caizhfy
 * @email caizhfy@163.com
 * @createTime 2017年12月6日
 * @description 常用web请求映射
 *
 */
@Configuration
public class MyWebConfiguration extends WebMvcConfigurerAdapter {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		/**
		 * 这样的话如果要拿index.html只需要访问" 127.0.0.1:8080/${context}/index.html "
		 */
//		registry.addResourceHandler("/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/templates/");
		/**
		 * 如果需要拿图片，图片路径就写" ./static/images/1.png "就可以相当于 "
		 * 127.0.0.1/${context}/static/images/1.png "
		 * 
		 */
//		registry.addResourceHandler("/static/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/");
		super.addResourceHandlers(registry);
		registry.addResourceHandler("/images/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/images/");
		registry.addResourceHandler("/css/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/css/");
		registry.addResourceHandler("/js/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/js/");
		super.addResourceHandlers(registry);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 拦截规则：除了login，其他都拦截判断
		/*
		 * registry.addInterceptor(new
		 * LoginInterceptor()).addPathPatterns("/**")
		 * .excludePathPatterns("/usr/signin")
		 * .excludePathPatterns("/login.html");
		 */
		super.addInterceptors(registry);
	}

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
	    registry.addViewController("/").setViewName("index");//主页面
        super.addViewControllers(registry);
    }

//    /**
//     * 设置视图解析器
//     * @param templateEngine
//     * @return
//     */
//    @Bean
//    public ViewResolver viewResolver(SpringTemplateEngine templateEngine){
//        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
//        resolver.setTemplateEngine(templateEngine);
//        return resolver;
//    }
//
//    /**
//     * 设置模板引擎
//     * @param templateResolver
//     * @return
//     */
//    @Bean
//    public SpringTemplateEngine templateEngine(TemplateResolver templateResolver){
//        SpringTemplateEngine engine = new SpringTemplateEngine();
//        engine.setTemplateResolver(templateResolver);
//        return engine;
//    }

}
