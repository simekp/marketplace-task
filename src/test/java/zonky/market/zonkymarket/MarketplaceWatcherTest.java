package zonky.market.zonkymarket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zonky.market.zonkymarket.rest.MarketplaceResponseDto;
import zonky.market.zonkymarket.rest.MarketplaceRestClient;

import java.util.Arrays;

import static java.time.ZonedDateTime.parse;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MarketplaceWatcherTest {

    private MarketplaceRestClient client;
    private LoanPublisher loanPublisher;
    private MarketplaceWatcher marketplaceWatcher;

    @BeforeEach
    void setUp() {
        client = mock(MarketplaceRestClient.class);
        loanPublisher = mock(LoanPublisher.class);
        marketplaceWatcher = new MarketplaceWatcher(client, loanPublisher, null);
    }

    @Test
    void findOnePageOfLoansWillCallOneRequestToZonky() {
        MarketplaceResponseDto response = mock(MarketplaceResponseDto.class);
        when(response.getPageCount()).thenReturn(1);
        when(response.getLoans())
                .thenReturn(singletonList(new Loan("http://localhost:9000/loan/298885", "REST TestHelper Loan ", parse("2018-01-01T00:00+01:00"))));
        when(client.findLoans(eq(0), any())).thenReturn(response);

        marketplaceWatcher.checkNewLoans();

        verify(loanPublisher).publishLoans(any());
        verify(client).findLoans(eq(0), any());
    }

    @Test
    void findTwoPageOfLoansWillCallTwoRequestToZonky() {
        MarketplaceResponseDto response = mock(MarketplaceResponseDto.class);
        when(response.getPageCount()).thenReturn(2);
        when(response.getLoans())
                .thenReturn(singletonList(new Loan("url", "name", parse("2018-01-01T00:00+01:00"))));
        when(client.findLoans(eq(0), any())).thenReturn(response);
        when(client.findLoans(eq(1), any())).thenReturn(response);

        marketplaceWatcher.checkNewLoans();

        verify(loanPublisher).publishLoans(any());
        verify(client).findLoans(eq(0), any());
        verify(client).findLoans(eq(1), any());
    }

    @Test
    void forNewRequestForLoansUseTimeFromLastRequest() {
        MarketplaceResponseDto response = mock(MarketplaceResponseDto.class);
        when(response.getPageCount()).thenReturn(1);
        when(response.getLoans())
                .thenReturn(Arrays.asList(
                        new Loan("url-1", "name", parse("2018-01-01T00:00+01:00")),
                        new Loan("url-2", "name", parse("2018-01-05T00:00+01:00")),
                        new Loan("url-3", "name", parse("2018-01-02T00:00+01:00"))
                ));
        when(client.findLoans(eq(0), any())).thenReturn(response);

        marketplaceWatcher.checkNewLoans();
        marketplaceWatcher.checkNewLoans();

        verify(loanPublisher, times(2)).publishLoans(any());
        verify(client, times(2)).findLoans(eq(0), any());
        verify(client).findLoans(eq(0), eq(parse("2018-01-05T00:00+01:00")));
    }
}