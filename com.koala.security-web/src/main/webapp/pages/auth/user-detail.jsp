<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="userDetial" id="userDetial">
<!--
	========================用户详细信息=========================
	 -->
	    <table class="table table-bordered table-hover">
	    <tr>
	    		<td colspan="4">
	    			<label style="font-size:20px;">①用户详细信息</label>
	    		</td>
	    </tr>
		<tr>
			<td width="50%" colspan="2">
			  <label class="control-label">用户名称：</label>
	          <span class="" data-id="name" ></span>
			</td>
			<td width="50%" colspan="2">
			   <label class="control-label">用户账号：</label>
	            <span class="" data-id="userAccount" ></span>
			</td>
		</tr>
		<tr>
			<td width="50%" colspan="2">
			    <label>创建时间：</label>
	            <span data-id="createDate"></span>
			</td>
			<td width="50%" colspan="2">
                <label>描述：</label>
                <span data-id="description" ></span>
            </td>
		</tr>
		<tr>
			<td colspan="4">
			    <label>是否可用：</label>
	            <span data-id="disabled"></span>
			</td>
		</tr>
	</table>
	<!-- 
	========================所有角色=========================
	 -->
	<table id="userDetialtoRoles" class="table table-bordered table-hover">
		<tr>
			<td colspan="2">
		    			<label style="font-size:20px; ">②所有角色</label>
		    </td>
		</tr>
		<tr>
			<td width="50%"> <label>角色名称</label></td>
			<td width="50%"><label>角色描述</label></td>
		</tr>
	</table>
	<!-- 
	========================所有权限=========================
	 -->
	<table id="userDetialtoPermissions"  class="table table-bordered table-hover">
		<tr>
			<td colspan="2"><label style="font-size:20px;">③所有权限</label></td>
		</tr>
		<tr>
			<td width="50%"><label>权限名称</label></td>
			<td width="50%"><label>权限描述</label></td>
		</tr>
	</table>

</div>
<script>
$(function() {
	var userId = $('.userDetial').parent().attr('data-value');
    $.get(contextPath + '/auth/user/findInfoOfUser.koala?userId=' + userId).done(function (result) {
		var user = result.data;
		var userDetial = $('.userDetial');
		userDetial.find('[data-id="name"]').text(user.name);
		userDetial.find('[data-id="userAccount"]').text(user.userAccount);
		userDetial.find('[data-id="createDate"]').text(user.createDate);
		userDetial.find('[data-id="description"]').text(user.description==null?"":user.description);
		userDetial.find('[data-id="disabled"]').text(user.disabled?"不可用":"可用");
		/* ↓============迭代角色数据============↓ */
		var roles = result.data.roles;
		$.each(roles, function(){
			 var $tr = $('<tr><td width="50%"><div>'+this.name+'</div></td><td width="50%"><div>'+this.description+'</div></td></tr>');
             $tr.appendTo($("#userDetialtoRoles"));
		});
		/* ↓============迭代权限数据============↓ */
		var roles = result.data.permissions;
		$.each(roles, function(){
			 var $tr = $(' <tr><td width="50%"><div>'+this.name+'</div></td><td width="50%"><div>'+this.description+'</div></td><tr>');
             $tr.appendTo($("#userDetialtoPermissions"));
		});
	});
});
</script>
