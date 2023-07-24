package web.service.uploaddata;

import com.njry.util.common.DateHelper;
import com.njry.util.common.FileHelper;
import com.njry.util.db.BatchSql;
import com.njry.util.db.ProcHelper;
import com.njry.util.excel.HssfHelper;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import web.service.BaseService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("uploaddataService")
public class UploadDataService extends BaseService {
    private String sql;
    private static final double EARTH_RADIUS = 6371.01;

    /**
     * 可导入项目表名
     * @return
     */
    public List<Map<String,Object>> getUploadTablesList(){
        sql = " select * from t_upload_table_config where status = 1 and config_id not in('1010','1011','1012','1013') order by config_id ";
        return db.queryForList(sql);
    }
    

    /**
     * 获取表名
     * @param config_id
     * @return
     */
    public Map<String,Object> getTable(String config_id){
        sql = " select * from t_upload_table_config where config_id = ? and status = 1 ";
        return db.queryForMap(sql, new Object[]{config_id});
    }

    /**
     * 获取表字段
     * @param config_id
     * @return
     */
    public List<Map<String,Object>> getTableColumns(String config_id){
        sql = " select * from t_upload_table_columns where config_id = ? and status = 1 order by show_order ";
        return db.queryForList(sql, new Object[]{config_id});
    }

    /**
     * 任务最新建立时间
     * @return
     */
    public String getNearTaskDaytime(){
        sql = " select max(to_char(create_time ,'yyyy-MM-dd')) from t_upload_task ";
        return db.queryForString(sql);
    }

    /**
     * 任务最新建立时间
     * @return
     */
    public String getlltjNearTaskDaytime(){
        sql = " select max(to_char(create_time ,'yyyy-MM-dd')) from t_upload_task " +
                " where config_id in('1010','1011','1012','1013')";
        return db.queryForString(sql);
    }

    /**
     * 4G流量最新导入日期
     * @return
     */
    public String getNear4GDaytime(){
        sql = " select max(to_char(to_date(column1,'yyyy-MM-dd') ,'yyyy-MM-dd')) from t_newstation_data_1003 ";
        return db.queryForString(sql);
    }

    /**
     * 导入任务查询
     * @param request
     * @return
     */
    public List<Map<String,Object>> getUploadTasksList(HttpServletRequest request){
        String create_time = req.getValue(request,"create_time");
        int pageSize = req.getPageSize(request, "pageSize"); //分页
        List<String> params = new ArrayList<String>();
        sql = " select a.*, b.remark, decode(a.status,1,'已导入文件',2,'导入数据中', 5, '导入失败',6, '数据校验中',8,'导入完成') stat "
                + "   from t_upload_task a, t_upload_table_config b "
                + "  where a.config_id = b.config_id "
                + "    and to_char(a.create_time,'yyyy-MM-dd') = ? and b.config_id not in('1010','1011','1012','1013') " ;
        params.add(create_time);
        sql += "order by task_id desc ";
        return db.getForList(sql, params, pageSize, request);
    }

    /**
     * 保存任务
     * @param request
     * @return
     */
    public int savTask(HttpServletRequest request){
        String config_id = req.getValue(request,"config_id");
        String create_code = this.getOperatorId(request);
        MultipartHttpServletRequest mpt = (MultipartHttpServletRequest) request;
        MultipartFile file = mpt.getFile("file");
        String file_name = file.getOriginalFilename();
        String new_file_name = create_code + "_" + DateHelper.getToday("yyyyMMddHHmmss")
                + "_" + Math.round(Math.random() * 1000)
                + "." + file_name.substring(file_name.lastIndexOf(".") + 1).toLowerCase();
        String file_url = "/wxdata/wxjt/websites/wxzt/files/upload/uploaddata/" + DateHelper.getToday("yyyyMM") + "/";
        FileHelper fileHelper = new FileHelper();
        if (file == null){
            return -1;
        }
        try {
            fileHelper.copyFile(file,file_url,new_file_name);
        } catch (Exception e) {
            return 0;
        }
        String task_id = db.getNextSequenceValue("seq_t_upload_task");
        String tablename = "TMP_" + create_code + "_" + DateHelper.getToday("yyyyMMddHHmmss");
        List<Map<String, Object>> collist = getTableColumns(config_id);
        String cols = "";
        for (Map<String, Object> map : collist) {
            cols += str.get(map, "column_name") + " " + str.get(map, "coltype") + ",";
        }
        cols += " RECORD_DATE date default sysdate, "
            + " RECORD_CODE varchar2(64) default '"+create_code+"' ";
        System.err.println("cols===="+cols);
        BatchSql batchSql = new BatchSql();
        sql = " create table " + tablename + " (" + cols + ")";
        batchSql.addBatch(sql);
        sql = " insert into t_upload_task(task_id, config_id, file_url, file_name, status, create_time, create_code, tablename) "
                + " values(?, ?, ?, ?, 1, sysdate, ?, ?)";
        batchSql.addBatch(sql, new Object[]{task_id, config_id, file_url, new_file_name, create_code, tablename});
        sql = " insert into t_datax_imp_log(id, template_id, path, tablename, record_date, status) "
                + " values(?, ?, ?, upper(?), sysdate, 0)";
        batchSql.addBatch(sql, new Object[]{task_id, config_id, file_url + new_file_name, tablename});
        return db.doInTransaction(batchSql);
    }

