<%-- The home URL "/" goes to the login page; LoginServlet forwards logged-in users to their dashboard. --%>
<% response.sendRedirect(request.getContextPath() + "/login"); %>
