package ua.bala.stock_feed_viewer.domain.data;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ua.bala.stock_feed_viewer.domain.model.Company;
import ua.bala.stock_feed_viewer.domain.repository.CompanyRepository;
import ua.bala.stock_feed_viewer.ports.in.CompanyFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyUpdater implements DataUpdater {

    private final CompanyFetcher companyFetcher;
    private final CompanyRepository companyRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Override
    public void update() {
        var thread = new Thread(new UpdateCompaniesTask(), "UpdateCompanies");
        thread.setDaemon(true);
        thread.start();
    }

    @AllArgsConstructor
    class UpdateCompaniesTask implements Runnable {

        @Override
        public void run() {
            log.info("Thread - {} : started", Thread.currentThread().getName());
            while (true) {
                ForkJoinPool forkJoinPool = new ForkJoinPool();
                var savedCompaniesFuture = CompletableFuture.supplyAsync(companyRepository::findAll);
                var fetchedCompaniesFuture = CompletableFuture.supplyAsync(companyFetcher::fetchCompanies);

                var combinedFuture = savedCompaniesFuture.thenCombineAsync(
                        fetchedCompaniesFuture,
                        (savedCompanies, fetchedCompanies) -> {
                            var differentCompanies = forkJoinPool.invoke(new DifferenceCompaniesTask(savedCompanies, fetchedCompanies));
                            return companyRepository.saveAll(differentCompanies);
                        }
                );

                combinedFuture.join();
            }
        }
    }

    @AllArgsConstructor
    class DifferenceCompaniesTask extends RecursiveTask<List<Company>> {

        private final List<Company> savedCompanies;
        private final List<Company> fetchedCompanies;

        @Override
        protected List<Company> compute() {
            if (fetchedCompanies.size() <= 100) {
                var difference = new ArrayList<>(fetchedCompanies);
                difference.removeAll(savedCompanies);
                return difference;
            } else {
                int mid = fetchedCompanies.size() / 2;

                var leftTask = new DifferenceCompaniesTask(savedCompanies, fetchedCompanies.subList(0, mid));
                var rightTask = new DifferenceCompaniesTask(savedCompanies, fetchedCompanies.subList(mid, fetchedCompanies.size()));

                leftTask.fork();
                var rightResult = rightTask.compute();
                var leftResult = leftTask.join();

                rightResult.addAll(leftResult);
                return rightResult;
            }
        }
    }
}
