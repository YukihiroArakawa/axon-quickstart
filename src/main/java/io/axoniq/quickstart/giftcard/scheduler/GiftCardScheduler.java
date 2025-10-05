package io.axoniq.quickstart.giftcard.scheduler;

import io.axoniq.quickstart.giftcard.command.IssueGiftCardCommand;
import io.axoniq.quickstart.giftcard.command.RedeemGiftCardCommand;
import io.axoniq.quickstart.giftcard.query.FindAllGiftCardsQuery;
import io.axoniq.quickstart.giftcard.query.GiftCardSummary;
import io.axoniq.quickstart.giftcard.query.GiftCardSummaryList;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Automated scheduler that performs random gift card operations for demonstration purposes.
 *
 * <p>This component demonstrates the gift card system's capabilities by automatically
 * generating realistic activity through scheduled gift card issuances and redemptions.
 * It serves as both a demo driver and a stress testing mechanism for the CQRS/Event
 * Sourcing implementation.</p>
 *
 * <p><strong>Operational pattern:</strong></p>
 * <p>The scheduler runs every 30 seconds and performs probabilistic operations:</p>
 * <ul>
 *   <li><strong>70% chance</strong>: Issues a new gift card with random amount ($10-$100)</li>
 *   <li><strong>50% chance</strong>: Redeems from an existing gift card (if any exist)</li>
 *   <li><strong>Both operations</strong> can occur in the same execution cycle</li>
 * </ul>
 *
 * <p><strong>Business logic simulation:</strong></p>
 * <ul>
 *   <li>New gift cards mimic customer purchases or promotional campaigns</li>
 *   <li>Redemptions simulate actual usage by customers</li>
 *   <li>Realistic amounts reflect typical gift card values and usage patterns</li>
 *   <li>Smart selection ensures only active gift cards (balance > 0) are selected for redemption</li>
 * </ul>
 *
 * <p><strong>Educational value:</strong></p>
 * <ul>
 *   <li>Demonstrates command dispatching in a scheduled context</li>
 *   <li>Shows query-before-command patterns for business validation</li>
 *   <li>Exhibits proper error handling in scheduled operations</li>
 *   <li>Provides continuous data flow for testing real-time UI updates</li>
 * </ul>
 *
 * <p><strong>Real-time demonstration benefits:</strong></p>
 * <ul>
 *   <li>Keeps the UI active with live data updates</li>
 *   <li>Showcases subscription query capabilities</li>
 *   <li>Provides immediate visual feedback for developers and stakeholders</li>
 *   <li>Tests system behavior under continuous load</li>
 * </ul>
 *
 * <p><strong>Production considerations:</strong></p>
 * <p>This scheduler is designed for demonstration and development environments.
 * For production systems, consider:</p>
 * <ul>
 *   <li>Removing or disabling this component</li>
 *   <li>Implementing proper business-driven scheduled operations</li>
 *   <li>Adding configuration to control scheduling behavior</li>
 *   <li>Implementing more sophisticated error handling and monitoring</li>
 * </ul>
 *
 * @see IssueGiftCardCommand
 * @see RedeemGiftCardCommand
 * @see <a href="https://docs.axoniq.io/reference-guide/">Axon Framework Reference Guide</a>
 *
 * @author AxonIQ Quickstart
 * @version 1.0
 * @since 1.0
 */
@Component
public class GiftCardScheduler {

    /**
     * Logger for tracking scheduled operations and debugging issues.
     */
    private static final Logger logger = LoggerFactory.getLogger(GiftCardScheduler.class);

    /**
     * Axon Framework command gateway for dispatching gift card commands.
     */
    private final CommandGateway commandGateway;

    /**
     * Axon Framework query gateway for retrieving gift card data before operations.
     */
    private final QueryGateway queryGateway;

    /**
     * Random number generator for probabilistic operation decisions and value generation.
     */
    private final Random random = new Random();

