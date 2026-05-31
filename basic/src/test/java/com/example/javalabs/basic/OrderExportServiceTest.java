package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderExportServiceTest {

    @Test
    void loadingAllProcessesEveryOrderWithOneRepositoryCall() {
        InMemoryOrderRepository repository = new InMemoryOrderRepository(sampleOrders(5));
        OrderExportService service = new OrderExportService(repository);

        OrderExportReport report = service.exportByLoadingAll();

        assertEquals(new OrderExportReport(5, 150.0, 1), report);
        assertEquals(1, repository.findAllCount());
        assertEquals(0, repository.findPageCount());
    }

    @Test
    void pagingProcessesEveryOrderWithoutCallingFindAll() {
        InMemoryOrderRepository repository = new InMemoryOrderRepository(sampleOrders(5));
        OrderExportService service = new OrderExportService(repository);

        OrderExportReport report = service.exportInPages(2);

        assertEquals(new OrderExportReport(5, 150.0, 3), report);
        assertEquals(0, repository.findAllCount());
        assertEquals(3, repository.findPageCount());
    }

    @Test
    void pageRequestCalculatesOffsetAndNextPage() {
        PageRequest request = new PageRequest(2, 25);

        assertEquals(50, request.offset());
        assertEquals(new PageRequest(3, 25), request.next());
    }

    private static List<Order> sampleOrders(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(index -> new Order("o-" + index, "c-1", index * 10.0))
                .toList();
    }
}
