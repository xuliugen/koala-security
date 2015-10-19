<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
<form class="form-horizontal">
	           <div class="form-group">
                    <label class="col-lg-3 control-label">姓名:</label>
                    <div class="col-lg-9">
                           <input name="name" style="display:inline; width:94%;" class="form-control"  type="text" dataType="Require" id="nameID" />
<span class="required">*</span>    </div>
</div>
	           <div class="form-group">
                    <label class="col-lg-3 control-label">性别:</label>
                    <div class="col-lg-9">
                           <input name="sex" style="display:inline; width:94%;" class="form-control"  type="text" dataType="Require" id="sexID" />
<span class="required">*</span>    </div>
</div>
	           <div class="form-group">
                    <label class="col-lg-3 control-label">身份证号码:</label>
                    <div class="col-lg-9">
                           <input name="identityCardNumber" style="display:inline; width:94%;" class="form-control"  type="text" dataType="Require" id="identityCardNumberID" />
<span class="required">*</span>    </div>
</div>
	           <div class="form-group">
                    <label class="col-lg-3 control-label">出生日期:</label>
                 <div class="col-lg-9">
                    <div class="input-group date form_datetime" style="width:160px;float:left;" >
                        <input type="text" class="form-control" style="width:160px;" name="birthday" id="birthdayID" dataType="Require">
                        <span class="input-group-addon"><span class="glyphicon glyphicon-th"></span></span>
                     </div>
<span class="required">*</span>    </div>
</div>
	           <div class="form-group">
                    <label class="col-lg-3 control-label">是否已婚:</label>
                    <div class="col-lg-9">
                           <div class="btn-group select" id="marriedID"></div>
	                       <input type="hidden" id="marriedID_" name="married" dataType="Require"/>
<span class="required">*</span>    </div>
</div>
	           <div class="form-group">
                    <label class="col-lg-3 control-label">收入:</label>
                    <div class="col-lg-9">
                           <input name="proceeds" style="display:inline; width:94%;" class="form-control"  type="text"  dataType="Number" require="false" id="proceedsID" />
    </div>
</div>
</form>
<script type="text/javascript">
    var selectItems = {};
                                            selectItems['marriedID'] = [
                   {title: '请选择', value: ''},
                   {title: '是', value: 'true'},
                   {title: '否', value: 'false'}
                ];
                </script>
</body>
</html>