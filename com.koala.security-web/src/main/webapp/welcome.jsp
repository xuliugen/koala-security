<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <title></title>
    <style>
        ul,li{list-style:none;margin:0;padding:0;}
        .box{ width: 800px; margin: auto;}
        .nav_link{ margin-top: 15px; overflow: hidden;}
        .nav_link span{float: left; line-height: 30px; font-size: 20px; font-weight:bold;}
        .nav_link li{ float: left; margin-right: 25px; margin-left:15px; border-radius:15px; width: 130px; height: 30px; background-color: #555; line-height: 30px; text-align: center;}
        .nav_link li a{ color: #fff; text-decoration: none;}
        .nav_link li a:hover{ color:#84ff00;}
        .main_ten{overflow: hidden; zoom:1; margin-top: 50px;}
        .main_ten li{ float: left; border-right:1px solid #ccc; width: 230px; height: 260px; padding: 20px 15px; text-align: center;}
        .main_ten li.last{border: none;}
        .main_ten li p{ margin-top: 10px; line-height: 25px;}
    </style>
</head>
<body>
<div class="box">
    <div class="nav_link">
        <ul>
            <span>快速导航：</span>
            <li><a href="http://openkoala.org/" target="_blank">Koala官网</a></li>
            <li><a href="http://dev.openkoala.org/?/article/4" target="_blank">开发文档</a></li>
            <li><a href="http://dev.openkoala.org/" target="_blank">开发者社区</a></li>
            <li><a href="http://dev.openkoala.org/?/explore/category-4" target="_blank">提交BUG</a></li>
        </ul>
    </div>
    <div class="main_ten">
        <h4>核心功能：</h4>
        <ul>
            <li><img src="images/openci_icon.png"/><p>向导式创建、导入项目，集成版本控制 (GIT/SVN)、项目管理(Trac/Redmine)、代码质量(Sonar)、持续集成(Jenkins)</p></li>
            <li><img src="images/koalaui_icon.png"/><p>基于Bootstrap3、基于开源的或定制实现的JS组件，简化前端开发。</p></li>
            <li class="last"><img src="images/lyjm_icon.png"/><p>以业务分析和领域建模为出发点和依归自动生成领域类，消除问题域与解决方案域之间的鸿沟</p> </li>
        </ul>
    </div>
</div>
</body>
</html>