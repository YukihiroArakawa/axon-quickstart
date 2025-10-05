package io.axoniq.quickstart.giftcard.web;

import io.axoniq.quickstart.giftcard.query.FindAllGiftCardsQuery;
import io.axoniq.quickstart.giftcard.query.FindGiftCardQuery;
import io.axoniq.quickstart.giftcard.query.GiftCardSummary;
import io.axoniq.quickstart.giftcard.query.GiftCardSummaryList;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Read-side REST controller exposing GiftCard query endpoints.
 *
 * <p>This service provides the query API for the split quickstart setup. It delegates incoming
 * requests to Axon's {@link QueryGateway}, which in turn resolves the calls against the
 * in-memory projection that listens to domain events published by the command service.</p>
 */
@RestController
@RequestMapping("/api/giftcards")
@CrossOrigin(origins = "*")
public class GiftCardController {

    private final QueryGateway queryGateway;

    public GiftCardController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping
    public CompletableFuture<List<GiftCardSummary>> getAllGiftCards() {
        return queryGateway.query(new FindAllGiftCardsQuery(), GiftCardSummaryList.class)
                           .thenApply(GiftCardSummaryList::giftCards);
    }

    @GetMapping("/{giftCardId}")
    public CompletableFuture<ResponseEntity<GiftCardSummary>> getGiftCard(
            @PathVariable("giftCardId") String giftCardId) {
        return queryGateway.query(new FindGiftCardQuery(giftCardId), GiftCardSummary.class)
                           .thenApply(giftCard -> giftCard != null
                                   ? ResponseEntity.ok(giftCard)
                                   : ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/{giftCardId}/updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<GiftCardSummary> getGiftCardUpdates(@PathVariable("giftCardId") String giftCardId) {
        SubscriptionQueryResult<GiftCardSummary, GiftCardSummary> subscriptionQuery =
                queryGateway.subscriptionQuery(new FindGiftCardQuery(giftCardId),
                                               org.axonframework.messaging.responsetypes.ResponseTypes.instanceOf(
                                                       GiftCardSummary.class),
                                               org.axonframework.messaging.responsetypes.ResponseTypes.instanceOf(
                                                       GiftCardSummary.class));

        return subscriptionQuery
                .initialResult()
                .concatWith(subscriptionQuery.updates())
                .doFinally(signal -> subscriptionQuery.close());
    }

    @GetMapping(value = "/updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<GiftCardSummary> getAllGiftCardUpdates() {
        SubscriptionQueryResult<GiftCardSummaryList, GiftCardSummary> subscriptionQuery =
                queryGateway.subscriptionQuery(new FindAllGiftCardsQuery(),
                                               GiftCardSummaryList.class,
                                               GiftCardSummary.class);

        return subscriptionQuery
                .initialResult()
                .flatMapMany(list -> Flux.fromIterable(list.giftCards()))
                .concatWith(subscriptionQuery.updates())
                .doFinally(signal -> subscriptionQuery.close());
    }
}
