<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div id="permissionDetail">
    <!--
        ========================权限详细信息=========================
         -->
    <table class="table table-bordered table-hover">
        <tr>
            <td colspan="3">
                <label style="font-size:20px;">①权限详细信息</label>
            </td>
        </tr>
        <tr>
            <td width="33.333%">
                <label class="control-label">权限名称:</label>
                <span class="" data-id="permName"></span>
            </td>
            <td width="33.333%">
                <label class="control-label">权限标识:</label>
                <span class="" data-id="permIdentifier" ></span>
            </td>
            <td width="33.333%">
                <label class="control-label">权限描述:</label>
                <span class="" data-id="permDescription"></span>
            </td>
        </tr>
    </table>
    <!--
    ========================菜单=========================
     -->
    <table class="table table-bordered table-hover">
        <tr>
            <td colspan="3">
                <label style="font-size:20px; ">②菜单资源</label>
            </td>
        </tr>
        <tr>
            <td width="33.333%">
                <label class="control-label">菜单名称:</label>
                <span class="" data-id="menuName"></span>
            </td>
            <td width="33.333%">
                <label class="control-label">菜单URL:</label>
                <span class="" data-id="menuUrl" ></span>
            </td>
            <td width="33.333%">
                <label class="control-label">菜单描述:</label>
                <span class="" data-id="menuDescription"></span>
            </td>
        </tr>
    </table>
    <!-- ========================  URL访问资源  ========================= -->
    <table class="table table-bordered table-hover">
        <tr>
            <td colspan="3">
                <label style="font-size:20px; ">③URL访问资源</label>
            </td>
        </tr>
        <tr>
            <td width="33.333%">
                <label class="control-label">URL名称:</label>
                <span class="" data-id="urlName"></span>
            </td>
            <td width="33.333%">
                <label class="control-label">URL路径:</label>
                <span class="" data-id="urlPath" ></span>
            </td>
            <td width="33.333%">
                <label class="control-label">URL描述:</label>
                <span class="" data-id="urlDescription"></span>
            </td>
        </tr>
    </table>

    <table class="table table-bordered table-hover">
        <tr>
            <td colspan="3">
                <label style="font-size:20px; ">④页面元素资源</label>
            </td>
        </tr>
        <tr>
            <td width="33.333%">
                <label class="control-label">页面元素名称:</label>
                <span class="" data-id="pageName"></span>
            </td>
            <td width="33.333%">
                <label class="control-label">页面元素标识:</label>
                <span class="" data-id="pageIdentifier" ></span>
            </td>
            <td width="33.333%">
                <label class="control-label">页面元素描述:</label>
                <span class="" data-id="pageDescription"></span>
            </td>
        </tr>
    </table>

</div>
<script>
    $(function() {
        var permissionId = $('#permissionDetail').parent().attr('data-value')
        $.get(contextPath + '/auth/permission/findInfoOfPermission.koala?permissionId=' + permissionId).done(function (result) {
            var permission = result.data;
            var permissionDetail = $('#permissionDetail');
            permissionDetail.find('[data-id="permName"]').text(permission.name);
            permissionDetail.find('[data-id="permIdentifier"]').text(permission.identifier);
            permissionDetail.find('[data-id="permDescription"]').text(permission.description==null?"":permission.description);
            permissionDetail.find('[data-id="menuName"]').text(permission.menuResource.name);

            permissionDetail.find('[data-id="menuUrl"]').text(permission.menuResource.url == null ? "" : permission.menuResource.url);
            permissionDetail.find('[data-id="menuDescription"]').text(permission.menuResource.description==null?"":permission.menuResource.description);

            permissionDetail.find('[data-id="urlName"]').text(permission.urlAccessResource.name);
            permissionDetail.find('[data-id="urlPath"]').text(permission.urlAccessResource.url);
            permissionDetail.find('[data-id="urlDescription"]').text(permission.urlAccessResource.description==null?"":permission.urlAccessResource.description);

            permissionDetail.find('[data-id="pageName"]').text(permission.pageElementResource.name);
            permissionDetail.find('[data-id="pageIdentifier"]').text(permission.pageElementResource.identifier);
            permissionDetail.find('[data-id="pageDescription"]').text(permission.pageElementResource.description==null?"":permission.pageElementResource.description);

        });
    });
</script>