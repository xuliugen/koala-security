<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@include file="/commons/taglibs.jsp"%>
<!DOCTYPE html>
<html lang="en-ZH">
<head>
    <title>资产中心</title>
    <%@include file="/commons/statics.jsp"%></head>
<body>
<div class="btn-group">
    <ks:hasSecurityResource identifier="assetCenterInWarehouse">
        <button class="btn btn-success">入库</button>
    </ks:hasSecurityResource>

    <ks:hasSecurityResource identifier="assetCenterOutWarehouse">
        <button class="btn btn-success">出库</button>
    </ks:hasSecurityResource>

    <ks:hasSecurityResource identifier="assetCenterDisabled">
        <button class="btn btn-danger">停用</button>
    </ks:hasSecurityResource>

    <ks:hasSecurityResource identifier="assetCenterRepair">
        <button class="btn btn-info">维修</button>
    </ks:hasSecurityResource>

    <%--<!--所有角色才显示入库按钮-->--%>
    <%--<ks:hasRole ifAllRoles="超级管理员,仓管管理员">--%>
        <%--<button class="btn btn-success">入库</button>--%>
    <%--</ks:hasRole>--%>
    <%--<!--任何一个角色就可以显示出库按钮-->--%>
    <%--<ks:hasRole ifAnyRoles="超级管理员,仓管管理员" >--%>
        <%--<button class="btn btn-success">出库</button>--%>
    <%--</ks:hasRole>--%>
    <%--<!--没有任何一个角色就显示停用按钮-->--%>
    <%--<ks:hasRole ifNotRoles="仓管管理员" >--%>
        <%--<button class="btn btn-danger">停用</button>--%>
    <%--</ks:hasRole>--%>
        <%--<button class="btn btn-info">维修</button>--%>

    <ks:hasPermission ifAllPermissions="assetCenterInPermission,assetCenterOutPermission,assetCenterDisabledPermission,assetCenterPermission">
        <button class="btn btn-success">入库</button>
    </ks:hasPermission>
    <ks:hasPermission ifAnyPermissions="assetCenterOutPermission,assetCenterPermission">
        <button class="btn btn-success">出库</button>
    </ks:hasPermission>
    <ks:hasPermission ifNotPermissions="assetCenterDisabledPermission">
        <button class="btn btn-danger">停用</button>
    </ks:hasPermission>
    <button class="btn btn-info">维修</button>

    </div>
</body>
</html>