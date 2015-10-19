<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@include file="/commons/taglibs.jsp"%>
<!DOCTYPE html>
<html lang="en-ZH">
<head>
    <title>资产中心</title>
    <%@include file="/commons/statics.jsp"%>
</head>
<body>
    <ks:hasSecurityResource identifier="assetRestitutionBorrow">
        <button class="btn btn-info">借用</button>
    </ks:hasSecurityResource>
</body>
</html>