import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import java.util.*;
import java.text.DecimalFormat;

/**
 * Sistema Avançado de Análise de Viabilidade (Uber/99)
 * * MELHORIAS DESTA VERSÃO:
 * 1. Considera Custo de Oportunidade (Selic/CDI) sobre o valor do carro.
 * 2. Adiciona Restrição de KM Anual (Preservação de valor de revenda).
 * 3. Otimização Híbrida via Simplex (Mix de estratégias).
 */
public class SimplexUberAnalysis {

    static class DadosAvancados {
        double valorCarro;
        double depreciacaoAnualPercentual; // Ex: 15% ao ano
        double ipvaSeguroAnual;
        double manutencaoMensal;
        double kmPorLitroProprio;
        double custoOportunidadeAnual; // Ex: 10% (Selic) se o dinheiro estivesse investido
        
        double planoSemanal;
        double kmPorLitroAlugado;
        
        double precoCombustivel;
        
        double faturamentoDiario;
        double diasTrabalhadosSemana;
        double kmDiario;
        double limiteKmAnualProprio; // Restrição para não desvalorizar demais
        
        public double getKmMensal() {
            return kmDiario * diasTrabalhadosSemana * 4.33;
        }

        public double getReceitaMensal() {
            return faturamentoDiario * diasTrabalhadosSemana * 4.33;
        }

        // Custo Mensal Próprio (Com Custo de Oportunidade)
        public double getLucroMensalProprio() {
            double custoCombustivel = (getKmMensal() / kmPorLitroProprio) * precoCombustivel;
            double custoIpvaSeguroMes = ipvaSeguroAnual / 12.0;
            
            // Depreciação real baseada no valor do carro
            double custoDepreciacaoMes = (valorCarro * (depreciacaoAnualPercentual / 100.0)) / 12.0;
            
            // Custo de Oportunidade: Quanto esse dinheiro renderia no banco?
            // Se o carro custa 60k e a Selic é 10% a.a., perdemos R$ 500/mês
            double custoOportunidadeMes = (valorCarro * (custoOportunidadeAnual / 100.0)) / 12.0;
            
            double custoTotal = custoCombustivel + manutencaoMensal + 
                                custoIpvaSeguroMes + custoDepreciacaoMes + custoOportunidadeMes;
                                
            return getReceitaMensal() - custoTotal;
        }

        public double getLucroMensalAlugado() {
            double custoCombustivel = (getKmMensal() / kmPorLitroAlugado) * precoCombustivel;
            double custoAluguelMes = planoSemanal * 4.33;
            
            return getReceitaMensal() - (custoAluguelMes + custoCombustivel);
        }
    }

    public static void main(String[] args) {
        // Força Locale US para aceitar ponto como decimal (ex: 5.50)
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);
        DadosAvancados dados = new DadosAvancados();
        
        exibirBanner();
        coletarDados(scanner, dados);
        
        System.out.println("\nCalculando melhor combinação...");
        resolverSimplex(dados);
        
