var roleManager = function () {
    var baseUrl = contextPath + '/auth/role/';
    var dialog = null;    		//对话框
    var name = null;  		//角色名称
    var description = null;    	//角色描述
    var dataGrid = null; 		//Grid对象

    /*
     *新增
     */
    var add = function (grid) {
        dataGrid = grid;
        $.get(contextPath + '/pages/auth/role-template.jsp').done(function (data) {
            addInit(data);
        });
    };

    function addInit(data) {
        dialog = $(data);
        dialog.find('#save').on('click', function () {
            $(this).attr('disabled', 'disabled');
            name = dialog.find('#roleName');
            description = dialog.find('#roleDescript');
            addSave();
        }).end().modal({
            keyboard: false
        }).on({
            'hidden.bs.modal': function () {
                $(this).remove();
            },
            'complete': function () {
                dataGrid.message({
                    type: 'success',
                    content: '保存成功'
                });
                $(this).modal('hide');
                dataGrid.grid('refresh');
            }
        });
    }

    function addSave() {
        var url = baseUrl + 'add.koala';
        console.log(url);
        $.post(url, getAllData()).done(function (data) {
            if (data.success) {
                dialog.trigger('complete');
            } else {
                dialog.find('.modal-content').message({
                    type: 'error',
                    content: data.errorMessage
                });
            }
            dialog.find('#save').removeAttr('disabled');
        });
    }

    /*
     * 修改
     */
    var modify = function (item, grid) {
        dataGrid = grid;
        $.get(contextPath + '/pages/auth/role-template.jsp').done(function (data) {
            init(data, item);
            setData(item);
        });
    };

    /**
     * 删除多个角色
     * @param roles 多个角色
     * @param grid
     */
    var deleteRole = function (roles, grid) {
        dataGrid = grid;
        var url = baseUrl + 'terminate.koala';

        var data = "";
        $.each(roles, function (i, role) {
            data += ("roleIds=" + role.id + "&");
        });
        data = data.substring(0, data.length - 1);

        $.post(url, data).done(function (data) {
            if (data.success) {
                dataGrid.message({
                    type: 'success',
                    content: '删除成功'
                });
                dataGrid.grid('refresh');
            } else {
                $('body').message({
                    type: 'error',
                    content: data.errorMessage
                });
            }
        }).fail(function (data) {
            dataGrid.message({
                type: 'error',
                content: '删除失败'
            });
        });
    };

    /**
     * 初始化
     */
    var init = function (data, item) {
        dialog = $(data);
        dialog.find('.modal-header').find('.modal-title').html(item ? '修改角色' : '添加角色');

        name = dialog.find('#roleName');
        description = dialog.find('#roleDescript');

        dialog.find('#save').on('click', function () {
            $(this).attr('disabled', 'disabled');
            save(item);
        }).end().modal({
            keyboard: false
        }).on({
            'hidden.bs.modal': function () {
                $(this).remove();
            },
            'complete': function () {
                dataGrid.message({
                    type: 'success',
                    content: '保存成功'
                });
                $(this).modal('hide');
                dataGrid.grid('refresh');
            }
        });
    };

    /*
     *设置值
     */
    var setData = function (item) {
        console.log(JSON.stringify(item));
        name.val(item.name);
        description.val(item.description);
    };

    /*
     *   保存数据 id存在则为修改 否则为新增
     */
    var save = function (item) {
        /*	if(!validate(item)){
         dialog.find('#save').removeAttr('disabled');
         return false;
         }*/
        var url = baseUrl + 'update.koala';
        $.post(url, getAllData(item)).done(function (data) {
            if (data.success) {
                dialog.trigger('complete');
            } else {
                dialog.find('.modal-content').message({
                    type: 'error',
                    content: data.errorMessage
                });
            }
            dialog.find('#save').removeAttr('disabled');
        });
    };

    /**
     * 数据验证
     */
    var validate = function (item) {
        if (!Validation.notNull(dialog, roleName, roleName.val(), '请输入角色名称')) {
            return false;
        }
        return true;
    };

    /**
     * 获取表单数据
     */
    var getAllData = function (item) {
        var data = {};
        data['name'] = name.val();
        data['description'] = description.val();
        if (item) {
            data['id'] = item.id;
        }
        return data;
    };
    var assignRole = function (roleId, name) {
        openTab('/pages/auth/user-list.jsp',
            name + '的用户管理',
            'userManager_' + roleId, roleId, {roleId: roleId});
    };
    /**
     * 资源授权
     */
    var assignResource = function (grid, roleId) {
        $.get(contextPath + '/pages/auth/menu-select.jsp').done(function (data) {
            var dialog = $(data);
            initResourceTree(dialog, roleId);
            dialog.find('.save').on('click', function () {
                var treeObj = $(".resourceTree").getTree();
                var nodes = treeObj.selectedItems();
                $.each(nodes, function (index) {
                    delete nodes[index].open;
                    nodes[index].name = nodes[index].title;
                    delete nodes[index].title;
                });
                var url = baseUrl + 'grantMenuResourcesToRole.koala?roleId=' + roleId;
                for (var i = 0, j = nodes.length; i < j; i++) {
                    data += "&menuResourceIds=" + nodes[i].id;
                }
                $.post(url, data).done(function (data) {

                    if (data.success) {
                        grid.message({
                            type: 'success',
                            content: '保存成功'
                        });
                        dialog.modal('hide');
                    } else {
                        dialog.find('.modal-content').message({
                            type: 'error',
                            content: data.errorMessage
                        });
                    }
                }).fail(function (data) {
                    dialog.find('.modal-content').message({
                        type: 'error',
                        content: '保存失败'
                    });
                });

            }).end().modal({
                keyboard: false
            }).on({
                'hidden.bs.modal': function () {
                    $(this).remove();
                },
                'complete': function () {
                    dataGrid.message({
                        type: 'success',
                        content: '保存成功'
                    });
                    $(this).modal('hide');
                    dataGrid.grid('refresh');
                }
            });
        });
    };

    /*
     * 加载资源树
     */
    var initResourceTree = function (dialog, roleId) {
        $.get(contextPath + '/auth/role/findMenuResourceTreeSelectItemByRoleId.koala?time=' + new Date().getTime() + '&roleId=' + roleId).done(function (result) {
            var zNodes = new Array();
            var items = result.data;
            if (!items) {
                return;
            }

            for (var i = 0, j = items.length; i < j; i++) {
                var item = items[i];
                var zNode = {};
                var menu = {};
                menu.id = item.id;
                menu.title = item.name;
                menu.open = true;//是否打开树
                menu.checked = item.checked;
                menu.identifier = item.identifier;
                zNode.menu = menu;
                if (item.children && item.children.length > 0) {
                    zNode.children = getChildrenData(new Array(), item.children);
                }
                zNode.type = item.menuType == '1' ? 'children' : 'parent';
                zNodes.push(zNode);
            }
            var dataSourceTree = {
                data: zNodes,
                delay: 400
            };
            dialog.find('.resourceTree').tree({
                dataSource: dataSourceTree,
                loadingHTML: '<div class="static-loader">Loading...</div>',
                multiSelect: true,
                useChkBox: true,
                cacheItems: true
            });
        });
    };

    /**
     *
     */
    var getChildrenData = function (nodes, items) {
        for (var i = 0, j = items.length; i < j; i++) {
            var item = items[i];
            var zNode = {};
            var menu = {};
            menu.id = item.id;
            menu.title = item.name;
            menu.open = true;
            menu.checked = item.checked;
            menu.identifier = item.identifier;
            zNode.menu = menu;
            if (item.children && item.children.length > 0) {
                zNode.children = getChildrenData(new Array(), item.children);
            }

            zNode.type = item.menuType == '1' ? 'children' : 'parent';
            nodes.push(zNode);
        }
        return nodes;
    };

    return {
        add: add,
        modify: modify,
        deleteRole: deleteRole,
        assignRole: assignRole,
        assignResource: assignResource
    };
};
