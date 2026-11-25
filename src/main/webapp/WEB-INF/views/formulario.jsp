<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="editando" value="${produto.id > 0}"/>
<jsp:include page="cabecalho.jspf">
	<jsp:param name="titulo" value="${editando ? 'Editar item' : 'Novo item'}"/>
</jsp:include>

<section class="titulo-pagina">
	<h1>${editando ? 'Editar item' : 'Novo item'}</h1>
	<p>${editando ? 'Altere os dados e salve.' : 'Preencha os dados da peça que entrará no acervo.'}</p>
</section>

<c:if test="${not empty erros}">
	<div class="alerta alerta-erro" role="alert">
		<strong>Corrija os campos abaixo:</strong>
		<ul>
			<c:forEach var="e" items="${erros}">
				<li>${fn:escapeXml(e)}</li>
			</c:forEach>
		</ul>
	</div>
</c:if>

<form class="formulario" method="post" action="${pageContext.request.contextPath}/produtos?acao=salvar">
	<input type="hidden" name="id" value="${produto.id}">

	<div class="campo campo-largo">
		<label for="nome">Nome do item *</label>
		<input type="text" id="nome" name="nome" maxlength="120" required
			value="${fn:escapeXml(produto.nome)}" placeholder="Ex.: Vitrola Portátil de Maleta">
	</div>

	<div class="campo">
		<label for="categoriaId">Categoria *</label>
		<select id="categoriaId" name="categoriaId" required>
			<option value="">Selecione…</option>
			<c:forEach var="cat" items="${categorias}">
				<option value="${cat.id}" ${produto.categoriaId == cat.id ? 'selected' : ''}>${fn:escapeXml(cat.nome)}</option>
			</c:forEach>
		</select>
	</div>

	<div class="campo">
		<label for="estado">Estado de conservação *</label>
		<select id="estado" name="estado" required>
			<option value="">Selecione…</option>
			<c:forEach var="e" items="${estados}">
				<option value="${e}" ${produto.estado == e ? 'selected' : ''}>${e}</option>
			</c:forEach>
		</select>
	</div>

	<div class="campo">
		<label for="anoFabricacao">Ano de fabricação</label>
		<input type="number" id="anoFabricacao" name="anoFabricacao" min="1700" max="2100"
			value="${produto.anoFabricacao}" placeholder="Ex.: 1938">
	</div>

	<div class="campo">
		<label for="preco">Preço (R$) *</label>
		<input type="text" id="preco" name="preco" inputmode="decimal" required
			value="${produto.preco}" placeholder="Ex.: 1250.00">
	</div>

	<div class="campo">
		<label for="estoque">Estoque *</label>
		<input type="number" id="estoque" name="estoque" min="0" required
			value="${(not editando and empty erros) ? 1 : produto.estoque}">
	</div>

	<div class="campo campo-largo">
		<label for="descricao">Descrição</label>
		<textarea id="descricao" name="descricao" rows="4"
			placeholder="Detalhes, funcionamento, procedência…">${fn:escapeXml(produto.descricao)}</textarea>
	</div>

	<div class="formulario-acoes">
		<button type="submit" class="botao botao-destaque">${editando ? 'Salvar alterações' : 'Cadastrar item'}</button>
		<a class="botao botao-claro" href="${pageContext.request.contextPath}/produtos">Cancelar</a>
	</div>
</form>

<%@ include file="rodape.jspf" %>
