<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@include file="/commons/taglibs.jsp"%>
<%@include file="/commons/permission-condition.jsp"%>

<div data-role="permissionGrid"></div>
<script>
	$(function(){
		var baseUrl = contextPath + '/auth/permission/';
		function initEditDialog(data, item, grid) {
            dialog = $(data);
            var form = dialog.find(".permisstion_form");
            validate(form, dialog, item);
            if(item){
                dialog.find('.modal-header').find('.modal-title').html('修改权限信息');
                form.find("input[name='name']").val(item.name);
                form.find("input[name='identifier']").val(item.identifier).attr('disabled', 'disabled');
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
		
		deletePermission = function(permissions, grid) {

			var url = baseUrl + 'terminate.koala';

            var data = "";
            $.each(permissions, function (i, permission) {
                data += ("permissionIds=" + permission.id + "&");
            });
            data = data.substring(0, data.length - 1);
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
				title : "权限名称",
				name : "name",
				width : 150
			},{
				title : "权限标识",
				name : "identifier",
				width : 150
			},{
				title : "权限描述",
				name : "description",
				width : 150
			},{
                title : "查看",
                name : "operate",
                width : 200,
                render: function(item, name, index){
                   return '<a href="#" onclick="showUserDetail('+item.id+', \''+item.name+'\')"><span class="glyphicon glyphicon glyphicon-eye-open"></span>&nbsp;详细</a>';
                }
            }];

        var buttons = function () {
            return [{
                    content: '<ks:hasSecurityResource identifier="permissionManagerAdd"><button class="btn btn-primary" type="button"><span class="glyphicon glyphicon-plus"><span>添加</button></ks:hasSecurityResource>',
                    action: 'add'
                }, {
                    content: '<ks:hasSecurityResource identifier="permissionManagerUpdate"><button class="btn btn-primary" type="button"><span class="glyphicon glyphicon-edit"><span>修改</button></ks:hasSecurityResource>',
                    action: 'modify'
                }, {
                    content: '<ks:hasSecurityResource identifier="permissionManagerTerminate"><button class="btn btn-danger" type="button"><span class="glyphicon glyphicon-remove"><span>撤销</button></ks:hasSecurityResource>',
                    action: 'delete'
                }, {
                    content: '<ks:hasSecurityResource identifier="permissionManagerQuery"><button class="btn btn-success" type="button"><span class="glyphicon glyphicon-search"></span>&nbsp;高级搜索&nbsp; <span class="caret"></span> </button></ks:hasSecurityResource>',
                    action: 'permissionManagerQuery'
                }];
        };
		
		var	url = contextPath + '/auth/permission/pagingQuery.koala';

		$('[data-role="permissionGrid"]').grid({
			 identity: 'id',
             columns: columns,
             buttons: buttons(),
             isShowPages: true,
             url:url
        }).on({
        	'add' : function(event, item) {
				var thiz = $(this);
				$.get(contextPath + '/pages/auth/permission-template.jsp').done(function(data) {
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
                $.get(contextPath + '/pages/auth/permission-template.jsp').done(function(dialog){
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
	                	deletePermission(data.item, grid);
	                }
	            });
        	},
			'permissionManagerQuery' : function() {
                $('#'+'${formId}'+"_div").slideToggle("slow");
			}
        });

        var form = $('#'+'${formId}');
        form.find('#permissionManagerSearch').on('click', function(){
            var params = {};
            form.find('.form-control').each(function(){
                var $this = $(this);
                var name = $this.attr('name');
                 if(name){
                    params[name] = $this.val();
                }
            });
           $('[data-role="permissionGrid"]').getGrid().search(params);
        });
});

    /**
     * 显示详细信息
     * @param id
     * @param userName
     */
    var showUserDetail = function(id, name){
        var thiz = $(this);
        var mark = thiz.attr('mark');
        mark = openTab('/pages/auth/permission-detail.jsp', name, mark, id);
        if (mark) {
            thiz.attr("mark", mark);
        }
    };
</script>
