-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: sistemaoficina
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `Id_Cliente` int NOT NULL AUTO_INCREMENT,
  `Nome` varchar(200) NOT NULL,
  `Endereco` varchar(200) NOT NULL,
  `Email` varchar(100) DEFAULT NULL,
  `Telefone` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`Id_Cliente`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `item_ordem_servico`
--

DROP TABLE IF EXISTS `item_ordem_servico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_ordem_servico` (
  `Id_Item` int NOT NULL AUTO_INCREMENT,
  `Nome` varchar(100) NOT NULL,
  `Quantidade` int NOT NULL,
  `Id_Ordem_Servico` int NOT NULL,
  PRIMARY KEY (`Id_Item`),
  KEY `FK_Item_Ordem_Servico` (`Id_Ordem_Servico`),
  CONSTRAINT `FK_Item_Ordem_Servico` FOREIGN KEY (`Id_Ordem_Servico`) REFERENCES `ordem_servico` (`Id_Ordem_Servico`) ON DELETE CASCADE,
  CONSTRAINT `CK_Item_Quantidade` CHECK ((`Quantidade` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ordem_servico`
--

DROP TABLE IF EXISTS `ordem_servico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ordem_servico` (
  `Id_Ordem_Servico` int NOT NULL AUTO_INCREMENT,
  `Data_Abertura` date NOT NULL,
  `Modelo_Veiculo` varchar(100) NOT NULL,
  `Placa` varchar(7) NOT NULL,
  `Quilometragem` int NOT NULL,
  `Id_Cliente` int NOT NULL,
  PRIMARY KEY (`Id_Ordem_Servico`),
  KEY `FK_Ordem_Servico_Cliente` (`Id_Cliente`),
  CONSTRAINT `FK_Ordem_Servico_Cliente` FOREIGN KEY (`Id_Cliente`) REFERENCES `cliente` (`Id_Cliente`) ON DELETE RESTRICT,
  CONSTRAINT `CK_Ordem_Servico_Quilometragem` CHECK ((`Quilometragem` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `peca`
--

DROP TABLE IF EXISTS `peca`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `peca` (
  `Id_Item` int NOT NULL,
  `Categoria` varchar(50) NOT NULL,
  PRIMARY KEY (`Id_Item`),
  CONSTRAINT `FK_Peca_Produto` FOREIGN KEY (`Id_Item`) REFERENCES `produto` (`Id_Item`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pessoa_fisica`
--

DROP TABLE IF EXISTS `pessoa_fisica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pessoa_fisica` (
  `Id_Cliente` int NOT NULL,
  `CPF` varchar(11) NOT NULL,
  PRIMARY KEY (`Id_Cliente`),
  UNIQUE KEY `CPF` (`CPF`),
  CONSTRAINT `FK_Pessoa_Fisica_Cliente` FOREIGN KEY (`Id_Cliente`) REFERENCES `cliente` (`Id_Cliente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pessoa_juridica`
--

DROP TABLE IF EXISTS `pessoa_juridica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pessoa_juridica` (
  `Id_Cliente` int NOT NULL,
  `CNPJ` varchar(14) NOT NULL,
  `Razao_Social` varchar(200) NOT NULL,
  `Inscricao_Estadual` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`Id_Cliente`),
  UNIQUE KEY `CNPJ` (`CNPJ`),
  CONSTRAINT `FK_Pessoa_Juridica_Cliente` FOREIGN KEY (`Id_Cliente`) REFERENCES `cliente` (`Id_Cliente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pneu`
--

DROP TABLE IF EXISTS `pneu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pneu` (
  `Id_Item` int NOT NULL,
  `Modelo` varchar(50) NOT NULL,
  `Largura` int NOT NULL,
  `Perfil` int NOT NULL,
  `Aro` int NOT NULL,
  PRIMARY KEY (`Id_Item`),
  CONSTRAINT `FK_Pneu_Produto` FOREIGN KEY (`Id_Item`) REFERENCES `produto` (`Id_Item`) ON DELETE CASCADE,
  CONSTRAINT `CK_Pneu_Aro` CHECK ((`Aro` > 0)),
  CONSTRAINT `CK_Pneu_Largura` CHECK ((`Largura` > 0)),
  CONSTRAINT `CK_Pneu_Perfil` CHECK ((`Perfil` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `produto`
--

DROP TABLE IF EXISTS `produto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `produto` (
  `Id_Item` int NOT NULL,
  `Marca` varchar(50) NOT NULL,
  `Valor_Venda_Unitario` decimal(10,2) NOT NULL,
  `Custo_Total` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`Id_Item`),
  CONSTRAINT `FK_Produto_Item` FOREIGN KEY (`Id_Item`) REFERENCES `item_ordem_servico` (`Id_Item`) ON DELETE CASCADE,
  CONSTRAINT `CK_Produto_Custo` CHECK (((`Custo_Total` is null) or (`Custo_Total` >= 0))),
  CONSTRAINT `CK_Produto_Valor_Venda` CHECK ((`Valor_Venda_Unitario` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recebimento`
--

DROP TABLE IF EXISTS `recebimento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recebimento` (
  `Id_Recebimento` int NOT NULL AUTO_INCREMENT,
  `Valor` decimal(10,2) NOT NULL,
  `Data_Recebimento` date DEFAULT NULL,
  `Forma_Pagamento` varchar(20) NOT NULL,
  `Id_Ordem_Servico` int NOT NULL,
  `Data_Prevista` date NOT NULL,
  PRIMARY KEY (`Id_Recebimento`),
  KEY `Id_Ordem_Servico` (`Id_Ordem_Servico`),
  CONSTRAINT `recebimento_ibfk_1` FOREIGN KEY (`Id_Ordem_Servico`) REFERENCES `ordem_servico` (`Id_Ordem_Servico`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `servico`
--

DROP TABLE IF EXISTS `servico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servico` (
  `Id_Item` int NOT NULL,
  `Valor_Cobrado` decimal(10,2) NOT NULL,
  PRIMARY KEY (`Id_Item`),
  CONSTRAINT `FK_Servico_Item` FOREIGN KEY (`Id_Item`) REFERENCES `item_ordem_servico` (`Id_Item`) ON DELETE CASCADE,
  CONSTRAINT `CK_Servico_Valor` CHECK ((`Valor_Cobrado` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-17 13:20:42
