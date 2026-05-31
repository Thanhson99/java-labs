package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CursorOrderExportServiceTest {

    @Test
    void cursorExportProcessesEveryOrderInSortedOrder() {
        InMemoryCursorOrderRepository repository = new InMemoryCursorOrderRepository(unsortedOrders());
        CursorOrderExportService service = new CursorOrderExportService(repository);

        OrderExportReport report = service.exportWithCursor(2);

        assertEquals(new OrderExportReport(5, 150.0, 3), report);
        assertEquals(3, repository.findAfterCount());
    }

    @Test
    void cursorPageReturnsNextCursorFromLastItemInPage() {
        InMemoryCursorOrderRepository repository = new InMemoryCursorOrderRepository(unsortedOrders());

        CursorPage<Order> firstPage = repository.findAfter(CursorPageRequest.first(2));
        CursorPage<Order> secondPage =
                repository.findAfter(new CursorPageRequest(firstPage.nextCursor(), 2));

        assertEquals(List.of("o-1", "o-2"), firstPage.items().stream().map(Order::id).toList());
        assertEquals(Optional.of("o-2"), firstPage.nextCursor());
        assertEquals(List.of("o-3", "o-4"), secondPage.items().stream().map(Order::id).toList());
    }

    @Test
    void cursorAfterLastItemReturnsEmptyPage() {
        InMemoryCursorOrderRepository repository = new InMemoryCursorOrderRepository(unsortedOrders());

        CursorPage<Order> page = repository.findAfter(new CursorPageRequest(Optional.of("o-9"), 2));

        assertEquals(0, page.items().size());
        assertFalse(page.hasNext());
    }

    private static List<Order> unsortedOrders() {
        return List.of(
                new Order("o-5", "c-1", 50.0),
                new Order("o-1", "c-1", 10.0),
                new Order("o-3", "c-1", 30.0),
                new Order("o-2", "c-1", 20.0),
                new Order("o-4", "c-1", 40.0)
        );
    }
}
