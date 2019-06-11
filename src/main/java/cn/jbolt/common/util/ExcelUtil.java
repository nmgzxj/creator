package cn.jbolt.common.util;

import com.jfinal.kit.StrKit;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;


import java.io.Serializable;

public class ExcelUtil {

    public static void createCell(Sheet sheet, int rowIndex, int cellIndex, CellType cellType, CellStyle cellStyle, CellRangeAddress cellRangeAddress, Serializable value){

        Row row = sheet.getRow(rowIndex);
        if(row==null){
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.createCell(cellIndex);

        if(cellStyle != null){
            cell.setCellStyle(cellStyle);
        }
        if(cellRangeAddress != null){
            sheet.addMergedRegion(cellRangeAddress);
        }
        if(cellType != null){
            cell.setCellType(cellType);
            if(value!=null  && !"".equals(value)) {
                if (cellType.equals(CellType.STRING)) {
                    cell.setCellValue(value.toString());
                } else if (cellType.equals(CellType.NUMERIC)) {
                    cell.setCellValue(Double.parseDouble(value.toString()));
                } else if (cellType.equals(CellType.BOOLEAN)) {
                    cell.setCellValue(Boolean.parseBoolean(value.toString()));
                } else {
                    cell.setCellValue(value.toString());
                }
            }
        }
        else{
            cell.setCellType(CellType.STRING );
            if(value!=null  && !"".equals(value)) {
                cell.setCellValue(value.toString());
            }
        }
    }
}
