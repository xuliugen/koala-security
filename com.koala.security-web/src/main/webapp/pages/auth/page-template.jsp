<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<div class="modal fade menu">
     <style>
        .menu .form-control {
            width: 95%;
        }
       .modal-footer{
       padding:10px 20px;
       }
    </style>
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">添加页面元素</h4>
            </div>
            <div class="modal-body" style="padding-left:45px; padding-right:65px; height: 250px;">
                <form class="form-horizontal page_form">
                    <div class="form-group">
                        <label class="col-lg-3 control-label" for="name">页面元素名称:</label>
                        <div class="col-lg-9">
                            <input type="text" class="form-control" name="name"><span class="required">*</span>
                        </div>
                    </div>
                   <div class="form-group">
                        <label class="col-lg-3 control-label" for="identifier">页面元素标识:</label>
                        <div class="col-lg-9">
                            <input type="text" class="form-control" name="identifier"><span class="required">*</span>
                        </div>
                    </div>
                       <div class="form-group">
                        <label class="col-lg-3 control-label" for="description">页面元素描述:</label>
                        <div class="col-lg-9">
                            <input type="text" class="form-control" name="description"/>
                        </div>
                    </div>
		            <div class="modal-footer">
		                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
		                <button type="button" class="btn btn-success save">保存</button>
		            </div>
                </form>
            </div>
        </div>
    </div>
</div>
