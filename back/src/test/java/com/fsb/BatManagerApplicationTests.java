package com.fsb;

import com.fsb.pojo.entity.Brand;
import com.fsb.pojo.entity.Racket;
import com.fsb.pojo.entity.Review;
import com.fsb.Service.BrandService;
import com.fsb.Service.RacketService;
import com.fsb.Service.ReviewService;
import com.fsb.Service.SupplierService;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class BatManagerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private RacketService racketService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ReviewService reviewService;

    @Test
    public void racketList(){
        PageInfo<Racket> racketList = racketService.findByPage(1,2);
        for (Racket racket:racketList.getList()) {
                System.out.println(racket);
        }
    }

    @Test
    public void brandList(){
        List<Brand>  brands = brandService.findAll();
        for (Brand brand : brands) {
            System.out.println(brand);
        }
    }

    @Test
    public void add(){
        Brand brand = new Brand(3,"Butterfly","日本","https://www.butterfly.tt",0);
        brandService.add(brand);
    }
    @Test
    public void update(){
        Brand brand = new Brand(3,"Butterfly","日本","https://www.butterfly.tt",0);
        brandService.update(brand);
    }

    @Test
    public void findAll(){
        List<Review> all = reviewService.findAll();
        for (Review review : all) {
            System.out.println(review);
        }
    }

}
