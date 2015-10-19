<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="user-info" id="user-info">
    <table class="table table-bordered table-hover">
        <tr>
            <td width="25%">
                <label>姓名:</label>
                <span data-id="name"></span>
            </td>
            <td width="25%">
                <label>用户名称:</label>
                <span data-id="userAccount"></span>
            </td>
        </tr>
        <tr>
            <td>
                <label>创建时间:</label>
                <span data-id="createDate"></span>
            </td>
            <td>
                <label>最后更改时间:</label>
                <span data-id="lastModifyTime"></span>
            </td>
        </tr>
        <tr>
            <td>
                <label>邮箱:</label>
                <span data-id="email"></span>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a data-target="#changeEmailOfUser" onclick="" class="glyphicon glyphicon-pencil">修改</a>
            </td>
            <td>
                <label>联系电话:</label>
                <span data-id="telePhone"></span>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a data-target="#changeTelePhoneOfUser" onclick="" class="glyphicon glyphicon-pencil">修改</a>
            </td>
        </tr>
        <tr>
            <td>
                <label>是否可用:</label>
                <span data-id="disabled"></span>
            </td>
            <td>
                <label>描述:</label>
                <span data-id="description"></span>
            </td>
        </tr>
    </table>

</div>
<script>
    $(function () {
        $.get(contextPath + '/auth/currentUser/getUserDetail.koala').done(function (result) {
            var data = result.data;
            var userInfo = $('#user-info');
            userInfo.find('[data-id="name"]').text(data.name);
            userInfo.find('[data-id="userAccount"]').text(data.userAccount);
            userInfo.find('[data-id="createDate"]').text(data.createDate);
            userInfo.find('[data-id="lastModifyTime"]').text(data.lastModifyTime == null ? "" : data.lastModifyTime);
            userInfo.find('[data-id="email"]').text(data.email == null ? "您还没有邮箱，请添加邮箱！" : data.email);
            userInfo.find('[data-id="description"]').text(data.description == null ? "" : data.description);
            userInfo.find('[data-id="telePhone"]').text(data.telePhone == null ? "您还没有联系电话，请添加电话！" : data.telePhone);
            userInfo.find('[data-id="disabled"]').text(data.disabled ? "不可用" : "可用");
        });

        /* -------------------修改邮箱--------------------  */
        $('#user-info').find("a[data-target=#changeEmailOfUser]").click(function () {
            $.get(contextPath + '/pages/auth/user-changeEmail.jsp').done(function (data) {
                var dialog = $(data);
                var oldEmail = dialog.find('#oldEmail');
                var newEmail = dialog.find('#newEmail');
                var confirmPassword = dialog.find('#confirmPassword');
                dialog.find('#changeEmailOfUserSave').on('click', function () {
                    var data = {};
                    data['email'] = newEmail.val();
                    data['userPassword'] = confirmPassword.val();
                    $.post(contextPath + '/auth/currentUser/changeUserEmail.koala', data, function (data) {
                        if (data.success) {
                            dialog.find('#changeEmailOfUserMessage').message({
                                type: 'success',
                                content: '更改邮箱成功!'
                            });
                            $('#user-info').find('[data-id="email"]').text(newEmail.val());
                            dialog.modal('hide');
                        } else {
                            dialog.find('#changeEmailOfUserMessage').message({
                                type: 'error',
                                content: data.errorMessage
                            });
                        }
                    });
                }).end().modal({
                    keyboard: false
                }).on({
                    'hidden.bs.modal': function () {
                        $(this).remove();
                    },
                    'complete': function () {
                        dialog.message({
                            type: 'success',
                            content: '更改邮箱成功!'
                        });
                        $(this).modal('hide');
                    }
                });
                //兼容IE8 IE9
                if (window.ActiveXObject) {
                    if (parseInt(navigator.userAgent.toLowerCase().match(/msie ([\d.]+)/)[1]) < 10) {
                        dialog.trigger('shown.bs.modal');
                    }
                }

            });
        });
        /* -------------------修改电话--------------------  */
    });
    $(function () {
        $('#user-info').find("a[data-target=#changeTelePhoneOfUser]").click(function () {
            $.get(contextPath + '/pages/auth/user-changeTelePhone.jsp').done(function (data) {
                var dialog = $(data);
                var oldTelePhone = dialog.find('#oldTelePhone');
                var newTelePhone = dialog.find('#newTelePhone');
                var confirmPassword = dialog.find('#confirmPassword');

                dialog.find('#changeTelePhoneOfUserSave').on('click', function () {
                    var data = {};
                    data['telePhone'] = newTelePhone.val();
                    data['userPassword'] = confirmPassword.val();
                    $.post(contextPath + '/auth/currentUser/changeUserTelePhone.koala', data, function (data) {
                        if (data.success) {
                            dialog.find('#changeTelePhoneOfUserMessage').message({
                                type: 'success',
                                content: '更改联系电话成功!'
                            });
                            $('#user-info').find('[data-id="telePhone"]').text(newTelePhone.val());
                            dialog.modal('hide');
                        } else {
                            dialog.find('#changeTelePhoneOfUserMessage').message({
                                type: 'error',
                                content: data.errorMessage
                            });
                        }
                    });
                }).end().modal({
                    keyboard: false
                }).on({
                    'hidden.bs.modal': function () {
                        $(this).remove();
                    },
                    'complete': function () {
                        dialog.message({
                            type: 'success',
                            content: '更改联系电话成功!'
                        });
                        $(this).modal('hide');
                    }
                });
                //兼容IE8 IE9
                if (window.ActiveXObject) {
                    if (parseInt(navigator.userAgent.toLowerCase().match(/msie ([\d.]+)/)[1]) < 10) {
                        dialog.trigger('shown.bs.modal');
                    }
                }
            });
        });
    });
</script>