package web.action.uploaddata;

import com.njry.util.common.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import web.action.BaseController;
import web.service.uploaddata.UploadDataService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping(value="/uploaddata/")
public class UploadDataController extends BaseController {
    @Autowired
    private UploadDataService uploaddataService;

    private MultipartFile file;
    private String file_name;

    /**
     * 导入任务查询页面
     * @return
     */
    @RequestMapping(value="taskframe.do")
    public String taskframe(){
        request.setAttribute("day_time", uploaddataService.getNearTaskDaytime());
        return  COM_PATH+"/uploaddata/task/frame";
    }


    /**
     * 任务进度展示
     * @return
     */
    @RequestMapping(value="tasklist.do")
    public String tasklist(){
        request.setAttribute("list",uploaddataService.getUploadTasksList(request));
        return  COM_PATH+"/uploaddata/task/list";
    }

    /**
     * 导入页面
     * @return
     */
    @RequestMapping(value="impframe.do")
    public String impframe(){
        request.setAttribute("list",uploaddataService.getUploadTablesList());
        return  COM_PATH+"/uploaddata/task/import";
    }


    /**
     * 保存导入
     * @param request
     */
    @RequestMapping(value="savTask.do")
    public String savTask(HttpServletRequest request){
        request.setAttribute("impResult", uploaddataService.savTask(request));
        return COM_PATH+"/uploaddata/task/importResult";
    }

    /**
     * 导入明细查询
     * @return
     */
    @RequestMapping(value="detailframe.do")
    public String detailframe(){
        String config_id = req.getValue(request,"config_id");
        System.err.println("config_id == " +config_id);
        Map<String,Object> tableMap = uploaddataService.getTable(config_id);
        String table_title = (String) tableMap.get("REMARK");
        request.setAttribute("table_title", table_title);
        return  COM_PATH+"/uploaddata/detail/frame";
    }

    /**
     * 导入明细查询列表
     * @return
     */
    @RequestMapping(value="detaillist.do")
    public String detaillist(){
        request.setAttribute("list",uploaddataService.getUploadDetailsList(request));
        return  COM_PATH+"/uploaddata/detail/list";
    }

    /**
     * 导出模板
     * @param request
     * @param response
     */
    @RequestMapping(value = "exportTemplate.do")
    public void exportTemplate(HttpServletRequest request, HttpServletResponse response) {
        uploaddataService.exportTemplate(request, response);
    }

    /**
     * 单站点报表
     * @return
     */
    @RequestMapping(value = "singleReportFrame.do")
    public String singleReportFrame(){
        request.setAttribute("day_time", uploaddataService.getNear4GDaytime());
        return  COM_PATH+"/uploaddata/report/singleFrame";
    }

    @RequestMapping(value = "singleReportList.do")
    public String singleReportList(){
        request.setAttribute("list", uploaddataService.getSingleReportList(request));
        return  COM_PATH+"/uploaddata/report/singleList";
    }

    /**
     * 微区域报表
     * @return
     */
    @RequestMapping(value = "miniAreaReportFrame.do")
    public String miniAreaReportFrame(){
        request.setAttribute("day_time", uploaddataService.getNear4GDaytime());
        return  COM_PATH+"/uploaddata/report/miniFrame";
    }

    @RequestMapping(value = "miniAreaReportList.do")
    public String miniAreaReportList(){
        request.setAttribute("list", uploaddataService.getMiniAreaReportList(request));
        return  COM_PATH+"/uploaddata/report/miniList";
    }
}