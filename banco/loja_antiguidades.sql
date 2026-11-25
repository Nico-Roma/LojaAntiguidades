-- ============================================================
-- Loja de Antiguidades Musicais - script do banco (MariaDB)
-- Importe pelo phpMyAdmin (aba "Importar") ou pelo terminal:
--   mariadb -u root -p < loja_antiguidades.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS `loja_antiguidades`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE `loja_antiguidades`;

SET NAMES utf8mb4;

DROP TABLE IF EXISTS `produto`;
DROP TABLE IF EXISTS `categoria`;

-- ------------------------------------------------------------
-- Tabela categoria (Vitrolas, Discos, Instrumentos)
-- ------------------------------------------------------------
CREATE TABLE `categoria` (
  `id`   int(11)     NOT NULL AUTO_INCREMENT,
  `nome` varchar(60) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_categoria_nome` (`nome`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ------------------------------------------------------------
-- Tabela produto (itens à venda)
-- ------------------------------------------------------------
CREATE TABLE `produto` (
  `id`             int(11)       NOT NULL AUTO_INCREMENT,
  `nome`           varchar(120)  NOT NULL,
  `categoria_id`   int(11)       NOT NULL,
  `descricao`      text          DEFAULT NULL,
  `ano_fabricacao` smallint(6)   DEFAULT NULL,
  `estado`         varchar(20)   NOT NULL DEFAULT 'Bom',
  `preco`          decimal(10,2) NOT NULL,
  `estoque`        int(11)       NOT NULL DEFAULT 1,
  `data_cadastro`  timestamp     NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_produto_categoria` (`categoria_id`),
  CONSTRAINT `fk_produto_categoria` FOREIGN KEY (`categoria_id`) REFERENCES `categoria` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ------------------------------------------------------------
-- Dados de exemplo
-- ------------------------------------------------------------
INSERT INTO `categoria` (`id`, `nome`) VALUES
  (1, 'Vitrolas'),
  (2, 'Discos'),
  (3, 'Instrumentos');

INSERT INTO `produto` (`nome`, `categoria_id`, `descricao`, `ano_fabricacao`, `estado`, `preco`, `estoque`) VALUES
  ('Vitrola Portátil de Maleta', 1, 'Toca-discos de manivela com caixa de madeira e fecho de latão. Funcionando, agulha nova.', 1938, 'Muito bom', 1850.00, 1),
  ('Gramofone com Corneta de Latão', 1, 'Gramofone de mesa com corneta original. Mecanismo revisado.', 1915, 'Bom', 4200.00, 1),
  ('Radiola com Toca-Discos', 1, 'Móvel de nogueira com rádio valvulado e toca-discos de 78 rpm.', 1952, 'Para restauro', 2300.00, 1),
  ('Toca-Discos de Mesa 33/45 rpm', 1, 'Modelo compacto dos anos 60, com tampa de acrílico.', 1964, 'Excelente', 980.00, 2),
  ('Disco de Shellac 78 rpm - Orquestra de Tango', 2, 'Gravação original de orquestra de tango. Capa de papel kraft.', 1931, 'Bom', 160.00, 3),
  ('LP Bossa Nova - Edição Original', 2, 'Primeira prensagem brasileira em vinil, capa em ótimo estado.', 1962, 'Muito bom', 520.00, 2),
  ('Coleção de 10 Compactos Jovem Guarda', 2, 'Dez compactos de 45 rpm com capas originais.', 1967, 'Bom', 340.00, 1),
  ('LP de Samba de Raiz', 2, 'Vinil com leve desgaste na capa, sem chiado.', 1974, 'Muito bom', 210.00, 4),
  ('Violão Clássico Artesanal', 3, 'Violão de jacarandá, braço original, cordas de nylon.', 1948, 'Bom', 2900.00, 1),
  ('Acordeão de 80 Baixos', 3, 'Acordeão com foles revisados e estojo original.', 1957, 'Muito bom', 3600.00, 1),
  ('Bandolim de Cedro', 3, 'Bandolim de 8 cordas com tampo de cedro e verniz original.', 1940, 'Regular', 1450.00, 1),
  ('Metrônomo de Pêndulo', 3, 'Metrônomo mecânico de madeira, funcionando.', 1925, 'Excelente', 390.00, 2);
