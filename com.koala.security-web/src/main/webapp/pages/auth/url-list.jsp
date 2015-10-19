<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@include file="/commons/taglibs.jsp"%>
<%@include file="/commons/url-condition.jsp"%>

<div data-role="urlGrid"></div>
<script>
	$(function() {
		var baseUrl = contextPath + '/auth/url/';
		function initEditDialog(data, item, grid) {
            dialog = $(data);
            var form = dialog.find("#url_form");
            validate(form, dialog, item);
            if(item){
                dialog.find('.modal-header').find('.modal-title').html('修改URL访问资源信息');
                form.find("input[name='name']").val(item.name);
                form.find("input[name='url']").val(item.url).attr('disabled', 'disabled');
                form.find("input[name='description']").val(item.description);
            }
			
			dialog.modal({
				keyboard : false
			}).on({
				'hidden.bs.modal' : function() {
					$(this).remove();
				},
				'complete' : function() {
					grid.message({
						type : 'success',
						content : '保存成功'
					});
					$(this).modal('hide');
					grid.grid('refresh');
				}
			});
		};
		
		function validate(form, dialog, item){
			var rules = {
				"notnull"		: {
					"rule" : function(value, formData){
						return value ? true : false;
					},
					"tip" : "不能为空"
				}
			};
			
			var inputs = [{ 
					name:"name",	
					rules:["notnull"],
					focusMsg:'必填',	
					rightMsg:"正确"
				},{
					name:"url",
					rules:['notnull'],
					focusMsg:'必填',
					rightMsg:"正确"
				}
			];
			
			form.validateForm({
	            inputs		: inputs,
	            button		: ".save",
	            rules 		: rules,
	            onButtonClick:function(result, button, form){
	            	/**
	            	 * result是表单验证的结果。
	            	 * 如果表单的验证结果为true,说明全部校验都通过，你可以通过ajax提交表单参数
	            	 */
	            	if(result){
	            		var data = form.serialize();
	            		var url = baseUrl + 'add.koala';
	        			if (item) {
	        				url = baseUrl + 'update.koala';
	        				data += ("&id=" + item.id);
	        			}
	        			
	        		
	        			$.ajax({
	        				url : url,
	        				data: data,
	        				type: "post",
	        				dataType:"json",
	        				success:function(data){
	        					if (data.success) {
		        					dialog.trigger('complete');
		        				} else {
		        					dialog.find('.modal-content').message({
		        						type : 'error',
		        						content : data.errorMessage
		        					});
		        				}
		        				dialog.find('#save').removeAttr('disabled');
	        				}
	        			});
					}
	            }
	       	});
		}
		
		deleteUrl = function(urlAccessResources, grid) {

            var data = "";
            $.each(urlAccessResources, function(i, urlAccessResource){
                data += ("urlAccessResourceIds=" + urlAccessResource.id + "&");
            });
            data = data.substring(0, data.length-1);

			var url = baseUrl + 'terminate.koala';
			$.post(url,data).done(function(data){
			 	if (data.success) {
			 		grid.message({
						type : 'success',
						content : '撤销成功'
					});
			 		grid.grid('refresh');
				} else {
					grid.message({
						type : 'error',
						content : data.errorMessage
					});
				}
			}).fail(function(data){
				grid.message({
					type : 'error',
					content : '撤销失败'
				});
			});
		};
		
		var columns = [{
			title 	: "URL名称",
			name 	: "name",
			width 	: 200
		},{
			title 	: "URL路径",
			name 	: "url",
			width 	: 400
		},{
			title 	: "URL描述",
			name 	: "description",
			width 	: 200
		}];

		var getButtons = function() {
				return [{
					content : '<ks:hasSecurityResource identifier="urlAccessResourceManagerAdd"><button class="btn btn-primary" type="button"><span class="glyphicon glyphicon-plus"><span>添加</button></ks:hasSecurityResource>',
					action : 'add'
				}, {
					content : '<ks:hasSecurityResource identifier="urlAccessResourceManagerUpdate"><button class="btn btn-primary" type="button"><span class="glyphicon glyphicon-edit"><span>修改</button></ks:hasSecurityResource>',
					action : 'modify'
				}, {
					content : '<ks:hasSecurityResource identifier="urlAccessResourceManagerTerminate"><button class="btn btn-danger" type="button"><span class="glyphicon glyphicon-remove"><span>撤销</button></ks:hasSecurityResource>',
					action : 'delete'
				},{
					content: '<ks:hasSecurityResource identifier="urlAccessResourceManagerGrantPermission"><button class="btn btn-info" type="button"><span class="glyphicon glyphicon-remove"><span>分配权限</button></ks:hasSecurityResource>',
					action: 'permissionAssign'
				},{
					content : '<ks:hasSecurityResource identifier="urlAccessResourceManagerQuery"><button class="btn btn-success" type="button"><span class="glyphicon glyphicon-search"></span>&nbsp;高级搜索&nbsp; <span class="caret"></span> </button></ks:hasSecurityResource>',
                    action : 'urlAccessResourceManagerQuery'
 				}];
		};
		
		var	url = contextPath + '/auth/url/pagingQuery.koala';
		$('[data-role="urlGrid"]').grid({
			identity : 'id',
			columns : columns,
			buttons : getButtons(),
			url : url
		}).on({
			'add' : function(event, item) {
				var thiz = $(this);
				$.get(contextPath + '/pages/auth/url-template.jsp').done(function(data) {
					initEditDialog(data, null, thiz);
				});
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
				$.get(contextPath + '/pages/auth/url-template.jsp').done(function(dialog) {
					initEditDialog(dialog, data.item[0], $this);
				});
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
						deleteUrl(data.item, $this);
					}
				});
			},
            'urlAccessResourceManagerQuery' : function() {
                $('#'+'${formId}'+"_div").slideToggle("slow");
            },
			"permissionAssign" : function(event,data){
        		var items 	= data.item;
				var thiz	= $(this);
				if(items.length == 0){
					thiz.message({type : 'warning',content : '请选择一条记录进行操作'});
					return;
				} else if(items.length > 1){
					thiz.message({type : 'warning',content : '只能选择一条记录进行操作'});
					return;
				}
				var urlPermission = items[0];
                console.log(urlPermission);
				openTab('/pages/auth/url-grant-permission.jsp', urlPermission.name+'的权限管理', 'urlGrantPermissionManager_' + urlPermission.id, urlPermission.id, {urlId : urlPermission.id});
        	}
		});

        var form = $('#'+'${formId}');
        form.find('#urlManagerSearch').on('click', function(){
            var params = {};
            form.find('.form-control').each(function(){
                var $this = $(this);
                var name = $this.attr('name');
                 if(name){
                    params[name] = $this.val();
                }
            });
           $('[data-role="urlGrid"]').getGrid().search(params);
        });
	});
</script>