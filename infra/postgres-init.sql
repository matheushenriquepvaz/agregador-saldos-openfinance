-- Massa inicial para o servico extrato-gravador
-- Executado automaticamente na criacao do banco PostgreSQL

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS eventos_processados (
  evento_id UUID PRIMARY KEY,
  data_processamento TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS saldos_clientes (
  cliente_id VARCHAR(255) PRIMARY KEY,
  saldo_atual NUMERIC(19,2) NOT NULL,
  quantidade_transacoes BIGINT NOT NULL,
  ultima_atualizacao TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS transacoes (
  evento_id UUID PRIMARY KEY,
  cliente_id VARCHAR(255) NOT NULL,
  instituicao_id VARCHAR(255) NOT NULL,
  tipo VARCHAR(20) NOT NULL,
  valor NUMERIC(19,2) NOT NULL,
  descricao VARCHAR(255),
  data_lancamento DATE NOT NULL,
  data_processamento TIMESTAMP NOT NULL
);

INSERT INTO saldos_clientes (cliente_id, saldo_atual, quantidade_transacoes, ultima_atualizacao)
VALUES
  ('cliente-001', 1300.00, 2, NOW()),
  ('cliente-002', 2450.50, 3, NOW()),
  ('cliente-003', -120.75, 1, NOW())
ON CONFLICT (cliente_id) DO NOTHING;

INSERT INTO transacoes (evento_id, cliente_id, instituicao_id, tipo, valor, descricao, data_lancamento, data_processamento)
VALUES
  (gen_random_uuid(), 'cliente-001', 'inst-001', 'CREDITO', 1500.00, 'SALARIO', CURRENT_DATE - INTERVAL '2 day', NOW() - INTERVAL '2 day'),
  (gen_random_uuid(), 'cliente-001', 'inst-001', 'DEBITO', 200.00, 'MERCADO', CURRENT_DATE - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
  (gen_random_uuid(), 'cliente-002', 'inst-002', 'CREDITO', 2500.50, 'TRANSFERENCIA', CURRENT_DATE - INTERVAL '3 day', NOW() - INTERVAL '3 day'),
  (gen_random_uuid(), 'cliente-002', 'inst-002', 'DEBITO', 50.00, 'TARIFA', CURRENT_DATE - INTERVAL '2 day', NOW() - INTERVAL '2 day'),
  (gen_random_uuid(), 'cliente-003', 'inst-001', 'DEBITO', 120.75, 'CONTA LUZ', CURRENT_DATE - INTERVAL '1 day', NOW() - INTERVAL '1 day');

INSERT INTO eventos_processados (evento_id, data_processamento)
VALUES
  (gen_random_uuid(), NOW() - INTERVAL '3 day'),
  (gen_random_uuid(), NOW() - INTERVAL '2 day'),
  (gen_random_uuid(), NOW() - INTERVAL '1 day')
ON CONFLICT (evento_id) DO NOTHING;


