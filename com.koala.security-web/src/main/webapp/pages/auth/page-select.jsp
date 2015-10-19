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
				<h4 class="modal-title">分配页面元素资源</h4>
			</div>
			<div class="modal-body" style="padding-left:45px; padding-right:65px;">
                <form id=“selectPageFrom” target="_self" class="form-inline searchCondition">
                    <div class="panel">
                        <table border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td>
                                    <div class="form-group">
                                        <label class="control-label">页面元素名称:&nbsp;</label>
                                        <input name="name" class="form-control" type="text" style="width:180px;"/>

                                        <label class="control-label">页面元素标识:&nbsp;</label>
                                        <input name="identifier" class="form-control" type="text" style="width:180px;"/>

                                        <td>
                                            <button id="search" type="button" class="btn btn-success glyphicon glyphicon-search"></button>
                                        </td>
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </div>
                </form>
				<div id="selectPageGrid" data-role="selectPageGrid"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
				<button type="button" class="btn btn-success" id="save">保存</button>
			</div>
		</div>
	</div>
</div>