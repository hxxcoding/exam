package com.yf.exam.modules.shiro;


import com.yf.exam.core.utils.RedisUtil;
import com.yf.exam.modules.shiro.jwt.JwtToken;
import com.yf.exam.modules.shiro.jwt.JwtUtils;
import com.yf.exam.modules.sys.user.dto.response.SysUserLoginDTO;
import com.yf.exam.modules.sys.user.entity.SysMenu;
import com.yf.exam.modules.sys.user.service.SysMenuService;
import com.yf.exam.modules.sys.user.service.SysUserRoleService;
import com.yf.exam.modules.sys.user.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户登录鉴权和获取用户授权
 * @author bool
 */
@Component
@Slf4j
public class ShiroRealm extends AuthorizingRealm {

	@Autowired
	@Lazy
	private SysUserService sysUserService;

	@Autowired
	@Lazy
	private SysUserRoleService sysUserRoleService;

	@Autowired
	@Lazy
	private SysMenuService sysMenuService;

	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof JwtToken;
	}


	/**
	 * 详细授权认证
	 * @param principals
	 * @return
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		String userId = null;
		SysUserLoginDTO user = null;
		if (principals != null) {
			user = (SysUserLoginDTO) principals.getPrimaryPrincipal();
			userId = user.getId();
		}
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

		// 查找用户角色
		List<String> roles = sysUserRoleService.listRoles(userId);
		info.setRoles(new HashSet<>(roles));

		List<SysMenu> menuList = null;
		menuList = sysMenuService.listByRoleIds(roles);
		if (!CollectionUtils.isEmpty(menuList)) {
			Set<String> permissionSet = new HashSet<>();
			for (SysMenu menu : menuList) {
				String permission = null;
				if (!StringUtils.isEmpty(permission = menu.getPerms())) {
					permissionSet.addAll(Arrays.asList(permission.trim().split(",")));
				}
			}
			info.setStringPermissions(permissionSet);
		}

		log.info("++++++++++校验详细权限完成");
		return info;
	}

	/**
	 * 校验用户的账号密码是否正确
	 * @param auth
	 * @return
	 * @throws AuthenticationException
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
		String token = (String) auth.getCredentials();
		if (token == null) {
			throw new AuthenticationException("token为空!");
		}

		log.info("++++++++++校验用户："+token);
		// 校验token有效性
		SysUserLoginDTO user = this.checkToken(token);
		return new SimpleAuthenticationInfo(user, token, getName());
	}


	/**
	 * 校验Token的有效性
	 * @param token
	 * @return
	 * @throws AuthenticationException
	 */
	public SysUserLoginDTO checkToken(String token) throws AuthenticationException {

		// 查询用户信息
		log.debug("++++++++++校验用户token： "+ token);

		// 从token中获取用户名
		String username = JwtUtils.getUsername(token);
		log.debug("++++++++++用户名： "+ username);

		if (username == null) {
			throw new AuthenticationException("无效的token");
		}

		// 从缓存中获取当前登录的用户的token
		Object cacheToken = RedisUtil.get(username);

		if (cacheToken == null) {
			throw new AuthenticationException("登陆失效，请重试登陆!");
		}

		if (!cacheToken.toString().equals(token)) { // 缓存中的token已经被更新
			throw new AuthenticationException("您的账号已在别处登录!");
		}

		// 重新设定expireTime
		RedisUtil.set(username, cacheToken, JwtUtils.getExpireTime());
		// 查找登录用户对象
		SysUserLoginDTO user = sysUserService.token(token);

		// 校验token是否失效
		if (!JwtUtils.verify(token, username)) {
			throw new AuthenticationException("登陆失效，请重试登陆!");
		}

		return user;
	}



	/**
	 * 清除当前用户的权限认证缓存
	 * @param principals
	 */
	@Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

}
