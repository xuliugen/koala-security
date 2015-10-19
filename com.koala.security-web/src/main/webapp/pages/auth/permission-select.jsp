<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<div class="modal fade select-role">
	<style>
		.select-role .modal-body {
			height: 420px;
		}
		.select-role .grid-table-body {
			height: 250px;
		}
		.select-role .modal-dialog {
			width: 850px;
		}
	</style>
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
					&times;
				</button>
				<h4 class="modal-title">分配权限</h4>
			</div>
			<div class="modal-body" style="padding-left:45px; padding-right:65px;">
                <form id="selectPermissionForm" target="_self" class="form-inline searchCondition">
                    <input type="hidden" class="form-control" name="page" value="0">
                    <input type="hidden" class="form-control" name="pagesize" value="10">

                    <div class="panel">
                        <table border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td>
                                    <div class="form-group">
                                        <label class="control-label">权限名称:&nbsp;</label>
                                        <input name="name" class="form-control" type="text"/>

                                        <label class="control-label">权限标识:&nbsp;</label>
                                        <input name="identifier" class="form-control" type="text"/>

                                    </div>
                                </td>
                                <td style="vertical-align: bottom; padding: 0 5px;">
                                    <button id="search" type="button" class="btn btn-success"><span class="glyphicon glyphicon-search"></span>&nbsp;
                                    </button>
                                </td>
                            </tr>
                        </table>
                    </div>
                </form>
				<div class="selectPermissionGrid" data-role="selectPermissionGrid"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
				<button type="button" class="btn btn-success" id="save">保存</button>
			</div>
		</div>
	</div>
</div>