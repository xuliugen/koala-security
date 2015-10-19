<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@include file="/commons/taglibs.jsp"%>
<!DOCTYPE html>
<html lang="en-ZH">
<head>
    <title>资产归还</title>
    <%@include file="/commons/statics.jsp"%>
</head>
<body>
    <ks:hasSecurityResource identifier="assetBorrowRestitution">
        <button class="btn btn-info">归还</button>
    </ks:hasSecurityResource>
</body>
</html>