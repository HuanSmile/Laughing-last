<%@ page contentType="text/html; charset=UTF-8"%>
<html>
	<head>
		<title>导入结果</title>
		<%@ include file="/inc/includeBase.jsp"%>
		<%@ include file="/inc/includeBootstrapBase.jsp"%>
	</head>

	<body style="background-color: #F7F6F7;">
		<form id="form2" name="form2" method="post" action="" target="listQuery" >
			<table border="0" align="center" cellpadding="0" cellspacing="0" width="60%" id="resultTab">
				<tr height="20px">
					<td colspan="2" class="outDetail" align="center">
						<font color="red">${message}</font>&nbsp;
						<c:if test="${impResult == 1}">
							<strong style="font-size: 13px; color: red;font-weight: bold;">上传任务建立成功！</strong>
						</c:if>
						<c:if test="${impResult == 0}">
							<strong style="font-size: 13px;font-weight: bold;">上传失败！</strong>
						</c:if>
						<c:if test="${impResult == -1}">
							<strong style="font-size: 13px; color: blue;font-weight: bold;">上传文件为空！</strong>
						</c:if>
					</td>
				</tr>
			</table>
			<input type="hidden" id="pageSize" name="pageSize" value="" />
			<iframe id="listQuery" allowtransparency="true" name="listQuery" frameborder="0" 
		    		 			width="100%" scrolling="no" style="margin-top: 8px;"></iframe>	
		</form>
		
		<script type="text/javascript">
            $(document).ready(function(){
                window.parent.$("#loadImg").hide();
                window.parent.$("#impBtn").removeAttr("disabled");//开放按钮
                setPageSize('listQuery');
            });
		</script>
	</body>
</html>