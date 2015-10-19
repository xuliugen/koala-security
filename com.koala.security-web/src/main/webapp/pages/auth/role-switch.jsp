<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>

<div class="modal fade select-role">
	<style>
		.select-role .modal-body {
			height: 350px;
		}
		.select-role .grid-table-body {
			height: 250px;
		}
		.select-role .modal-dialog {
			width: 700px;
		}
	</style>
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">切换角色</h4>
			</div>
			<div class="modal-body" style="padding-left:45px; padding-right:65px;"><div class="selectRoleGrid"></div></div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
				<button type="button" class="btn btn-success" id="toggle">确定</button>
			</div>
		</div>
	</div>
</div>