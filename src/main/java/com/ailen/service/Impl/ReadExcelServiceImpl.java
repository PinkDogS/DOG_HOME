package com.ailen.service.Impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.ailen.SelfException;
import com.ailen.model.ErrorFile;
import com.ailen.service.ReadExcelService;
import com.ailen.util.FiledCheckUtil;
import com.ailen.util.IdcardValidator;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import static java.sql.Types.BOOLEAN;
import static java.sql.Types.NUMERIC;
import static org.apache.poi.util.POILogger.ERROR;
import static org.apache.xmlbeans.impl.piccolo.xml.Piccolo.STRING;

@Service
public class ReadExcelServiceImpl implements ReadExcelService {

    @Value("${file.savePath}")
    String savePath;

    /**
     * 读取记录文件
     * @param file
     * @return
     * @throws Exception
     */
    @Override
    public List readExcel(File file) throws Exception {
        //idcard判断类
        IdcardValidator idcardValidator = new IdcardValidator();
        //标注身份证下标
        int index = 0;
        int idCard2 = 0;
        int phoneFlag = 0;
        //身份证、手机号正则判断字符串
        String regex = "^.*[身份证,证件号码].*$";
        String phoneRegex = "^.*[手机,电话].*$";
        //振兴局专用，家庭人口数，与户主关系
//        String familyNum = "^.*人口数$";
//        String relation = "^.*户主关系$";
        //振兴局 日期类型处理 xxxx年xx月 傻逼填报方式
//        Calendar calendar = new GregorianCalendar(1900,0,-1);

//        需要添加到excel表中的字段数据存储
        int addFiledNum = 0;
        String county = "";
        String fallDue = "0";
//      标志位，多excel读取时，用来表示是否是第一次读取，用来行记录数据写入alist
        //用来指定从第几行开始获取数据
        int flag = 0;
        /**
         * 返回当前excel文件所有的行记录到alist
         */
        List aList = new ArrayList();

//        for (File file:files){
        /**
         * 单个文件处理
         */
            //获取文件名字
            String fileName = file.getName();
            //获取文件类型
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
            System.out.println(" **** fileType:" + fileType+"****** fileName: "+fileName);
            //获取输入流
            InputStream stream = null;
            //获取工作薄
            Workbook xssfWorkbook = null;
        if (fileType.equals("xls")) {
                stream = new FileInputStream(file);
                xssfWorkbook = new HSSFWorkbook(stream);
                stream.close();
            } else if (fileType.equals("xlsx")) {
                stream = new FileInputStream(file);
                xssfWorkbook = new XSSFWorkbook(stream);
                stream.close();
            } else {
                System.out.println("您输入的excel格式不正确");
                return aList;
            }
            // 获取第一个sheet文档
            Sheet Sheet = xssfWorkbook.getSheetAt(0);
            // Read the Row 从0开始
            /**
             * 判断是否是第一个excel表
             */
//            int i = 0;
//            if (0 == flag){
//                i = 2;
//                flag++;
//            }else {
//                i = 3;
//                flag++;
//            }
            /**
             * 获取当前ｅｘｃｅｌ表格的特定字段数据,根据文件名进行区分
             */
            if (fileName.matches("^.*党员基本信息.*xls.*$")){
                /**
                 * 根据文件名添加额外字段
                 */
                county = fileName.substring(0,3);
                fallDue = fileName.substring(3,fileName.indexOf("党员"));
                addFiledNum = 2;
                flag = 2;
            }else if (fileName.matches("^.*人大代表.*xls.*$")||fileName.matches("^.*政协委员.*xls.*$")){
                county = fileName.substring(0,3);
                fallDue = fileName.substring(3,6);
                addFiledNum = 2;
                flag = 2;
            }else if (fileName.matches("^.*财政供养人员.*xls.*$")){
                county = fileName.substring(0,3);
                final int indexOf = fileName.indexOf("-");
                if(-1 == indexOf){
                    fallDue = fileName.substring(3,fileName.indexOf("基本"));
                }else {
                    fallDue = fileName.substring(10,fileName.indexOf("统计"));
                }
                addFiledNum = 2;
                flag = 2;
            }else if (fileName.matches("^.*监察对象.*xls.*$")){
                county = fileName.substring(0,3);
                fallDue = fileName.substring(3,fileName.indexOf("监察"));
                if (-1 != fileName.indexOf("一")){
                    flag = 2;
                }else{
                    flag = 1;
                }
                addFiledNum = 2;
            }else if (fileName.matches("^.*振兴局.*xls.*$")){
                county = "null";
                fallDue = "null";
                addFiledNum = 2;
                flag = 2;
            }
            /**
             * 获取当前excel表格有多少字段
             */
            final Row sheetRow = Sheet.getRow(flag);
            //确定每行记录遍历字段的长度
            int size = sheetRow.getLastCellNum();

            /**
             * 获取身份证、手机字段下标
             */
            for (int x=0;x<sheetRow.getPhysicalNumberOfCells();x++){
                if (sheetRow.getCell(x).toString().matches(regex)){
                    index = x;
                }
                if (sheetRow.getCell(x).toString().matches(phoneRegex)){
                    phoneFlag = x;
                }
            }
            //获取所有行记录
        final int lastRowNum = Sheet.getLastRowNum();
        /**
         * 遍历所有行记录，将每行记录的字段数据存入list集合，这里直接从数据字段开始读取记录（原始为i，但是单表处理，不需要为i）
         */
        for (int rowNum = flag; rowNum <= lastRowNum; rowNum++) {
            //获取当前行记录
                Row Row = Sheet.getRow(rowNum);
                if (Row != null) {
                    //判断这行记录是否存在
                    if (Row.getLastCellNum() < 1 || "".equals(Row.getCell(0).toString())) {
                        continue;
                    }
                    //获取每一行封装成对象，汇总单行记录数据
                    List<String> rowList = new ArrayList<String>();
                    /**
                     * 处理当前行数据，汇总rowList
                     */
                    for(int colNum=0;colNum<size;colNum++)
                    {
                        //若第二个字段数据为空，直接处理下一行数据，---这里根据第二字段进行判断改行记录是否有效
                        if (null == Row.getCell(1)){
                            break;
                        }
//                        第一次添加到excel中，添加多个字段，反之获取数据
                        if ((flag == rowNum && colNum == 0)){
                            //特殊处理乡村振兴局
                            if ("null".equals(county)){
                                rowList.add(colNum,"户主身份证号码");
                                rowList.add(colNum+1,"户主姓名");
                                rowList.add(colNum+2, Row.getCell(colNum).toString());
                            }else {
                                rowList.add(colNum,"地区（区、县、乡）");
                                rowList.add(colNum+1,"单位/届别/类型");
                                rowList.add(colNum+2, Row.getCell(colNum).toString());
                            }
                        }else if (flag != rowNum && colNum == 0){
                            //若不是第一行记录，是余下的行，将先前确定好的记录添加到开始的0，1位置
                            rowList.add(colNum,county);
                            rowList.add(colNum+1,fallDue);
                            rowList.add(colNum+2, Row.getCell(colNum).toString());
                            //不是第一行字段数据，且遍历到手机所在字段，对手机格式进行统一
                        }else if (phoneFlag == colNum && rowNum != flag){
                            if (null != Row.getCell(colNum)){
                                /**
                                 * excel中数字转String方法，不然会回写科学计数法的数据
                                 */
                                Row.getCell(colNum).setCellType(CellType.STRING);
                                rowList.add(colNum+2, Row.getCell(colNum).getStringCellValue());
                            }else {
                                rowList.add(colNum+2, null);
                            }
                            //身份证所在列判断是否是公式获取值
                        }else if (colNum == index && null != Row.getCell(colNum)) {
                            Cell cell = Row.getCell(colNum);
                            //跨excel获取数据
                            final String filed = FiledCheckUtil.checkFiled(cell);
                            String idCard = filed;
                            //去除首尾特殊符号
                            if (filed.matches("^[\",\',‘,’,”,“].*$")){
                                idCard = filed.substring(1);
                            }
                            if (idCard.matches("^.*[\",\',‘,’,“,”]$")){
                                idCard = idCard.substring(0,idCard.length()-1);
                            }
                            rowList.add(colNum+2,idCard);
                        }else {
                            //不是第一行，也不是手机所在字段下标，正常汇聚到rowList
                            rowList.add(colNum+2, Row.getCell(colNum)==null?"":Row.getCell(colNum).toString());
                        }
                    }
                    //汇聚完成后判断当前行是否有记录，无效行数据直接过滤
                    if (0 != rowList.size()){
                        aList.add(rowList);
                    }
                }
            }
        /**
         * 处理乡村振兴表数据,根据家庭人数的数据量进行数据统计
         */
        if (fileName.matches("^.*振兴局.*xls.*$")){
            String personName = null;
            Object idCard = null;
            //获取第一行记录，一定是户主信息
            List list= (List)aList.get(1);
            personName = list.get(7).toString();
            idCard = list.get(index+2);
            list.set(0,list.get(index+2));
            list.set(1,personName);
            //标准模板下家庭人口数：9，户主关系：10 （下标）
            for (int i=2;i<aList.size();i++){
                list = (List) aList.get(i);
                if (!"户主".equals(list.get(10).toString())){
                    list.set(0,idCard);
                    list.set(1,personName);
                }else {
                    personName = list.get(7).toString();
                    idCard = list.get(index+2);
                    list.set(0,idCard);
                    list.set(1,personName);
                }
//                for (int j=0;j<personNum;j++){
//                    //修改16、17位上的不合法日期格式
//                    List currentList = (List)aList.get(i+j);
////                    String str1 = currentList.get(17).toString();
////                    String str2 = currentList.get(16).toString();
////                    if (-1 == str1.indexOf("年") && !"".equals(str1)){
//////                        Date date = DateUtils.addDays(calendar.getTime(),Double.valueOf(currentList.get(17).toString()).intValue());
//////                        String time = new SimpleDateFormat("yyyy-MM-dd").format(date);
//////                        currentList.set(17,str1);
////                    }
////                    if (-1 == str2.indexOf("年") && !"".equals(str2)){
//////                        Date date = DateUtils.addDays(calendar.getTime(),Double.valueOf(currentList.get(16).toString()).intValue());
//////                        String time = new SimpleDateFormat("yyyy-MM-dd").format(date);
//////                        currentList.set(16,time);
////                    }
//                    //修改记录
//                    currentList.set(0,list.get(index+2));
//                    currentList.set(1,personName);
//                }
//                i = i+personNum;
            }
        }
//        }
        /**
         * 记录分类，y=0是字段行数据，后面单独放入到两种list中
         */
        for (int y=1;y<aList.size();y++){
            //校验每行记录的身份证是否正确
            final String trim = StrUtil.trim(((List) aList.get(y)).get(index+2).toString());
            boolean state = idcardValidator.isValidate18Idcard(trim);
            /**
             * 当前身份证记录存在错误时,添加错误标志
             */
            List<String> list = new ArrayList<>();
            if (!state){
                list.add("exist");
                aList.add(list);
                break;
            }
            /**
             * 若没有记录出错，添加正确标志
             */
            if (y == aList.size()-1){
                list.add("noExist");
                aList.add(list);
                break;
            }
        }
        System.out.println(fileName+"数据汇总完成");
        return aList;
    }





//    public String dealIdCard(){
//
//    }


