import com.ailen.Application;
import com.ailen.mapper.GoodsMapper;
import com.ailen.model.Goods;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;

@SpringBootTest(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class Springboot18EsApplicationTests {

    @Autowired
    private GoodsMapper goodsMapper;


    @Test
    public void testCreateClient() throws IOException {
        Goods goods = new Goods().setId("1")
                .setGoodsName("apple")
                .setStore("20")
                .setPrice("1.5");
        goodsMapper.save(goods);
        System.out.println("添加成功");
    }

    @Test
    public void findById(){
        final Goods goods = goodsMapper.findById("1").get();
        System.out.println(goods);
    }
}
