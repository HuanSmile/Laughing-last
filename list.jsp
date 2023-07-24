<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <%@ include file="/inc/includeOldBase.jsp" %>
    </head>
    <body>
        <display:table name="list" requestURI="${app}/uploaddata/detaillist.do" class="list" id="row"
            style="width:${(colsList.size()-2)*10+100}%" partialList="true" size="size" cellpadding="0"
            pagesize="${param.pageSize}" export="false" sort="external">
            <c:forEach items="${colsList}" var="item">
                <display:column title="${item.column_comment}" property="${item.column_name}"/>
            </c:forEach>
            <display:column title="导入时间">
                <f:formatDate value="${row.RECORD_DATE}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </display:column>
        </display:table>
        <div style="color:red;font-size: 16px;padding-left: 50px;padding-top: 0px;text-align: left">
            <c:if test="${param.config_id == '1001'||param.config_id == '1002'}">
                基站信息如变更，请通过“新建站导入数据入口”重新导入，此功能为覆盖新增，以CellName为准，如有重复则覆盖之前信息
            </c:if>
        </div>
    </body>
    <script type="text/javascript">
        $(document).ready(function(){
            window.parent.window.enable('#schbtn');
        });
    </script>
</html>