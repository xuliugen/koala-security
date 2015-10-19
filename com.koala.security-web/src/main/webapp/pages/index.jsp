<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/taglibs.jsp"%>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <%--<%@include file="/commons/metas.jsp"%>--%>
    <title>Koala权限系统</title>
    <%@include file="/commons/statics.jsp"%>
    <style>
        .nav-stacked .node ul{
            display:none;
        }
    </style>
    <script>
        var contextPath = '${pageContext.request.contextPath}';

        $(function(){
            var url = contextPath + "/auth/currentUser/findAllMenusByUserAsRole.koala?"+new Date().getTime();
            $.get(url,function(data){
                $.each(data.data,function(){
                var $li = $('<li class="folder"><a data-toggle="collapse" href="#menuMark'+this.id+'"><span class="'+this.menuIcon+'"></span>&nbsp;'+this.name+'&nbsp;'+
                            '<i class="glyphicon glyphicon-chevron-left" style=" float: right;font-size: 12px;position: relative;right: 8px;top: 3px;"></i></a><ul id="menuMark'+this.id+'" class="second-level-menu in"></ul></li>');
                        $('.first-level-menu').append($li);
                        renderSubMenu(this.children, $li);
            	});
            /*
                    * 菜单收缩样式变化
                     */
                    var firstLevelMenu = $('.first-level-menu');
                    firstLevelMenu.find('[data-toggle="collapse"]').each(function(){
                        var $this = $(this);
                        firstLevelMenu.find($(this).attr('href')).on({
                            'shown.bs.collapse': function(e){
                                $this.find('i:last').addClass('glyphicon-chevron-left').removeClass('glyphicon-chevron-right');
                            },
                            'hidden.bs.collapse': function(e){
                                $this.find('i:last').removeClass('glyphicon-chevron-left').addClass('glyphicon-chevron-right');
                            }
                        });
                    });
            },"json");
        });

        	var renderSubMenu = function(data, $menu){
						$.each(data, function(){
							if(this.children.length > 0){
		                        var $li = $('<li class="folder"><a data-toggle="collapse" href="#menuMark'+this.id+'"><span class="'+this.menuIcon+'"></span>&nbsp;'+this.name+'&nbsp;'+
		                            '<i class="glyphicon glyphicon-chevron-right pull-right" style="position: relative; right: 12px;font-size: 12px;"></i></a><ul id="menuMark'+this.id+'" class="second-level-menu collapse"></ul></li>');
		                        $li.appendTo($menu.find('.second-level-menu:first')).find('a').css('padding-left', parseInt(this.level + 1)*18+'px');
		                        renderSubMenu(this.children, $li);
		                    }else{
		                        var $li = $(' <li class="submenu" data-role="openTab" data-target="'+this.url+'" data-title="'+this.name+'" ' +
		                            'data-mark="menuMark'+this.id+'"><a ><span class="'+this.menuIcon+'"></span>&nbsp;'+this.name+'</a></li>');
		                        $li.appendTo($menu.find('.second-level-menu:first')).find('a').css('padding-left', parseInt(this.level + 1)*18+'px');
		                        
		                        
		                    }
						});
	                    $menu.find('[data-toggle="collapse"]').each(function(){
	                        var $this = $(this);
	                        $menu.find($(this).attr('href')).on({
	                            'shown.bs.collapse': function(e){
	                            	e.stopPropagation();
	                            	e.preventDefault();
	                                $this.find('i:last').addClass('glyphicon-chevron-left').removeClass('glyphicon-chevron-right');
	                            },
	                            'hidden.bs.collapse': function(e){
	                            	e.stopPropagation();
	                            	e.preventDefault();
	                                $this.find('i:last').removeClass('glyphicon-chevron-left').addClass('glyphicon-chevron-right');
	                            }
	                        });
	                    });
						$menu.find('li.submenu').on('click', function(){
							var $this = $(this);
							$('.first-level-menu').find('li').each(function(){
								var $menuLi = $(this);
								$menuLi.hasClass('active') && $menuLi.removeClass('active').parent().parent().removeClass('active');
							});
							$this.addClass('active').parents().filter('.folder').addClass('active');
							var target = $this.data('target');
							var title = $this.data('title');
							var mark = $this.data('mark');
							if(target && title && mark ){
								openTab(target, title, mark);
							}
						});
			};

        /*判断一个对象是否数组*/
        function isArray(o){
            return '[object Array]' == Object.prototype.toString.call(o);
        }

        // 更改联系电话
        // ------------ changeTelePhoneOfUser start ---------------
        $(function(){
            $('#userManager').find("li[data-target=#changeTelePhoneOfUser]").click(function(){
                $.get(contextPath + '/pages/auth/user-changeTelePhone.jsp').done(function(data){
                    var dialog  = $(data);
                    var oldTelePhone = dialog.find('#oldTelePhone');
                    var newTelePhone = dialog.find('#newTelePhone');
                    var confirmPassword = dialog.find('#confirmPassword');

                    dialog.find('#changeTelePhoneOfUserSave').on('click', function(){
                        var data = {};
                        data['telePhone'] = newTelePhone.val();
                        data['userPassword'] = confirmPassword.val();
                        $.post(contextPath+'/auth/currentUser/changeUserTelePhone.koala',data,function(data){
                            if(data.success){
                                dialog.find('#changeTelePhoneOfUserMessage').message({
                                    type : 'success',
                                    content : '更改联系电话成功!'
                                });
                                dialog.modal('hide');
                            }else{
                                dialog.find('#changeTelePhoneOfUserMessage').message({
                                    type : 'error',
                                    content : data.errorMessage
                                });
                            }
                        });
                    }).end().modal({
                        keyboard : false
                    }).on({
                        'hidden.bs.modal' : function() {
                            $(this).remove();
                        },
                        'complete': function(){
                            dialog.message({
                                type: 'success',
                                content: '更改联系电话成功!'
                            });
                            $(this).modal('hide');
                        }
                    });
                    //兼容IE8 IE9
                    if(window.ActiveXObject){
                        if(parseInt(navigator.userAgent.toLowerCase().match(/msie ([\d.]+)/)[1]) < 10){
                            dialog.trigger('shown.bs.modal');
                        }
                    }
                });
            });
        });
        // ------------ changeTelePhoneOfUser end ---------------

        // 更改邮箱
        // ------------ changeEmailOfUser start ---------------
        $(function(){
            $('#userManager').find("li[data-target=#changeEmailOfUser]").click(function(){
                $.get(contextPath + '/pages/auth/user-changeEmail.jsp').done(function(data){
                    var dialog  = $(data);
                    var oldEmail = dialog.find('#oldEmail');
                    var newEmail = dialog.find('#newEmail');
                    var confirmPassword = dialog.find('#confirmPassword');

                    dialog.find('#changeEmailOfUserSave').on('click', function(){
                        var data = {};
                        data['email'] = newEmail.val();
                        data['userPassword'] = confirmPassword.val();
                        $.post(contextPath+'/auth/currentUser/changeUserEmail.koala',data,function(data){
                            if(data.success){
                                dialog.find('#changeEmailOfUserMessage').message({
                                    type : 'success',
                                    content : '更改邮箱成功!'
                                });
                                dialog.modal('hide');
                            }else{
                                dialog.find('#changeEmailOfUserMessage').message({
                                    type : 'error',
                                    content : data.errorMessage
                                });
                            }
                        });
                    }).end().modal({
                        keyboard : false
                    }).on({
                        'hidden.bs.modal' : function() {
                            $(this).remove();
                        },
                        'complete': function(){
                            dialog.message({
                                type: 'success',
                                content: '更改邮箱成功!'
                            });
                            $(this).modal('hide');
                        }
                    });
                    //兼容IE8 IE9
                    if(window.ActiveXObject){
                        if(parseInt(navigator.userAgent.toLowerCase().match(/msie ([\d.]+)/)[1]) < 10){
                            dialog.trigger('shown.bs.modal');
                        }
                    }
                });
            });
        });
        // ------------ changeEmailOfUser end ---------------

        /*切换角色*/
        $(function(){
            // ------------ switchOverRoleOfUser start ---------------
            $('#userManager').find("li[data-target=#rolesToggle]").click(function(){
                $.get(contextPath + '/pages/auth/role-switch.jsp').done(function(data){
                    var dialog = $(data);
                    dialog.find('#toggle').on('click',function(){
                        var items = dialog.find('.selectRoleGrid').data('koala.grid').selectedRows();
                        if(items.length == 0){
                            dialog.find('.selectRoleGrid').message({
                                type: 'warning',
                                content: '请选择要切换的角色'
                            });
                            return;
                        }
                        if(items.length>1){
                            dialog.find('.selectRoleGrid').message({
                                type:'warning',
                                content:'只能选择一个角色'
                            });
                            return;
                        }
                        $.post(contextPath + '/auth/currentUser/switchOverRoleOfUser.koala', "roleName=" + items[0].name,function(data){
                            if(data.success){
                                dialog.find('.selectRoleGrid').message({
                                    type:'success',
                                    content:'切换角色成功！'
                                });
                                window.location.href=contextPath+"/index.koala";
                            }else{
                                dialog.find('.selectRoleGrid').message({
                                    type:'error',
                                    content:'系统错误！'
                                });
                            }
                        });

                    })
                    dialog.modal({
                        keyboard: false
                    }).on({
                        'hidden.bs.modal': function(){
                            $(this).remove();
                        },

                        'shown.bs.modal': function(){ //弹窗初始化完毕后，初始化url选择表格
                            var columns = [{
                                title : "角色名称",
                                name : "name",
                                width : 250
                            }, {
                                title : "角色描述",
                                name : "description",
                                width : 250
                            }];

                            dialog.find('.selectRoleGrid').grid({
                                identity: 'id',
                                columns: columns,
                                url: contextPath + '/auth/currentUser/pagingQueryRolesOfUser.koala'
                            });
                        }

                    });
                    //兼容IE8 IE9
                    if(window.ActiveXObject){
                        if(parseInt(navigator.userAgent.toLowerCase().match(/msie ([\d.]+)/)[1]) < 10){
                            dialog.trigger('shown.bs.modal');
                        }
                    }
                });
                // ------------ switchOverRoleOfUser end ---------------
            });
            
        
        });
        /**
    	 * 显示详细信息
    	 */
    	var showDetail = function(){
        	var userInfo = $("#userInfo").html();
        	   var thiz 	= $(this),
               mark 	= thiz.attr('mark');
       		  mark = openTab('/pages/auth/currentUser-info.jsp', userInfo, mark);
              if(mark){
                  thiz.attr("mark",mark);
              }
    	};

    </script>
