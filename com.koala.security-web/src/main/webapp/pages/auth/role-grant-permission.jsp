<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@include file="/commons/taglibs.jsp"%>
<%@include file="/commons/permission-condition.jsp"%>

<div data-role="roleGrantPermissionGrid"></div>
<script>
$(function(){
    var tabData = $('.tab-pane.active').data();
    var roleId = tabData.permissionId;

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

    var buttons = function(){
            return [{
                content : '<button class="btn btn-primary" type="button"><span class="glyphicon glyphicon-th-large"><span>分配权限</button>',
                action : 'assignPermissionForRole'
            }, {
                content : '<button class="btn btn-danger" type="button"><span class="glyphicon glyphicon-remove"><span>撤销权限</button>',
                action : 'removePermissionForRole'
            },{
                content : '<button class="btn btn-success" type="button"><span class="glyphicon glyphicon-search" />&nbsp;高级搜索&nbsp;<span class="caret" /></button>',
                action : 'permissionManagerQuery'
            }];
    };

    var url = contextPath + '/auth/role/pagingQueryGrantPermissionsByRoleId.koala?roleId=' + roleId;

    $('[data-role="roleGrantPermissionGrid"]').grid({
        identity: 'id',
        columns: columns,
        buttons: buttons(),
        isShowPages: true,
        url: url
    }).on({
        'permissionManagerQuery' : function() {
            $('#'+'${formId}'+"_div").slideToggle("slow");
        },
        'assignPermissionForRole':function(event, data){
            var grid = $(this);
            $.get(contextPath + '/pages/auth/permission-select.jsp').done(function(data){
                var dialog = $(data);

                dialog.find('#search').on('click', function(){
                    var params = {};
                    dialog.find('.form-control').each(function(){
                        var $this = $(this);
                        var name = $this.attr('name');
                        if(name){
                            params[name] = $this.val();
                        }
                    });
                    $('[data-role="selectPermissionGrid"]').getGrid().search(params);
                });

                dialog.find('#save').click(function(){
                    var saveBtn = $(this);
                    var items = dialog.find('.selectPermissionGrid').data('koala.grid').selectedRows();

                    if(items.length == 0){
                        dialog.find('.modal-content').message({
                            type: 'warning',
                            content: '请选择要分配的权限'
                        });
                        return;
                    }

                    saveBtn.attr('disabled', 'disabled');

                    var data = "roleId="+roleId;
                    for(var i=0,j=items.length; i<j; i++){
                        data += "&permissionIds=" + items[i].id;
                    }

                    $.post(contextPath + '/auth/role/grantPermissionsToRole.koala', data).done(function(data){
                        if(data.success){
                            grid.message({
                                type: 'success',
                                content: '保存成功'
                            });
                            dialog.modal('hide');
                            grid.grid('refresh');
                        }else{
                            saveBtn.attr('disabled', 'disabled');
                            grid.message({
                                type: 'error',
                                content: data.errorMessage
                            });
                        }
                    }).fail(function(data){
                        saveBtn.attr('disabled', 'disabled');
                        grid.message({
                            type: 'error',
                            content: '保存失败'
                        });
                    });
                }).end().modal({
                    keyboard: false
                }).on({
                    'hidden.bs.modal': function(){
                        $(this).remove();
                    },

                    'shown.bs.modal': function(){ //弹窗初始化完毕后，初始化权限选择表格
                        var columns = [{
                            title : "权限名称",
                            name : "name",
                            width : 150
                        },{
                            title : "菜单标识",
                            name : "identifier",
                            width : 150
                        },{
                            title : "权限描述",
                            name : "description",
                            width : 150
                        }];

                        dialog.find('.selectPermissionGrid').grid({
                            identity: 'id',
                            columns: columns,
                            url: contextPath + '/auth/role/pagingQueryNotGrantPermissionsByRoleId.koala?roleId='+roleId
                        });
                    },
                    'complete': function(){
                        grid.message({
                            type: 'success',
                            content: '保存成功'
                        });
                        $(this).modal('hide');
                        grid.grid('refresh');
                    }
                });
                //兼容IE8 IE9
                if(window.ActiveXObject){
                    if(parseInt(navigator.userAgent.toLowerCase().match(/msie ([\d.]+)/)[1]) < 10){
                        dialog.trigger('shown.bs.modal');
                    }
                }
            });
        },
        'removePermissionForRole':function(event, data) {
            var indexs = data.data;
            var grid = $(this);
            if (indexs.length == 0) {
                grid.message({
                    type : 'warning',
                    content : '请选择要删除的记录'
                });
                return;
            }
            grid.confirm({
                content : '确定要删除所选记录吗?',
                callBack : function() {
                    var url = contextPath + '/auth/role/terminatePermissionsFromRole.koala';
                    var params = "roleId="+roleId;
                    for (var i = 0, j = data.item.length; i < j; i++) {
                        params += ("&permissionIds=" + data.item[i].id);
                    }

                    $.post(url, params).done(function(data){
                        if(data.success){
                            grid.message({
                                type: 'success',
                                content: '删除成功'
                            });
                            grid.grid('refresh');
                        }else{
                            grid.message({
                                type: 'error',
                                content: data.errorMessage
                            });
                        }
                    }).fail(function(data){
                        grid.message({
                            type: 'error',
                            content: '删除失败'
                        });
                    });
                }
            });
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
        $('[data-role="roleGrantPermissionGrid"]').getGrid().search(params);
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