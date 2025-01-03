package org.barrysen.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Joiner;
import org.apache.shiro.SecurityUtils;
import org.barrysen.exception.BaseException;
import org.barrysen.vo.LoginUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * 功能：JWT工具类
 *
 * @author: Barrysen
 * @date: 2025/1/3 13:41
 */
public class JwtUtil {

	// 用户签名过期时间，用于登录校验
	public static final long EXPIRE_TIME = 30 * 1000L;

	/**
	 * 校验token是否正确
	 *
	 * @param token  密钥
	 * @param secret 用户的密码
	 * @return 是否正确
	 */
	public static boolean verify(String token, String username, Integer tenantId,String secret) {
		try {
			// 根据密码生成JWT效验器
			Algorithm algorithm = Algorithm.HMAC256(secret);
			JWTVerifier verifier = JWT.require(algorithm).withClaim("username", username).withClaim("tenantId", tenantId).build();
			// 效验TOKEN
			DecodedJWT jwt = verifier.verify(token);
			return true;
		} catch (Exception exception) {
			return false;
		}
	}

	/**
	 * 获得token中的信息无需secret解密也能获得
	 *
	 * @return token中包含的用户名
	 */
	public static String getUsername(String token) {
		try {
			DecodedJWT jwt = JWT.decode(token);
			return jwt.getClaim("username").asString();
		} catch (JWTDecodeException e) {
			return null;
		}
	}

	/**
	 * 通过 token解析当前登录用户信息
	 * @param token
	 * @return
	 */
	public static LoginUser getLoginUser(String token) {
		LoginUser user = new LoginUser();
		String userName = null;
		Integer tenantId = null;
		try {
			DecodedJWT jwt = JWT.decode(token);
			userName =  jwt.getClaim("username").asString();
			tenantId = jwt.getClaim("tenantId").asInt();
			user.setUsername(userName);
			user.setTenantId(tenantId);
		} catch (JWTDecodeException e) {
			return user;
		}
		return user;
	}

	/**
	 * 生成签名,5min后过期
	 *
	 * @param username 用户名
	 * @param tenantId 租户Id
	 * @param secret   用户的密码
	 * @return 加密的token
	 */
	public static String sign(String username, Integer tenantId, String secret) {
		Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
		Algorithm algorithm = Algorithm.HMAC256(secret);
		// 附带username信息
		return JWT.create().withClaim("username", username).withClaim("tenantId", tenantId).withExpiresAt(date).sign(algorithm);

	}

	/**
	 * 根据request中的token获取用户账号
	 *
	 * @param request
	 * @return
	 * @throws BaseException
	 */
	public static String getUserNameByToken(HttpServletRequest request) throws BaseException {
		String accessToken = request.getHeader("X-Access-Token");
		String username = getUsername(accessToken);
		if (ObjectConvertUtils.isEmpty(username)) {
			throw new BaseException("未获取到用户");
		}
		return username;
	}

	/**
	  *  从session中获取变量
	 * @param key
	 * @return
	 */
	public static String getSessionData(String key) {
		//${myVar}%
		//得到${} 后面的值
		String moshi = "";
		if(key.indexOf("}")!=-1){
			 moshi = key.substring(key.indexOf("}")+1);
		}
		String returnValue = null;
		if (key.contains("#{")) {
			key = key.substring(2,key.indexOf("}"));
		}
		if (ObjectConvertUtils.isNotEmpty(key)) {
			HttpSession session = SpringContextUtils.getHttpServletRequest().getSession();
			returnValue = (String) session.getAttribute(key);
		}
		//结果加上${} 后面的值
		if(returnValue!=null){returnValue = returnValue + moshi;}
		return returnValue;
	}


}
