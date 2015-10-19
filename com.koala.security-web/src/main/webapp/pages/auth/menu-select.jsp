<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<div class="modal fade menu-select">
	<style>
		.menu-select .modal-body {
		    height:320px;
			min-height: 320px;
		}
	</style>
    <div class="modal-dialog" style="width:620px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">分配菜单资源</h4>
            </div>
            <div class="modal-body" style="padding-left:45px; padding-right:65px;">
              	 <ul class="resourceTree tree" style="height:280px;min-height:280px;margin-top: 2px;"></ul>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-success save">保存</button>
            </div>
        </div>
    </div>
</div>