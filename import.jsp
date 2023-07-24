<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
        <title>新建站数据导入</title>
        <%@ include file="/inc/includeBootstrapBase.jsp"%>
        <%@ include file="/inc/includeBase.jsp"%>
        <link href="${app}/js/bootstrap/js/sweetalert/css/normalize.css" rel="stylesheet" />
        <link href="${app}/js/bootstrap/js/sweetalert/css/popup.css" rel="stylesheet" />
        <script src="${app}/js/bootstrap/js/sweetalert/js/popup.js"></script>
    </head>
    <body >
        <form id="form1" method="post" class="form-horizontal bv-form" target="importResult"
              enctype="multipart/form-data" action="${app}/uploaddata/savTask.do">
            <!-- 内容 -->
            <div class="page-body">
                <div class="row">
                    <div class="col-xs-12 col-md-12">
                        <div class="widget" style="margin-bottom: 0px;">
                            <div class="widget-header bordered-bottom bordered-blue">
                                <div class="widget-header">
                                    <div class="page-header position-relative">
                                        <div class="header-title">
                                            <h1>新建站数据导入</h1>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="widget-body">
                                <div class="form-group has-feedback">
                                    <label class="col-sm-2 control-label no-padding-right">上传项目：</label>
                                    <div class="col-sm-10">
                                        <select id="config_id" name="config_id">
                                            <option value="">请选择···</option>
                                            <c:forEach items="${list}" var="item">
                                                <option value="${item.config_id}">${item.remark}</option>
                                            </c:forEach>
                                        </select>&nbsp;
                                    </div>
                                </div>
                                <div class="form-group has-feedback">
                                    <label class="col-sm-2 control-label no-padding-right">上传文件：</label>
                                    <div class="col-sm-7">
                                        <input type="file" name="file" id="file" style="width: 100%;"
                                               accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"/>&nbsp;
                                    </div>
                                    <div class="col-sm-1">
                                        <div style="margin-top: 0px;margin-right: 20px;">
                                            <button type="button" class="btn btn-blue" id="impBtn"
                                                    onclick="send(this);return false;">上&nbsp;传</button>
                                        </div>
                                    </div>

                                    <div class="col-sm-1">
                                        <div style="margin-top: 0px;margin-right: 40px;">
                                            <button type="button" class="btn btn-blue" id="exptbtn"
                                                    onclick="expt();return false;">模板下载</button>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <div class="col-lg-8" style="text-align: left;">
                                        <p style="color: red;margin: 10px 0 0 30px;">*1、请确认csv文件格式，内容，不能有复杂样式，不可有筛选等操作！</p>
                                        <p style="color: red;margin: 10px 0 0 30px;">*2、此功能只支持csv文件上传,如果是excel文件，请转存csv文件后再上传！</p>
                                        <p style="color: red;margin: 5px 0 0 30px;">*3、模板标题不能修改！</p>
                                    </div>
                                </div>

                                <div align="center">
                                    <img id="loadImg" src="${app}/images/loading/loading1.gif" style="display:none" />
                                </div>
                                <iframe id="importResult" name="importResult" frameborder="0"
                                        src="${app}/inc/base.jsp" width="100%" height="75%" scrolling="auto"></iframe>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form>

        <script type="text/javascript">
            $(function() {
                $('.widget-body').height($(document).height()*0.86);
            });

            //导入数据
            function send(src) {
                var file=$('#file').val();
                if(file==''){
                    alert("文件上传不能为空");
                    return false;
                }
                src.disabled = "true";
                // 重置导出结果页面
                $("#importResult").attr("src", "${app}/inc/base.jsp");
                var file = $("#file").val();
                if (!"" == file) {
                    $(src).removeAttr("disabled"); //开放按钮
                    return false;
                }

                if(form1.file.value.endWith("[cC][sS][vV]$")) {
                    $(window.frames["importResult"].document).find("#resultTab").css("display", "none");
                    $("#loadImg").show();

                    form1.action="${app}/uploaddata/savTask.do";
                    $("#form1").submit();
                } else {
                    alert('只能导入CSV件');
                    src.disabled=false;
                    return;
                }

            }

            // 导出模板
            function expt() {
                var config_id = $('#config_id').val();
                form1.action="${app}/uploaddata/exportTemplate.do?config_id=?";
                form1.submit();
            }
        </script>
    </body>
</html>
