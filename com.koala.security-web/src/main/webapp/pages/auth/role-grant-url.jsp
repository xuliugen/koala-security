<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@include file="/commons/taglibs.jsp"%>
<%@include file="/commons/url-condition.jsp"%>

<div data-role="roleGrantUrlGrid"></div>
<script>
$(function() {
    var role = $('.tab-pane.active').data();
    var roleId = role ? role.roleId : null;

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

    var getButtons = function () {
        return [
            {
                content: '<ks:hasSecurityResource identifier="roleManagerGrantUrlAccessResource"><button class="btn btn-primary" type="button"><span class="glyphicon glyphicon-th-large"/>&nbsp;分配URL</button></ks:hasSecurityResource>',
                action: 'assignUrl'
            }, {
                content: '<button class="btn btn-danger" type="button"><span class="glyphicon glyphicon-remove" />&nbsp;撤销URL</button>',
                action: 'removeUrlFromRole'
            }, {
                content: '<button class="btn btn-success" type="button"><span class="glyphicon glyphicon-search" />&nbsp;高级搜索&nbsp;<span class="caret" /></button>',
                action: 'urlAccessResourceManagerQuery'
            }
        ];
    };

    url = contextPath + '/auth/role/pagingQueryGrantUrlAccessResourcesByRoleId.koala' + '?roleId=' + roleId;

    $('[data-role="roleGrantUrlGrid"]').grid({
        identity : 'id',
        columns : columns,
        buttons : getButtons(),
        url : url
    }).on({
        "assignUrl" : function(event, data){
            var grid = $(this);
            $.get(contextPath + '/pages/auth/url-select.jsp').done(function(data){
                var dialog = $(data);

                dialog.find('#selectUrlsearch').on('click', function(){
                    var params = {};
                    dialog.find('.form-control').each(function(){
                        var $this = $(this);
                        var name = $this.attr('name');
                        if(name){
                            params[name] = $this.val();
                        }
                    });
                    $('[data-role="selectUrlGrid"]').getGrid().search(params);
                });

                dialog.find('#save').click(function(){
                    var $saveBtn = $(this);
                    var items = dialog.find('#selectUrlGrid').data('koala.grid').selectedRows();
                    if(items.length == 0){
                        dialog.find('#selectUrlGrid').message({
                            type: 'warning',
                            content: '请选择要分配的url'
                        });
                        return;
                    }

                    $saveBtn.attr('disabled', 'disabled');
                    var data = "roleId="+roleId;


                    for(var i=0,j=items.length; i<j; i++){
                        data += "&urlAccessResourceIds="+items[i].id;
                    }

                    $.post(contextPath + '/auth/role/grantUrlAccessResourcesToRole.koala', data).done(function(data){
                        grid.message({
                            type: 'success',
                            content: '保存成功'
                        });
                        dialog.modal('hide');
                        grid.grid('refresh');
                    }).fail(function(data){
                        $saveBtn.attr('disabled', 'disabled');
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

                    'shown.bs.modal': function(){ //弹窗初始化完毕后，初始化url选择表格
                        var columns = [{
                            title 	: "URL名称",
                            name 	: "name",
                            width 	: 300
                        },{
                            title 	: "URL路径",
                            name 	: "url",
                            width 	: 300
                        },{
                            title 	: "URL描述",
                            name 	: "description",
                            width 	: 100
                        }];

                        dialog.find('#selectUrlGrid').grid({
                            identity: 'id',
                            columns: columns,
                            url: contextPath + '/auth/role/pagingQueryNotGrantUrlAccessResourcesByRoleId.koala?roleId=' + roleId
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
                        dialog.trigger('shown.bs.modal')
                    }
                }
            });
        },
        'urlAccessResourceManagerQuery' : function() {
            $('#'+'${formId}'+"_div").slideToggle("slow");
        },
        "removeUrlFromRole" : function(event, data){ //解除授予
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
                    var url = contextPath + '/auth/role/terminateUrlAccessResourcesFromRole.koala';
                    var params = "roleId="+roleId;
                    for (var i = 0, j = data.item.length; i < j; i++) {
                        params += ("&urlAccessResourceIds=" + data.item[i].id);
                    }

                    $.post(url, params).done(function(data) {
                        grid.message({
                            type : 'success',
                            content : '删除成功'
                        });
                        grid.grid('refresh');
                    }).fail(function(data) {
                        grid.message({
                            type : 'error',
                            content : '删除失败'
                        });
                    });
                }
            });
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
        $('[data-role="roleGrantUrlGrid"]').getGrid().search(params);
    });

});
</script>