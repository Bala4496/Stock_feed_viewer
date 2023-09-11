package ua.bala.stock_feed_viewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockFeedViewerApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockFeedViewerApplication.class, args);
    }

}
