<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>新建站导入数据入口</title>
        <%@ include file="/inc/includeWebIEBase.jsp"%>
        <%@ include file="/inc/includeBase.jsp"%>
        <script src="${app}/js/My97DatePicker/WdatePicker.js"></script>
    </head>
    <body>
        <form id="form1" name="form1" method="post" action="${app}/uploaddata/tasklist.do" target="listQuery" >
            <div class="page-body">
                <div class="row">
                    <div class="col-xs-12 col-md-12">
                        <div class="widget">
                            <div class="widget-header bordered-bottom bordered-blue">
                                <div class="widget-header">
                                    <div class="page-header position-relative">
                                        <div class="header-title">
                                            <h1>新建站导入数据入口</h1>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="widget-body">
                                <div role="grid" id="simpledatatable_wrapper"
                                     class="dataTables_wrapper form-inline no-footer">

                                    <div style="position: relative;">
                                        <table class="search_table" cellspacing="0" cellpadding="0">
                                            <tr class="pageSearch" height="25" >
                                                <td class="outDetail" style="width: 100px;" >
                                                    导入日期:
                                                </td>
                                                <td class="outDetail2">
                                                    <div style="float:left;">
                                                        <div id="simpledatatable_filter" class="dataTables_filter">
                                                            <label>
                                                                <input style="width:140%;" type="text" name="create_time" id="create_time" value="${day_time}" class="form-control input-sm" size="10" onfocus="WdatePicker();" readonly="readonly" />
                                                            </label>
                                                        </div>
                                                    </div>
                                                </td>
                                            </tr>
                                        </table>
                                        <div id="queryPanel_footer" class="queryPanel_footer" style="position: absolute; right: 0px; bottom: 4px;">
                                            <input type="hidden" id="pageSize" name="pageSize" value="10" />
                                            <a class="btn btn-blue" onclick="sch();return false;">&nbsp;查 询</a>
                                            <a class="btn btn-blue" onclick="add();return false;">&nbsp;导 入</a>
                                        </div>
                                    </div>
                                    <div id="listPanel" class="queryPanel">
                                        <div id="queryPanel_content" class="queryPanel_content">
                                            <iframe id="listQuery" name="listQuery" frameborder="0" height="460px" width="100%" scrolling="auto"></iframe>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </form>

        <script type="text/javascript">
            //先调用设置行数的函数，再调用默认查询函数
            $(function() {
                //先调用设置行数的函数，再调用默认查询函数
                sch();
            });

            function sch(){
                form1.submit();
            }

            //新增覆盖信息点
            function add() {
                MyWindow.OpenCenterWindowScroll('${app}/uploaddata/impframe.do','',650,800)
            }

        </script>
    </body>
</html>
