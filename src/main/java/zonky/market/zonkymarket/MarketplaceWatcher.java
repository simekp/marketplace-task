package zonky.market.zonkymarket;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import zonky.market.zonkymarket.rest.MarketplaceResponseDto;
import zonky.market.zonkymarket.rest.MarketplaceRestClient;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class MarketplaceWatcher {

    private static final Logger logger = getLogger(MarketplaceWatcher.class);

    private final MarketplaceRestClient client;
    private final LoanPublisher loanPublisher;
    private ZonedDateTime lastRun;

    public MarketplaceWatcher(MarketplaceRestClient client, LoanPublisher loanPublisher,
                              @Value("${zonky.marketplace.initialDate:}") String initialDate) {
        this.client = client;
        this.loanPublisher = loanPublisher;
        this.lastRun = StringUtils.isEmpty(initialDate) ? ZonedDateTime.now() : ZonedDateTime.parse(initialDate);
    }

    /**
     * Check if new loans are in marketplace
     */
    @Scheduled(fixedDelayString = "${zonky.marketplace.watchPeriod}")
    protected void checkNewLoans() {
        final List<Loan> loans = findAllNewLoans();
        if (loans.isEmpty()) {
            logger.info("There are not new loans from last check");
            return;
        }

        loanPublisher.publishLoans(loans);
        lastRun = loans
                .stream()
                .map(Loan::getDatePublished)
                .max(ZonedDateTime::compareTo)
                .orElse(lastRun);
    }

    private List<Loan> findAllNewLoans() {
        final MarketplaceResponseDto marketplaceFirstPage = client.findLoans(0, lastRun);
        if (marketplaceFirstPage.getLoans().isEmpty()) {
            return emptyList();
        }

        final List<Loan> loans = new ArrayList<>(marketplaceFirstPage.getLoans());
        for (int i = 1; i < marketplaceFirstPage.getPageCount(); i++) {
            MarketplaceResponseDto marketplacePageResponse = client.findLoans(i, lastRun);
            loans.addAll(marketplacePageResponse.getLoans());
        }
        return loans;
    }
}
