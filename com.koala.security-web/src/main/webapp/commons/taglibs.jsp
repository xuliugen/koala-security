<%-- 使用这种方式的注解可以避免IE 兼容性问题--%>
<%@ page import="java.util.Date" %>
<%-- 引入所有得标签库 TagLib 并且设置上下文路径 --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ks" uri="http://www.openkoala.org/security" %>

<%-- 放置contextPath作用，其他得页面直接可以拿到放置contextPath，不需要再重复定义一个--%>
<%pageContext.setAttribute("contextPath", request.getContextPath());%>

<%-- 放置formId 作用，因为界面操作可能会打开同一个页面多次，导致formId重复，因此采用时间戳。--%>
<%pageContext.setAttribute("formId", "formId_" + new Date().getTime());%>