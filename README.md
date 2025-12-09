# 🚗 Simplex Uber Analysis

Sistema de otimização para motoristas de aplicativo que utiliza **Programação Linear** (método Simplex) para determinar a melhor estratégia entre utilizar carro próprio ou alugado, maximizando o lucro líquido anual.

## 📋 Sobre o Projeto

Este projeto resolve um problema real enfrentado por motoristas de aplicativo (Uber, 99, etc.): **qual é a melhor estratégia financeira - usar carro próprio ou alugado?**

A aplicação utiliza o algoritmo **Simplex** para encontrar a combinação ótima de meses utilizando cada estratégia ao longo de um ano, considerando:

- ✅ Custo de oportunidade (Selic/CDI) sobre o valor do carro
- ✅ Depreciação anual do veículo
- ✅ Custos fixos (IPVA, seguro, licenciamento)
- ✅ Custos variáveis (combustível, manutenção)
- ✅ Restrições de quilometragem anual (preservação do valor de revenda)
- ✅ Receitas e custos do aluguel

### Modelagem Matemática

O problema é formulado como um problema de **Programação Linear**:

**Função Objetivo:**
```
Max Z = Lp × x₁ + La × x₂
```

Onde:
- `Lp` = Lucro mensal com carro próprio
- `La` = Lucro mensal com carro alugado
- `x₁` = Número de meses usando carro próprio
- `x₂` = Número de meses usando carro alugado

**Restrições:**
- `x₁ + x₂ = 12` (total de meses no ano)
- `KM_mensal × x₁ ≤ KM_limite` (se limite de KM for definido)
- `x₁ ≥ 0, x₂ ≥ 0` (não-negatividade)

## 🛠️ Tecnologias Utilizadas

- **Java 21** - Linguagem de programação
- **Apache Commons Math 3.6.1** - Biblioteca para otimização e algoritmos matemáticos
- **Maven** - Gerenciamento de dependências e build

## 📦 Pré-requisitos

- Java 21 ou superior
- Maven 3.6+ instalado
- Terminal/Command Prompt

## 🚀 Como Executar

### 1. Clone o repositório

```bash
git clone https://github.com/Matheus-Nardi/Simplex-Uber-Analysis.git
cd "otimizacao p sistemas"
```

### 2. Compile o projeto

```bash
mvn clean compile
```

### 3. Execute a aplicação

```bash
mvn exec:java -Dexec.mainClass="SimplexUberAnalysis"
```

Ou, se preferir compilar e executar em um único comando:

```bash
mvn clean compile exec:java -Dexec.mainClass="SimplexUberAnalysis"
```

## 📖 Como Usar

Ao executar a aplicação, você será solicitado a informar os seguintes dados:

### Perfil Operacional
- Faturamento médio diário (R$)
- Dias trabalhados por semana
- KM rodados por dia (média)

### Dados do Carro Próprio
- Valor de mercado do carro (R$)
- Taxa de investimento/Selic anual (%)
- Depreciação anual estimada (%)
- Gasto anual total (IPVA + Seguro + Licenciamento) (R$)
- Manutenção mensal (R$)
- Consumo do veículo (km/litro)

### Restrição Anual (Opcional)
- Limite de KM anual para preservar o carro (0 = sem limite)

### Dados do Aluguel
- Valor do plano semanal (R$)
- Consumo do carro alugado (km/litro)
- Preço do combustível (R$/litro)

### Resultado

A aplicação irá:
1. Calcular o lucro mensal de cada estratégia
2. Aplicar o algoritmo Simplex para encontrar a solução ótima
3. Exibir a recomendação (carro próprio, aluguel ou estratégia híbrida)
4. Mostrar o lucro anual estimado


## 📁 Estrutura do Projeto

```
.
├── src/
│   └── main/
│       └── java/
│           └── SimplexUberAnalysis.java
├── docs/                    # Documentação e artigos
├── pom.xml                  # Configuração Maven
├── README.md                # Este arquivo
└── .gitignore              # Arquivos ignorados pelo Git
```

## 👥 Contribuidores

Este projeto foi desenvolvido por:

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/Matheus-Nardi">
        <img src="https://github.com/Matheus-Nardi.png" width="100px;" alt="Matheus Nardi"/>
        <br />
        <sub><b>Matheus Nardi</b></sub>
      </a>
      <br />
      <a href="https://github.com/Matheus-Nardi" title="GitHub">🔗 GitHub</a>
    </td>
    <td align="center">
      <a href="https://github.com/italobeckman">
        <img src="https://github.com/italobeckman.png" width="100px;" alt="Ítalo Beckman"/>
        <br />
        <sub><b>Ítalo Beckman</b></sub>
      </a>
      <br />
      <a href="https://github.com/italobeckman" title="GitHub">🔗 GitHub</a>
    </td>
  </tr>
</table>

## 📚 Documentação Adicional

Documentação técnica, artigos e análises estão disponíveis na pasta [`docs/`](docs/).


## 📝 Licença

Este projeto é de uso acadêmico e educacional.


**Desenvolvido com ❤️ para a disciplina de Otimização de Sistemas**

