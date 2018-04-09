package cn.cssn.crawler;

import cn.cssn.crawler.entity.Standard;
import cn.cssn.crawler.processor.StandardSearchProcessor;
import cn.cssn.crawler.repository.StandardRepo;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Random;

@Component
public class StandardCrawler implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(StandardCrawler.class);
    @Autowired
    private StandardRepo standardRepo;

    @Override
    public void run(String... args) throws Exception {
        int[] tids = {2, 3, 4}; // 2:国标，3：行标，4：地标
        for (int tid : tids) {
            StandardSearchProcessor standardSearchProcessor = new StandardSearchProcessor(tid, null, null, 200);
            int page = 1;
            if (tid == 2) {
                page = 219;
            }
            while (true) {
                logger.info("Getting page {} ...", page);
                Pair<List<Standard>, Boolean> result = standardSearchProcessor.download(page++);
                List<Standard> standards = result.getLeft();
                saveStandards(standards);
                Boolean hasMore = result.getRight();
                if (!hasMore) {
                    break;
                }
                Thread.sleep((long) (new Random().nextDouble() * 3000));
            }
        }
    }

    @Transactional
    private void saveStandards(List<Standard> standards) {
        standardRepo.save(standards);
        logger.info("{} standards saved.", standards.size());
    }
}
