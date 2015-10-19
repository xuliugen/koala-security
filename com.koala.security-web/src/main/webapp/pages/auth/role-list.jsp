<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@include file="/commons/taglibs.jsp"%>

<!-- strat form -->
<form name="roleListForm" id="${formId}" target="_self" class="form-horizontal searchCondition">
<div  id="roleManagerQueryDivId" hidden="true" >
<table border="0" cellspacing="0" cellpadding="0">
  <tr>
      <td>
          <div class="form-group">
              <label class="control-label" style="width:100px;float:left;">角色名称:&nbsp;</label>
              <div style="margin-left:15px;float:left;">
                  <input name="name" class="form-control" type="text" style="width:180px;"/>
              </div>

              <label class="control-label" style="width:100px;float:left;">角色描述:&nbsp;</label>
              <div style="margin-left:15px;float:left;">
                  <input name="description" class="form-control" type="text" style="width:180px;"/>
              </div>
          </div>
      </td>
      <td style="vertical-align: bottom;"><button id="roleManagerSearch" type="button" style="position:relative; margin-left:35px; top: -15px" class="btn btn-success"><span class="glyphicon glyphicon-search"></span>&nbsp;</button></td>
  </tr>
</table>	
</div>
</form>
<!-- end form -->
<div data-role="roleGrid"></div>
<script>

	$(function() {
		var columns = [{
			title : "角色名称",
			name : "name",
			width : 250
		}, {
			title : "角色描述",
			name : "description",
			width : 250
		}
        ];
	
		var buttons = (function() {
            return [{
                content : '<ks:hasSecurityResource identifier="roleManagerAdd"><button class="btn btn-primary" type="button"><span class="glyphicon glyphicon-plus"><span>添加</button></ks:hasSecurityResource>',
                action : 'add'
            }, {
                content : '<ks:hasSecurityResource identifier="roleManagerUpdate"><button class="btn btn-primary" type="button"><span class="glyphicon glyphicon-edit"><span>修改</button></ks:hasSecurityResource>',
                action : 'modify'
            }, {
                content : '<ks:hasSecurityResource identifier="roleManagerTerminate"><button class="btn btn-danger" type="button"><span class="glyphicon glyphicon-remove"><span>撤销</button></ks:hasSecurityResource>',
                action : 'delete'
            }, {
                content : '<ks:hasSecurityResource identifier="roleManagerGrantUrlAccessResource"><button class="btn btn-info" type="button"><span class="glyphicon glyphicon-th-large"></span>&nbsp;分配URL访问资源</button></ks:hasSecurityResource>',
                action : 'urlAssign'
            }, {
                content : '<ks:hasSecurityResource identifier="roleManagerGrantMenuResource"><button class="btn btn-info" type="button"><span class="glyphicon glyphicon-th-large"></span>&nbsp;分配菜单资源</button></ks:hasSecurityResource>',
                action : 'menuAssign'
            },{
                content : '<ks:hasSecurityResource identifier="roleManagerGrantPageElementResource"><button class="btn btn-info" type="button"><span class="glyphicon glyphicon-th-large"></span>&nbsp;分配页面元素资源</button></ks:hasSecurityResource>',
                action : 'pageAssign'
            },{
                content : '<ks:hasSecurityResource identifier="roleManagerGrantPermission"><button class="btn btn-info" type="button"><span class="glyphicon glyphicon-th-large"></span>&nbsp;分配权限</button></ks:hasSecurityResource>',
                action : 'permissionAssign'
            },{
                content : '<ks:hasSecurityResource identifier="roleManagerQuery"><button class="btn btn-success" type="button"><span class="glyphicon glyphicon-search"></span>&nbsp;高级搜索&nbsp; <span class="caret"></span> </button></ks:hasSecurityResource>',
                action : 'roleManagerQuery'
            }];
		})();
		
		var url = contextPath + '/auth/role/pagingQuery.koala';

		$('[data-role="roleGrid"]').grid({
			identity : 'id',
			columns : columns,
			buttons : buttons,
			url 	: url
		}).on({
			'add' 	: function() {
				roleManager().add($(this));
			},
			'modify' : function(event, data) {
				var indexs = data.data;
				var $this = $(this);
				if (indexs.length == 0) {       
					$this.message({
						type : 'warning',
						content : '请选择一条记录进行修改'
					});
					return;
				}
				if (indexs.length > 1) {
					$this.message({
						type : 'warning',
						content : '只能选择一条记录进行修改'
					});
					return;
				}
				roleManager().modify(data.item[0], $(this));
			},
			'delete' : function(event, data) {
				var indexs = data.data;
				var $this = $(this);
				if (indexs.length == 0) {
					$this.message({
						type : 'warning',
						content : '请选择要撤销的记录'
					});
					return;
				}
				$this.confirm({
					content : '确定要撤销所选记录吗?',
					callBack : function() {
						roleManager().deleteRole(data.item, $this);
					}
				});
			},
			'roleManagerQuery' : function() {
				$("#roleManagerQueryDivId").slideToggle("slow");
			},
			"urlAssign" : function(event, data){
				var items 	= data.item;
				var thiz	= $(this);
				if(items.length == 0){
					thiz.message({type : 'warning',content : '请选择一条记录进行操作'});
					return;
				} else if(items.length > 1){
					thiz.message({type : 'warning',content : '只能选择一条记录进行操作'});
					return;
				}
				
				var role = items[0];
                console.log(role);
				/*打开url表格*/
				openTab('/pages/auth/role-grant-url.jsp', role.name+'的URL访问资源管理', 'roleGrantUrlManager_' + role.id, role.id, {roleId : role.id});
			},
			"menuAssign" : function(event, data) {
				var items = data.item;
				var $this = $(this);
				if (items.length == 0) {
					$this.message({
						type : 'warning',
						content : '请选择一条记录进行操作'
					});
					return;
				}
				if (items.length > 1) {
					$this.message({
						type : 'warning',
						content : '只能选择一条记录进行操作'
					});
					return;
				}
				roleManager().assignResource($this, items[0].id);
			},
			'pageAssign' : function(event, data) {
				var items = data.item;
				var $this = $(this);
				if (items.length == 0) {
					$this.message({
						type : 'warning',
						content : '请选择一条记录进行操作'
					});
					return;
				}
				if (items.length > 1) {
					$this.message({
						type : 'warning',
						content : '只能选择一条记录进行操作'
					});
					return;
				}
				var page = items[0];
				openTab('/pages/auth/role-grant-page.jsp', page.name+'的页面元素资源管理', 'roleGrantPageManager_' + page.id, page.id, {pageId : page.id});
			},
			'permissionAssign' : function(event, data) {
				var items = data.item;
				var $this = $(this);
				if (items.length == 0) {
					$this.message({
						type : 'warning',
						content : '请选择一条记录进行操作'
					});
					return;
				}
				if (items.length > 1) {
					$this.message({
						type : 'warning',
						content : '只能选择一条记录进行操作'
					});
					return;
				}
				var permission = items[0];
				openTab('/pages/auth/role-grant-permission.jsp', permission.name+'的权限管理', 'roleGrantPermissionManager_' + permission.id, permission.id, {permissionId : permission.id});
			},
			'assignResource' : function(event, data) {
				var indexs = data.data;
				var $this = $(this);
				if (indexs.length == 0) {
					$this.message({
						type : 'warning',
						content : '请选择一条记录进行操作'
					});
					return;
				}
				if (indexs.length > 1) {
					$this.message({
						type : 'warning',
						content : '只能选择一条记录进行操作'
					});
					return;
				}
				roleManager().assignResource($(this), data.data[0]);
			}
		});
        var form = $('#'+'${formId}');
		form.find('#roleManagerSearch').on('click', function(){
	            var params = {};
	            form.find('.form-control').each(function(){
	                var $this = $(this);
	                var name = $this.attr('name');
	                 if(name){
	                    params[name] = $this.val();
	                }
	            });
	           $('[data-role="roleGrid"]').getGrid().search(params);
	        });
	});
</script>