    /**
     * 写入记录到对应文件
     * @param list
     * @throws IOException
     */
    @Override
    public void writeExcel(String key,String fileName,List list) throws IOException, InvalidFormatException {
        //分别将该excel中的记录存入该类型的两种excel中用作数据区分
        String url = savePath+key+".xlsx";
        String errorUrl = savePath+key+"---错误"+".xlsx";
        File file = new File(url);
        File errorFile = new File(errorUrl);
        //idcard校验类
        IdcardValidator idcardValidator = new IdcardValidator();
        //标注身份证下标
        int flag = 0;
        //idcard校验正则
        String regex = "^.*身份证.*$";
        //两类数据集合
        List realList = new ArrayList();
        List errorList = new ArrayList();
        /**
         * 错误统计表创建
         */
        //错误结果统计
        String resultUrl = savePath+"错误统计"+".xlsx";
        File resultFile = new File(resultUrl);
        List<List> resultList = new ArrayList();
        if (!resultFile.exists()){
            List list1 = new ArrayList();
            list1.add("文件名称");
            list1.add("正确数据量");
            list1.add("错误数据量");
            resultList.add(0,list1);
            this.writeExcelWhenEmpty(resultUrl,resultList);
        }
        /**
         * 判断表是否存在，都存在或者都不存在
         */
        if (file.exists()&&errorFile.exists()){
            /**
             * 两个excel字段相同，用谁做身份线标校验都行
             */
            Workbook workbook = WorkbookFactory.create(new FileInputStream(url));
            //获取到工作表，因为一个excel可能有多个工作表
            Sheet sheet=workbook.getSheetAt(0);
            final Row row = sheet.getRow(0);
            //list集合下标开始位置，用来list自增
            int realFlag = 0;
            int errorFlag = 0;
            /**
             * 获取身份证字段下标
             */
            for (int i=0;i<row.getPhysicalNumberOfCells();i++){
                if (row.getCell(i).toString().matches(regex)){
                    flag = i;
                }
            }
            /**
             * 记录分类，第0行存储字段信息，excel文件存在，所以直接追加记录即可，i=1表示存储数据行记录
             */
            for (int i=1;i<list.size();i++){
                final String trim = StrUtil.trim(((List) list.get(i)).get(flag).toString());
                boolean state = idcardValidator.isValidate18Idcard(trim);
                if (state){
                    realList.add(realFlag++,list.get(i));
                }else {
                    errorList.add(errorFlag++,list.get(i));
                }
            }
            /**
             * 错误记录处理
             */
            if (errorList.size()>0){
                this.errorFileCount(fileName,realList,errorList,0);
            }
//            都存在时进行处理
            final boolean state = this.writeExcelWhenFileExist(url, realList);
            boolean state1 = this.writeExcelWhenFileExist(errorUrl, errorList);
            if (state && state1){
                System.out.println("追加到excel成功！");
            }else {
                System.out.println("追加到excel失败");
            }
        }else {
            /**
             * 当两excel文件不存在时处理
             */
//            同上,为1是默认添加数据字段
            int realFlag = 1;
            int errorFlag = 1;
            /**
             * 找到身份证对应字段下标
             */
            final List filedList = (List) list.get(0);
            for (int i=0;i<filedList.size();i++){
                if (filedList.get(i).toString().matches(regex)){
                    flag = i;
                    break;
                }
            }
            /**
             * 记录分类，分别将首行的字段信息添加到集合中
             */
            realList.add(0,list.get(0));
            errorList.add(0,list.get(0));
            //根据身份证是否正确将数据分别汇聚到对应的集合中
            for (int i=1;i<list.size();i++){
                final String trim = StrUtil.trim(((List) list.get(i)).get(flag).toString());
                boolean state = idcardValidator.isValidate18Idcard(trim);
                if (state){
                    realList.add(realFlag++,list.get(i));
                }else {
                    errorList.add(errorFlag++,list.get(i));
                }
            }
            /**
             * 错误记录处理
             */
            if (errorList.size()>0){
                this.errorFileCount(fileName,realList,errorList,1);
            }
            //写入对应excel文件
            boolean state = this.writeExcelWhenEmpty(url,realList);
            boolean state1 = this.writeExcelWhenEmpty(errorUrl,errorList);
            if (state && state1){
                System.out.println("写入excel成功");
            }else {
                System.out.println("写入excel失败");
            }
        }
    }

