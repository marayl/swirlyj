/*******************************************************************************
 * Copyright (C) 2013, 2014 Mark Aylett <mark.aylett@gmail.com>
 *
 * All rights reserved.
 *******************************************************************************/
package org.doobry.web;

import static org.doobry.web.WebUtil.splitPathInfo;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.doobry.domain.RecType;

@SuppressWarnings("serial")
public final class RecServlet extends HttpServlet {

    @Override
    public final void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        final Ctx ctx = Ctx.getInstance();
        final StringBuilder sb = new StringBuilder();

        final String pathInfo = req.getPathInfo();
        final String[] parts = splitPathInfo(pathInfo);
        if (parts.length == 0) {
            ctx.getRec(sb);
        } else {
            if (parts[0].equals("asset")) {
                if (parts.length == 1) {
                    ctx.getRec(sb, RecType.ASSET);
                } else if (parts.length == 2) {
                    ctx.getRec(sb, RecType.ASSET, parts[1]);
                } else {
                    // FIXME
                }
            } else if (parts[0].equals("contr")) {
                if (parts.length == 1) {
                    ctx.getRec(sb, RecType.CONTR);
                } else if (parts.length == 2) {
                    ctx.getRec(sb, RecType.CONTR, parts[1]);
                } else {
                    // FIXME
                }
            }
        }

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().append(sb);
    }
}
