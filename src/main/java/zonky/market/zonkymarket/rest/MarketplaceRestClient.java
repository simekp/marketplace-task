package zonky.market.zonkymarket.rest;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import zonky.market.zonkymarket.Loan;

import java.time.ZonedDateTime;

import static java.util.Collections.singletonList;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpMethod.GET;

@Component
public class MarketplaceRestClient {

    private static final Logger logger = getLogger(MarketplaceRestClient.class);

    private final RestTemplate restTemplate;
    private final String zonkyBaseUrl;
    private final String zanyMarketEndpoint;
    private final Long zonkyMarketplacePageSize;

    public MarketplaceRestClient(RestTemplateBuilder restTemplateBuilder,
                                 @Value("${zonky.marketplace.baseUrl}") String zonkyBaseUrl,
                                 @Value("${zonky.marketplace.marketEndpoint}") String zanyMarketEndpoint,
                                 @Value("${zonky.marketplace.pageSize}") Long zonkyMarketplacePageSize) {
        this.restTemplate = restTemplateBuilder.build();
        this.zonkyBaseUrl = zonkyBaseUrl;
        this.zanyMarketEndpoint = zanyMarketEndpoint;
        this.zonkyMarketplacePageSize = zonkyMarketplacePageSize;
    }

    /**
     * Create request to Marketplace
     *
     * @param pageNumber Page number of marketplace content
     * @param lastSearch Search newer loans than this date
     * @return if request to marketplace failed return empty otherwise with received data
     */
    public MarketplaceResponseDto findLoans(final int pageNumber, final ZonedDateTime lastSearch) {
        final HttpEntity<String> entity = new HttpEntity<>("parameters", getHeaders(pageNumber));
        final String requestUrl = getRequestUrl(lastSearch);

        try {
            ResponseEntity<Loan[]> exchange = restTemplate.exchange(requestUrl, GET, entity, Loan[].class);
            return new MarketplaceResponseDto(exchange, zonkyMarketplacePageSize);
        } catch (RestClientException e) {
            logger.error("Communication with zonky marketplace failed for pageNumber={}, size={}, url={}", pageNumber,
                    zonkyMarketplacePageSize, requestUrl, e);
            throw new MarketplaceRestException(e);
        }
    }

    private String getRequestUrl(final ZonedDateTime lastSearch) {
        return UriComponentsBuilder
                .fromHttpUrl(zonkyBaseUrl)
                .path(zanyMarketEndpoint)
                .queryParam("fields", "id,url,datePublished,name")
                .queryParam("datePublished__gt", lastSearch.toInstant().toString())
                .toUriString();
    }

    private HttpHeaders getHeaders(final Integer page) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(singletonList(MediaType.APPLICATION_JSON_UTF8));
        headers.add("X-Page", page.toString());
        headers.add("X-Size", zonkyMarketplacePageSize.toString());
        headers.add("X-Order", "datePublished");
        return headers;
    }
}
