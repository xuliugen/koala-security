<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@include file="/commons/taglibs.jsp"%>

<div aria-hidden="false" style="display: block;" class="modal fade in" id="changeEmailOfUser">
    <div class="modal-dialog changeEmailOfUser" style="padding-top:80px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h4 class="modal-title">更改用户邮箱</h4></div>
            <div class="modal-body">
                <form class="form-horizontal" role="form">
                    <div class="form-group"><label for="oldEmail" class="col-lg-3 control-label">现在的邮箱地址:</label>
                        <div class="col-lg-9">
                            <p id="oldEmail" class="form-control-static">
                                <ks:user property="email"/>
                            </p>
                        </div>
                    </div>

                    <div class="form-group"><label for="newEmail" class="col-lg-3 control-label">新的邮箱地址:</label>
                        <div class="col-lg-9"><input class="form-control" style="width:80%;" id="newEmail" type="text"></div>
                    </div>

                    <div class="form-group"><label for="confirmPassword" class="col-lg-3 control-label">密码确认:</label>
                        <div class="col-lg-9"><input class="form-control" style="width:80%;" id="confirmPassword" type="password"></div>
                    </div>
                </form>
            </div>
            <div class="modal-footer" id="changeEmailOfUserMessage">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-success" data-toggle="save" id="changeEmailOfUserSave">保存</button>
            </div>
        </div>
    </div>
</div>