<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="cabecalho.jspf">
	<jsp:param name="titulo" value="Acervo"/>
</jsp:include>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="pt_BR"/>

<section class="titulo-pagina">
	<h1>Acervo da loja</h1>
	<p>Gerencie as vitrolas, discos e instrumentos à venda.</p>
</section>

<c:if test="${not empty msgTexto}">
	<div class="alerta alerta-${msgTipo}" role="status">${fn:escapeXml(msgTexto)}</div>
</c:if>
<c:if test="${not empty erroBanco}">
	<div class="alerta alerta-erro" role="alert">${fn:escapeXml(erroBanco)}</div>
</c:if>

<section class="resumo">
	<div class="cartao-resumo">
		<span class="resumo-numero">${fn:length(produtos)}</span>
		<span class="resumo-rotulo">itens listados</span>
	</div>
	<div class="cartao-resumo">
		<span class="resumo-numero">${totalPecas}</span>
		<span class="resumo-rotulo">peças em estoque</span>
	</div>
	<div class="cartao-resumo">
		<span class="resumo-numero"><fmt:formatNumber value="${valorEstoque}" type="currency"/></span>
		<span class="resumo-rotulo">valor em estoque</span>
	</div>
</section>

<form class="filtros" method="get" action="${pageContext.request.contextPath}/produtos">
	<input type="search" name="busca" value="${fn:escapeXml(busca)}" placeholder="Buscar por nome ou descrição…" aria-label="Buscar">
	<select name="categoria" aria-label="Categoria">
		<option value="">Todas as categorias</option>
		<c:forEach var="cat" items="${categorias}">
			<option value="${cat.id}" ${categoriaSelecionada == cat.id ? 'selected' : ''}>${fn:escapeXml(cat.nome)}</option>
		</c:forEach>
	</select>
	<button type="submit" class="botao">Filtrar</button>
	<c:if test="${not empty busca or not empty categoriaSelecionada}">
		<a class="botao botao-claro" href="${pageContext.request.contextPath}/produtos">Limpar</a>
	</c:if>
</form>

<c:choose>
	<c:when test="${empty produtos}">
		<div class="vazio">
			<p>Nenhum item encontrado.</p>
			<a class="botao botao-destaque" href="${pageContext.request.contextPath}/produtos?acao=novo">Cadastrar o primeiro item</a>
		</div>
	</c:when>
	<c:otherwise>
		<div class="tabela-wrap">
			<table class="tabela">
				<thead>
					<tr>
						<th>Item</th>
						<th>Categoria</th>
						<th>Ano</th>
						<th>Estado</th>
						<th class="num">Preço</th>
						<th class="num">Estoque</th>
						<th class="acoes-col">Ações</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="p" items="${produtos}">
						<tr>
							<td data-rotulo="Item">
								<strong>${fn:escapeXml(p.nome)}</strong>
								<c:if test="${not empty p.descricao}">
									<span class="descricao">${fn:escapeXml(p.descricao)}</span>
								</c:if>
							</td>
							<td data-rotulo="Categoria"><span class="etiqueta">${fn:escapeXml(p.categoriaNome)}</span></td>
							<td data-rotulo="Ano"><c:out value="${p.anoFabricacao}" default="—"/></td>
							<td data-rotulo="Estado">${fn:escapeXml(p.estado)}</td>
							<td data-rotulo="Preço" class="num"><fmt:formatNumber value="${p.preco}" type="currency"/></td>
							<td data-rotulo="Estoque" class="num ${p.estoque == 0 ? 'sem-estoque' : ''}">${p.estoque}</td>
							<td class="acoes">
								<a class="botao botao-pequeno" href="${pageContext.request.contextPath}/produtos?acao=editar&amp;id=${p.id}">Editar</a>
								<form method="post" action="${pageContext.request.contextPath}/produtos?acao=excluir"
									data-nome="${fn:escapeXml(p.nome)}"
									onsubmit="return confirm('Excluir &quot;' + this.dataset.nome + '&quot;? Esta ação não pode ser desfeita.');">
									<input type="hidden" name="id" value="${p.id}">
									<button type="submit" class="botao botao-pequeno botao-perigo">Excluir</button>
								</form>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</c:otherwise>
</c:choose>

<%@ include file="rodape.jspf" %>
