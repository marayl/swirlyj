<%-- -*- html -*- --%>
<%--
   Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<nav class="navbar navbar-inverse navbar-fixed-top">
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
          <li ${state.orderPage ? 'class="active"' : ""}><a href="/page/order">Order</a></li>
          <li ${state.quotePage ? 'class="active"' : ""}><a href="/page/quote">Quote</a></li>
          <li ${state.contrPage ? 'class="active"' : ""}><a href="/page/contr">Contract</a></li>
          <c:if test="${state.userAdmin}">
            <li ${state.adminPage ? 'class="active dropdown"' : 'class="dropdown"'}>
              <a data-toggle="dropdown" role="button">Admin <span class="caret"></span></a>
              <ul class="dropdown-menu" role="menu">
                <li><a href="/page/market">Market</a></li>
                <li><a href="/page/trader">Trader</a></li>
              </ul>
            </li>
          </c:if>
        </c:if>
        <li ${state.aboutPage ? 'class="active"' : ""}><a href="/page/about">About</a></li>
        <li ${state.contactPage ? 'class="active"' : ""}><a href="/page/contact">Contact</a></li>
      </ul>
      <ul class="nav navbar-nav navbar-right">
        <c:choose>
          <c:when test="${state.userLoggedIn}">
            <li><a>Hello, ${fn:escapeXml(state.userEmail)}</a></li>
            <li>
              <a href="${state.signOutUrl}">Sign Out</a>
            </li>
          </c:when>
          <c:otherwise>
            <li><a>Welcome</a></li>
            <li>
              <a href="${state.signInUrl}">Sign In</a>
            </li>
          </c:otherwise>
        </c:choose>
      </ul>
    </div>
  </div>
</nav>
