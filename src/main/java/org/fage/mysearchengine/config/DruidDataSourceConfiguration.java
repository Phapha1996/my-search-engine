package org.fage.mysearchengine.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;

/**
 * 
 * @author Caizhfy
 * @email caizhfy@163.com
 * @createTime 2017年12月6日
 * @description druid数据源配置类
 *
 */
@Configuration
@PropertySource(value = { "classpath:druid.properties" })
@ConfigurationProperties( prefix = "spring.datasource", ignoreUnknownFields = true)//有属性不能匹配到声明的域时抛出异常
public class DruidDataSourceConfiguration {
	/**
	 * @ConfigurationProperties 中的prefix为配置中的前缀，这里
	 * 声明所需要注入的属性要跟配置文件里面的属性名字一致，并且生成set／get方法
	 * 就可以注入了，不需要再写@Value(${xxx})这样那么麻烦了
	 */
	private Logger logger = LoggerFactory.getLogger(DruidDataSourceConfiguration.class); 
	private String url;
	private String username;
	private String password;
	private String driverClass;
	private int initialSize;
	private int minIdle;
	private int maxActive;
	private long maxWait;
	private long timeBetweenEvictionRunsMillis;
	private long minEvictableIdleTimeMillis;
	private String validationQuery;
	private boolean testWhileIdle;
	private boolean testOnBorrow;
	private boolean testOnReturn;
	private boolean poolPreparedStatements;
	private int maxPoolPreparedStatementPerConnectionSize;
	private String filters;
	private String connectionProperties;
	
	@Bean     //声明其为Bean实例  
    @Primary  //在同样的DataSource中，首先使用被标注的DataSource (即是当有多个bean实现类的时候，让spring优先选择这个类做实现)
    public DataSource dataSource(){  
        DruidDataSource datasource = new DruidDataSource();  
        //基本  
        datasource.setUrl(url);  
        datasource.setUsername(username);  
        datasource.setPassword(password);  
        datasource.setDriverClassName(driverClass);  
        //configuration  
        datasource.setInitialSize(initialSize);  
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);  
        datasource.setMaxWait(maxWait);  
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);  
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);  
        datasource.setValidationQuery(validationQuery);  
        datasource.setTestWhileIdle(testWhileIdle);  
        datasource.setTestOnBorrow(testOnBorrow);  
        datasource.setTestOnReturn(testOnReturn);  
        datasource.setPoolPreparedStatements(poolPreparedStatements);  
        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);  
        datasource.setConnectionProperties(connectionProperties);
        try {  
            datasource.setFilters(filters);  
        } catch (SQLException e) {  
            logger.error("druid configuration initialization filter", e);  
        }  
        datasource.setConnectionProperties(connectionProperties);  
        return datasource;  
    }  
	
	/**
	 * 定义监控服务器，后台访问地址：/druid/*
	 * 
	 * @return
	 */
	@Bean
	public ServletRegistrationBean statVuewServlet(){
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
		//IP白名单
		servletRegistrationBean.addInitParameter("allow", "111.58.222.27");
		//IP黑名单
		servletRegistrationBean.addInitParameter("deny", "");
		//控制台管理用户
		servletRegistrationBean.addInitParameter("loginUsername", "root");
		servletRegistrationBean.addInitParameter("loginPassword", "root");
		//是否能够重制数据
		servletRegistrationBean.addInitParameter("resetEnable", "false");
		
		return servletRegistrationBean;
	}
	
	/**
	 * 添加过滤器，定义过滤格式
	 * @return
	 */
	@Bean
	public FilterRegistrationBean statFilter(){
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
		//添加过滤规则
		filterRegistrationBean.addUrlPatterns("/*");
		//忽略过滤的格式,这些不需要过滤
		filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
		return filterRegistrationBean;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public int getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public long getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(long maxWait) {
		this.maxWait = maxWait;
	}

	public long getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public long getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public boolean isPoolPreparedStatements() {
		return poolPreparedStatements;
	}

	public void setPoolPreparedStatements(boolean poolPreparedStatements) {
		this.poolPreparedStatements = poolPreparedStatements;
	}

	public int getMaxPoolPreparedStatementPerConnectionSize() {
		return maxPoolPreparedStatementPerConnectionSize;
	}

	public void setMaxPoolPreparedStatementPerConnectionSize(int maxPoolPreparedStatementPerConnectionSize) {
		this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
	}

	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	public String getConnectionProperties() {
		return connectionProperties;
	}

	public void setConnectionProperties(String connectionProperties) {
		this.connectionProperties = connectionProperties;
	}
	
	
}
