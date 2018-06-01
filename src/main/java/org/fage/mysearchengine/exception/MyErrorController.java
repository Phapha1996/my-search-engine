package org.fage.mysearchengine.exception;

import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午12:12 2018/6/1
 * @description http状态显示控制器
 **/
@Controller
public class MyErrorController extends BasicErrorController {
    public MyErrorController(ServerProperties serverProperties) {
        super(new DefaultErrorAttributes(), serverProperties.getError());
    }

    /**
     * 覆盖默认的Json响应
     */
    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request,
                isIncludeStackTrace(request, MediaType.ALL));
        HttpStatus status = getStatus(request);

        //输出自定义的Json格式
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", false);
        map.put("msg", body.get("message"));

        return new ResponseEntity<Map<String, Object>>(map, status);
    }

    /**
     * 覆盖默认的HTML响应
     */
    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        //请求的状态
        HttpStatus status = getStatus(request);
        response.setStatus(getStatus(request).value());
        Map<String, Object> model = getErrorAttributes(request,
                isIncludeStackTrace(request, MediaType.TEXT_HTML));
        if(status == HttpStatus.INTERNAL_SERVER_ERROR){
            model.put("emsg", "500-服务器开了个小差...");
        }
        if(status == HttpStatus.NOT_FOUND){
            model.put("emsg", "404-网页小哥泡妹去了...");
        }
        ModelAndView modelAndView = resolveErrorView(request, response, status, model);
        //指定自定义的视图
        return(modelAndView == null ? new ModelAndView("error", model) : modelAndView);
    }
}
