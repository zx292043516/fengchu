package com.fc.common.interceptor;

import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.fc.business.consts.Rediskey;
import com.fc.business.dao.ApiAuthInfoMapper;
import com.fc.business.dao.ApiCallInfoMapper;
import com.fc.business.entity.ApiAuthInfo;
import com.fc.business.entity.ApiCallInfo;
import com.fc.common.service.RedisService;

public class ApiInterceptor implements HandlerInterceptor {

	public static Logger log = LoggerFactory.getLogger(ApiInterceptor.class);

	@Autowired
	RedisService redisService;
	@Autowired
	ApiAuthInfoMapper apiAuthInfoMapper;
	@Autowired
	ApiCallInfoMapper apiCallInfoMapper;

	@Value("${fc.domain}")
	private String domain;

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest reqeust, HttpServletResponse response, Object arg2, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		String referer = request.getHeader("referer");
		String appId = request.getParameter("appId");
		String sign = request.getParameter("sign");

		if (referer != null && referer.startsWith(domain)) {// 本域名访问
			appId = "100000";
		}

		boolean isAuth = false;

		if (StringUtils.isEmpty(appId)) {
			PrintWriter printWriter = response.getWriter();
			printWriter.write("{message:\"please get auth info!\"}");
			return false;
		}
		if (!appId.equals("100000")) {
			// 从缓存种查找数据
			String authRedisKey = String.format(Rediskey.API_AUTH_INFO, appId);
			if (redisService.exists(authRedisKey)) {
				String value = (String) redisService.get(authRedisKey);
				if (!StringUtils.isEmpty(value)) {
					if (value.toLowerCase().equals(sign.toLowerCase())) {
						isAuth = true;
					}
				}
			}

			if (!isAuth) {// 从数据库中匹配
				ApiAuthInfo authInfo = apiAuthInfoMapper.selectByPrimaryKey(Integer.parseInt(appId));
				if (authInfo != null && authInfo.getSign().toLowerCase().equals(sign.toLowerCase())) {
					isAuth = true;
					redisService.set(authRedisKey, authInfo.getSign().toLowerCase(), Rediskey.TIME_DAY);
				}
			}
		}

		if (!isAuth && !appId.equals("100000")) {
			PrintWriter printWriter = response.getWriter();
			printWriter.write("{message:\"please get auth info!\"}");
			return false;
		}

		// 统计调用次数
		String getRequestURI = request.getRequestURI();
		String callRedisKey = String.format(Rediskey.API_CALL_INFO, appId, getRequestURI);
		Long times = redisService.incr(callRedisKey);
		System.out.println(appId + "调用" + getRequestURI + "次数:" + times + "保存" + (times % 100 == 0));
		if (times % 100 == 0) {// 每100次保存数据库
			ApiCallInfo record = new ApiCallInfo();
			record.setAppId(Integer.parseInt(appId));
			record.setApiName(getRequestURI);
			ApiCallInfo bean = apiCallInfoMapper.selectByPrimaryKey(record);
			if (bean != null) {
				bean.setCalls(times);
				bean.setReferer(referer);
				bean.setUpdateTime(new Date());
				apiCallInfoMapper.updateByPrimaryKeySelective(bean);
			} else {
				record.setCalls(times);
				record.setReferer(referer);
				record.setUpdateTime(new Date());
				record.setCreateTime(new Date());
				apiCallInfoMapper.insertSelective(record);
			}

		}
		return true;

	}

}
