package cn.jbolt.data.rental;


import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.model.Dictionary;
import cn.jbolt.common.model.Rental;
import cn.jbolt.common.util.CACHE;
import cn.jbolt.common.util.DateUtil;
import cn.jbolt.common.util.ExcelUtil;
import com.jfinal.aop.Before;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@CheckPermission({PermissionKey.RETALMANAGER})
public class RentalController extends BaseController {
    RentalService srv = RentalService.me;

    public void index() {
        Page<Rental> pageData = srv.paginate(getParaToInt("page", 1),getPara("keywords"),
                getPara("hasTaxControl"),getPara("companyAttrib"),getPara("payType"));
//        keepPara();
//		set("deleteCount", srv.getDeleteCount());
        set("pageData", pageData);
        render("index.html");
    }

    public void add() {
        render("add_edit.html");
    }
    /**
     * 修改
     */
    public void edit() {
        setAttr("rental", srv.edit(getParaToInt()));
        render("add_edit.html");
    }

    /**
     * 提交修改
     */
    @Before(RentalValidator.class)
    public void update() {
        Rental bean = getBean(Rental.class);
        bean.setModifyBy(getSessionAdminUserId());
        bean.setModifyTime(new Date());
        Ret ret = srv.update(bean, getSessionAdminUserId());
        renderJson(ret);
    }

    /**
     * 提交新增
     */
    @Before(RentalValidator.class)
    public void save() {
        Rental bean = getBean(Rental.class);
        bean.setCreator(getSessionAdminUserId());
        bean.setCreateTime(new Date());
        Ret ret = srv.save(bean);
        renderJson(ret);
    }

    public void delete() {
        Ret ret = srv.delete(getParaToInt(), getSessionAdminUserId());
        renderJson(ret);
    }

