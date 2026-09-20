package br.edu.ifpr.pedidos;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PedidoServiceTest {

    @Test
    void deveFecharPedidoDeClienteComumComFreteDoParanaEPagamentoAprovado() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
            () -> assertEquals("PAGO", resultado.status()),
            () -> assertEquals(10_000L, resultado.subtotalCentavos()),
            () -> assertEquals(0L, resultado.descontoCentavos()),
            () -> assertEquals(1_200L, resultado.freteCentavos()),
            () -> assertEquals(11_200L, resultado.totalCentavos()),
            () -> assertEquals(List.of(11_200L), cobrancas)
        );
    }

    @Test
    void clienteBloqueadoRetornaBloqueadoSemCobrar() {
        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(cobrancas::add);
        Pedido pedido = new Pedido(List.of(), "PR", false, "CUPOM-INVALIDO");

        ResultadoPedido resultado = service.fechar(pedido, new Cliente(false, true, 0));

        assertAll(
            () -> assertEquals("BLOQUEADO", resultado.status()),
            () -> assertEquals(0L, resultado.totalCentavos()),
            () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void deveRejeitarPedidoNulo() {
        PedidoService service = new PedidoService(total -> true);
        assertThrows(NullPointerException.class,
            () -> service.fechar(null, new Cliente(false, false, 0)));
    }

    @Test
    void deveRejeitarClienteNulo() {
        PedidoService service = new PedidoService(total -> true);
        Pedido pedido = pedidoComItem(10_000, 1, 1, false);
        assertThrows(NullPointerException.class, () -> service.fechar(pedido, null));
    }

    @Test
    void pedidoSemItensAtivosLancaExcecao() {
        PedidoService service = new PedidoService(total -> true);
        Pedido pedido = new Pedido(List.of(
            new ItemPedido("INATIVO", 1000, 0, 0, 500, false)), "PR", false, null);
        assertThrows(IllegalArgumentException.class,
            () -> service.fechar(pedido, new Cliente(false, false, 0)));
    }

    @Test
    void faltaDeEstoqueRetornaSemEstoqueSemCobrar() {
        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(cobrancas::add);
        Pedido pedido = pedidoComItem(10_000, 2, 1, false);

        ResultadoPedido resultado = service.fechar(pedido, new Cliente(false, false, 1));

        assertAll(
            () -> assertEquals("SEM_ESTOQUE", resultado.status()),
            () -> assertEquals(0L, resultado.totalCentavos()),
            () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void riscoPendenteRetornaValoresCalculadosSemCobrar() {
        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(cobrancas::add);
        Pedido pedido = new Pedido(List.of(
            new ItemPedido("A", 110_000, 1, 1, 1000, false)), "PR", false, null);

        ResultadoPedido resultado = service.fechar(pedido, new Cliente(false, false, 0));

        assertAll(
            () -> assertEquals("REVISAO", resultado.status()),
            () -> assertEquals(110_000L, resultado.subtotalCentavos()),
            () -> assertEquals(5_500L, resultado.descontoCentavos()),
            () -> assertEquals(0L, resultado.freteCentavos()),
            () -> assertEquals(104_500L, resultado.totalCentavos()),
            () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void pagamentoRecusadoRetornaValoresCalculados() {
        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return false;
        });
        Pedido pedido = pedidoComItem(10_000, 1, 1, false);

        ResultadoPedido resultado = service.fechar(pedido, new Cliente(false, false, 1));

        assertAll(
            () -> assertEquals("PAGAMENTO_RECUSADO", resultado.status()),
            () -> assertEquals(11_200L, resultado.totalCentavos()),
            () -> assertEquals(List.of(11_200L), cobrancas)
        );
    }

    @Test
    void pagamentoPodeSerTentadoNovamenteQuandoIndisponivel() {
        int[] chamadas = {0};
        PedidoService service = new PedidoService(total -> {
            chamadas[0]++;
            if (chamadas[0] < 3) throw new IllegalStateException();
            return true;
        });
        Pedido pedido = pedidoComItem(10_000, 1, 1, false);

        ResultadoPedido resultado = service.fechar(pedido, new Cliente(false, false, 1));

        assertEquals("PAGO", resultado.status());
        assertEquals(3, chamadas[0]);
    }

    @Test
    void devePropagarExcecaoDoProcessador() {
        PedidoService service = new PedidoService(total -> {
            throw new IllegalArgumentException("falha definitiva");
        });
        Pedido pedido = pedidoComItem(10_000, 1, 1, false);
        assertThrows(IllegalArgumentException.class,
            () -> service.fechar(pedido, new Cliente(false, false, 1)));
    }

    @Test
    void deveAplicarDescontoECalcularTotal() {
        PedidoService service = new PedidoService(total -> true);
        Pedido pedido = new Pedido(List.of(
            new ItemPedido("A", 50_000, 1, 1, 1000, false)), "PR", false, null);

        ResultadoPedido resultado = service.fechar(pedido, new Cliente(false, false, 1));

        assertAll(
            () -> assertEquals("PAGO", resultado.status()),
            () -> assertEquals(50_000L, resultado.subtotalCentavos()),
            () -> assertEquals(2_500L, resultado.descontoCentavos()),
            () -> assertEquals(0L, resultado.freteCentavos()),
            () -> assertEquals(47_500L, resultado.totalCentavos())
        );
    }

    private Pedido pedidoComItem(long preco, int quantidade, int estoque, boolean fragil) {
        return new Pedido(List.of(
            new ItemPedido("SKU", preco, quantidade, estoque, 1_000, fragil)), "PR", false, null);
    }
}
