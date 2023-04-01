import cn.hutool.core.util.StrUtil;
import com.ailen.Application;
import com.ailen.SelfException;
import com.ailen.common.ConfigEnum;
import com.ailen.controller.PageController;
import com.ailen.controller.ThreadController;
//import com.ailen.service.FileDealService;
import com.ailen.controller.ThreadFileDealController;
import com.ailen.model.User;
import com.ailen.service.Impl.OneServiceImpl;
import com.ailen.service.Impl.ThreadDemo;
import com.ailen.service.OneService;
import com.ailen.service.ReadExcelService;
import com.ailen.util.FiledCheckUtil;
import com.ailen.util.IdcardValidator;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Utf8;
import com.google.common.collect.Lists;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.aspectj.util.FileUtil;
import org.assertj.core.data.MapEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.apache.poi.ss.usermodel.DataValidationConstraint.ValidationType.FORMULA;
import static org.apache.poi.util.POILogger.ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
//@AutoConfigureMockMvc
//@WebAppConfiguration
public class TestDemo01 {

    @Resource
    ThreadController threadController;

    @Resource
    PageController pageController;

    @Resource
    ReadExcelService readExcelService;

    private MockMvc mockMvc;

    @Resource
    OneService oneService;

//    @Resource
//    FileDealService fileDealService;

    @Resource
    ThreadDemo threadDemo;

    @Autowired
    ThreadFileDealController fileDealController;

    @Value("${file.dirPath}")
    String dirPath;


    @Before
    public void setup(){
        System.out.println("------------开始测试-----------");
        mockMvc = MockMvcBuilders.standaloneSetup(fileDealController).build();
    }


    /**
     * 请求体的post请求单元测试解决
     * @throws Exception
     */
    @Test
    public void threadTest() throws Exception {
        User param = new User();
        List<String> list = new ArrayList<>();
        list.add("111");
        param.setId("1").setName("ailen").setList(list);
        MvcResult result = mockMvc.perform(
                post("/threadFileDeal/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(param)))
                        .andReturn();
        final String contentAsString = result.getResponse().getContentAsString();
        System.out.println(contentAsString);
    }


    /**
     * 测试单表获取数据
     */
    @Test
    public void fileNames() throws IOException, InvalidFormatException {
        /**
         * 获取所有待插入excel文件路径
         */
        List<File> fileNames = oneService.getFileNames("E:\\anshare\\anshare解决\\测试数据-------------翔太\\ailen\\ceshi\\ceshi");
        /**
         * 暂存各类总表
         */
        Map<String,List<File>> fileMap = new HashMap<>();
        /**
         * excel表汇各类型汇总
         */
        for (File file : fileNames){
            String fileName = file.getName();
            if (fileName.matches("^.*人大代表.xls.*$")){
                fileMap.put("人大代表",new ArrayList<>());
                fileMap.get("人大代表").add(file);
            }else if (fileName.matches("^.*政协委员名单.xls.*$")){
                fileMap.put("政协委员",new ArrayList<>());
                fileMap.get("政协委员").add(file);
            }else if (fileName.matches("^.*党员基本信息.xls.*$")){
                fileMap.put("共产党信息表",new ArrayList<>());
                fileMap.get("共产党信息表").add(file);
            }
        }
        /**
         * 读取数据，统一入表
         */
//        Map<String,List> fileTypeMap = new HashMap<>();
        for (Map.Entry<String,List<File>> mapType : fileMap.entrySet()){
            List list = null;
            try{
//                list = readExcelService.readExcel(mapType.getValue().toArray(new File[mapType.getValue().size()]));
            }catch (Exception e){
                e.printStackTrace();
            }
            /**
             * 统一插入到文件中去
             */
            readExcelService.writeExcel(mapType.getKey(),"",list);
//            fileTypeMap.put(mapType.getKey(),list);
        }
//        final File[] fileArray = fileNames.toArray(new File[fileNames.size()]);

//        List list = null;
//        try{
//            list = readExcelService.readExcel(fileArray);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

//        for (int i=2;i< fileNames.size();i++){
//            int size = ((List)fileNames.get(i)).size();
//            for (int j=0;j<size;j++){
//                System.out.println(((List)fileNames.get(i)).get(j).toString());
//            }
//        }
//        c88740bf88e0e67d51219ab25b7df098ae40ea3d
    }

    @Test
    public void test() throws IOException, InvalidFormatException, InterruptedException {
        String str = "退出低保";
        final int indexOf = str.indexOf("退出");
        final int indexOf1 = str.indexOf("低保");
        System.out.println(indexOf1+"----"+indexOf);
    }
}