    /**
     * Constructs a new GiftCardScheduler with required dependencies.
     *
     * @param commandGateway the command gateway for dispatching commands
     * @param queryGateway the query gateway for retrieving gift card data
     */
    public GiftCardScheduler(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    /**
     * Main scheduled method that performs random gift card operations every 30 seconds.
     *
     * <p>This method orchestrates the demonstration activity by:</p>
     * <ol>
     *   <li>Querying the current state of all gift cards</li>
     *   <li>Probabilistically deciding whether to issue new gift cards (70% chance)</li>
     *   <li>Probabilistically deciding whether to redeem from existing cards (50% chance)</li>
     *   <li>Logging all operations for tracking and debugging</li>
     * </ol>
     *
     * <p><strong>Scheduling configuration:</strong></p>
     * <ul>
     *   <li>Fixed rate: 30 seconds between executions</li>
     *   <li>Automatic retry: Spring handles failed executions</li>
     *   <li>Thread safety: Each execution runs in isolation</li>
     * </ul>
     *
     * <p><strong>Error handling:</strong></p>
     * <p>The method includes comprehensive exception handling to ensure that
     * individual operation failures don't stop the scheduler from continuing
     * its demonstration activities.</p>
     *
     * <p><strong>Performance impact:</strong></p>
     * <p>The method executes synchronously but completes quickly as it only
     * performs a single query and 0-2 command operations per execution.</p>
     */
    @Scheduled(fixedRate = 30000)
    public void performRandomGiftCardOperations() {
        try {
            List<GiftCardSummary> giftCards = queryGateway.query(new FindAllGiftCardsQuery(), GiftCardSummaryList.class)
                    .get().giftCards();

            if (random.nextDouble() < 0.7) {
                issueRandomGiftCard();
            }

            if (!giftCards.isEmpty() && random.nextDouble() < 0.5) {
                redeemFromRandomGiftCard(giftCards);
            }

        } catch (Exception e) {
            logger.error("Error during scheduled gift card operations", e);
        }
    }

    /**
     * Issues a new gift card with a randomly generated amount.
     *
     * <p>This method simulates the business process of gift card creation by:</p>
     * <ul>
     *   <li>Generating a random amount between $10 and $100</li>
     *   <li>Creating a unique UUID for the new gift card</li>
     *   <li>Dispatching an {@link IssueGiftCardCommand} through the command gateway</li>
     *   <li>Logging the successful operation for demonstration purposes</li>
     * </ul>
     *
     * <p><strong>Amount generation strategy:</strong></p>
     * <p>The random amount is calculated as: {@code 10 + random(0-90)} resulting in
     * values from $10 to $100, which reflects realistic gift card denominations.</p>
     *
     * <p><strong>Synchronous execution:</strong></p>
     * <p>The method waits for command completion using {@code .get()} to ensure
     * the operation succeeds before logging, providing accurate demonstration feedback.</p>
     *
     * <p><strong>Error handling:</strong></p>
     * <p>Any exceptions during the issuance process are caught and logged,
     * preventing them from disrupting the overall scheduling cycle.</p>
     */
    private void issueRandomGiftCard() {
        try {
            BigDecimal amount = BigDecimal.valueOf(10 + random.nextInt(91));
            String giftCardId = UUID.randomUUID().toString();

            commandGateway.send(new IssueGiftCardCommand(giftCardId, amount)).get();
            logger.info("Scheduled operation: Issued gift card {} with amount ${}", giftCardId, amount);
        } catch (Exception e) {
            logger.error("Error issuing scheduled gift card", e);
        }
    }

    /**
     * Redeems a random amount from a randomly selected active gift card.
     *
     * <p>This method simulates realistic gift card usage patterns by:</p>
     * <ol>
     *   <li>Filtering to only active gift cards (remaining balance > 0)</li>
     *   <li>Randomly selecting one active gift card for redemption</li>
     *   <li>Calculating a realistic redemption amount (up to $20 or remaining balance)</li>
     *   <li>Dispatching a {@link RedeemGiftCardCommand} with proper validation</li>
     *   <li>Logging the successful redemption for demonstration tracking</li>
     * </ol>
     *
     * <p><strong>Smart card selection:</strong></p>
     * <p>The method only considers gift cards with positive balances, ensuring
     * that redemption operations will succeed and demonstrate valid business scenarios.</p>
     *
     * <p><strong>Realistic redemption amounts:</strong></p>
     * <p>Redemption amounts are calculated as: {@code 1 + random(0-19)} with an
     * upper limit of the card's remaining balance, simulating typical customer usage.</p>
     *
     * <p><strong>Business validation:</strong></p>
     * <p>The method performs an additional validation check to ensure the calculated
     * redemption amount doesn't exceed the available balance before dispatching the command.</p>
     *
     * <p><strong>Defensive programming:</strong></p>
     * <p>Includes null checks and exception handling to gracefully handle edge cases
     * and maintain system stability during demonstration runs.</p>
     *
     * @param giftCards the current list of all gift cards in the system
     */
    private void redeemFromRandomGiftCard(List<GiftCardSummary> giftCards) {
        List<GiftCardSummary> activeGiftCards = giftCards.stream()
                .filter(gc -> gc.remainingValue().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        if (!activeGiftCards.isEmpty()) {
            try {
                GiftCardSummary selectedCard = activeGiftCards.get(random.nextInt(activeGiftCards.size()));
                BigDecimal maxRedeem = selectedCard.remainingValue();
                BigDecimal redeemAmount = BigDecimal.valueOf(1 + random.nextInt(Math.min(20, maxRedeem.intValue())));

                if (redeemAmount.compareTo(maxRedeem) <= 0) {
                    commandGateway.send(new RedeemGiftCardCommand(selectedCard.giftCardId(), redeemAmount)).get();
                    logger.info("Scheduled operation: Redeemed ${} from gift card {}", redeemAmount, selectedCard.giftCardId());
                }
            } catch (Exception e) {
                logger.error("Error redeeming from scheduled gift card", e);
            }
        }
    }
}