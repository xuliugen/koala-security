<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<form name=”permissionListForm“ id="${formId}" target="_self" class="form-horizontal searchCondition">
    <div id="${formId}_div" hidden="true">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td>
                    <div class="form-group">
                        <label class="control-label" style="width:100px;float:left;">权限名称:&nbsp;</label>

                        <div style="margin-left:15px;float:left;">
                            <input name="name" class="form-control" type="text" style="width:180px;"/>
                        </div>
                        <label class="control-label" style="width:100px;float:left;">权限标识:&nbsp;</label>

                        <div style="margin-left:15px;float:left;">
                            <input name="identifier" class="form-control" type="text" style="width:180px;"/>
                        </div>
                    </div>
                </td>
                <td style="vertical-align: bottom;">
                    <button id="permissionManagerSearch" type="button" style="position:relative; margin-left:35px; top: -15px"
                            class="btn btn-success"><span class="glyphicon glyphicon-search"></span>&nbsp;</button>
                </td>
            </tr>
        </table>
    </div>
</form>