</head>

<body>
	<input type="hidden" id="roleId" value="${roleId}" />
    <%-- 顶部 --%>
	<div class="g-head">
	    <nav class="navbar navbar-default">
	        <a class="navbar-brand" href="#">
	        	<img src="${contextPath}/images/global.logo.png"/>
	        	<span style="font-weight:800;">Koala权限系统</span>
	        </a>
	        <div class="collapse navbar-collapse navbar-ex1-collapse">
                <div class="btn-group navbar-right">
                    <img class="dropdown-toggle" data-toggle="dropdown" id='btn1'  src =${contextPath}/images/systemFunction.png  style="width: 35px; height: 35px; margin-top: 5px;" />
                    <ul class="dropdown-menu" id="userManager" style="min-width: 0">
                        <li data-target="loginOut"><a href="#" class="glyphicon glyphicon-off">&nbsp;注销</a></li>
                        <li data-target="modifyPwd"><a href="#" class="glyphicon glyphicon-pencil">&nbsp;更改密码</a></li>
                        <li data-toggle="modal" data-target="#changeEmailOfUser"><a href="#" class="glyphicon glyphicon-envelope">&nbsp;更改邮箱</a></li>
                        <li data-toggle="modal" data-target="#changeTelePhoneOfUser"><a href="#" class="glyphicon glyphicon-earphone">&nbsp;更改电话</a></li>
                        <li data-toggle='modal' data-target="#rolesToggle"><a href="#" class="glyphicon glyphicon-repeat">&nbsp;切换角色</a></li>
                    </ul>
                </div>
                <!--角色信息-->
                <div class="btn-group navbar-right">
                    <label for = "roles" class = "user_name">角色: </label>
	            	<span id="roles" style="font-size: 12px"><ks:user property="roleName" /></span>
                    <ul class="dropdown-menu" id="allRolesId"></ul>
                </div>
	            <!-- 账号信息 -->
	            <div class="btn-group navbar-right">
                    <span>
                        <!-- 为了不改变页面布局-->
                    </span>
                    <a href="#" id="userInfo"  onclick="showDetail()" class="glyphicon glyphicon-user" style="color: #fff;text-decoration: none; -moz-osx-font-smoothing:none;top:-1px;font-weight: bold; font-size: 12px;"  title="查看个人信息"  >&nbsp;<ks:user property="name" />
                    </a>
                    &nbsp; &nbsp;
                </div>
	        </div>
	    </nav>
	</div>

	<%-- 中间 --%>
	<div class="g-body">
		<!-- 左边导航 -->
	    <div class="col-xs-2 g-sidec">
	        <ul class="nav nav-stacked first-level-menu">
	       		
	        </ul>
	    </div>
	    <!-- 右边内容 -->
	    <div class="col-xs-10 g-mainc">
	        <ul class="nav nav-tabs" id="navTabs">
	            <li class="active"><a href="#home" data-toggle="tab">主页</a></li>
	        </ul>
	        <div class="tab-content" id="tabContent">
	            <div id="home" class="tab-pane active"></div>
	        </div>
	    </div>
	</div>
    <%-- 底部 --%>
	<div id="footer" class="g-foot"><span>Copyright © 2011-2014 Koala</span></div>
</body>
</html>