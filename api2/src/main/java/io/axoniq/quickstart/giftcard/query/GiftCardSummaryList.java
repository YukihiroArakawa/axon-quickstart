package io.axoniq.quickstart.giftcard.query;

import java.util.List;

/**
 * Wrapper class for a collection of gift card summaries.
 *
 * <p>This record serves as a type-safe container for collections of {@link GiftCardSummary}
 * objects, specifically designed to resolve type conversion issues with generic collections
 * in Axon Framework query responses. It wraps a {@code List<GiftCardSummary>} to provide
 * explicit type information that the framework can properly handle during serialization
 * and deserialization.</p>
 *
 * <p><strong>Technical rationale:</strong></p>
 * <p>This wrapper addresses the Java type erasure limitation where generic type information
 * ({@code List<GiftCardSummary>}) is lost at runtime. Without this wrapper, the query system
 * encounters "Retrieved response [class java.util.ArrayList] is not convertible to a List"
 * errors because it cannot determine the correct parameterized type.</p>
 *
 * <p>Usage pattern:</p>
 * <ul>
 *   <li>Returned by {@link FindAllGiftCardsQuery} query handlers</li>
 *   <li>Used in {@link GiftCardProjection} for consistent type handling</li>
 *   <li>Unwrapped in REST controllers to extract the actual list</li>
 *   <li>Serialized properly to JSON with correct type metadata</li>
 * </ul>
 *
 * <p>Design benefits:</p>
 * <ul>
 *   <li><strong>Type safety</strong>: Explicit container type prevents ClassCastException</li>
 *   <li><strong>Framework compatibility</strong>: Works seamlessly with Axon's query system</li>
 *   <li><strong>Serialization support</strong>: JSON serialization preserves structure</li>
 *   <li><strong>Future extensibility</strong>: Can add metadata fields without breaking changes</li>
 * </ul>
 *
 * <p>Alternative approaches considered:</p>
 * <ul>
 *   <li>Using raw {@code List} (loses type safety)</li>
 *   <li>Using {@code TypeReference} (adds complexity without clear benefit)</li>
 *   <li>Custom collection class (overengineered for this use case)</li>
 * </ul>
 *
 * @param giftCards the list of gift card summaries (never null, may be empty)
 *
 * @see GiftCardSummary
 * @see FindAllGiftCardsQuery
 * @see GiftCardProjection
 * @see <a href="https://docs.axoniq.io/reference-guide/">Axon Framework Reference Guide</a>
 *
 * @author AxonIQ Quickstart
 * @version 1.0
 * @since 1.0
 */
public record GiftCardSummaryList(List<GiftCardSummary> giftCards) {
}