    public void importExcel(){
        UploadFile file = getFile("file");
        if(!checkExcel(file)){
            renderText("文件格式错误，请选择Excel文件（.xls或.xlsx)。");
            return;
        }
        try {
            Workbook workbook = WorkbookFactory.create(file.getFile());
            int size = workbook.getNumberOfSheets();
            LogKit.debug("Excel文件中有"+size+"个工作表也要处理。");
            Sheet sheet;
            Row row;
            Cell cell;
            List<Rental> rentalList = new ArrayList<>();
            Rental rental;
            for(int i=0; i<size; i++){
                sheet = workbook.getSheetAt(i);
                LogKit.debug("读取当前工作表名称："+sheet.getSheetName());
                int rowCount = sheet.getPhysicalNumberOfRows();
                LogKit.debug("有效行数："+rowCount);
                for(int rowIndex=0; rowIndex<rowCount; rowIndex++){
                    if(rowIndex==0){
                        continue;
                    }
                    row = sheet.getRow(rowIndex);
                    if(row != null) {
                        rental = new Rental();
                        if (row.getCell(1) != null) {
                            row.getCell(1).setCellType(CellType.STRING);
                            rental.setTaxId(getCellStringValue(row, 1));
                        }
                        rental.setCompanyName(getCellStringValue(row, 2));
                        rental.setRegAddress(getCellStringValue(row, 3));
                        rental.setRepresentativeName(getCellStringValue(row, 4));
                        rental.setRepresentativePhone(getCellStringValue(row, 5));
                        rental.setAgentNamePhone(getCellStringValue(row, 6));
                        rental.setFirstRentalDate(getCellStringValue(row, 7));
                        rental.setAddressFrom(getCellStringValue(row, 8));
                        rental.setCost(BigDecimal.valueOf(getCellNumericValue(row, 9)));
                        rental.setCostDate(DateUtil.getDate(getCellStringValue(row, 10), "yyyy.MM.dd"));
                        rental.setHasTaxControl(Db.queryInt("select id from dictionary where name='" + getCellStringValue(row, 11) + "' and typeId=13"));
                        rental.setCompanyAttrib(Db.queryInt("select id from dictionary where name='" + getCellStringValue(row, 12) + "' and typeId=14"));
                        rental.setCompanyProject(getCellStringValue(row, 13));
                        rental.setPayType(Db.queryInt("select id from dictionary where name='" + getCellStringValue(row, 14) + "' and typeId=15"));
                        rental.setMemo(getCellStringValue(row, 15));
                        rentalList.add(rental);
                    }
                }

            }

            if(rentalList.size()>0){
                //数据校验
                List<Rental> filterList = new ArrayList<>();
                for(Rental rental1:rentalList){
                    if(checkExist(rental1)) {
                        filterList.add(rental1);
                        rentalList.remove(rental1);
                    }
                }
                if(filterList.size()>0){
                    LogKit.debug("已存在数据库中的行数："+filterList.size());
                }
                Db.batchSave(rentalList, rentalList.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderText("Excel文件解析失败。");
        }
        redirect("/admin/data/rental");
    }

    private boolean checkExcel(UploadFile file){
        String fileName = file.getFileName();
        boolean isExcel = fileName.endsWith(".xls")||fileName.endsWith(".xlsx");
        if(!isExcel){
            return false;
        }
        try {
            if(POIFSFileSystem.hasPOIFSHeader(new FileInputStream(file.getFile()))){
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean checkExist(Rental rental){
        return Db.queryInt("select count(*) from rental where tax_id='"+rental.getTaxId()+"'")>0;
    }

    public static String getCellStringValue(Row row, int cellIndex){
        Cell cell = row.getCell(cellIndex);
        return  cell.getStringCellValue();
    }

    public static double getCellNumericValue(Row row, int cellIndex){
        Cell cell = row.getCell(cellIndex);
        return cell.getNumericCellValue();
    }

    public static int getCellIntValue(Row row, int cellIndex){
        Cell cell = row.getCell(cellIndex);
        return (int)cell.getNumericCellValue();
    }

    public void export(){
        String fileName = "租户信息表.xlsx";
        List<Rental> rentalList = Rental.dao.find("select * from rental order by id asc");
        HttpServletResponse response = getResponse();
        response.reset();
        response.setHeader("Content-disposition", "attachment; filename="+fileName);
//        response.setContentType("application/vnd.ms-excel");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        XSSFWorkbook wb = new XSSFWorkbook();

        OutputStream out = null;
        try {
            //创建Excel文件
            out = response.getOutputStream();
            processSheetData(rentalList,wb);
            wb.write(out);
            wb.close();
        }
        catch (IOException e) {
            LogKit.error(e.getMessage(), e);
        }
        finally {
            if(out !=null){
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    LogKit.error(e.getMessage(), e);
                }

            }
        }

        renderNull();
    }

    public static void main(String[] args){
        Workbook wb = new XSSFWorkbook();


        FileOutputStream out = null;
        try {
            //创建Excel文件
            out = new FileOutputStream("workbook.xlsx");

            wb.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processSheetData(List<Rental> rentalList, Workbook wb){
        Sheet sheet = wb.createSheet("工作表1");
        CellStyle borderStyle = wb.createCellStyle();
        borderStyle.setBorderBottom(CellStyle.BORDER_THIN);
        borderStyle.setBorderTop(CellStyle.BORDER_THIN);
        borderStyle.setBorderLeft(CellStyle.BORDER_THIN);
        borderStyle.setBorderRight(CellStyle.BORDER_THIN);
        borderStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        borderStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        borderStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        borderStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        borderStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER); //水平布局：居中
        sheet.autoSizeColumn((short)0);
        sheet.setColumnWidth(1, 256*30+184);
        sheet.setColumnWidth(2, 256*60+184);
        sheet.setColumnWidth(3, 256*60+184);

        ExcelUtil.createCell(sheet, 0, 0, CellType.STRING, borderStyle, null, "编号");
        ExcelUtil.createCell(sheet, 0, 1, CellType.STRING, borderStyle, null, "纳税人识别号");
        ExcelUtil.createCell(sheet, 0, 2, CellType.STRING, borderStyle, null, "公司名称");
        ExcelUtil.createCell(sheet, 0, 3, CellType.STRING, borderStyle, null, "注册地址");
        ExcelUtil.createCell(sheet, 0, 4, CellType.STRING, borderStyle, null, "法人");
        ExcelUtil.createCell(sheet, 0, 5, CellType.STRING, borderStyle, null, "联系电话");
        ExcelUtil.createCell(sheet, 0, 6, CellType.STRING, borderStyle, null, "代办人电话");
        ExcelUtil.createCell(sheet, 0, 7, CellType.STRING, borderStyle, null, "首租时间");
        ExcelUtil.createCell(sheet, 0, 8, CellType.STRING, borderStyle, null, "地址出处");
        ExcelUtil.createCell(sheet, 0, 9, CellType.STRING, borderStyle, null, "费用");
        ExcelUtil.createCell(sheet, 0, 10, CellType.STRING, borderStyle, null, "续费时间");
        ExcelUtil.createCell(sheet, 0, 11, CellType.STRING, borderStyle, null, "有无税控");
        ExcelUtil.createCell(sheet, 0, 12, CellType.STRING, borderStyle, null, "公司性质");
        ExcelUtil.createCell(sheet, 0, 13, CellType.STRING, borderStyle, null, "经营项目");
        ExcelUtil.createCell(sheet, 0, 14, CellType.STRING, borderStyle, null, "付款方式");
        ExcelUtil.createCell(sheet, 0, 15, CellType.STRING, borderStyle, null, "备注");

        int curRow = 1;
        CellStyle dataStyle = wb.createCellStyle();
        dataStyle.setBorderBottom(CellStyle.BORDER_THIN);
        dataStyle.setBorderTop(CellStyle.BORDER_THIN);
        dataStyle.setBorderLeft(CellStyle.BORDER_THIN);
        dataStyle.setBorderRight(CellStyle.BORDER_THIN);
        dataStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        dataStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        dataStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        dataStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        dataStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER); //水平布局：居中

        CellStyle redDataStyle = wb.createCellStyle();
        redDataStyle.setBorderBottom(CellStyle.BORDER_THIN);
        redDataStyle.setBorderTop(CellStyle.BORDER_THIN);
        redDataStyle.setBorderLeft(CellStyle.BORDER_THIN);
        redDataStyle.setBorderRight(CellStyle.BORDER_THIN);
        redDataStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        redDataStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        redDataStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        redDataStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        redDataStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER); //水平布局：居中

        // 设置字体
        Font font = wb.createFont();
//        font.setFontHeightInPoints((short) 2); //字体高度
        font.setColor(XSSFFont.COLOR_NORMAL); //字体颜色
//        font.setFontName("黑体"); //字体
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); //宽度
//        font.setItalic(true); //是否使用斜体
//        font.setStrikeout(true); //是否使用划线

        Font redFont = wb.createFont();
//        redFont.setFontHeightInPoints((short) 20); //字体高度
        redFont.setColor(XSSFFont.COLOR_RED); //字体颜色
        redFont.setFontName("黑体"); //字体
//        redFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); //宽度
        redFont.setItalic(true); //是否使用斜体
//        font.setStrikeout(true); //是否使用划线

        for(int rowIndex=0; rowIndex < rentalList.size(); rowIndex++){
            curRow = rowIndex+1;
            //if()
            {
                dataStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
            }
            dataStyle.setFont(font);
            ExcelUtil.createCell(sheet, curRow, 0, CellType.NUMERIC, dataStyle, null, rentalList.get(rowIndex).getId());
            ExcelUtil.createCell(sheet, curRow, 1, CellType.STRING, dataStyle, null, rentalList.get(rowIndex).getTaxId());
            ExcelUtil.createCell(sheet, curRow, 2, CellType.STRING, dataStyle, null, rentalList.get(rowIndex).getCompanyName());
            ExcelUtil.createCell(sheet, curRow, 3, CellType.STRING, dataStyle, null, rentalList.get(rowIndex).getRegAddress());
            ExcelUtil.createCell(sheet, curRow, 4, CellType.STRING, dataStyle, null, rentalList.get(rowIndex).getRepresentativeName());
            ExcelUtil.createCell(sheet, curRow, 5, CellType.STRING, dataStyle, null, rentalList.get(rowIndex).getRepresentativePhone());
            ExcelUtil.createCell(sheet, curRow, 6, CellType.STRING, dataStyle, null, rentalList.get(rowIndex).getAgentNamePhone());
            ExcelUtil.createCell(sheet, curRow, 7, CellType.STRING, dataStyle, null, rentalList.get(rowIndex).getFirstRentalDate());
            ExcelUtil.createCell(sheet, curRow, 8, CellType.STRING, dataStyle, null, rentalList.get(rowIndex).getAddressFrom());
            ExcelUtil.createCell(sheet, curRow, 9, CellType.NUMERIC, dataStyle, null, rentalList.get(rowIndex).getCost());
            if(rentalList.get(rowIndex).getCostDate()!=null && rentalList.get(rowIndex).getCostDate().compareTo(cn.jbolt.common.util.DateUtil.getNowDate())<0){
                redDataStyle.setFont(redFont);
                ExcelUtil.createCell(sheet, curRow, 10, CellType.STRING, redDataStyle, null, rentalList.get(rowIndex).getCostDate());
            }
            else {
                ExcelUtil.createCell(sheet, curRow, 10, CellType.STRING, dataStyle, null, rentalList.get(rowIndex).getCostDate());
            }
            ExcelUtil.createCell(sheet, curRow, 11, CellType.STRING, dataStyle, null, CACHE.me.getDictionaryName(rentalList.get(rowIndex).getHasTaxControl()));
            ExcelUtil.createCell(sheet, curRow, 12, CellType.STRING, dataStyle, null, CACHE.me.getDictionaryName(rentalList.get(rowIndex).getCompanyAttrib()));
            ExcelUtil.createCell(sheet, curRow, 13, CellType.STRING, dataStyle, null, rentalList.get(rowIndex).getCompanyProject());
            ExcelUtil.createCell(sheet, curRow, 14, CellType.STRING, dataStyle, null, CACHE.me.getDictionaryName(rentalList.get(rowIndex).getPayType()));
            ExcelUtil.createCell(sheet, curRow, 15, CellType.STRING, dataStyle, null, rentalList.get(rowIndex).getMemo());
        }
//        Cell cell_merged = row0.createCell(0);
//        sheet.addMergedRegion(new CellRangeAddress(0,0,0,3));
//        cell_merged.setCellType(CellType.STRING);
//        cell_merged.setCellValue(sheetName);
    }
}