package zonky.market.zonkymarket.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import zonky.market.zonkymarket.Loan;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;

public class MarketplaceResponseDto {

    private final int pageCount;
    private final List<Loan> loans;

    MarketplaceResponseDto(ResponseEntity<Loan[]> loans, Long pageSize) {
        String total = loans.getHeaders().getFirst("X-Total");
        this.pageCount = StringUtils.isEmpty(total) ? 0 : (int) Math.ceil(Long.valueOf(total) / pageSize.doubleValue());
        this.loans = loans.getBody() == null ? emptyList() : Arrays.asList(loans.getBody());
    }

    public int getPageCount() {
        return pageCount;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    @Override
    public String toString() {
        return "MarketplaceResponseDto{" +
                "pageCount=" + pageCount +
                ", loans=" + loans +
                '}';
    }
}
