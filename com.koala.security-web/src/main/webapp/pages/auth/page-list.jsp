<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@include file="/commons/taglibs.jsp"%>
<%@include file="/commons/page-condition.jsp"%>

<div data-role="pageGrid"></div>
<script>
	$(function(){
		var baseUrl = contextPath + '/auth/page/';
		
		function initEditDialog(data, item, grid) {
			dialog = $(data);
			dialog.find('.modal-header').find('.modal-title').html( item ? '修改页面元素资源信息' : '添加页面元素资源信息');
			
			var form = dialog.find(".page_form");
			validate(form, dialog, item);
			
			if(item){
				form.find("input[name='name']").val(item.name);
				form.find("input[name='identifier']").val(item.identifier);
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
				"notnull" : {
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
				}];
			
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
	        				type: "POST",
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
		};
		
		deletePage = function(pageElements, grid) {

            var data = "";
            $.each(pageElements, function(i, pageElement){
                data += ("pageElementResourceIds=" + pageElement.id + "&");
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
	
		var tabData = $('.tab-pane.active').data();
		var pageId = tabData.pageId;

		var columns = [{
				title : "页面元素名称",
				name : "name",
				width : 200
			},{
				title : "页面元素标识",
				name : "identifier",
				width : 250
			},{
				title : "页面元素描述",
				name : "description",
				width : 150
			}];
		
		
		var buttons = function(){
             return [{
                    content: '<ks:hasSecurityResource identifier="pageElementResourceManagerAdd"><button class="btn btn-primary" type="button"><span class="glyphicon glyphicon-plus"><span>添加</button></ks:hasSecurityResource>',
                    action: 'add'
                },{
                    content: '<ks:hasSecurityResource identifier="pageElementResourceManagerUpdate"><button class="btn btn-primary" type="button"><span class="glyphicon glyphicon-edit"><span>修改</button></ks:hasSecurityResource>',
                    action: 'modify'
                },{
                    content: '<ks:hasSecurityResource identifier="pageElementResourceManagerTerminate"><button class="btn btn-danger" type="button"><span class="glyphicon glyphicon-remove"><span>撤销</button></ks:hasSecurityResource>',
                    action: 'delete'
                },{
                    content: '<ks:hasSecurityResource identifier="pageElementResourceManagerGrantPermission"><button class="btn btn-info" type="button"><span class="glyphicon glyphicon-remove"><span>分配权限</button></ks:hasSecurityResource>',
                    action: 'permissionAssignForPage'
                },{
                    content : '<ks:hasSecurityResource identifier="pageElementResourceManagerQuery"><button class="btn btn-success" type="button"><span class="glyphicon glyphicon-search"></span>&nbsp;高级搜索&nbsp; <span class="caret"></span> </button></ks:hasSecurityResource>',
                    action : 'pageElementResourceManagerQuery'
                }];
		};
		
		//add here
	    var url = contextPath + "/auth/page/pagingQuery.koala";

		$('[data-role="pageGrid"]').grid({
			 identity: 'id',
             columns: columns,
             buttons: buttons(),
             isShowPages: true,
             url: url
        }).on({
        	'add': function(evnet, item){
	            
        		var thiz = $(this);
				$.get(contextPath + '/pages/auth/page-template.jsp').done(function(data) {
					initEditDialog(data, null, thiz);
				});
	            
        	},
        	'modify': function(event, data){
        		var indexs = data.data;
	            var grid = $(this);
	            if(indexs.length == 0){
	                grid.message({
	                    type: 'warning',
	                    content: '请选择一条记录进行修改'
	                });
	                return;
	            }
	            if(indexs.length > 1){
	                grid.message({
	                    type: 'warning',
	                    content: '只能选择一条记录进行修改'
	                });
	                return;
	            }
	            $.get(contextPath + '/pages/auth/page-template.jsp').done(function(dialog) {
					initEditDialog(dialog, data.item[0], grid);
				});
        	},
        	'delete': function(event, data){	               
        		var indexs = data.data;
	            var grid = $(this);
        		if(indexs.length == 0){
		            grid.message({
		                   type: 'warning',
		                    content: '请选择要撤销的记录'
		            });
		             return;
	            }
	            grid.confirm({
	                content: '确定要撤销所选记录吗?',
	                callBack: function(){
	                	deletePage(data.item, grid);
	                }
	            });
	            
        	},
        	"permissionAssignForPage" : function(event,data){
        		var items 	= data.item;
				var thiz	= $(this);
				if(items.length == 0){
					thiz.message({type : 'warning',content : '请选择一条记录进行操作'});
					return;
				} 
				if(items.length > 1){
					thiz.message({type : 'warning',content : '只能选择一条记录进行操作'});
					return;
				}
				
				var page = items[0];
				openTab('/pages/auth/page-grant-permission.jsp', page.name+'的权限管理', 'pageGrantManager_' + page.id, page.id, {pageId : page.id});
        	},
			'pageElementResourceManagerQuery' : function() {
                $('#'+'${formId}'+"_div").slideToggle("slow");
			}
        });

        var form = $('#'+'${formId}');
        form.find('#pageManagersearch').on('click',function(){
            var params = {};
            form.find('.form-control').each(function(){
                var $this = $(this);
                var name = $this.attr('name');
                if(name){
                    params[name] = $this.val();
                }
            });
            $('[data-role="pageGrid"]').getGrid().search(params);
        });

    });
</script>