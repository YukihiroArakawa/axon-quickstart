package io.axoniq.quickstart.giftcard.web;

import io.axoniq.quickstart.giftcard.command.IssueGiftCardCommand;
import io.axoniq.quickstart.giftcard.command.RedeemGiftCardCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller responsible for dispatching gift card commands.
 *
 * <p>This controller exposes the write-side API of the sample, forwarding requests to the
 * Axon command bus. Query endpoints are intentionally excluded to keep this service focused
 * on event publication.</p>
 *
 * @see CommandGateway
 * @see <a href="https://docs.axoniq.io/reference-guide/">Axon Framework Reference Guide</a>
 *
 * @author AxonIQ Quickstart
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/giftcards")
@CrossOrigin(origins = "*")
public class GiftCardController {

    /**
     * Axon Framework command gateway for dispatching commands to aggregates.
     * Handles command validation, routing, and result handling.
     */
    private final CommandGateway commandGateway;

    /**
     * Constructs a new GiftCardController with required Axon Framework gateways.
     *
     * @param commandGateway the command gateway for dispatching commands
     */
    public GiftCardController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    /**
     * Issues a new gift card with the specified amount.
     *
     * <p>This endpoint creates a new gift card by generating a unique identifier and
     * dispatching an {@link IssueGiftCardCommand} through the command gateway. The
     * operation is asynchronous and returns the generated gift card ID upon successful
     * completion.</p>
     *
     * <p><strong>HTTP mapping:</strong> {@code POST /api/giftcards}</p>
     *
     * <p><strong>Request body:</strong> JSON containing the initial amount</p>
     * <pre>{@code
     * {
     *   "amount": 50.00
     * }
     * }</pre>
     *
     * <p><strong>Success response:</strong> HTTP 200 with the new gift card ID</p>
     * <p><strong>Error cases:</strong> HTTP 400 for validation errors (negative amounts, etc.)</p>
     *
     * @param request the issue request containing the initial gift card amount
     * @return CompletableFuture containing ResponseEntity with the generated gift card ID
     */
    @PostMapping
    public CompletableFuture<ResponseEntity<String>> issueGiftCard(@RequestBody IssueRequest request) {
        String giftCardId = UUID.randomUUID().toString();
        return commandGateway.send(new IssueGiftCardCommand(giftCardId, request.amount()))
                             .thenApply(result -> ResponseEntity.ok(giftCardId));
    }

    /**
     * Redeems a specified amount from an existing gift card.
     *
     * <p>This endpoint processes a redemption by dispatching a {@link RedeemGiftCardCommand}
     * for the specified gift card. The operation validates that sufficient funds are available
     * and updates the gift card balance accordingly.</p>
     *
     * <p><strong>HTTP mapping:</strong> {@code POST /api/giftcards/{giftCardId}/redeem}</p>
     *
     * <p><strong>Path variable:</strong> {@code giftCardId} - the unique identifier of the gift card</p>
     * <p><strong>Request body:</strong> JSON containing the redemption amount</p>
     * <pre>{@code
     * {
     *   "amount": 25.00
     * }
     * }</pre>
     *
     * <p><strong>Success response:</strong> HTTP 200 with success message</p>
     * <p><strong>Error cases:</strong></p>
     * <ul>
     *   <li>HTTP 400 for insufficient funds or invalid amounts</li>
     *   <li>HTTP 400 for non-existent gift card IDs</li>
     * </ul>
     *
     * @param giftCardId the unique identifier of the gift card to redeem from
     * @param request the redeem request containing the amount to redeem
     * @return CompletableFuture containing ResponseEntity with success message or error details
     */
    @PostMapping("/{giftCardId}/redeem")
    public CompletableFuture<ResponseEntity<String>> redeemGiftCard(
            @PathVariable("giftCardId") String giftCardId,
            @RequestBody RedeemRequest request) {
        return commandGateway.send(new RedeemGiftCardCommand(giftCardId, request.amount()))
                             .thenApply(result -> ResponseEntity.ok("Redeemed successfully"))
                             .exceptionally(throwable -> ResponseEntity.badRequest().body(throwable.getMessage()));
    }

    /**
     * Request DTO for gift card issuance operations.
     *
     * <p>This record encapsulates the data required to issue a new gift card,
     * containing only the initial monetary amount. The gift card ID is generated
     * automatically by the controller.</p>
     *
     * @param amount the initial amount to load onto the new gift card (must be positive)
     */
    public record IssueRequest(BigDecimal amount) {

    }

    /**
     * Request DTO for gift card redemption operations.
     *
     * <p>This record encapsulates the data required to redeem from an existing
     * gift card. The gift card ID is provided as a path parameter in the endpoint.</p>
     *
     * @param amount the amount to redeem from the gift card (must be positive and not exceed balance)
     */
    public record RedeemRequest(BigDecimal amount) {

    }
}
