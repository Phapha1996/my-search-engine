package org.fage.mysearchengine;

import org.fage.mysearchengine.entity.Article;
import org.fage.mysearchengine.entity.User;
import org.fage.mysearchengine.spider.Spiders;
import org.fage.mysearchengine.web.repository.ArticleRepository;
import org.fage.mysearchengine.web.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTests {
	@Autowired
	UserRepository userRepository;
	@Autowired
	ArticleRepository articleRepository;
	@Autowired
	Spiders spiders;

	private final static Logger logger = LoggerFactory.getLogger(AppTests.class);

	@Test
	public void contextLoads() {
		User u = new User();
		u.setUsername("haha");
		userRepository.save(u);
	}

	@Test
	public void testUserSpider(){
		logger.info("用户爬虫启动中...");
		long startTime, endTime;
		startTime = System.currentTimeMillis();

		try {
			spiders.csdnSpider(false, 10);
		} catch (Exception e) {
			e.printStackTrace();
		}

		endTime = System.currentTimeMillis();
		logger.info("爬取结束，耗时约" + ((endTime - startTime) / 1000) + "秒");
	}

	/**
	 * 测试堆溢出问题
	 * @throws InterruptedException
	 */
	@Test
	public void test() throws InterruptedException {
		int i = 0;
		List<Article> articles = null;
		while (true) {
//			Thread.sleep(3000);
			i++;
			logger.info("第" + i + "次");
			articles = articleRepository.findTopByLimitProperties(0, 1000);
			//doOtherThings
			articles.clear();
		}
	}
}
