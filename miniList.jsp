<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <%@ include file="/inc/includeOldBase.jsp" %>
    </head>
    <body>
        <display:table name="rptlist" requestURI="${app}/uploaddata/miniAreaReportList.do" class="list" id="row"
                       style="width:100%" cellpadding="0" export="true" sort="external">
            <display:column title="日期" property="type"/>
            <c:forEach var="one" items="${daylist}" varStatus="vs1">
                <display:column  property="day${one.daytime}" title="${one.daytime.substring(4,6)}月${one.daytime.substring(6)}日"/>
            </c:forEach>
        </display:table>
        <div style="color:red;font-size: 16px;padding-left: 50px;padding-top: 0px;text-align: left">
            距小区平均距离${avgdistance}米。以下展示1km范围内小区
        </div>
        <display:table name="list" requestURI="${app}/uploaddata/miniAreaReportList.do" class="list" id="row"
                       style="width:100%" cellpadding="0" export="false" sort="external">
            <display:column title="CellName" property="cellname"/>
            <display:column title="小区名称" property="xq_name"/>
            <display:column title="距离（m）" property="distance"/>
            <display:column title="经度" property="longitude"/>
            <display:column title="维度" property="latitude"/>
            <display:column title="基站类型" property="type"/>
        </display:table>
    </body>
    <script type="text/javascript">
        $(document).ready(function(){
            window.parent.$("#loadImg").hide();
            window.parent.$("#schbtn").removeAttr("disabled");
        });
    </script>
</html>