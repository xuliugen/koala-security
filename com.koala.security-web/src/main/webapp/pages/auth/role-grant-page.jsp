<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@include file="/commons/taglibs.jsp"%>
<%@include file="/commons/page-condition.jsp"%>

<div data-role="roleGrantPageGrid"></div>
<script>
$(function(){
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
            content: '<button class="btn btn-primary" type="button"><span class="glyphicon glyphicon-th-large"><span>分配页面元素</button>',
            action: 'assignPageFromRole'
        }, {
            content: '<button class="btn btn-danger" type="button"><span class="glyphicon glyphicon-th-large"><span>撤销页面元素</button>',
            action: 'removePageFromRole'
        }, {
            content : '<button class="btn btn-success" type="button"><span class="glyphicon glyphicon-search" />&nbsp;高级搜索&nbsp;<span class="caret" /></button>',
            action : 'pageElementResourceManagerQuery'
        }];
    };

    var url = contextPath + "/auth/role/pagingQueryGrantPageElementResourcesByRoleId.koala?roleId="+ pageId;

    $('[data-role="roleGrantPageGrid"]').grid({
        identity: 'id',
        columns: columns,
        buttons: buttons(),
        isShowPages: true,
        url: url
    }).on({
        "assignPageFromRole" : function(event, data){
            var grid = $(this);
            /*
             获取选中项的信息
             */
            $.get(contextPath + '/pages/auth/page-select.jsp').done(function(data){
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
                    $('[data-role="selectPageGrid"]').getGrid().search(params);
                });

                dialog.find('#save').click(function(){
                    var saveBtn = $(this);
                    var items = dialog.find('#selectPageGrid').data('koala.grid').selectedRows();

                    if(items.length == 0){
                        dialog.find('#selectPageGrid').message({
                            type: 'warning',
                            content: '请选择要分配的页面'
                        });
                        return;
                    }

                    saveBtn.attr('disabled', 'disabled');

                    var data = "roleId="+ pageId;
                    for(var i=0,j=items.length; i<j; i++){
                        data += "&pageElementResourceIds=" + items[i].id;
                    }

                    $.post(contextPath + '/auth/role/grantPageElementResourcesToRole.koala', data).done(function(data){
                        if(data.success){
                            grid.message({
                                type: 'success',
                                content: '保存成功'
                            });
                            dialog.modal('hide');
                            grid.grid('refresh');
                        }else{
                            grid.message({
                                type : 'error',
                                content : data.errorMessage
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

                    'shown.bs.modal': function(){ //弹窗初始化完毕后，初始化url选择表格
                        var columns = [{
                            title : "页面元素名称",
                            name : "name",
                            width : 200
                        }, {
                            title : "页面元素标识",
                            name : "identifier",
                            width : 200
                        }, {
                            title : "页面元素描述",
                            name : "description",
                            width : 200
                        }];

                        dialog.find('#selectPageGrid').grid({
                            identity: 'id',
                            columns: columns,
                            url: contextPath + '/auth/role/pagingQueryNotGrantPageElementResourcesByRoleId.koala?roleId='+pageId
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
        'pageElementResourceManagerQuery' : function() {
            $('#'+'${formId}'+"_div").slideToggle("slow");
        },
        'removePageFromRole' : function(event, data) {
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
                    var url = contextPath + '/auth/role/terminatePageElementResourcesFromRole.koala';
                    var params = "roleId="+pageId;
                    for (var i = 0, j = data.item.length; i < j; i++) {
                        params += ("&pageElementResourceIds=" + data.item[i].id);
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
    form.find('#pageManagersearch').on('click',function(){
        var params = {};
        form.find('.form-control').each(function(){
            var $this = $(this);
            var name = $this.attr('name');
            if(name){
                params[name] = $this.val();
            }
        });
        $('[data-role="roleGrantPageGrid"]').getGrid().search(params);
    });
});
</script>