        scanner.close();
    }

    private static void exibirBanner() {
        System.out.println("=== Otimizador de Frota Híbrida ===");
        System.out.println("Simplex considerando custo, depreciação e limite de km.");
    }

    private static void coletarDados(Scanner sc, DadosAvancados dados) {
        System.out.println("\nPerfil operacional:");
        System.out.print("Faturamento Médio Diário (R$): ");
        dados.faturamentoDiario = lerDouble(sc);
        System.out.print("Dias trabalhados por semana: ");
        dados.diasTrabalhadosSemana = lerDouble(sc);
        System.out.print("KM rodados por dia (Média): ");
        dados.kmDiario = lerDouble(sc);

        System.out.println("\nDados do carro próprio:");
        System.out.print("Valor de mercado do carro (R$): ");
        dados.valorCarro = lerDouble(sc);
        System.out.print("Investimento/Selic Anual (% ganho se deixasse no banco) [Ex: 10.5]: ");
        dados.custoOportunidadeAnual = lerDouble(sc);
        System.out.print("Depreciação Anual estimada (%) [Ex: 15]: ");
        dados.depreciacaoAnualPercentual = lerDouble(sc);
        System.out.print("Gasto anual total (IPVA + Seguro + Licenc.) (R$): ");
        dados.ipvaSeguroAnual = lerDouble(sc);
        System.out.print("Manutenção mensal (Pneus/Óleo/Mecânica) (R$): ");
        dados.manutencaoMensal = lerDouble(sc);
        System.out.print("Consumo (km/litro): ");
        dados.kmPorLitroProprio = lerDouble(sc);
        
        System.out.println("\nRestrição anual:");
        System.out.println("Informe um limite anual de km para preservar o carro (0 = sem limite).");
        System.out.print("Limite de KM Anual para o Carro Próprio [Ex: 60000 | 0 = sem limite]: ");
        dados.limiteKmAnualProprio = lerDouble(sc);

        System.out.println("\nDados do aluguel e combustível:");
        System.out.print("Valor do Plano Semanal (R$): ");
        dados.planoSemanal = lerDouble(sc);
        System.out.print("Consumo Carro Alugado (km/litro): ");
        dados.kmPorLitroAlugado = lerDouble(sc);
        System.out.print("Preço do Combustível (R$/litro): ");
        dados.precoCombustivel = lerDouble(sc);
    }

    private static double lerDouble(Scanner sc) {
        while (true) {
            String entrada = sc.next().trim().replace(',', '.');
            try {
                return Double.parseDouble(entrada);
            } catch (NumberFormatException ex) {
                System.out.print("Valor inválido, tente novamente: ");
            }
        }
    }

    private static void resolverSimplex(DadosAvancados dados) {
        double lucroProprio = dados.getLucroMensalProprio();
        double lucroAlugado = dados.getLucroMensalAlugado();
        
        exibirResumoFinanceiro(dados, lucroProprio, lucroAlugado);

        // Se ambos derem prejuízo, abortar otimização
        if (lucroProprio < 0 && lucroAlugado < 0) {
            System.out.println("\nAlerta: ambas as estratégias geram prejuízo mensal.");
            System.out.println("Revise premissas de receita e custos antes de prosseguir.");
            return;
        }

        LinearObjectiveFunction funcaoObjetivo = new LinearObjectiveFunction(
            new double[] { lucroProprio, lucroAlugado }, 0
        );

        Collection<LinearConstraint> restricoes = new ArrayList<>();
        boolean possuiRestricaoKm = dados.limiteKmAnualProprio > 0;

        restricoes.add(new LinearConstraint(
            new double[] { 1, 1 }, 
            Relationship.LEQ, 
            12
        ));

        if (possuiRestricaoKm) {
            restricoes.add(new LinearConstraint(
                new double[] { dados.getKmMensal(), 0 }, 
                Relationship.LEQ, 
                dados.limiteKmAnualProprio
            ));
        }

        try {
            SimplexSolver solver = new SimplexSolver();
            PointValuePair solucao = solver.optimize(
                new MaxIter(100),
                funcaoObjetivo,
                new LinearConstraintSet(restricoes),
                GoalType.MAXIMIZE,
                new NonNegativeConstraint(true)
            );

            double[] ponto = solucao.getPoint();
            double x1_Proprio = ponto[0];
            double x2_Alugado = ponto[1];
            double lucroMaximo = solucao.getValue();

            interpretacaoResultados(x1_Proprio, x2_Alugado, lucroMaximo, dados, possuiRestricaoKm);

        } catch (Exception e) {
            System.out.println("Erro na otimização: " + e.getMessage());
        }
    }

    private static void exibirResumoFinanceiro(DadosAvancados dados, double lp, double la) {
        DecimalFormat df = new DecimalFormat("R$ #,##0.00");
        double custoOportunidadeMes = (dados.valorCarro * (dados.custoOportunidadeAnual / 100)) / 12;
        System.out.println("\nLucro mensal estimado:");
        System.out.println("Carro próprio: " + df.format(lp) + " (receita " + df.format(dados.getReceitaMensal()) + ", custo oportunidade " + df.format(custoOportunidadeMes) + ")");
        System.out.println("Carro alugado: " + df.format(la));
    }

    private static void interpretacaoResultados(double mesesProprio, double mesesAlugado, double lucroTotal, DadosAvancados dados, boolean possuiRestricaoKm) {
        System.out.println("\nPlano sugerido (Simplex):");
        System.out.printf("Carro próprio: %.1f meses\n", mesesProprio);
        System.out.printf("Carro alugado: %.1f meses\n", mesesAlugado);
        System.out.printf("Lucro anual estimado: R$ %,.2f\n", lucroTotal);

        double kmTotalProprio = mesesProprio * dados.getKmMensal();
        
        if (mesesProprio > 11.9) {
            System.out.println("Carro próprio domina.");
            if (possuiRestricaoKm) {
                System.out.println("Limite anual não é atingido.");
            } else {
                System.out.println("Nenhum limite anual informado.");
            }
        } 
        else if (mesesAlugado > 11.9) {
            System.out.println("Aluguel domina. Custos fixos do carro próprio superam o lucro.");
        } 
        else {
            System.out.println("Estratégia mista recomendada.");
            if (possuiRestricaoKm) {
                System.out.printf("Use o carro próprio até %.0f km (estimado %.0f km). Depois, migre para o aluguel.\n", 
                    dados.limiteKmAnualProprio, kmTotalProprio);
            } else {
                System.out.println("Mix definido apenas pelos custos relativos, sem limite anual.");
            }
        }
    }
}