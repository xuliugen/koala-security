var userManager = function() {
	var baseUrl = contextPath + '/auth/user/';
	var dialog = null;
	//对话框
	var userName = null;
	//用户名
	var userAccount = null;
	//用户账号
	var description = null;
	//描述
	var isEnable = null;
	//是否启用
	var dataGrid = null;
	//Grid对象

	/*
	 *新增
	 */
	var add = function(grid) {
		dataGrid = grid;
		$.get(contextPath + '/pages/auth/user-template.jsp').done(function(data) {
			addInit(data);
		});
	};
	function addInit(data){
		dialog = $(data);
		userName = dialog.find('#userName');
		userAccount = dialog.find('#userAccount');
		description = dialog.find('#description');
		dialog.find('#save').on('click', function(){
			save();
		}).end().modal({
			keyboard : false
		}).on({
			'hidden.bs.modal' : function() {
				$(this).remove();
			},
			'complete' : function() {
				dataGrid.message({
					type : 'success',
					content : '保存成功'
				});
				$(this).modal('hide');
                dataGrid.grid('refresh');
            }
		});
	}
	/*
	 * 修改
	 */
	var modify = function(item, grid) {
		dataGrid = grid;
		$.get(contextPath + '/pages/auth/user-template.jsp').done(function(data) {
			init(data, item);
			setData(item);
		});
	};
	/*
	 * 重置密码
	 */
	var resetPassword = function(item, grid) {
		var dataGrid = grid;
		console.log(item.id);
		$.post(baseUrl + 'resetPassword.koala?userId=' + item.id).done(function(data) {
		    
			if (data.success) {				
				dataGrid.message({
					type : 'success',
					content : '重置密码成功，初始密码为888888！'
				});
			} else {
				dataGrid.message({
					type : 'error',
					content : data.errorMessage
				});
			}
		}).fail(function(data) {
			dataGrid.message({
				type : 'error',
				content : '重置密码失败'
			});
		});
	};
	/*
	 * 可用
	 */
	var available = function(item, grid) {
		var dataGrid = grid;
		$.post(baseUrl + 'activate.koala?userId=' + item.id).done(function(data) {
		    
			if (data.success == true) {				
				dataGrid.message({
					type : 'success',
					content : '用户现在可用！'
				});
				grid.grid('refresh');
			} else {
				dataGrid.message({
					type : 'error',
					content : data.errorMessage
				});
			}
		}).fail(function(data) {
			dataGrid.message({
				type : 'error',
				content : '可用失败'
			});
		});
	};
	/*
	 禁用
	 */
	var forbidden = function(item, grid) {
		var dataGrid = grid;
	    console.log(item.id);
		$.post(baseUrl + 'suspend.koala?userId=' + item.id).done(function(data) {
		    
			if (data.success == true) {				
				dataGrid.message({
					type : 'success',
					content : '用户禁用成功！'
				});
				grid.grid('refresh');
			} else {
				dataGrid.message({
					type : 'error',
					content : data.errorMessage
				});
			}
		}).fail(function(data) {
			dataGrid.message({
				type : 'error',
				content : '禁用失败'
			});
		});
	};

    /**
     * 删除多个用户
     * @param users 多个用户
     * @param grid
     */
	var deleteUser = function(users, grid) {

        var data = "";
        $.each(users, function(i, user){
            data += ("userIds=" + user.id + "&");
        });
        data = data.substring(0, data.length-1);

        dataGrid = grid;
		$.post(baseUrl + 'terminate.koala', data).done(function(data){
			if(data.success){
				dataGrid.message({
					type: 'success',
					content: '撤销成功'
				});
				dataGrid.grid('refresh');
			}else{
				$('body').message({
					type: 'error',
					content: data.errorMessage
				});
			}
		}).fail(function(data){
			dataGrid.message({
				type: 'error',
				content: '撤销失败'
			});
		});
	};

	/**
	 * 初始化
	 */
	var init = function(data, item) {
		dialog = $(data);
		dialog.find('.modal-header').find('.modal-title').html( item ? '修改用户信息' : '添加用户');
		userName = dialog.find('#userName');
		userAccount = dialog.find('#userAccount');
		description = dialog.find('#description');
		isEnable = dialog.find('[name="isEnable"]');
		isEnable.on('click', function() {
			isEnable.each(function() {
				$(this).parent().removeClass('checked');
			});
			$(this).parent().addClass('checked');
		});
		dialog.find('#save').on('click', function() {
			save(item);
		}).end().modal({
			keyboard : false
		}).on({
			'hidden.bs.modal' : function() {
				$(this).remove();
			},
			'complete' : function() {
				dataGrid.message({
					type : 'success',
					content : '保存成功'
				});
				$(this).modal('hide');
				dataGrid.grid('refresh');
			}
		});
	};

	/*
	 *设置值
	 */
	var setData = function(item) {
		userName.val(item.name);
		userAccount.val(item.userAccount).attr('disabled', 'disabled');
		description.val(item.description);
		if (!item.valid) {
			dialog.find('[name="isEnable"][value="true"]').removeAttr('checked', 'checked').parent().removeClass('checked');
			dialog.find('[name="isEnable"][value="false"]').attr('checked', 'checked').parent().addClass('checked');
		}
	};
	/*
	 *   保存数据 id存在则为修改 否则为新增
	 */
	var save = function(item) {
		if (!validate(item)) {
			dialog.find('#save').removeAttr('disabled');
			return false;
		}
		var url = baseUrl + 'add.koala';
		if (item) {
			url = baseUrl + 'update.koala';
		}
		
		$.post(url, getAllData(item)).done(function(data) {
			if (data.success) {
				dialog.trigger('complete');
			} else {
				dialog.find('.modal-content').message({
					type : 'error',
					content : data.errorMessage
				});
			}
			dialog.find('#save').removeAttr('disabled');
		});
	};

	/**
	 * 数据验证
	 */
	var validate = function(item) {
		if (!Validation.notNull(dialog, userName, userName.val(), '请输入用户名称')) {
			return false;
		}
		if (!Validation.notNull(dialog, userAccount, userAccount.val(), '请输入用户账号')) {
			return false;
		}
		if (!Validation.checkByRegExp(dialog, userAccount, '^[0-9a-zA-Z]*$', userAccount.val(), '用户帐号只能输入字母及数字')) {
			return false;
		}
		return true;
	};
	/*
	 *获取表单数据
	 */
	var getAllData = function(item) {
		var data = {};
		data['name'] = userName.val();
		data['userAccount'] = userAccount.val();
		data['description'] = description.val();
		if (item) {
			data['id'] = item.id;
		}
		return data;
	};

	return {
		add : add,
		modify : modify,
		deleteUser : deleteUser,
		resetPassword : resetPassword,
		available : available,
		forbidden : forbidden
	};
}; 