<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <%@ include file="/inc/includeOldBase.jsp" %>
    </head>
    <body>
        <display:table name="list" requestURI="${app}/uploaddata/tasklist.do" class="list" id="row" style="width:100%"
            partialList="true" size="size" cellpadding="0" pagesize="${param.pageSize}" export="true" sort="external">
            <display:column title="任务编号" property="task_id"/>
            <display:column title="导入时间">
                <f:formatDate value="${row.create_time}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </display:column>
            <display:column title="导入工号" property="create_code"/>
            <display:column title="项目名称" property="remark"/>
            <display:column title="任务状态" property="stat"/>
            <display:column title="任务执行开始时间">
                <f:formatDate value="${row.begin_date}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </display:column>
            <display:column title="任务执行结束时间">
                <f:formatDate value="${row.end_date}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </display:column>
        </display:table>
        <div style="color:red;font-size: 16px;padding-left: 50px;padding-top: 0px;text-align: left">
            此导入功能建立任务后需等待几分钟，待后台程序执行入库任务完成后可在新建站导入明细菜单查看明细。
        </div>
    </body>
    <script type="text/javascript">
        $(document).ready(function(){
            window.parent.window.enable('#schbtn');
        });
    </script>
</html>