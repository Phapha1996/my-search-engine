package org.fage.mysearchengine.config;

import org.fage.mysearchengine.App;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
/**
 * 
 * @author Caizhfy
 * @email caizhfy@163.com
 * @createTime 2017年12月7日
 * @description war包排除tomcat
 *
 */
public class ServletInitializer extends SpringBootServletInitializer{
	/**
	 * 创建ServletInitializer.java，继承SpringBootServletInitializer ，
	 * 覆盖configure()，把启动类Application注册进去。
	 * 外部web应用服务器构建Web Application Context的时候，会把启动类添加进去
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(App.class);
	}
}
