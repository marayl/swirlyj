<%-- -*- html -*- --%>
<%--
   Copyright (C) 2013, 2014 Mark Aylett <mark.aylett@gmail.com>

   All rights reserved.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<nav class="navbar navbar-default navbar-fixed-top">
  <div class="container">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand text-muted logo">Swirly</a>
    </div>
    <div id="navbar" class="collapse navbar-collapse">
      <ul class="nav navbar-nav">
        <li ${state.homePage ? 'class="active"' : ""}><a href="/page/home">Home</a></li>
        <c:if test="${state.userLoggedIn}">
          <li ${state.traderPage ? 'class="active"' : ""}><a href="/page/trader">Trader</a></li>
          <li ${state.contrPage ? 'class="active"' : ""}><a href="/page/contr">Contract</a></li>
          <c:if test="${state.userAdmin}">
            <li ${state.userPage ? 'class="active"' : ""}><a href="/page/user">User</a></li>
          </c:if>
        </c:if>
        <li ${state.aboutPage ? 'class="active"' : ""}><a href="/page/about">About</a></li>
        <li ${state.contactPage ? 'class="active"' : ""}><a href="/page/contact">Contact</a></li>
      </ul>
      <ul class="nav navbar-nav navbar-right">
        <c:choose>
          <c:when test="${state.userLoggedIn}">
            <li><a href="#">Hello, ${fn:escapeXml(state.userName)}</a></li>
            <li>
              <a href="${state.logoutURL}">Sign Out</a>
            </li>
          </c:when>
          <c:otherwise>
            <li><a href="#">Welcome</a></li>
            <li>
              <a href="${state.loginURL}">Sign In</a>
            </li>
          </c:otherwise>
        </c:choose>
      </ul>
    </div>
  </div>
</nav>