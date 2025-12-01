package com.fsb;

import com.fsb.entity.Racket;
import com.fsb.Service.RacketService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class BatManagerApplication implements CommandLineRunner {

//    @Autowired
//    private RacketService racketService;
      @Autowired
      private SystemWindow  systemWindow;

    public static void main(String[] args) {
        SpringApplication.run(BatManagerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        systemWindow.show();
    }
}