    public List<Map<String,Object>> getUploadDetailsList(HttpServletRequest request){
        String config_id = req.getValue(request,"config_id");
        request.setAttribute("colsList",getTableColumns(config_id));
        String record_date = req.getValue(request,"record_date");
        List<String> params = new ArrayList<String>();//查询参数
        int pageSize = req.getPageSize(request, "pageSize"); //分页
        if ("1001".equals(config_id)){
            sql = " select * from T_NEWSTATION_DATA_1001 a where 1 = 1 ";
            String xq_name = req.getValue(request,"xq_name");
            if (!"".equals(xq_name)){
                sql += " and (COLUMN4 = ? or COLUMN5 = ?) ";
                params.add(xq_name);
                params.add(xq_name);
            }
            return db.getForList(sql, params, pageSize, request);
        } else if ("1002".equals(config_id)){
            sql = " select * from T_NEWSTATION_DATA_1002 a where 1 = 1 ";
            String xq_name = req.getValue(request,"xq_name");
            if (!"".equals(xq_name)){
                sql += " and (COLUMN4 = ? or COLUMN5 = ?) ";
                params.add(xq_name);
                params.add(xq_name);
            }
            return db.getForList(sql, params, pageSize, request);
        } else if ("1003".equals(config_id)){
            sql = " select * from T_NEWSTATION_DATA_1003 a where 1 = 1 ";
            String cellname = req.getValue(request,"cellname");
            String day_time = req.getValue(request,"day_time");
            if (!"".equals(cellname)){
                sql += " and COLUMN2 = ? ";
                params.add(cellname);
            }
            if (!"".equals(day_time)){
                sql += " and column1 = ? ";
                params.add(day_time);
            }
            sql += " order by column1 desc ";
            return db.getForList(sql, params, pageSize, request);
        } else if ("1004".equals(config_id)){
            sql = " select * from T_NEWSTATION_DATA_1004 a where 1 = 1 ";
            String cellname = req.getValue(request,"cellname");
            String day_time = req.getValue(request,"day_time");
            if (!"".equals(cellname)){
                sql += " and COLUMN4 = ? ";
                params.add(cellname);
            }
            if (!"".equals(day_time)){
                sql += " and column1 = ? ";
                params.add(day_time);
            }
            sql += " order by column1 desc ";
            return db.getForList(sql, params, pageSize, request);
        } else {
            Map<String,Object> tableMap = getTable(config_id);
            String table_name = (String) tableMap.get("FINAL_TABLE");
            sql = " select * from " + table_name + " a where to_char(record_date,'yyyy-MM-dd') = ? ";
            params.add(record_date);

            return db.getForList(sql, params, pageSize, request);
        }
    }

