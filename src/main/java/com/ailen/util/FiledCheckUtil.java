package com.ailen.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FiledCheckUtil {

    /**
     * 单元格公式跨文件获取数据处理
     * @param cell
     * @return
     */
    public static String checkFiled(Cell cell){
        //获取文件类型
        final CellType cellTypeEnum = cell.getCellTypeEnum();
        String value = cell.toString().trim();
        //判断是否是公式
        if (cellTypeEnum.equals(CellType.FORMULA)){
            //获取缓存公式结果类型
            CellType cachedFormulaResultTypeEnum = cell.getCachedFormulaResultTypeEnum();
            switch (cachedFormulaResultTypeEnum){
                case NUMERIC:
                    if(DateUtil.isCellDateFormatted(cell)){
                        Date date = cell.getDateCellValue();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        value = sdf.format(date);
                    }else{
                        BigDecimal n = new BigDecimal(cell.getNumericCellValue());
                        DecimalFormat decimalFormat = new DecimalFormat("0");
                        decimalFormat.setMaximumFractionDigits(18);
                        value = decimalFormat.format(n.doubleValue());
                    }break;
                case STRING:
                    value = String.valueOf(cell.getStringCellValue());
                    if(value != null){
                        value = value.trim();
                    }
                    break;
                case BOOLEAN:
                    value = String.valueOf(cell.getBooleanCellValue());
                    break;
                case ERROR: value = "";
                    break;
                default:
                    value = cell.getRichStringCellValue().getString();
                    break;
            }
        }
        return value;
    }
}
