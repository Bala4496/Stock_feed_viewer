package ua.bala.stock_feed_viewer.domain.data;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CompanyUpdater {

    private final CompanyFetcher companyFetcher;
    private final CompanyRepository companyRepository;


    @EventListener(ApplicationReadyEvent.class)
    public void updateCompanies() {
        var thread = new Thread(new UpdateCompaniesTask(), "UpdateCompaniesThread");
        thread.setDaemon(true);
        thread.start();
    }

    @AllArgsConstructor
    class UpdateCompaniesTask implements Runnable {

        @Override
        public void run() {
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            var savedCompaniesFuture = CompletableFuture.supplyAsync(companyFetcher::fetchCompanies);
            var fetchedCompaniesFuture = CompletableFuture.supplyAsync(companyRepository::findAll);

            var combinedFuture = savedCompaniesFuture.thenCombineAsync(
                    fetchedCompaniesFuture,
                    (savedCompanies, fetchedCompanies) -> {
                        var differentCompanies = forkJoinPool.invoke(new DifferenceCompaniesTask(fetchedCompanies, savedCompanies));
                        return companyRepository.saveAll(differentCompanies);
                    }
            );

            combinedFuture.join();
        }
    }

    @AllArgsConstructor
    class DifferenceCompaniesTask extends RecursiveTask<List<Company>> {

        private final List<Company> fetchedCompanies;
        private final List<Company> savedCompanies;

        @Override
        protected List<Company> compute() {
            if (fetchedCompanies.size() <= 100) {
                var difference = new ArrayList<>(fetchedCompanies);
                difference.removeAll(savedCompanies);
                return difference;
            } else {
                int mid = fetchedCompanies.size() / 2;

                var leftTask = new DifferenceCompaniesTask(fetchedCompanies.subList(0, mid), savedCompanies);
                var rightTask = new DifferenceCompaniesTask(fetchedCompanies.subList(mid, fetchedCompanies.size()), savedCompanies);

                leftTask.fork();
                var rightResult = rightTask.compute();
                var leftResult = leftTask.join();

                rightResult.addAll(leftResult);
                return rightResult;
            }
        }
    }
}
