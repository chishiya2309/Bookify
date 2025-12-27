<jsp:include page="/customer/header_customer.jsp" />
<h2>Add Book to Order ID: ${orderId}</h2>

<form method="post">
<select name="bookId">
<c:forEach var="b" items="${books}">
<option value="${b.bookId}">
${b.title} - ${b.author.name}
</option>
</c:forEach>
</select>

<input type="number" name="quantity" min="1"/>

<button>Add</button>
<a href="edit?id=${orderId}">Cancel</a>
</form>
<jsp:include page="/customer/footer_customer.jsp" />