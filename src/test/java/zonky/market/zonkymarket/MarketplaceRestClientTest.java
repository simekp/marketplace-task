package zonky.market.zonkymarket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import zonky.market.zonkymarket.rest.MarketplaceResponseDto;
import zonky.market.zonkymarket.rest.MarketplaceRestClient;

import static java.time.ZonedDateTime.parse;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.util.CollectionUtils.toMultiValueMap;

@ExtendWith(SpringExtension.class)
@RestClientTest(MarketplaceRestClient.class)
class MarketplaceRestClientTest {

    @Autowired
    private MarketplaceRestClient marketplaceRestClient;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Test
    void findTwoNewLoansReturnLoansAndOnePages() {
        mockRestServiceServer
                .expect(manyTimes(),requestTo("https://zonky.cz/loans/marketplace"
                        + "?fields=id,url,datePublished,name&datePublished__gt=2018-01-01T00:00:00Z"))
                .andExpect(header("X-Page", "0"))
                .andExpect(header("X-Size", "2"))
                .andExpect(header("X-Order", "datePublished"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[\n" +
                        "  {\n" +
                        "    \"id\": 1,\n" +
                        "    \"url\": \"https://app.zonky.cz/loan/335161\",\n" +
                        "    \"topped\": false,\n" +
                        "    \"datePublished\": \"2018-11-06T09:20:01.092+01:00\",\n" +
                        "    \"published\": true,\n" +
                        "    \"questionsAllowed\": false,\n" +
                        "    \"multicash\": true\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"id\": 2,\n" +
                        "    \"url\": \"https://app.zonky.cz/loan/334736\",\n" +
                        "    \"topped\": false,\n" +
                        "    \"datePublished\": \"2018-11-06T09:16:41.769+01:00\",\n" +
                        "    \"published\": true,\n" +
                        "    \"questionsAllowed\": false,\n" +
                        "    \"multicash\": true\n" +
                        "  }\n" +
                        "]", MediaType.APPLICATION_JSON)
                        .headers(createTotalHeader("2")));

        MarketplaceResponseDto newLoans = marketplaceRestClient.findLoans(0, parse("2018-01-01T00:00Z"));
        assertThat(newLoans).isNotNull();
        assertThat(newLoans.getLoans()).hasSize(2);
        assertThat(newLoans.getPageCount()).isEqualTo(1);

        mockRestServiceServer.verify();
    }

    @Test
    void findZeroLoanReturnEmptyLoansListAndZeroPages() {
        mockRestServiceServer
                .expect(requestTo("https://zonky.cz/loans/marketplace"
                        + "?fields=id,url,datePublished,name&datePublished__gt=2018-01-01T00:00:00Z"))
                .andExpect(header("X-Page", "0"))
                .andExpect(header("X-Size", "2"))
                .andExpect(header("X-Order", "datePublished"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON).headers(createTotalHeader("0")));

        MarketplaceResponseDto newLoans = marketplaceRestClient.findLoans(0, parse("2018-01-01T00:00:00Z"));
        assertThat(newLoans).isNotNull();
        assertThat(newLoans.getLoans()).isEmpty();
        assertThat(newLoans.getPageCount()).isEqualTo(0);

        mockRestServiceServer.verify();
    }

    @Test
    void findMoreLoansThanPageSizeResponseContainsThreePages() {
        mockRestServiceServer
                .expect(requestTo("https://zonky.cz/loans/marketplace"
                        + "?fields=id,url,datePublished,name&datePublished__gt=2018-01-01T00:00:00Z"))
                .andExpect(header("X-Page", "1"))
                .andExpect(header("X-Size", "2"))
                .andExpect(header("X-Order", "datePublished"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[\n" +
                        "  {\n" +
                        "    \"id\": 1,\n" +
                        "    \"url\": \"https://app.zonky.cz/loan/335161\",\n" +
                        "    \"topped\": false,\n" +
                        "    \"datePublished\": \"2018-11-06T09:20:01.092+01:00\",\n" +
                        "    \"published\": true,\n" +
                        "    \"questionsAllowed\": false,\n" +
                        "    \"multicash\": true\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"id\": 2,\n" +
                        "    \"url\": \"https://app.zonky.cz/loan/334736\",\n" +
                        "    \"topped\": false,\n" +
                        "    \"datePublished\": \"2018-11-06T09:16:41.769+01:00\",\n" +
                        "    \"published\": true,\n" +
                        "    \"questionsAllowed\": false,\n" +
                        "    \"multicash\": true\n" +
                        "  }\n" +
                        "]", MediaType.APPLICATION_JSON)
                        .headers(createTotalHeader("5")));


        MarketplaceResponseDto newLoans = marketplaceRestClient.findLoans(1, parse("2018-01-01T00:00Z"));
        assertThat(newLoans).isNotNull();
        assertThat(newLoans.getLoans()).hasSize(2);
        assertThat(newLoans.getPageCount()).isEqualTo(3);

        mockRestServiceServer.verify();
    }

    private HttpHeaders createTotalHeader(String count) {
        return new HttpHeaders(toMultiValueMap(singletonMap("X-Total", singletonList(count))));
    }
}