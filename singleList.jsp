<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <%@ include file="/inc/includeOldBase.jsp" %>
    </head>
    <body>
        <display:table name="list" requestURI="${app}/uploaddata/singleReportList.do" class="list" id="row"
                       style="width:100%" cellpadding="0" export="true" sort="external">
            <display:column title="日期" property="type"/>
            <c:forEach var="one" items="${daylist}" varStatus="vs1">
                <display:column  property="day${one.daytime}" title="${one.daytime.substring(4,6)}月${one.daytime.substring(6)}日"/>
            </c:forEach>
        </display:table>
        <table style="width:100%" class="search_table" cellspacing="1" cellpadding="1" border="1">
            <tr height="25" style="background-color: #FFFFFF;">
                <td colspan="6" style="text-align: center;">统计小区</td>
            </tr>
            <tr height="25" style="background-color: #FFFFFF;">
                <td class="outDetail" style="width: 15%;text-align: center;">CellName</td>
                <td class="outDetail" style="width: 25%;text-align: center;">小区名称</td>
                <td class="outDetail" style="width: 10%;text-align: center;">基站类型</td>
                <td class="outDetail" style="width: 15%;text-align: center;">CellName</td>
                <td class="outDetail" style="width: 25%;text-align: center;">小区名称</td>
                <td class="outDetail" style="width: 10%;text-align: center;">基站类型</td>
            </tr>
            <c:forEach var="item" items="${celllist}" varStatus="vs">
                <c:if test="${vs.index%2==0}">
                    <tr class="pageSearch" height="25" >
                        <td class="outDetail" style="text-align: center;">${item.cellname}</td>
                        <td class="outDetail" style="text-align: center;">${item.xq_name}</td>
                        <td class="outDetail" style="text-align: center;">${item.type}</td>
                </c:if>
                <c:if test="${vs.index%2==1}">
                        <td class="outDetail" style="text-align: center;">${item.cellname}</td>
                        <td class="outDetail" style="text-align: center;">${item.xq_name}</td>
                        <td class="outDetail" style="text-align: center;">${item.type}</td>
                    </tr>
                </c:if>
            </c:forEach>
            <c:if test="${celllist.size()%2==1}">
                    <td class="outDetail" style="text-align: center;"></td>
                    <td class="outDetail" style="text-align: center;"></td>
                    <td class="outDetail" style="text-align: center;"></td>
                </tr>
            </c:if>
        </table>
    </body>
    <script type="text/javascript">
        $(document).ready(function(){
            window.parent.$("#loadImg").hide();
            window.parent.$("#schbtn").removeAttr("disabled");
        });
    </script>
</html>