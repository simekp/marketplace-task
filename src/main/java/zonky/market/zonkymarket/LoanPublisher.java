package zonky.market.zonkymarket;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class LoanPublisher {

    private static final Logger logger = getLogger(LoanPublisher.class);

    /**
     * Publish loans
     *
     * Loans are publish to log file
     *
     * @param loans Data to publish
     */
    void publishLoans(final List<Loan> loans) {
        //TODO: send loans to UI, Kafka, Console, Specific file ....
        logger.info("Marketplace has new loans:\n{}", loans
                .stream()
                .map(Loan::formatForPublish)
                .collect(Collectors.joining("\n")));
    }
}