    /**
     * 错误excel记录统计
     * @param fileName
     */
    public void errorFileCount(String fileName,List realList,List errorList,int state) throws IOException {
        //文件名获取
        final String name = fileName.substring(0, fileName.indexOf("."));
        //错误结果统计
        String resultUrl = savePath+"错误统计"+".xlsx";
        List<List> resultList = new ArrayList();
        List list2 = new ArrayList();
        list2.add(name);
        list2.add(realList.size()-state);
        list2.add(errorList.size()-state);
        resultList.add(0,list2);
        final boolean resultState = this.writeExcelWhenFileExist(resultUrl, resultList);
        if (resultState){
            System.out.println("错误文件执行追加");
        }
    }

    /**
     * 存在文件时写入
     * @param url
     * @param list
     * @return
     */
    public boolean writeExcelWhenFileExist(String url,List list){
        try{
            //获取excel路径的数据流
            Workbook workbook = WorkbookFactory.create(new FileInputStream(url));
            //获取到工作表，因为一个excel可能有多个工作表--0
            Sheet sheet=workbook.getSheetAt(0);
//            首行
            Row row=sheet.getRow(0);
            int hang=0;
            //若首行为空，无数据
            if("".equals(row)||row==null){
                hang=0;
            }else {
                //首行有数据，获取sheet0总行数，+1进行数据添加
                hang = sheet.getLastRowNum();
                hang = hang + 1;
            }
            //获取excel url文件的输出流
            FileOutputStream out=new FileOutputStream(url);
            /**
             * 追加数据到excel，因为两excel都存在，这里的list存出的都是行记录，不存在字段行
             */
            for (int i=0;i<list.size();i++){
                int size = ((List)list.get(0)).size();
                //在现有行号后追加数据
                row=sheet.createRow((hang)+i);
                for (int j=0;j<size;j++){
                    row.createCell(j).setCellValue(((List)list.get(i)).get(j).toString());
                }
            }
            out.flush();
            workbook.write(out);
            //关闭输出流
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public boolean writeExcelWhenEmpty(String url,List list) throws IOException {
        //1、创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        //2、创建工作表
        XSSFSheet sheet = workbook.createSheet("工作表一");
        /**
         * 不存在excel表时，list中存储的首行带有字段行，先将行记录插入到excel中
         */
        for (int i=0;i<list.size();i++){
            int size = ((List)list.get(i)).size();
            //新建行记录
            XSSFRow row = sheet.createRow(i);
            for (int j=0;j<size;j++){
                //3、创建行
                //创建初始化列，设置其格式
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(((List)list.get(i)).get(j).toString());
            }
        }
        //url 输出流
        FileOutputStream out = new FileOutputStream(url);
        workbook.write(out);
        out.flush();
        //释放资源
        out.close();
        workbook.close();
        return true;
    }



    /**
     * 得到文件名称
     *
     * @param path 路径
     * @return {@link List}<{@link String}>
     */
    @Override
    public List<String> getFileNames(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        List<String> fileNames = new ArrayList<>();
        return getFileNames(file, fileNames);
    }

    /**
     * 得到文件名称
     *
     * @param file      文件
     * @param fileNames 文件名
     * @return {@link List}<{@link String}>
     */
    @Override
    public List<String> getFileNames(File file, List<String> fileNames) {
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                System.out.println("文件名称是："+f.getName());
                getFileNames(f, fileNames);
            } else {
                System.out.println("文件名是："+f.getPath());
                System.out.println(f.getAbsolutePath());
                fileNames.add(f.getName());
            }
        }
        return fileNames;
    }

}