    public void exportTemplate(HttpServletRequest request, HttpServletResponse response){
        String config_id = req.getValue(request,"config_id");
        Map<String,Object> tableMap = getTable(config_id);
        String file_type = (String) tableMap.get("FILE_TYPE");
        String table_name = (String) tableMap.get("TABLE_NAME");
        List<Map<String, Object>> collist = getTableColumns(config_id);
        if ("csv".equals(file_type)){
            String[][] map = new String[collist.size()][];
            int i = 0;
            for (Map<String, Object> result : collist) {
                map[i] = new String[] { str.notEmpty(result.get("COLUMN_COMMENT")),
                        str.notEmpty(result.get("COLUMN_NAME")), "1"};
                i++;
            }
            sql = " select * from " + table_name + " a where 1 = 2 ";
            try {
                response.setCharacterEncoding("gbk");
                response.setHeader("Content-Disposition",
                        "attachment; filename=exp" + DateHelper.getToday("yyyyMMddHHmmss") + ".csv");
                HssfHelper.exportCsv(sql, map, response.getWriter());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if ("xlsx".equals(file_type)){
            HSSFWorkbook wb = new HSSFWorkbook();
            HssfHelper hssfHelper = new HssfHelper();

            HSSFFont font = wb.createFont(); // 设置字体的样式
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // 字体加粗

            HSSFCell cell = null;
            HSSFRow row = null;

            HSSFSheet sheet = wb.createSheet("Sheet1");
            wb.setSheetName(0, "Sheet1");
            row = sheet.createRow((short) 0);

            String[][] map = null;
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            int i = 0;
            for (Map<String, Object> result : collist) {
                cell = hssfHelper.createHeadCell(wb, row, i);
                cell.setCellValue(str.notEmpty(result.get("COLUMN_COMMENT")));
                i++;
            }
            hssfHelper.export2(wb, list, map, 1, 0, 0);
            try {
                writeExcel(wb, response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeExcel(HSSFWorkbook wb, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition",
                "attachment;filename=result.xlsx");
        wb.write(response.getOutputStream());
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    public List<Map<String,Object>> getSingleReportList(HttpServletRequest request){
        String xq_name = req.getValue(request,"xq_name");
        String v_daytime = req.getValue(request,"day_time");
        List<Map<String,Object>> list = new ArrayList<>();
        sql = " select to_char((to_date(?,'yyyy-mm-dd') -7) +(level-1),'yyyymmdd') as daytime from dual "
                + " connect by level <= ((to_date(?,'yyyy-mm-dd') + 7) - (to_date(?,'yyyy-mm-dd')-7)) + 1 ";
        List<Map<String,Object>> daylist = db.queryForList(sql, new Object[]{v_daytime, v_daytime, v_daytime});
        request.setAttribute("daylist", daylist);
        sql = " select * from T_NEWSTATION_DATA_1002 where  COLUMN4 = ? or COLUMN5 = ? ";
        Map<String,Object> map = db.queryForMap(sql, new Object[]{xq_name, xq_name});
        String v_longitude = "";
        String v_latitude = "";
        if (map.isEmpty()){
            sql = " select * from T_NEWSTATION_DATA_1001 where  COLUMN4 = ? or COLUMN5 = ? ";
            map = db.queryForMap(sql, new Object[]{xq_name, xq_name});
            if (map.isEmpty()){
                return list;
            } else {
                v_longitude = (String) map.get("COLUMN10");
                v_latitude = (String) map.get("COLUMN11");
            }
        } else {
            v_longitude = (String) map.get("COLUMN13");
            v_latitude = (String) map.get("COLUMN14");
        }

        String operator_id = this.getOperatorId(request);
        String v_tablename = "TMP_"+operator_id+"_"+DateHelper.getToday("MMddHHmmss");
        String v_celltb = "TMPCELL_"+operator_id+"_"+DateHelper.getToday("MMddHHmmss");
        getNearCellnameList(v_longitude,v_latitude,v_celltb, 0);

        int proc_result = 1;
        ProcHelper proc = null ;
        proc = db.getProcHelper("ry_p_single_report");
        proc.setVarcharParam("v_tablename");
        proc.setVarcharParam("v_daytime");
        proc.setVarcharParam("v_longitude");
        proc.setVarcharParam("v_latitude");
        proc.setVarcharOutParam("v_proc_result");
        proc.setValue("v_tablename", v_tablename);
        proc.setValue("v_daytime", v_daytime);
        proc.setValue("v_longitude", v_longitude);
        proc.setValue("v_latitude", v_latitude);
        try {
            Map<String, Object> procMap = (Map<String, Object>) proc.execute();
            proc_result = Integer.valueOf(str.get(procMap, "v_proc_result"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.setAttribute("proc_result", proc_result);
        if (proc_result == 1) {
            sql = " select * from " + v_tablename;
            list = db.queryForList(sql);
            sql = " select * from " + v_celltb;
            request.setAttribute("celllist",db.queryForList(sql));
            db.update(" drop table " + v_tablename + " purge ");
            db.update(" drop table " + v_celltb + " purge ");
            return list;
        }else {
            return null;
        }
    }

    public static double distance(double startLat, double startLong,
                                  double endLat, double endLong) {
        double dLat  = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));
        startLat = Math.toRadians(startLat);
        endLat   = Math.toRadians(endLat);
        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    public static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    public void getNearCellnameList(String v_longitude,String v_latitude,String tablename, double maxdistance) {
        sql = " select * from T_NEWSTATION_DATA_1002 ";
        List<Map<String,Object>> list1 = db.queryForList(sql);
        sql = " select * from T_NEWSTATION_DATA_1001 ";
        List<Map<String,Object>> list2 = db.queryForList(sql);
        double distance = 0;
        double latitude = 0;
        double longitude = 0;
        BatchSql batchSql = new BatchSql();
        sql = " create table " + tablename + "(cellname varchar2(128), xq_name varchar2(128), "
            + "        distance varchar2(64), longitude varchar2(64), latitude varchar2(64), type varchar2(64)) ";
        batchSql.addBatch(sql);
        System.err.println("beginDate is: " + DateHelper.getToday("yyyy-MM-dd HH:mm:ss"));
        for (Map<String,Object> map:list1){
            latitude = Double.parseDouble((String) map.get("COLUMN14"));
            longitude = Double.parseDouble((String) map.get("COLUMN13"));
            distance = distance(Double.parseDouble(v_latitude), Double.parseDouble(v_longitude), latitude, longitude);
            if (distance <= maxdistance){ //与小区距离在1KM之内的
                //记录小区、距离   计算平均值  报表展示平均值
                sql = " insert into " + tablename + "(cellname, xq_name, distance, longitude, latitude, type) "
                    + " values(?, ?, round(?), ?, ?, '5G基站')";
                batchSql.addBatch(sql, new Object[]{(String) map.get("column4"), (String) map.get("column5"), distance*1000, longitude, latitude});
            }
        }
        for (Map<String,Object> map:list2){
            latitude = Double.parseDouble((String) map.get("COLUMN11"));
            longitude = Double.parseDouble((String) map.get("COLUMN10"));
            distance = distance(Double.parseDouble(v_latitude), Double.parseDouble(v_longitude), latitude, longitude);
            if (distance <= maxdistance){ //与小区距离在1KM之内的
                //记录小区、距离   计算平均值  报表展示平均值
                sql = " insert into " + tablename + "(cellname, xq_name, distance, longitude, latitude, type) "
                        + " values(?, ?, round(?), ?, ?, '4G基站')";
                batchSql.addBatch(sql, new Object[]{(String) map.get("column4"), (String) map.get("column5"), distance*1000, longitude, latitude});
            }
        }
        db.doInTransaction(batchSql);
    }

    public List<Map<String,Object>> getMiniAreaReportList(HttpServletRequest request){
        String xq_name = req.getValue(request,"xq_name");
        String v_daytime = req.getValue(request,"day_time");
        List<Map<String,Object>> list = new ArrayList<>();
        sql = " select to_char((to_date(?,'yyyy-mm-dd') -7) +(level-1),'yyyymmdd') as daytime from dual "
                + " connect by level <= ((to_date(?,'yyyy-mm-dd') + 7) - (to_date(?,'yyyy-mm-dd')-7)) + 1 ";
        List<Map<String,Object>> daylist = db.queryForList(sql, new Object[]{v_daytime, v_daytime, v_daytime});
        request.setAttribute("daylist", daylist);
        sql = " select * from T_NEWSTATION_DATA_1002 where  COLUMN4 = ? or COLUMN5 = ? ";
        Map<String,Object> map = db.queryForMap(sql, new Object[]{xq_name, xq_name});
        String v_longitude = "";
        String v_latitude = "";
        if (map.isEmpty()){
            sql = " select * from T_NEWSTATION_DATA_1001 where  COLUMN4 = ? or COLUMN5 = ? ";
            map = db.queryForMap(sql, new Object[]{xq_name, xq_name});
            if (map.isEmpty()){
                return list;
            } else {
                v_longitude = (String) map.get("COLUMN10");
                v_latitude = (String) map.get("COLUMN11");
            }
        } else {
            v_longitude = (String) map.get("COLUMN13");
            v_latitude = (String) map.get("COLUMN14");
        }
        String operator_id = this.getOperatorId(request);
        String v_celltb = "TMPCELL_"+operator_id+"_"+DateHelper.getToday("MMddHHmmss");
        getNearCellnameList(v_longitude,v_latitude,v_celltb, 1);
        String v_tablename = "TMP_"+operator_id+"_"+DateHelper.getToday("MMddHHmmss");
        sql = " select round(avg(distance)) from " + v_celltb;
        String avgdistance = db.queryForString(sql);
        request.setAttribute("avgdistance", avgdistance);

        int proc_result = 1;
        ProcHelper proc = null ;
        proc = db.getProcHelper("ry_p_miniarea_report");
        proc.setVarcharParam("v_tablename");
        proc.setVarcharParam("v_daytime");
        proc.setVarcharParam("v_celltb");
        proc.setVarcharOutParam("v_proc_result");
        proc.setValue("v_tablename", v_tablename);
        proc.setValue("v_daytime", v_daytime);
        proc.setValue("v_celltb", v_celltb);
        try {
            Map<String, Object> procMap = (Map<String, Object>) proc.execute();
            proc_result = Integer.valueOf(str.get(procMap, "v_proc_result"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.setAttribute("proc_result", proc_result);
        if (proc_result == 1) {
            sql = " select * from " + v_tablename;
            request.setAttribute("rptlist", db.queryForList(sql));
            sql = " select * from " + v_celltb + " order by type, distance ";
            list = db.queryForList(sql);
            db.update(" drop table " + v_tablename + " purge ");
            db.update(" drop table " + v_celltb + " purge ");
            return list;
        }else {
            return null;
        }
    }
}