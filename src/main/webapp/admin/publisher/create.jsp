<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create New Publisher</title>
    <link rel="stylesheet" href="../../css/styles.css" type="text/css"/>
</head>
<body>

<h2>Create New Publisher</h2>

<form action="${pageContext.request.contextPath}/admin/publishers?action=create" method="post">

    <div>
        <label>Full Name:</label><br>
        <input type="text" name="name" required placeholder="Enter publisher name"/>
    </div>
    <br>

    <div>
        <label>Contact Email:</label><br>
        <input type="email" name="contactEmail" required placeholder="example@mail.com"/>
    </div>
    <br>

    <div>
        <label>Address:</label><br>
        <input type="text" name="address" placeholder="Enter address"/>
    </div>
    <br>

    <div>
        <label>Website:</label><br>
        <input type="text" name="website" placeholder="www.example.com"/>
    </div>
    <br>

    <div>
        <input type="submit" value="Save" />
        &nbsp;
        <a href="${pageContext.request.contextPath}/admin/publishers">Cancel</a>
    </div>

</form>

</body>
</html>