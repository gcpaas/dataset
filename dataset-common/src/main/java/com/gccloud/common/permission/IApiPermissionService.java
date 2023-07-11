package com.gccloud.common.permission;


import javax.servlet.http.HttpServletRequest;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/5/15 10:38
 */
public interface IApiPermissionService {

     /**
     * 校验接口权限
     * @param request 请求
     * @param permission 权限列表
     * @return 是否有权限访问
     */
    boolean verifyApiPermission(HttpServletRequest request, String... permission);


}
