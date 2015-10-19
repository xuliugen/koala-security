<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/taglibs.jsp"%>

<!DOCTYPE html>
<html lang="zh-CN">
    <head>
    <%--<%@include file="/commons/metas.jsp"%>--%>
        <title>欢迎使用Koala</title>
        <%@include file="/commons/statics.jsp"%>

<style type="text/css">
*   .* {
	margin: 0;
	padding: 0;
}

body {
	margin: 0;
	padding: 0;
}

.head {
	height: 30px;
	width: 100%;
	background: #f2f2f2;
	padding: 0;
	margin: 0;
}

.logo {
	height: 90px;
	width: 960px;
	margin: 0 auto;
	overflow: hidden;
	clear: both;
}

.logo img {
	height: 90px;
	width: 200px;
	overflow: hidden;
	float: left;
}

.logo div {
	font-size: 24px;
	color: #666;
	height: 40px;
	float: left;
	line-height: 60px;
	margin: 20px 10px;
	padding: 10px;
	border-left: 1px solid #d2d2d2;
}

.login_con {
	width: 960px;
	height: 332px;
	margin: 10px auto;
	clear: both;
}

.login_con_L {
	float: left;
	width: 568px;
	height: 332px;
	overflow: hidden;
}

.login_con_R {
	float: left;
	width: 376px;
	height: 332px;
	border: 1px solid #dce7f4;
}

.login_con_R h4 {
	background: #F2F2F2;
	line-height: 36px;
	width: 376px;
	padding: 0px 6px;
	border: 1px solid #fff;
	border-bottom: 1px solid #d4d4d4;
	margin-top: 0px;
}

.login_con_R  form {
	position: relative;
	padding-top: 10%;
	padding-left: 7%;
	padding-right: 7%;
}
.login_con_R .input-group {
    width: 80%;
    margin-left: auto;
    margin-right: auto;
    margin-bottom: 25px!important;
}
.checkCode {
	position: absolute;
	top: 54%;
	left: 56%;
	height: 50px;
}
.btn-login {
	width: 100%;
	margin-left: auto;
    margin-right: auto;
    font-size: 17px;
    font-weight: bold;
    letter-spacing: 5px;
}

.login_footer {
	clear: both;
	margin: 8% auto 0;
	width: 300px;
	color: inherit;
    font-size: 21px;
    font-weight: 200;
    line-height: 2.14286;
}
</style>
<script type = "text/javascript">
        var contextPath = '${pageContext.request.contextPath}';
</script>
</head>
<% response.setHeader("login","login"); %>
<body>
	<div class="head login"></div>
	<div class="logo">
		<img src="images/background/logo.gif" />
		<div>Koala系统</div>
	</div>
	<div class="login_con">
		<div class="login_con_L">
			<img src="images/background/login_img.gif" />
		</div>
		<div class="login_con_R">
			<h4>登录</h4>
			<FORM id="loginFormId" class="form-horizontal" action="login" method="post">
				<div class="form-group input-group">
                    <span class="input-group-addon"><span class="glyphicon glyphicon-user"></span></span>
                    <input type="text" class="form-control" placeholder="用户名"  name="username" id="j_username" value="koala">
				</div>
                <div class="form-group input-group">
                    <span class="input-group-addon"><span class="glyphicon glyphicon-lock"></span></span>
                    <input type="password" name="password" id="j_password" class="form-control" placeholder="密码" value="888888"/>
                </div>
                
                <c:if test="${!jCaptchaDisabled}">
                	<div class="form-group input-group">
				    	<span class="input-group-addon"><span class="glyphicon glyphicon-magnet"></span></span>
						<input type="text" id="jCaptchaCode"  style="width:50%;" name="jCaptchaCode" value="" class="form-control" placeholder="验证码"  autocomplete="off"/>
						<div style="width:120px;"></div>
					</div>
					<img src="jcaptcha.jpg" id="checkCode" onclick='refreshCode()' class="checkCode"/>
                </c:if>
				
				<div class="form-group input-group" style="margin-top: 45px;">
					<button type="button"  class="btn btn-primary btn-block" id="loginBtn">登录</button>
				</div>
			</FORM>
		</div>
	</div>
	<div class="login_footer">Koala 版权信息 2014</div>
	<script>
    $(function(){
     	var btnLogin = $('#loginBtn');
    	var form = $('#loginFormId');
        $('body').keydown(function(e) {
            if (e.keyCode == 13) {
            	dologin();
            }
        });
        btnLogin.on('click',function() {
        	dologin();
        });
        
	    var dologin = function() {
	        var userNameElement = $("#j_username");
	        var passwordElement = $("#j_password");
	        var username = userNameElement.val();
	        var password = passwordElement.val();
	        if (!Validation.notNull($('body'), userNameElement, username, '用户名不能为空')) {
	            return false;
	        }
	        if (!Validation.notNull($('body'), passwordElement, password, '密码不能为空')) {
	            return false;
	        }
	        btnLogin.attr('disabled', 'disabled').html('正在登录...');
    		var param = form.serialize();
        	$.ajax({
        		url: contextPath+"/login.koala",
        		dataType: "json",
        		data: param,
        		type: "POST",
        		success: function(data){
        			if(data.success){
        				$('.login_con_R').message({
        					type: 'success',
        					content:  '登录成功！'
        				});
        				window.location.href=contextPath+"/index.koala";
        			}else{
        				btnLogin.removeAttr('disabled').html('登录');
        				$('.login_con_R').message({
        					type: 'error',
        					content: data.errorMessage
        				});
        				refreshCode();
        			}
        		}
        	});
		};
		});
		
		function refreshCode() {
			$("#checkCode").attr("src","jcaptcha.jpg?time="+new Date().getTime());
		}
		
	</script>
</body>
</html>
